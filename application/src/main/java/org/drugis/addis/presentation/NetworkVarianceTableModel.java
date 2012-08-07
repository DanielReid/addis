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

import javax.swing.table.AbstractTableModel;

import org.drugis.addis.entities.treatment.TreatmentDefinition;
import org.drugis.mtc.Parameter;
import org.drugis.mtc.presentation.InconsistencyWrapper;
import org.drugis.mtc.presentation.MTCModelWrapper;
import org.drugis.mtc.summary.QuantileSummary;

@SuppressWarnings("serial")
public class NetworkVarianceTableModel extends AbstractTableModel {

	private static final int RANDOM_EFFECTS = 0;
	private final MTCModelWrapper<org.drugis.addis.entities.treatment.TreatmentDefinition> d_mtc;
	private final PropertyChangeListener d_listener;

	public NetworkVarianceTableModel(final MTCModelWrapper<TreatmentDefinition> mtc) {
		d_mtc = mtc;

		d_listener = new PropertyChangeListener() {
			@Override
			public void propertyChange(final PropertyChangeEvent evt) {
				fireTableDataChanged();
			}
		};

		if (isInconsistency()) {
			attachListener(((InconsistencyWrapper<TreatmentDefinition>) d_mtc).getInconsistencyVariance());
		}
		attachListener(mtc.getRandomEffectsVariance());
	}

	private void attachListener(final Parameter p) {
		final QuantileSummary quantileSummary = d_mtc.getQuantileSummary(p);
		if(quantileSummary != null) {
			quantileSummary.addPropertyChangeListener(d_listener);
		}
	}

	@Override
	public Class<?> getColumnClass(final int columnIndex) {
		if (columnIndex == 0) {
			return String.class;
		} else {
			return QuantileSummary.class;
		}
	}

	@Override
	public String getColumnName(final int column) {
		return column == 0 ? "Parameter" : "Median (95% CrI)";
	}

	@Override
	public int getRowCount() {
		return isInconsistency() ? 2 : 1;
	}

	private boolean isInconsistency() {
		return (d_mtc instanceof InconsistencyWrapper);
	}

	@Override
	public Object getValueAt(final int row, final int col) {
		if (col == 0) {
			return getRowDescription(row);
		} else {
			return getEstimate(row);
		}
	}

	private QuantileSummary getEstimate(final int row) {
		return row == RANDOM_EFFECTS ? getRandomEffectsSummary() : getInconsistencySummary();
	}

	private QuantileSummary getInconsistencySummary() {
		if (isInconsistency()) {
			final Parameter p = ((InconsistencyWrapper<TreatmentDefinition>) d_mtc).getInconsistencyVariance();
			return d_mtc.getQuantileSummary(p);
		}
		return null;
	}

	private QuantileSummary getRandomEffectsSummary() {
		final Parameter p = d_mtc.getRandomEffectsVariance();
		return d_mtc.getQuantileSummary(p);
	}

	private String getRowDescription(final int row) {
		if (row == RANDOM_EFFECTS) {
			return "Random Effects Variance";
		} else {
			return "Inconsistency Variance";
		}
	}

	@Override
	public int getColumnCount() {
		return 2;
	}
}
