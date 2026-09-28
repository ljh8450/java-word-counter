package kr.sesac.wordcounter.io;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

public class ResultFileWriter {

    public void save(Path output, List<Map.Entry<String, Long>> words) throws IOException {

        Files.createDirectories(output.getParent());

        try (BufferedWriter writer = Files.newBufferedWriter(output, StandardCharsets.UTF_8)) {

            writer.write("word\tcount");
            writer.newLine();

            for (Map.Entry<String, Long> entry : words) {
                writer.write(entry.getKey() + "\t" + entry.getValue());
                writer.newLine();
            }
        }
    }
}
