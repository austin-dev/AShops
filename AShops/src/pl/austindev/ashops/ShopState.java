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

import org.bukkit.ChatColor;

public enum ShopState {
	OPEN('0', ""), CLOSED('1', ChatColor.DARK_RED + "||||||||||||||||");
	private final char code;
	private final String line;

	private ShopState(char code, String line) {
		this.code = code;
		this.line = line;
	}

	public static ShopState getByCode(char code) {
		for (ShopState state : values())
			if (state.code == code)
				return state;
		return null;
	}

	public char getCode() {
		return code;
	}

	public String getLine() {
		return line;
	}

}
