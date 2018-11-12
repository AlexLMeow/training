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
         * @param t test case id (1-indexed)
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
        long startTime = 0;
        for (int t = 1; t <= T; t++) {
            if (t % 5 == 1) {
                System.out.println("Test case " + t + " start! " + LocalTime.now());
                startTime = System.nanoTime();
            }
            handler.handleCase(t, in, out);
            if (t % 5 == 0) {
                double elapsedTime = ((double) System.nanoTime() - startTime) / 10e9;
                System.out.println("Case time elapsed: " + elapsedTime + "s");
                System.out.println();
            }
        }
    }

    public static Path askForInputFile() {
        Scanner stdin = new Scanner(System.in);
        System.out.println("Enter input file path");
        return Paths.get(stdin.nextLine());
    }

    public static Path defaultOutFile(Path infile) {
        return Paths.get(infile.toString() + ".out");
    }
}
