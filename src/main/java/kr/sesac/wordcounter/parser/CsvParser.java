package kr.sesac.wordcounter.parser;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class CsvParser implements FileTextParser {

    private static final List<String> TARGET_COLUMNS = List.of("text");

    @Override
    public List<String> parse(Path file) throws IOException {
        List<String> texts = new ArrayList<>();

        try (
                Reader reader = Files.newBufferedReader(file, StandardCharsets.UTF_8);

                CSVParser csvParser =
                        CSVFormat.DEFAULT.builder()
                                .setHeader()
                                .setSkipHeaderRecord(true)
                                .setTrim(true)
                                .get()
                                .parse(reader)
        ) {

            for (String column: TARGET_COLUMNS) {
                if (!csvParser.getHeaderMap().containsKey(column)) {
                    throw new IOException("필수 열이 없습니다: " + column);
                }
            }

            for (CSVRecord record: csvParser) {
                for (String column: TARGET_COLUMNS) {
                    String value = record.get(column);

                    if (!value.isBlank()) {
                        texts.add(value);
                    }
                }
            }
        }

        return texts;
    }
}
