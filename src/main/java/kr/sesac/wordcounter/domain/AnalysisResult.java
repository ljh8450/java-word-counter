package kr.sesac.wordcounter.domain;

import java.util.List;
import java.util.Map;

public class AnalysisResult {

    private final Map<String, Long> wordCount;
    private final AnalysisSummary summary;
    private final List<FileAnalysisFailure> failures;

    public AnalysisResult(Map<String, Long> wordCount, AnalysisSummary summary, List<FileAnalysisFailure> failures) {
        this.wordCount = Map.copyOf(wordCount);
        this.summary = summary;
        this.failures = List.copyOf(failures);
    }

    public Map<String, Long> getWordCount() {
        return wordCount;
    }

    public AnalysisSummary getSummary() {
        return summary;
    }

    public boolean canQuery() {
        return summary.getStatus() == AnalysisStatus.COMPLETED && summary.getSuccessFiles() > 0;
    }

    public boolean canSave() {
        return canQuery();
    }

    public List<FileAnalysisFailure> getFailures() {
        return failures;
    }
}
