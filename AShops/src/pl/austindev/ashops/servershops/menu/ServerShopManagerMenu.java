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
package pl.austindev.ashops.servershops.menu;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import pl.austindev.ashops.ASMessageKey;
import pl.austindev.ashops.SignedChest;
import pl.austindev.mc.InventoryUtils;

public class ServerShopManagerMenu extends ServerShopMenu {
	private final Inventory inventory;

	private ServerShopManagerMenu(ServerShopSession session) {
		super(session);
		this.inventory = Bukkit.createInventory(null, 9, getTitle());
		setupInventory();
	}

	public static ServerShopManagerMenu open(ServerShopSession session,
		Player player) {
		ServerShopManagerMenu menu = new ServerShopManagerMenu(session);
		session.setMenu(menu);
		player.openInventory(menu.inventory);
		return menu;
	}

	private String getTitle() {
		return getSession().getTranslator().$(ASMessageKey.MANAGER_MENU_TITLE);
	}

	@Override
	public Inventory getInventory() {
		return inventory;
	}

	@Override
	public void onClose(Player player, Inventory inventory) {
		getSession().end();
	}

	@Override
	public void onClick(Player player, InventoryClickEvent event) {
		if (event.getCurrentItem() != null
				&& !event.getCurrentItem().getType().equals(Material.AIR)) {
			if (event.getRawSlot() <= event.getSlot()) {
				switch (event.getSlot()) {
				case 0:
					if (event.isShiftClick()) {
						ServerSellOfferItemInput.open(getSession(), player);
					} else {
						ServerSellOfferItemSelectMenu
								.open(getSession(), player);
					}
					break;
				case 1:
					if (event.isShiftClick()) {
						ServerBuyOfferItemInput.open(getSession(), player);
					} else {
						ServerBuyOfferItemSelectMenu.open(getSession(), player);
					}
					break;
				case 2:
					handleShopRemove(player);
					break;
				case 3:
					handleToggleState(player);
					break;
				}
			}
		}
	}

	private void handleToggleState(Player player) {
		getSession().end();
		player.closeInventory();
		SignedChest signedChest = SignedChest.findShopChest(getSession()
				.getLocation().getBlock());
		if (signedChest != null)
			getSession().getHandler()
					.handleShopStateToggle(player, signedChest);
	}

	private void handleShopRemove(final Player player) {
		getSession().end();
		player.closeInventory();
		getSession().getHandler().handleShopRemove(
				player,
				SignedChest
						.findShopChest(getSession().getLocation().getBlock()),
				true);
	}

	private void setupInventory() {
		setIcon(0, Material.WATER_BUCKET, ASMessageKey.MANAGER_ICON_SELL,
				ASMessageKey.MANAGER_ICON_SELL_CLICK,
				ASMessageKey.MANAGER_ICON_SELL_SHIFT);
		setIcon(1, Material.BUCKET, ASMessageKey.MANAGER_ICON_BUY,
				ASMessageKey.MANAGER_ICON_BUY_CLICK,
				ASMessageKey.MANAGER_ICON_BUY_SHIFT);
		setIcon(2, Material.LAVA_BUCKET, ASMessageKey.MANAGER_ICON_REMOVE,
				ASMessageKey.MANAGER_ICON_REMOVE_CLICK,
				ASMessageKey.MANAGER_ICON_REMOVE_SHIFT);
		setIcon(3, Material.REDSTONE_TORCH_ON, ASMessageKey.MANAGER_ICON_STATE,
				ASMessageKey.MANAGER_ICON_STATE_CLICK);
	}

	private void setIcon(int slot, Material material, ASMessageKey name,
		ASMessageKey line) {
		ItemStack icon = new ItemStack(material);
		InventoryUtils.setItemName(icon,
				ChatColor.RESET.toString() + ChatColor.GOLD.toString()
						+ getSession().getTranslator().$(name));
		InventoryUtils.appendLore(icon, ChatColor.WHITE
				+ getSession().getTranslator().$(line));
		inventory.setItem(slot, icon);
	}

	private void setIcon(int slot, Material material, ASMessageKey name,
		ASMessageKey line1, ASMessageKey line2) {
		ItemStack icon = new ItemStack(material);
		InventoryUtils.setItemName(icon,
				ChatColor.RESET.toString() + ChatColor.GOLD.toString()
						+ getSession().getTranslator().$(name));
		InventoryUtils.appendLore(icon, ChatColor.WHITE
				+ getSession().getTranslator().$(line1), ChatColor.WHITE
				+ getSession().getTranslator().$(line2));
		inventory.setItem(slot, icon);
	}

}
