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

public class PlayerOfferCollectMenu extends PlayerShopMenu {
	private final PlayerShopOffer offer;
	private final Inventory inventory;
	private final PlayerShopInventoryMenu shopMenu;

	private PlayerOfferCollectMenu(PlayerShopInventoryMenu shopMenu,
			PlayerShopOffer offer) {
		super(shopMenu.getSession());
		this.shopMenu = shopMenu;
		this.offer = offer;
		this.inventory = Bukkit.createInventory(null, 9, getTitle());
		setupInventory();
	}

	public static PlayerOfferCollectMenu open(PlayerShopInventoryMenu shopMenu,
		PlayerShopOffer offer, Player player) {
		PlayerOfferCollectMenu menu = new PlayerOfferCollectMenu(shopMenu,
				offer);
		menu.getSession().setMenu(menu);
		player.openInventory(menu.inventory);
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
				PlayerShopInventoryMenu.open(getSession(), offer.getItem()
						.getShop(), player);
			}
		});
	}

	@Override
	public void onClick(Player player, InventoryClickEvent event) {
		if (offer.getSlot() > -1) {
			if (event.getCurrentItem() != null
					&& !event.getCurrentItem().getType().equals(Material.AIR)) {
				if (event.getRawSlot() <= event.getSlot()) {
					int amount = event.getSlot() + 1;
					if (event.isShiftClick())
						amount *= event.getCurrentItem().getMaxStackSize();
					try {
						int collectedAmount = getSession().getHandler()
								.getShopsManager()
								.collectItems(player, offer, amount);
						if (collectedAmount > 0) {
							shopMenu.updateIcon(offer.getSlot());
							PlayerShopInventoryMenu.open(getSession(), offer
									.getItem().getShop(), player);
						} else if (collectedAmount == 0) {
							PlayerShopInventoryMenu.open(getSession(), offer
									.getItem().getShop(), player);
							getSession().getTranslator().$(player,
									ASMessageKey.NO_ITEMS_OR_SPACE);
						}
					} catch (ShopDataException e) {
						e.printStackTrace();
						PlayerShopInventoryMenu.open(getSession(), offer
								.getItem().getShop(), player);
						getSession().getTranslator().$(player,
								ASMessageKey.ERROR);
					}
				}
			}
		} else {
			getSession().end();
			player.closeInventory();
			getSession().getTranslator().$(player, ASMessageKey.REMOVED_OFFER);
		}
	}

	private String getTitle() {
		return getSession().getTranslator().$(ASMessageKey.COLLECT_MENU_TITLE);
	}

	private void setupInventory() {
		String clickLine = ChatColor.WHITE
				+ getSession().getTranslator().$(
						ASMessageKey.COLLECT_ICON_CLICK) + ChatColor.WHITE;
		String shiftClickLine = ChatColor.WHITE
				+ getSession().getTranslator().$(
						ASMessageKey.COLLECT_ICON_SHIFT) + ChatColor.WHITE;
		for (int i = 0; i < inventory.getSize(); i++) {
			ItemStack itemStack = new ItemStack(offer.getItem().getItemStack());
			InventoryUtils.appendLore(
					itemStack,
					clickLine + " " + (i + 1),
					shiftClickLine + " "
							+ ((i + 1) * itemStack.getMaxStackSize()));
			itemStack.setAmount(i + 1);
			inventory.setItem(i, itemStack);
		}
	}
}
