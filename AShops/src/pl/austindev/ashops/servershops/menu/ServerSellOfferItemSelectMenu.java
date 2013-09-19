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

public class ServerSellOfferItemSelectMenu extends ServerShopMenu {
	private final Inventory inventory;
	private final int INVENTORY_SIZE = 9;

	private ServerSellOfferItemSelectMenu(ServerShopSession session) {
		super(session);
		this.inventory = Bukkit.createInventory(null, INVENTORY_SIZE,
				getTitle());
	}

	public static ServerSellOfferItemSelectMenu open(ServerShopSession session,
			Player player) {
		ServerSellOfferItemSelectMenu menu = new ServerSellOfferItemSelectMenu(
				session);
		session.setMenu(menu);
		player.openInventory(menu.inventory);
		return menu;
	}

	private String getTitle() {
		return getSession().getTranslator().$(ASMessageKey.SELECT_ITEM);
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
					ServerSellOfferPriceInput.open(getSession(), player,
							new ItemStack(item));
				} else {
					player.closeInventory();
					getSession().getTranslator().$(player,
							ASMessageKey.FORBIDDEN_TYPE);
				}
			}
		}
	}

	@Override
	public Inventory getInventory() {
		return inventory;
	}

}
