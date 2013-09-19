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

import pl.austindev.ashops.ASConfiguration;
import pl.austindev.ashops.ASMessageKey;
import pl.austindev.ashops.AShops;
import pl.austindev.mc.PluginCommand;
import pl.austindev.mc.PluginCommandExecutor;
import pl.austindev.mc.TemporaryValuesContainer;

public abstract class ASCommandExecutor extends PluginCommandExecutor<AShops> {

	public ASCommandExecutor(AShops plugin, PluginCommand pluginCommand) {
		super(plugin, pluginCommand, ASMessageKey.NO_PERMISSION,
				ASMessageKey.NOT_PLAYER);
	}

	public TemporaryValuesContainer getTemporaryValues() {
		return getPlugin().getTemporaryValues();
	}

	public ASConfiguration getConfiguration() {
		return getPlugin().getConfiguration();
	}

}
