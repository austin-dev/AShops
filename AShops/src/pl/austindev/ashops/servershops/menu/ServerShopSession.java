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
package pl.austindev.ashops.servershops.menu;

import org.bukkit.Location;

import pl.austindev.ashops.ASConfiguration;
import pl.austindev.ashops.AShops;
import pl.austindev.ashops.ShopSession;
import pl.austindev.ashops.servershops.ServerShopsHandler;
import pl.austindev.mc.MessageTranslator;
import pl.austindev.mc.PermissionsProvider;
import pl.austindev.mc.TemporaryValuesContainer;

public class ServerShopSession extends ShopSession {
	private final ServerShopsHandler handler;
	private volatile ServerShopMenu menu;
	private volatile ServerShopInput input;

	public ServerShopSession(Location location, ServerShopsHandler handler,
			String playerName) {
		super(playerName, location);
		this.handler = handler;
	}

	public ServerShopsHandler getHandler() {
		return handler;
	}

	public synchronized ServerShopMenu getMenu() {
		return menu;
	}

	public ServerShopInput getInput() {
		return input;
	}

	public synchronized void setMenu(ServerShopMenu menu) {
		this.input = null;
		this.menu = menu;
	}

	public void setInput(ServerShopInput input) {
		this.menu = null;
		this.input = input;
	}

	public AShops getPlugin() {
		return handler.getPlugin();
	}

	public PermissionsProvider getPermissionsProvider() {
		return handler.getPlugin().getPermissionsProvider();
	}

	public MessageTranslator getTranslator() {
		return handler.getTranslator();
	}

	public ASConfiguration getConfiguration() {
		return handler.getPlugin().getConfiguration();
	}

	public TemporaryValuesContainer getTemporaryValues() {
		return handler.getPlugin().getTemporaryValues();
	}

	public void end() {
		handler.closeSession(getLocation(), getPlayerName());
	}

}
