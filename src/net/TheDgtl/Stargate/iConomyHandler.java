package net.TheDgtl.Stargate;

import com.nijiko.coelho.iConomy.iConomy;
import com.nijiko.coelho.iConomy.system.Account;

public class iConomyHandler {
	public static boolean useiConomy = false;
	public static iConomy iconomy = null;
	
	public static int useCost = 0;
	public static int createCost = 0;
	public static int destroyCost = 0;
	public static String inFundMsg = "Insufficient Funds.";
	public static boolean toOwner = false;
	
	public static double getBalance(String player) {
		if (useiConomy && iconomy != null) {
			Account acc = iConomy.getBank().getAccount(player);
			if (acc == null) {
				Stargate.log.info("[Stargate::ich::getBalance] Error fetching iConomy account for " + player);
				return 0;
			}
			return acc.getBalance();
		}
		return 0;
	}
	
	public static boolean chargePlayer(String player, String target, double amount) {
		if (useiConomy && iconomy != null) {
			Account acc = iConomy.getBank().getAccount(player);
			if (acc == null) {
				Stargate.log.info("[Stargate::ich::chargePlayer] Error fetching iConomy account for " + player);
				return false;
			}
			double balance = acc.getBalance();
			
			if (balance < amount) return false;
			acc.setBalance(balance - amount);
			
			if (toOwner && target != null && !player.equals(target)) {
				Account tAcc = iConomy.getBank().getAccount(target);
				if (tAcc != null) {
					balance = tAcc.getBalance();
					tAcc.setBalance(balance + amount);
				}
			}
			return true;
		}
		return true;
	}
	
	public static boolean useiConomy() {
		return (useiConomy && iconomy != null);
	}
}
