package sequences;

public class SpiralWalk {

	enum Dir {
		UP(0,-1), LEFT(-1,0), RIGHT(1,0), DOWN(0,1);
		final int dx, dy;
		Dir(int dx, int dy) {
			this.dx = dx;
			this.dy = dy;
		}
		Dir next() {
			switch (this) {
				case UP: return RIGHT;
				case DOWN: return LEFT;
				case LEFT: return UP;
				case RIGHT: return DOWN;
				default:
					throw new IllegalStateException();
			}
		}
	}

	/**
	 * Can step out of grid during counter clockwise spiral (first step is to the right).
	 * @param R rows in grid
	 * @param C cols in grid
	 * @param r0 startrow
	 * @param c0 startcol
	 * @return order of coordinates in grid visited
	 */
	public int[][] spiralMatrixIII(int R, int C, int r0, int c0) {
		int[][] visitOrder = new int[R*C][];
		int r = r0;
		int c = c0;
		int maxStrt = 1;
		int strtSteps = 0; // steps taken so far in a straight line
		Dir direction = Dir.RIGHT;
		int i = 0;
		while (i < visitOrder.length) {
			// HERE I AM
			if (inRange(r, R) && inRange(c, C)) {
				visitOrder[i++] = new int[]{r, c};
			}
			// WHERE SHOULD I GO NEXT
			if (strtSteps == maxStrt) { // prepare next straight segment
				strtSteps = 0;
				direction = direction.next();
				if (direction == Dir.LEFT || direction == Dir.RIGHT) {
					maxStrt++; // horizontals are start of longer straight walk
				}
			}
			// GO THERE
			r += direction.dy;
			c += direction.dx;
			strtSteps++;
		}
		return visitOrder;
	}

	static boolean inRange(int q, int limit) { return 0 <= q && q < limit; }

}
