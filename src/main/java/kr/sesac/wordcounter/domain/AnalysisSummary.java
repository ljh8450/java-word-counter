package kr.sesac.wordcounter.domain;

import java.nio.file.Path;

public class AnalysisSummary {

    private final  Path inputPath;
    private final int attemptedFiles;
    private final int successFiles;
    private final int failedFiles;
    private final int skippedFiles;
    private final long totalWordCount;
    private final int uniqueWordCount;
    private final long elapsedNanos;
    public AnalysisSummary(
            Path inputPath,
            int attemptedFiles,
            int successFiles,
            int failedFiles,
            int skippedFiles,
            long totalWordCount,
            int uniqueWordCount,
            long elapsedNanos
    ) {
        this.inputPath = inputPath;
        this.attemptedFiles = attemptedFiles;
        this.successFiles = successFiles;
        this.failedFiles = failedFiles;
        this.skippedFiles = skippedFiles;
        this.totalWordCount = totalWordCount;
        this.uniqueWordCount = uniqueWordCount;
        this.elapsedNanos = elapsedNanos;
    }

    public Path getInputPath() {
        return inputPath;
    }

    public int getAttemptedFiles() {
        return attemptedFiles;
    }

    public int getSuccessFiles() {
        return successFiles;
    }

    public int getFailedFiles() {
        return failedFiles;
    }

    public int getSkippedFiles() {
        return skippedFiles;
    }

    public long getTotalWordCount() {
        return totalWordCount;
    }

    public int getUniqueWordCount() {
        return uniqueWordCount;
    }

    public long getElapsedNanos() {
        return elapsedNanos;
    }
}
