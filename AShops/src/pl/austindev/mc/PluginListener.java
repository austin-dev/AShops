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

import org.bukkit.event.Listener;

public abstract class PluginListener<T extends BukkitPlugin> implements
		Listener {
	private final T plugin;

	protected PluginListener(T plugin) {
		this.plugin = plugin;
	}

	public T getPlugin() {
		return plugin;
	}

	public MessageTranslator getTranslator() {
		return plugin.getTranslator();
	}

	public PermissionsProvider getPermissionsProvider() {
		return plugin.getPermissionsProvider();
	}

	public void synch(Runnable task) {
		getPlugin().synch(task);
	}

	public void asynch(Runnable task) {
		getPlugin().asynch(task);
	}

}
