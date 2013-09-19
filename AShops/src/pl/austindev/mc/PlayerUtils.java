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

import java.util.regex.Pattern;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;

public class PlayerUtils {
	private PlayerUtils() {
	}

	public static boolean isPlayer(CommandSender sender) {
		return sender instanceof Player;
	}

	public static boolean isPlayer(HumanEntity entity) {
		return entity instanceof Player;
	}

	public static boolean isValidPlayerName(String name) {
		return Pattern.matches("[a-zA-Z0-9_]{2,16}", name);
	}
}
