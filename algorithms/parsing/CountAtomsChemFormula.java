package parsing;

import java.util.*;
import java.util.stream.*;

public class CountAtomsChemFormula {
	/**
	 * https://leetcode.com/problems/number-of-atoms/
	 */
	public String countOfAtoms(String formula) {

		// todo fill up map
		// theres a stack of
		Deque<Freqs> layeredCounts = new ArrayDeque<>();
		layeredCounts.push(new Freqs()); // counts for layer 0
		int i = 0;


		while (i < formula.length()) { // every round processes either an entire element+count or opens or closes a group
			char c = formula.charAt(i);

			// group handling
			if (c == '(') {
				layeredCounts.push(new Freqs()); // counts for next layer
				i++;

			} else if (c == ')') {
				Freqs finishedLayer = layeredCounts.pop();
				StringBuilder multiplierDigits = new StringBuilder();
				// extract group count digits
				while (++i < formula.length() && Character.isDigit(c = formula.charAt(i))) {
					multiplierDigits.append(c);
				}
				int multiplier = multiplierDigits.length() == 0 ? 1 : Integer.parseInt(multiplierDigits.toString());
				finishedLayer.multiplyCounts(multiplier);
				layeredCounts.peek().absorbCountsFrom(finishedLayer);

			} else if (Character.isUpperCase(c)) { // start of new element, pull out entire element name and local count, then update layercount
				// extract full name
				StringBuilder elemName = new StringBuilder();
				elemName.append(c);
				while (++i < formula.length() && !endsElemName(c = formula.charAt(i))) {
					elemName.append(c);
				}
				// i is now at char after elem name
				// extract local count digits
				StringBuilder localCountDigits = new StringBuilder();
				while (i < formula.length() && Character.isDigit(c = formula.charAt(i))) {
					localCountDigits.append(c);
					i++;
				}
				// i is now at the char after the last digit
				String elem = elemName.toString();
				int localCount = localCountDigits.length() == 0 ? 1 : Integer.parseInt(localCountDigits.toString());
				// update current layer count
				layeredCounts.peek().add(elem, localCount);
			} else {
				throw new IllegalStateException();
			}
		}


		return layeredCounts.pop().countsToString();
	}

	static boolean endsElemName(char c) {
		return c == '(' || c == ')' || Character.isUpperCase(c) || Character.isDigit(c);
	}

	static class Freqs {
		Map<String, Integer> counts = new LinkedHashMap<>();
		int countFor(String elem) {
			return counts.getOrDefault(elem, 0);
		}
		Freqs add(String elem) {
			counts.merge(elem, 1, Math::addExact);
			return this;
		}
		Freqs add(String elem, int count) {
			counts.merge(elem, count, Math::addExact);
			return this;
		}
		Freqs absorbCountsFrom(Freqs other) {
			other.counts.forEach((e, cnt) -> this.add(e,cnt));
			return this;
		}
		Freqs multiplyCounts(int factor) {
			counts.replaceAll((e, cnt) -> cnt * factor);
			return this;
		}
		String countsToString() {
			return counts.entrySet().stream()
							.map(entry -> entry.getKey() + (entry.getValue() > 1 ? entry.getValue() : ""))
							.sorted()
							.collect(Collectors.joining());
		}
	}

	public static void main(String... args) {
		System.out.println(new CountAtomsChemFormula().countOfAtoms("Mg(OH)2"));
	}

}
