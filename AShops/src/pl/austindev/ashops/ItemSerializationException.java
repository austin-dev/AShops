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

import org.bukkit.Material;

public class ItemSerializationException extends RuntimeException {
	private static final long serialVersionUID = 1L;
	private final int itemId;
	private final Material itemType;
	private final ShopType shopType;

	public ItemSerializationException(Throwable cause, int itemId,
			Material itemType, ShopType shopType) {
		super("Could not serialize an item of type: " + itemType
				+ ", for the type of shop: " + shopType + ", record ID: "
				+ itemId + ".", cause);
		this.itemId = itemId;
		this.itemType = itemType;
		this.shopType = shopType;
	}

	public int getItemId() {
		return itemId;
	}

	public Material getItemType() {
		return itemType;
	}

	public ShopType getShopType() {
		return shopType;
	}

}