package kr.sesac.wordcounter.domain;

import java.nio.file.Path;
import java.util.List;

public class FileSelectionResult {

    private final Path inputPath;
    private final List<Path> supportedFiles;
    private final int skippedFiles;

    public FileSelectionResult(Path inputPath, List<Path> supportedFiles, int skippedFiles){
        this.inputPath = inputPath;
        this.supportedFiles = supportedFiles;
        this.skippedFiles = skippedFiles;
    }

    public Path getInputPath() {
        return inputPath;
    }

    public List<Path> getSupportedFiles() {
        return supportedFiles;
    }

    public int getSkippedFiles() {
        return skippedFiles;
    }
}
