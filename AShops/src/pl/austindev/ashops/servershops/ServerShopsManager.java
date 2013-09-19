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

import javax.persistence.PersistenceException;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import pl.austindev.ashops.AShops;
import pl.austindev.ashops.OfferType;
import pl.austindev.ashops.ShopDataException;
import pl.austindev.ashops.ShopsManager;
import pl.austindev.mc.InventoryUtils;

public class ServerShopsManager extends ShopsManager<ServerShop> {

	public ServerShopsManager(AShops plugin) {
		super(plugin);
	}

	public void createShop(Location location) throws ShopDataException {
		try {
			ServerShop shop = new ServerShop(location);
			getDB().save(shop);
		} catch (PersistenceException e) {
			throw new ShopDataException(
					"Could not create a new server shop in the database in: "
							+ location + ".", e);
		}
	}

	public ServerShop removeShop(Location location) throws ShopDataException {
		FutureShop futureShop = get(location);
		try {
			ServerShop shop = futureShop.getShop();
			shop.getLock().lock();
			try {
				getDB().delete(shop);
				for (ServerShopItem item : shop.getItems())
					for (ServerShopOffer offer : item.getOffers())
						offer.setId(-1);
				shop.setId(-1);
				remove(location);
				return shop;
			} finally {
				shop.getLock().unlock();
			}
		} catch (ShopDataException e) {
			remove(location);
			throw e;
		} finally {
			release(location);
		}
	}

	public ServerShopOffer addOffer(Location location, OfferType offerType,
			ItemStack itemStack, BigDecimal price) throws ShopDataException {
		FutureShop futureShop = get(location);
		try {
			ServerShop shop = futureShop.getShop();
			shop.getLock().lock();
			try {
				if (shop.getOffers().size() < InventoryType.CHEST
						.getDefaultSize()) {
					ServerShopItem shopItem = shop.getItem(itemStack);
					ServerShopOffer offer = shopItem.addOffer(offerType, price);
					if (offer != null) {
						try {
							getDB().beginTransaction();
							getDB().save(shopItem);
							getDB().save(offer);
							getDB().commitTransaction();
							return offer;
						} catch (PersistenceException e) {
							shop.getOffers().remove(offer.getSlot());
							shopItem.getOffers().remove(offer);
							if (shopItem.getOffers().size() < 1)
								shop.getItems().remove(shopItem);
							throw new ShopDataException(
									"Could not add a new server offer in the database. The shop location: "
											+ location + ".", e);
						}
					}
				}
			} finally {
				shop.getLock().unlock();
			}
		} catch (ShopDataException e) {
			remove(location);
			throw e;
		} finally {
			release(location);
		}
		return null;
	}

	public int removeOffer(ServerShopOffer offer) throws ShopDataException {
		ServerShopItem item = offer.getItem();
		ServerShop shop = item.getShop();
		if (shop.getLock().tryLock()) {
			try {
				if (offer.getId() > -1) {
					if (item.getOffers().size() > 1) {
						try {
							getDB().delete(offer);
							item.getOffers().remove(offer);
							shop.getOffers().remove(offer.getSlot());
							offer.setId(-1);
							return 1;
						} catch (PersistenceException e) {
							throw new ShopDataException(
									"Could not remove a player offer.", e);
						}
					} else {
						try {
							getDB().delete(offer);
							getDB().delete(item);
							shop.getItems().remove(item);
							shop.getOffers().remove(offer.getSlot());
							offer.setId(-1);
							return 2;
						} catch (PersistenceException e) {
							throw new ShopDataException(
									"Could not remove a player offer.", e);
						}
					}
				} else {
					return 0;
				}
			} finally {
				shop.getLock().unlock();
			}
		} else {
			return -1;
		}
	}

	public int buyItem(Player player, ServerBuyOffer offer, int amount)
			throws ShopDataException {
		ServerShopItem item = offer.getItem();
		ServerShop shop = item.getShop();
		if (shop.getLock().tryLock()) {
			try {
				if (offer.getId() > -1) {
					Inventory playerInventory = player.getInventory();
					ItemStack offeredItemStack = item.getItemStack();
					int availableAmount = InventoryUtils.getAmount(
						playerInventory, offeredItemStack);
					amount = Math.min(amount, availableAmount);
					if (amount > 0) {
						InventoryUtils.remove(playerInventory,
							offeredItemStack, amount);
						getPlugin().getEconomy().pay(player.getName(),
							player.getWorld().getName(),
							offer.getPrice().multiply(new BigDecimal(amount)));
						return amount;
					} else {
						return 0;
					}
				} else {
					return 0;
				}
			} finally {
				shop.getLock().unlock();
			}
		} else {
			return -1;
		}
	}

	public int sellItem(Player player, ServerShopOffer offer, int amount)
			throws ShopDataException {
		ServerShopItem item = offer.getItem();
		ServerShop shop = item.getShop();
		if (shop.getLock().tryLock()) {
			try {
				if (offer.getId() > -1) {
					Inventory playerInventory = player.getInventory();
					ItemStack offeredItemStack = item.getItemStack();
					int availableSpace = InventoryUtils.getSpace(
						playerInventory, offeredItemStack);
					amount = Math.min(amount, availableSpace);
					if (amount > 0) {
						amount = Math.min(
							amount,
							getPlugin().getEconomy().getAffordableAmount(
								player.getName(), player.getWorld().getName(),
								offer.getPrice()));
						if (amount > 0) {
							InventoryUtils.add(playerInventory,
								offeredItemStack, amount);
							getPlugin().getEconomy().take(
								player.getName(),
								player.getWorld().getName(),
								offer.getPrice().multiply(
									new BigDecimal(amount)));
							return amount;
						}
					}
					return amount;
				} else {
					return 0;
				}
			} finally {
				shop.getLock().unlock();
			}
		} else {
			return -1;
		}
	}

	@Override
	protected Class<ServerShop> getShopClass() {
		return ServerShop.class;
	}

}
