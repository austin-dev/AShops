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

import java.util.List;
import java.util.Locale;

public class PluginConfiguration {
	private final BukkitPlugin plugin;

	public PluginConfiguration(BukkitPlugin plugin) {
		this.plugin = plugin;
		plugin.saveDefaultConfig();
	}

	public BukkitPlugin getPlugin() {
		return plugin;
	}

	public String getString(ConfigurationPath path) {
		return plugin.getConfig().getString(getPath(path));
	}

	public String getString(ConfigurationPath path, Object argument) {
		return plugin.getConfig().getString(getPath(path, argument));
	}

	public String getString(ConfigurationPath path, Object... arguments) {
		return plugin.getConfig().getString(getPath(path, arguments));
	}

	public int getInt(ConfigurationPath path) {
		return plugin.getConfig().getInt(getPath(path));
	}

	public int getInt(ConfigurationPath path, Object argument) {
		return plugin.getConfig().getInt(getPath(path, argument));
	}

	public int getInt(ConfigurationPath path, Object... arguments) {
		return plugin.getConfig().getInt(getPath(path, arguments));
	}

	public double getDouble(ConfigurationPath path) {
		return plugin.getConfig().getDouble(getPath(path));
	}

	public double getDouble(ConfigurationPath path, Object argument) {
		return plugin.getConfig().getDouble(getPath(path, argument));
	}

	public double getDouble(ConfigurationPath path, Object... arguments) {
		return plugin.getConfig().getDouble(getPath(path, arguments));
	}

	public boolean getBoolean(ConfigurationPath path) {
		return plugin.getConfig().getBoolean(getPath(path));
	}

	public boolean getBoolean(ConfigurationPath path, Object argument) {
		return plugin.getConfig().getBoolean(getPath(path, argument));
	}

	public boolean getBoolean(ConfigurationPath path, Object... arguments) {
		return plugin.getConfig().getBoolean(getPath(path, arguments));
	}

	public List<Integer> getIntegerList(ConfigurationPath path) {
		return plugin.getConfig().getIntegerList(getPath(path));
	}

	public List<Integer> getIntegerList(ConfigurationPath path, Object argument) {
		return plugin.getConfig().getIntegerList(getPath(path, argument));
	}

	public List<Integer> getIntegerList(ConfigurationPath path,
			Object... arguments) {
		return plugin.getConfig().getIntegerList(getPath(path, arguments));
	}

	public List<String> getStringList(ConfigurationPath path) {
		return plugin.getConfig().getStringList(getPath(path));
	}

	public List<String> getStringList(ConfigurationPath path, Object argument) {
		return plugin.getConfig().getStringList(getPath(path, argument));
	}

	public List<String> getStringList(ConfigurationPath path,
			Object... arguments) {
		return plugin.getConfig().getStringList(getPath(path, arguments));
	}

	private String getPath(ConfigurationPath path) {
		return String.format(Locale.ENGLISH, path.getPath());
	}

	private String getPath(ConfigurationPath path, Object argument) {
		return String.format(Locale.ENGLISH, path.getPath(), argument);
	}

	private String getPath(ConfigurationPath path, Object... arguments) {
		return String.format(Locale.ENGLISH, path.getPath(), arguments);
	}
}
