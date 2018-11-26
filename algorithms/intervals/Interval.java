package intervals;

public class Interval implements Comparable<Interval> {

	int start;
	int end; // inclusive, if exclusive need to change algo

	public Interval(int start, int end) {
		this.start = start;
		this.end = end;
	}

	@Override
	public int compareTo(Interval other) {
		int signum = this.start - other.start;
		if (signum == 0) {
			signum = this.end - other.end;
		}
		return signum;
	}

	static boolean overlaps(Interval a, Interval b) {
		return a.overlaps(b);
	}
	public boolean overlaps(Interval other) {
		return this.start <= other.end && other.start <= this.end; // change if exclusive bounds
	}
	public boolean contains(int point) {
		return start <= point && point <= end;
	}
}
