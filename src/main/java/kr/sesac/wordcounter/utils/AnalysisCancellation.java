package kr.sesac.wordcounter.utils;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.CancellationException;

/** Cooperative cancellation at I/O and CPU loop boundaries. */
public final class AnalysisCancellation {
    private AnalysisCancellation() { }

    public static void check() {
        if (Thread.currentThread().isInterrupted()) {
            throw new CancellationException("분석이 중단됐습니다.");
        }
    }

    public static InputStream openStream(Path path) throws IOException {
        check();
        return new FilterInputStream(Files.newInputStream(path)) {
            @Override
            public int read() throws IOException {
                check();
                return super.read();
            }

            @Override
            public int read(byte[] bytes, int offset, int length) throws IOException {
                check();
                return in.read(bytes, offset, length);
            }
        };
    }

    public static BufferedReader openReader(Path path) throws IOException {
        return new BufferedReader(new InputStreamReader(openStream(path),
                StandardCharsets.UTF_8.newDecoder()));
    }
}
