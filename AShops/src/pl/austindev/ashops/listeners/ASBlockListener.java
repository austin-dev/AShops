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

import java.util.Iterator;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityExplodeEvent;

import pl.austindev.ashops.ASMessageKey;
import pl.austindev.ashops.AShops;
import pl.austindev.ashops.ShopSignUtils;
import pl.austindev.ashops.ShopType;
import pl.austindev.ashops.SignedChest;
import pl.austindev.mc.BlockUtils;

public class ASBlockListener extends ASListener {

	public ASBlockListener(AShops plugin) {
		super(plugin);
	}

	public static void register(AShops plugin) {
		ASBlockListener listener = new ASBlockListener(plugin);
		Bukkit.getPluginManager().registerEvents(listener, plugin);
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onSignChange(SignChangeEvent event) {
		Player player = event.getPlayer();
		if (!event.isCancelled()) {
			SignedChest signedChest = SignedChest.findShopChest(event
					.getBlock());
			if (signedChest != null) {
				SignedChest checkChest = SignedChest.findShopChest(signedChest
						.getChest().getBlock());
				if (checkChest == null) {
					for (ShopType shopType : ShopType.values()) {
						if (event.getLine(0).equalsIgnoreCase(
								shopType.getSignTag())) {
							getShopsHandler(shopType).handleCreateTag(
									signedChest,
									player,
									ShopSignUtils
											.getCreateCommandArguments(event
													.getLines()));
						}
					}
				}
			}
		}
	}

	@SuppressWarnings("deprecation")
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onBlockPlace(BlockPlaceEvent event) {
		Block block = event.getBlock();
		Material blockType = block.getType();
		if (SignedChest.FORBIDDEN_TYPES.contains(blockType)) {
			if (hasShopNeighbours(block)) {
				event.setCancelled(true);
				getTranslator().$(event.getPlayer(),
						ASMessageKey.FORBIDDEN_BLOCK_PLACING, blockType);
				event.getPlayer().updateInventory();
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onBlockBreak(BlockBreakEvent event) {
		Block block = event.getBlock();
		SignedChest signedChest = SignedChest.findShopChest(block);
		if (signedChest != null && signedChest.isShop()) {
			event.setCancelled(true);
			getTranslator().$(event.getPlayer(), ASMessageKey.REMOVE_SHOP);
		} else {
			if (isShopSignAttached(block)) {
				event.setCancelled(true);
				getTranslator().$(event.getPlayer(), ASMessageKey.REMOVE_SHOP);
			}
		}

	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onEntityChangeBlock(EntityChangeBlockEvent event) {
		SignedChest signedChest = SignedChest.findShopChest(event.getBlock());
		if (signedChest != null && signedChest.isShop())
			event.setCancelled(true);
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onBlockPistonExtend(BlockPistonExtendEvent event) {
		for (Block block : event.getBlocks()) {
			SignedChest signedChest = SignedChest.findShopChest(block);
			if (signedChest != null && signedChest.isShop()) {
				event.setCancelled(true);
				return;
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onBlockPistonRetract(BlockPistonRetractEvent event) {
		Block block = event.getRetractLocation().getBlock();
		if (block != null) {
			SignedChest signedChest = SignedChest.findShopChest(block);
			if (signedChest != null && signedChest.isShop())
				event.setCancelled(true);
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onEntityExplode(EntityExplodeEvent event) {
		Iterator<Block> iterator = event.blockList().iterator();
		while (iterator.hasNext()) {
			Block block = iterator.next();
			SignedChest signedChest = SignedChest.findShopChest(block);
			if (signedChest != null && signedChest.isShop())
				iterator.remove();
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onBlockIgnate(BlockIgniteEvent event) {
		Block block = event.getBlock();
		if (block != null) {
			SignedChest signedChest = SignedChest.findShopChest(block);
			if (signedChest != null && signedChest.isShop())
				event.setCancelled(true);
		}
	}

	private boolean hasShopNeighbours(Block block) {
		if (hasHorizontalShopNeighbours(block)) {
			return true;
		} else {
			Block upperBlock = block.getRelative(BlockFace.UP);
			SignedChest signedChest = SignedChest.findShopChest(upperBlock);
			if (signedChest != null && signedChest.isShop()) {
				return true;
			}
			if (hasHorizontalShopNeighbours(upperBlock)) {
				return true;
			} else {
				Block lowerBlock = block.getRelative(BlockFace.DOWN);
				signedChest = SignedChest.findShopChest(lowerBlock);
				if (signedChest != null && signedChest.isShop())
					return true;
				return hasHorizontalShopNeighbours(lowerBlock);
			}
		}
	}

	private boolean hasHorizontalShopNeighbours(Block block) {
		for (BlockFace blockFace : BlockUtils.DIRECT_HORIZONTAL_BLOCK_FACES) {
			Block neighbour = block.getRelative(blockFace);
			if (neighbour != null) {
				SignedChest signedChest = SignedChest.findShopChest(neighbour);
				if (signedChest != null && signedChest.isShop()) {
					return true;
				}
			}
		}
		return false;
	}

	private boolean isShopSignAttached(Block block) {
		for (BlockFace blockFace : BlockUtils.DIRECT_HORIZONTAL_BLOCK_FACES) {
			Block neighbour = block.getRelative(blockFace);
			if (neighbour != null
					&& neighbour.getType().equals(Material.WALL_SIGN)) {
				SignedChest signedChest = SignedChest.findShopChest(neighbour);
				if (signedChest != null && signedChest.isShop()) {
					Sign sign = (Sign) neighbour.getState();
					if (BlockUtils.getAttachedBlock(sign).equals(block)) {
						return true;
					}
				}
			}
		}
		return false;
	}
}
