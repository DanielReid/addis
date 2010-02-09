/*
 * This file is part of ADDIS (Aggregate Data Drug Information System).
 * ADDIS is distributed from http://drugis.org/.
 * Copyright (C) 2009  Gert van Valkenhoef and Tommi Tervonen.
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

package org.drugis.addis.entities;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.drugis.common.EqualsUtil;

public class Study extends AbstractEntity implements Comparable<Study>, Entity, Population {
	private static final long serialVersionUID = 532314508658928979L;
	
	private static class MeasurementKey implements Serializable {
		private static final long serialVersionUID = 6310789667384578005L;
		
		private Entity d_outcomeM;
		private Arm d_arm;
		
		public MeasurementKey(Entity e, Arm g) {
			if (e == null) {
				throw new NullPointerException("Variable/Outcome = " + e + " may not be null");
			}
			if (!(e instanceof Variable) && g == null) {
				throw new NullPointerException("Arm = " + g + " may not be null for Endpoints/ADEs");
			}
			d_outcomeM = e;
			d_arm = g;
		}
		
		public boolean equals(Object o) {
			if (o instanceof MeasurementKey) { 
				MeasurementKey other = (MeasurementKey)o;
				return d_outcomeM.equals(other.d_outcomeM) && EqualsUtil.equal(d_arm, other.d_arm);
			}
			return false;
		}
		
		public int hashCode() {
			int code = 1;
			code = code * 31 + d_outcomeM.hashCode();
			code = code * 31 + (d_arm == null ? 0 : d_arm.hashCode());
			return code;
		}
	}
	
	public final static String PROPERTY_ID = "id";
	public final static String PROPERTY_ENDPOINTS = "endpoints";
	public final static String PROPERTY_ADVERSE_EVENTS = "adverseEvents";
	public final static String PROPERTY_POPULATION_CHARACTERISTICS = "populationCharacteristics";
	public final static String PROPERTY_ARMS = "arms";
	public final static String PROPERTY_CHARACTERISTIC = "Characteristics";
	public final static String PROPERTY_NOTE = "Note";
	public final static String PROPERTY_INDICATION = "indication";

	private List<Arm> d_arms = new ArrayList<Arm>();
	private String d_id;
	private Map<MeasurementKey, Measurement> d_measurements = new HashMap<MeasurementKey, Measurement>();
	private List<Endpoint> d_endpoints = new ArrayList<Endpoint>();
	private List<AdverseDrugEvent> d_adverseEvents = new ArrayList<AdverseDrugEvent>();
	private List<Variable> d_populationChars = new ArrayList<Variable>();
	private CharacteristicsMap d_chars = new CharacteristicsMap();
	private VariableMap d_popChars;
	private Indication d_indication;
	private Map<Object, Note> d_notes = new HashMap<Object, Note>();
	
	public Study(String id, Indication i) {
		d_id = id;
		d_indication = i;
		setArms(new ArrayList<Arm>());
		d_popChars = new VariableMap();
	}

	private void readObject(ObjectInputStream in) 
		throws IOException, ClassNotFoundException {
				in.defaultReadObject();
	}
	
	public List<Arm> getArms() {
		return d_arms;
	}
	
	public void setArms(List<Arm> arms) {
		List<Arm> oldVal = d_arms;
		d_arms = arms;
		updateMeasurements();
		
		firePropertyChange(PROPERTY_ARMS, oldVal, d_arms);
	}
	
	public void addArm(Arm group) {
		List<Arm> newVal = new ArrayList<Arm>(d_arms);
		newVal.add(group);
		setArms(newVal);
	}
	
	public Set<Drug> getDrugs() {
		Set<Drug> drugs = new HashSet<Drug>();
		for (Arm g : getArms()) {
			drugs.add(g.getDrug());
		}
		return drugs;
	}
	
	public Indication getIndication() {
		return d_indication;
	}

	public void setIndication(Indication indication) {
		Indication oldInd = d_indication;
		d_indication = indication;
		firePropertyChange(PROPERTY_INDICATION, oldInd, indication);
	}
	
	public Set<Entity> getDependencies() {
		HashSet<Entity> dep = new HashSet<Entity>(getDrugs());
		dep.addAll(getOutcomeMeasures());
		dep.add(d_indication);
		return dep;
	}
	
	public void setCharacteristic(BasicStudyCharacteristic c, Object val) {
		d_chars.put(c, val);
		/* Beware: Every characteristicHolder attached to this study will receive this event, even though only one characteristic has changed*/
		firePropertyChange(PROPERTY_CHARACTERISTIC, c, c);
	}
	
	public CharacteristicsMap setCharacteristics() {
		throw new IllegalAccessError("Can't set characteristics map directly.");
	}
	
	public CharacteristicsMap getCharacteristics() {
		return d_chars;
	}
	
	public String getId() {
		return d_id;
	}
	
	public void setId(String id) {
		String oldVal = d_id;
		d_id = id;
		firePropertyChange(PROPERTY_ID, oldVal, d_id);
	}
	
	@Override
	public String toString() {
		return getId();
	}
	
	@Override
	public boolean equals(Object o) {
		if (o instanceof Study) {
			Study other = (Study)o;
			if (other.getId() == null) {
				return getId() == null;
			}
			return other.getId().equals(getId());
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		return getId() == null ? 0 : getId().hashCode();
	}
	
	public int compareTo(Study other) {
		return getId().compareTo(other.getId());
	}
	
	public Measurement getMeasurement(OutcomeMeasure e, Arm g) {
		Measurement measurement = d_measurements.get(new MeasurementKey(e, g));
		return measurement;
	}
	
	public Measurement getMeasurement(Variable v, Arm g) {
		return d_measurements.get(new MeasurementKey(v, g));
	}
	
	public Measurement getMeasurement(Variable v) {
		return getMeasurement(v, null);
	}
	
	private void forceLegalArguments(OutcomeMeasure e, Arm g, Measurement m) {
		if (!getArms().contains(g)) {
			throw new IllegalArgumentException("Arm " + g + " not part of this study.");
		}
		if (!getOutcomeMeasures().contains(e)) {
			throw new IllegalArgumentException("Outcome " + e + " not measured by this study.");
		}
		if (!m.isOfType(e.getType())) {
			throw new IllegalArgumentException("Measurement does not conform with outcome");
		}
	}
	
	public void setMeasurement(OutcomeMeasure e, Arm g, Measurement m) {
		forceLegalArguments(e, g, m);
		d_measurements.put(new MeasurementKey(e, g), m);
	}
	
	/**
	 * Set population characteristic measurement on arm.
	 * @param v
	 * @param g
	 * @param m
	 */
	public void setMeasurement(Variable v, Arm g, Measurement m) {
		forceLegalArguments(v, g, m);
		d_measurements.put(new MeasurementKey(v, g), m);
	}
	
	/**
	 * Set population characteristic measurement on study.
	 * @param v
	 * @param g
	 * @param m
	 */
	public void setMeasurement(Variable v, Measurement m) {
		setMeasurement(v, null, m);
	}
	
	private void forceLegalArguments(Variable v, Arm g, Measurement m) {
		if (!d_populationChars.contains(v)) {
			throw new IllegalArgumentException("Variable " + v + " not in study");
		}
		if (g != null && !d_arms.contains(g)) {
			throw new IllegalArgumentException("Arm " + g + " not in study");
		}
		if (!m.isOfType(v.getType())) {
			throw new IllegalArgumentException("Measurement does not conform with outcome");
		}
	}

	public List<OutcomeMeasure> getOutcomeMeasures() {
		List<OutcomeMeasure> l = new ArrayList<OutcomeMeasure>();
		l.addAll(d_endpoints);
		l.addAll(d_adverseEvents);
		return l;
	}
	
	public List<Endpoint> getEndpoints() {
		return Collections.unmodifiableList(d_endpoints);
	}
	
	public List<AdverseDrugEvent> getAdverseEvents() {
		return Collections.unmodifiableList(d_adverseEvents);
	}
	
	public List<Variable> getPopulationCharacteristics() {
		return Collections.unmodifiableList(d_populationChars);
	}
	
	public List<? extends OutcomeMeasure> getOutcomeMeasures(Class<? extends OutcomeMeasure> type) {
		if (type == Endpoint.class) {
			return Collections.unmodifiableList(d_endpoints);
		} else if (type == AdverseDrugEvent.class){
			return Collections.unmodifiableList(d_adverseEvents);
		}
		throw new IllegalArgumentException(type + " is not a recognized type of Outcome for studies");
	}
	
	public void setEndpoints(List<Endpoint> endpoints) {
		List<Endpoint> oldVal = getEndpoints();
		d_endpoints = new ArrayList<Endpoint>(endpoints);
		updateMeasurements();
		firePropertyChange(PROPERTY_ENDPOINTS, oldVal, getEndpoints());
	}
	
	public void setAdverseEvents(List<AdverseDrugEvent> ade) {
		List<AdverseDrugEvent> oldVal = getAdverseEvents();
		d_adverseEvents = new ArrayList<AdverseDrugEvent>(ade);
		updateMeasurements();
		firePropertyChange(PROPERTY_ADVERSE_EVENTS, oldVal, getAdverseEvents());
	}
	
	public void setPopulationCharacteristics(List<Variable> chars) {
		List<Variable> oldVal = getPopulationCharacteristics();
		d_populationChars = new ArrayList<Variable>(chars);
		updateMeasurements();
		firePropertyChange(PROPERTY_POPULATION_CHARACTERISTICS, oldVal, getPopulationCharacteristics());
	}
	
	public void addAdverseEvent(AdverseDrugEvent ade) {
		if (ade == null) 
			throw new NullPointerException("Cannot add a NULL outcome measure");
		
		List<AdverseDrugEvent> newList = new ArrayList<AdverseDrugEvent>(d_adverseEvents);
		newList.add(ade);
		setAdverseEvents(newList);
	}
	
	public void addOutcomeMeasure(Endpoint om) {
		addEndpoint(om);
	}

	public void addEndpoint(Endpoint om) {
		if (om == null) 
			throw new NullPointerException("Cannot add a NULL outcome measure");
		
		List<Endpoint> newVal = new ArrayList<Endpoint>(d_endpoints);
		newVal.add(om);
		setEndpoints(newVal);
	}
		
	public void deleteOutcomeMeasure(Endpoint om) {
		deleteEndpoint(om);
	}

	public void deleteEndpoint(Endpoint om) {
		if (d_endpoints.contains(om)) {
			List<Endpoint> newVal = new ArrayList<Endpoint>(d_endpoints);
			newVal.remove(om);
			setEndpoints(newVal);
		}
	}
	
	private void updateMeasurements() {
		for (OutcomeMeasure om : getOutcomeMeasures()) {
			for (Arm g : getArms()) {
				MeasurementKey key = new MeasurementKey(om, g);
				if (d_measurements.get(key) == null) {
					d_measurements.put(key, om.buildMeasurement(g));
				}
			}
		}
	}
	
	public Object getCharacteristic(Characteristic c) {
		return d_chars.get(c);
	}
	
	public int getSampleSize() {
		int s = 0;
		for (Arm pg : d_arms)
			s += pg.getSize();
		return s;
	}
	
	public Measurement getPopulationCharacteristic(Variable v) {
		return d_popChars.get(v);
	}
	
	public VariableMap getPopulationCharacteristicMap() {
		return d_popChars; 
	}
	
	public void setPopulationCharacteristic(Variable v, Measurement m) {
		d_popChars.put(v, m);
	}
	

	public void putNote(Object key, Note note){
		d_notes.put(key, note);
		firePropertyChange(PROPERTY_NOTE, key, key);
	}
	
	public Note getNote(Object key){
		Note note = d_notes.get(key);
		return note != null ? note : new Note();
	}
	
	public Map<Object,Note> getNotes() {
		return d_notes;
	}
	
	public void removeNote (Object key){
		d_notes.remove(key);
	}

	public void removeEndpoint(int i) {
		List<Endpoint> newVal = new ArrayList<Endpoint>(d_endpoints);
		newVal.remove(i);
		setEndpoints(newVal);
	}
}