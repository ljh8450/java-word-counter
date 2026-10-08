package kr.sesac.wordcounter.parser;

import kr.sesac.wordcounter.utils.AnalysisCancellation;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

public class TxtParser implements FileTextParser {

    @Override
    public List<String> parse(Path file) throws IOException {
        List<String> lines = new java.util.ArrayList<>();
        try (java.io.BufferedReader reader =
                     AnalysisCancellation.openReader(file)) {
            String line;
            while ((line = reader.readLine()) != null) {
                AnalysisCancellation.check();
                lines.add(line);
            }
        }
        return lines;
    }
}
