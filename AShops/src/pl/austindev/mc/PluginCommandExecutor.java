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
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public abstract class PluginCommandExecutor<T extends BukkitPlugin> implements
		CommandExecutor {
	private final T plugin;
	private final PluginCommand pluginCommand;
	private final MessageKey noPermissionMessage;
	private final MessageKey notPlayerMessage;

	public PluginCommandExecutor(T plugin, PluginCommand pluginCommand,
			MessageKey noPermissionMessage, MessageKey notPlayerMessage) {
		this.plugin = plugin;
		this.pluginCommand = pluginCommand;
		this.noPermissionMessage = noPermissionMessage;
		this.notPlayerMessage = notPlayerMessage;
	}

	@Override
	public final boolean onCommand(CommandSender sender, Command command,
			String label, String[] arguments) {
		if (PlayerUtils.isPlayer(sender) || pluginCommand.isConsoleCallable())
			if (plugin.getPermissionsProvider().hasAll(sender,
				pluginCommand.getPermission()))
				if (arguments.length >= pluginCommand.getMinArgumentsNumber())
					if (arguments.length <= pluginCommand
							.getMaxArgumentsNumber())
						run(sender, command, label, Arrays.asList(arguments));
					else
						getTranslator().$(sender, ChatColor.RED.toString(),
							pluginCommand.getDescription());
				else
					getTranslator().$(sender, ChatColor.RED.toString(),
						pluginCommand.getDescription());
			else
				getTranslator().$(sender, noPermissionMessage);
		else
			getTranslator().$(sender, notPlayerMessage);
		return true;
	}

	protected abstract void run(CommandSender sender, Command command,
			String label, List<String> arguments);

	public PluginCommand getPluginCommand() {
		return pluginCommand;
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

	public EconomyProvider getEconomyProvider() {
		return plugin.getEconomyProvider();
	}

	public void synch(Runnable task) {
		getPlugin().synch(task);
	}

	public void asynch(Runnable task) {
		getPlugin().asynch(task);
	}
}
