package org.drugis.addis.presentation;

import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;

import java.beans.PropertyChangeListener;

import org.drugis.addis.entities.BasicPatientGroup;
import org.drugis.addis.entities.BasicRateMeasurement;
import org.drugis.addis.entities.Endpoint;
import org.drugis.common.JUnitUtil;
import org.junit.Before;
import org.junit.Test;

import com.jgoodies.binding.value.AbstractValueModel;

public class RateMeasurementPresentationTest {
	private BasicRateMeasurement d_measurement;
	private Endpoint d_endpoint;
	private BasicPatientGroup d_pg;
	private RateMeasurementPresentation d_presentation;
	
	@Before
	public void setUp() {
		d_endpoint = new Endpoint("E", Endpoint.Type.RATE);
		d_pg = new BasicPatientGroup(null, null, 101);
		d_measurement = new BasicRateMeasurement(d_endpoint, 67, d_pg);
		d_presentation = new RateMeasurementPresentation(d_measurement);
	}

	@Test
	public void testFireLabelRateChanged() {
		AbstractValueModel lm = d_presentation.getLabelModel();
		PropertyChangeListener l = JUnitUtil.mockListener(
				lm, "value", "67/101", "68/101");
		lm.addPropertyChangeListener(l);
		d_measurement.setRate(68);
		verify(l);
	}
		
	@Test
	public void testFireLabelSizeChanged() {
		AbstractValueModel lm = d_presentation.getLabelModel();
		PropertyChangeListener l = JUnitUtil.mockListener(
				lm, "value", "67/101", "67/102");
		lm.addPropertyChangeListener(l);
		d_pg.setSize(102);
		verify(l);
	}	
	
	
	@Test
	public void testGetLabel() {
		assertEquals("67/101", d_presentation.getLabelModel().getValue());
	}
}
