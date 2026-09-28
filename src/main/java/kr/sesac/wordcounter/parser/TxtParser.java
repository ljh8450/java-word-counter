package kr.sesac.wordcounter.parser;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class TxtParser implements FileTextParser {

    @Override
    public List<String> parse(Path file) throws IOException {
        return Files.readAllLines(file, StandardCharsets.UTF_8);
    }
}
