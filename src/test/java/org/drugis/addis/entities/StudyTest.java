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

package org.drugis.addis.entities;

import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;

import org.drugis.addis.ExampleData;
import org.drugis.addis.entities.Study.MeasurementKey;
import org.drugis.addis.entities.StudyActivity.UsedBy;
import org.drugis.common.JUnitUtil;
import org.junit.Before;
import org.junit.Test;

public class StudyTest {
	
	private Arm d_pg;
	private Study d_orig;
	private Study d_clone;
	private Note d_note;
	private Study d_empty;

	@Before
	public void setUp() {
		d_note = new Note(Source.CLINICALTRIALS, "Original text Yo!");
		d_pg = new Arm("", 0, null, null);
		d_orig = ExampleData.buildStudyFava2002();
		
		// Add some notes to test them being cloned.
		d_orig.getArms().get(1).getNotes().add(d_note);
		d_orig.getStudyAdverseEvents().get(0).getNotes().add(d_note);
		d_orig.getStudyIdWithNotes().getNotes().add(d_note);
		ObjectWithNotes<Object> val = new ObjectWithNotes<Object>(null);
		val.getNotes().add(d_note);
		d_orig.setCharacteristicWithNotes(BasicStudyCharacteristic.SOURCE,
				val);
		
		d_clone = d_orig.clone();
		d_empty = new Study("empty", ExampleData.buildIndicationDepression());
	}
	
	@Test
	public void testSetId() {
		JUnitUtil.testSetter(new Study("X", new Indication(0L, "")), Study.PROPERTY_ID, "X", "NCT00351273");
	}
	
	@Test
	public void testSetEndpoints() {
		List<Endpoint> list = Collections.singletonList(new Endpoint("e", Variable.Type.RATE));
		JUnitUtil.testSetter(new Study("X", new Indication(0L, "")), Study.PROPERTY_ENDPOINTS, Collections.EMPTY_LIST, 
				list);
	}
	
	@Test
	public void testSetPopulationCharacteristics() {
		List<Variable> list = Collections.<Variable>singletonList(new ContinuousPopulationCharacteristic("e"));
		JUnitUtil.testSetter(new Study("X", new Indication(0L, "")),
				Study.PROPERTY_POPULATION_CHARACTERISTICS, Collections.EMPTY_LIST, 
				list);
	}
	
	@Test
	public void testAddEndpoint() {
		JUnitUtil.testAdder(new Study("X", new Indication(0L, "")), Study.PROPERTY_ENDPOINTS, "addEndpoint", new Endpoint("e", Variable.Type.RATE));
	}
	
	@Test(expected=NullPointerException.class)
	public void testAddOutcomeMeasureNULLthrows() {
		Study s = new Study("s", new Indication(0L, ""));
		s.addEndpoint(null);
	}
	
	@Test
	public void testSetArms() {
		List<Arm> list = Collections.singletonList(d_pg);
		JUnitUtil.testSetter(new Study("X", new Indication(0L, "")), Study.PROPERTY_ARMS, Collections.EMPTY_LIST, 
				list);
	}
	
	@Test
	public void testInitialArms() {
		Study study = new Study("X", new Indication(0L, ""));
		assertNotNull(study.getArms());
		assertTrue(study.getArms().isEmpty());
	}
	
	@Test
	public void testAddArm() {
		JUnitUtil.testAdder(new Study("X", new Indication(0L, "")), Study.PROPERTY_ARMS, "addArm", d_pg);
	}
	
	@Test
	public void testGetDrugs() {
		Study s = ExampleData.buildStudyDeWilde();
		Set<Drug> expected = new HashSet<Drug>();
		expected.add(ExampleData.buildDrugFluoxetine());
		expected.add(ExampleData.buildDrugParoxetine());
		assertEquals(expected, s.getDrugs());
	}
	
	@Test
	public void testToString() {
		String id = "NCT00351273";
		Study study = new Study(id, new Indication(0L, ""));
		assertEquals(id, study.toString());
	}
	
