/*
 * Copyright (C) 2013 see CREDITS.txt
 * All rights reserved.
 */

package fi.uef.envi.wavellite.module.store.test;

import static fi.uef.envi.wavellite.entity.core.EntityFactory.componentProperty;
import static fi.uef.envi.wavellite.entity.core.EntityFactory.componentPropertyValue;
import static fi.uef.envi.wavellite.entity.core.EntityFactory.dataset;
import static fi.uef.envi.wavellite.entity.core.EntityFactory.dateTime;
import static fi.uef.envi.wavellite.entity.core.EntityFactory.interval;
import static fi.uef.envi.wavellite.entity.core.EntityFactory.sensor;
import static fi.uef.envi.wavellite.entity.core.EntityFactory.property;
import static fi.uef.envi.wavellite.entity.core.EntityFactory.feature;
import static fi.uef.envi.wavellite.entity.core.EntityFactory.sensorOutput;
import static fi.uef.envi.wavellite.entity.core.EntityFactory.temporalLocation;
import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.openrdf.util.iterators.Iterators;

import fi.uef.envi.wavellite.entity.core.base.TemporalLocationDateTime;
import fi.uef.envi.wavellite.entity.derivation.DatasetObservation;
import fi.uef.envi.wavellite.entity.derivation.base.ComponentPropertyValueTemporalLocation;
import fi.uef.envi.wavellite.entity.derivation.base.DatasetObservationBase;
import fi.uef.envi.wavellite.entity.observation.SensorObservation;
import fi.uef.envi.wavellite.entity.observation.base.SensorObservationBase;
import fi.uef.envi.wavellite.module.store.ModuleStore;
import fi.uef.envi.wavellite.module.store.sesame.ModuleStoreSail;
import fi.uef.envi.wavellite.module.store.stardog.ModuleStoreStardog;
import fi.uef.envi.wavellite.vocabulary.SDMX;

/**
 * <p>
 * Title: ModuleStoreTest
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Project: Wavellite Module
 * </p>
 * <p>
 * Copyright: Copyright (C) 2013
 * </p>
 * 
 * @author Markus Stocker
 */

public class ModuleStoreTest {

	private static ModuleStore store;
	private static final DateTimeFormatter dtf = ISODateTimeFormat.dateTime()
			.withOffsetParsed();

	@BeforeClass
	public static void beforeClass() {
//		store = new ModuleStoreStardog("localhost", "test",
//				"http://example.org#");
		store = new ModuleStoreSail("http://example.org#");

		DateTime start = dtf.parseDateTime("2014-01-01T00:00:00.000+02:00");

		// Ten days
		for (int i = 0; i < 240; i++) {
			SensorObservation o = new SensorObservationBase();
			o.setSensor(sensor("s1"));
			o.setProperty(property("p1"));
			o.setFeature(feature("f1"));
			o.setSensorOutput(sensorOutput(0.0));
			o.setTemporalLocation(temporalLocation(start.plusHours(i)));

			store.consider(o);
		}
	}

	@AfterClass
	public static void afterClass() {
		store.close();
	}

	@Test
	public void test1() {
		Iterator<SensorObservation> it = store.getSensorObservations(
				sensor("http://example.org#s1"),
				null,
				null,
				interval(dateTime(2014, 1, 3, 0, 0, 0),
						dateTime(2014, 1, 3, 23, 59, 59)));

		List<SensorObservation> a = Iterators.asList(it);

		assertEquals(24, a.size());
	}

	@Ignore
	@Test
	public void test2() {
		DatasetObservation o1 = new DatasetObservationBase();
		o1.setDataset(dataset("d1"));
		o1.addComponent(
				componentProperty(SDMX.Dimension.timePeriod),
				new ComponentPropertyValueTemporalLocation(
						new TemporalLocationDateTime("tl1", dtf
								.parseDateTime("2014-02-14T00:00:00.000+02:00"))));
		o1.addComponent(componentProperty("cp1"), componentPropertyValue(0.0));
		store.consider(o1);

		Iterator<DatasetObservation> it = store.getDatasetObservations(
				dataset("http://example.org#d1"),
				componentProperty(SDMX.Dimension.timePeriod),
				componentPropertyValue(dateTime(dtf
						.parseDateTime("2014-02-10T00:00:00.000+02:00"))),
				componentPropertyValue(dateTime(dtf
						.parseDateTime("2014-02-15T00:00:00.000+02:00"))));

		List<DatasetObservation> a = Iterators.asList(it);

		List<DatasetObservation> e = new ArrayList<DatasetObservation>();

		DatasetObservation o2 = new DatasetObservationBase();
		o2.setDataset(dataset("http://example.org#d1"));
		o2.addComponent(
				componentProperty(SDMX.Dimension.timePeriod),
				new ComponentPropertyValueTemporalLocation(
						new TemporalLocationDateTime(
								"http://example.org#tl1",
								dtf.parseDateTime("2014-02-14T00:00:00.000+02:00"))));
		o2.addComponent(componentProperty("http://example.org#cp1"),
				componentPropertyValue(0.0));
		e.add(o2);

		assertEquals(e, a);
	}

}
