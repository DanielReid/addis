/*
 * This file is part of ADDIS (Aggregate Data Drug Information System).
 * ADDIS is distributed from http://drugis.org/.
 * Copyright (C) 2009 Gert van Valkenhoef, Tommi Tervonen.
 * Copyright (C) 2010 Gert van Valkenhoef, Tommi Tervonen, 
 * Tijs Zwinkels, Maarten Jacobs, Hanno Koeslag, Florin Schimbinschi, 
 * Ahmad Kamal, Daniel Reid.
 * Copyright (C) 2011 Gert van Valkenhoef, Ahmad Kamal, 
 * Daniel Reid, Florin Schimbinschi.
 * Copyright (C) 2012 Gert van Valkenhoef, Daniel Reid, 
 * Joël Kuiper, Wouter Reckman.
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

package org.drugis.addis.entities.relativeeffect;


import org.drugis.addis.entities.RateMeasurement;

public abstract class BasicRatio extends AbstractBasicRelativeEffect<RateMeasurement> implements BasicRateRelativeEffect {
	private static final double NEUTRAL_VALUE = 1.0;
	protected BasicRatio(RateMeasurement baseline, RateMeasurement subject) throws IllegalArgumentException {
		super(baseline, subject);
	}
	
	@Override
	public AxisType getAxisType() {
		return AxisType.LOGARITHMIC;
	}

	public TransformedLogStudentT getDistribution() {
		return new TransformedLogStudentT(getMu(), getSigma(), getDegreesOfFreedom());
	}
	
	@Override
	public double getNeutralValue() {
		return NEUTRAL_VALUE;
	}
	
	protected abstract double getMu();
	protected abstract double getSigma();
}