	@Test
	public void testSetStudyActivityAt() throws DatatypeConfigurationException {
		Arm arm1 = new Arm("testArm1", 100);
		Arm arm2 = new Arm("testArm2", 200);
		Epoch epoch1 = new Epoch("testEpoch1", DatatypeFactory.newInstance().newDuration(10000));
		StudyActivity randomization = new StudyActivity("Randomization", PredefinedActivity.RANDOMIZATION);
		d_empty.getEpochs().add(epoch1);
		d_empty.addArm(arm1);
		d_empty.getStudyActivities().add(randomization);
		d_empty.setStudyActivityAt(arm1, epoch1, randomization);
		Set<UsedBy> usedByRandomization = new HashSet<UsedBy>();
		UsedBy usedByarm1epoch1 = new UsedBy(arm1, epoch1);
		usedByRandomization.add(usedByarm1epoch1);
		assertEquals(usedByRandomization, randomization.getUsedBy());

		// adding again should not change anything
		d_empty.setStudyActivityAt(arm1, epoch1, randomization);
		assertEquals(usedByRandomization, randomization.getUsedBy());

		// adding new UsedBy should change UsedBy
		d_empty.addArm(arm2);
		d_empty.setStudyActivityAt(arm2, epoch1, randomization);
		UsedBy usedByarm2epoch1 = new UsedBy(arm2, epoch1);
		usedByRandomization.add(usedByarm2epoch1);
		assertEquals(usedByRandomization, randomization.getUsedBy());
		
		// adding new activity for an (arm, epoch) pair should remove any other activity at those coordinates
		StudyActivity screening = new StudyActivity("Screening", PredefinedActivity.SCREENING);
		d_empty.getStudyActivities().add(screening);
		d_empty.setStudyActivityAt(arm1, epoch1, screening);		
		Set<UsedBy> usedByScreening= new HashSet<UsedBy>();
		usedByScreening.add(usedByarm1epoch1);
		usedByRandomization.remove(usedByarm1epoch1);
		assertEquals(usedByScreening, screening.getUsedBy());
		assertEquals(usedByRandomization, randomization.getUsedBy());
		
		// adding <null> activity should clear item
		d_empty.setStudyActivityAt(arm2, epoch1, null);
		assertEquals(Collections.emptySet(), randomization.getUsedBy());
	}
	
	@Test
	public void testGetStudyActivityAt() throws DatatypeConfigurationException {
		Arm arm1 = new Arm("testArm1", 100);
		Arm arm2 = new Arm("testArm2", 200);
		Arm arm3 = new Arm("testArm3", 300);
		Epoch epoch1 = new Epoch("Trias", DatatypeFactory.newInstance().newDuration(10000));
		Epoch epoch2 = new Epoch("Jura", DatatypeFactory.newInstance().newDuration(10000));
		StudyActivity randomization = new StudyActivity("Randomization", PredefinedActivity.RANDOMIZATION);
		StudyActivity skriening = new StudyActivity("Screening", PredefinedActivity.SCREENING);
		d_empty.getEpochs().add(epoch1);
		d_empty.getEpochs().add(epoch2);
		d_empty.addArm(arm1);
		d_empty.addArm(arm2);
		d_empty.getStudyActivities().add(randomization);
		d_empty.getStudyActivities().add(skriening);
		d_empty.setStudyActivityAt(arm1, epoch1, randomization);
		d_empty.setStudyActivityAt(arm2, epoch1, randomization);
		d_empty.setStudyActivityAt(arm2, epoch1, skriening);
		d_empty.setStudyActivityAt(arm1, epoch2, skriening);
		
		assertEquals(randomization, d_empty.getStudyActivityAt(arm1, epoch1));
		assertEquals(skriening, d_empty.getStudyActivityAt(arm2, epoch1));
		assertEquals(skriening, d_empty.getStudyActivityAt(arm1, epoch2));
		assertEquals(null, d_empty.getStudyActivityAt(arm3, epoch1));
	}
	
	@Test (expected=IllegalArgumentException.class)
	public void testArmNotExistsException() throws DatatypeConfigurationException {
		Arm arm1 = new Arm("testArm1", 100);
		Epoch epoch1 = new Epoch("testEpoch1", DatatypeFactory.newInstance().newDuration(10000));
		StudyActivity randomization = new StudyActivity("Randomization", PredefinedActivity.RANDOMIZATION);
		d_empty.getEpochs().add(epoch1);
		d_empty.getStudyActivities().add(randomization);
		d_empty.setStudyActivityAt(arm1, epoch1, randomization);
	}

