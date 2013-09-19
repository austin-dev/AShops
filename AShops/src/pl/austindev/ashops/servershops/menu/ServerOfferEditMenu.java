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
import pl.austindev.ashops.ShopDataException;
import pl.austindev.ashops.servershops.ServerShopOffer;
import pl.austindev.mc.InventoryUtils;

public class ServerOfferEditMenu extends ServerShopMenu {
	private final ServerShopOffer offer;
	private volatile Inventory inventory;
	private final ServerShopInventoryMenu shopMenu;

	private ServerOfferEditMenu(ServerShopInventoryMenu shopMenu,
			ServerShopOffer offer) {
		super(shopMenu.getSession());
		this.shopMenu = shopMenu;
		this.offer = offer;
		this.inventory = Bukkit.createInventory(null, 9, getTitle());
		setupInventory();
	}

	public static ServerOfferEditMenu open(ServerShopInventoryMenu shopMenu,
		ServerShopOffer offer, Player player) {
		ServerOfferEditMenu menu = new ServerOfferEditMenu(shopMenu, offer);
		menu.getSession().setMenu(menu);
		menu.inventory = player.openInventory(menu.inventory).getTopInventory();
		return menu;
	}

	@Override
	public Inventory getInventory() {
		return inventory;
	}

	@Override
	public void onClose(final Player player, Inventory inventory) {
		getSession().getPlugin().synch(new Runnable() {

			@Override
			public void run() {
				ServerShopInventoryMenu.open(getSession(), offer.getItem()
						.getShop(), player);
			}
		});
	}

	@Override
	public void onClick(Player player, InventoryClickEvent event) {
		if (event.getCurrentItem() != null
				&& !event.getCurrentItem().getType().equals(Material.AIR)) {
			if (event.getRawSlot() <= event.getSlot()) {
				switch (event.getSlot()) {
				case 0:
					handleOfferRemove(player, event);
					break;
				}
			}
		}
	}

	private void handleOfferRemove(Player player, InventoryClickEvent event) {
		try {
			int offerSlot = offer.getSlot();
			int result = getSession().getHandler().getShopsManager()
					.removeOffer(offer);
			if (result > 0) {
				shopMenu.updateIcon(offerSlot);
				ServerShopInventoryMenu.open(getSession(), offer.getItem()
						.getShop(), player);
			} else if (result == 0) {
				ServerShopInventoryMenu.open(getSession(), offer.getItem()
						.getShop(), player);
				getSession().getTranslator().$(player,
						ASMessageKey.UNCOLLECTED_ITEMS);
			}
		} catch (ShopDataException e) {
			e.printStackTrace();
			ServerShopInventoryMenu.open(getSession(), offer.getItem()
					.getShop(), player);
			getSession().getTranslator().$(player, ASMessageKey.ERROR);
		}
	}

	private void setupInventory() {
		setIcon(0, Material.LAVA_BUCKET, ASMessageKey.EDIT_REMOVE_ICON,
				ASMessageKey.EDIT_REMOVE_ICON_CLICK);
	}

	private String getTitle() {
		return getSession().getTranslator().$(
				ASMessageKey.OFFER_EDIT_MENU_TITLE)
				+ ": " + offer.getItem().getItemStack().getType().toString();
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
}
