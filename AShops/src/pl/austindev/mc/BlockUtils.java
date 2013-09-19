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
package pl.austindev.mc;

import java.util.EnumSet;
import java.util.Set;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;

public class BlockUtils {
	public final static Set<BlockFace> DIRECT_HORIZONTAL_BLOCK_FACES = EnumSet
			.of(BlockFace.NORTH, BlockFace.EAST, BlockFace.WEST,
				BlockFace.SOUTH);

	private BlockUtils() {
	}

	public static void setLines(Sign sign, String firstLine, String secondLine,
			String thirdLine, String fourthLine) {
		if (firstLine != null)
			sign.setLine(0, firstLine);
		if (secondLine != null)
			sign.setLine(1, secondLine);
		if (thirdLine != null)
			sign.setLine(2, thirdLine);
		if (fourthLine != null)
			sign.setLine(3, fourthLine);
		sign.update();
	}

	public static Block getAttachedBlock(Sign sign) {
		org.bukkit.material.Sign signData = (org.bukkit.material.Sign) sign
				.getData();
		if (signData.isWallSign())
			return sign.getBlock().getRelative(signData.getAttachedFace());
		else
			return null;
	}

	public static Block getNeighbour(Block block, Set<Material> materials) {
		for (BlockFace blockFace : DIRECT_HORIZONTAL_BLOCK_FACES) {
			Block neighbour = block.getRelative(blockFace);
			if (materials.contains(neighbour.getType()))
				return neighbour;
		}
		Block upperBlock = block.getRelative(BlockFace.UP);
		if (materials.contains(upperBlock.getType()))
			return upperBlock;
		for (BlockFace blockFace : DIRECT_HORIZONTAL_BLOCK_FACES) {
			Block neighbour = upperBlock.getRelative(blockFace);
			if (materials.contains(neighbour.getType()))
				return neighbour;
		}
		Block lowerBlock = block.getRelative(BlockFace.DOWN);
		if (materials.contains(lowerBlock.getType()))
			return lowerBlock;
		for (BlockFace blockFace : DIRECT_HORIZONTAL_BLOCK_FACES) {
			Block neighbour = lowerBlock.getRelative(blockFace);
			if (materials.contains(neighbour.getType()))
				return neighbour;
		}
		return null;
	}
}