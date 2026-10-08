package kr.sesac.wordcounter.domain;

public record AnalysisSettings(int workerCount) {
    public AnalysisSettings {
        if (workerCount < 1) {
            throw new IllegalArgumentException("workerCount는 1 이상이어야 합니다.");
        }
    }

    public static AnalysisSettings defaults() {
        return new AnalysisSettings(1);
    }
}
