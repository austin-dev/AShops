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
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

import pl.austindev.ashops.ASMessageKey;
import pl.austindev.ashops.ASPermissionKey;
import pl.austindev.ashops.AShops;
import pl.austindev.ashops.OfferType;
import pl.austindev.ashops.OffersUtils;
import pl.austindev.ashops.ShopDataException;
import pl.austindev.ashops.ShopSessionsManager;
import pl.austindev.ashops.ShopState;
import pl.austindev.ashops.ShopType;
import pl.austindev.ashops.ShopsHandler;
import pl.austindev.ashops.SignedChest;
import pl.austindev.ashops.commands.ABUYCommandExecutor.BuyOfferAddProcedureValues;
import pl.austindev.ashops.commands.ASELLCommandExecutor.SellOfferAddProcedureValues;
import pl.austindev.ashops.commands.ASHOPCommandExecutor.ASHOPCommandValues;
import pl.austindev.ashops.playershops.menu.PlayerShopInventoryMenu;
import pl.austindev.ashops.playershops.menu.PlayerShopManagerMenu;
import pl.austindev.ashops.playershops.menu.PlayerShopMenu;
import pl.austindev.ashops.playershops.menu.PlayerShopSession;
import pl.austindev.mc.PlayerUtils;
import pl.austindev.mc.TemporaryValuesContainer.TemporaryValue;

public class PlayerShopsHandler extends ShopsHandler {
	ShopSessionsManager<PlayerShopSession> sessionsManager = new ShopSessionsManager<PlayerShopSession>();

	public PlayerShopsHandler(final AShops plugin) {
		super(plugin, ShopType.PLAYER_SHOP);
		final int limit = plugin.getConfiguration().getTransactionDayLimit();
		Bukkit.getScheduler().runTaskTimerAsynchronously(plugin,
				new Runnable() {

					@Override
					public void run() {
						try {
							getShopsManager().clearTransaction(limit);
						} catch (ShopDataException e) {
							getTranslator().synch$(e);
						}
					}
				}, 0, TimeUnit.DAYS.toSeconds(1) * 20);
	}

	public PlayerShopsManager getShopsManager() {
		return getPlugin().getPlayerShopsManager();
	}

	@Override
	public void handleCreateTag(SignedChest signedChest, Player player,
			String[] arguments) {
		if (arguments.length == 0)
			tryCreateOwnShop(player, player.getName(), signedChest);
		else
			tryCreateShopForOtherPlayer(player, arguments[0], signedChest);
	}

	@Override
	public void handleShopCreate(final Player player,
			final SignedChest signedChest, TemporaryValue temporaryValue) {
		ASHOPCommandValues values = temporaryValue
				.get(ASHOPCommandValues.class);
		handleShopCreate(player, signedChest, values.getOwnerName(),
				values.getPrice());
	}

	@Override
	public void handleShopRemove(final Player player,
			final SignedChest signedChest, final Boolean force) {
		if (player.getName().equalsIgnoreCase(signedChest.getDataLine())
				|| getPlugin().getPermissionsProvider().has(player,
						ASPermissionKey.MANAGER)) {
			getPlugin().asynch(new Runnable() {

				@Override
				public void run() {
					try {
						final PlayerShop shop = getShopsManager().removeShop(
								signedChest.getLocation(), force);
						if (shop != null) {
							getPlugin().synch(new Runnable() {

								@Override
								public void run() {
									Block block = shop.getLocation().getBlock();
									sessionsManager.remove(block.getLocation());
									if (signedChest != null)
										signedChest.removeShop();
									getTranslator().$(player,
											ASMessageKey.SHOP_REMOVED);
								}
							});
						} else {
							getTranslator().synch$(player,
									ASMessageKey.UNCOLLECTED_ITEMS);
						}
					} catch (ShopDataException e) {
						getTranslator().synch$(player, e, ASMessageKey.ERROR);
					}
				}
			});
		} else {
			getTranslator().$(player, ASMessageKey.NO_PERMISSION);
		}
	}

