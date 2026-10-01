package kr.sesac.wordcounter.domain;

import java.nio.file.Path;
import java.util.Map;

public class FileAnalysisResult {
    private final Path path;
    private final Map<String, Long> counts;
    private final Exception failure;

    private FileAnalysisResult(Path path, Map<String, Long> counts, Exception failure) {
        this.path = path;
        this.counts = Map.copyOf(counts);
        this.failure = failure;
    }

    public static FileAnalysisResult success(Path path, Map<String, Long> counts) {
        return new FileAnalysisResult(path, counts, null);
    }

    public static FileAnalysisResult failure(Path path, Exception cause) {
        return new FileAnalysisResult(path, Map.of(), cause);
    }

    public boolean isSuccess() {
        return failure == null;
    }

    public Path getPath() {
        return path;
    }

    public Map<String, Long> getCounts() {
        return counts;
    }

    public Exception getFailure() {
        return failure;
    }
}
