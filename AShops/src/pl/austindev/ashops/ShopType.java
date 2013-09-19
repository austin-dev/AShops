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

import pl.austindev.ashops.commands.ASCommand;
import pl.austindev.mc.TemporaryValuesSource;

public enum ShopType implements TemporaryValuesSource {
	PLAYER_SHOP('0', "", ":as:", ASCommand.ASHOP),
	SERVER_SHOP('1', ChatColor.BOLD + "ServerShop", ":ass:", ASCommand.ASSHOP);
	private final char code;
	private final String dataLinePrefix;
	private final String signTag;
	private final ASCommand createCommand;

	private ShopType(char code, String dataLinePrefix, String signTag,
			ASCommand createCommand) {
		this.code = code;
		this.dataLinePrefix = dataLinePrefix;
		this.signTag = signTag;
		this.createCommand = createCommand;
	}

	public static ShopType getByCode(char code) {
		for (ShopType type : values())
			if (type.code == code)
				return type;
		return null;
	}

	public char getCode() {
		return code;
	}

	public String getDataLinePrefix() {
		return dataLinePrefix;
	}

	public String getSignTag() {
		return signTag;
	}

	public ASCommand getCreateCommand() {
		return createCommand;
	}
}
