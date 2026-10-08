package kr.sesac.wordcounter.domain;

import java.nio.file.Path;

public record FileAnalysisFailure(Path path, Exception cause) {
}
