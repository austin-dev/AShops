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
package pl.austindev.ashops.listeners;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Result;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

import pl.austindev.ashops.ASMessageKey;
import pl.austindev.ashops.AShops;
import pl.austindev.ashops.ShopType;
import pl.austindev.ashops.ShopsHandler;
import pl.austindev.ashops.SignedChest;
import pl.austindev.ashops.commands.ABUYCommandExecutor.BuyOfferAddProcedureValues;
import pl.austindev.ashops.commands.ASCommand;
import pl.austindev.ashops.commands.ASELLCommandExecutor.SellOfferAddProcedureValues;
import pl.austindev.mc.TemporaryValuesContainer.TemporaryValue;

public class ASPlayerListener extends ASListener {

	public ASPlayerListener(AShops plugin) {
		super(plugin);
	}

	public static void register(AShops plugin) {
		ASPlayerListener listener = new ASPlayerListener(plugin);
		Bukkit.getPluginManager().registerEvents(listener, plugin);
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		for (ShopType type : ShopType.values())
			getShopsHandler(type).handlePlayerJoin(player);
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerQuit(PlayerQuitEvent event) {
		Player player = event.getPlayer();
		getTemporaryValues().remove(player.getName());
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onPlayerTeleport(PlayerTeleportEvent event) {
		Player player = event.getPlayer();
		TemporaryValue temporaryValue = getTemporaryValues().get(
				player.getName());
		if (temporaryValue != null
				&& temporaryValue.getSource() instanceof ShopType) {
			ShopType shopType = (ShopType) temporaryValue.getSource();
			getShopsHandler(shopType).handlePlayerTeleport(player,
					temporaryValue);
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onPlayerPickupItemEvent(PlayerPickupItemEvent event) {
		Player player = event.getPlayer();
		TemporaryValue temporaryValue = getTemporaryValues().get(
				player.getName());
		if (temporaryValue != null
				&& temporaryValue.getSource() instanceof ShopType) {
			event.setCancelled(true);
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = false)
	public void onPlayerInteract(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		Block block = event.getClickedBlock();
		switch (event.getAction()) {
		case RIGHT_CLICK_BLOCK:
			onRightClickBlock(event, player, block);
			break;
		case LEFT_CLICK_BLOCK:
			onLeftClickBlock(event, player, block);
			break;
		default:
			break;
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = false)
	public void onChatEvent(AsyncPlayerChatEvent event) {
		Player player = event.getPlayer();
		TemporaryValue temporaryValue = getTemporaryValues().get(
				player.getName());
		if (temporaryValue != null
				&& temporaryValue.getSource() instanceof ShopType) {
			ShopType shopType = (ShopType) temporaryValue.getSource();
			getShopsHandler(shopType).handleChatInput(player, temporaryValue,
					event.getMessage());
			event.setCancelled(true);
		}
	}

	private void onRightClickBlock(PlayerInteractEvent event, Player player,
			Block block) {
		TemporaryValue temporaryValue = getTemporaryValues().get(
				player.getName());
		if (temporaryValue != null
				&& temporaryValue.getSource() instanceof ASCommand) {
			handleCommandsTemporaryValue(event, player, block, temporaryValue);
			getTemporaryValues().remove(player.getName());
		} else {
			SignedChest signedChest = SignedChest.findShopChest(block);
			if (signedChest != null && signedChest.isShop()) {
				if (block.getType().equals(Material.WALL_SIGN)) {
					if (!player.isSneaking()) {
						handleAccess(event, player, signedChest);
					} else {
						handleManagerAccess(event, player, signedChest);
					}
				}
				cancelInteractEvent(event);
			}
		}
	}

	private void onLeftClickBlock(PlayerInteractEvent event, Player player,
			Block block) {
		TemporaryValue temporaryValue = getTemporaryValues().get(
				player.getName());
		if (temporaryValue != null
				&& temporaryValue.getSource() instanceof ASCommand) {
			getTemporaryValues().remove(player.getName());
			getTranslator().$(player, ASMessageKey.RIGHT_CLICK_EXPECTED);
		}
	}

	private void handleManagerAccess(PlayerInteractEvent event, Player player,
			SignedChest signedChest) {
		if (!player.getGameMode().equals(GameMode.CREATIVE)) {
			if (!signedChest.hasForbiddenNeighbour()) {
				getShopsHandler(signedChest.getShopType()).handleManagerAccess(
						player, signedChest);
			} else {
				getTranslator().$(player, ASMessageKey.FORBIDDEN_NEIGHBOUR,
						signedChest.getForbiddenNeighbour());
			}
		} else {
			getTranslator().$(player, ASMessageKey.ILLEGAL_GAME_MODE);
		}
	}

	private void handleCommandsTemporaryValue(PlayerInteractEvent event,
			Player player, Block block, TemporaryValue temporaryValue) {
		ASCommand command = (ASCommand) temporaryValue.getSource();
		if (!event.isCancelled()) {
			SignedChest signedChest = SignedChest.findShopChest(block);
			if (signedChest != null) {
				if (!signedChest.hasForbiddenNeighbour()) {
					if (signedChest.isShop()) {
						if (!handleShopCommands(player, signedChest, command,
								temporaryValue))
							getTranslator()
									.$(player, ASMessageKey.ALREADY_SHOP);
					} else {
						if (!handleCreateCommands(player, signedChest, command,
								temporaryValue))
							getTranslator().$(player, ASMessageKey.NOT_SHOP);
					}
				} else {
					getTranslator().$(player, ASMessageKey.FORBIDDEN_NEIGHBOUR,
							signedChest.getForbiddenNeighbour());
				}
			} else {
				getTranslator().$(player, ASMessageKey.ACTION_ABORTED);
			}
		}
		cancelInteractEvent(event);
	}

	private boolean handleCreateCommands(Player player,
			SignedChest signedChest, ASCommand command,
			TemporaryValue temporaryValue) {
		for (ShopType shopType : ShopType.values())
			if (shopType.getCreateCommand().equals(command)) {
				getShopsHandler(shopType).handleShopCreate(player, signedChest,
						temporaryValue);
				return true;
			}
		return false;
	}

	private boolean handleShopCommands(Player player, SignedChest signedChest,
			ASCommand command, TemporaryValue temporaryValue) {
		ShopsHandler handler = getShopsHandler(signedChest.getShopType());
		switch (command) {
		case ABUY:
			handler.handleBuyOfferAdd(player, signedChest,
					temporaryValue.get(BuyOfferAddProcedureValues.class));
			return true;
		case ASELL:
			handler.handleSellOfferAdd(player, signedChest,
					temporaryValue.get(SellOfferAddProcedureValues.class));
			return true;
		case AREMOVE:
			handler.handleShopRemove(player, signedChest,
					temporaryValue.get(Boolean.class));
			return true;
		case ATOGGLE:
			handler.handleShopStateToggle(player, signedChest);
		default:
			return false;
		}
	}

	private void handleAccess(PlayerInteractEvent event, Player player,
			SignedChest signedChest) {
		if (!player.getGameMode().equals(GameMode.CREATIVE)) {
			if (!signedChest.hasForbiddenNeighbour()) {
				getShopsHandler(signedChest.getShopType()).handleAccess(player,
						signedChest);
			} else {
				getTranslator().$(player, ASMessageKey.FORBIDDEN_NEIGHBOUR,
						signedChest.getForbiddenNeighbour());
			}
		} else {
			getTranslator().$(player, ASMessageKey.ILLEGAL_GAME_MODE);
		}
	}

	private void cancelInteractEvent(PlayerInteractEvent event) {
		event.setCancelled(true);
		event.setUseInteractedBlock(Result.DENY);
		event.setUseItemInHand(Result.DENY);
	}
}
