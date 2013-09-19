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

import net.milkbowl.vault.permission.Permission;

import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public class VaultPermissionsProvider extends PermissionsProvider {
	private final Permission permissions;

	public VaultPermissionsProvider(JavaPlugin plugin) {
		RegisteredServiceProvider<Permission> rsp = plugin.getServer()
				.getServicesManager().getRegistration(Permission.class);
		if (rsp != null) {
			permissions = rsp.getProvider();
			if (permissions == null) {
				throw new PluginSetupException(
						"Could not find any permissions plugin.");
			}
		} else {
			throw new PluginSetupException(
					"Could not find any permissions plugin.");
		}
	}

	@Override
	public String[] getGroups(Player player) {
		return permissions.getGroups();
	}

	@Override
	public String[] getGroups(String playerName, World world) {
		return permissions.getPlayerGroups(world, playerName);
	}

	@Override
	public boolean has(String playerName, String worldName,
			PermissionKey permission) {
		if (!permissions.has(playerName, worldName, permission.getPath())) {
			for (PermissionKey p : permission.getImplicating())
				if (has(playerName, worldName, p))
					return true;
			return false;
		} else {
			return true;
		}
	}
}
