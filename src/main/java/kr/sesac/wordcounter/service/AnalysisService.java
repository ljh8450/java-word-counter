package kr.sesac.wordcounter.service;

import kr.sesac.wordcounter.domain.*;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class AnalysisService {
    private final SingleFileAnalyzer analyzer;

    public AnalysisService() { this(new SingleFileAnalyzer()); }

    // Package-level injection for deterministic concurrency tests.
    AnalysisService(SingleFileAnalyzer analyzer) {
        this.analyzer = Objects.requireNonNull(analyzer);
    }

    public AnalysisResult analyze(FileSelectionResult selection, AnalysisSettings settings) {
        Objects.requireNonNull(settings);
        List<Path> files = selection.getSupportedFiles().stream()
                .sorted(Comparator.comparing(Path::toString)).toList();
        AnalysisAccumulator accumulator = new AnalysisAccumulator();
        AtomicInteger attempted = new AtomicInteger();
        AnalysisStatus status = AnalysisStatus.COMPLETED;
        String failure = null;
        long start = System.nanoTime();
        try {
            if (settings.workerCount() == 1) {
                analyzeSequential(files, accumulator, attempted);
            } else {
                analyzeParallel(files, settings.workerCount(), accumulator, attempted);
            }
            checkInterrupted();
        } catch (InterruptedException | CancellationException e) {
            status = AnalysisStatus.CANCELLED;
            failure = describeFailure(e);
            Thread.currentThread().interrupt();
        } catch (ExecutionException | RuntimeException e) {
            status = AnalysisStatus.FAILED;
            failure = describeFailure(e);
        }
        Map<String, Long> counts = accumulator.getTotalCounts();
        long total = counts.values().stream().mapToLong(Long::longValue).sum();
        long elapsed = System.nanoTime() - start;
        accumulator.getFailures().sort(Comparator.comparing(f -> f.path().toString()));
        AnalysisSummary summary = new AnalysisSummary(selection.getInputPath(), attempted.get(),
                accumulator.getSuccessFiles(), accumulator.getFailureFiles(),
                selection.getSkippedFiles(), total, counts.size(), elapsed,
                status, files.size(), failure);
        return new AnalysisResult(counts, summary, accumulator.getFailures());
    }

    private void analyzeSequential(List<Path> files, AnalysisAccumulator accumulator,
                                   AtomicInteger attempted) throws InterruptedException {
        for (Path file : files) {
            checkInterrupted();
            attempted.incrementAndGet();
            accumulator.accept(analyzer.analyze(file));
        }
    }

    private void analyzeParallel(List<Path> files, int workers, AnalysisAccumulator accumulator,
                                 AtomicInteger attempted)
            throws InterruptedException, ExecutionException {
        checkInterrupted();
        AtomicInteger workerId = new AtomicInteger();
        ExecutorService executor = Executors.newFixedThreadPool(workers, task ->
                new Thread(task, "word-analysis-" + workerId.incrementAndGet()));
        ExecutorCompletionService<FileAnalysisResult> completion =
                new ExecutorCompletionService<>(executor);
        Map<Future<FileAnalysisResult>, Path> pending = new HashMap<>();
        Throwable originalFailure = null;
        boolean complete = false;
        try {
            for (Path file : files) {
                checkInterrupted();
                Future<FileAnalysisResult> future = completion.submit(() -> {
                    checkInterrupted();
                    attempted.incrementAndGet();
                    return analyzer.analyze(file);
                });
                pending.put(future, file);
            }
            for (int i = 0; i < files.size(); i++) {
                Future<FileAnalysisResult> finished = completion.take();
                Path file = pending.remove(finished);
                try {
                    accumulator.accept(finished.get());
                } catch (ExecutionException e) {
                    Throwable cause = e.getCause();
                    if (cause instanceof InterruptedException || cause instanceof CancellationException) {
                        throw new InterruptedException("File task cancelled: " + file);
                    }
                    if (cause instanceof Error error) throw error;
                    throw new ExecutionException("File task failed: " + file, cause);
                }
            }
            complete = true;
        } catch (InterruptedException | ExecutionException | RuntimeException | Error e) {
            originalFailure = e;
            throw e;
        } finally {
            try {
                stopExecutor(executor, pending, complete);
            } catch (InterruptedException | RuntimeException cleanupFailure) {
                if (originalFailure == null) throw cleanupFailure;
                originalFailure.addSuppressed(cleanupFailure);
                if (cleanupFailure instanceof InterruptedException) Thread.currentThread().interrupt();
            }
        }
    }

    private void stopExecutor(ExecutorService executor,
                              Map<Future<FileAnalysisResult>, Path> pending,
                              boolean complete) throws InterruptedException {
        if (complete) executor.shutdown();
        else cancelPending(executor, pending);
        boolean interrupted = false;
        boolean terminated = false;
        // Two bounded waits; subsequent interrupts never restart the deadline.
        for (int phase = 0; phase < 2 && !terminated; phase++) {
            long deadline = System.nanoTime() + TimeUnit.SECONDS.toNanos(5);
            while (!terminated) {
                long remaining = deadline - System.nanoTime();
                if (remaining <= 0) break;
                try {
                    terminated = executor.awaitTermination(remaining, TimeUnit.NANOSECONDS);
                } catch (InterruptedException e) {
                    interrupted = true;
                    cancelPending(executor, pending);
                }
            }
            if (!terminated) cancelPending(executor, pending);
        }
        if (interrupted) {
            InterruptedException error = new InterruptedException("Interrupted during worker shutdown");
            if (!terminated) error.addSuppressed(new IllegalStateException("Worker shutdown timed out"));
            throw error;
        }
        if (!terminated) throw new IllegalStateException("Worker shutdown timed out");
    }

    private void cancelPending(ExecutorService executor,
                               Map<Future<FileAnalysisResult>, Path> pending) {
        pending.keySet().forEach(future -> future.cancel(true));
        executor.shutdownNow();
    }

    private static void checkInterrupted() throws InterruptedException {
        if (Thread.currentThread().isInterrupted()) throw new InterruptedException("Analysis cancelled");
    }

    private static String describeFailure(Throwable error) {
        StringBuilder text = new StringBuilder(error.toString());
        if (error.getCause() != null) text.append(" / cause: ").append(error.getCause());
        for (Throwable suppressed : error.getSuppressed()) {
            text.append(" / cleanup: ").append(describeFailure(suppressed));
        }
        return text.toString();
    }
}
