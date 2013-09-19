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

import java.util.Arrays;
import java.util.Locale;
import java.util.ResourceBundle;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class MessageTranslator {
	private final BukkitPlugin plugin;
	private final ResourceBundle resourceBundle;

	public MessageTranslator(BukkitPlugin plugin, String resourcePath,
			Locale locale) {
		this.plugin = plugin;
		resourceBundle = ResourceBundle.getBundle(resourcePath, locale);
	}

	public MessageTranslator(BukkitPlugin plugin, String resourcePath,
			Locale locale, boolean debug) {
		this.plugin = plugin;
		resourceBundle = debug ? null : ResourceBundle.getBundle(resourcePath,
			locale);
	}

	public BukkitPlugin getPlugin() {
		return plugin;
	}

	public String $(MessageKey key) {
		String message;
		if (resourceBundle != null) {
			message = resourceBundle.getString(key.getKey()).replace("&&",
				key.getFormat());
			message = ChatColor.translateAlternateColorCodes('&', message);
			message = String.format(Locale.ENGLISH, message);
		} else {
			message = "[" + key.getKey() + "]";
		}
		return key.getFormat() + message;
	}

	public String $(MessageKey key, Object argument) {
		String message;
		if (resourceBundle != null) {
			message = resourceBundle.getString(key.getKey()).replace("&&",
				key.getFormat());
			message = ChatColor.translateAlternateColorCodes('&', message);
			message = String.format(Locale.ENGLISH, message, argument);
		} else {
			message = "[" + key.getKey() + ": " + argument + "]";
		}
		return key.getFormat() + message;
	}

	public String $(MessageKey key, Object... arguments) {
		String message;
		if (resourceBundle != null) {
			message = resourceBundle.getString(key.getKey()).replace("&&",
				key.getFormat());
			message = ChatColor.translateAlternateColorCodes('&', message);
			message = String.format(Locale.ENGLISH, message, arguments);
		} else {
			message = "[" + key.getKey() + ": " + Arrays.asList(arguments)
					+ "]";
		}
		return key.getFormat() + message;
	}

	public void $(CommandSender sender, MessageKey key) {
		sender.sendMessage($(key));
	}

	public void $(CommandSender sender, MessageKey key, Object argument) {
		sender.sendMessage($(key, argument));
	}

	public void $(CommandSender sender, MessageKey key, Object... arguments) {
		sender.sendMessage($(key, arguments));
	}

	public void $(CommandSender sender, String prefix, MessageKey key) {
		sender.sendMessage(prefix + $(key));
	}

	public void $(CommandSender sender, String prefix, MessageKey key,
			Object argument) {
		sender.sendMessage(prefix + $(key, argument));
	}

	public void $(CommandSender sender, String prefix, MessageKey key,
			Object... arguments) {
		sender.sendMessage(prefix + $(key, arguments));
	}

	public void synch$(final CommandSender sender, final MessageKey key) {
		plugin.synch(new Runnable() {

			@Override
			public void run() {
				sender.sendMessage($(key));
			}
		});
	}

	public void synch$(final CommandSender sender, final MessageKey key,
			final Object argument) {
		plugin.synch(new Runnable() {
			@Override
			public void run() {
				sender.sendMessage($(key, argument));
			}
		});
	}

	public void synch$(final CommandSender sender, final MessageKey key,
			final Object... arguments) {
		plugin.synch(new Runnable() {

			@Override
			public void run() {
				sender.sendMessage($(key, arguments));
			}
		});
	}

	public void synch$(final CommandSender sender, final Exception e,
			final MessageKey key) {
		plugin.synch(new Runnable() {

			@Override
			public void run() {
				sender.sendMessage($(key));
				e.printStackTrace();
			}
		});
	}

	public void synch$(final CommandSender sender, final Exception e,
			final MessageKey key, final Object argument) {
		plugin.synch(new Runnable() {
			@Override
			public void run() {
				sender.sendMessage($(key, argument));
				e.printStackTrace();
			}
		});
	}

	public void synch$(final CommandSender sender, final Exception e,
			final MessageKey key, final Object... arguments) {
		plugin.synch(new Runnable() {

			@Override
			public void run() {
				sender.sendMessage($(key, arguments));
				e.printStackTrace();
			}
		});
	}

	public void synch$(final Exception e) {
		plugin.synch(new Runnable() {

			@Override
			public void run() {
				e.printStackTrace();
			}
		});
	}
}
