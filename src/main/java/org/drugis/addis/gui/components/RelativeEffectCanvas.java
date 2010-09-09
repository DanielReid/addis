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

package org.drugis.addis.gui.components;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JPanel;

import org.drugis.addis.presentation.ForestPlotPresentation;
import org.drugis.addis.treeplot.ForestPlot;

@SuppressWarnings("serial")
public class RelativeEffectCanvas extends JPanel {
	
	private ForestPlot d_plot;

	public ForestPlot getPlot() {
		return d_plot;
	}

	public RelativeEffectCanvas(ForestPlotPresentation model) {
		d_plot = new ForestPlot(model);
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		d_plot.paint((Graphics2D) g);
	}
	
	@Override
	public Dimension getPreferredSize() {
		return d_plot.getPlotSize();
	}
}