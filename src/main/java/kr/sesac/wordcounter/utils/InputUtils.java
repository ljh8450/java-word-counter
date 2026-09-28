package kr.sesac.wordcounter.utils;

import java.nio.file.Path;
import java.util.Scanner;

public class InputUtils {

    public int readTopN(Scanner scanner) {
        while (true) {
            System.out.print("상위 몇 개의 단어를 조회할까요? (기본값: 10) : ");

            String input = scanner.nextLine().trim();

            if (input.isEmpty()) {
                return 10;
            }

            try {
                int n = Integer.parseInt(input);

                if (n < 1 ) {
                    System.out.println("1 이상의 정수를 입력해주세요.");
                    continue;
                }

                return  n;
            } catch (NumberFormatException e) {
                System.out.println("올바른 정수를 입력해주세요.");
            }
        }
    }

    public String readSearchWord(Scanner scanner) {
        System.out.print("조회할 단어: ");
        return scanner.nextLine();
    }

    public Path readPath(Scanner scanner) {
        System.out.print("분석할 파일 또는 경로: ");

        String input = scanner.nextLine().trim();

        return Path.of(input);
    }
}
