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

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import pl.austindev.ashops.ASMessageKey;
import pl.austindev.ashops.ASPermissionKey;
import pl.austindev.ashops.Offer;
import pl.austindev.ashops.OffersUtils;

public class PlayerBuyOfferPriceInput extends PlayerShopInput {
	private final ItemStack item;

	private PlayerBuyOfferPriceInput(PlayerShopSession session, ItemStack item) {
		super(session);
		this.item = item;
	}

	public static PlayerBuyOfferPriceInput open(PlayerShopSession session,
			ItemStack item, Player player) {
		PlayerBuyOfferPriceInput menu = new PlayerBuyOfferPriceInput(session,
				item);
		menu.getSession().setInput(menu);
		player.closeInventory();
		session.getTranslator().$(player, ASMessageKey.INSERT_PRICE);
		return menu;

	}

	@Override
	public void onInput(final Player player, String message) {
		final BigDecimal price = OffersUtils.toPrice(message);
		if (price.compareTo(BigDecimal.ZERO) > 0
				&& price.precision() <= Offer.PRICE_PRECISION) {
			BigDecimal minimalPrice = getSession().getConfiguration()
					.getMinimalPrice(item.getType());
			if (minimalPrice.compareTo(price) <= 0
					|| getSession().getPermissionsProvider().has(player,
						ASPermissionKey.ALLOW_ANY_PRICE)) {
				PlayerBuyOfferMaxAmountInput.open(getSession(), player, item,
					price);
			} else {
				getSession().end();
				getSession().getTranslator().$(player,
					ASMessageKey.MINIMAL_PRICE,
					OffersUtils.getFormatedPrice(minimalPrice));
			}
		} else {
			getSession().end();
			getSession().getTranslator().$(player, ASMessageKey.WRONG_PRICE);
		}
	}
}
