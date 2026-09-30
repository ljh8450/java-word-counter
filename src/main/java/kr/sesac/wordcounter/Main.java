package kr.sesac.wordcounter;

import kr.sesac.wordcounter.domain.AnalysisResult;
import kr.sesac.wordcounter.domain.AnalysisSummary;
import kr.sesac.wordcounter.domain.FileSelectionResult;
import kr.sesac.wordcounter.input.InputPathService;
import kr.sesac.wordcounter.parser.*;
import kr.sesac.wordcounter.service.WordQueryService;
import kr.sesac.wordcounter.utils.InputUtils;
import kr.sesac.wordcounter.io.ResultFileWriter;
import kr.sesac.wordcounter.utils.ResultPrinter;
import kr.sesac.wordcounter.tokenizer.WordTokenizer;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

/**
 * 첫 실행용 코드입니다. TXT 내용을 읽은 뒤 TODO를 채우며 기능을 추가하세요.
 * 구현 기준은 docs/requirements.md에 있습니다.
 */
public class Main {
    public static void main(String[] args) {

        WordTokenizer tokenizer = new WordTokenizer();
        FileParserResolver resolver = new FileParserResolver();
        InputPathService inputPathService = new InputPathService();

        WordQueryService queryService = new WordQueryService();
        ResultPrinter printer = new ResultPrinter();
        ResultFileWriter writer = new ResultFileWriter();

        Scanner scanner = new Scanner(System.in);
        InputUtils inputUtils = new InputUtils();

        AnalysisResult currentResult = null;

        while (true) {
            System.out.println("1. 새 분석");
            System.out.println("2. 상위 단어 조회");
            System.out.println("3. 특정 단어 검색");
            System.out.println("4. 결과 저장");
            System.out.println("5. 최근 분석 요약");
            System.out.println("0. 종료");
            System.out.print("메뉴 선택: ");

            String menu = scanner.nextLine().trim();

            switch (menu) {
                case "1" -> {

                    FileSelectionResult selection;

                    while (true) {

                        try {
                            Path input = inputUtils.readPath(scanner);
                            selection = inputPathService.resolve(input);
                            break;
                        } catch (IllegalArgumentException | IOException e) {
                            System.out.println(e.getMessage());
                        }
                    }

                    Map<String, Long> wordCount = new HashMap<>();

                    int attemptFiles = 0;
                    int successFiles = 0;
                    int failedFiles = 0;
                    int skippedFiles = selection.getSkippedFiles();

                    long parsedCharacterCount = 0;
                    long generatedTokenCount = 0;

                    long startTime = System.nanoTime();

                    for (Path file : selection.getSupportedFiles()) {

                        attemptFiles++;

                        Map<String, Long> fileCount = new HashMap<>();

                        try {
                            FileTextParser parser = resolver.resolve(file);

                            List<String> texts = parser.parse((file));

                            for (String text : texts) {
                                /**
                                List<String> words = tokenizer.tokenize(text);

                                for (String word : words) {
                                    fileCount.merge(word, 1L, Long::sum);
                                }
                                 */

                                parsedCharacterCount += text.length();

                                List<String> words = tokenizer.tokenize(text);
                                generatedTokenCount += words.size();

                                for (String word: words) {
                                    fileCount.merge(word, 1L, Long::sum);
                                }
                            }

                            for (Map.Entry<String, Long> entry : fileCount.entrySet()) {
                                wordCount.merge(entry.getKey(), entry.getValue(), Long::sum);
                            }
                            successFiles++;
                            System.out.println("파일 읽기 성공.");

                        } catch (IOException | UncheckedIOException e) {
                            failedFiles++;
                            System.out.println("파일 읽기에 실패: " + file + " / " + e.getMessage());
                        }
                    }

                    long totalWordCount = wordCount.values().stream().mapToLong(Long::longValue).sum();

                    int uniqueCount = wordCount.size();

                    long endTime = System.nanoTime();

                    long elapsedNanos = endTime - startTime;

                    System.out.println("출력 문자 수: " + parsedCharacterCount);
                    System.out.println("생성 토큰 수: " + generatedTokenCount);

                    AnalysisSummary summary = new AnalysisSummary(selection.getInputPath(), attemptFiles, successFiles, failedFiles, skippedFiles, totalWordCount, uniqueCount, elapsedNanos);
                    currentResult = new AnalysisResult(wordCount, summary);
                    printer.printSummary(currentResult.getSummary());
                }

                case "2" -> {
                    if (currentResult == null) {
                        System.out.println("분석을 먼저 완료해주세요.");
                    } else if (!currentResult.canQuery()) {
                        System.out.println("성공한 파일이 없어 조회할 수 없습니다.");
                    } else {
                        int n = inputUtils.readTopN(scanner);

                        List<Map.Entry<String, Long>> topWords = queryService.getTopWords(currentResult.getWordCount(), n);

                        printer.printTopWords(topWords);
                    }
                }

                case "3" -> {
                    if (currentResult == null) {
                        System.out.println("분석을 먼저 완료해주세요.");
                    } else if (!currentResult.canQuery()) {
                        System.out.println("성공한 파일이 없어 조회할 수 없습니다.");
                    } else {
                        while (true) {
                            String inputString = inputUtils.readSearchWord(scanner);

                            List<String> token = tokenizer.tokenize(inputString);

                            if (token.size() != 1) {
                                printer.printInvalidSearchWord();
                                continue;
                            }

                            String word = token.get(0);

                            long count = queryService.findWordCount(currentResult.getWordCount(), word);

                            printer.printWordCount(word, count);
                            break;
                        }
                    }
                }

                case "4" -> {
                    if (currentResult == null) {
                        System.out.println("분석을 먼저 완료해주세요.");
                    } else if (!currentResult.canSave()) {
                        System.out.println("성공한 파일이 없어 저장할 수 없습니다.");
                    } else {
                        Path output = Path.of("out/counts.tsv");

                        List<Map.Entry<String, Long>> allWords = queryService.getSortedWords(currentResult.getWordCount());

                        try {
                            writer.save(output, allWords);

                            printer.printSaveSuccess(output);
                        } catch (IOException e) {
                            printer.printSaveFailure(e.getMessage());
                        }
                    }
                }

                case "5" -> {
                    if (currentResult == null) {
                        System.out.println("분석을 먼저 완료해주세요.");
                    } else {
                        printer.printSummary(currentResult.getSummary());
                    }
                }

                case "0" -> {
                    System.out.println("종료합니다.");
                    return;
                }

                default -> {
                    System.out.println("0~5 중에서 선택해주세요.");
                }
            }
        }
    }
}
