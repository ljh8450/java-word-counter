package kr.sesac.wordcounter;

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

        Scanner scanner = new Scanner(System.in);
        InputUtils inputUtils = new InputUtils();

        FileSelectionResult selection;

        while (true) {
            Path input = inputUtils.readPath(scanner);

            try {
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

        long startTime = System.nanoTime();

        for (Path file : selection.getSupportedFiles()) {

            attemptFiles++;

            Map<String, Long> fileCount = new HashMap<>();

            try {
                FileTextParser parser = resolver.resolve(file);

                List<String> texts = parser.parse((file));

                for (String text : texts) {

                    List<String> words = tokenizer.tokenize(text);

                    for (String word : words) {
                        fileCount.merge(word, 1L, Long::sum);
                    }
                }

                for (Map.Entry<String, Long> entry: fileCount.entrySet()) {
                    wordCount.merge(entry.getKey(), entry.getValue(), Long::sum);
                }
                successFiles++;
                System.out.println("파일 읽기 성공.");

            } catch (IOException e) {
                failedFiles++;
                System.out.println("파일 읽기에 실패: " + file + " / " + e.getMessage());
            }
        }
        long endTime = System.nanoTime();

        long elapsedNanos = endTime - startTime;

        long totalWordCount = wordCount.values()
                .stream()
                .mapToLong(Long::longValue)
                .sum();

        int uniqueCount = wordCount.size();

        AnalysisSummary summary = new AnalysisSummary(
                selection.getInputPath(),
                attemptFiles,
                successFiles,
                failedFiles,
                skippedFiles,
                totalWordCount,
                uniqueCount,
                elapsedNanos
        );

        WordQueryService queryService = new WordQueryService();
        ResultPrinter printer = new ResultPrinter();

        printer.printSummary(summary);

        int n = inputUtils.readTopN(scanner);

        List<Map.Entry<String, Long>> topWords = queryService.getTopWords(wordCount, n);

        printer.printTopWords(topWords);

        while (true) {
            String inputString = inputUtils.readSearchWord(scanner);

            List<String> token = tokenizer.tokenize(inputString);

            if (token.size() != 1) {
                printer.printInvalidSearchWord();
                continue;
            }

            String word = token.get(0);

            long count = queryService.findWordCount(wordCount, word);

            printer.printWordCount(word, count);
            break;
        }

        Path output = Path.of("out/counts.tsv");

        List<Map.Entry<String, Long>> allWords = queryService.getSortedWords(wordCount);

        ResultFileWriter fileWriter = new ResultFileWriter();

        try {
            fileWriter.save(output, allWords);

            printer.printSaveSuccess(output);
        } catch (IOException e) {
            printer.printSaveFailure(e.getMessage());
        }
    }
}
