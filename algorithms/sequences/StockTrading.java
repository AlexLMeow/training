package sequences;

public class StockTrading {

	static int maxProfitOneTrade(int[] prices) {
		int[] suffixMax = new int[prices.length];
		suffixMax[prices.length - 1] = prices[prices.length - 1];
		for (int i = prices.length-2; i >= 0; i--) {
			suffixMax[i] = Math.max(suffixMax[i+1], prices[i]);
		}
		int maxProfit = 0;
		for (int i = 0; i < prices.length; i++) {
			maxProfit = Math.max(maxProfit, suffixMax[i] - prices[i]);
		}
		return maxProfit;
	}

	static int maxProfitMultiTrades(int[] prices, int startCapital) {
		int wallet = startCapital;
		int holding = 0;
		for (int i = 0; i < prices.length - 1; i++) {
			if (holding == 0 && prices[i] < prices[i+1]) { // buy all if will increase
				holding += wallet / prices[i];
				wallet = wallet % prices[i];
			} else if (holding > 0 && prices[i] > prices[i+1]) { // sell all if will decrease
				wallet += holding * prices[i];
				holding = 0;
			}
		}
		if (holding > 0) { // sell if still holding at end
			wallet += holding * prices[prices.length-1];
		}
		return wallet - startCapital;
	}
}
