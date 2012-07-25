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

package org.drugis.addis.gui.wizard;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

import org.drugis.addis.FileNames;
import org.drugis.addis.entities.Drug;
import org.drugis.addis.entities.FixedDose;
import org.drugis.addis.entities.FlexibleDose;
import org.drugis.addis.entities.UnknownDose;
import org.drugis.addis.entities.treatment.Category;
import org.drugis.addis.entities.treatment.DosedDrugTreatment;
import org.drugis.addis.entities.treatment.LeafNode;
import org.drugis.addis.entities.treatment.TypeNode;
import org.drugis.addis.gui.AuxComponentFactory;
import org.drugis.addis.gui.CategoryKnowledgeFactory;
import org.drugis.addis.gui.GUIFactory;
import org.drugis.addis.gui.builder.DoseView;
import org.drugis.addis.gui.components.NotEmptyValidator;
import org.drugis.addis.gui.knowledge.DosedDrugTreatmentKnowledge;
import org.drugis.addis.gui.knowledge.DosedDrugTreatmentKnowledge.CategorySpecifiers;
import org.drugis.addis.presentation.DosedDrugTreatmentPresentation;
import org.drugis.addis.presentation.ModifiableHolder;
import org.drugis.addis.presentation.ValueHolder;
import org.drugis.common.EqualsUtil;
import org.drugis.common.gui.LayoutUtil;

import com.jgoodies.binding.adapter.BasicComponentFactory;
import com.jgoodies.binding.beans.PropertyAdapter;
import com.jgoodies.binding.list.ArrayListModel;
import com.jgoodies.binding.list.ObservableList;
import com.jgoodies.binding.value.AbstractValueModel;
import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

public class AddDosedDrugTreatmentWizardStep extends AbstractDoseTreatmentWizardStep {
	private static final long serialVersionUID = 7730051460456443680L;
	private final TypeNode d_unknownNode = new TypeNode(UnknownDose.class);
	private final TypeNode d_fixedNode = new TypeNode(FixedDose.class);
	private final TypeNode d_flexibleNode = new TypeNode(FlexibleDose.class);

	private NotEmptyValidator d_validator;
	private JComboBox d_knownDoseCombo;
	private JComboBox d_unknownDoseCombo;
	
	private ValueHolder<Boolean> d_considerDoseType = new ModifiableHolder<Boolean>(null);

	public AddDosedDrugTreatmentWizardStep(DosedDrugTreatmentPresentation presentationModel) {
		super(presentationModel, "Add characteristics", "Add the name, drug and categories for this treatment", null);
		d_validator = new NotEmptyValidator();
		d_validators.add(d_validator);
		
		d_pm.getCategories().addListDataListener(new ListDataListener() {			
			@Override
			public void intervalRemoved(ListDataEvent e) {
				rebuildPanel();
			}
			
			@Override
			public void intervalAdded(ListDataEvent e) {
				rebuildPanel();
			}
			
			@Override
			public void contentsChanged(ListDataEvent e) {
				rebuildPanel();
			}
		});
	}		
	
	
	@Override
	protected void initialize() {
//		rebuildPanel();
	}
	
	public void setConsiderDoseType() {
		Object selection = d_knownDoseCombo.getSelectedItem();
		if(EqualsUtil.equal(selection, CategorySpecifiers.CONSIDER)) {
			d_considerDoseType.setValue(true);
		} else if(EqualsUtil.equal(selection, CategorySpecifiers.DO_NOT_CONSIDER)) {
			d_considerDoseType.setValue(false);
		} else { 
			d_considerDoseType.setValue(null);
		}
	}
	
	public ValueHolder<Boolean> getConsiderDoseType() { 
		return d_considerDoseType;
	}
	
