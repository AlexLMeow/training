package util;

public class MathUtil {

	public static long nCk(int n, int k) {
		long res = 1; // nC(0) is 1
		for (int kk = 1; kk <= k; kk++) { // MUST BUILD UP FROM K=0, SO THAT EVERY STEP IS AN INTEGER
			double real = ((double) res) * (n - kk + 1) / kk;
			res = Math.round(real);
		}
		return res;
	}
}
