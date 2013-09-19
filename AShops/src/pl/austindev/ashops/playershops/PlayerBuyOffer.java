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
package pl.austindev.ashops.playershops;

import java.math.BigDecimal;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import pl.austindev.ashops.OfferType;

@Entity
@DiscriminatorValue("BUY")
public class PlayerBuyOffer extends PlayerShopOffer {

	public PlayerBuyOffer() {
	}

	public PlayerBuyOffer(PlayerShopItem item, BigDecimal price) {
		super(item, price);
	}

	@Override
	public OfferType getType() {
		return OfferType.BUY;
	}

}
