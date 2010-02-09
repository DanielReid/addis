package org.drugis.addis.presentation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import org.drugis.addis.ExampleData;
import org.drugis.addis.entities.DomainImpl;
import org.drugis.addis.entities.Endpoint;
import org.drugis.addis.entities.OutcomeMeasure;
import org.drugis.addis.entities.Study;
import org.junit.Before;
import org.junit.Test;

public class MeasurementTableModelTest {

	private Study d_standardStudy;
	private PresentationModelFactory d_pmf;
	private MeasurementTableModel model;
	
	@Before
	public void setUp() {
		DomainImpl domain = new DomainImpl();
		ExampleData.initDefaultData(domain);
		d_pmf = new PresentationModelFactory(domain);
		d_standardStudy = ExampleData.buildStudyDeWilde();
		model = new MeasurementTableModel(d_standardStudy, d_pmf, Endpoint.class);
	}
	
	@Test
	public void testGetColumnCount() {
		assertEquals(d_standardStudy.getArms().size() + 1, model.getColumnCount());
	}

	@Test
	public void testGetRowCount() {
		assertEquals(d_standardStudy.getOutcomeMeasures().size(), model.getRowCount());
	}

	@Test
	public void testGetValueAt() {
		
		int index = 0;
		for (OutcomeMeasure m : d_standardStudy.getOutcomeMeasures()) {
			if (m instanceof Endpoint) {
				Endpoint e = (Endpoint) m;
				assertEquals(e.getName(), model.getValueAt(index, 0));
				index++;
			}
		}
		
		assertEquals(
				d_standardStudy.getMeasurement(
					d_standardStudy.getOutcomeMeasures().get(0),
					d_standardStudy.getArms().get(0)),
				model.getValueAt(0, 1));
	}
	
	@Test
	public void testGetColumnName() {
		assertEquals("Endpoint", model.getColumnName(0));
		for (int i=0;i<d_standardStudy.getArms().size();i++) {
			String exp = d_pmf.getLabeledModel(d_standardStudy.getArms().get(i)).getLabelModel().getString();
			String cname = model.getColumnName(i+1);
			assertEquals(exp, cname);
		}
	}
	
	@Test
	public void testIsCellEditable() {
		for (int i=0;i<model.getRowCount();i++) {
			for (int j=0;j<model.getColumnCount();j++) {
				assertFalse(model.isCellEditable(i, j));				
			}
		}
	}
}
