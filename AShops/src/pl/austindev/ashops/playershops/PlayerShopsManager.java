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
import java.util.List;
import java.util.concurrent.TimeUnit;

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

public class PlayerShopsManager extends ShopsManager<PlayerShop> {

	public PlayerShopsManager(AShops plugin) {
		super(plugin);
	}

	public int getShopsNumber(String playerName) throws ShopDataException {
		try {
			return getDB().find(PlayerShop.class).where()
					.eq("owner_name", playerName).findRowCount();
		} catch (PersistenceException e) {
			throw new ShopDataException("Could not get a number of shops for: "
					+ playerName + ".", e);
		}
	}

	public void createShop(Location location, String ownerName)
			throws ShopDataException {
		try {
			PlayerShop shop = new PlayerShop(location, ownerName);
			getDB().save(shop);
		} catch (PersistenceException e) {
			throw new ShopDataException(
					"Could not create a new player shop in the database for: "
							+ ownerName + ", in : " + location + ".", e);
		}
	}

	public PlayerShop removeShop(Location location, boolean force)
			throws ShopDataException {
		FutureShop futureShop = get(location);
		try {
			PlayerShop shop = futureShop.getShop();
			shop.getLock().lock();
			try {
				if (!force)
					for (PlayerShopItem item : shop.getItems())
						if (item.getAmount() > 0)
							return null;
				getDB().delete(shop);
				for (PlayerShopItem item : shop.getItems())
					for (PlayerShopOffer offer : item.getOffers())
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

	public PlayerShopOffer addOffer(Location location, OfferType offerType,
			ItemStack itemStack, BigDecimal price, int maxAmount)
			throws ShopDataException {
		FutureShop futureShop = get(location);
		try {
			PlayerShop shop = futureShop.getShop();
			shop.getLock().lock();
			try {
				if (shop.getOffers().size() < InventoryType.CHEST
						.getDefaultSize()) {
					PlayerShopItem shopItem = shop.getItem(itemStack);
					PlayerShopOffer offer = shopItem.addOffer(offerType, price);
					if (offer != null) {
						if (offerType.equals(OfferType.BUY))
							shopItem.setMaxAmount(maxAmount);
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
									"Could not add a new player offer in the database. The shop location: "
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

	public int removeOffer(PlayerShopOffer offer) throws ShopDataException {
		PlayerShopItem item = offer.getItem();
		PlayerShop shop = item.getShop();
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
						if (item.getAmount() < 1) {
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
						} else {
							return 0;
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

	public int buyItem(Player player, PlayerBuyOffer offer, int amount)
			throws ShopDataException {
		PlayerShopItem item = offer.getItem();
		PlayerShop shop = item.getShop();
		if (shop.getLock().tryLock()) {
			try {
				if (offer.getId() > -1) {
					Inventory playerInventory = player.getInventory();
					ItemStack offeredItemStack = item.getItemStack();
					int availableAmount = InventoryUtils.getAmount(
						playerInventory, offeredItemStack);
					amount = Math.min(amount, availableAmount);
					if (amount > 0) {
						amount = Math.min(
							amount,
							getPlugin().getEconomy().getAffordableAmount(
								shop.getOwnerName(),
								player.getWorld().getName(), offer.getPrice()));
						if (amount > 0) {
							amount = Math.min(amount, item.getMaxAmount()
									- item.getAmount());
							if (amount > 0) {
								amount = Math
										.min(amount, shop
												.getCapacity(offeredItemStack
														.getType()));
								if (amount > 0) {
									item.setAmount(item.getAmount() + amount);
									BigDecimal value = offer.getPrice()
											.multiply(new BigDecimal(amount));
									PlayerShopTransaction transaction = new PlayerShopTransaction(
											new Date(), shop.getOwnerName(),
											player.getName(), OfferType.BUY,
											offeredItemStack.getType(), amount,
											value);
									try {
										getDB().beginTransaction();
										try {
											getDB().save(item);
											getDB().save(transaction);
											getDB().commitTransaction();
										} finally {
											getDB().endTransaction();
										}
										InventoryUtils.remove(playerInventory,
											offeredItemStack, amount);
										getPlugin().getEconomy().pay(
											shop.getOwnerName(),
											player.getName(),
											player.getWorld().getName(), value);
										shop.takeCapacity(
											offeredItemStack.getType(), amount);
										getPlugin().getPlayerShopsHandler()
												.handleNewTransaction(
													transaction);
										return amount;
									} catch (PersistenceException e) {
										throw new ShopDataException(
												"Could not complete transaction. (type: buy)",
												e);
									}
								}
							}
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

	public int sellItem(Player player, PlayerSellOffer offer, int amount)
			throws ShopDataException {
		PlayerShopItem item = offer.getItem();
		PlayerShop shop = item.getShop();
		if (shop.getLock().tryLock()) {
			try {
				if (offer.getId() > -1) {
					Inventory playerInventory = player.getInventory();
					ItemStack offeredItemStack = item.getItemStack();
					int availableSpace = InventoryUtils.getSpace(
						playerInventory, offeredItemStack);
					amount = Math.min(amount, availableSpace);
					if (amount > 0) {
						amount = Math.min(amount, item.getAmount());
						if (amount > 0) {
							amount = Math.min(
								amount,
								getPlugin().getEconomy().getAffordableAmount(
									player.getName(),
									player.getWorld().getName(),
									offer.getPrice()));
							if (amount > 0) {
								item.setAmount(item.getAmount() - amount);
								BigDecimal value = offer.getPrice().multiply(
									new BigDecimal(amount));
								PlayerShopTransaction transaction = new PlayerShopTransaction(
										new Date(), shop.getOwnerName(),
										player.getName(), OfferType.SELL,
										offeredItemStack.getType(), amount,
										value);
								try {
									getDB().beginTransaction();
									try {
										getDB().save(item);
										getDB().save(transaction);
										getDB().commitTransaction();
									} finally {
										getDB().endTransaction();
									}
									InventoryUtils.add(playerInventory,
										offeredItemStack, amount);
									getPlugin().getEconomy().pay(
										player.getName(), shop.getOwnerName(),
										player.getWorld().getName(), value);
									shop.releaseCapacity(
										offeredItemStack.getType(), amount);
									getPlugin().getPlayerShopsHandler()
											.handleNewTransaction(transaction);
									return amount;
								} catch (PersistenceException e) {
									throw new ShopDataException(
											"Could not complete transaction. (type: sell)",
											e);
								}
							}
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

	public int loadItems(Player player, PlayerShopOffer offer, int amount)
			throws ShopDataException {
		PlayerShopItem item = offer.getItem();
		PlayerShop shop = item.getShop();
		if (shop.getLock().tryLock()) {
			try {
				if (offer.getId() > -1) {
					Inventory playerInventory = player.getInventory();
					ItemStack offeredItemStack = item.getItemStack();
					int availableAmount = InventoryUtils.getAmount(
						playerInventory, offeredItemStack);
					amount = Math.min(amount, availableAmount);
					if (amount > 0) {
						amount = Math.min(amount,
							shop.getCapacity(offeredItemStack.getType()));
						if (amount > 0) {
							item.setAmount(item.getAmount() + amount);
							try {
								getDB().save(item);
								InventoryUtils.remove(playerInventory,
									offeredItemStack, amount);
								shop.takeCapacity(offeredItemStack.getType(),
									amount);
								return amount;
							} catch (PersistenceException e) {
								throw new ShopDataException(
										"Could not load items.", e);
							}
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

	public int collectItems(Player player, PlayerShopOffer offer, int amount)
			throws ShopDataException {
		PlayerShopItem item = offer.getItem();
		PlayerShop shop = item.getShop();
		if (shop.getLock().tryLock()) {
			try {
				if (offer.getId() > -1) {
					Inventory playerInventory = player.getInventory();
					ItemStack offeredItemStack = item.getItemStack();
					int availableAmount = InventoryUtils.getSpace(
						playerInventory, offeredItemStack);
					amount = Math.min(amount, availableAmount);
					if (amount > 0) {
						amount = Math.min(amount, item.getAmount());
						if (amount > 0) {
							item.setAmount(item.getAmount() - amount);
							try {
								getDB().save(item);
								InventoryUtils.add(playerInventory,
									offeredItemStack, amount);
								shop.releaseCapacity(
									offeredItemStack.getType(), amount);
								return amount;
							} catch (PersistenceException e) {
								throw new ShopDataException(
										"Could not collect items.", e);
							}
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
	protected Class<PlayerShop> getShopClass() {
		return PlayerShop.class;
	}

	public List<PlayerShopTransaction> getTransactions(String player, long from)
			throws ShopDataException {
		try {
			return getDB().find(PlayerShopTransaction.class)
					.where("shop_owner=? && transaction_date>=?")
					.setParameter(1, player).setParameter(2, new Date(from))
					.setMaxRows(10).findList();
		} catch (PersistenceException e) {
			throw new ShopDataException(
					"Could not load transactions from the databse for: "
							+ player + " from " + from + ".", e);
		}
	}

	public void clearTransaction(int limit) throws ShopDataException {
		try {
			getDB().createSqlUpdate(
				"DELETE FROM as_p_transactions WHERE transaction_date < "
						+ (System.currentTimeMillis() - TimeUnit.DAYS
								.toMillis(limit))).execute();
		} catch (PersistenceException e) {
			throw new ShopDataException("Could not load clear transactions.", e);
		}
	}
}
