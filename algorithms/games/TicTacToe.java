package games;

import java.util.*;
import java.util.stream.Stream;

public class TicTacToe {

	private enum Cell {
		CIRCLE, CROSS, EMPTY;
		@Override
		public String toString() {
			switch(this) {
				case CIRCLE: return "O";
				case CROSS: return "X";
				default: return " ";
			}
		}
	}

	Cell[][] boardState;
	boolean isCirclesTurn;

	Cell winner; // if null, game ongoing. if draw will be EMPTY.

	TicTacToe() {
		isCirclesTurn = true;
		winner = null;
		boardState = new Cell[3][3];
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				boardState[i][j] = Cell.EMPTY;
			}
		}
	}

	String displayString() {
		StringBuilder sb = new StringBuilder();
		sb.append("  0 1 2\n");
		for (int i = 0; i < 3; i++) {
			sb.append(" +-+-+-+\n");
			sb.append(i);
			for (int j = 0; j < 3; j++) {
				sb.append('|').append(boardState[i][j]);
			}
			sb.append("|\n");
		}
		sb.append(" +-+-+-+\n");
		return sb.toString();
	}

	String currentPlayer() { return isCirclesTurn ? "(O) CIRCLE" : "(X) CROSS"; }
	String getWinner() {
		if (isOngoing()) throw new IllegalStateException();
		switch (winner) {
			case CROSS: return "(X) CROSS";
			case CIRCLE: return "(O) CIRCLE";
			default: return "NO WINNER (DRAW)";
		}
	}

	boolean isOngoing() {
		return winner == null;
	}

	boolean isLegalMove(int x, int y) {
		try {
			Cell target = boardState[x][y];
			if (target == Cell.EMPTY) {
				return true;
			} else {
				return false;
			}
		} catch (IndexOutOfBoundsException e) {
			return false;
		}
	}

	/**
	 * Makes move for current player, then switches current player.
	 * If move was invalid the current player does not switch.
	 */
	void makeMove(int x, int y) {
		if (!isOngoing()) { throw new IllegalStateException(); }
		if (!isLegalMove(x, y)) { return; }

		boardState[x][y] = isCirclesTurn ? Cell.CIRCLE : Cell.CROSS;
		isCirclesTurn = !isCirclesTurn;

		// check and handle if this move ends the game
		winner = getVictor();
	}

	// null if game not over; EMPTY if draw; else the winner
	Cell getVictor() {
		Cell winner;
		// check for row victory
		for (Cell[] row : boardState) {
			winner = threeInRow(row);
			if (winner != null && winner != Cell.EMPTY) return winner;
		}
		// check for column victory
		for (int i = 0; i < 3; i++) {
			winner = threeInRow(boardState[0][i], boardState[1][i], boardState[2][i]);
			if (winner != null && winner != Cell.EMPTY) return winner;
		}
		// check for diagonal victory
		winner = threeInRow(boardState[0][2], boardState[1][1], boardState[2][0]);
		if (winner != null && winner != Cell.EMPTY) return winner;
		winner = threeInRow(boardState[0][0], boardState[1][1], boardState[2][2]);
		if (winner != null && winner != Cell.EMPTY) return winner;

		// check for full board
		boolean boardFull = Arrays.stream(boardState).flatMap(Arrays::stream)
						.noneMatch(c -> c == Cell.EMPTY);
		if (boardFull) { return Cell.EMPTY; } // draw

		// all checks passed, still ongoing
		return null;
	}

	// returns the cell that won in this row, otherwise null
	static Cell threeInRow(Cell... cells) {
		if (Arrays.stream(cells).distinct().count() == 1) {
			return cells[0];
		} else {
			return null;
		}
	}


	static void handlePlayerCommand(TicTacToe game, Scanner in) {
		int x, y;
		// input validation below
		String[] command = in.nextLine().split(" ");
		if (command.length != 2) {
			System.out.println("Wrong instruction format!");
			return;
		}
		try {
			x = Integer.parseInt(command[0]);
			y = Integer.parseInt(command[1]);
		} catch (NumberFormatException nfe) {
			System.out.println("Coordinates have to be numbers!");
			return;
		}
		if (game.isLegalMove(x, y)) {
			game.makeMove(x, y);
		} else {
			System.out.println("You cannot place your stone on those coordinates");
		}
	}

	public static void main(String... args) {
		Scanner in = new Scanner(System.in);
		TicTacToe game = new TicTacToe();
		while(game.isOngoing()) {
			System.out.println(game.displayString());
			System.out.println("It is " + game.currentPlayer() + "'s turn to move.");
			System.out.println("Enter next move in format: \"x y\" without quotes.");
			handlePlayerCommand(game, in);
		}
		System.out.println("Winner is: " + game.getWinner());
	}
}
