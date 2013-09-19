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

import java.io.IOException;
import java.math.BigDecimal;
import java.util.LinkedList;
import java.util.List;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.bukkit.inventory.ItemStack;

import pl.austindev.ashops.ItemSerializationException;
import pl.austindev.ashops.OfferType;
import pl.austindev.ashops.ShopItem;
import pl.austindev.ashops.ShopType;
import pl.austindev.mc.SerializationUtils;

@Entity
@Table(name = "as_s_items")
public class ServerShopItem implements ShopItem {
	private volatile Integer id;
	private volatile ServerShop shop;
	private volatile byte[] serializedItemStack;
	private volatile List<ServerShopOffer> offers = new LinkedList<ServerShopOffer>();

	public ServerShopItem() {
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
	public ServerShop getShop() {
		return shop;
	}

	public void setShop(ServerShop shop) {
		this.shop = shop;
	}

	@Basic
	@Column(nullable = false, columnDefinition = "BLOB")
	public byte[] getSerializedItemStack() {
		return serializedItemStack;
	}

	public void setSerializedItemStack(byte[] serializedItemStack) {
		this.serializedItemStack = serializedItemStack;
	}

	@OneToMany(mappedBy = "item", cascade = CascadeType.REMOVE)
	public List<ServerShopOffer> getOffers() {
		return offers;
	}

	public void setOffers(List<ServerShopOffer> offers) {
		this.offers = offers;
	}

	@Transient
	private volatile ItemStack itemStack;

	public ServerShopItem(ServerShop shop, ItemStack itemStack) {
		setShop(shop);
		setItemStack(itemStack);
	}

	@Transient
	public ItemStack getItemStack() {
		if (itemStack == null)
			try {
				this.itemStack = SerializationUtils
						.toItemStack(getSerializedItemStack());
			} catch (ClassNotFoundException e) {
				throw new ItemSerializationException(e, id,
						itemStack.getType(), ShopType.SERVER_SHOP);
			} catch (IOException e) {
				throw new ItemSerializationException(e, id,
						itemStack.getType(), ShopType.SERVER_SHOP);
			}
		return itemStack;
	}

	@Transient
	public void setItemStack(ItemStack itemStack) {
		this.itemStack = itemStack;
		try {
			setSerializedItemStack(SerializationUtils.toByteArray(itemStack));
		} catch (IOException e) {
			throw new ItemSerializationException(e, id, itemStack.getType(),
					ShopType.SERVER_SHOP);
		}
	}

	@Transient
	public ServerShopOffer addOffer(OfferType offerType, BigDecimal price) {
		for (ServerShopOffer o : offers)
			if (o.getType().equals(offerType))
				return null;
		ServerShopOffer offer = offerType.equals(OfferType.SELL) ? new ServerSellOffer(
				this, price) : new ServerBuyOffer(this, price);
		offer.setSlot(shop.getFreeSlot(offerType));
		offers.add(offer);
		shop.getOffers().put(offer.getSlot(), offer);
		return offer;
	}
}
