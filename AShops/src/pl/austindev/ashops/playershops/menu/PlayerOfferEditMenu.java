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
package pl.austindev.ashops.playershops.menu;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import pl.austindev.ashops.ASMessageKey;
import pl.austindev.ashops.ShopDataException;
import pl.austindev.ashops.playershops.PlayerShopOffer;
import pl.austindev.mc.InventoryUtils;

public class PlayerOfferEditMenu extends PlayerShopMenu {
	private final PlayerShopOffer offer;
	private volatile Inventory inventory;
	private final PlayerShopInventoryMenu shopMenu;

	private PlayerOfferEditMenu(PlayerShopInventoryMenu shopMenu,
			PlayerShopOffer offer) {
		super(shopMenu.getSession());
		this.shopMenu = shopMenu;
		this.offer = offer;
		this.inventory = Bukkit.createInventory(null, 9, getTitle());
		setupInventory();
	}

	public static PlayerOfferEditMenu open(PlayerShopInventoryMenu shopMenu,
		PlayerShopOffer offer, Player player) {
		PlayerOfferEditMenu menu = new PlayerOfferEditMenu(shopMenu, offer);
		menu.getSession().setMenu(menu);
		menu.inventory = player.openInventory(menu.inventory).getTopInventory();
		return menu;
	}

	@Override
	public void onClose(final Player player, Inventory inventory) {
		getSession().getPlugin().synch(new Runnable() {

			@Override
			public void run() {
				PlayerShopInventoryMenu.open(getSession(), offer.getItem()
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
					PlayerOfferLoadMenu.open(shopMenu, offer, player);
					break;
				case 1:
					PlayerOfferCollectMenu.open(shopMenu, offer, player);
					break;
				case 2:
					handleOfferRemove(player, event);
					break;
				}
			}
		}
	}

	@Override
	public Inventory getInventory() {
		return inventory;
	}

	private void setupInventory() {
		setIcon(0, Material.WATER_BUCKET, ASMessageKey.EDIT_LOAD_ICON,
				ASMessageKey.EDIT_LOAD_ICON_CLICK);
		setIcon(1, Material.BUCKET, ASMessageKey.EDIT_COLLECT_ICON,
				ASMessageKey.EDIT_COLLECT_ICON_CLICK);
		setIcon(2, Material.LAVA_BUCKET, ASMessageKey.EDIT_REMOVE_ICON,
				ASMessageKey.EDIT_REMOVE_ICON_CLICK);
	}

	private String getTitle() {
		return getSession().getTranslator().$(
				ASMessageKey.OFFER_EDIT_MENU_TITLE)
				+ ": " + offer.getItem().getItemStack().getType().toString();
	}

	private void handleOfferRemove(Player player, InventoryClickEvent event) {
		try {
			int offerSlot = offer.getSlot();
			int result = getSession().getHandler().getShopsManager()
					.removeOffer(offer);
			if (result > 0) {
				shopMenu.updateIcon(offerSlot);
				PlayerShopInventoryMenu.open(getSession(), offer.getItem()
						.getShop(), player);
			} else if (result == 0) {
				PlayerShopInventoryMenu.open(getSession(), offer.getItem()
						.getShop(), player);
				getSession().getTranslator().$(player,
						ASMessageKey.UNCOLLECTED_ITEMS);
			}
		} catch (ShopDataException e) {
			e.printStackTrace();
			PlayerShopInventoryMenu.open(getSession(), offer.getItem()
					.getShop(), player);
			getSession().getTranslator().$(player, ASMessageKey.ERROR);
		}
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
