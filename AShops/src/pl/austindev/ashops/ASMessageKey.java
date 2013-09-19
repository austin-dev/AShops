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

import static pl.austindev.mc.MessageKey.Level.FAILURE;
import static pl.austindev.mc.MessageKey.Level.INFO;
import static pl.austindev.mc.MessageKey.Level.NONE;
import static pl.austindev.mc.MessageKey.Level.SUCCESS;

import org.bukkit.ChatColor;

import pl.austindev.mc.MessageKey;

public enum ASMessageKey implements MessageKey {
	CMD_ASHOP(NONE),
	CMD_ASSHOP(NONE),
	NO_PERMISSION(FAILURE),
	NOT_PLAYER(FAILURE),
	SELECT_CHEST(INFO),
	SHOPS_LIMIT(FAILURE),
	ERROR(Level.ERROR),
	INVALID_PLAYER(FAILURE),
	ACTION_ABORTED(FAILURE),
	FORBIDDEN_NEIGHBOUR(FAILURE) /* (String) */,
	NOT_SHOP(FAILURE),
	ALREADY_SHOP(FAILURE),
	NO_MONEY(FAILURE),
	SHOP_CREATED(SUCCESS),
	CMD_ABUY(NONE),
	CMD_ASELL(NONE),
	MINIMAL_PRICE(FAILURE) /* (String) */,
	FORBIDDEN_TYPE(FAILURE),
	WRONG_ITEM(FAILURE),
	WRONG_AMOUNT(FAILURE),
	WRONG_PRICE(FAILURE),
	OFFER_ADDED(SUCCESS),
	NO_SLOTS_OR_EXISTS(FAILURE),
	NOT_OWNER(FAILURE),
	ILLEGAL_GAME_MODE(FAILURE),
	SHOP_CLOSED(FAILURE),
	NO_SHOP_MONEY_OR_PLAYER_ITEMS(FAILURE),
	NO_MONEY_OR_SHOP_ITEMS(FAILURE),
	NO_ITEMS_OR_SPACE(FAILURE),
	SELECT_ITEM(NONE),
	INSERT_PRICE(INFO),
	INSERT_MAX_AMOUNT(INFO),
	INSERT_ITEM_ID(INFO),
	CMD_AREMOVE(NONE),
	UNCOLLECTED_ITEMS(FAILURE),
	FAR_FROM_SHOP(FAILURE),
	SHOP_REMOVED(SUCCESS),
	REMOVED_OFFER(FAILURE),
	CMD_ATOGGLE(NONE),
	STATE_CHANGED(SUCCESS),
	SOLD_NOTIFICATION(NONE) /*
							 * ( Integer ) amount , ( String ) itemType , (
							 * String ) client
							 */,
	BOUGHT_NOTIFICATION(NONE) /*
							 * (Integer) amount, (String) itemType, (String)
							 * client
							 */,
	CONFIG_RELOADED(SUCCESS),
	CMD_ARELOAD(NONE),
	RIGHT_CLICK_EXPECTED(FAILURE),
	FORBIDDEN_BLOCK_PLACING(FAILURE) /* (String) */,
	REMOVE_SHOP(FAILURE),
	CMD_ASHOPS(NONE),
	MANAGER_ICON_SELL(NONE),
	MANAGER_ICON_SELL_CLICK(NONE),
	MANAGER_ICON_SELL_SHIFT(NONE),
	MANAGER_ICON_BUY(NONE),
	MANAGER_ICON_BUY_CLICK(NONE),
	MANAGER_ICON_BUY_SHIFT(NONE),
	MANAGER_ICON_REMOVE(NONE),
	MANAGER_ICON_REMOVE_CLICK(NONE),
	MANAGER_ICON_REMOVE_SHIFT(NONE),
	MANAGER_ICON_STATE(NONE),
	MANAGER_ICON_STATE_CLICK(NONE),
	MANAGER_MENU_TITLE(NONE),
	SHOP_INV_ICON_SELL(NONE),
	SHOP_INV_ICON_BUY(NONE),
	BUY_ICON_CLICK(NONE),
	BUY_ICON_SHIFT(NONE),
	BUY_MENU_TITLE(NONE),
	SELL_ICON_CLICK(NONE),
	SELL_ICON_SHIFT(NONE),
	SELL_MENU_TITLE(NONE),
	EDIT_LOAD_ICON(NONE),
	EDIT_COLLECT_ICON(NONE),
	EDIT_REMOVE_ICON(NONE),
	EDIT_LOAD_ICON_CLICK(NONE),
	EDIT_COLLECT_ICON_CLICK(NONE),
	EDIT_REMOVE_ICON_CLICK(NONE),
	COLLECT_ICON_CLICK(NONE),
	COLLECT_ICON_SHIFT(NONE),
	COLLECT_MENU_TITLE(NONE),
	LOAD_ICON_CLICK(NONE),
	LOAD_ICON_SHIFT(NONE),
	LOAD_MENU_TITLE(NONE),
	OFFER_EDIT_MENU_TITLE(NONE);
	private final String format;

	private ASMessageKey(Level level, ChatColor... format) {
		StringBuilder formatString = new StringBuilder(level.getColors());
		for (ChatColor color : format)
			formatString.append(color);
		this.format = formatString.toString();
	}

	@Override
	public String getKey() {
		return name();
	}

	@Override
	public String getFormat() {
		return format;
	}
}
