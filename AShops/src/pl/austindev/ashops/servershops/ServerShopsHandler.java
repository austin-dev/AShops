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

import java.util.Map;

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
import pl.austindev.ashops.ShopDataException;
import pl.austindev.ashops.ShopSessionsManager;
import pl.austindev.ashops.ShopState;
import pl.austindev.ashops.ShopType;
import pl.austindev.ashops.ShopsHandler;
import pl.austindev.ashops.SignedChest;
import pl.austindev.ashops.commands.ABUYCommandExecutor.BuyOfferAddProcedureValues;
import pl.austindev.ashops.commands.ASELLCommandExecutor.SellOfferAddProcedureValues;
import pl.austindev.ashops.servershops.menu.ServerShopInventoryMenu;
import pl.austindev.ashops.servershops.menu.ServerShopManagerMenu;
import pl.austindev.ashops.servershops.menu.ServerShopMenu;
import pl.austindev.ashops.servershops.menu.ServerShopSession;
import pl.austindev.mc.TemporaryValuesContainer.TemporaryValue;

public class ServerShopsHandler extends ShopsHandler {
	ShopSessionsManager<ServerShopSession> sessionsManager = new ShopSessionsManager<ServerShopSession>();

	public ServerShopsHandler(AShops plugin) {
		super(plugin, ShopType.SERVER_SHOP);
	}

	public ServerShopsManager getShopsManager() {
		return getPlugin().getServerShopsManager();
	}

	@Override
	public void handleCreateTag(SignedChest signedChest, Player player,
		String[] arguments) {
		if (getPlugin().getPermissionsProvider().has(player,
				ASPermissionKey.SERVER_SHOP))
			handleShopCreate(player, signedChest, null);
		else
			getTranslator().$(player, ASMessageKey.NO_PERMISSION);
	}

	@Override
	public void handleShopCreate(final Player player,
		final SignedChest signedChest, TemporaryValue temporaryValue) {
		final ServerShopCreateEvent event = ServerShopCreateEvent.trigger(
				player, signedChest.getLocation());
		if (!event.isCancelled()) {
			getPlugin().asynch(new Runnable() {

				@Override
				public void run() {
					try {
						getShopsManager().createShop(signedChest.getLocation());
						getPlugin().synch(new Runnable() {

							@Override
							public void run() {
								signedChest.initialize(ShopType.SERVER_SHOP,
										"", ShopState.OPEN);
								getTranslator().$(player,
										ASMessageKey.SHOP_CREATED);
							}
						});
					} catch (ShopDataException e) {
						getTranslator().synch$(player, e, ASMessageKey.ERROR);
					}
				}
			});
		}
	}

