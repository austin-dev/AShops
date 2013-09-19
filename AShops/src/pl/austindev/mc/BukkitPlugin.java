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

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public abstract class BukkitPlugin extends JavaPlugin {
	private volatile MessageTranslator translator;
	private volatile PermissionsProvider permissionsProvider;
	private volatile EconomyProvider economyProvider;

	public MessageTranslator getTranslator() {
		return translator;
	}

	public void setTranslator(MessageTranslator translator) {
		this.translator = translator;
	}

	public PermissionsProvider getPermissionsProvider() {
		return permissionsProvider;
	}

	public void setPermissionsProvider(PermissionsProvider permissionsProvider) {
		this.permissionsProvider = permissionsProvider;
	}

	public EconomyProvider getEconomyProvider() {
		return economyProvider;
	}

	public void setEconomyProvider(EconomyProvider economyProvider) {
		this.economyProvider = economyProvider;
	}

	public void synch(Runnable task) {
		Bukkit.getScheduler().runTask(this, task);
	}

	public void asynch(Runnable task) {
		Bukkit.getScheduler().runTaskAsynchronously(this, task);
	}
}
