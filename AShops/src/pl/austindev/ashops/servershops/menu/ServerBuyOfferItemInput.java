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

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import pl.austindev.ashops.ASMessageKey;
import pl.austindev.ashops.ASPermissionKey;
import pl.austindev.ashops.OffersUtils;

public class ServerBuyOfferItemInput extends ServerShopInput {

	private ServerBuyOfferItemInput(ServerShopSession session) {
		super(session);
	}

	public static ServerBuyOfferItemInput open(ServerShopSession session,
			Player player) {
		ServerBuyOfferItemInput menu = new ServerBuyOfferItemInput(session);
		session.setInput(menu);
		player.closeInventory();
		session.getTranslator().$(player, ASMessageKey.INSERT_ITEM_ID);
		return menu;
	}

	@Override
	public void onInput(final Player player, String message) {
		ItemStack item = OffersUtils.toItem(message);
		if (item != null) {
			if (!getSession().getConfiguration().getForbiddenItems()
					.contains(item.getType())
					|| getSession().getPermissionsProvider().has(player,
						ASPermissionKey.TRADE_ANY_ITEM)) {
				ServerBuyOfferPriceInput.open(getSession(), item, player);
			} else {
				getSession().getTranslator().$(player,
					ASMessageKey.FORBIDDEN_TYPE);
			}
		} else {
			getSession().getTranslator().$(player, ASMessageKey.WRONG_ITEM);
		}
	}
}
