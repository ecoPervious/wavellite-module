/*
 * Copyright (C) 2014 see CREDITS.txt
 * All rights reserved.
 */

package fi.uef.envi.wavellite.module.store.sesame.test;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.joda.time.DateTime;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import org.openrdf.util.iterators.Iterators;

import fi.uef.envi.wavellite.entity.core.base.TemporalLocationDateTime;
import fi.uef.envi.wavellite.entity.observation.SensorObservation;
import fi.uef.envi.wavellite.entity.observation.base.ObservationValueDouble;
import fi.uef.envi.wavellite.entity.observation.base.SensorObservationBase;
import fi.uef.envi.wavellite.entity.observation.base.SensorOutputBase;
import fi.uef.envi.wavellite.module.store.ModuleStore;
import fi.uef.envi.wavellite.module.store.sesame.ModuleStoreSail;
import static fi.uef.envi.wavellite.entity.core.EntityFactory.sensor;
import static fi.uef.envi.wavellite.entity.core.EntityFactory.property;
import static fi.uef.envi.wavellite.entity.core.EntityFactory.feature;
import static fi.uef.envi.wavellite.entity.core.EntityFactory.interval;
import static fi.uef.envi.wavellite.entity.core.EntityFactory.dateTime;

/**
 * <p>
 * Title: ModuleStoreSailTest
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Project: Wavellite Module
 * </p>
 * <p>
 * Copyright: Copyright (C) 2014
 * </p>
 * 
 * @author Markus Stocker
 */

public class ModuleStoreSailTest {

	@Test
	public void test1() {
		ModuleStore store = new ModuleStoreSail("http://example.org#");

		SensorObservation o1 = new SensorObservationBase("o1");
		o1.setSensor(sensor("s1"));
		o1.setSensorOutput(new SensorOutputBase("so1",
				new ObservationValueDouble("ov1", 0.0)));
		o1.setTemporalLocation(new TemporalLocationDateTime("tl1",
				new DateTime(2014, 2, 14, 0, 0, 0)));

		store.consider(o1);

		Iterator<SensorObservation> it = store.getSensorObservations(
				sensor("http://example.org#s1"),
				null,
				null,
				interval(dateTime(2014, 2, 10, 0, 0, 0),
						dateTime(2014, 2, 15, 0, 0, 0)));

		List<SensorObservation> a = Iterators.asList(it);

		List<SensorObservation> e = new ArrayList<SensorObservation>();

		SensorObservation o2 = new SensorObservationBase(
				"http://example.org#o1");
		e.add(o2);

		assertEquals(e, a);

		store.close();
	}

	@Test
	public void test2() {
		ModuleStore store = new ModuleStoreSail("http://example.org#");

		SensorObservation o1 = new SensorObservationBase("o1");
		o1.setSensor(sensor("s1"));
		o1.setProperty(property("p1"));
		o1.setFeature(feature("f1"));
		o1.setSensorOutput(new SensorOutputBase("so1",
				new ObservationValueDouble("ov1", 0.0)));
		o1.setTemporalLocation(new TemporalLocationDateTime("tl1",
				new DateTime(2014, 2, 14, 0, 0, 0)));

		store.consider(o1);

		Iterator<SensorObservation> it = store.getSensorObservations(
				sensor("http://example.org#s1"),
				property("http://example.org#p1"),
				feature("http://example.org#f1"),
				interval(dateTime(2014, 2, 10, 0, 0, 0),
						dateTime(2014, 2, 15, 0, 0, 0)));

		List<SensorObservation> a = Iterators.asList(it);

		List<SensorObservation> e = new ArrayList<SensorObservation>();

		SensorObservation o2 = new SensorObservationBase(
				"http://example.org#o1");
		e.add(o2);

		assertEquals(e, a);

		store.close();
	}
}
