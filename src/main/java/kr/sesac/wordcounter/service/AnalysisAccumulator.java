package kr.sesac.wordcounter.service;

import kr.sesac.wordcounter.domain.FileAnalysisFailure;
import kr.sesac.wordcounter.domain.FileAnalysisResult;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AnalysisAccumulator {

    private final Map<String, Long> totalCounts = new HashMap<>();
    private final List<FileAnalysisFailure> failures = new ArrayList<>();

    private int successFiles = 0;
    private int failureFiles = 0;

    public void accept(FileAnalysisResult result) {

        if (result.isSuccess()) {
            for (Map.Entry<String, Long> entry : result.getCounts().entrySet()) {
                totalCounts.merge(
                        entry.getKey(),
                        entry.getValue(),
                        Long::sum
                );
            }
            successFiles++;
        } else {
            failureFiles++;

            failures.add(new FileAnalysisFailure(
                    result.getPath(),
                    result.getFailure()
            ));
        }
    }

    public Map<String, Long> getTotalCounts() {
        return totalCounts;
    }

    public int getSuccessFiles() {
        return successFiles;
    }

    public List<FileAnalysisFailure> getFailures() {
        return failures;
    }

    public int getFailureFiles() {
        return failureFiles;
    }
}
