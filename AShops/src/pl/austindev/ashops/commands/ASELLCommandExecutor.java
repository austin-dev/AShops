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
package pl.austindev.ashops.commands;

import java.math.BigDecimal;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import pl.austindev.ashops.ASMessageKey;
import pl.austindev.ashops.ASPermissionKey;
import pl.austindev.ashops.AShops;
import pl.austindev.ashops.Offer;
import pl.austindev.ashops.OffersUtils;

public class ASELLCommandExecutor extends ASCommandExecutor {
	private ASELLCommandExecutor(AShops plugin) {
		super(plugin, ASCommand.ASELL);
	}

	public static void register(AShops plugin) {
		ASELLCommandExecutor executor = new ASELLCommandExecutor(plugin);
		plugin.getCommand(executor.getPluginCommand().toString()).setExecutor(
			executor);
	}

	@Override
	protected void run(CommandSender sender, Command command, String label,
			List<String> arguments) {
		Player player = (Player) sender;
		BigDecimal price = OffersUtils.toPrice(arguments.get(0));
		if (price.compareTo(BigDecimal.ZERO) > 0
				&& price.precision() <= Offer.PRICE_PRECISION) {
			ItemStack item;
			if (arguments.size() > 1) {
				item = OffersUtils.toItem(arguments.get(1));
			} else {
				item = new ItemStack(player.getItemInHand());
				item.setAmount(1);
			}
			if (item != null) {
				if (!getPlugin().getConfiguration().getForbiddenItems()
						.contains(item.getType())
						|| getPermissionsProvider().has(player,
							ASPermissionKey.TRADE_ANY_ITEM)) {
					BigDecimal minimalPrice = getPlugin().getConfiguration()
							.getMinimalPrice(item.getType());
					if (minimalPrice.compareTo(price) <= 0
							|| getPermissionsProvider().has(player,
								ASPermissionKey.ALLOW_ANY_PRICE)) {
						getPlugin().getTemporaryValues().put(player.getName(),
							getPluginCommand(),
							new SellOfferAddProcedureValues(price, item));
						getTranslator().$(player, ASMessageKey.SELECT_CHEST);
					} else {
						getTranslator().$(player, ASMessageKey.MINIMAL_PRICE,
							OffersUtils.getFormatedPrice(minimalPrice));
					}
				} else {
					getTranslator().$(player, ASMessageKey.FORBIDDEN_TYPE);
				}
			} else {
				getTranslator().$(player, ASMessageKey.WRONG_ITEM);
			}
		} else {
			getTranslator().$(player, ASMessageKey.WRONG_PRICE);
		}
	}

	public class SellOfferAddProcedureValues {
		private final BigDecimal price;
		private final ItemStack item;

		public SellOfferAddProcedureValues(BigDecimal price, ItemStack item) {
			this.price = price;
			this.item = item;
		}

		public BigDecimal getPrice() {
			return price;
		}

		public ItemStack getItem() {
			return item;
		}

	}
}