	@Override
	public void handleShopRemove(final Player player,
		final SignedChest signedChest, final Boolean force) {
		if (getPlugin().getPermissionsProvider().has(player,
				ASPermissionKey.SERVER_SHOP)) {
			getPlugin().asynch(new Runnable() {

				@Override
				public void run() {
					try {
						final ServerShop shop = getShopsManager().removeShop(
								signedChest.getLocation());
						getPlugin().synch(new Runnable() {

							@Override
							public void run() {
								Block block = shop.getLocation().getBlock();
								sessionsManager.remove(block.getLocation());
								SignedChest signedChest = SignedChest
										.findShopChest(block);
								if (signedChest != null)
									signedChest.removeShop();
								getTranslator().$(player,
										ASMessageKey.SHOP_REMOVED);
							}
						});
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
		final SignedChest signedChest, final BuyOfferAddProcedureValues values) {
		if (getPlugin().getPermissionsProvider().has(player,
				ASPermissionKey.SERVER_SHOP)) {
			getPlugin().asynch(new Runnable() {

				@Override
				public void run() {
					try {
						final ServerShopOffer offer = getShopsManager()
								.addOffer(signedChest.getLocation(),
										OfferType.BUY, values.getItem(),
										values.getPrice());
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
			getTranslator().$(player, ASMessageKey.NO_PERMISSION);
		}
	}

	@Override
	public void handleSellOfferAdd(final Player player,
		final SignedChest signedChest, final SellOfferAddProcedureValues values) {
		if (getPlugin().getPermissionsProvider().has(player,
				ASPermissionKey.SERVER_SHOP)) {
			getPlugin().asynch(new Runnable() {

				@Override
				public void run() {
					try {
						final ServerShopOffer offer = getShopsManager()
								.addOffer(signedChest.getLocation(),
										OfferType.SELL, values.getItem(),
										values.getPrice());
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
			getTranslator().$(player, ASMessageKey.NO_PERMISSION);
		}

	}

	@Override
	public void handleAccess(Player player, SignedChest signedChest) {
		if (signedChest.getShopState().equals(ShopState.OPEN)
				|| getPlugin().getPermissionsProvider().has(player,
						ASPermissionKey.SERVER_SHOP)) {
			ServerShopSession session = new ServerShopSession(
					signedChest.getLocation(), this, player.getName());
			getPlugin().getTemporaryValues().put(player.getName(),
					ShopType.SERVER_SHOP, session);
			sessionsManager.register(session);
			loadShop(player, session);
		} else {
			getTranslator().$(player, ASMessageKey.SHOP_CLOSED);
		}
	}

	@Override
	public void handleManagerAccess(Player player, SignedChest signedChest) {
		if (getPlugin().getPermissionsProvider().has(player,
				ASPermissionKey.SERVER_SHOP)) {
			ServerShopSession session = new ServerShopSession(
					signedChest.getLocation(), this, player.getName());
			getPlugin().getTemporaryValues().put(player.getName(),
					ShopType.SERVER_SHOP, session);
			sessionsManager.register(session);
			ServerShopManagerMenu.open(session, player);
		} else {
			getTranslator().$(player, ASMessageKey.NO_PERMISSION);
		}
	}

	@Override
	public void handleInventoryClose(Player player,
		TemporaryValue temporaryValue, Inventory inventory) {
		ServerShopSession session = temporaryValue.get(ServerShopSession.class);
		if (session.getMenu() != null)
			if (session.getMenu().getInventory().getTitle()
					.equals(inventory.getTitle()))
				session.getMenu().onClose(player, inventory);
	}

	@Override
	public void handleChatInput(Player player, TemporaryValue temporaryValue,
		String message) {
		ServerShopSession session = temporaryValue.get(ServerShopSession.class);
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
		ServerShopSession session = temporaryValue.get(ServerShopSession.class);
		ServerShopMenu menu = session.getMenu();
		if (menu != null)
			if (session.getMenu().getInventory().getTitle()
					.equals(event.getInventory().getTitle()))
				menu.onClick(player, event);
	}

	@Override
	public boolean handleChestInventoryOpen(Player player, Chest chest,
		TemporaryValue temporaryValue) {
		ServerShopSession session = temporaryValue.get(ServerShopSession.class);
		if (session.getMenu() != null)
			return session.getLocation().equals(chest.getLocation());
		else if (session.getInput() != null)
			getTranslator().$(player, ASMessageKey.ACTION_ABORTED);
		session.end();
		return false;
	}

	public void updateIcon(Location location, int slot) {
		Map<String, ServerShopSession> map = sessionsManager.get(location);
		if (map != null) {
			for (ServerShopSession session : map.values()) {
				if (session.getMenu() != null)
					if (session.getMenu() instanceof ServerShopInventoryMenu) {
						((ServerShopInventoryMenu) session.getMenu())
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
						ASPermissionKey.SERVER_SHOP)) {
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
	public void handlePlayerTeleport(Player player, TemporaryValue value) {
		ServerShopSession session = value.get(ServerShopSession.class);
		if (session != null) {
			sessionsManager.remove(session.getLocation());
		}
	}

	private void loadShop(final Player player, final ServerShopSession session) {
		getPlugin().asynch(new Runnable() {

			@Override
			public void run() {
				ServerShopsManager.FutureShop futureShop = getShopsManager()
						.get(session.getLocation());
				try {
					final ServerShop shop = futureShop.getShop();
					getPlugin().synch(new Runnable() {

						@Override
						public void run() {
							shop.getLock().lock();
							try {
								ServerShopInventoryMenu.open(session, shop,
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

	public void closeSession(Location location, String playerName) {
		sessionsManager.unregister(location, playerName);
		getPlugin().getTemporaryValues().remove(playerName);
	}

	@Override
	public void handlePlayerJoin(Player player) {
		// TODO Auto-generated method stub

	}

}
