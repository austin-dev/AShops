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

import java.util.LinkedList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.block.Sign;

import pl.austindev.mc.BlockUtils;

public class ShopSignUtils {
	private final static String SHOP_SIGN_TITLE = "" + ChatColor.DARK_GRAY
			+ ChatColor.BOLD + "(AShops)";

	private ShopSignUtils() {
	}

	public static boolean isTitled(Sign sign) {
		return sign.getLine(1).startsWith(SHOP_SIGN_TITLE);
	}

	public static ShopType getShopType(Sign sign) {
		String line = sign.getLine(1);
		return line.length() >= 2 ? ShopType.getByCode(line.charAt(line
				.length() - 1)) : null;
	}

	public static String getDataLine(Sign sign) {
		return sign.getLine(2);
	}

	public static ShopState getShopState(Sign sign) {
		String line = sign.getLine(3);
		return line.length() >= 2 ? ShopState.getByCode(line.charAt(line
				.length() - 1)) : null;
	}

	public static String[] getCreateCommandArguments(String[] lines) {
		List<String> list = new LinkedList<String>();
		for (int i = 1; i < lines.length; i++) {
			if (lines[i] == null || lines[i].length() < 1)
				return list.toArray(new String[list.size()]);
			else
				list.add(lines[i]);
		}
		return list.toArray(new String[list.size()]);
	}

	public static void initializeSign(Sign sign, ShopType shopType,
			String dataLine, ShopState state) {
		String title = SHOP_SIGN_TITLE + encode(shopType.getCode());
		BlockUtils.setLines(sign, "", title, shopType.getDataLinePrefix()
				+ dataLine, state.getLine() + encode(state.getCode()));
	}

	private static String encode(char code) {
		return ChatColor.getByChar(code).toString();
	}

	public static ShopState setState(Sign sign, ShopState state) {
		BlockUtils.setLines(sign, null, null, null, state.getLine()
				+ encode(state.getCode()));
		return state;
	}

}
