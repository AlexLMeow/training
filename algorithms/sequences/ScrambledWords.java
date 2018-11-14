package sequences;

import util.ProblemSetIO;

import java.io.IOException;
import java.nio.file.Path;
import java.util.*;

public class ScrambledWords {
    /**
     * Google Code Jam Kickstart 2018 Round A problem C
     *
     * Given a long string of chars S, and a dictionary of words DICT,
     * find how many of the words in DICT exist in S in scrambled form,
     * where 2 words are equal scramble-wise if the first and last chars stay in the same position and the rest
     * of the chars can be in any order.
     *
     * All letters involved are lowercase. Words do not contain whitespace, only letters.
     * Terms:
     * - N = length of string S
     * - L = number of dict words
     * - M = max sum of lengths of all words in dict
     *
     * Optimal solution: O(N*sqrt(M))
     * See official editorial
     */
    public static final byte LETTER_OFFSET = 97;
    public static final byte ALPHABET_SIZE = 26;

    static int solve(byte[] S, String[] dict) {
        // group dict words by length and count scramble-duplicates
        Map<Integer, Map<Scrambled, Integer>> wordFreqsByLength = getWordFreqsByLen(dict);
        int count = 0;
        // for each unique dict word length
        for (Map.Entry<Integer, Map<Scrambled, Integer>> entry : wordFreqsByLength.entrySet()) {
            count += countExistences(S, entry.getKey(), entry.getValue());
        }
        return count;
    }

    /**
     * Convert words into scrambled encodings, count frequencies, and group by word length.
     */
    static Map<Integer, Map<Scrambled, Integer>> getWordFreqsByLen(String... dict) {
        Map<Integer, Map<Scrambled, Integer>> wordFreqsByLen = new HashMap<>();
        for (String word : dict) {
            Scrambled encoded = new Scrambled(word);
            wordFreqsByLen.computeIfAbsent(word.length(), k -> new HashMap<>()) // get word counts for this word length
                    .compute(encoded, (k, v) -> v == null ? 1 : v + 1); // increment appropriate word count
        }
        return wordFreqsByLen;
    }

    /**
     * Count how many given words of specific length exist in scrambled form in S.
     * @param wordFreqs SIDE EFFECT: MUTATES MAPPING, MAKE SURE ONLY CALLED ONCE FOR EACH WORDFREQS MAP
     */
    static int countExistences(byte[] S, int wordLength, Map<Scrambled, Integer> wordFreqs) {
        int windowTailSize = wordLength - 1;
        int count = 0;
        // init window to 1 step before start of S
        Scrambled window = new Scrambled(Arrays.copyOf(S, wordLength));
        Integer nMatched = wordFreqs.remove(window); // in the future, ignore those seen before
        if (nMatched != null) { count += nMatched; }
        for (int i = 1; i < S.length - windowTailSize; i++) {
            if (wordFreqs.size() == 0) { return count; } // no more words for this length, early terminate
            window.updateWindow(S[i-1], S[i], S[i + windowTailSize]);
            nMatched = wordFreqs.remove(window); // in the future, ignore those seen before
            if (nMatched != null) { count += nMatched; }
        }
        return count;
    }


    /**
     * @return array of letters in string (a -> 0, ... z -> 25)
     */
    static byte[] generateLetters(char s0, char s1, int len, long A, long B, long C, long D) {
        byte[] seq = new byte[len];
        if (len > 0) { seq[0] = asLetter(s0); }
        if (len > 1) { seq[1] = asLetter(s1); }
        Deque<Long> prevX = new LinkedList<>();
        prevX.add((long) s0);
        prevX.add((long) s1);
        for (int i = 2; i < len; i++) {
            long x = (B*prevX.removeFirst() + A*prevX.getLast() + C) % D;
            prevX.addLast(x);
            seq[i] = (byte) (x % 26);
        }
        return seq;
    }

    public static void main(String... args) throws IOException {
        Path inFile = ProblemSetIO.askForInputFile();
        Path outFile = ProblemSetIO.defaultOutFile(inFile);
        ProblemSetIO.googleCodeJamFileIO(inFile, outFile, (t, in, out) -> {
            int L = in.nextInt();
            in.nextLine();
            String[] dict = in.nextLine().split(" ");
            char c0 = in.next().charAt(0);
            char c1 = in.next().charAt(0);
            int solution = solve(
                    generateLetters(c0, c1, in.nextInt(), in.nextLong(), in.nextLong(), in.nextLong(), in.nextLong()),
                    dict);
            out.println("Case #" + t + ": " + solution);
        });
    }

    static class Scrambled {
        int[] letterFreqs = new int[ALPHABET_SIZE];
        byte firstLetter;
        byte lastLetter;
        Scrambled(String word) {
            for (char c : word.toCharArray()) {
                letterFreqs[asLetter(c)]++;
            }
            firstLetter = asLetter(word.charAt(0));
            lastLetter = asLetter(word.charAt(word.length() - 1));
        }
        Scrambled(byte[] letters) {
            for (byte b : letters) {
                letterFreqs[b]++;
            }
            firstLetter = letters[0];
            lastLetter = letters[letters.length - 1];
        }

        void updateWindow(byte oldFirst, byte newFirst, byte newLast) {
            letterFreqs[oldFirst]--;
            letterFreqs[newLast]++;
            lastLetter = newLast;
            firstLetter = newFirst;
        }

        @Override
        public int hashCode() {
            return (Arrays.hashCode(letterFreqs) * 31 + firstLetter) * 31 + lastLetter;
        }

        @Override
        public boolean equals(Object o) {
            if (!(o instanceof Scrambled)) { return false; }
            Scrambled other = (Scrambled) o;
            return Arrays.equals(this.letterFreqs, other.letterFreqs)
                    && this.firstLetter == other.firstLetter
                    && this.lastLetter == other.lastLetter;
        }
    }

    static byte asLetter(char c) { return (byte) (c - LETTER_OFFSET); }

}
