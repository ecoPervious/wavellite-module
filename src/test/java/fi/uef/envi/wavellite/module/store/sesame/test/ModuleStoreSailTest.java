/*
 * Copyright (C) 2014 see CREDITS.txt
 * All rights reserved.
 */

package fi.uef.envi.wavellite.module.store.sesame.test;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.ValueFactoryImpl;
import org.openrdf.util.iterators.Iterators;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;

import fi.uef.envi.wavellite.entity.core.base.SpatialGeometryPoint;
import fi.uef.envi.wavellite.entity.core.base.SpatialLocationPlace;
import fi.uef.envi.wavellite.entity.core.base.SpatialLocationRegion;
import fi.uef.envi.wavellite.entity.core.base.TemporalLocationDateTime;
import fi.uef.envi.wavellite.entity.core.base.TemporalLocationInterval;
import fi.uef.envi.wavellite.entity.derivation.DatasetObservation;
import fi.uef.envi.wavellite.entity.derivation.base.ComponentPropertyValueTemporalLocation;
import fi.uef.envi.wavellite.entity.derivation.base.DatasetObservationBase;
import fi.uef.envi.wavellite.entity.observation.SensorObservation;
import fi.uef.envi.wavellite.entity.observation.base.ObservationValueDouble;
import fi.uef.envi.wavellite.entity.observation.base.SensorObservationBase;
import fi.uef.envi.wavellite.entity.observation.base.SensorOutputBase;
import fi.uef.envi.wavellite.module.store.ModuleStore;
import fi.uef.envi.wavellite.module.store.sesame.ModuleStoreSail;
import fi.uef.envi.wavellite.vocabulary.SDMX;
import static fi.uef.envi.wavellite.entity.core.EntityFactory.sensor;
import static fi.uef.envi.wavellite.entity.core.EntityFactory.property;
import static fi.uef.envi.wavellite.entity.core.EntityFactory.feature;
import static fi.uef.envi.wavellite.entity.core.EntityFactory.interval;
import static fi.uef.envi.wavellite.entity.core.EntityFactory.dateTime;
import static fi.uef.envi.wavellite.entity.core.EntityFactory.dataset;
import static fi.uef.envi.wavellite.entity.core.EntityFactory.componentPropertyValue;
import static fi.uef.envi.wavellite.entity.core.EntityFactory.componentProperty;

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

	private static final ValueFactory vf = ValueFactoryImpl.getInstance();
	private static final DateTimeFormatter dtf = ISODateTimeFormat.dateTime()
			.withOffsetParsed();

	@Test
	public void test1a() {
		ModuleStore store = new ModuleStoreSail("http://example.org#");

		SensorObservation o1 = new SensorObservationBase("o1");
		o1.setSensor(sensor("s1"));
		o1.setTemporalLocation(new TemporalLocationDateTime("tl1", dtf
				.parseDateTime("2014-02-14T00:00:00.000+02:00")));

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
		o2.setSensor(sensor("http://example.org#s1"));
		o2.setTemporalLocation(new TemporalLocationDateTime("tl1", dtf
				.parseDateTime("2014-02-14T00:00:00.000+02:00")));
		e.add(o2);

		assertEquals(e, a);

		store.close();
	}

	@Test
	public void test1b() {
		ModuleStore store = new ModuleStoreSail("http://example.org#");

		SensorObservation o1 = new SensorObservationBase("o1");
		o1.setSensor(sensor("s1"));
		o1.setTemporalLocation(new TemporalLocationDateTime("tl1", dtf
				.parseDateTime("2014-02-14T00:00:00.000+02:00")));

		store.consider(o1);

		Iterator<SensorObservation> it = store.getSensorObservations(
				sensor("http://example.org#s1"),
				null,
				null,
				interval(dateTime(2014, 2, 15, 0, 0, 0),
						dateTime(2014, 2, 20, 0, 0, 0)));

		List<SensorObservation> a = Iterators.asList(it);

		assertTrue(a.isEmpty());

		store.close();
	}

	@Test
	public void test2() {
		ModuleStore store = new ModuleStoreSail("http://example.org#");

		SensorObservation o1 = new SensorObservationBase("o1");
		o1.setSensor(sensor("s1"));
		o1.setProperty(property("p1"));
		o1.setTemporalLocation(new TemporalLocationDateTime("tl1", dtf
				.parseDateTime("2014-02-14T00:00:00.000+02:00")));

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
		o2.setSensor(sensor("http://example.org#s1"));
		o2.setProperty(property("http://example.org#p1"));
		o2.setTemporalLocation(new TemporalLocationDateTime("tl1", dtf
				.parseDateTime("2014-02-14T00:00:00.000+02:00")));
		e.add(o2);

		assertEquals(e, a);

		store.close();
	}

	@Test
	public void test3() {
		ModuleStore store = new ModuleStoreSail("http://example.org#");

		SensorObservation o1 = new SensorObservationBase("o1");
		o1.setSensor(sensor("s1"));
		o1.setProperty(property("p1"));
		o1.setTemporalLocation(new TemporalLocationDateTime("tl1", dtf
				.parseDateTime("2014-02-14T00:00:00.000+02:00")));

		store.consider(o1);

		Iterator<SensorObservation> it = store.getSensorObservations(
				null,
				property("http://example.org#p1"),
				null,
				interval(dateTime(2014, 2, 10, 0, 0, 0),
						dateTime(2014, 2, 15, 0, 0, 0)));

		List<SensorObservation> a = Iterators.asList(it);

		List<SensorObservation> e = new ArrayList<SensorObservation>();

		SensorObservation o2 = new SensorObservationBase(
				"http://example.org#o1");
		o2.setSensor(sensor("http://example.org#s1"));
		o2.setProperty(property("http://example.org#p1"));
		o2.setTemporalLocation(new TemporalLocationDateTime("tl1", dtf
				.parseDateTime("2014-02-14T00:00:00.000+02:00")));
		e.add(o2);

		assertEquals(e, a);

		store.close();
	}

	@Test
	public void test4() {
		ModuleStore store = new ModuleStoreSail("http://example.org#");

		SensorObservation o1 = new SensorObservationBase("o1");
		o1.setSensor(sensor("s1"));
		o1.setProperty(property("p1"));
		o1.setFeature(feature("f1"));
		o1.setTemporalLocation(new TemporalLocationDateTime("tl1", dtf
				.parseDateTime("2014-02-14T00:00:00.000+02:00")));

		store.consider(o1);

		Iterator<SensorObservation> it = store.getSensorObservations(
				null,
				property("http://example.org#p1"),
				null,
				interval(dateTime(2014, 2, 10, 0, 0, 0),
						dateTime(2014, 2, 15, 0, 0, 0)));

		List<SensorObservation> a = Iterators.asList(it);

		List<SensorObservation> e = new ArrayList<SensorObservation>();

		SensorObservation o2 = new SensorObservationBase(
				"http://example.org#o1");
		o2.setSensor(sensor("http://example.org#s1"));
		o2.setProperty(property("http://example.org#p1"));
		o2.setFeature(feature("http://example.org#f1"));
		o2.setTemporalLocation(new TemporalLocationDateTime("tl1", dtf
				.parseDateTime("2014-02-14T00:00:00.000+02:00")));
		e.add(o2);

		assertEquals(e, a);

		store.close();
	}

	@Test
	public void test5a() {
		ModuleStore store = new ModuleStoreSail("http://example.org#");

		SensorObservation o1 = new SensorObservationBase("o1");
		o1.setSensor(sensor("s1"));
		o1.setTemporalLocation(new TemporalLocationInterval("tl1",
				new TemporalLocationDateTime("dt1", dtf
						.parseDateTime("2014-02-13T00:00:00.000+02:00")),
				new TemporalLocationDateTime("dt2", dtf
						.parseDateTime("2014-02-14T00:00:00.000+02:00"))));

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
		o2.setSensor(sensor("http://example.org#s1"));
		o2.setTemporalLocation(new TemporalLocationInterval("tl1",
				new TemporalLocationDateTime("dt1", dtf
						.parseDateTime("2014-02-13T00:00:00.000+02:00")),
				new TemporalLocationDateTime("dt2", dtf
						.parseDateTime("2014-02-14T00:00:00.000+02:00"))));
		e.add(o2);

		assertEquals(e, a);

		store.close();
	}

	@Test
	public void test5b() {
		ModuleStore store = new ModuleStoreSail("http://example.org#");

		SensorObservation o1 = new SensorObservationBase("o1");
		o1.setSensor(sensor("s1"));
		o1.setTemporalLocation(new TemporalLocationInterval("tl1",
				new TemporalLocationDateTime("dt1", dtf
						.parseDateTime("2014-02-13T00:00:00.000+02:00")),
				new TemporalLocationDateTime("dt2", dtf
						.parseDateTime("2014-02-14T00:00:00.000+02:00"))));

		store.consider(o1);

		Iterator<SensorObservation> it = store.getSensorObservations(
				sensor("http://example.org#s1"),
				null,
				null,
				interval(dateTime(2014, 2, 15, 0, 0, 0),
						dateTime(2014, 2, 20, 0, 0, 0)));

		List<SensorObservation> a = Iterators.asList(it);

		assertTrue(a.isEmpty());

		store.close();
	}

	@Test
	public void test5c() {
		ModuleStore store = new ModuleStoreSail("http://example.org#");

		SensorObservation o1 = new SensorObservationBase("o1");
		o1.setSensor(sensor("s1"));
		o1.setTemporalLocation(new TemporalLocationInterval("tl1",
				new TemporalLocationDateTime("dt1", dtf
						.parseDateTime("2014-02-13T00:00:00.000+02:00")),
				new TemporalLocationDateTime("dt2", dtf
						.parseDateTime("2014-02-14T00:00:00.000+02:00"))));

		store.consider(o1);

		Iterator<SensorObservation> it = store.getSensorObservations(
				sensor("http://example.org#s1"),
				null,
				null,
				interval(dateTime(2014, 2, 13, 12, 0, 0),
						dateTime(2014, 2, 14, 12, 0, 0)));

		List<SensorObservation> a = Iterators.asList(it);

		assertTrue(a.isEmpty());

		store.close();
	}

	@Test
	public void test5d() {
		ModuleStore store = new ModuleStoreSail("http://example.org#");

		SensorObservation o1 = new SensorObservationBase("o1");
		o1.setSensor(sensor("s1"));
		o1.setTemporalLocation(new TemporalLocationInterval("tl1",
				new TemporalLocationDateTime("dt1", dtf
						.parseDateTime("2014-02-13T00:00:00.000+02:00")),
				new TemporalLocationDateTime("dt2", dtf
						.parseDateTime("2014-02-14T00:00:00.000+02:00"))));

		store.consider(o1);

		Iterator<SensorObservation> it = store.getSensorObservations(
				sensor("http://example.org#s1"),
				null,
				null,
				interval(dateTime(2014, 2, 12, 12, 0, 0),
						dateTime(2014, 2, 13, 12, 0, 0)));

		List<SensorObservation> a = Iterators.asList(it);

		assertTrue(a.isEmpty());

		store.close();
	}

	@Test
	public void test6() {
		ModuleStore store = new ModuleStoreSail("http://example.org#");

		SensorObservation o1 = new SensorObservationBase("o1");
		o1.setSensor(sensor("s1"));
		o1.setProperty(property("p1"));
		o1.setFeature(feature("f1"));
		o1.setSensorOutput(new SensorOutputBase("so1",
				new ObservationValueDouble("ov1", 0.0)));
		o1.setTemporalLocation(new TemporalLocationDateTime("tl1", dtf
				.parseDateTime("2014-02-14T00:00:00.000+02:00")));

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
		o2.setSensor(sensor("http://example.org#s1"));
		o2.setProperty(property("http://example.org#p1"));
		o2.setFeature(feature("http://example.org#f1"));
		o2.setSensorOutput(new SensorOutputBase("http://example.org#so1",
				new ObservationValueDouble("http://example.org#ov1", 0.0)));
		o2.setTemporalLocation(new TemporalLocationDateTime(
				"http://example.org#tl1", dtf
						.parseDateTime("2014-02-14T00:00:00.000+02:00")));
		e.add(o2);

		assertEquals(e, a);

		store.close();
	}

	@Test
	public void test7a() {
		ModuleStore store = new ModuleStoreSail("http://example.org#");

		GeometryFactory gf = new GeometryFactory();
		Point p1 = gf.createPoint(new Coordinate(0.0, 0.0));

		SensorObservation o1 = new SensorObservationBase("o1");
		o1.setSensor(sensor("s1"));
		o1.setSensorOutput(new SensorOutputBase("so1",
				new ObservationValueDouble("ov1", 0.0)));
		o1.setTemporalLocation(new TemporalLocationDateTime("tl1", dtf
				.parseDateTime("2014-02-14T00:00:00.000+02:00")));
		o1.setSpatialLocation(new SpatialLocationRegion("sl1",
				new SpatialGeometryPoint("p1", p1)));

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
		o2.setSensor(sensor("http://example.org#s1"));
		o2.setSensorOutput(new SensorOutputBase("http://example.org#so1",
				new ObservationValueDouble("http://example.org#ov1", 0.0)));
		o2.setTemporalLocation(new TemporalLocationDateTime(
				"http://example.org#tl1", dtf
						.parseDateTime("2014-02-14T00:00:00.000+02:00")));
		o2.setSpatialLocation(new SpatialLocationRegion(
				"http://example.org#sl1", new SpatialGeometryPoint(
						"http://example.org#p1", p1)));
		e.add(o2);

		assertEquals(e, a);

		store.close();
	}

	@Test
	public void test7b() {
		ModuleStore store = new ModuleStoreSail("http://example.org#");

		SensorObservation o1 = new SensorObservationBase("o1");
		o1.setSensor(sensor("s1"));
		o1.setSensorOutput(new SensorOutputBase("so1",
				new ObservationValueDouble("ov1", 0.0)));
		o1.setTemporalLocation(new TemporalLocationDateTime("tl1", dtf
				.parseDateTime("2014-02-14T00:00:00.000+02:00")));
		o1.setSpatialLocation(new SpatialLocationPlace("sl1", vf
				.createURI("http://geonames.org#sl1"), "sl1"));

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
		o2.setSensor(sensor("http://example.org#s1"));
		o2.setSensorOutput(new SensorOutputBase("http://example.org#so1",
				new ObservationValueDouble("http://example.org#ov1", 0.0)));
		o2.setTemporalLocation(new TemporalLocationDateTime(
				"http://example.org#tl1", dtf
						.parseDateTime("2014-02-14T00:00:00.000+02:00")));
		o2.setSpatialLocation(new SpatialLocationPlace(
				"http://example.org#sl1", vf
						.createURI("http://geonames.org#sl1"), "sl1"));
		e.add(o2);

		assertEquals(e, a);

		store.close();
	}

	@Test
	public void test8a() {
		ModuleStore store = new ModuleStoreSail("http://example.org#");

		DatasetObservation o1 = new DatasetObservationBase("o1");
		o1.setDataset(dataset("d1"));
		o1.addComponent(
				componentProperty(SDMX.Dimension.timePeriod),
				new ComponentPropertyValueTemporalLocation(
						new TemporalLocationDateTime("dt1", dtf
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

		DatasetObservation o2 = new DatasetObservationBase(
				"http://example.org#o1");
		o2.setDataset(dataset("http://example.org#d1"));
		o2.addComponent(
				componentProperty(SDMX.Dimension.timePeriod),
				new ComponentPropertyValueTemporalLocation(
						new TemporalLocationDateTime(
								"http://example.org#dt1",
								dtf.parseDateTime("2014-02-14T00:00:00.000+02:00"))));
		o2.addComponent(componentProperty("http://example.org#cp1"),
				componentPropertyValue(0.0));
		e.add(o2);

		assertEquals(e, a);

		store.close();
	}

	@Test
	public void test8b() {
		ModuleStore store = new ModuleStoreSail("http://example.org#");

		DatasetObservation o1 = new DatasetObservationBase("o1");
		o1.setDataset(dataset("d1"));
		o1.addComponent(
				componentProperty(SDMX.Dimension.timePeriod),
				new ComponentPropertyValueTemporalLocation(
						new TemporalLocationDateTime("dt1", dtf
								.parseDateTime("2014-02-14T00:00:00.000+02:00"))));
		o1.addComponent(componentProperty("cp1"), componentPropertyValue(0.0));
		store.consider(o1);

		Iterator<DatasetObservation> it = store.getDatasetObservations(
				dataset("http://example.org#d1"),
				componentProperty(SDMX.Dimension.timePeriod),
				componentPropertyValue(dateTime(dtf
						.parseDateTime("2014-02-15T00:00:00.000+02:00"))),
				componentPropertyValue(dateTime(dtf
						.parseDateTime("2014-02-20T00:00:00.000+02:00"))));

		List<DatasetObservation> a = Iterators.asList(it);

		assertTrue(a.isEmpty());

		store.close();
	}

	@Test
	public void test9a() {
		ModuleStore store = new ModuleStoreSail("http://example.org#");

		DatasetObservation o1 = new DatasetObservationBase("o1");
		o1.setDataset(dataset("d1"));
		o1.addComponent(
				componentProperty(SDMX.Dimension.timePeriod),
				new ComponentPropertyValueTemporalLocation(
						new TemporalLocationInterval(
								"tl1",
								new TemporalLocationDateTime(
										"dt1",
										dtf.parseDateTime("2014-02-13T00:00:00.000+02:00")),
								new TemporalLocationDateTime(
										"dt2",
										dtf.parseDateTime("2014-02-14T00:00:00.000+02:00")))));
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

		DatasetObservation o2 = new DatasetObservationBase(
				"http://example.org#o1");
		o2.setDataset(dataset("http://example.org#d1"));
		o2.addComponent(
				componentProperty(SDMX.Dimension.timePeriod),
				new ComponentPropertyValueTemporalLocation(
						new TemporalLocationInterval(
								"http://example.org#tl1",
								new TemporalLocationDateTime(
										"http://example.org#dt1",
										dtf.parseDateTime("2014-02-13T00:00:00.000+02:00")),
								new TemporalLocationDateTime(
										"http://example.org#dt2",
										dtf.parseDateTime("2014-02-14T00:00:00.000+02:00")))));
		o2.addComponent(componentProperty("http://example.org#cp1"),
				componentPropertyValue(0.0));
		e.add(o2);

		assertEquals(e, a);

		store.close();
	}

	@Test
	public void test9b() {
		ModuleStore store = new ModuleStoreSail("http://example.org#");

		DatasetObservation o1 = new DatasetObservationBase("o1");
		o1.setDataset(dataset("d1"));
		o1.addComponent(
				componentProperty(SDMX.Dimension.timePeriod),
				new ComponentPropertyValueTemporalLocation(
						new TemporalLocationInterval(
								"tl1",
								new TemporalLocationDateTime(
										"dt1",
										dtf.parseDateTime("2014-02-13T00:00:00.000+02:00")),
								new TemporalLocationDateTime(
										"dt2",
										dtf.parseDateTime("2014-02-14T00:00:00.000+02:00")))));
		o1.addComponent(componentProperty("cp1"), componentPropertyValue(0.0));
		store.consider(o1);

		Iterator<DatasetObservation> it = store.getDatasetObservations(
				dataset("http://example.org#d1"),
				componentProperty(SDMX.Dimension.timePeriod),
				componentPropertyValue(dateTime(dtf
						.parseDateTime("2014-02-10T00:00:00.000+02:00"))),
				componentPropertyValue(dateTime(dtf
						.parseDateTime("2014-02-13T12:00:00.000+02:00"))));

		List<DatasetObservation> a = Iterators.asList(it);

		assertTrue(a.isEmpty());

		store.close();
	}

	@Test
	public void test9c() {
		ModuleStore store = new ModuleStoreSail("http://example.org#");

		DatasetObservation o1 = new DatasetObservationBase("o1");
		o1.setDataset(dataset("d1"));
		o1.addComponent(
				componentProperty(SDMX.Dimension.timePeriod),
				new ComponentPropertyValueTemporalLocation(
						new TemporalLocationInterval(
								"tl1",
								new TemporalLocationDateTime(
										"dt1",
										dtf.parseDateTime("2014-02-13T00:00:00.000+02:00")),
								new TemporalLocationDateTime(
										"dt2",
										dtf.parseDateTime("2014-02-14T00:00:00.000+02:00")))));
		o1.addComponent(componentProperty("cp1"), componentPropertyValue(0.0));
		store.consider(o1);

		Iterator<DatasetObservation> it = store.getDatasetObservations(
				dataset("http://example.org#d1"),
				componentProperty(SDMX.Dimension.timePeriod),
				componentPropertyValue(dateTime(dtf
						.parseDateTime("2014-02-13T12:00:00.000+02:00"))),
				componentPropertyValue(dateTime(dtf
						.parseDateTime("2014-02-15T00:00:00.000+02:00"))));

		List<DatasetObservation> a = Iterators.asList(it);

		assertTrue(a.isEmpty());

		store.close();
	}

	@Test
	public void test10a() {
		ModuleStore store = new ModuleStoreSail("http://example.org#");

		DatasetObservation o1 = new DatasetObservationBase("o1");
		o1.setDataset(dataset("d1"));
		o1.addComponent(
				componentProperty(SDMX.Dimension.timePeriod),
				new ComponentPropertyValueTemporalLocation(
						new TemporalLocationDateTime("dt1", dtf
								.parseDateTime("2014-02-14T00:00:00.000+02:00"))));
		o1.addComponent(componentProperty("cp1"), componentPropertyValue(0.0));
		store.consider(o1);

		Iterator<DatasetObservation> it = store.getDatasetObservations(
				dataset("http://example.org#d1"),
				componentProperty("http://example.org#cp1"),
				componentPropertyValue(-1.0), componentPropertyValue(1.0));

		List<DatasetObservation> a = Iterators.asList(it);

		List<DatasetObservation> e = new ArrayList<DatasetObservation>();

		DatasetObservation o2 = new DatasetObservationBase(
				"http://example.org#o1");
		o2.setDataset(dataset("http://example.org#d1"));
		o2.addComponent(
				componentProperty(SDMX.Dimension.timePeriod),
				new ComponentPropertyValueTemporalLocation(
						new TemporalLocationDateTime(
								"http://example.org#dt1",
								dtf.parseDateTime("2014-02-14T00:00:00.000+02:00"))));
		o2.addComponent(componentProperty("http://example.org#cp1"),
				componentPropertyValue(0.0));
		e.add(o2);

		assertEquals(e, a);

		store.close();
	}

	@Test
	public void test10b() {
		ModuleStore store = new ModuleStoreSail("http://example.org#");

		DatasetObservation o1 = new DatasetObservationBase("o1");
		o1.setDataset(dataset("d1"));
		o1.addComponent(
				componentProperty(SDMX.Dimension.timePeriod),
				new ComponentPropertyValueTemporalLocation(
						new TemporalLocationDateTime("dt1", dtf
								.parseDateTime("2014-02-14T00:00:00.000+02:00"))));
		o1.addComponent(componentProperty("cp1"), componentPropertyValue(0.0));
		store.consider(o1);

		Iterator<DatasetObservation> it = store.getDatasetObservations(
				dataset("http://example.org#d1"),
				componentProperty("http://example.org#cp1"),
				componentPropertyValue(1.0), componentPropertyValue(2.0));

		List<DatasetObservation> a = Iterators.asList(it);

		assertTrue(a.isEmpty());

		store.close();
	}

	@Test
	public void test11a() {
		ModuleStore store = new ModuleStoreSail("http://example.org#");

		DatasetObservation o1 = new DatasetObservationBase("o1");
		o1.setDataset(dataset("d1"));
		o1.addComponent(
				componentProperty(SDMX.Dimension.timePeriod),
				new ComponentPropertyValueTemporalLocation(
						new TemporalLocationDateTime("dt1", dtf
								.parseDateTime("2014-02-14T00:00:00.000+02:00"))));
		o1.addComponent(componentProperty("cp1"), componentPropertyValue(0));
		store.consider(o1);

		Iterator<DatasetObservation> it = store.getDatasetObservations(
				dataset("http://example.org#d1"),
				componentProperty("http://example.org#cp1"),
				componentPropertyValue(-1), componentPropertyValue(1));

		List<DatasetObservation> a = Iterators.asList(it);

		List<DatasetObservation> e = new ArrayList<DatasetObservation>();

		DatasetObservation o2 = new DatasetObservationBase(
				"http://example.org#o1");
		o2.setDataset(dataset("http://example.org#d1"));
		o2.addComponent(
				componentProperty(SDMX.Dimension.timePeriod),
				new ComponentPropertyValueTemporalLocation(
						new TemporalLocationDateTime(
								"http://example.org#dt1",
								dtf.parseDateTime("2014-02-14T00:00:00.000+02:00"))));
		o2.addComponent(componentProperty("http://example.org#cp1"),
				componentPropertyValue(0));
		e.add(o2);

		assertEquals(e, a);

		store.close();
	}

	@Test
	public void test11b() {
		ModuleStore store = new ModuleStoreSail("http://example.org#");

		DatasetObservation o1 = new DatasetObservationBase("o1");
		o1.setDataset(dataset("d1"));
		o1.addComponent(
				componentProperty(SDMX.Dimension.timePeriod),
				new ComponentPropertyValueTemporalLocation(
						new TemporalLocationDateTime("dt1", dtf
								.parseDateTime("2014-02-14T00:00:00.000+02:00"))));
		o1.addComponent(componentProperty("cp1"), componentPropertyValue(0));
		store.consider(o1);

		Iterator<DatasetObservation> it = store.getDatasetObservations(
				dataset("http://example.org#d1"),
				componentProperty("http://example.org#cp1"),
				componentPropertyValue(1), componentPropertyValue(2));

		List<DatasetObservation> a = Iterators.asList(it);

		assertTrue(a.isEmpty());

		store.close();
	}

	@Test
	public void test12() {
		ModuleStore store = new ModuleStoreSail("http://example.org#");

		DatasetObservation o1 = new DatasetObservationBase("o1");
		o1.setDataset(dataset("d1"));
		o1.addComponent(
				componentProperty(SDMX.Dimension.timePeriod),
				new ComponentPropertyValueTemporalLocation(
						new TemporalLocationDateTime("dt1", dtf
								.parseDateTime("2014-02-14T00:00:00.000+02:00"))));
		o1.addComponent(componentProperty("cp1"), componentPropertyValue(0.0));
		store.consider(o1);

		Iterator<DatasetObservation> it = store.getDatasetObservations(
				dataset("http://example.org#d1"),
				componentProperty("http://example.org#cp1"),
				componentPropertyValue(-1), componentPropertyValue(1));

		List<DatasetObservation> a = Iterators.asList(it);

		List<DatasetObservation> e = new ArrayList<DatasetObservation>();

		DatasetObservation o2 = new DatasetObservationBase(
				"http://example.org#o1");
		o2.setDataset(dataset("http://example.org#d1"));
		o2.addComponent(
				componentProperty(SDMX.Dimension.timePeriod),
				new ComponentPropertyValueTemporalLocation(
						new TemporalLocationDateTime(
								"http://example.org#dt1",
								dtf.parseDateTime("2014-02-14T00:00:00.000+02:00"))));
		o2.addComponent(componentProperty("http://example.org#cp1"),
				componentPropertyValue(0.0));
		e.add(o2);

		assertEquals(e, a);

		store.close();
	}

	@Test
	public void test13a() {
		ModuleStore store = new ModuleStoreSail("http://example.org#");

		DatasetObservation o1 = new DatasetObservationBase("o1");
		o1.setDataset(dataset("d1"));
		o1.addComponent(
				componentProperty(SDMX.Dimension.timePeriod),
				new ComponentPropertyValueTemporalLocation(
						new TemporalLocationDateTime("dt1", dtf
								.parseDateTime("2014-02-14T00:00:00.000+02:00"))));
		o1.addComponent(componentProperty("cp1"), componentPropertyValue(0.0));
		o1.addComponent(
				componentProperty("cp2"),
				componentPropertyValue(new SpatialLocationPlace("sl1", vf
						.createURI("http://geonames.org#sl1"), "sl1")));
		store.consider(o1);

		Iterator<DatasetObservation> it = store.getDatasetObservations(
				dataset("http://example.org#d1"),
				componentProperty("http://example.org#cp1"),
				componentPropertyValue(-1), componentPropertyValue(1));

		List<DatasetObservation> a = Iterators.asList(it);

		List<DatasetObservation> e = new ArrayList<DatasetObservation>();

		DatasetObservation o2 = new DatasetObservationBase(
				"http://example.org#o1");
		o2.setDataset(dataset("http://example.org#d1"));
		o2.addComponent(
				componentProperty(SDMX.Dimension.timePeriod),
				new ComponentPropertyValueTemporalLocation(
						new TemporalLocationDateTime(
								"http://example.org#dt1",
								dtf.parseDateTime("2014-02-14T00:00:00.000+02:00"))));
		o2.addComponent(componentProperty("http://example.org#cp1"),
				componentPropertyValue(0.0));
		o2.addComponent(
				componentProperty("http://example.org#cp2"),
				componentPropertyValue(new SpatialLocationPlace(
						"http://example.org#sl1", vf
								.createURI("http://geonames.org#sl1"), "sl1")));
		e.add(o2);

		assertEquals(e, a);

		store.close();
	}

	@Test
	public void test13b() {
		ModuleStore store = new ModuleStoreSail("http://example.org#");

		GeometryFactory gf = new GeometryFactory();
		Point p1 = gf.createPoint(new Coordinate(0.0, 0.0));

		DatasetObservation o1 = new DatasetObservationBase("o1");
		o1.setDataset(dataset("d1"));
		o1.addComponent(
				componentProperty(SDMX.Dimension.timePeriod),
				new ComponentPropertyValueTemporalLocation(
						new TemporalLocationDateTime("dt1", dtf
								.parseDateTime("2014-02-14T00:00:00.000+02:00"))));
		o1.addComponent(componentProperty("cp1"), componentPropertyValue(0.0));
		o1.addComponent(componentProperty("cp2"),
				componentPropertyValue(new SpatialLocationRegion("sl1",
						new SpatialGeometryPoint("p1", p1))));
		store.consider(o1);

		Iterator<DatasetObservation> it = store.getDatasetObservations(
				dataset("http://example.org#d1"),
				componentProperty("http://example.org#cp1"),
				componentPropertyValue(-1), componentPropertyValue(1));

		List<DatasetObservation> a = Iterators.asList(it);

		List<DatasetObservation> e = new ArrayList<DatasetObservation>();

		DatasetObservation o2 = new DatasetObservationBase(
				"http://example.org#o1");
		o2.setDataset(dataset("http://example.org#d1"));
		o2.addComponent(
				componentProperty(SDMX.Dimension.timePeriod),
				new ComponentPropertyValueTemporalLocation(
						new TemporalLocationDateTime(
								"http://example.org#dt1",
								dtf.parseDateTime("2014-02-14T00:00:00.000+02:00"))));
		o2.addComponent(componentProperty("http://example.org#cp1"),
				componentPropertyValue(0.0));
		o2.addComponent(componentProperty("http://example.org#cp2"),
				componentPropertyValue(new SpatialLocationRegion(
						"http://example.org#sl1", new SpatialGeometryPoint(
								"http://example.org#p1", p1))));
		e.add(o2);

		assertEquals(e, a);

		store.close();
	}

	@Test
	public void test13c() {
		ModuleStore store = new ModuleStoreSail("http://example.org#");

		DatasetObservation o1 = new DatasetObservationBase("o1");
		o1.setDataset(dataset("d1"));
		o1.addComponent(
				componentProperty(SDMX.Dimension.timePeriod),
				new ComponentPropertyValueTemporalLocation(
						new TemporalLocationDateTime("dt1", dtf
								.parseDateTime("2014-02-14T00:00:00.000+02:00"))));
		o1.addComponent(componentProperty("cp1"), componentPropertyValue(0.0));
		o1.addComponent(
				componentProperty("cp2"),
				componentPropertyValue(new SpatialLocationPlace("sl1", vf
						.createURI("http://geonames.org#sl1"), "sl1")));
		store.consider(o1);

		Iterator<DatasetObservation> it = store.getDatasetObservations(
				dataset("http://example.org#d1"),
				componentProperty("http://example.org#cp1"),
				componentPropertyValue(dtf
						.parseDateTime("2014-02-15T00:00:00.000+02:00")),
				componentPropertyValue(dtf
						.parseDateTime("2014-02-16T00:00:00.000+02:00")));

		List<DatasetObservation> a = Iterators.asList(it);

		assertTrue(a.isEmpty());

		store.close();
	}

}
