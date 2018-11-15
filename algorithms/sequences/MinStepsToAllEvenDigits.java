package sequences;

import util.ProblemSetIO;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;

/**
 * Given a number N, find the minimum number of steps to change the N
 * into a number with no odd digits in base 10 representation.
 * allowed steps: INCREMENT, DECREMENT
 */
public class MinStepsToAllEvenDigits {

	/**
	 * We'll never mix ++ and -- because they cancel out; we will need to decide on a direction.
	 * <p>
	 * find the largest odd digit X = N[i] (at place with i-th power of 10):
	 * - UP increase N[i] to X+1, set N[j] = 0 (where j < i)      // 0 is the first even number encountered otw up
	 * - DOWN : decrease N[i] to X-1, set N[j] = 8 (where j < i)  // 8 is the first even number encountered otw down
	 * <p>
	 * Determine direction:
	 * case i == 0: least sig digit, just pick any direction
	 * case N[i] == 9: MUST DECREASE TO 8 (going up to 0 will create a 1 at a higher power)
	 * for j = i to 0:
	 * case N[j] > 4: GO UP
	 * case N[j] < 4: GO DOWN
	 * if still no direction decided, pick any direction
	 */

	public static final int MAX_DIGITS_IN_LONG = 19;

	static long minSteps(long N) {
		byte[] digits = splitDigits(N);
		int highestOddPower = rightmostOddIndex(digits);
		if (highestOddPower < 0) {
			return 0;
		} // already all even
		// find target number
		if (shouldGoUp(highestOddPower, digits)) {
			Arrays.fill(digits, 0, highestOddPower, (byte) 0);
			digits[highestOddPower]++;
		} else {
			Arrays.fill(digits, 0, highestOddPower, (byte) 8);
			digits[highestOddPower]--;
		}
		long target = digitsToNum(digits);
		return Math.abs(N - target);
	}

	static byte[] splitDigits(long N) {
		byte[] digits = new byte[MAX_DIGITS_IN_LONG];
		int i = 0;
		while (N > 0) {
			digits[i] = (byte) (N % 10);
			N /= 10;
			i++;
		}
		return Arrays.copyOf(digits, i);
	}

	static long digitsToNum(byte[] digits) {
		if (digits.length > MAX_DIGITS_IN_LONG) throw new IllegalArgumentException("Can't fit in long");
		long n = 0;
		for (int i = digits.length - 1; i >= 0; i--) {
			n *= 10;
			n += digits[i];
		}
		return n;
	}

	/**
	 * Will never overflow to next power place (never return UP if highest odd digit is 9)
	 *
	 * @param digits from least to most significant
	 * @return true if should go up, false if should go down (to reach closest all-even-digit number)
	 */
	static boolean shouldGoUp(int highestOddPower, byte[] digits) {
		if (highestOddPower == 0) {
			return true;
		} // doesn't matter which direction
		if (digits[highestOddPower] == 9) {
			return false;
		} // cannot go up; will create new 1 in higher power
		for (int i = highestOddPower - 1; i >= 0; i--) {
			// 4 is the split point (54 -> 60 = 6 steps) (54 -> 48 = 6 steps)
			if (digits[i] > 4) {
				return true;
			}
			if (digits[i] < 4) {
				return false;
			}
		}
		return true; // number is in form ..X444444..., same distance either direction.
	}

	static int rightmostOddIndex(byte[] bytes) {
		int i = bytes.length - 1; // highest power of 10
		while (i >= 0) {
			if (bytes[i] % 2 != 0) {
				return i;
			}
			i--;
		}
		return -1;
	}

	public static void main(String... args) throws IOException {
		Path infile = ProblemSetIO.askForInputFile();
		Path outfile = ProblemSetIO.defaultOutFile(infile);
		ProblemSetIO.googleCodeJamFileIO(infile, outfile, (t, in, out) -> {
			long steps = minSteps(in.nextLong());
			out.println("Case #" + t + ": " + steps);
		});
	}
}
