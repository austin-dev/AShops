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

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import java.util.concurrent.atomic.AtomicInteger;

import org.bukkit.Location;

import pl.austindev.mc.DBUtils;

import com.avaje.ebean.EbeanServer;

public abstract class ShopsManager<T extends Shop> {
	private final AShops plugin;
	private final Map<Location, FutureShop> loadedShops = new HashMap<Location, FutureShop>();
	private final ExecutorService executor = Executors
			.newSingleThreadExecutor();

	public synchronized FutureShop get(final Location location) {
		FutureShop shopTask = loadedShops.get(location);
		if (shopTask == null) {
			shopTask = new FutureShop(location);
			executor.submit(shopTask);
			loadedShops.put(location, shopTask);
		}
		shopTask.incrementCounter();
		return shopTask;
	}

	public synchronized FutureShop getIfLoaded(final Location location) {
		return loadedShops.get(location);
	}

	public synchronized FutureShop release(Location location) {
		FutureShop task = loadedShops.get(location);
		if (task != null) {
			int counter = task.decrementCounter();
			if (counter < 1)
				loadedShops.remove(location);
		}
		return task;
	}

	public synchronized void remove(Location location) {
		loadedShops.remove(location);
	}

	public ShopsManager(AShops plugin) {
		this.plugin = plugin;
	}

	public AShops getPlugin() {
		return plugin;
	}

	public EbeanServer getDB() {
		return plugin.getDatabase();
	}

	protected abstract Class<T> getShopClass();

	public class FutureShop extends FutureTask<T> {
		private final AtomicInteger counter = new AtomicInteger(0);
		private final Location location;

		public FutureShop(final Location location) {
			super(new Callable<T>() {
				@Override
				public T call() {
					T shop = getDB().find(getShopClass())
							.where(DBUtils.getLocationCondition(location))
							.setMaxRows(1).findUnique();
					shop.mapOffers();
					return shop;
				}
			});
			this.location = location;
		}

		public T getShop() throws ShopDataException {
			try {
				T shop = super.get();
				if (shop != null)
					return shop;
				else
					throw new ShopDataException(
							"Could not find data in the database for a shop. Shop type: "
									+ getShopClass().getSimpleName()
									+ " in location: " + location + ".",
							new NullPointerException());
			} catch (InterruptedException e) {
				throw new ShopDataException(
						"Could not obtain shops data from the databse. Shop type: "
								+ getShopClass().getSimpleName()
								+ " in location: " + location + ".", e);
			} catch (ExecutionException e) {
				throw new ShopDataException(
						"Could not obtain shops data from the databse. Shop type: "
								+ getShopClass().getSimpleName()
								+ " in location: " + location + ".", e);
			}
		}

		public int incrementCounter() {
			return counter.incrementAndGet();
		}

		public int decrementCounter() {
			return counter.decrementAndGet();
		}
	}
}