	@Override
	public void handleBuyOfferAdd(final Player player,
			final SignedChest signedChest,
			final BuyOfferAddProcedureValues values) {
		if (signedChest.getDataLine().equalsIgnoreCase(player.getName())
				|| getPlugin().getPermissionsProvider().has(player,
						ASPermissionKey.MANAGER)) {
			getPlugin().asynch(new Runnable() {

				@Override
				public void run() {
					try {
						final PlayerShopOffer offer = getShopsManager()
								.addOffer(signedChest.getLocation(),
										OfferType.BUY, values.getItem(),
										values.getPrice(),
										values.getMaxAmount());
						if (offer != null) {
							getPlugin().synch(new Runnable() {

								@Override
								public void run() {
									updateIcon(signedChest.getLocation(),
											offer.getSlot());
									getTranslator().$(player,
											ASMessageKey.OFFER_ADDED);
								}
							});
						} else {
							getTranslator().synch$(player,
									ASMessageKey.NO_SLOTS_OR_EXISTS);
						}
					} catch (ShopDataException e) {
						getTranslator().synch$(player, e, ASMessageKey.ERROR);
					}
				}
			});
		} else {
			getTranslator().$(player, ASMessageKey.NOT_OWNER);
		}
	}

	@Override
	public void handleSellOfferAdd(final Player player,
			final SignedChest signedChest,
			final SellOfferAddProcedureValues values) {
		if (signedChest.getDataLine().equalsIgnoreCase(player.getName())
				|| getPlugin().getPermissionsProvider().has(player,
						ASPermissionKey.MANAGER)) {
			getPlugin().asynch(new Runnable() {

				@Override
				public void run() {
					try {
						final PlayerShopOffer offer = getShopsManager()
								.addOffer(signedChest.getLocation(),
										OfferType.SELL, values.getItem(),
										values.getPrice(), 0);
						if (offer != null) {
							getPlugin().synch(new Runnable() {

								@Override
								public void run() {
									updateIcon(signedChest.getLocation(),
											offer.getSlot());
									getTranslator().$(player,
											ASMessageKey.OFFER_ADDED);
								}
							});
						} else {
							getTranslator().synch$(player,
									ASMessageKey.NO_SLOTS_OR_EXISTS);
						}
					} catch (ShopDataException e) {
						getTranslator().synch$(player, e, ASMessageKey.ERROR);
					}
				}
			});
		} else {
			getTranslator().$(player, ASMessageKey.NOT_OWNER);
		}
	}

	@Override
	public void handleAccess(Player player, SignedChest signedChest) {
		boolean isOwner = signedChest.getDataLine().equalsIgnoreCase(
				player.getName());
		if (isOwner
				|| getPlugin().getPermissionsProvider().hasOneOf(player,
						ASPermissionKey.BUY_ITEMS, ASPermissionKey.SELL_ITEMS)) {
			if (isOwner
					|| signedChest.getShopState().equals(ShopState.OPEN)
					|| getPlugin().getPermissionsProvider().has(player,
							ASPermissionKey.MANAGER)) {
				PlayerShopSession session = new PlayerShopSession(
						signedChest.getLocation(), this, player.getName());
				getPlugin().getTemporaryValues().put(player.getName(),
						ShopType.PLAYER_SHOP, session);
				sessionsManager.register(session);
				loadShop(player, session);
			} else {
				getTranslator().$(player, ASMessageKey.SHOP_CLOSED);
			}
		} else {
			getTranslator().$(player, ASMessageKey.NO_PERMISSION);
		}
	}

	@Override
	public void handleManagerAccess(Player player, SignedChest signedChest) {
		if (getPlugin().getPermissionsProvider().has(player,
				ASPermissionKey.MANAGER)
				|| player.getName().equalsIgnoreCase(signedChest.getDataLine())) {
			PlayerShopSession session = new PlayerShopSession(
					signedChest.getLocation(), this, player.getName());
			getPlugin().getTemporaryValues().put(player.getName(),
					ShopType.PLAYER_SHOP, session);
			sessionsManager.register(session);
			PlayerShopManagerMenu.open(session, player);
		} else {
			getTranslator().$(player, ASMessageKey.NO_PERMISSION);
		}
	}

	@Override
	public void handleInventoryClose(Player player,
			TemporaryValue temporaryValue, Inventory inventory) {
		PlayerShopSession session = temporaryValue.get(PlayerShopSession.class);
		if (session.getMenu() != null) {
			int length = inventory.getTitle().length();
			if (session
					.getMenu()
					.getInventory()
					.getTitle()
					.startsWith(
							inventory.getTitle().substring(0,
									length > 15 ? 15 : length)))
				session.getMenu().onClose(player, inventory);
		}
	}

