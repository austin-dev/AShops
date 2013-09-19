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

import java.math.BigDecimal;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import pl.austindev.ashops.ASMessageKey;
import pl.austindev.ashops.OffersUtils;
import pl.austindev.ashops.ShopDataException;
import pl.austindev.ashops.playershops.PlayerBuyOffer;
import pl.austindev.mc.InventoryUtils;

public class PlayerBuyOfferMenu extends PlayerShopMenu {
	private final PlayerBuyOffer offer;
	private volatile Inventory inventory;
	private final PlayerShopInventoryMenu shopMenu;

	private PlayerBuyOfferMenu(PlayerShopInventoryMenu shopMenu,
			PlayerBuyOffer offer) {
		super(shopMenu.getSession());
		this.shopMenu = shopMenu;
		this.offer = offer;
		this.inventory = Bukkit.createInventory(null, 9, getTitle());
		setupInventory();
	}

	public static PlayerBuyOfferMenu open(PlayerShopInventoryMenu shopMenu,
		PlayerBuyOffer offer, Player player) {
		PlayerBuyOfferMenu menu = new PlayerBuyOfferMenu(shopMenu, offer);
		menu.getSession().setMenu(menu);
		menu.inventory = player.openInventory(menu.inventory).getTopInventory();
		return menu;
	}

	@Override
	public Inventory getInventory() {
		return inventory;
	}

	@Override
	public void onClose(final Player player, Inventory invnetory) {
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
				int amount = event.getSlot() + 1;
				if (event.isShiftClick())
					amount *= event.getCurrentItem().getMaxStackSize();
				try {
					int boughtAmount = getSession().getHandler()
							.getShopsManager().buyItem(player, offer, amount);
					if (boughtAmount > 0) {
						shopMenu.updateIcon(offer.getSlot());
						PlayerShopInventoryMenu.open(getSession(), offer
								.getItem().getShop(), player);
					} else if (boughtAmount == 0) {
						PlayerShopInventoryMenu.open(getSession(), offer
								.getItem().getShop(), player);
						getSession().getTranslator().$(player,
								ASMessageKey.NO_SHOP_MONEY_OR_PLAYER_ITEMS);
					}
				} catch (ShopDataException e) {
					e.printStackTrace();
					PlayerShopInventoryMenu.open(getSession(), offer.getItem()
							.getShop(), player);
					getSession().getTranslator().$(player, ASMessageKey.ERROR);
				}
			}
		}
	}

	private void setupInventory() {
		String clickLine = ChatColor.WHITE
				+ getSession().getTranslator().$(ASMessageKey.BUY_ICON_CLICK);
		String shiftClickLine = ChatColor.WHITE
				+ getSession().getTranslator().$(ASMessageKey.BUY_ICON_SHIFT);
		String price = OffersUtils.getFormatedPrice(offer.getPrice());
		for (int i = 0; i < inventory.getSize(); i++) {
			ItemStack itemStack = new ItemStack(offer.getItem().getItemStack());
			InventoryUtils.appendLore(itemStack, clickLine,
					getClickSummaryLine(i + 1, price), shiftClickLine,
					getShiftClickSummaryLine(i + 1, price));
			itemStack.setAmount(i + 1);
			inventory.setItem(i, itemStack);
		}
	}

	private String getTitle() {
		return new StringBuilder(ChatColor.DARK_GREEN.toString())
				.append(getSession().getTranslator().$(
						ASMessageKey.BUY_MENU_TITLE))
				.append(ChatColor.DARK_GRAY.toString()).append(": ")
				.append(OffersUtils.getFormatedPrice(offer.getPrice()))
				.toString();
	}

	private String getClickSummaryLine(int i, String price) {
		return new StringBuilder(ChatColor.WHITE.toString()).append(i)
				.append(" x ").append(price).append(" = ")
				.append(ChatColor.GREEN)
				.append(offer.getPrice().multiply(new BigDecimal(i)))
				.toString();
	}

	private String getShiftClickSummaryLine(int i, String price) {
		return new StringBuilder(ChatColor.WHITE.toString())
				.append(offer.getItem().getItemStack().getMaxStackSize())
				.append(" x ")
				.append(i)
				.append(" x ")
				.append(price)
				.append(" = ")
				.append(ChatColor.GREEN)
				.append(offer.getPrice().multiply(
						new BigDecimal(offer.getItem().getItemStack()
								.getMaxStackSize()
								* i))).toString();
	}
}
