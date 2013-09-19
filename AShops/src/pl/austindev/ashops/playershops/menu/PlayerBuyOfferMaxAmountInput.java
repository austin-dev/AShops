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
import pl.austindev.ashops.OfferType;
import pl.austindev.ashops.OffersUtils;
import pl.austindev.ashops.ShopDataException;
import pl.austindev.ashops.ShopItem;
import pl.austindev.ashops.playershops.PlayerShopOffer;

public class PlayerBuyOfferMaxAmountInput extends PlayerShopInput {
	private final ItemStack item;
	private final BigDecimal price;

	private PlayerBuyOfferMaxAmountInput(PlayerShopSession session,
			ItemStack item, BigDecimal price) {
		super(session);
		this.item = item;
		this.price = price;
	}

	public static PlayerBuyOfferMaxAmountInput open(PlayerShopSession session,
		Player player, ItemStack item, BigDecimal price) {
		PlayerBuyOfferMaxAmountInput menu = new PlayerBuyOfferMaxAmountInput(
				session, item, price);
		session.setInput(menu);
		session.getTranslator().$(player, ASMessageKey.INSERT_MAX_AMOUNT);
		return menu;
	}

	@Override
	public void onInput(final Player player, String message) {
		final int maxAmount = OffersUtils.toAmount(message);
		if (maxAmount > 0
				&& Integer.toString(maxAmount).length() <= ShopItem.AMOUNT_LENGTH) {
			getSession().end();
			getSession().getPlugin().asynch(new Runnable() {

				@Override
				public void run() {
					try {
						PlayerShopOffer offer = getSession()
								.getHandler()
								.getShopsManager()
								.addOffer(getSession().getLocation(),
										OfferType.BUY, item, price, maxAmount);
						if (offer != null) {
							int slot = offer.getSlot();
							getSession().getTranslator().synch$(player,
									ASMessageKey.OFFER_ADDED);
							getSession().getHandler().updateIcon(
									getSession().getLocation(), slot);
						} else {
							getSession().getTranslator().synch$(player,
									ASMessageKey.NO_SLOTS_OR_EXISTS);
						}
					} catch (ShopDataException e) {
						getSession().getTranslator().synch$(player, e,
								ASMessageKey.ERROR);
						e.printStackTrace();
					}
				}
			});
		} else {
			getSession().end();
			getSession().getTranslator().$(player, ASMessageKey.WRONG_AMOUNT);
		}
	}
}
