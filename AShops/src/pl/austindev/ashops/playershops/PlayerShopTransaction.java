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
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.bukkit.Material;

import pl.austindev.ashops.Offer;
import pl.austindev.ashops.OfferType;
import pl.austindev.ashops.ShopItem;
import pl.austindev.ashops.ShopTransaction;

@Entity
@Table(name = "as_p_transactions")
public class PlayerShopTransaction implements ShopTransaction {
	private volatile Integer id;
	private volatile Date transactionDate;
	private volatile String shopOwner;
	private volatile String client;
	private volatile String offerType;
	private volatile String itemType;
	private volatile Integer amount;
	private volatile BigDecimal value;

	public PlayerShopTransaction() {
	}

	@Id
	@Column(length = 9)
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	@Column(nullable = false)
	@Temporal(TemporalType.TIMESTAMP)
	public Date getTransactionDate() {
		return transactionDate;
	}

	public void setTransactionDate(Date transactionDate) {
		this.transactionDate = transactionDate;
	}

	@Column(nullable = false, length = 17)
	public String getShopOwner() {
		return shopOwner;
	}

	public void setShopOwner(String shopOwner) {
		this.shopOwner = shopOwner;
	}

	public String getClient() {
		return client;
	}

	@Column(nullable = false, length = 17)
	public void setClient(String client) {
		this.client = client;
	}

	@Column(nullable = false, length = 4)
	public String getOfferType() {
		return offerType;
	}

	public void setOfferType(String offerType) {
		this.offerType = offerType;
	}

	@Column(nullable = false, length = 30)
	public String getItemType() {
		return itemType;
	}

	public void setItemType(String itemType) {
		this.itemType = itemType;
	}

	@Column(length = ShopItem.AMOUNT_LENGTH, nullable = false)
	public Integer getAmount() {
		return amount;
	}

	public void setAmount(Integer amount) {
		this.amount = amount;
	}

	@Column(precision = Offer.PRICE_PRECISION + Offer.PRICE_SCALE, scale = Offer.PRICE_SCALE)
	public BigDecimal getValue() {
		return value;
	}

	public void setValue(BigDecimal value) {
		this.value = value;
	}

	public PlayerShopTransaction(Date date, String shopOwner, String client,
			OfferType offerType, Material itemType, Integer amount,
			BigDecimal value) {
		setTransactionDate(date);
		setShopOwner(shopOwner);
		setClient(client);
		setOfferType(offerType.name());
		setItemType(itemType.name());
		setAmount(amount);
		setValue(value);
	}

}
