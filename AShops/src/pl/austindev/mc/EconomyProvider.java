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

import org.bukkit.entity.Player;

public interface EconomyProvider {
	boolean has(String playerName, String worldName, double amount);

	boolean has(Player player, double amount);

	boolean withdrawPlayer(String playerName, String worldName, double amount);

	boolean withdrawPlayer(Player player, double amount);

	boolean depositPlayer(String playerName, String worldName, double amount);

	boolean depositPlayer(Player player, double amount);

	boolean transfer(String fromPlayer, String fromWorld, String toPlayer,
			String toWorld, double amount);

	boolean transfer(Player fromPlayer, Player toPlayer, double amount);

	double getBalance(String playerName, String worldName);
	
	String getCurrency();
}
