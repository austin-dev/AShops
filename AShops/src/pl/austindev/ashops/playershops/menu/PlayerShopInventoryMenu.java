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

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import pl.austindev.ashops.ASMessageKey;
import pl.austindev.ashops.ASPermissionKey;
import pl.austindev.ashops.OfferType;
import pl.austindev.ashops.OffersUtils;
import pl.austindev.ashops.ShopDataException;
import pl.austindev.ashops.playershops.PlayerBuyOffer;
import pl.austindev.ashops.playershops.PlayerSellOffer;
import pl.austindev.ashops.playershops.PlayerShop;
import pl.austindev.ashops.playershops.PlayerShopOffer;
import pl.austindev.ashops.playershops.PlayerShopsManager;
import pl.austindev.mc.InventoryUtils;

public class PlayerShopInventoryMenu extends PlayerShopMenu {
	private volatile Inventory inventory;
	private final PlayerShop shop;

	private PlayerShopInventoryMenu(PlayerShopSession session, PlayerShop shop) {
		super(session);
		this.shop = shop;
		this.inventory = getInventory();
		inventory.setContents(getIcons());
	}

	public static PlayerShopInventoryMenu open(PlayerShopSession session,
		PlayerShop shop, Player player) {
		PlayerShopInventoryMenu menu = new PlayerShopInventoryMenu(session,
				shop);
		session.setMenu(menu);
		menu.inventory = player.openInventory(menu.inventory).getTopInventory();
		return menu;
	}

	public Inventory getInventory() {
		Block block = getSession().getLocation().getBlock();
		if (block != null && block.getState() instanceof Chest) {
			return ((Chest) block.getState()).getInventory();
		} else
			return null;
	}

	@Override
	public void onClick(Player player, InventoryClickEvent event) {
		if (event.getCurrentItem() != null
				&& !event.getCurrentItem().getType().equals(Material.AIR)) {
			if (event.getRawSlot() <= event.getSlot()) {
				int slot = event.getSlot();
				PlayerShopOffer offer = shop.getOffers().get(slot);
				if (offer != null) {
					if (event.isLeftClick()) {
						if (player.getName().equalsIgnoreCase(
								shop.getOwnerName())) {
							handleOfferEdit(player, event, offer);
						} else {
							handleTrade(player, event, offer);
						}
					} else if (event.isRightClick()) {
						if (getSession().getPermissionsProvider().has(player,
								ASPermissionKey.MANAGER)) {
							handleOfferEdit(player, event, offer);
						}
					}
				}
			}
		}
	}

	@Override
	public void onClose(Player player, Inventory inventory) {
		getSession().end();
		PlayerShopsManager.FutureShop futureShop = getSession().getHandler()
				.getShopsManager().release(getSession().getLocation());
		if (futureShop != null) {
			try {
				PlayerShop shop = futureShop.getShop();
				shop.getLock().lock();
				try {
					if (inventory.getViewers().size() < 2)
						inventory.clear();
				} finally {
					shop.getLock().unlock();
				}
			} catch (ShopDataException e) {
				e.printStackTrace();
			}
		} else {
			inventory.clear();
		}
	}

	public void updateIcon(int slot) {
		PlayerShopOffer offer = shop.getOffers().get(slot);
		if (offer != null)
			for (PlayerShopOffer o : offer.getItem().getOffers())
				inventory.setItem(o.getSlot(), getIcon(o));
	}

	private void handleTrade(Player player, InventoryClickEvent event,
		PlayerShopOffer offer) {
		if (offer.getType().equals(OfferType.SELL)) {
			if (getSession().getPermissionsProvider().has(player,
					ASPermissionKey.BUY_ITEMS)) {
				PlayerSellOfferMenu.open(this, (PlayerSellOffer) offer, player);
			} else {
				getSession().getTranslator().$(player,
						ASMessageKey.NO_PERMISSION);
			}
		} else {
			if (getSession().getPermissionsProvider().has(player,
					ASPermissionKey.SELL_ITEMS)) {
				PlayerBuyOfferMenu.open(this, (PlayerBuyOffer) offer, player);
			} else {
				getSession().getTranslator().$(player,
						ASMessageKey.NO_PERMISSION);
			}
		}
	}

	private void handleOfferEdit(Player player, InventoryClickEvent event,
		PlayerShopOffer offer) {
		PlayerOfferEditMenu.open(this, offer, player);
	}

	private ItemStack[] getIcons() {
		ItemStack[] icons = new ItemStack[InventoryType.CHEST.getDefaultSize()];
		for (PlayerShopOffer offer : shop.getOffers().values()) {
			icons[offer.getSlot()] = getIcon(offer);
		}
		return icons;
	}

	private ItemStack getIcon(PlayerShopOffer offer) {
		if (offer.getType().equals(OfferType.SELL)) {
			return getSellIcon(offer);
		} else {
			return getBuyIcon(offer);
		}
	}

	private ItemStack getBuyIcon(PlayerShopOffer offer) {
		ItemStack icon = new ItemStack(offer.getItem().getItemStack());
		icon.setAmount(1);
		String priceLine = ChatColor.GRAY
				+ getSession().getTranslator()
						.$(ASMessageKey.SHOP_INV_ICON_BUY) + ChatColor.GREEN
				+ " " + OffersUtils.getFormatedPrice(offer.getPrice());
		String amountLine = ChatColor.GRAY + "x" + offer.getItem().getAmount()
				+ "/" + offer.getItem().getMaxAmount();
		String ownerLine = ChatColor.GRAY + shop.getOwnerName();
		InventoryUtils.appendLore(icon, priceLine, amountLine, ownerLine);
		return icon;
	}

	private ItemStack getSellIcon(PlayerShopOffer offer) {
		ItemStack icon = new ItemStack(offer.getItem().getItemStack());
		icon.setAmount(1);
		String priceLine = ChatColor.GRAY
				+ getSession().getTranslator().$(
						ASMessageKey.SHOP_INV_ICON_SELL) + ChatColor.RED + " "
				+ OffersUtils.getFormatedPrice(offer.getPrice());
		String amountLine = ChatColor.GRAY + "x" + offer.getItem().getAmount();
		String ownerLine = ChatColor.GRAY + shop.getOwnerName();
		InventoryUtils.appendLore(icon, priceLine, amountLine, ownerLine);
		return icon;

	}

}
