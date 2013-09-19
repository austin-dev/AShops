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

import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public abstract class PermissionsProvider {
	public boolean has(CommandSender player, PermissionKey permission) {
		if (!player.hasPermission(permission.getPath())) {
			for (PermissionKey p : permission.getImplicating())
				if (has(player, p))
					return true;
			return false;
		} else {
			return true;
		}
	}

	public boolean hasOneOf(CommandSender player, PermissionKey... permissions) {
		for (PermissionKey permission : permissions)
			if (has(player, permission))
				return true;
		return permissions.length == 0;
	}

	public boolean hasOneOf(CommandSender player, PermissionKey permission1,
			PermissionKey permission2) {
		return has(player, permission1) || has(player, permission2);
	}

	public boolean hasAll(CommandSender player, PermissionKey... permissions) {
		for (PermissionKey permission : permissions)
			if (!has(player, permission))
				return false;
		return true;
	}

	public boolean hasAll(CommandSender player, PermissionKey permission1,
			PermissionKey permission2) {
		return has(player, permission1) && has(player, permission2);
	}

	public abstract String[] getGroups(Player player);

	public abstract String[] getGroups(String playerName, World world);

	public abstract boolean has(String playerName, String worldName,
			PermissionKey permission);
}