	@Override
	public void handleChatInput(Player player, TemporaryValue temporaryValue,
			String message) {
		PlayerShopSession session = temporaryValue.get(PlayerShopSession.class);
		if (session.getInput() != null) {
			if (session.getLocation().distanceSquared(player.getLocation()) < 100) {
				session.getInput().onInput(player, message);
			} else {
				session.end();
				getTranslator().$(player, ASMessageKey.FAR_FROM_SHOP);
			}
		}
	}

	@Override
	public void handleInventoryClick(Player player,
			TemporaryValue temporaryValue, InventoryClickEvent event) {
		PlayerShopSession session = temporaryValue.get(PlayerShopSession.class);
		PlayerShopMenu menu = session.getMenu();
		if (menu != null) {
			if (session.getMenu().getInventory().getTitle()
					.equals(event.getInventory().getTitle())) {
				menu.onClick(player, event);
			}
		}
	}

	@Override
	public boolean handleChestInventoryOpen(Player player, Chest chest,
			TemporaryValue temporaryValue) {
		PlayerShopSession session = temporaryValue.get(PlayerShopSession.class);
		if (session.getMenu() != null)
			return session.getLocation().equals(chest.getLocation());
		else if (session.getInput() != null)
			getTranslator().$(player, ASMessageKey.ACTION_ABORTED);
		session.end();
		return false;
	}

	public void closeSession(Location location, String playerName) {
		sessionsManager.unregister(location, playerName);
		getPlugin().getTemporaryValues().remove(playerName);
	}

	public void updateIcon(Location location, int slot) {
		Map<String, PlayerShopSession> map = sessionsManager.get(location);
		if (map != null) {
			for (PlayerShopSession session : map.values()) {
				if (session.getMenu() != null)
					if (session.getMenu() instanceof PlayerShopInventoryMenu) {
						((PlayerShopInventoryMenu) session.getMenu())
								.updateIcon(slot);
						return;
					}
			}
		}
	}

	@Override
	public void handlePluginDisable() {
		sessionsManager.endAll();
	}

	@Override
	public void handleShopStateToggle(Player player, SignedChest signedChest) {
		if (player.getName().equalsIgnoreCase(signedChest.getDataLine())
				|| getPlugin().getPermissionsProvider().has(player,
						ASPermissionKey.MANAGER)) {
			if (signedChest.toggleState().equals(ShopState.CLOSED)) {
				getShopsManager().remove(signedChest.getLocation());
				sessionsManager.remove(signedChest.getLocation());
			}
			getTranslator().$(player, ASMessageKey.STATE_CHANGED);
		} else {
			getTranslator().$(player, ASMessageKey.NO_PERMISSION);
		}
	}

	@Override
	public void handlePlayerJoin(final Player player) {
		final long lastPlayed = player.getLastPlayed();
		if (lastPlayed > 0) {
			getPlugin().asynch(new Runnable() {

				@Override
				public void run() {
					try {
						final List<PlayerShopTransaction> transactions = getShopsManager()
								.getTransactions(player.getName(), lastPlayed);
						if (transactions != null && transactions.size() > 0) {
							getPlugin().synch(new Runnable() {

								@Override
								public void run() {
									for (PlayerShopTransaction transaction : transactions)
										sendNotification(player, transaction);
								}
							});
						}
					} catch (ShopDataException e) {
						e.printStackTrace();
					}
				}
			});
		}
	}

	public void handleNewTransaction(PlayerShopTransaction transaction) {
		if (getPlugin().getConfiguration().shouldSendNotification()) {
			Player player = Bukkit.getPlayer(transaction.getShopOwner());
			if (player != null) {
				sendNotification(player, transaction);
			}
		}
	}

	@Override
	public void handlePlayerTeleport(Player player, TemporaryValue value) {
		PlayerShopSession session = value.get(PlayerShopSession.class);
		if (session != null) {
			sessionsManager.remove(session.getLocation());
		}
	}

	private void handleShopCreate(final Player player,
			final SignedChest signedChest, String ownerName, BigDecimal price) {
		final PlayerShopCreateEvent event = PlayerShopCreateEvent.trigger(
				player, signedChest.getLocation(), ownerName, price);
		if (!event.isCancelled()) {
			if (getPlugin().getEconomy().collectShopPrice(player,
					event.getPrice())) {
				getPlugin().asynch(new Runnable() {

					@Override
					public void run() {
						try {
							getShopsManager().createShop(event.getLocation(),
									event.getOwnerName());
							getPlugin().synch(new Runnable() {

								@Override
								public void run() {
									signedChest.initialize(getShopType(),
											event.getOwnerName(),
											ShopState.OPEN);
									getTranslator().$(player,
											ASMessageKey.SHOP_CREATED);
								}
							});
						} catch (ShopDataException e) {
							getTranslator().synch$(player, e,
									ASMessageKey.ERROR);
						}
					}
				});
			} else {
				getTranslator().$(player, ASMessageKey.NO_MONEY);
			}
		}

	}

