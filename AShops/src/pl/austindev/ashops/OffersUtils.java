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

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

public class OffersUtils {

	private OffersUtils() {
	}

	public static BigDecimal toPrice(String string) {
		try {
			return new BigDecimal(string).setScale(Offer.PRICE_SCALE,
					RoundingMode.HALF_EVEN);
		} catch (NumberFormatException e) {
			return BigDecimal.ZERO;
		}
	}

	public static ItemStack toItem(String string) {
		String[] itemCode = string.split(":");
		if (itemCode.length > 0) {
			Material type = Material.getMaterial(itemCode[0].toUpperCase());
			if (type != null) {
				if (itemCode.length > 1) {
					short data = toData(itemCode[1]);
					if (data >= 0) {
						if (itemCode.length > 2) {
							Map<Enchantment, Integer> enchants = toEnchantments(itemCode[2]);
							if (enchants != null)
								return getItem(type, data, enchants);
						} else {
							return getItem(type, data, null);
						}
					}
				} else {
					return getItem(type, (short) 0, null);
				}
			}
		}
		return null;
	}

	private static ItemStack getItem(Material type, short data,
		Map<Enchantment, Integer> enchantments) {
		ItemStack item = new ItemStack(type, 1, data);
		if (enchantments != null) {
			for (Entry<Enchantment, Integer> e : enchantments.entrySet())
				if (e.getKey().canEnchantItem(item))
					item.addEnchantment(e.getKey(), e.getValue());
				else
					return null;
		}
		return item;
	}

	private static Map<Enchantment, Integer> toEnchantments(String string) {
		Map<Enchantment, Integer> enchants = new HashMap<Enchantment, Integer>();
		for (String enchantCode : string.split(",")) {
			String[] enchant = enchantCode.split("-");
			if (enchant.length == 2) {
				Enchantment ench = Enchantment.getByName(enchant[0]
						.toUpperCase());
				if (ench != null) {
					int level = toEnchantLevel(enchant[1]);
					if (level > 0) {
						if (level <= ench.getMaxLevel()) {
							enchants.put(ench, level);
						} else
							return null;
					} else
						return null;
				} else
					return null;
			} else
				return null;
		}
		return enchants;
	}

	private static int toEnchantLevel(String string) {
		return string.matches("\\d+") ? Integer.parseInt(string) : -1;
	}

	private static short toData(String string) {
		try {
			return Short.parseShort(string);
		} catch (NumberFormatException e) {
			return -1;
		}
	}

	public static int toAmount(String string) {
		try {
			return Integer.parseInt(string);
		} catch (NumberFormatException e) {
			return -1;
		}
	}

	public static void resetDurability(ItemStack item) {
		if (DURABLE_ITEMS.contains(item.getType()))
			item.setDurability((short) 0);
	}

	public static String getFormatedPrice(BigDecimal price) {
		String formatedPrice = PRICE_FORMATTER.format(price);
		if (currency != null && currency.length() > 0)
			formatedPrice += " (" + currency + ")";
		return formatedPrice;
	}

	public static void setCurrency(String currency) {
		OffersUtils.currency = currency;
	}

	private static final Set<Material> DURABLE_ITEMS = EnumSet.of(
			Material.FISHING_ROD, Material.LEATHER_BOOTS,
			Material.LEATHER_CHESTPLATE, Material.LEATHER_HELMET,
			Material.LEATHER_LEGGINGS, Material.CHAINMAIL_BOOTS,
			Material.CHAINMAIL_CHESTPLATE, Material.CHAINMAIL_HELMET,
			Material.CHAINMAIL_LEGGINGS, Material.IRON_HELMET,
			Material.IRON_CHESTPLATE, Material.IRON_LEGGINGS,
			Material.IRON_BOOTS, Material.DIAMOND_HELMET,
			Material.DIAMOND_CHESTPLATE, Material.DIAMOND_LEGGINGS,
			Material.DIAMOND_BOOTS, Material.GOLD_HELMET,
			Material.GOLD_CHESTPLATE, Material.GOLD_LEGGINGS,
			Material.GOLD_BOOTS, Material.IRON_AXE, Material.IRON_HOE,
			Material.IRON_PICKAXE, Material.IRON_SPADE, Material.BOW,
			Material.IRON_SWORD, Material.WOOD_SWORD, Material.WOOD_SPADE,
			Material.WOOD_PICKAXE, Material.WOOD_AXE, Material.WOOD_HOE,
			Material.DIAMOND_SWORD, Material.DIAMOND_SPADE,
			Material.DIAMOND_PICKAXE, Material.DIAMOND_AXE,
			Material.DIAMOND_HOE, Material.GOLD_SWORD, Material.GOLD_SPADE,
			Material.GOLD_PICKAXE, Material.GOLD_AXE, Material.GOLD_HOE,
			Material.STONE_HOE, Material.STONE_AXE, Material.STONE_PICKAXE,
			Material.STONE_SPADE, Material.STONE_SWORD, Material.CARROT_STICK);

	private static final NumberFormat PRICE_FORMATTER = NumberFormat
			.getNumberInstance(Locale.ENGLISH);
	static {
		PRICE_FORMATTER.setMaximumFractionDigits(2);
		PRICE_FORMATTER.setMinimumFractionDigits(2);
		PRICE_FORMATTER.setRoundingMode(RoundingMode.HALF_EVEN);
	}

	private volatile static String currency;
}
