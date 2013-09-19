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
import pl.austindev.ashops.servershops.ServerSellOffer;
import pl.austindev.mc.InventoryUtils;

public class ServerSellOfferMenu extends ServerShopMenu {
	private final ServerSellOffer offer;
	private final Inventory inventory;
	private final ServerShopInventoryMenu shopMenu;

	private ServerSellOfferMenu(ServerShopInventoryMenu shopMenu,
			ServerSellOffer offer) {
		super(shopMenu.getSession());
		this.shopMenu = shopMenu;
		this.offer = offer;
		this.inventory = Bukkit.createInventory(null, 9, getTitle());
		setupInventory();
	}

	public static ServerSellOfferMenu open(ServerShopInventoryMenu shopMenu,
		ServerSellOffer offer, Player player) {
		ServerSellOfferMenu menu = new ServerSellOfferMenu(shopMenu, offer);
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
				int amount = event.getSlot() + 1;
				if (event.isShiftClick())
					amount *= event.getCurrentItem().getMaxStackSize();
				try {
					int boughtAmount = getSession().getHandler()
							.getShopsManager().sellItem(player, offer, amount);
					if (boughtAmount > 0) {
						shopMenu.updateIcon(offer.getSlot());
						ServerShopInventoryMenu.open(getSession(), offer
								.getItem().getShop(), player);
					} else if (boughtAmount == 0) {
						ServerShopInventoryMenu.open(getSession(), offer
								.getItem().getShop(), player);
						getSession().getTranslator().$(player,
								ASMessageKey.NO_MONEY_OR_SHOP_ITEMS);
					}
				} catch (ShopDataException e) {
					e.printStackTrace();
					ServerShopInventoryMenu.open(getSession(), offer.getItem()
							.getShop(), player);
					getSession().getTranslator().$(player, ASMessageKey.ERROR);
				}
			}
		}
	}

	private void setupInventory() {
		String clickLine = ChatColor.WHITE
				+ getSession().getTranslator().$(ASMessageKey.SELL_ICON_CLICK);
		String shiftClickLine = ChatColor.WHITE
				+ getSession().getTranslator().$(ASMessageKey.SELL_ICON_SHIFT);
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

	private String getClickSummaryLine(int i, String price) {
		return new StringBuilder(ChatColor.WHITE.toString()).append(i)
				.append(" x ").append(price).append(" = ")
				.append(ChatColor.RED)
				.append(offer.getPrice().multiply(new BigDecimal(i)))
				.toString();
	}

	private String getShiftClickSummaryLine(int i, String price) {
		return new StringBuilder(ChatColor.WHITE.toString())
				.append(offer.getItem().getItemStack().getMaxStackSize())
				.append(" x ").append(i).append(" x ").append(price)
				.append(" = ").append(ChatColor.RED)
				.append(offer.getPrice().multiply(new BigDecimal(64 * i)))
				.toString();
	}

	private String getTitle() {
		return new StringBuilder(ChatColor.DARK_RED.toString())
				.append(getSession().getTranslator().$(
						ASMessageKey.SELL_MENU_TITLE))
				.append(ChatColor.DARK_GRAY.toString()).append(": ")
				.append(offer.getItem().getItemStack().getType().name())
				.toString();
	}
}
