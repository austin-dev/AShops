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
package pl.austindev.ashops;

import java.math.BigDecimal;

import org.bukkit.entity.Player;

import pl.austindev.mc.EconomyProvider;

public class ASEconomy {
	private final AShops plugin;
	private final EconomyProvider economyProvider;

	public ASEconomy(AShops plugin) {
		this.plugin = plugin;
		this.economyProvider = plugin.getEconomyProvider();
		plugin.synch(new Runnable() {

			@Override
			public void run() {
				String currency = economyProvider.getCurrency();
				if (currency != null && currency.length() > 0)
					OffersUtils.setCurrency(currency);
			}
		});
	}

	public boolean collectShopPrice(Player player, BigDecimal shopPrice) {
		String serverAccountName = plugin.getConfiguration()
				.getServerAccountName();
		if (serverAccountName != null) {
			String worldName = player.getWorld().getName();
			return economyProvider.transfer(player.getName(), worldName,
				serverAccountName, worldName, shopPrice.doubleValue());
		} else {
			return economyProvider.withdrawPlayer(player,
				shopPrice.doubleValue());
		}
	}

	public int getAffordableAmount(String ownerName, String worldName,
			BigDecimal price) {
		double balance = economyProvider.getBalance(ownerName, worldName);
		return (int) (balance / price.doubleValue());
	}

	public boolean pay(String fromAccount, String toAccount, String worldName,
			BigDecimal price) {
		String serverAccountName = plugin.getConfiguration()
				.getServerAccountName();
		if (serverAccountName != null) {
			int tax = plugin.getConfiguration().getTax(toAccount, worldName);
			double taxAmount = (price.doubleValue() * tax) / 100;
			if (economyProvider.transfer(fromAccount, worldName, toAccount,
				worldName, price.doubleValue() - taxAmount))
				return economyProvider.depositPlayer(serverAccountName,
					worldName, taxAmount);
			else
				return false;
		} else {
			return economyProvider.transfer(fromAccount, worldName, toAccount,
				worldName, price.doubleValue());
		}
	}

	public boolean pay(String playerName, String worldName, BigDecimal price) {
		String serverAccountName = plugin.getConfiguration()
				.getServerAccountName();
		if (serverAccountName != null) {
			int tax = plugin.getConfiguration().getTax(playerName, worldName);
			double taxAmount = (price.doubleValue() * tax) / 100;
			if (economyProvider.depositPlayer(playerName, worldName,
				price.doubleValue() - taxAmount))
				return economyProvider.depositPlayer(serverAccountName,
					worldName, taxAmount);
			else
				return false;
		} else {
			return economyProvider.depositPlayer(playerName, worldName,
				price.doubleValue());
		}
	}

	public boolean take(String playerName, String worldName, BigDecimal price) {
		return economyProvider.withdrawPlayer(playerName, worldName,
			price.doubleValue());
	}
}
