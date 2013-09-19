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

import pl.austindev.ashops.ASMessageKey;
import pl.austindev.ashops.ASPermissionKey;
import pl.austindev.ashops.AShops;
import pl.austindev.ashops.ShopDataException;
import pl.austindev.mc.PlayerUtils;

public class ASHOPCommandExecutor extends ASCommandExecutor {
	private ASHOPCommandExecutor(AShops plugin) {
		super(plugin, ASCommand.ASHOP);
	}

	public static void register(AShops plugin) {
		ASHOPCommandExecutor executor = new ASHOPCommandExecutor(plugin);
		plugin.getCommand(executor.getPluginCommand().toString()).setExecutor(
			executor);
	}

	@Override
	protected void run(CommandSender sender, Command command, String label,
			List<String> arguments) {
		Player player = (Player) sender;
		if (arguments.size() == 0)
			tryCreateOwnShop(player, player.getName());
		else
			tryCreateShopForOtherPlayer(player, arguments.get(0));
	}

	private void tryCreateOwnShop(final Player player, final String playerName) {
		final BigDecimal shopPrice = getPermissionsProvider().has(player,
			ASPermissionKey.FREE_SHOP) ? BigDecimal.ZERO : getConfiguration()
				.getPrice(player);
		if (!getPermissionsProvider().has(player,
			ASPermissionKey.UNLIMITED_SHOPS)) {
			final int shopsLimit = getConfiguration().getShopsLimit(player);
			asynch(new Runnable() {

				@Override
				public void run() {
					try {
						int shopsNumber = getPlugin().getPlayerShopsManager()
								.getShopsNumber(playerName);
						if (shopsNumber < shopsLimit) {
							getTemporaryValues().put(playerName,
								getPluginCommand(),
								new ASHOPCommandValues(playerName, shopPrice));
							getTranslator().synch$(player,
								ASMessageKey.SELECT_CHEST);
						} else {
							getTranslator().synch$(player,
								ASMessageKey.SHOPS_LIMIT);
						}
					} catch (ShopDataException e) {
						getTranslator().synch$(player, e, ASMessageKey.ERROR);
					}
				}
			});
		} else {
			getTemporaryValues().put(playerName, getPluginCommand(),
				new ASHOPCommandValues(playerName, shopPrice));
			getTranslator().$(player, ASMessageKey.SELECT_CHEST);
		}
	}

	private void tryCreateShopForOtherPlayer(Player player, String ownerName) {
		if (getPermissionsProvider().has(player,
			ASPermissionKey.MANAGER)) {
			if (PlayerUtils.isValidPlayerName(ownerName)) {
				getPlugin().getTemporaryValues().put(player.getName(),
					getPluginCommand(),
					new ASHOPCommandValues(ownerName, BigDecimal.ZERO));
				getTranslator().$(player, ASMessageKey.SELECT_CHEST);
			} else {
				getTranslator().$(player, ASMessageKey.INVALID_PLAYER);
			}
		} else {
			getTranslator().$(player, ASMessageKey.NO_PERMISSION);
		}
	}

	public class ASHOPCommandValues {
		private final String ownerName;
		private final BigDecimal price;

		ASHOPCommandValues(String ownerName, BigDecimal price) {
			this.ownerName = ownerName;
			this.price = price;
		}

		public String getOwnerName() {
			return ownerName;
		}

		public BigDecimal getPrice() {
			return price;
		}

	}

}
