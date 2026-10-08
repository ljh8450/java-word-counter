package kr.sesac.wordcounter.utils;

import kr.sesac.wordcounter.domain.AnalysisSummary;
import kr.sesac.wordcounter.domain.FileAnalysisFailure;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;

public class ResultPrinter {

    public void printTopWords(
            List<Map.Entry<String, Long>> words
    ) {
        for (int i = 0; i < words.size(); i++) {
            Map.Entry<String, Long> entry = words.get(i);

            System.out.println(
                    (i + 1)
                            + ". "
                            + entry.getKey()
                            + " : "
                            + entry.getValue()
                            + "회"
            );
        }
    }

    public void printWordCount(String word, long count) {
        System.out.println(word + ": " + count + "회");
    }

    public void printInvalidSearchWord() {
        System.out.println("단어 하나를 입력해주세요.");
    }

    public void printSummary(AnalysisSummary summary) {
        System.out.println("실행 상태: " + summary.getStatus());
        System.out.println("대상 파일: " + summary.getTargetFiles());
        System.out.println("완료 파일: " + summary.getCompletedFiles());
        System.out.println("미완료 파일: " + summary.getUnfinishedFiles());
        if (summary.getExecutionFailure() != null) {
            System.out.println("실행 안내: " + summary.getExecutionFailure());
        }

        double elapseMillis = summary.getElapsedNanos() / 1_000_000.0;

        System.out.println("[분석 요약]");
        System.out.println("입력 경로: " + summary.getInputPath());
        System.out.println("분석 시도: " + summary.getAttemptedFiles());
        System.out.println("성공: " + summary.getSuccessFiles());
        System.out.println("실패: " + summary.getFailedFiles());
        System.out.println("건너뜀: " + summary.getSkippedFiles());
        System.out.println("전체 단어 수: " + summary.getTotalWordCount());
        System.out.println("단어 종류 수: " + summary.getUniqueWordCount());
        System.out.printf("처리 시간: %.3f ms%n", elapseMillis);
    }

    public void printSaveSuccess(Path output) {
        System.out.println("저장 완료: " + output);
    }

    public void printSaveFailure(String message){
        System.out.println("저장 실패: " + message);
    }

    public void printFailures(List<FileAnalysisFailure> failures) {
        for (FileAnalysisFailure failure : failures) {
            System.out.println("파일 읽기에 실패: " + failure.path() + " / " + failure.cause().getMessage());
        }
    }
}
