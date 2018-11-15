package dynamic_prog;

// if you encode an A-Z string into numbers {A: 1, ... Z: 26},
// then given a number string, how many possible decodings are there?
public class CountNumberEncodings {
	public static final int ALPHABET_SIZE = 26; // needs to be in set [10,99]

	static int countDecodingsDP(String S) {
		int n = S.length();
		int[] prefixCounts = new int[n + 1];
		for (int i = 0; i < prefixCounts.length; i++) {
			if (i < 2) {
				prefixCounts[i] = 1;
			} else {
				if (S.charAt(i - 1) != '0') {
					prefixCounts[i] += prefixCounts[i - 1];
				}
				int twoDigit = Integer.parseInt(S.substring(i - 2, i));
				if (10 <= twoDigit && twoDigit <= ALPHABET_SIZE) {
					prefixCounts[i] += prefixCounts[i - 2];
				}
			}
		}
		return prefixCounts[n];
	}

	// end is exclusive bound
	static int countDecodingsRecursive(int end, String S) {
		if (end < 2) {
			return 1;
		} // 0 chars left: 1 way to decode empty string ; 1 char: have to use the single digit encoding
		int count = 0;
		if (S.charAt(end - 1) != '0') { // count decodings if we take next char as single digit encoded (and is valid)
			count += countDecodingsRecursive(end - 1, S);
		}
		int twoDigit = Integer.parseInt(S.substring(end - 2, end));
		if (twoDigit <= ALPHABET_SIZE && twoDigit >= 10) { // count decodings if we take next char as 2 digits encoded (and is valid)
			count += countDecodingsRecursive(end - 2, S);
		}
		return count;
	}
}
