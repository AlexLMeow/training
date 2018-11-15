package sequences;

import util.ProblemSetIO;

import java.util.Arrays;

public class SwapChargeShoot {
	/**
	 * {@url https://codejam.withgoogle.com/2018/challenges/00000000000000cb/dashboard}
	 */

	/**
	 * minimum number of swaps to have total dmg <= shield, else -1 if impossible
	 * <p>
	 * O(N^2) method (simulate performing optimal swap sequence):
	 * - every swap we want to reduce the dmg
	 * - the only swap that reduces dmg is swapping [C,S] -> [S,C]. Dmg reduced by 2^(number of Cs left of this pair)
	 * - therefore at every step, best swap is swapping the rightmost [C,S] instance
	 * <p>
	 * O(N) method
	 */

	static long solve(boolean[] charging, long shield) {
		int N = charging.length;
		int minHacks = 0;
		long startDmg = dmg(charging);

		if (startDmg <= shield) {
			return minHacks;
		}
		if (nShoots(charging) > shield) {
			return -1;
		}

		// number of shots coming after each index
		int[] nShotsAfter = getNumShotsAfter(charging);
		// number of charges before and at this index
		int[] powerOf2 = getPowersOf2(charging);

		long dmg = startDmg;
		long swapsUsed = 0;
		for (int i = N - 1; i >= 0; i--) { // from last instruction to first
			if (charging[i] && nShotsAfter[i] > 0) { // can reduce dmg by swapping this charge to back
				// max reduction after swapping all shots after this charge to the front of it
				// each swap will reduce shot dmg by half of it's original
				long dmgReducePerShot = 1L << (powerOf2[i] - 1);
				long maxReduceFromSwapping = nShotsAfter[i] * dmgReducePerShot;
				long dmgReduceNeeded = dmg - shield;
				if (dmgReduceNeeded <= maxReduceFromSwapping) { // can save earth by swapping this charge to the back
					long swapsNeeded = dmgReduceNeeded / dmgReducePerShot;
					if (dmgReduceNeeded % dmgReducePerShot != 0) {
						swapsNeeded++; // one more swap to cover residual dmg
					}
					return swapsUsed + swapsNeeded;
				}
				// swap this charge backwards till there are no more shots behind it
				dmg -= maxReduceFromSwapping;
				swapsUsed += nShotsAfter[i];
			}
		}

		throw new IllegalStateException(); // should never reach here
//        return -1;
	}

	static int[] getPowersOf2(boolean[] isCharging) {
		int N = isCharging.length;
		int[] powersOf2 = new int[N];
		powersOf2[0] = isCharging[0] ? 1 : 0;
		for (int i = 1; i < N; i++) {
			powersOf2[i] = powersOf2[i - 1];
			if (isCharging[i]) {
				powersOf2[i]++;
			}
		}
		return powersOf2;
	}

	static int[] getNumShotsAfter(boolean[] chargeSeq) {
		int N = chargeSeq.length;
		int[] nShotsAfter = new int[N]; // number of shoots behind each index
		nShotsAfter[N - 1] = 0;
		for (int i = N - 2; i >= 0; i--) {
			nShotsAfter[i] = nShotsAfter[i + 1];
			if (!chargeSeq[i + 1]) {
				nShotsAfter[i]++;
			} // if next instruction was SHOOT, update cumulative count
		}
		return nShotsAfter;
	}

	static int nShoots(boolean[] chargeSeq) {
		int n = 0;
		for (boolean c : chargeSeq) {
			if (!c) n++;
		}
		return n;
	}

	static long dmg(boolean[] chargeSeq) {
		long totalDmg = 0;
		long shotDmg = 1;
		for (boolean charging : chargeSeq) {
			if (charging) {
				shotDmg *= 2;
			} else {
				totalDmg += shotDmg;
			}
		}
		return totalDmg;
	}

	public static void main(String... args) {
		ProblemSetIO.googleCodeJamSTDIO((t, in, out) -> {
			long shield = in.nextLong();
			char[] instructions = in.next().toCharArray();
			boolean[] charging = new boolean[instructions.length];
			for (int i = 0; i < instructions.length; i++) {
				charging[i] = instructions[i] == 'C';
			}
			long result = solve(charging, shield);
			out.println("Case #" + t + ": " + (result < 0 ? "IMPOSSIBLE" : result));
		});
	}
}
