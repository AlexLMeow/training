package util;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalTime;
import java.util.Scanner;

public class ProblemSetIO {

    @FunctionalInterface
    public interface TestCaseHandler {
        /**
         *
         * @param t test case id (1-indexed)
         * @param in
         * @param out
         */
        void handleCase(int t, Scanner in, PrintStream out);
    }

    /**
     * Handles test case iteration and logging
     */
    public static void googleCodeJam(Path inFile, Path outFile, TestCaseHandler handler) throws IOException {
        Scanner in = new Scanner(inFile);
        PrintStream out = new PrintStream(new FileOutputStream(outFile.toFile()));
        int T = in.nextInt();
        for (int t = 1; t <= T; t++) {
            System.out.println("Test case " + t + " start! " + LocalTime.now());
            long startTime = System.nanoTime();
            handler.handleCase(t, in, out);
            double elapsedTime = ((double) System.nanoTime() - startTime) / 10e9;
            System.out.println("Case time elapsed: " + elapsedTime + "s");
            System.out.println();
        }
    }

    public static Path askForInputFile() {
        Scanner stdin = new Scanner(System.in);
        System.out.println("Enter input file path");
        return Paths.get(stdin.nextLine());
    }
}
