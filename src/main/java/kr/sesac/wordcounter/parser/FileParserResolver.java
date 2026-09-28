package kr.sesac.wordcounter.parser;

import java.nio.file.Path;

public class FileParserResolver {

    private final TxtParser txtParser = new TxtParser();
    private final CsvParser csvParser = new CsvParser();
    private final TsvParser tsvParser = new TsvParser();
    private final HtmlParser htmlParser = new HtmlParser();

    public FileTextParser resolve(Path file) {

        String fileName =
                file.getFileName()
                        .toString()
                        .toLowerCase();

        if (fileName.endsWith(".txt")) {
            return txtParser;
        }

        if (fileName.endsWith(".csv")) {
            return csvParser;
        }

        if (fileName.endsWith(".tsv")) {
            return tsvParser;
        }

        if (fileName.endsWith("html")
                || fileName.endsWith(".hml")) {
            return htmlParser;
        }

        throw new IllegalArgumentException("지원하지 않는 파일 형식입니다: " + file);
    }
}
