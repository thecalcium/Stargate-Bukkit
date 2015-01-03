package net.thedgtl.stargate;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;

import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;

/**
 * Stargate - A portal plugin for Bukkit Copyright (C) 2011, 2012 Steven
 * "Drakia" Scott <Contact@TheDgtl.net>
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
public final class EconomyHandler {

    public Economy economy = null;
    public int useCost = 0;
    public int createCost = 0;
    public int destroyCost = 0;
    public boolean toOwner = false;
    public boolean chargeFreeDestination = true;
    public boolean freeGatesGreen = false;
    public boolean enableEconomy;
    private Plugin plugin;
    private boolean enabled = false;
    private final Stargate stargate;

    public EconomyHandler(Stargate stargate) {
        this.stargate = stargate;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public double getBalance(String player) {
        if (!enabled) {
            return 0;
        }
        return economy.getBalance(player);
    }

    public boolean chargePlayer(String player, String target, double amount) {
        if (!enabled) {
            return true;
        }
        if (player.equals(target)) {
            return true;
        }
        return economy.withdrawPlayer(player, amount).transactionSuccess() &&
                (target == null || economy.bankDeposit(target, amount).transactionSuccess());
    }

    public String format(int amt) {
        if (enabled) {
            return economy.format(amt);
        } else {
            return "";
        }
    }

    public boolean setupEconomy(PluginManager pm) {
        enabled = false;
        economy = null;
        plugin = null;

        if (!enableEconomy) {
            return false;
        }

        boolean result = false;
        Plugin plugin = pm.getPlugin("Vault");
        if (plugin != null && plugin.isEnabled()) {
            RegisteredServiceProvider<Economy> economyProvider = stargate.getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);
            if (economyProvider != null) {
                economy = economyProvider.getProvider();
                this.plugin = plugin;
                result = true;
            }
        }

        this.enabled = result;
        if (result) {
            stargate.getLogger().info("Vault v" + getVersion() + " found");
        } else {
            stargate.getLogger().info("Vault not found, disabling economy");
        }
        
        return result;
    }

    public String getVersion() {
        if (!enabled) {
            return "";
        } else {
            return plugin.getDescription().getVersion();
        }
    }
}