	private void tryCreateOwnShop(final Player player, final String playerName,
			final SignedChest signedChest) {
		if (getPlugin().getPermissionsProvider().has(player,
				ASPermissionKey.PLAYER_SHOP)) {
			final BigDecimal shopPrice = getPlugin().getPermissionsProvider()
					.has(player, ASPermissionKey.FREE_SHOP) ? BigDecimal.ZERO
					: getPlugin().getConfiguration().getPrice(player);
			if (!getPlugin().getPermissionsProvider().has(player,
					ASPermissionKey.UNLIMITED_SHOPS)) {
				final int shopsLimit = getPlugin().getConfiguration()
						.getShopsLimit(player);
				getPlugin().asynch(new Runnable() {

					@Override
					public void run() {
						try {
							int shopsNumber = getPlugin()
									.getPlayerShopsManager().getShopsNumber(
											playerName);
							if (shopsNumber < shopsLimit) {
								getPlugin().synch(new Runnable() {

									@Override
									public void run() {
										handleShopCreate(player, signedChest,
												playerName, shopPrice);
									}
								});
							} else {
								getTranslator().synch$(player,
										ASMessageKey.SHOPS_LIMIT);
							}
						} catch (ShopDataException e) {
							getTranslator().synch$(player, e,
									ASMessageKey.ERROR);
						}
					}
				});
			} else {
				handleShopCreate(player, signedChest, playerName, shopPrice);
			}
		} else {
			getTranslator().$(player, ASMessageKey.NO_PERMISSION);
		}
	}

	private void tryCreateShopForOtherPlayer(Player player, String ownerName,
			SignedChest signedChest) {
		if (getPlugin().getPermissionsProvider().has(player,
				ASPermissionKey.MANAGER)) {
			if (PlayerUtils.isValidPlayerName(ownerName)) {
				handleShopCreate(player, signedChest, ownerName,
						BigDecimal.ZERO);
			} else {
				getTranslator().$(player, ASMessageKey.INVALID_PLAYER);
			}
		} else {
			getTranslator().$(player, ASMessageKey.NO_PERMISSION);
		}
	}

	private void loadShop(final Player player, final PlayerShopSession session) {
		getPlugin().asynch(new Runnable() {

			@Override
			public void run() {
				PlayerShopsManager.FutureShop futureShop = getShopsManager()
						.get(session.getLocation());
				try {
					final PlayerShop shop = futureShop.getShop();
					getPlugin().synch(new Runnable() {

						@Override
						public void run() {
							shop.getLock().lock();
							try {
								PlayerShopInventoryMenu.open(session, shop,
										player);
							} finally {
								shop.getLock().unlock();
							}
						}
					});
				} catch (ShopDataException e) {
					getShopsManager().remove(session.getLocation());
					session.end();
					handleLoadingFailure(player, e);
				}
			}
		});
	}

	private void handleLoadingFailure(final Player player, final Exception e) {
		getPlugin().synch(new Runnable() {

			@Override
			public void run() {
				e.printStackTrace();
				getTranslator().$(player, ASMessageKey.ERROR);
				player.closeInventory();
			}
		});
	}

	private void sendNotification(Player player,
			PlayerShopTransaction transaction) {
		String message;
		if (transaction.getOfferType().equalsIgnoreCase(OfferType.SELL.name())) {
			message = ChatColor.GREEN
					+ "+"
					+ OffersUtils.getFormatedPrice(transaction.getValue())
					+ ChatColor.GRAY
					+ " ("
					+ getTranslator().$(ASMessageKey.SOLD_NOTIFICATION,
							transaction.getAmount(), transaction.getItemType(),
							transaction.getClient()) + ")";
		} else {
			message = ChatColor.RED
					+ "-"
					+ OffersUtils.getFormatedPrice(transaction.getValue())
					+ ChatColor.GRAY
					+ " ("
					+ getTranslator().$(ASMessageKey.BOUGHT_NOTIFICATION,
							transaction.getAmount(), transaction.getItemType(),
							transaction.getClient()) + ")";
		}
		player.sendMessage(message);
	}
}
