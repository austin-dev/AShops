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

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class ShopSessionsManager<T extends ShopSession> {
	private final Map<Location, Map<String, T>> sessions = new HashMap<Location, Map<String, T>>();

	public synchronized void register(T session) {
		Map<String, T> map = sessions.get(session.getLocation());
		if (map != null)
			map.put(session.getPlayerName(), session);
		else {
			map = new HashMap<String, T>();
			map.put(session.getPlayerName(), session);
			sessions.put(session.getLocation(), map);
		}
	}

	public synchronized void unregister(Location location, String playerName) {
		Map<String, T> map = sessions.get(location);
		if (map != null) {
			map.remove(playerName);
			if (map.isEmpty())
				sessions.remove(location);
		}
	}

	public synchronized Map<String, T> get(Location location) {
		return sessions.get(location);
	}

	public synchronized T get(Location location, String playerName) {
		Map<String, T> map = sessions.get(location);
		return map != null ? map.get(playerName) : null;
	}

	public synchronized void remove(Location location) {
		Map<String, T> map = sessions.get(location);
		if (map != null) {
			for (T session : map.values()) {
				session.end();
				Player player = Bukkit.getPlayer(session.getPlayerName());
				if (player != null)
					player.closeInventory();
			}
			sessions.remove(location);
		}
	}

	public synchronized void endAll() {
		for (Map<String, T> entry : sessions.values())
			for (T session : entry.values()) {
				session.end();
				Player player = Bukkit.getPlayer(session.getPlayerName());
				if (player != null)
					player.closeInventory();
			}
		sessions.clear();
	}
}
