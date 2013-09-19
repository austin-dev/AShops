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

import java.util.LinkedList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class InventoryUtils {
	private InventoryUtils() {
	}

	public static boolean isEmpty(Inventory inventory) {
		return inventory.firstEmpty() < 0;
	}

	@SuppressWarnings("deprecation")
	public static int add(Inventory inventory, ItemStack item,
			int requestedAmount) {
		int leftAmount = requestedAmount;
		int firstEmpty = inventory.firstEmpty();
		int first = firstSimilar(inventory, item);
		if (firstEmpty > -1)
			first = first > -1 ? Math.min(first, firstEmpty) : firstEmpty;
		if (first > -1) {
			for (int i = first; i < inventory.getSize() && leftAmount > 0; i++) {
				ItemStack itemInSlot = inventory.getItem(i);
				int amountToAdd = 0;
				if (itemInSlot == null || itemInSlot.getTypeId() < 1) {
					amountToAdd = Math.min(item.getMaxStackSize(), leftAmount);
					setItem(inventory, i, item, amountToAdd);
				} else if (itemInSlot.isSimilar(item)) {
					amountToAdd = Math.min(
							item.getMaxStackSize() - itemInSlot.getAmount(),
							leftAmount);
					if (amountToAdd > 0) {
						setItem(inventory, i, item, itemInSlot.getAmount()
								+ amountToAdd);
					}
				}
				leftAmount -= amountToAdd;
			}
		}
		if (inventory.getType().equals(InventoryType.PLAYER))
			((Player) inventory.getHolder()).updateInventory();
		return requestedAmount - leftAmount;
	}

	public static void setItem(Inventory inventory, int slot, ItemStack item,
			int amount) {
		ItemStack itemStack = new ItemStack(item);
		itemStack.setAmount(amount);
		inventory.setItem(slot, itemStack);
	}

	public static void appendLore(ItemStack item, String... lines) {
		ItemMeta meta = item.getItemMeta();
		if (meta == null)
			meta = Bukkit.getItemFactory().getItemMeta(item.getType());
		List<String> lore = meta.getLore();
		if (lore == null)
			lore = new LinkedList<String>();
		for (String line : lines)
			lore.add(line);
		meta.setLore(lore);
		item.setItemMeta(meta);
	}

	public static void setItemName(ItemStack item, String name) {
		ItemMeta meta = item.getItemMeta();
		if (meta == null)
			meta = Bukkit.getItemFactory().getItemMeta(item.getType());
		meta.setDisplayName(name);
		item.setItemMeta(meta);
	}

	public static int firstSimilar(Inventory inventory, ItemStack item) {
		for (int i = 0; i < inventory.getSize(); i++)
			if (item.isSimilar(inventory.getItem(i)))
				return i;
		return -1;
	}

	@SuppressWarnings("deprecation")
	public static void updateInventory(Inventory inventory) {
		for (HumanEntity entity : inventory.getViewers())
			if (PlayerUtils.isPlayer(entity))
				((Player) entity).updateInventory();
	}

	@SuppressWarnings("deprecation")
	public static int remove(Inventory inventory, ItemStack item,
			int requestedAmount) {
		int leftAmount = requestedAmount;
		for (int i = firstSimilar(inventory, item); i > -1 && leftAmount > 0; i = firstSimilar(
				inventory, item)) {
			ItemStack itemInSlot = inventory.getItem(i);
			int amountToRemove = Math.min(itemInSlot.getAmount(), leftAmount);
			itemInSlot.setAmount(itemInSlot.getAmount() - amountToRemove);
			inventory
					.setItem(i, itemInSlot.getAmount() > 0 ? itemInSlot : null);
			leftAmount -= amountToRemove;
		}
		if (inventory.getType().equals(InventoryType.PLAYER))
			((Player) inventory.getHolder()).updateInventory();
		return requestedAmount - leftAmount;
	}

	public static int getAmount(Inventory inventory, ItemStack item) {
		int amount = 0;
		ItemStack[] contents = inventory.getContents();
		for (ItemStack i : contents)
			if (i != null && i.isSimilar(item))
				amount += i.getAmount();
		return amount;
	}

	public static int getSpace(Inventory inventory, ItemStack offeredItemStack) {
		int amount = 0;
		ItemStack[] contents = inventory.getContents();
		int maxStackSize = offeredItemStack.getMaxStackSize();
		for (ItemStack i : contents)
			if (i == null || i.getType().equals(Material.AIR))
				amount += maxStackSize;
			else if (i.isSimilar(offeredItemStack)
					&& i.getAmount() <= maxStackSize)
				amount += maxStackSize - i.getAmount();
		return amount;
	}
}
