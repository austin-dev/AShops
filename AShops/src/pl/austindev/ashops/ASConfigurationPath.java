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

import pl.austindev.mc.ConfigurationPath;

public enum ASConfigurationPath implements ConfigurationPath {
	/**
	 * A language code.
	 */
	LANGUAGE("language"),
	/**
	 * Shop limits for groups.
	 * 
	 * @param String
	 *            group
	 */
	SHOPS_LIMIT("shops_limit.%s"),
	/**
	 * A price of a new shop for groups.
	 * 
	 * @param String
	 *            group
	 */
	SHOP_PRICE("shop_price.%s"),
	/**
	 * A server account name.
	 */
	SERVER_ACCOUNT_NAME("server_account_name"),
	/**
	 * A list of type names, that are not allowed in trade.
	 */
	FORBIDDEN_ITEMS("forbidden_items"),
	/**
	 * Minimal prices for certain item IDs.
	 * 
	 * @param int itemID
	 */
	MINIMAL_PRICE("minimal_price.%s"),
	/**
	 * Values of the income tax for groups.
	 * 
	 * @param String
	 *            group
	 */
	TAX("income_tax.%s"),
	/**
	 * True, if should send transactions notifications to shop owners.
	 */
	NOTIIFICATIONS("notifications"),
	/**
	 * Number of days transactions will be kept in the database.
	 */
	TRANSACTIONS_DAYS_LIMIT("transactions_days_limit");
	private final String path;

	private ASConfigurationPath(String path) {
		this.path = path;
	}

	@Override
	public String getPath() {
		return path;
	}

}
