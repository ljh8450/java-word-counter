package kr.sesac.wordcounter.tokenizer;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WordTokenizer {

    private static final Pattern WORD_PATTERN =  Pattern.compile("[A-Za-z0-9가-힣ㄱ-ㅎㅏ-ㅣ]+");
    private static final Pattern NUMBER_PATTERN = Pattern.compile("[0-9]+");

    public List<String> tokenize(String line) {
        List<String> words = new ArrayList<>();

        Matcher matcher = WORD_PATTERN.matcher(line);

        while (matcher.find()){
            String word = normalize(matcher.group());

            if (isNumberOnly(word)) {
                continue;
            }

            words.add(word);
        }
        return words;
    }

    private String normalize(String word) {
        return word.toLowerCase(Locale.ROOT);
    }

    private boolean isNumberOnly(String word) {
        return NUMBER_PATTERN.matcher(word).matches();
    }
}
