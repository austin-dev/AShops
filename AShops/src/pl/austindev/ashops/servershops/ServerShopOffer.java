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
package pl.austindev.ashops.servershops;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import pl.austindev.ashops.Offer;
import pl.austindev.ashops.OfferType;

@Entity
@Table(name = "as_s_offers")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(discriminatorType = DiscriminatorType.STRING, name = "type", length = 4)
public abstract class ServerShopOffer implements Offer {
	private volatile Integer id;
	private volatile ServerShopItem item;
	private volatile BigDecimal price;

	public ServerShopOffer() {
	}

	@Id
	@Column(length = 9)
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	@ManyToOne(optional = false)
	public ServerShopItem getItem() {
		return item;
	}

	public void setItem(ServerShopItem item) {
		this.item = item;
	}

	@Column(precision = PRICE_PRECISION + PRICE_SCALE, scale = PRICE_SCALE)
	public BigDecimal getPrice() {
		return price;
	}

	public void setPrice(BigDecimal price) {
		this.price = price;
	}

	@Transient
	private volatile int slot;

	public ServerShopOffer(ServerShopItem item, BigDecimal price) {
		setItem(item);
		setPrice(price);
	}

	@Transient
	public int getSlot() {
		return slot;
	}

	public void setSlot(int slot) {
		this.slot = slot;
	}

	@Transient
	public abstract OfferType getType();

}
