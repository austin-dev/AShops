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

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class TemporaryValuesContainer {
	private final Map<String, TemporaryValue> values = new ConcurrentHashMap<String, TemporaryValue>();

	public <T> void put(String playerName, TemporaryValuesSource source, T value) {
		values.put(playerName.toLowerCase(), new TemporaryValue(source, value));
	}

	public TemporaryValue get(String playerName) {
		return values.get(playerName.toLowerCase());
	}
	public TemporaryValue remove(String playerName) {
		return values.remove(playerName.toLowerCase());
	}

	public class TemporaryValue {
		private final TemporaryValuesSource source;
		private final Class<?> type;
		private final Object value;

		public <T> T get(Class<T> type) {
			return this.type.equals(type) ? type.cast(value) : null;
		}

		public TemporaryValuesSource getSource() {
			return source;
		}

		public <T> TemporaryValue(TemporaryValuesSource source, T value) {
			this.source = source;
			this.type = value.getClass();
			this.value = value;
		}
	}
}
