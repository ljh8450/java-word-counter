package kr.sesac.wordcounter.tokenizer;

import kr.sesac.wordcounter.utils.AnalysisCancellation;

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

    public List<String> tokenizeByCharacter(String text) {
        List<String> words = new ArrayList<>();

        int start = -1;
        boolean hasNonDigit = false;

        for (int i = 0; i < text.length(); i++) {
            if ((i & 4095) == 0) {
                AnalysisCancellation.check();
            }
            char ch = text.charAt(i);

            if (isAllowed(ch)) {
                if (start == -1) {
                    start = i;
                }

                if (ch < '0' || ch >'9') {
                    hasNonDigit = true;
                }
            }
            else {
                if (start != -1 && hasNonDigit) {
                    String word = text.substring(start, i);
                    words.add(normalize(word));
                }

                start = -1;
                hasNonDigit = false;
            }
        }
        if (start != -1 && hasNonDigit) {
            words.add(normalize(text.substring(start)));
        }
        return words;
    }

    private boolean isAllowed(char ch) {
        return (ch >= 'A' && ch <= 'Z')
                || (ch >= 'a' && ch <= 'z')
                || (ch >= '0' && ch <= '9')
                || (ch >= '가' && ch <= '힣')
                || (ch >= 'ㄱ' && ch <= 'ㅎ')
                || (ch >= 'ㅏ' && ch <= 'ㅣ');
    }
}
