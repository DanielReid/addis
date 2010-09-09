/*
 * This file is part of ADDIS (Aggregate Data Drug Information System).
 * ADDIS is distributed from http://drugis.org/.
 * Copyright (C) 2009 Gert van Valkenhoef, Tommi Tervonen.
 * Copyright (C) 2010 Gert van Valkenhoef, Tommi Tervonen, 
 * Tijs Zwinkels, Maarten Jacobs, Hanno Koeslag, Florin Schimbinschi, 
 * Ahmad Kamal, Daniel Reid.
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

package org.drugis.addis.entities;


public enum DerivedStudyCharacteristic implements Characteristic {

	STUDYSIZE("Study size", Integer.class, true),
	INDICATION("Intended Indication", Indication.class, true),
	DRUGS("Investigational drugs", Object.class, true),
	DOSING("Dosing", Dosing.class, true),
	ARMS("Study Arms", Integer.class, false);

	private Class<?> d_type;
	private String d_description;
	private boolean d_defaultVisible;

	DerivedStudyCharacteristic(String description, Class<?> type, boolean defaultVisible) {
		d_description = description;
		d_type = type;
		d_defaultVisible = defaultVisible;
	}
	
	public enum Dosing {
		FIXED("Fixed"),
		FLEXIBLE("Flexible"),
		MIXED("Mixed");
		
		private String d_title;

		Dosing(String title) {
			d_title = title;
		}
		
		@Override
		public String toString() {
			return d_title;
		}
	}

	public String getDescription() {
		return d_description;
	}

	public Class<?> getValueType() {
		return d_type;
	}	
	
	public boolean getDefaultVisible() {
		return d_defaultVisible;
	}
}
