package kr.sesac.wordcounter.parser;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

public interface FileTextParser {

    List<String> parse(Path file) throws IOException;
}
