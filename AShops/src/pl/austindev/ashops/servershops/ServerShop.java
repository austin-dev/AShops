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

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;

import pl.austindev.ashops.ItemSerializationException;
import pl.austindev.ashops.OfferType;
import pl.austindev.ashops.Shop;

@Entity
@Table(name = "as_s_shops")
public class ServerShop implements Shop {
	private volatile Integer id;
	private volatile String worldName;
	private volatile Integer x;
	private volatile Integer y;
	private volatile Integer z;
	private volatile List<ServerShopItem> items = new LinkedList<ServerShopItem>();

	public ServerShop() {
	}

	@Id
	@Column(length = 9)
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	@Column(length = 20, nullable = false)
	public String getWorldName() {
		return worldName;
	}

	public void setWorldName(String worldName) {
		this.worldName = worldName;
	}

	@Column(length = 5, nullable = false)
	public Integer getX() {
		return x;
	}

	public void setX(Integer x) {
		this.x = x;
	}

	@Column(length = 5, nullable = false)
	public Integer getY() {
		return y;
	}

	public void setY(Integer y) {
		this.y = y;
	}

	@Column(length = 5, nullable = false)
	public Integer getZ() {
		return z;
	}

	public void setZ(Integer z) {
		this.z = z;
	}

	@OneToMany(cascade = CascadeType.REMOVE, mappedBy = "shop")
	public List<ServerShopItem> getItems() {
		return items;
	}

	public void setItems(List<ServerShopItem> items) {
		this.items = items;
	}

	@Transient
	private volatile Location location;
	@Transient
	private final Map<Integer, ServerShopOffer> offers = new ConcurrentHashMap<Integer, ServerShopOffer>();

	@Transient
	private final ReentrantLock lock = new ReentrantLock();

	public ServerShop(Location location) {
		setLocation(location);
	}

	@Transient
	public Location getLocation() {
		if (location == null)
			location = new Location(Bukkit.getWorld(getWorldName()), getX(),
					getY(), getZ());
		return location;
	}

	@Transient
	public void setLocation(Location location) {
		this.location = location;
		setWorldName(location.getWorld().getName());
		setX(location.getBlockX());
		setY(location.getBlockY());
		setZ(location.getBlockZ());
	}

	@Transient
	public Map<Integer, ServerShopOffer> getOffers()
			throws ItemSerializationException {
		return offers;
	}

	@Transient
	public int getFreeSlot(OfferType offerType)
			throws ItemSerializationException {
		if (offerType.equals(OfferType.SELL)) {
			for (int s = 0; s < InventoryType.CHEST.getDefaultSize(); s++)
				if (!getOffers().containsKey(s))
					return s;
		} else {
			for (int b = InventoryType.CHEST.getDefaultSize() - 1; b >= 0; b--)
				if (!getOffers().containsKey(b))
					return b;
		}
		return -1;
	}

	@Transient
	public ServerShopItem getItem(ItemStack itemStack) {
		for (ServerShopItem item : getItems())
			if (item.getItemStack().isSimilar(itemStack))
				return item;
		ServerShopItem item = new ServerShopItem(this, itemStack);
		items.add(item);
		return item;
	}

	@Transient
	public void mapOffers() {
		int s = 0;
		int b = InventoryType.CHEST.getDefaultSize() - 1;
		Iterator<ServerShopItem> itemsIt = items.iterator();
		ServerShopItem item = null;
		while (itemsIt.hasNext() && s <= b) {
			item = itemsIt.next();
			if (item.getItemStack() != null) {
				List<ServerShopOffer> offers = item.getOffers();
				for (ServerShopOffer offer : offers) {
					if (offer.getType().equals(OfferType.SELL)) {
						offer.setSlot(s);
						this.offers.put(s++, offer);
					} else {
						offer.setSlot(b);
						this.offers.put(b--, offer);
					}
				}
			}
		}
	}

	@Transient
	public Lock getLock() {
		return lock;
	}

}
