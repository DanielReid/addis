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

package org.drugis.addis.mcmcmodel;

import gov.lanl.yadas.ArgumentMaker;


public class InverseLogitArgumentMaker implements ArgumentMaker{

	private final int d_idx;

	public InverseLogitArgumentMaker(int idx) {
		d_idx = idx;
		
	}
	
	public double[] getArgument(double[][] params) {
		double[] result = new double[params[d_idx].length];
		for (int i = 0; i < result.length; ++i) {
			result[i] =  MathUtil.ilogit(params[d_idx][i]);
		}
		return result;
	}

}
