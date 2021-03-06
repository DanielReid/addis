/*
 * This file is part of ADDIS (Aggregate Data Drug Information System).
 * ADDIS is distributed from http://drugis.org/.
 * Copyright © 2009 Gert van Valkenhoef, Tommi Tervonen.
 * Copyright © 2010 Gert van Valkenhoef, Tommi Tervonen, Tijs Zwinkels,
 * Maarten Jacobs, Hanno Koeslag, Florin Schimbinschi, Ahmad Kamal, Daniel
 * Reid.
 * Copyright © 2011 Gert van Valkenhoef, Ahmad Kamal, Daniel Reid, Florin
 * Schimbinschi.
 * Copyright © 2012 Gert van Valkenhoef, Daniel Reid, Joël Kuiper, Wouter
 * Reckman.
 * Copyright © 2013 Gert van Valkenhoef, Joël Kuiper.
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

package org.drugis.addis.util.jaxb;

import java.io.IOException;
import java.io.InputStream;

import javax.xml.bind.JAXBException;
import javax.xml.transform.TransformerException;

import org.drugis.addis.util.jaxb.JAXBConvertor.ConversionException;
import org.junit.Before;
import org.junit.Test;
import org.xml.sax.SAXException;

public class TreatmentCategorizationsConverterTest {

	private JAXBConvertorTest d_jaxbConverterTest;
	private static final String TEST_DATA = JAXBConvertorTest.TEST_DATA_PATH + "testDataWithTreatmentCategories.addis";

	@Before 
	public void setUp() throws JAXBException { 
		d_jaxbConverterTest = new JAXBConvertorTest();
		d_jaxbConverterTest.setup();
	}

	@Test
	public void testRoundTripConversion() throws JAXBException, ConversionException, SAXException, IOException, TransformerException {
		d_jaxbConverterTest.doRoundTripTest(getTransformedSavedResultsData(), false);

	}
	
	private static InputStream getTransformedSavedResultsData() throws TransformerException, IOException {
		return JAXBConvertorTest.getTestData(TEST_DATA);
	}
}
