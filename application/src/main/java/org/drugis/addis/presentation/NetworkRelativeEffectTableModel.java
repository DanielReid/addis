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

package org.drugis.addis.presentation;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import org.drugis.addis.entities.TreatmentCategorySet;
import org.drugis.addis.entities.mtcwrapper.MTCModelWrapper;
import org.drugis.mtc.summary.QuantileSummary;

@SuppressWarnings("serial")
public class NetworkRelativeEffectTableModel extends AbstractTableModel {
	private NetworkMetaAnalysisPresentation d_pm;
	MTCModelWrapper d_networkModel;
	private final PropertyChangeListener d_listener;
	
	public NetworkRelativeEffectTableModel(NetworkMetaAnalysisPresentation pm, MTCModelWrapper networkModel) {
		d_pm = pm;
		d_networkModel = networkModel;
		d_listener = new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent evt) {
				fireTableDataChanged();
			}
		};
		
		// Listen to summaries
		List<TreatmentCategorySet> drugs = d_pm.getIncludedDrugs();
		for(TreatmentCategorySet d1 : drugs) {
			for (TreatmentCategorySet d2 : drugs) {
				if (!d1.equals(d2)) {
					attachListener(networkModel, d1, d2);
				}
			}
		}
	}

	private void attachListener(MTCModelWrapper networkModel, TreatmentCategorySet d1, TreatmentCategorySet d2) {
		QuantileSummary quantileSummary = getSummary(d1, d2);
		if(quantileSummary != null) {
			quantileSummary.addPropertyChangeListener(d_listener);
		}
	}

	public int getColumnCount() {
		return d_pm.getIncludedDrugs().size();
	}

	public int getRowCount() {
		return d_pm.getIncludedDrugs().size();
	}
	
	public String getDescriptionAt(int row, int col) {
		if (row == col) {
			return null;
		}
		return "\"" + getDrugAt(col).getLabel() + "\" relative to \"" + getDrugAt(row).getLabel() + "\"";
	}

	private TreatmentCategorySet getDrugAt(int idx) {
		return d_pm.getIncludedDrugs().get(idx);
	}
	
	public Object getValueAt(int row, int col) {
		if (row == col) {
			return getDrugAt(row);
		}
		return getSummary(getDrugAt(row), getDrugAt(col));
	}
	
	private QuantileSummary getSummary(final TreatmentCategorySet d1, final TreatmentCategorySet d2) {
		return d_networkModel.getQuantileSummary(d_networkModel.getRelativeEffect(d1, d2));
	}
}
