package kr.sesac.wordcounter.parser;

import kr.sesac.wordcounter.utils.AnalysisCancellation;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class HtmlParser implements FileTextParser {

    private static final String CONTENT_SELECTOR = "#content";

    @Override
    public List<String> parse(Path file) throws IOException {
        List<String> texts = new ArrayList<>();

        Document document;
        try (java.io.InputStream input =
                     AnalysisCancellation.openStream(file)) {
            document = Jsoup.parse(input, StandardCharsets.UTF_8.name(), file.toUri().toString());
        }
        AnalysisCancellation.check();

        Elements contents = document.select(CONTENT_SELECTOR);

        if (contents.size() != 1) {
            throw new IOException("본문 요소를 정확히 1개 찾아야 합니다. 현재 개수: "+ contents.size());
        }

        Element content = contents.get(0);

        content.select("script, style, nav, header, footer")
                .remove();

        String text = content.text();
        AnalysisCancellation.check();

        if (!text.isBlank()) {
            texts.add(text);
        }

        return texts;
    }
}
