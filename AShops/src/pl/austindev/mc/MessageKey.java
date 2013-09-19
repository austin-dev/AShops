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

import org.bukkit.ChatColor;

public interface MessageKey {
	String getKey();

	String getFormat();

	public enum Level {
		SUCCESS(ChatColor.GREEN),
		INFO(ChatColor.AQUA),
		FAILURE(ChatColor.RED),
		ERROR(ChatColor.DARK_RED),
		NONE();
		private final String colors;

		private Level(ChatColor... colors) {
			StringBuilder colorsString = new StringBuilder();
			for (ChatColor color : colors)
				colorsString.append(color);
			this.colors = colorsString.toString();
		}

		public String getColors() {
			return colors;
		}
	}
}