	protected JPanel buildPanel() {
		FormLayout layout = new FormLayout(
				"left:pref, 3dlu, pref, 3dlu, pref, fill:pref:grow, pref, 3dlu, pref",
				"p, 3dlu, p, 3dlu, p, 3dlu, p, 3dlu, p, 3dlu, p, 3dlu, p"
				);	
		
		PanelBuilder builder = new PanelBuilder(layout);
		
		CellConstraints cc = new CellConstraints();
		int row = 1;
		int colSpan = layout.getColumnCount();

		JTextField name = BasicComponentFactory.createTextField(d_pm.getModel(DosedDrugTreatment.PROPERTY_NAME), false);
		name.setColumns(15);
		
		final AbstractValueModel drugModel = d_pm.getModel(DosedDrugTreatment.PROPERTY_DRUG);
		builder.addLabel("Drug:", cc.xy(1, row));
		JComboBox drugSelect = AuxComponentFactory.createBoundComboBox(d_domain.getDrugs(), drugModel, true);
		builder.add(drugSelect, cc.xy(3, row));
		builder.add(createNewDrugButton(drugModel), cc.xy(5, row));
		d_validator.add(drugSelect);

		builder.addLabel("Name:", cc.xy(7, row));
		builder.add(name, cc.xy(9, row));
		d_validator.add(name);
		
		row += 2;
		builder.addSeparator("Category labels", cc.xyw(1, row, colSpan));
		
		row += 2;
		JComponent categoriesPanel = createCategoriesPanel(d_pm);
		JScrollPane catPane = new JScrollPane(categoriesPanel);
		catPane.setPreferredSize(new Dimension(PANEL_WIDTH, 200));
		builder.add(catPane, cc.xyw(1, row, colSpan));
		
		row += 2;
		builder.addSeparator("Dose criteria", cc.xyw(1, row, colSpan));
		
		row += 2;
		builder.addLabel("Unit:", cc.xy(1, row));
		builder.add(DoseView.createDoseUnitRow(d_pm.getDoseUnitPresentation(), d_domain.getUnits()), cc.xyw(3, row, colSpan - 2));
		
		row += 2;
		builder.addLabel("Unknown dose:", cc.xy(1, row));
		d_unknownDoseCombo = createCategoryComboBox(d_pm.getCategories());
		d_unknownDoseCombo.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					d_pm.setSelected(d_unknownNode, d_unknownDoseCombo.getSelectedItem());	
				}
			}
		});
		builder.add(d_unknownDoseCombo, cc.xyw(3, row, colSpan - 2));

		row += 2;
		builder.addLabel("Known dose:", cc.xy(1, row));
		d_knownDoseCombo = createCategoryComboBox(d_pm.getCategories(),  DosedDrugTreatmentKnowledge.CategorySpecifiers.CONSIDER, DosedDrugTreatmentKnowledge.CategorySpecifiers.DO_NOT_CONSIDER);
		d_knownDoseCombo.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) { 
					d_pm.resetTree();
					d_pm.setSelected(d_fixedNode, d_knownDoseCombo.getSelectedItem());
					d_pm.setSelected(d_flexibleNode, d_knownDoseCombo.getSelectedItem());
					setConsiderDoseType();
				}
			}
		});
		builder.add(d_knownDoseCombo, cc.xyw(3, row, colSpan - 2));
		return builder.getPanel();
	}

	public static JComboBox createCategoryComboBox(List<Category> categories, DosedDrugTreatmentKnowledge.CategorySpecifiers ... extraItems) {
		ObservableList<Object> list = new ArrayListModel<Object>();
		list.add(0, new LeafNode());
		for (DosedDrugTreatmentKnowledge.CategorySpecifiers item : extraItems) {
			list.add(item);
		}
		for (Category category : categories) {
			list.add(new LeafNode(category));
		}
		return new JComboBox(list.toArray());
	}
	
	private JButton createNewDrugButton(final AbstractValueModel drugModel) {
		JButton btn = GUIFactory.createPlusButton("Create drug");
		btn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				d_mainWindow.showAddDialog(CategoryKnowledgeFactory.getCategoryKnowledge(Drug.class), drugModel);
			}
		});
		return btn;
	}
	
	private JComponent createCategoriesPanel(final DosedDrugTreatmentPresentation model) { 
		FormLayout layout = new FormLayout(
				"left:pref, 3dlu, fill:pref:grow, 3dlu, pref",
				"p");
		PanelBuilder builder = new PanelBuilder(layout);
		CellConstraints cc = new CellConstraints();
		builder.setDefaultDialogBorder();
		int row = 1;

		for(final Category category : model.getCategories()) {
			builder.add(new JLabel("Category name"), cc.xy(1, row));
			JTextField name = BasicComponentFactory.createTextField(new PropertyAdapter<Category>(category, Category.PROPERTY_NAME), false);
			builder.add(name, cc.xy(3, row));
			JButton remove = GUIFactory.createIconButton(FileNames.ICON_DELETE, "delete");
			remove.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					model.getCategories().remove(category);
				}
			});
			builder.add(remove, cc.xy(5, row));
			row = LayoutUtil.addRow(layout, row);
		}
		
		builder.add(createAddCategoryButton(model), cc.xy(1, row));
		return builder.getPanel();
	}
	

	private JButton createAddCategoryButton(final DosedDrugTreatmentPresentation model) {
		JButton btn = GUIFactory.createLabeledIconButton("Add category" ,FileNames.ICON_PLUS);
		btn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				model.getCategories().add(new Category());
			}
		});
		return btn;
	}
}