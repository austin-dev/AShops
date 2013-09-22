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

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import javax.persistence.PersistenceException;

import pl.austindev.ashops.commands.ABUYCommandExecutor;
import pl.austindev.ashops.commands.ARELOADCommandExecutor;
import pl.austindev.ashops.commands.AREMOVECommandExecutor;
import pl.austindev.ashops.commands.ASELLCommandExecutor;
import pl.austindev.ashops.commands.ASHOPCommandExecutor;
import pl.austindev.ashops.commands.ASHOPSCommandExecutor;
import pl.austindev.ashops.commands.ASSHOPCommandExecutor;
import pl.austindev.ashops.commands.ATOGGLECommandExecutor;
import pl.austindev.ashops.listeners.ASBlockListener;
import pl.austindev.ashops.listeners.ASInventoryListener;
import pl.austindev.ashops.listeners.ASPlayerListener;
import pl.austindev.ashops.playershops.PlayerBuyOffer;
import pl.austindev.ashops.playershops.PlayerSellOffer;
import pl.austindev.ashops.playershops.PlayerShop;
import pl.austindev.ashops.playershops.PlayerShopItem;
import pl.austindev.ashops.playershops.PlayerShopOffer;
import pl.austindev.ashops.playershops.PlayerShopTransaction;
import pl.austindev.ashops.playershops.PlayerShopsHandler;
import pl.austindev.ashops.playershops.PlayerShopsManager;
import pl.austindev.ashops.servershops.ServerBuyOffer;
import pl.austindev.ashops.servershops.ServerSellOffer;
import pl.austindev.ashops.servershops.ServerShop;
import pl.austindev.ashops.servershops.ServerShopItem;
import pl.austindev.ashops.servershops.ServerShopOffer;
import pl.austindev.ashops.servershops.ServerShopsHandler;
import pl.austindev.ashops.servershops.ServerShopsManager;
import pl.austindev.mc.BukkitPlugin;
import pl.austindev.mc.MessageTranslator;
import pl.austindev.mc.TemporaryValuesContainer;
import pl.austindev.mc.VaultEconomyProvider;
import pl.austindev.mc.VaultPermissionsProvider;

public class AShops extends BukkitPlugin {
	private volatile TemporaryValuesContainer temporaryValues;
	private volatile ASConfiguration configuration;
	private volatile ASEconomy economy;

	private volatile PlayerShopsManager playerShopsManager;
	private volatile ServerShopsManager serverShopsManager;

	private volatile PlayerShopsHandler playerShopsHandler;
	private volatile ServerShopsHandler serverShopsHandler;

	@Override
	public void onEnable() {
		prepareTables();
		this.temporaryValues = new TemporaryValuesContainer();
		this.configuration = new ASConfiguration(this);
		setPermissionsProvider(new VaultPermissionsProvider(this));
		setTranslator(new MessageTranslator(this,
				"pl.austindev.ashops.lang/ashops", getConfiguration()
						.getLanguage(), false));
		setEconomyProvider(new VaultEconomyProvider(this));
		this.economy = new ASEconomy(this);
		this.playerShopsManager = new PlayerShopsManager(this);
		this.serverShopsManager = new ServerShopsManager(this);
		this.playerShopsHandler = new PlayerShopsHandler(this);
		this.serverShopsHandler = new ServerShopsHandler(this);
		PlayerShop.MAX_CAPACITY = getConfiguration().getCapacity();
		registerExecutors();
		registerListeners();
	}

	@Override
	public void onDisable() {
		for (ShopType shopType : ShopType.values()) {
			getShopsHandler(shopType).handlePluginDisable();
		}
	}

	public TemporaryValuesContainer getTemporaryValues() {
		return temporaryValues;
	}

	public ASConfiguration getConfiguration() {
		return configuration;
	}

	public ASEconomy getEconomy() {
		return economy;
	}

	public PlayerShopsManager getPlayerShopsManager() {
		return playerShopsManager;
	}

	public ServerShopsManager getServerShopsManager() {
		return serverShopsManager;
	}

	public ShopsManager<? extends Shop> getShopsManager(ShopType shopType) {
		return shopType.equals(ShopType.PLAYER_SHOP) ? playerShopsManager
				: serverShopsManager;
	}

	public PlayerShopsHandler getPlayerShopsHandler() {
		return playerShopsHandler;
	}

	public ServerShopsHandler getServerShopsHandler() {
		return serverShopsHandler;
	}

	public ShopsHandler getShopsHandler(ShopType shopType) {
		return shopType.equals(ShopType.PLAYER_SHOP) ? playerShopsHandler
				: serverShopsHandler;
	}

	@Override
	public List<Class<?>> getDatabaseClasses() {
		return tableClasses;
	}

	private void registerExecutors() {
		ASHOPCommandExecutor.register(this);
		ASSHOPCommandExecutor.register(this);
		ABUYCommandExecutor.register(this);
		ASELLCommandExecutor.register(this);
		AREMOVECommandExecutor.register(this);
		ATOGGLECommandExecutor.register(this);
		ARELOADCommandExecutor.register(this);
		ASHOPSCommandExecutor.register(this);
	}

	private void registerListeners() {
		ASPlayerListener.register(this);
		ASBlockListener.register(this);
		ASInventoryListener.register(this);
	}

	private void prepareTables() {
		try {
			for (Class<?> tableClass : tableClasses)
				getDatabase().find(tableClass).findRowCount();
		} catch (PersistenceException e) {
			installDDL();
		}
	}

	private final static List<Class<?>> tableClasses = new LinkedList<Class<?>>();
	static {
		Collections.<Class<?>> addAll(tableClasses, PlayerShop.class,
				ServerShop.class, PlayerShopItem.class, ServerShopItem.class,
				PlayerShopOffer.class, ServerShopOffer.class,
				PlayerSellOffer.class, PlayerBuyOffer.class,
				ServerSellOffer.class, ServerBuyOffer.class,
				PlayerShopTransaction.class);
	}

}
