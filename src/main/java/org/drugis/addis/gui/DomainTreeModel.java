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

package org.drugis.addis.gui;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;

import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

import org.drugis.addis.entities.AdverseEvent;
import org.drugis.addis.entities.Domain;
import org.drugis.addis.entities.DomainEvent;
import org.drugis.addis.entities.DomainListener;
import org.drugis.addis.entities.Drug;
import org.drugis.addis.entities.Endpoint;
import org.drugis.addis.entities.Entity;
import org.drugis.addis.entities.EntityCategory;
import org.drugis.addis.entities.Indication;
import org.drugis.addis.entities.PopulationCharacteristic;
import org.drugis.addis.entities.Study;
import org.drugis.addis.entities.analysis.BenefitRiskAnalysis;
import org.drugis.addis.entities.analysis.MetaAnalysis;
import org.drugis.common.CollectionUtil;


public class DomainTreeModel implements TreeModel {
	private String d_root = "Database";
	private Domain d_domain;
	
	private List<TreeModelListener> d_listeners;
	
	
	private class DomainListenerImpl implements DomainListener {
		public void domainChanged(DomainEvent evt) {
			fireTreeStructureChanged();	
		}
	}
	
	public DomainTreeModel(Domain domain) {
		d_domain = domain;
		d_domain.addListener(new DomainListenerImpl());
		
		d_listeners = new ArrayList<TreeModelListener>();
	}

	public Object getChild(Object parent, int childIndex) {
		if (d_root == parent && childIndex >= 0 && childIndex < getCategories().size()) {
			return getCategories().get(childIndex);
		} else {
			for (EntityCategory cat : getCategories()) {
				if (isCategoryRequest(cat, parent, childIndex)) {
					return CollectionUtil.getElementAtIndex(d_domain.getCategoryContents(cat), childIndex);
				}
			}
		}
		return null;
	}
	
	private boolean isCategoryRequest(EntityCategory categoryNode, Object parent,
			int childIndex) {
		return categoryNode == parent && childIndex >= 0 && childIndex < d_domain.getCategoryContents(categoryNode).size();
	}

	private EntityCategory getCategoryNode(Object node) {
		int typeIdx = getCategories().indexOf(node);
		if (typeIdx >= 0) {
			return getCategories().get(typeIdx);
		}
		return null;
	}

	public int getChildCount(Object parent) {
		if (d_root == parent) {
			return getCategories().size();
		} else {
			SortedSet<?> contents = d_domain.getCategoryContents(getCategoryNode(parent));
			if (contents != null) {
				return contents.size();
			}
		}
		return 0;
	}

	public int getIndexOfChild(Object parent, Object child) {
		if (parent == d_root) {
			return getCategories().indexOf(child);
		} else {
			SortedSet<?> contents = d_domain.getCategoryContents(getCategoryNode(parent));
			if (contents != null) {
				return CollectionUtil.getIndexOfElement(contents, child);
			}
		}
		return -1;
	}

	public Object getRoot() {
		return d_root;
	}
	
	public boolean isLeaf(Object node) {
		if (node instanceof Entity) {
			EntityCategory category = d_domain.getCategory(((Entity) node));
			if (category != null) {
				return d_domain.getCategoryContents(category).contains(node);
			}
		}
		return false;
	}
	
	public TreePath getPathTo(Object node) {
		if (d_root.equals(node)) {
			return new TreePath(new Object[] { d_root });
		} else if (getCategories().contains(node)) {
			return new TreePath(new Object[] { d_root, node });
		} else if (isLeaf(node)) {
			return new TreePath(new Object[] { d_root, d_domain.getCategory(((Entity)node)), node }); 
		}
		return null;
	}

	public void addTreeModelListener(TreeModelListener listener) {
		if (!d_listeners.contains(listener)) {
			d_listeners.add(listener);
		}
	}
	
	public void removeTreeModelListener(TreeModelListener listener) {
		d_listeners.remove(listener);
	}

	private void fireTreeStructureChanged() {
		for (TreeModelListener l : d_listeners) {
			l.treeStructureChanged(new TreeModelEvent(this, new Object[]{d_root}));
		}
	}

	public void valueForPathChanged(TreePath path, Object node) {
	}
	
	public Object getIndicationsNode() {
		return d_domain.getCategory(Indication.class);
	}

	public Object getStudiesNode() {
		return d_domain.getCategory(Study.class);
	}

	public Object getEndpointsNode() {
		return d_domain.getCategory(Endpoint.class);
	}
	
	public Object getAdverseEventsNode() {
		return d_domain.getCategory(AdverseEvent.class);
	}
	
	public Object getDrugsNode() {
		return d_domain.getCategory(Drug.class);
	}
	
	public Object getMetaAnalysesNode() {
		return d_domain.getCategory(MetaAnalysis.class);
	}
	
	public Object getPopulationCharacteristicsNode() {
		return d_domain.getCategory(PopulationCharacteristic.class);
	}
	
	public Object getBenefitRiskAnalysesNode() {
		return d_domain.getCategory(BenefitRiskAnalysis.class);
	}

	public List<EntityCategory> getCategories() {
		return d_domain.getCategories();
	}
	
}
