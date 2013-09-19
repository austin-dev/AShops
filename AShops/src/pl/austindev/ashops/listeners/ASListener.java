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
package pl.austindev.ashops.listeners;

import pl.austindev.ashops.ASConfiguration;
import pl.austindev.ashops.AShops;
import pl.austindev.ashops.ShopType;
import pl.austindev.ashops.ShopsHandler;
import pl.austindev.mc.PluginListener;
import pl.austindev.mc.TemporaryValuesContainer;

public class ASListener extends PluginListener<AShops> {

	protected ASListener(AShops plugin) {
		super(plugin);
	}

	public TemporaryValuesContainer getTemporaryValues() {
		return getPlugin().getTemporaryValues();
	}

	public ASConfiguration getConfiguration() {
		return getPlugin().getConfiguration();
	}

	public ShopsHandler getShopsHandler(ShopType shopType) {
		return getPlugin().getShopsHandler(shopType);
	}
}
