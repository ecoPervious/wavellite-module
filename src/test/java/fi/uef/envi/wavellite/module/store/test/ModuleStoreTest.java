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

import fi.uef.envi.wavellite.entity.derivation.DatasetObservation;
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
	private static final DateTimeFormatter dtf = ISODateTimeFormat
			.dateTimeParser().withOffsetParsed();

	private static double days = 0.01;
	private static final DateTime start = dtf
			.parseDateTime("2014-01-01T00:00:00.000+02:00");
	private static final int queryTimeIntervalInSeconds = 50;
	private static long loadTimeInSeconds = 0;

	@BeforeClass
	public static void beforeClass() {
		// store = new ModuleStoreStardog("localhost", "test",
		// "http://example.org#");
		store = new ModuleStoreSail("http://example.org#");

		long t1 = System.currentTimeMillis();

		for (int i = 0; i < 60 * 60 * 24 * days; i++) {
			SensorObservation o1 = new SensorObservationBase();
			o1.setSensor(sensor("s1"));
			o1.setProperty(property("p1"));
			o1.setFeature(feature("f1"));
			o1.setSensorOutput(sensorOutput(Integer.valueOf(i).doubleValue()));
			o1.setTemporalLocation(temporalLocation(start.plusSeconds(i)));
			store.consider(o1);

			DatasetObservation o2 = new DatasetObservationBase();
			o2.setDataset(dataset("d1"));
			o2.addComponent(componentProperty(SDMX.Dimension.timePeriod),
					componentPropertyValue(start.plusSeconds(i)));
			o2.addComponent(componentProperty("cp1"), componentPropertyValue(i));
			store.consider(o2);
		}

		long t2 = System.currentTimeMillis();

		loadTimeInSeconds = (t2 - t1) / 1000;
	}

	@AfterClass
	public static void afterClass() {
		System.out.println("Load time: " + loadTimeInSeconds + " [s]");
		System.out.println("Store size: " + store.size());

		store.close();
	}

	@Test
	@Ignore
	public void test1a() {
		Iterator<SensorObservation> it = store
				.getSensorObservations(
						sensor("http://example.org#s1"),
						null,
						null,
						interval(dateTime(start.plusSeconds(1)), dateTime(start
								.plusSeconds(queryTimeIntervalInSeconds))));

		List<SensorObservation> a = Iterators.asList(it);

		assertEquals(queryTimeIntervalInSeconds, a.size());
	}

	@Test
	@Ignore
	public void test1b() {
		Iterator<SensorObservation> it = store.getSensorObservations(
				sensor("http://example.org#s1"),
				null,
				null,
				interval(
						dateTime(start.minusYears(1).plusSeconds(1)),
						dateTime(start.minusYears(1).plusSeconds(
								queryTimeIntervalInSeconds))));

		List<SensorObservation> a = Iterators.asList(it);

		assertEquals(0, a.size());
	}

	@Test
	@Ignore
	public void test2a() {
		Iterator<DatasetObservation> it = store.getDatasetObservations(
				dataset("http://example.org#d1"),
				componentProperty(SDMX.Dimension.timePeriod),
				componentPropertyValue(dateTime(start.plusSeconds(1))),
				componentPropertyValue(dateTime(start
						.plusSeconds(queryTimeIntervalInSeconds))));

		List<DatasetObservation> a = Iterators.asList(it);

		assertEquals(queryTimeIntervalInSeconds, a.size());
	}

	@Test
	@Ignore
	public void test2b() {
		Iterator<DatasetObservation> it = store.getDatasetObservations(
				dataset("http://example.org#d1"),
				componentProperty(SDMX.Dimension.timePeriod),
				componentPropertyValue(dateTime(start.minusYears(1)
						.plusSeconds(1))),
				componentPropertyValue(dateTime(start.minusYears(1)
						.plusSeconds(queryTimeIntervalInSeconds))));

		List<DatasetObservation> a = Iterators.asList(it);

		assertEquals(0, a.size());
	}

}
