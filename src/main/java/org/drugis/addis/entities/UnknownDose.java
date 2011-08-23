/*
 * This file is part of ADDIS (Aggregate Data Drug Information System).
 * ADDIS is distributed from http://drugis.org/.
 * Copyright (C) 2009 Gert van Valkenhoef, Tommi Tervonen.
 * Copyright (C) 2010 Gert van Valkenhoef, Tommi Tervonen, 
 * Tijs Zwinkels, Maarten Jacobs, Hanno Koeslag, Florin Schimbinschi, 
 * Ahmad Kamal, Daniel Reid.
 * Copyright (C) 2011 Gert van Valkenhoef, Ahmad Kamal, 
 * Daniel Reid, Florin Schimbinschi.
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

import org.drugis.addis.ExampleData;


public class UnknownDose extends AbstractDose {
	
	public UnknownDose() {
		d_unit = ExampleData.MILLIGRAMS_A_DAY;
	}
	
	@Override
	public void setDoseUnit(DoseUnit u) {
		d_unit = u;
	}

	@Override
	public String toString() {
		return "Unknown Dose";
	}
	
	@Override
	public boolean equals(Object o) {
		if (o instanceof UnknownDose) {
			return true;
		}
		
		return false;
	}
	
	@Override
	public int hashCode() {
		return 1;
	}
	
	@Override
	public AbstractDose clone() {
		return new UnknownDose();
	}
	
}
