package kr.sesac.wordcounter.service;

import java.util.List;
import java.util.Map;

public class WordQueryService {

    public List<Map.Entry<String, Long>> getTopWords(
            Map<String, Long> wordCount,
            int n
    ) {
        return wordCount.entrySet().stream()
                .sorted(
                        Map.Entry.<String, Long>comparingByValue()
                                .reversed()
                                .thenComparing(Map.Entry.comparingByKey())
                )
                .limit(n)
                .toList();
    }

    public long findWordCount(Map<String, Long> wordCount, String word) {
        return wordCount.getOrDefault(word, 0L);
    }

    public List<Map.Entry<String, Long>> getSortedWords(Map<String, Long> wordCount) {
        return wordCount.entrySet().stream()
                .sorted(
                        Map.Entry.<String, Long>comparingByValue()
                                .reversed()
                                .thenComparing(Map.Entry.comparingByKey())
                )
                .toList();
    }
}
