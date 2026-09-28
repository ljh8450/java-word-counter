package kr.sesac.wordcounter.input;

import kr.sesac.wordcounter.domain.FileSelectionResult;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class InputPathService {

    public FileSelectionResult resolve(Path input) throws IOException {

        if (!Files.exists(input)) {
            throw new IllegalArgumentException("존재하지 않는 경로입니다: " + input);
        }

        if (!Files.isReadable(input)) {
            throw new IllegalArgumentException("읽을 수 없는 파일입니다: " + input);
        }

        if (Files.isRegularFile(input)) {
            return resolveFile(input);
        }

        if (Files.isDirectory(input)) {
            return resolveDirectory(input);
        }

        throw new IllegalArgumentException("파일 또는 폴더를 입력해주세요: "+ input);
    }

    private FileSelectionResult resolveFile(Path file) {

        if (!isSupported(file)) {
            throw new IllegalArgumentException("지원하지 않는 파일 형식입니다. " + "지원 확장자: .txt, .csv, .tsv, .html, .htm");
        }

        return new FileSelectionResult(file, List.of(file), 0);
    }

    private FileSelectionResult resolveDirectory(Path directory) throws IOException {

        List<Path> supportedFile = new ArrayList<>();
        int skippedFiles = 0;

        try (var paths = Files.list(directory)) {

            List<Path> files = paths
                    .filter(Files::isRegularFile)
                    .toList();

            for (Path file : files) {

                if (isSupported(file)) {
                    supportedFile.add(file);
                } else {
                    skippedFiles++;
                }
            }
        }

        if (supportedFile.isEmpty()) {
            throw new IllegalArgumentException("폴더에 지원하는 파일이 없습니다.");
        }

        return new FileSelectionResult(directory, supportedFile, skippedFiles);
    }

    private boolean isSupported(Path file) {
        String name = file.getFileName()
                .toString()
                .toLowerCase(Locale.ROOT);

        return name.endsWith(".txt")
                || name.endsWith(".csv")
                || name.endsWith(".tsv")
                || name.endsWith(".html")
                || name.endsWith(".htm");
    }
}
