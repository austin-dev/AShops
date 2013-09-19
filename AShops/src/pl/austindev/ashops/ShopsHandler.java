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

import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

import pl.austindev.ashops.commands.ABUYCommandExecutor.BuyOfferAddProcedureValues;
import pl.austindev.ashops.commands.ASELLCommandExecutor.SellOfferAddProcedureValues;
import pl.austindev.mc.MessageTranslator;
import pl.austindev.mc.TemporaryValuesContainer.TemporaryValue;

public abstract class ShopsHandler {
	private final AShops plugin;
	private final ShopType shopType;

	public ShopsHandler(AShops plugin, ShopType shopType) {
		this.plugin = plugin;
		this.shopType = shopType;
	}

	public AShops getPlugin() {
		return plugin;
	}

	public MessageTranslator getTranslator() {
		return plugin.getTranslator();
	}

	public ShopType getShopType() {
		return shopType;
	}

	public abstract void handleShopCreate(Player player, SignedChest shopChest,
			TemporaryValue temporaryValue);

	public abstract void handleBuyOfferAdd(Player player,
			SignedChest shopChest,
			BuyOfferAddProcedureValues addBuyOfferProcedureValues);

	public abstract void handleSellOfferAdd(Player player,
			SignedChest shopChest,
			SellOfferAddProcedureValues sellOfferAddProcedureValues);

	public abstract void handleAccess(Player player, SignedChest signedChest);

	public abstract void handleInventoryClose(Player player,
			TemporaryValue temporaryValue, Inventory inventory);

	public abstract void handleManagerAccess(Player player,
			SignedChest signedChest);

	public abstract void handleChatInput(Player player,
			TemporaryValue temporaryValue, String message);

	public abstract void handleShopRemove(Player player,
			SignedChest signedChest, Boolean boolean1);

	public abstract void handleCreateTag(SignedChest signedChest,
			Player player, String[] arguments);

	public abstract void handleInventoryClick(Player player,
			TemporaryValue temporaryValue, InventoryClickEvent event);

	public abstract boolean handleChestInventoryOpen(Player player,
			Chest chest, TemporaryValue temporaryValue);

	public abstract void handlePluginDisable();

	public abstract void handleShopStateToggle(Player player,
			SignedChest signedChest);

	public abstract void handlePlayerJoin(Player player);

	public abstract void handlePlayerTeleport(Player player,
			TemporaryValue temporaryValue);

}
