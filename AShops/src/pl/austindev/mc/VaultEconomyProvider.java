/*
 * AShops Bukkit Plugin
 * Copyright 2013 Austin Reuter (_austinho)
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package pl.austindev.mc;

import net.milkbowl.vault.economy.Economy;

import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;

public class VaultEconomyProvider implements EconomyProvider {
	private final Economy economy;

	public VaultEconomyProvider(BukkitPlugin plugin) {
		RegisteredServiceProvider<Economy> rsp = plugin.getServer()
				.getServicesManager().getRegistration(Economy.class);
		if (rsp != null) {
			economy = rsp.getProvider();
			if (economy == null) {
				throw new PluginSetupException(
						"Could not find any economy plugin.");
			}
		} else {
			throw new PluginSetupException("Could not find any economy plugin.");
		}
	}

	@Override
	public boolean withdrawPlayer(String playerName, String worldName,
			double amount) {
		return economy.withdrawPlayer(playerName, worldName, amount)
				.transactionSuccess();
	}

	@Override
	public boolean depositPlayer(String playerName, String worldName,
			double amount) {
		return economy.depositPlayer(playerName, worldName, amount)
				.transactionSuccess();
	}

	@Override
	public boolean has(String playerName, String worldName, double amount) {
		return economy.has(playerName, worldName, amount);
	}

	@Override
	public boolean transfer(String fromPlayer, String fromWorld,
			String toPlayer, String toWorld, double amount) {
		if (economy.withdrawPlayer(fromPlayer, fromWorld, amount)
				.transactionSuccess())
			if (economy.depositPlayer(toPlayer, toWorld, amount)
					.transactionSuccess())
				return true;
			else
				economy.depositPlayer(fromPlayer, fromWorld, amount);
		return false;
	}

	@Override
	public boolean has(Player player, double amount) {
		return has(player.getName(), player.getWorld().getName(), amount);
	}

	@Override
	public boolean withdrawPlayer(Player player, double amount) {
		return withdrawPlayer(player.getName(), player.getWorld().getName(),
			amount);
	}

	@Override
	public boolean depositPlayer(Player player, double amount) {
		return depositPlayer(player.getName(), player.getWorld().getName(),
			amount);
	}

	@Override
	public boolean transfer(Player fromPlayer, Player toPlayer, double amount) {
		return transfer(fromPlayer.getName(), fromPlayer.getWorld().getName(),
			toPlayer.getName(), toPlayer.getWorld().getName(), amount);
	}

	@Override
	public double getBalance(String playerName, String worldName) {
		return economy.getBalance(playerName, worldName);
	}

	@Override
	public String getCurrency() {
		try {
			return economy.currencyNamePlural();
		} catch (Exception e) {
		}
		try {
			return economy.currencyNameSingular();
		} catch (Exception e) {
		}
		return null;
	}
}
