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

package org.drugis.addis.gui.builder;

import java.util.List;

import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.text.DefaultFormatter;

import org.drugis.addis.entities.DoseUnit;
import org.drugis.addis.entities.ScaleModifier;
import org.drugis.addis.entities.Unit;
import org.drugis.addis.gui.components.ComboBoxPopupOnFocusListener;
import org.drugis.addis.gui.components.NotEmptyValidator;
import org.drugis.addis.presentation.DosePresentation;
import org.drugis.common.gui.ViewBuilder;

import com.jgoodies.binding.adapter.BasicComponentFactory;
import com.jgoodies.binding.beans.PropertyConnector;
import com.jgoodies.binding.list.SelectionInList;

public class DoseView implements ViewBuilder {
	private DosePresentation d_model;
	private JComboBox d_scaleModifierCB;
	private JComboBox d_unitCB;
	private NotEmptyValidator d_validator;
	private JFormattedTextField d_quantityMin;
	private JFormattedTextField d_quantityMax;
	private final List<Unit> d_unitOptions;
	
	public DoseView(DosePresentation dose, List<Unit> unitOptions) {
		d_model = dose;
		d_unitOptions = unitOptions;
	}
	
// FIXME: unused?
//	public DoseView(DosePresentation dose, NotEmptyValidator validator) {
//		d_model = dose;
//		d_validator = validator;
//	}	
	
	public void initComponents() {
		d_quantityMin = new JFormattedTextField(new DefaultFormatter());
		d_quantityMax = new JFormattedTextField(new DefaultFormatter());
		PropertyConnector.connectAndUpdate(d_model.getMinModel(), d_quantityMin, "value");
		PropertyConnector.connectAndUpdate(d_model.getMaxModel(), d_quantityMax, "value");
		d_quantityMin.setColumns(8);
		d_quantityMax.setColumns(8);
		
		SelectionInList<ScaleModifier> scaleModifierSelectionInList = new SelectionInList<ScaleModifier>(
				ScaleModifier.values(),
				d_model.getDoseUnitPresentation().getModel(DoseUnit.PROPERTY_SCALE_MODIFIER));
		
		SelectionInList<Unit> unitSelectionInList = new SelectionInList<Unit>(
				d_unitOptions,
				d_model.getDoseUnitPresentation().getModel(DoseUnit.PROPERTY_UNIT));
		
		d_scaleModifierCB = BasicComponentFactory.createComboBox(scaleModifierSelectionInList);
		d_unitCB = BasicComponentFactory.createComboBox(unitSelectionInList);
		ComboBoxPopupOnFocusListener.add(d_unitCB);
		ComboBoxPopupOnFocusListener.add(d_scaleModifierCB);
		
		if (d_validator != null) {
			d_validator.add(d_quantityMin);
			d_validator.add(d_unitCB);			
		}		
	}

	public JComponent buildPanel() {
		initComponents();
		JPanel panel = new JPanel();
		panel.add(d_quantityMin);
		panel.add(new JLabel(" up to "));
		panel.add(d_quantityMax);
		panel.add(d_unitCB);
		return panel;
	}
}
