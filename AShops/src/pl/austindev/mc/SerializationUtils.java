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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;

public class SerializationUtils {
	public static List<Map<String, Object>> serializeItems(List<ItemStack> items) {
		List<Map<String, Object>> serializedItems = new ArrayList<Map<String, Object>>();
		for (ItemStack item : items)
			serializedItems.add(serialize(item));
		return serializedItems;
	}

	public static Map<String, Object> serialize(ConfigurationSerializable object) {
		Map<String, Object> serializedObject = new HashMap<String, Object>();
		serializedObject.putAll(object.serialize());
		for (Map.Entry<String, Object> entry : serializedObject.entrySet()) {
			if (entry.getValue() instanceof ConfigurationSerializable) {
				entry.setValue(serialize((ConfigurationSerializable) entry
						.getValue()));
			}
		}
		serializedObject.put(ConfigurationSerialization.SERIALIZED_TYPE_KEY,
			ConfigurationSerialization.getAlias(object.getClass()));
		return serializedObject;
	}

	@SuppressWarnings("unchecked")
	public static ConfigurationSerializable deserialize(
			Map<String, Object> object) {
		for (Map.Entry<String, Object> entry : object.entrySet()) {
			if (entry.getValue() instanceof Map
					&& ((Map<String, Object>) entry.getValue())
							.containsKey(ConfigurationSerialization.SERIALIZED_TYPE_KEY)) {
				entry.setValue(deserialize((Map<String, Object>) entry
						.getValue()));
			}
		}
		return ConfigurationSerialization.deserializeObject(object);
	}

	public static List<ItemStack> deserializeItems(
			List<Map<String, Object>> serializedItems) {
		List<ItemStack> items = new ArrayList<ItemStack>();
		for (Map<String, Object> map : serializedItems)
			items.add((ItemStack) deserialize(map));
		return items;
	}

	public static byte[] toByteArrayOld(ItemStack itemStack) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			ObjectOutputStream oos = new ObjectOutputStream(baos);
			try {
				oos.writeObject(serialize(itemStack));
				return baos.toByteArray();
			} finally {
				oos.close();
			}
		} finally {
			baos.close();
		}
	}

	@SuppressWarnings("unchecked")
	public static ItemStack toItemStackOld(byte[] byteArray)
			throws IOException, ClassNotFoundException {
		ByteArrayInputStream bais = new ByteArrayInputStream(byteArray);
		try {
			ObjectInputStream ois = new ObjectInputStream(bais);
			try {
				return (ItemStack) deserialize((Map<String, Object>) ois
						.readObject());
			} finally {
				ois.close();
			}
		} finally {
			bais.close();
		}
	}

	public static byte[] toByteArray(ItemStack itemStack) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			ObjectOutputStream out = new BukkitObjectOutputStream(baos);
			try {
				out.writeObject(itemStack);
				return baos.toByteArray();
			} finally {
				out.close();
			}
		} finally {
			baos.close();
		}
	}

	public static ItemStack toItemStack(byte[] byteArray) throws IOException,
			ClassNotFoundException {
		ByteArrayInputStream bais = new ByteArrayInputStream(byteArray);
		try {
			ObjectInputStream in = new BukkitObjectInputStream(bais);
			try {
				return (ItemStack) in.readObject();
			} finally {
				in.close();
			}
		} finally {
			bais.close();
		}
	}
}
