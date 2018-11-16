package triplebyte;
import java.util.*;
import java.util.stream.*;

/**
 *
 */
public class GameSkeleton {

	enum Status {

	}

	GameSkeleton() {
		// todo init
	}

	boolean hasEnded() {
		// todo termination check here?
		return false;
	}

	String displayStateString() {
		return null; // todo
	}

	// parses command and tries to execute, returning string representing result
	String parseAndExecute(String input) {
		if (!isValidCommand(input)) {
			return "INVALID COMMAND!"; // invalid command message
		}
		// todo parse command
		// todo then switch statement for routing
		return null;
	}

	static boolean isValidCommand(String rawInput) {
		// todo validate
		return true;
	}

	/**
	 * 1. Make state display work
	 * 2. Stubs for all player actions
	 * 3. Optimistic command parsing and routing
	 * 4. Implement simplest command
	 * 4.5. (optional) implement player switching
	 * 5. Implement termination check
	 * 6. Impl. remaining actions in order of difficulty
	 */

	public static void main(String... args) {
		Scanner in = new Scanner(System.in);
		GameSkeleton game = new GameSkeleton();

		while (!game.hasEnded()) {
			System.out.println();
			System.out.println(game.displayStateString()); // display state
			String input = in.nextLine(); // read in
			String result = game.parseAndExecute(input); // parse, route, execute command
			System.out.println(result); // feedback for player action
		}
		// todo show final result?
	}
}
