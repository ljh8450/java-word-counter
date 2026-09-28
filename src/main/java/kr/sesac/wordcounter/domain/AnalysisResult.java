package kr.sesac.wordcounter.domain;

import java.util.Map;

public class AnalysisResult {

    private final Map<String, Long> wordCount;
    private final AnalysisSummary summary;

    public AnalysisResult(Map<String, Long> wordCount, AnalysisSummary summary){
        this.wordCount = wordCount;
        this.summary = summary;
    }

    public Map<String, Long> getWordCount() {
        return wordCount;
    }

    public AnalysisSummary getSummary() {
        return summary;
    }

    public boolean canQuery() {
        return summary.getSuccessFiles() > 0;
    }

    public boolean canSave() {
        return summary.getSuccessFiles() > 0;
    }
}
