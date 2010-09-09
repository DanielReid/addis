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

package org.drugis.addis.presentation.wizard;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import org.drugis.addis.entities.Drug;
import org.drugis.addis.gui.SelectableStudyGraph;
import org.drugis.addis.presentation.ListHolder;
import org.drugis.addis.presentation.StudyGraphModel.Vertex;
import org.jgraph.JGraph;
import org.jgraph.graph.DefaultGraphCell;

public class SelectedDrugsGraphListener extends MouseAdapter {
	
	private ListHolder<Drug> d_drugList;
	private JGraph d_jgraph;
	private SelectableStudyGraph d_studyGraph;

	public SelectedDrugsGraphListener(SelectableStudyGraph selectableStudyGraph, JGraph graph, ListHolder<Drug> drugsList) {
		this.d_drugList = drugsList;
		this.d_studyGraph = selectableStudyGraph;
		this.d_jgraph = graph;
	}
	
	@Override
	public void mousePressed(MouseEvent e) {
		Object cell = d_jgraph.getFirstCellForLocation(e.getX(), e.getY());		
		
		if (cell instanceof DefaultGraphCell) {
			DefaultGraphCell realcell = (DefaultGraphCell) cell;
			Object obj = realcell.getUserObject();
			if (obj instanceof Vertex) {
				selectUnselectDrug(((Vertex) obj).getDrug());
			}
		}
	}	

	private void selectUnselectDrug(Drug drug) {
		ArrayList<Drug> drugs = new ArrayList<Drug>(d_drugList.getValue());
		if (drugs.contains(drug)) {
			drugs.remove(drug);
		} else {
			drugs.add(drug);
		}
		d_drugList.setValue(drugs);
		d_studyGraph.layoutGraph();
	}

}
