package kr.sesac.wordcounter.parser;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class TsvParser implements FileTextParser {

    private static final List<String> TARGET_COLUMNS = List.of("document");

    @Override
    public List<String> parse(Path file) throws IOException {
        List<String> texts = new ArrayList<>();

        try (BufferedReader reader = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {

            String headLine = reader.readLine();

            if (headLine == null) {
                throw new IOException("헤더가 없는 빈 TSV 파일입니다");
            }

            String[] headers = headLine.split("\t", -1);

            for (int i = 0; i < headers.length; i++) {
                headers[i] = headers[i].trim();
            }

            List<Integer> targetIndexes = new ArrayList<>();

            for (String targetColumn: TARGET_COLUMNS) {

                int foundIndex = -1;

                for (int i = 0; i < headers.length; i++) {
                    if (headers[i].equals(targetColumn)) {
                        foundIndex = i;
                        break;
                    }
                }

                targetIndexes.add(foundIndex);
            }

            String line;

            while ((line = reader.readLine()) != null) {

                String[] cells = line.split("\t", -1);

                // 레코드 셀 수가 헤더와 다르면 파일 분석 실패
                if (cells.length != headers.length) {
                    throw new IOException("헤더와 레코드 열 개수가 다릅니다.");
                }

                for (int targetIndex: targetIndexes) {
                    String value = cells[targetIndex];

                    if (!value.isBlank()) {
                        texts.add(value);
                    }
                }
            }

            for (String header: headers) {
                System.out.println(header);
            }
        }

        return texts;
    }
}
