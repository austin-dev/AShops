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
package pl.austindev.ashops;

import java.util.EnumSet;
import java.util.Set;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Chest;
import org.bukkit.block.Sign;

import pl.austindev.mc.BlockUtils;

public class SignedChest {
	private final Location location;
	private final Chest chest;
	private final Sign sign;
	private volatile ShopType shopType;
	private volatile String dataLine;
	private volatile ShopState shopState;
	private volatile Material forbiddenNeighbour;

	public SignedChest(Chest chest, Sign sign) {
		this.location = chest.getLocation();
		this.chest = chest;
		this.sign = sign;
		checkSign(sign);
		Block forbiddenBlock = BlockUtils.getNeighbour(chest.getBlock(),
			FORBIDDEN_TYPES);
		if (forbiddenBlock != null)
			this.forbiddenNeighbour = forbiddenBlock.getType();
	}

	public static SignedChest findShopChest(Block block) {
		if (block.getState() instanceof Chest)
			return findShopChest((Chest) block.getState());
		else if (block.getType().equals(Material.WALL_SIGN))
			return findShopChest((Sign) block.getState());
		return null;
	}

	public boolean isShop() {
		return shopType != null && shopState != null;
	}

	public ShopType getShopType() {
		return shopType;
	}

	public String getDataLine() {
		return dataLine;
	}

	public ShopState getShopState() {
		return shopState;
	}

	public Material getForbiddenNeighbour() {
		return forbiddenNeighbour;
	}

	public boolean hasForbiddenNeighbour() {
		return forbiddenNeighbour != null;
	}

	public Location getLocation() {
		return location;
	}

	public Chest getChest() {
		return chest;
	}

	public Sign getSign() {
		return sign;
	}

	public void initialize(ShopType shopType, String dataLine, ShopState state) {
		ShopSignUtils.initializeSign(sign, shopType, dataLine, state);
		this.shopType = shopType;
		this.dataLine = dataLine;
		this.shopState = state;
	}

	private static SignedChest findShopChest(Chest chest) {
		Block block = chest.getBlock().getRelative(BlockFace.UP);
		Sign sign;
		if (block != null && block.getType().equals(Material.WALL_SIGN)) {
			sign = (Sign) block.getState();
			if (ShopSignUtils.isTitled(sign))
				return new SignedChest(chest, sign);

		}
		for (BlockFace blockFace : BlockUtils.DIRECT_HORIZONTAL_BLOCK_FACES) {
			block = chest.getBlock().getRelative(blockFace);
			if (block != null && block.getType().equals(Material.WALL_SIGN)) {
				sign = (Sign) block.getState();
				if (ShopSignUtils.isTitled(sign))
					return new SignedChest(chest, sign);
			}
		}
		return null;
	}

	private static SignedChest findShopChest(Sign sign) {
		Block block = BlockUtils.getAttachedBlock(sign);
		if (block.getState() instanceof Chest) {
			return new SignedChest((Chest) block.getState(), sign);
		} else {
			block = sign.getBlock().getRelative(BlockFace.DOWN);
			if (block.getState() instanceof Chest)
				return new SignedChest((Chest) block.getState(), sign);
		}
		return null;
	}

	private void checkSign(Sign sign) {
		if (ShopSignUtils.isTitled(sign)) {
			shopType = ShopSignUtils.getShopType(sign);
			dataLine = ShopSignUtils.getDataLine(sign);
			shopState = ShopSignUtils.getShopState(sign);
		}
	}

	public static final Set<Material> FORBIDDEN_TYPES = EnumSet.of(
		Material.CHEST, Material.HOPPER);

	public void removeShop() {
		BlockUtils.setLines(sign, "", "", "", "");
		chest.getInventory().clear();
	}

	public ShopState toggleState() {
		return this.shopState = ShopSignUtils.setState(sign, shopState
				.equals(ShopState.OPEN) ? ShopState.CLOSED : ShopState.OPEN);
	}
}
