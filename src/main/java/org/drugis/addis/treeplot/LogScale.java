/*
 * This file is part of ADDIS (Aggregate Data Drug Information System).
 * ADDIS is distributed from http://drugis.org/.
 * Copyright (C) 2009  Gert van Valkenhoef and Tommi Tervonen.
 * Copyright (C) 2010  Gert van Valkenhoef, Tommi Tervonen, Tijs Zwinkels,
 * Maarten Jacobs and Hanno Koeslag.
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

package org.drugis.addis.treeplot;

import org.drugis.common.Interval;

public class LogScale implements Scale {

	private double d_max;
	private double d_min;

	public LogScale(Interval<Double> interval) {
		d_max = interval.getUpperBound();
		d_min = interval.getLowerBound();
	}

	public double getMax() {
		return d_max;
	}

	public double getMin() {
		return d_min;
	}

	public double getNormalized(double x) {
		return Math.log(x / d_min) / Math.log(d_max / d_min); 
	}
	
	public double getNormalizedLog10(double x) {
		return Math.log10(x / d_min) / Math.log10(d_max / d_min); 
	}
}
