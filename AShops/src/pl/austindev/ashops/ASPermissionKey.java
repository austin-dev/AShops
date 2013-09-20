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

import pl.austindev.mc.PermissionKey;

public enum ASPermissionKey implements PermissionKey {
	/**
	 * A set of permissions for an operator.
	 */
	OPERATOR("operator"),
	/**
	 * Allows to: create a new player shop for an other player.
	 */
	MANAGER("manager", OPERATOR),
	/**
	 * A set of permissions for a player.
	 */
	PLAYER("player", MANAGER),
	/**
	 * Allows to: create a server shop,
	 */
	SERVER_SHOP("servershop", OPERATOR),
	/**
	 * Allows to: create an own player shop,
	 */
	PLAYER_SHOP("playershop", PLAYER),
	/**
	 * Allows to: buy items from shops; buy items in own shops if a player has
	 * also permission to create one.
	 */
	BUY_ITEMS("buy", PLAYER),
	/**
	 * Allows to: sell items to shops; sell items in own shops if a player has
	 * also permission to create one.
	 */
	SELL_ITEMS("sell", PLAYER),
	/**
	 * Allows to: create a new player shop for free.
	 */
	FREE_SHOP("free", OPERATOR),
	/**
	 * Allows to: create any number of player shops.
	 */
	UNLIMITED_SHOPS("unlimited", OPERATOR),
	/**
	 * Allows to: sell or buy any item.
	 */
	TRADE_ANY_ITEM("anyitem", OPERATOR),
	/**
	 * Allows to: sell or buy items for any price.
	 */
	ALLOW_ANY_PRICE("anyprice", OPERATOR);
	private final String path;
	private final PermissionKey[] implicating;

	private ASPermissionKey(String path, PermissionKey... implicating) {
		this.path = "ashops." + path;
		this.implicating = implicating;
	}

	@Override
	public String getPath() {
		return path;
	}

	@Override
	public PermissionKey[] getImplicating() {
		return implicating;
	}
}
