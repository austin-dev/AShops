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
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import pl.austindev.ashops.ASMessageKey;
import pl.austindev.ashops.ASPermissionKey;
import pl.austindev.ashops.OffersUtils;

public class ServerBuyOfferItemSelectMenu extends ServerShopMenu {
	private volatile Inventory inventory;
	private final int INVENTORY_SIZE = 9;

	private ServerBuyOfferItemSelectMenu(ServerShopSession session) {
		super(session);
		this.inventory = Bukkit.createInventory(null, INVENTORY_SIZE,
				getTitle());
	}

	public static ServerBuyOfferItemSelectMenu open(ServerShopSession session,
		Player player) {
		ServerBuyOfferItemSelectMenu menu = new ServerBuyOfferItemSelectMenu(
				session);
		session.setMenu(menu);
		menu.inventory = player.openInventory(menu.inventory).getTopInventory();
		return menu;
	}

	private String getTitle() {
		return getSession().getTranslator().$(ASMessageKey.SELECT_ITEM);
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
		ItemStack item = event.getCurrentItem();
		if (item != null && !item.getType().equals(Material.AIR)) {
			if (event.getRawSlot() >= INVENTORY_SIZE) {
				if (!getSession().getConfiguration().getForbiddenItems()
						.contains(item.getType())
						|| getSession().getPermissionsProvider().has(player,
								ASPermissionKey.TRADE_ANY_ITEM)) {
					OffersUtils.resetDurability(item);
					ServerBuyOfferPriceInput.open(getSession(), new ItemStack(
							item), player);
				} else {
					player.closeInventory();
					getSession().getTranslator().$(player,
							ASMessageKey.FORBIDDEN_TYPE);
				}
			}
		}
	}
}