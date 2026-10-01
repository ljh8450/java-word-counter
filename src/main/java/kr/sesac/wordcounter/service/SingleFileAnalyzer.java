package kr.sesac.wordcounter.service;

import kr.sesac.wordcounter.domain.FileAnalysisResult;
import kr.sesac.wordcounter.parser.FileParserResolver;
import kr.sesac.wordcounter.parser.FileTextParser;
import kr.sesac.wordcounter.tokenizer.WordTokenizer;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SingleFileAnalyzer {
    FileAnalysisResult analyze(Path file) {
        Map<String, Long> counts = new HashMap<>();
        FileParserResolver resolver = new FileParserResolver();
        WordTokenizer tokenizer = new WordTokenizer();

        try {
            FileTextParser parser = resolver.resolve(file);
            List<String> texts = parser.parse(file);

            for (String text : texts) {
                List<String> words = tokenizer.tokenizeByCharacter(text);
                for (String word : words) {
                    counts.merge(word, 1L, Long::sum);
                }
            }

            return FileAnalysisResult.success(file, counts);
        } catch (IOException | UncheckedIOException e) {
            return FileAnalysisResult.failure(file, e);
        }
    }
}
