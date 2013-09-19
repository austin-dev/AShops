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
package pl.austindev.ashops.commands;

import pl.austindev.ashops.ASMessageKey;
import pl.austindev.ashops.ASPermissionKey;
import pl.austindev.mc.MessageKey;
import pl.austindev.mc.PermissionKey;
import pl.austindev.mc.PluginCommand;

public enum ASCommand implements PluginCommand {
	ASHOP(ASMessageKey.CMD_ASHOP, false, 0, 1, ASPermissionKey.PLAYER_SHOP),
	ASSHOP(ASMessageKey.CMD_ASSHOP, false, 0, 0, ASPermissionKey.SERVER_SHOP),
	ABUY(ASMessageKey.CMD_ABUY, false, 2, 3, ASPermissionKey.PLAYER_SHOP,
			ASPermissionKey.BUY_ITEMS),
	ASELL(ASMessageKey.CMD_ASELL, false, 1, 2, ASPermissionKey.PLAYER_SHOP,
			ASPermissionKey.SELL_ITEMS),
	AREMOVE(ASMessageKey.CMD_AREMOVE, false, 0, 1, ASPermissionKey.PLAYER_SHOP),
	ATOGGLE(ASMessageKey.CMD_ATOGGLE, false, 0, 0, ASPermissionKey.PLAYER_SHOP),
	ARELOAD(ASMessageKey.CMD_ARELOAD, true, 0, 0, ASPermissionKey.OPERATOR),
	ASHOPS(ASMessageKey.CMD_ASHOPS, true, 0, 0);
	;
	private final MessageKey description;
	private final boolean consoleCallable;
	private final int minArgumentsNumber;
	private final int maxArgumentsNumber;
	private final PermissionKey[] permissions;

	private ASCommand(MessageKey description, boolean consoleCallable,
			int minArgumentsNumber, int maxArgumentsNumber,
			PermissionKey... permissions) {
		this.description = description;
		this.permissions = permissions;
		this.consoleCallable = consoleCallable;
		this.minArgumentsNumber = minArgumentsNumber;
		this.maxArgumentsNumber = maxArgumentsNumber;
	}

	@Override
	public boolean isConsoleCallable() {
		return consoleCallable;
	}

	@Override
	public MessageKey getDescription() {
		return description;
	}

	@Override
	public PermissionKey[] getPermission() {
		return permissions;
	}

	@Override
	public int getMinArgumentsNumber() {
		return minArgumentsNumber;
	}

	@Override
	public int getMaxArgumentsNumber() {
		return maxArgumentsNumber;
	}
}