	@Test (expected=IllegalArgumentException.class)
	public void testEpochNotExistsException() throws DatatypeConfigurationException {
		Arm arm1 = new Arm("testArm1", 100);
		Epoch epoch1 = new Epoch("testEpoch1", DatatypeFactory.newInstance().newDuration(10000));
		StudyActivity randomization = new StudyActivity("Randomization", PredefinedActivity.RANDOMIZATION);
		d_empty.addArm(arm1);
		d_empty.getStudyActivities().add(randomization);
		d_empty.setStudyActivityAt(arm1, epoch1, randomization);
	}

	@Test (expected=IllegalArgumentException.class)
	public void testStudyActivityNotExistsException() throws DatatypeConfigurationException {
		Arm arm1 = new Arm("testArm1", 100);
		Epoch epoch1 = new Epoch("testEpoch1", DatatypeFactory.newInstance().newDuration(10000));
		StudyActivity randomization = new StudyActivity("Randomization", PredefinedActivity.RANDOMIZATION);
		d_empty.addArm(arm1);
		d_empty.getEpochs().add(epoch1);
		d_empty.setStudyActivityAt(arm1, epoch1, randomization);
	}

	@Test
	public void testSetMeasurement() {
		Study study = new Study("X", new Indication(0L, ""));
		Endpoint endpoint = new Endpoint("e", Variable.Type.RATE);
		study.addEndpoint(endpoint);
		Arm group = new Arm("", 100, null, null);
		study.addArm(group);
		BasicRateMeasurement m = new BasicRateMeasurement(0, group.getSize());
		m.setRate(12);
		study.setMeasurement(study.getOutcomeMeasures().iterator().next(), study.getArms().get(0), m);
		
		assertEquals(m, study.getMeasurement(study.getOutcomeMeasures().iterator().next(), study.getArms().get(0)));
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testSetMeasurementThrowsException1() {
		Study study = new Study("X", new Indication(0L, ""));
		Endpoint e = new Endpoint("E", Variable.Type.RATE);
		Arm pg = new Arm("", 100, null, null);
		study.setMeasurement(e, pg, 
				new BasicRateMeasurement(100, pg.getSize()));
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testSetMeasurementThrowsException2() {
		Study study = new Study("X", new Indication(0L, ""));
		Endpoint e = new Endpoint("e", Variable.Type.RATE);
		study.addEndpoint(e);
		Arm group = new Arm("", 100, null, null);
		study.addArm(group);
		
		BasicMeasurement m = new BasicRateMeasurement(12, group.getSize());
		
		study.getOutcomeMeasures().iterator().next().setType(Variable.Type.CONTINUOUS);
		study.setMeasurement(study.getOutcomeMeasures().iterator().next(), study.getArms().get(0), m);
	}
	
	
	@Test
	public void testEquals() {
		String name1 = "Study A";
		String name2 = "Study B";
		Indication i = new Indication(0L, "");
		
		assertEquals(new Study(name1, i), new Study(name1, i));
		JUnitUtil.assertNotEquals(new Study(name1, i), new Study(name2, i));
		assertEquals(new Study(name1, i).hashCode(), new Study(name1, i).hashCode());
	}
	
	@Test
	public void testGetDependencies() {
		Study s = ExampleData.buildStudyDeWilde();
		assertFalse(s.getOutcomeMeasures().isEmpty());
		assertFalse(s.getDrugs().isEmpty());
		
		Set<Entity> dep = new HashSet<Entity>(s.getOutcomeMeasures());
		dep.addAll(s.getDrugs());
		dep.add(s.getIndication());
		assertEquals(dep, s.getDependencies());
	}	
	
	@Test
	public void testSetCharacteristic() {
		Study study = new Study("X", new Indication(0L, ""));
		
		PropertyChangeListener listener = JUnitUtil.mockStrictListener(study.getCharacteristics(), 
				MapBean.PROPERTY_CONTENTS,null, null);		
		study.getCharacteristics().addPropertyChangeListener(listener);

		study.setCharacteristic(BasicStudyCharacteristic.CENTERS, new Integer(2));
		verify(listener);
	}
	
	@Test
	public void testSetCharacteristicKeepsNotes() {
		Study study = new Study("X", new Indication(0L, ""));
		study.setCharacteristic(BasicStudyCharacteristic.TITLE, null);
		Note note = new Note(Source.MANUAL, "My text");
		study.getCharacteristicWithNotes(BasicStudyCharacteristic.TITLE).getNotes().add(note);
		study.setCharacteristic(BasicStudyCharacteristic.TITLE, "My title");
		assertEquals(Collections.singletonList(note), study.getCharacteristicWithNotes(BasicStudyCharacteristic.TITLE).getNotes());
	}
	
	@Test
	public void testGetSampleSize() {
		Arm pg1 = new Arm("pg1", 25, null, null);
		Arm pg2 = new Arm("pg2", 35, null, null);
		Study s = new Study("s1", new Indication(01L, "i"));
		s.addArm(pg1);
		s.addArm(pg2);
		assertEquals(60, s.getSampleSize());
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testSetPopulationCharNotPresent() {
		Variable v = new ContinuousPopulationCharacteristic("Age");
		Study s = new Study("X", new Indication(0L, "Y"));
		s.setMeasurement(v, new BasicContinuousMeasurement(0.0, 1.0, 5));
	}
	
	@Test
	public void testSetPopulationChar() {
		PopulationCharacteristic v = new ContinuousPopulationCharacteristic("Age");
		Study s = new Study("X", new Indication(0L, "Y"));
		s.addArm(new Arm("X", 200, new Drug("X", "ATC3"), new FixedDose(5, SIUnit.MILLIGRAMS_A_DAY)));
		s.setPopulationCharacteristics(Collections.singletonList(v));
		BasicContinuousMeasurement m = new BasicContinuousMeasurement(0.0, 1.0, 5);
		
		s.setMeasurement(v, m);
		assertEquals(m, s.getMeasurement(v));
		
		s.setMeasurement(v, s.getArms().get(0), m);
		assertEquals(m, s.getMeasurement(v, s.getArms().get(0)));
	}
	
	@Test
	public void testAddPopulationCharDefaultMeasurements() {
		PopulationCharacteristic v = new ContinuousPopulationCharacteristic("Age");
		Study s = new Study("X", new Indication(0L, "Y"));
		Arm arm1 = new Arm("arm1", 200, new Drug("X", "ATC3"), new FixedDose(5, SIUnit.MILLIGRAMS_A_DAY));
		s.addArm(arm1);
		Arm arm2 = new Arm("arm2", 100, new Drug("X", "ATC3"), new FixedDose(5, SIUnit.MILLIGRAMS_A_DAY));
		s.addArm(arm2);
		s.setPopulationCharacteristics(Collections.singletonList(v));
		
		s.initializeDefaultMeasurements();
		assertEquals(300, (int)s.getMeasurement(v).getSampleSize());
		assertEquals(200, (int)s.getMeasurement(v, arm1).getSampleSize());
		assertEquals(100, (int)s.getMeasurement(v, arm2).getSampleSize());
	}
	
	@Test
	public void testChangePopulationCharRetainMeasurements() {
		Study s = new Study("X", new Indication(0L, "Y"));
		Arm arm1 = new Arm("X", 200, new Drug("X", "ATC3"), new FixedDose(5, SIUnit.MILLIGRAMS_A_DAY));
		s.addArm(arm1);
		
		PopulationCharacteristic v1 = new ContinuousPopulationCharacteristic("Age1");
		PopulationCharacteristic v2 = new ContinuousPopulationCharacteristic("Age2");
		PopulationCharacteristic v3 = new ContinuousPopulationCharacteristic("Age3");
		
		ArrayList<PopulationCharacteristic> vars1 = new ArrayList<PopulationCharacteristic>();
		vars1.add(v1);
		vars1.add(v2);
		s.setPopulationCharacteristics(vars1);
		
		Measurement m10 = new BasicContinuousMeasurement(3.0, 2.0, 150);
		Measurement m11 = new BasicContinuousMeasurement(3.0, 2.0, 150);
		Measurement m20 = new BasicContinuousMeasurement(3.0, 2.0, 150);
		Measurement m21 = new BasicContinuousMeasurement(3.0, 2.0, 150);
		s.setMeasurement(v1, m10);
		s.setMeasurement(v1, arm1, m11);
		s.setMeasurement(v2, m20);
		s.setMeasurement(v2, arm1, m21);
		
		ArrayList<PopulationCharacteristic> vars2 = new ArrayList<PopulationCharacteristic>();
		vars2.add(v2);
		vars2.add(v3);
		s.setPopulationCharacteristics(vars2);
		s.initializeDefaultMeasurements();
		
		assertEquals(m20, s.getMeasurement(v2));
		assertEquals(m21, s.getMeasurement(v2, arm1));
		assertEquals(200, (int)s.getMeasurement(v3).getSampleSize());
		assertEquals(200, (int)s.getMeasurement(v3, arm1).getSampleSize());
		
		s.setPopulationCharacteristics(vars1);
		s.initializeDefaultMeasurements();
		assertEquals(200, (int)s.getMeasurement(v1).getSampleSize());
		assertEquals(200, (int)s.getMeasurement(v1, arm1).getSampleSize());
	}

	@Test
	public void testCloneReturnsEqualEntity() {
		assertEquals(d_orig, d_clone);
		AssertEntityEquals.assertEntityEquals(d_orig, d_clone);
	}
	
	@Test
	public void testCloneReturnsDistinctObject() {
		assertFalse(d_orig == d_clone);
	}
	
	@Test
	public void testCloneReturnsDistinctArms() {
		assertFalse(d_orig.getArms() == d_clone.getArms());
		for (int i = 0; i < d_orig.getArms().size(); ++i) {
			assertFalse(d_orig.getArms().get(i) == d_clone.getArms().get(i));
		}
	}
	
	@Test
	public void testCloneReturnsDistinctVariableLists() {
		assertFalse(d_orig.getEndpoints() == d_clone.getEndpoints());
		assertFalse(d_orig.getAdverseEvents() == d_clone.getAdverseEvents());
		assertFalse(d_orig.getPopulationCharacteristics() == d_clone.getPopulationCharacteristics());
	}
	
	@Test
	public void testCloneReturnsDistinctMeasurements() {
		assertFalse(d_orig.getMeasurements() == d_clone.getMeasurements());
		for (MeasurementKey key : d_orig.getMeasurements().keySet()) {
			assertFalse(d_orig.getMeasurements().get(key) == d_clone.getMeasurements().get(key));
		}
	}
	
	@Test
	public void testCloneHasCorrectMeasurementKeys() {
		Arm arm = d_clone.getArms().get(1);
		arm.setDrug(ExampleData.buildDrugViagra());
		assertEquals(d_orig.getMeasurement(d_orig.getEndpoints().get(0), d_orig.getArms().get(1)),
				d_clone.getMeasurement(d_clone.getEndpoints().get(0), arm));
	}
	
	@Test
	public void testCloneHasDistinctCharacteristics() {
		assertFalse(d_orig.getCharacteristics() == d_clone.getCharacteristics());
	}
	
	@Test
	public void testCloneHasDistinctNotes() {
		Note note = new Note(Source.MANUAL);
		
		assertTrue(d_clone.getStudyEndpoints().get(0).getNotes().isEmpty());
		d_clone.getStudyEndpoints().get(0).getNotes().add(note);
		assertTrue(d_orig.getStudyEndpoints().get(0).getNotes().isEmpty());
		
		assertTrue(d_clone.getIndicationWithNotes().getNotes().isEmpty());
		d_clone.getIndicationWithNotes().getNotes().add(note);
		assertTrue(d_orig.getIndicationWithNotes().getNotes().isEmpty());
		
		assertTrue(d_clone.getCharacteristicWithNotes(BasicStudyCharacteristic.BLINDING).getNotes().isEmpty());
		d_clone.getCharacteristicWithNotes(BasicStudyCharacteristic.BLINDING).getNotes().add(note);
		assertTrue(d_orig.getCharacteristicWithNotes(BasicStudyCharacteristic.BLINDING).getNotes().isEmpty());
		
		assertTrue(d_clone.getArms().get(0).getNotes().isEmpty());
		d_clone.getArms().get(0).getNotes().add(note);
		assertTrue(d_orig.getArms().get(0).getNotes().isEmpty());
	}
}
