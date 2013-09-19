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
package pl.austindev.ashops.servershops;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

import pl.austindev.mc.CancellableEvent;

public class ServerShopCreateEvent extends CancellableEvent {
	private static final HandlerList handlers = new HandlerList();
	private final Player player;
	private final Location location;

	public ServerShopCreateEvent(Player player, Location location) {
		this.player = player;
		this.location = location;
	}

	public static ServerShopCreateEvent trigger(Player player, Location location) {
		ServerShopCreateEvent event = new ServerShopCreateEvent(player,
				location);
		Bukkit.getServer().getPluginManager().callEvent(event);
		return event;
	}

	public Player getPlayer() {
		return player;
	}

	public Location getLocation() {
		return location;
	}

	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}
}
