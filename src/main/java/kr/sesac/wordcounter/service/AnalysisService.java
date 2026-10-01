package kr.sesac.wordcounter.service;

import kr.sesac.wordcounter.domain.AnalysisResult;
import kr.sesac.wordcounter.domain.AnalysisSummary;
import kr.sesac.wordcounter.domain.FileAnalysisResult;
import kr.sesac.wordcounter.domain.FileSelectionResult;

import java.nio.file.Path;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AnalysisService {

    private final SingleFileAnalyzer analyzer = new SingleFileAnalyzer();

    public AnalysisResult analyze(FileSelectionResult selection) {
        List<Path> files = selection.getSupportedFiles().stream().
                sorted(Comparator.comparing(Path::toString))
                .toList();

        Map<String, Long> totalCounts = new HashMap<>();

        int attemptedFiles = 0;
        int successFiles = 0;
        int failureFiles = 0;

        long startTime = System.nanoTime();

        // 파일을 순회하며 분석하고 결과 계산
        for (Path file : files) {
            attemptedFiles++;

            FileAnalysisResult fileResult = analyzer.analyze(file);

            if (fileResult.isSuccess()) {
                for (Map.Entry<String, Long> entry : fileResult.getCounts().entrySet()) {
                    totalCounts.merge(entry.getKey(), entry.getValue(), Long::sum);
                }
                successFiles++;
            } else {
                failureFiles++;
            }
        }

        // 전체 단어 수와 처리 시간 계산
        long totalWordCount = totalCounts.values().stream()
                .mapToLong(Long::longValue)
                .sum();

        int uniqueWordCount = totalCounts.size();

        long elepsedNanos = System.nanoTime() - startTime;

        // AnalysisSummary 생성 후 AnalysisResult 반환
        AnalysisSummary summary = new AnalysisSummary(
                selection.getInputPath(),
                attemptedFiles,
                successFiles,
                failureFiles,
                selection.getSkippedFiles(),
                totalWordCount,
                uniqueWordCount,
                elepsedNanos
        );

        return new AnalysisResult(totalCounts, summary);
    }
}
