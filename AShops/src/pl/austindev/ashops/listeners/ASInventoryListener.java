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
package pl.austindev.ashops.listeners;

import org.bukkit.Bukkit;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Result;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.InventoryHolder;

import pl.austindev.ashops.AShops;
import pl.austindev.ashops.ShopType;
import pl.austindev.ashops.SignedChest;
import pl.austindev.mc.PlayerUtils;
import pl.austindev.mc.TemporaryValuesContainer.TemporaryValue;

public class ASInventoryListener extends ASListener {
	public ASInventoryListener(AShops plugin) {
		super(plugin);
	}

	public static void register(AShops plugin) {
		ASInventoryListener listener = new ASInventoryListener(plugin);
		Bukkit.getPluginManager().registerEvents(listener, plugin);
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = false)
	public void onInventoryOpen(final InventoryOpenEvent event) {
		if (PlayerUtils.isPlayer(event.getPlayer())) {
			Player player = (Player) event.getPlayer();
			InventoryHolder holder = event.getInventory().getHolder();
			if (holder instanceof Chest) {
				Chest chest = (Chest) holder;
				SignedChest signedChest = SignedChest.findShopChest(chest
						.getBlock());
				if (signedChest != null) {
					TemporaryValue temporaryValue = getTemporaryValues().get(
							player.getName());
					if (temporaryValue != null
							&& temporaryValue.getSource() instanceof ShopType)
						if (!getShopsHandler(
								(ShopType) temporaryValue.getSource())
								.handleChestInventoryOpen(player, chest,
										temporaryValue))
							event.setCancelled(true);
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onInventoryClose(InventoryCloseEvent event) {
		if (PlayerUtils.isPlayer(event.getPlayer())) {
			Player player = (Player) event.getPlayer();
			TemporaryValue temporaryValue = getTemporaryValues().get(
					player.getName());
			if (temporaryValue != null
					&& temporaryValue.getSource() instanceof ShopType) {
				ShopType shopType = (ShopType) temporaryValue.getSource();
				getShopsHandler(shopType).handleInventoryClose(player,
						temporaryValue, event.getInventory());
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onInventoryClickEvent(InventoryClickEvent event) {
		if (PlayerUtils.isPlayer(event.getWhoClicked())) {
			Player player = (Player) event.getWhoClicked();
			TemporaryValue temporaryValue = getTemporaryValues().get(
					player.getName());
			if (temporaryValue != null
					&& temporaryValue.getSource() instanceof ShopType) {
				getShopsHandler((ShopType) temporaryValue.getSource())
						.handleInventoryClick(player, temporaryValue, event);
				event.setCancelled(true);
				event.setResult(Result.DENY);
			}
		}
	}
}