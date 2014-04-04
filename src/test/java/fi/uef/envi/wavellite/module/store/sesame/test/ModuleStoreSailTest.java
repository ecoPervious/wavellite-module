/*
 * Copyright (C) 2014 see CREDITS.txt
 * All rights reserved.
 */

package fi.uef.envi.wavellite.module.store.sesame.test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;
import org.joda.time.format.ISOPeriodFormat;
import org.joda.time.format.PeriodFormatter;
import org.junit.Ignore;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.ValueFactoryImpl;
import org.openrdf.util.iterators.Iterators;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;

import fi.uef.envi.wavellite.entity.core.base.SpatialGeometryLineString;
import fi.uef.envi.wavellite.entity.core.base.SpatialGeometryPoint;
import fi.uef.envi.wavellite.entity.core.base.SpatialGeometryPolygon;
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
import fi.uef.envi.wavellite.entity.situation.Attribute;
import fi.uef.envi.wavellite.entity.situation.ElementaryInfon;
import fi.uef.envi.wavellite.entity.situation.Polarity;
import fi.uef.envi.wavellite.entity.situation.Relation;
import fi.uef.envi.wavellite.entity.situation.RelevantIndividual;
import fi.uef.envi.wavellite.entity.situation.Situation;
import fi.uef.envi.wavellite.entity.situation.Value;
import fi.uef.envi.wavellite.entity.situation.base.AttributeSpatialLocation;
import fi.uef.envi.wavellite.entity.situation.base.AttributeTemporalLocation;
import fi.uef.envi.wavellite.entity.situation.base.AttributeUri;
import fi.uef.envi.wavellite.entity.situation.base.AttributeValue;
import fi.uef.envi.wavellite.entity.situation.base.ElementaryInfonBase;
import fi.uef.envi.wavellite.entity.situation.base.RelationBase;
import fi.uef.envi.wavellite.entity.situation.base.RelevantIndividualBase;
import fi.uef.envi.wavellite.entity.situation.base.SituationBase;
import fi.uef.envi.wavellite.entity.situation.base.ValueDouble;
import fi.uef.envi.wavellite.entity.situation.base.ValueInteger;
import fi.uef.envi.wavellite.entity.situation.base.ValuePeriod;
import fi.uef.envi.wavellite.entity.situation.base.ValueString;
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

	private final static PeriodFormatter pf = ISOPeriodFormat.standard();
	private static final ValueFactory vf = ValueFactoryImpl.getInstance();
	private static final DateTimeFormatter dtf = ISODateTimeFormat
			.dateTimeParser().withOffsetParsed();

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

	@Test
	public void test14a() {
		ModuleStore store = new ModuleStoreSail("http://example.org#");

		Situation s1 = new SituationBase("s1");
		ElementaryInfon i1 = new ElementaryInfonBase("i1");
		s1.addSupportedInfon(i1);
		i1.setRelation(new RelationBase("r1"));
		RelevantIndividual o1 = new RelevantIndividualBase("o1");
		Attribute a1 = new AttributeValue("a1");
		Value v1 = new ValueDouble("v1", 0.0);
		a1.setValue(v1);
		o1.addAttribute(a1);
		i1.addRelevantObject(o1);
		i1.setPolarity(Polarity.True);
		store.consider(s1);

		Iterator<Situation> it = store.getSituations(new RelationBase(
				"http://example.org#r1"));

		List<Situation> a = Iterators.asList(it);

		List<Situation> e = new ArrayList<Situation>();
		Situation s2 = new SituationBase("http://example.org#s1");
		ElementaryInfon i2 = new ElementaryInfonBase("http://example.org#i1");
		s2.addSupportedInfon(i2);
		i2.setRelation(new RelationBase("http://example.org#r1"));
		RelevantIndividual o2 = new RelevantIndividualBase(
				"http://example.org#o1");
		Attribute a2 = new AttributeValue("http://example.org#a1");
		Value v2 = new ValueDouble("http://example.org#v1", 0.0);
		a2.setValue(v2);
		o2.addAttribute(a2);
		i2.addRelevantObject(o2);
		i2.setPolarity(Polarity.True);
		e.add(s2);

		assertEquals(e, a);

		store.close();
	}

	@Test
	public void test14b() {
		ModuleStore store = new ModuleStoreSail("http://example.org#");

		Situation s1 = new SituationBase("s1");
		ElementaryInfon i1 = new ElementaryInfonBase("i1");
		s1.addSupportedInfon(i1);
		i1.setRelation(new RelationBase("r1"));
		RelevantIndividual o1 = new RelevantIndividualBase("o1");
		Attribute a1 = new AttributeValue("a1");
		Value v1 = new ValueDouble("v1", 0.0);
		a1.setValue(v1);
		o1.addAttribute(a1);
		i1.addRelevantObject(o1);
		i1.setPolarity(Polarity.True);
		store.consider(s1);

		Iterator<Situation> it = store.getSituations(new RelationBase(
				"http://example.org#r2"));

		List<Situation> a = Iterators.asList(it);

		assertTrue(a.isEmpty());

		store.close();
	}

	@Test
	public void test15a() {
		ModuleStore store = new ModuleStoreSail("http://example.org#");

		Situation s1 = new SituationBase("s1");
		ElementaryInfon i1 = new ElementaryInfonBase("i1");
		s1.addSupportedInfon(i1);
		i1.setRelation(new RelationBase("r1"));
		RelevantIndividual o1 = new RelevantIndividualBase("o1");
		Attribute a1 = new AttributeValue("a1");
		Value v1 = new ValueString("v1", "v1");
		a1.setValue(v1);
		o1.addAttribute(a1);
		i1.addRelevantObject(o1);
		i1.setPolarity(Polarity.True);
		store.consider(s1);

		Iterator<Situation> it = store.getSituations(new RelationBase(
				"http://example.org#r1"));

		List<Situation> a = Iterators.asList(it);

		List<Situation> e = new ArrayList<Situation>();
		Situation s2 = new SituationBase("http://example.org#s1");
		ElementaryInfon i2 = new ElementaryInfonBase("http://example.org#i1");
		s2.addSupportedInfon(i2);
		i2.setRelation(new RelationBase("http://example.org#r1"));
		RelevantIndividual o2 = new RelevantIndividualBase(
				"http://example.org#o1");
		Attribute a2 = new AttributeValue("http://example.org#a1");
		Value v2 = new ValueString("http://example.org#v1", "v1");
		a2.setValue(v2);
		o2.addAttribute(a2);
		i2.addRelevantObject(o2);
		i2.setPolarity(Polarity.True);
		e.add(s2);

		assertEquals(e, a);

		store.close();
	}

	@Test
	public void test16a() {
		ModuleStore store = new ModuleStoreSail("http://example.org#");

		Situation s1 = new SituationBase("s1");
		ElementaryInfon i1 = new ElementaryInfonBase("i1");
		s1.addSupportedInfon(i1);
		i1.setRelation(new RelationBase("r1"));
		RelevantIndividual o1 = new RelevantIndividualBase("o1");
		Attribute a1 = new AttributeValue("a1");
		Value v1 = new ValueInteger("v1", 1);
		a1.setValue(v1);
		o1.addAttribute(a1);
		i1.addRelevantObject(o1);
		i1.setPolarity(Polarity.True);
		store.consider(s1);

		Iterator<Situation> it = store.getSituations(new RelationBase(
				"http://example.org#r1"));

		List<Situation> a = Iterators.asList(it);

		List<Situation> e = new ArrayList<Situation>();
		Situation s2 = new SituationBase("http://example.org#s1");
		ElementaryInfon i2 = new ElementaryInfonBase("http://example.org#i1");
		s2.addSupportedInfon(i2);
		i2.setRelation(new RelationBase("http://example.org#r1"));
		RelevantIndividual o2 = new RelevantIndividualBase(
				"http://example.org#o1");
		Attribute a2 = new AttributeValue("http://example.org#a1");
		Value v2 = new ValueInteger("http://example.org#v1", 1);
		a2.setValue(v2);
		o2.addAttribute(a2);
		i2.addRelevantObject(o2);
		i2.setPolarity(Polarity.True);
		e.add(s2);

		assertEquals(e, a);

		store.close();
	}

	@Test
	public void test17a() {
		ModuleStore store = new ModuleStoreSail("http://example.org#");

		Situation s1 = new SituationBase("s1");
		ElementaryInfon i1 = new ElementaryInfonBase("i1");
		s1.addSupportedInfon(i1);
		i1.setRelation(new RelationBase("r1"));
		RelevantIndividual o1 = new RelevantIndividualBase("o1");
		Attribute a1 = new AttributeValue("a1");
		Value v1 = new ValuePeriod("v1", pf.parsePeriod("P1D"));
		a1.setValue(v1);
		o1.addAttribute(a1);
		i1.addRelevantObject(o1);
		i1.setPolarity(Polarity.True);
		store.consider(s1);

		Iterator<Situation> it = store.getSituations(new RelationBase(
				"http://example.org#r1"));

		List<Situation> a = Iterators.asList(it);

		List<Situation> e = new ArrayList<Situation>();
		Situation s2 = new SituationBase("http://example.org#s1");
		ElementaryInfon i2 = new ElementaryInfonBase("http://example.org#i1");
		s2.addSupportedInfon(i2);
		i2.setRelation(new RelationBase("http://example.org#r1"));
		RelevantIndividual o2 = new RelevantIndividualBase(
				"http://example.org#o1");
		Attribute a2 = new AttributeValue("http://example.org#a1");
		Value v2 = new ValuePeriod("http://example.org#v1",
				pf.parsePeriod("P1D"));
		a2.setValue(v2);
		o2.addAttribute(a2);
		i2.addRelevantObject(o2);
		i2.setPolarity(Polarity.True);
		e.add(s2);

		assertEquals(e, a);

		store.close();
	}

	@Test
	public void test18a() {
		ModuleStore store = new ModuleStoreSail("http://example.org#");

		Situation s1 = new SituationBase("s1");
		ElementaryInfon i1 = new ElementaryInfonBase("i1");
		s1.addSupportedInfon(i1);
		i1.setRelation(new RelationBase("r1"));
		RelevantIndividual o1 = new RelevantIndividualBase("o1");
		Attribute a1 = new AttributeUri("a1");
		a1.setValue(vf.createURI("http://test.org#u1"));
		o1.addAttribute(a1);
		i1.addRelevantObject(o1);
		i1.setPolarity(Polarity.True);
		store.consider(s1);

		Iterator<Situation> it = store.getSituations(new RelationBase(
				"http://example.org#r1"));

		List<Situation> a = Iterators.asList(it);

		List<Situation> e = new ArrayList<Situation>();
		Situation s2 = new SituationBase("http://example.org#s1");
		ElementaryInfon i2 = new ElementaryInfonBase("http://example.org#i1");
		s2.addSupportedInfon(i2);
		i2.setRelation(new RelationBase("http://example.org#r1"));
		RelevantIndividual o2 = new RelevantIndividualBase(
				"http://example.org#o1");
		Attribute a2 = new AttributeUri("http://example.org#a1");
		a2.setValue(vf.createURI("http://test.org#u1"));
		o2.addAttribute(a2);
		i2.addRelevantObject(o2);
		i2.setPolarity(Polarity.True);
		e.add(s2);

		assertEquals(e, a);

		store.close();
	}

	@Test
	@Ignore
	// This will fail because currently AttributeUri is not queried
	public void test19a() {
		ModuleStore store = new ModuleStoreSail("http://example.org#");

		Situation s1 = new SituationBase("s1");
		ElementaryInfon i1 = new ElementaryInfonBase("i1");
		s1.addSupportedInfon(i1);
		i1.setRelation(new RelationBase("r1"));
		i1.addRelevantObject(new AttributeUri(vf
				.createURI("http://test.org#u1")));
		i1.setPolarity(Polarity.True);
		store.consider(s1);

		Iterator<Situation> it = store.getSituations(new RelationBase(
				"http://example.org#r1"));

		List<Situation> a = Iterators.asList(it);

		List<Situation> e = new ArrayList<Situation>();
		Situation s2 = new SituationBase("http://example.org#s1");
		ElementaryInfon i2 = new ElementaryInfonBase("http://example.org#i1");
		s2.addSupportedInfon(i2);
		i2.setRelation(new RelationBase("http://example.org#r1"));
		i2.addRelevantObject(new AttributeUri(vf
				.createURI("http://test.org#u1")));
		i2.setPolarity(Polarity.True);
		e.add(s2);

		assertEquals(e, a);

		store.close();
	}

	@Test
	public void test20a() {
		ModuleStore store = new ModuleStoreSail("http://example.org#");

		Situation s1 = new SituationBase("s1");
		ElementaryInfon i1 = new ElementaryInfonBase("i1");
		s1.addSupportedInfon(i1);
		i1.setRelation(new RelationBase("r1"));
		i1.addRelevantObject(new AttributeTemporalLocation(
				new TemporalLocationDateTime(dtf
						.parseDateTime("2001-01-01T00:00:00.000+02:00"))));
		i1.setPolarity(Polarity.True);
		store.consider(s1);

		Iterator<Situation> it = store.getSituations(new RelationBase(
				"http://example.org#r1"));

		List<Situation> a = Iterators.asList(it);

		List<Situation> e = new ArrayList<Situation>();
		Situation s2 = new SituationBase("http://example.org#s1");
		ElementaryInfon i2 = new ElementaryInfonBase("http://example.org#i1");
		s2.addSupportedInfon(i2);
		i2.setRelation(new RelationBase("http://example.org#r1"));
		i2.addRelevantObject(new AttributeTemporalLocation(
				new TemporalLocationDateTime(dtf
						.parseDateTime("2001-01-01T00:00:00.000+02:00"))));
		i2.setPolarity(Polarity.True);
		e.add(s2);

		assertEquals(e, a);

		store.close();
	}

	@Test
	public void test21a() {
		ModuleStore store = new ModuleStoreSail("http://example.org#");

		Situation s1 = new SituationBase("s1");
		ElementaryInfon i1 = new ElementaryInfonBase("i1");
		s1.addSupportedInfon(i1);
		i1.setRelation(new RelationBase("r1"));
		i1.addRelevantObject(new AttributeTemporalLocation(
				new TemporalLocationInterval(
						new TemporalLocationDateTime(dtf
								.parseDateTime("2001-01-01T00:00:00.000+02:00")),
						new TemporalLocationDateTime(dtf
								.parseDateTime("2001-01-01T01:00:00.000+02:00")))));
		i1.setPolarity(Polarity.True);
		store.consider(s1);

		Iterator<Situation> it = store.getSituations(new RelationBase(
				"http://example.org#r1"));

		List<Situation> a = Iterators.asList(it);

		List<Situation> e = new ArrayList<Situation>();
		Situation s2 = new SituationBase("http://example.org#s1");
		ElementaryInfon i2 = new ElementaryInfonBase("http://example.org#i1");
		s2.addSupportedInfon(i2);
		i2.setRelation(new RelationBase("http://example.org#r1"));
		i2.addRelevantObject(new AttributeTemporalLocation(
				new TemporalLocationInterval(
						new TemporalLocationDateTime(dtf
								.parseDateTime("2001-01-01T00:00:00.000+02:00")),
						new TemporalLocationDateTime(dtf
								.parseDateTime("2001-01-01T01:00:00.000+02:00")))));
		i2.setPolarity(Polarity.True);
		e.add(s2);

		assertEquals(e, a);

		store.close();
	}

	@Test
	public void test22a() {
		ModuleStore store = new ModuleStoreSail("http://example.org#");

		Situation s1 = new SituationBase("s1");
		ElementaryInfon i1 = new ElementaryInfonBase("i1");
		s1.addSupportedInfon(i1);
		i1.setRelation(new RelationBase("r1"));
		i1.addRelevantObject(new AttributeSpatialLocation(
				new SpatialLocationPlace("p1")));
		i1.setPolarity(Polarity.True);
		store.consider(s1);

		Iterator<Situation> it = store.getSituations(new RelationBase(
				"http://example.org#r1"));

		List<Situation> a = Iterators.asList(it);

		List<Situation> e = new ArrayList<Situation>();
		Situation s2 = new SituationBase("http://example.org#s1");
		ElementaryInfon i2 = new ElementaryInfonBase("http://example.org#i1");
		s2.addSupportedInfon(i2);
		i2.setRelation(new RelationBase("http://example.org#r1"));
		i2.addRelevantObject(new AttributeSpatialLocation(
				new SpatialLocationPlace("http://example.org#p1")));
		i2.setPolarity(Polarity.True);
		e.add(s2);

		assertEquals(e, a);

		store.close();
	}

	@Test
	public void test23a() {
		ModuleStore store = new ModuleStoreSail("http://example.org#");

		GeometryFactory gf = new GeometryFactory();
		Point p1 = gf.createPoint(new Coordinate(0.0, 0.0));

		Situation s1 = new SituationBase("s1");
		ElementaryInfon i1 = new ElementaryInfonBase("i1");
		s1.addSupportedInfon(i1);
		i1.setRelation(new RelationBase("r1"));
		i1.addRelevantObject(new AttributeSpatialLocation(
				new SpatialLocationRegion("p1", new SpatialGeometryPoint("g1",
						p1))));
		i1.setPolarity(Polarity.True);
		store.consider(s1);

		Iterator<Situation> it = store.getSituations(new RelationBase(
				"http://example.org#r1"));

		List<Situation> a = Iterators.asList(it);

		List<Situation> e = new ArrayList<Situation>();
		Situation s2 = new SituationBase("http://example.org#s1");
		ElementaryInfon i2 = new ElementaryInfonBase("http://example.org#i1");
		s2.addSupportedInfon(i2);
		i2.setRelation(new RelationBase("http://example.org#r1"));
		i2.addRelevantObject(new AttributeSpatialLocation(
				new SpatialLocationRegion("http://example.org#p1",
						new SpatialGeometryPoint("http://example.org#g1", p1))));
		i2.setPolarity(Polarity.True);
		e.add(s2);

		assertEquals(e, a);

		store.close();
	}

	@Test
	public void test24a() {
		ModuleStore store = new ModuleStoreSail("http://example.org#");

		Situation s1 = new SituationBase("s1");
		ElementaryInfon i1 = new ElementaryInfonBase("i1");
		s1.addSupportedInfon(i1);
		i1.setRelation(new RelationBase("r1"));
		i1.addRelevantObject(new ValueDouble("v1", 0.0));
		i1.setPolarity(Polarity.True);
		store.consider(s1);

		Iterator<Situation> it = store.getSituations(new RelationBase(
				"http://example.org#r1"));

		List<Situation> a = Iterators.asList(it);

		List<Situation> e = new ArrayList<Situation>();
		Situation s2 = new SituationBase("http://example.org#s1");
		ElementaryInfon i2 = new ElementaryInfonBase("http://example.org#i1");
		s2.addSupportedInfon(i2);
		i2.setRelation(new RelationBase("http://example.org#r1"));
		i2.addRelevantObject(new ValueDouble("v1", 0.0));
		i2.setPolarity(Polarity.True);
		e.add(s2);

		assertEquals(e, a);

		store.close();
	}

	@Test
	public void test25a() {
		ModuleStore store = new ModuleStoreSail("http://example.org#");

		Situation s1 = new SituationBase("s1");
		ElementaryInfon i1 = new ElementaryInfonBase("i1");
		s1.addSupportedInfon(i1);
		i1.setRelation(new RelationBase("r1"));
		i1.addRelevantObject(new ValueInteger("v1", 0));
		i1.setPolarity(Polarity.True);
		store.consider(s1);

		Iterator<Situation> it = store.getSituations(new RelationBase(
				"http://example.org#r1"));

		List<Situation> a = Iterators.asList(it);

		List<Situation> e = new ArrayList<Situation>();
		Situation s2 = new SituationBase("http://example.org#s1");
		ElementaryInfon i2 = new ElementaryInfonBase("http://example.org#i1");
		s2.addSupportedInfon(i2);
		i2.setRelation(new RelationBase("http://example.org#r1"));
		i2.addRelevantObject(new ValueInteger("v1", 0));
		i2.setPolarity(Polarity.True);
		e.add(s2);

		assertEquals(e, a);

		store.close();
	}

	@Test
	public void test25b() {
		ModuleStore store = new ModuleStoreSail();

		Situation s1 = new SituationBase("http://example.org#s1");
		ElementaryInfon i1 = new ElementaryInfonBase("http://example.org#i1");
		s1.addSupportedInfon(i1);
		i1.setRelation(new RelationBase("http://example.org#r1"));
		i1.addRelevantObject(new ValueInteger("http://example.org#v1", 0));
		i1.setPolarity(Polarity.True);
		store.consider(s1);

		Situation s2 = new SituationBase("http://example.org#s2");
		ElementaryInfon i2 = new ElementaryInfonBase("http://example.org#i2");
		s2.addSupportedInfon(i2);
		i2.setRelation(new RelationBase("http://example.org#r1"));
		i2.addRelevantObject(new ValueInteger("http://example.org#v2", 1));
		i2.setPolarity(Polarity.True);
		store.consider(s2);

		Iterator<Situation> it = store.getSituations(new RelationBase(
				"http://example.org#r1"));

		Set<Situation> a = new HashSet<Situation>(Iterators.asList(it));

		Set<Situation> e = new HashSet<Situation>();

		e.add(s1);
		e.add(s2);

		assertEquals(e, a);

		store.close();
	}

	@Test
	public void test25c() {
		ModuleStore store = new ModuleStoreSail();

		Situation s1 = new SituationBase("http://example.org#s1");
		ElementaryInfon i1 = new ElementaryInfonBase("http://example.org#i1");
		s1.addSupportedInfon(i1);
		i1.setRelation(new RelationBase("http://example.org#r1"));
		i1.addRelevantObject(new ValueInteger("http://example.org#v1", 0));
		i1.addRelevantObject(new ValueInteger("http://example.org#v2", 1));
		i1.setPolarity(Polarity.True);
		store.consider(s1);

		Situation s2 = new SituationBase("http://example.org#s2");
		ElementaryInfon i2 = new ElementaryInfonBase("http://example.org#i2");
		s2.addSupportedInfon(i2);
		i2.setRelation(new RelationBase("http://example.org#r1"));
		i2.addRelevantObject(new ValueInteger("http://example.org#v3", 1));
		i2.addRelevantObject(new ValueInteger("http://example.org#v4", 2));
		i2.setPolarity(Polarity.True);
		store.consider(s2);

		Iterator<Situation> it = store.getSituations(new RelationBase(
				"http://example.org#r1"));

		Set<Situation> a = new HashSet<Situation>(Iterators.asList(it));

		Set<Situation> e = new HashSet<Situation>();

		e.add(s1);
		e.add(s2);

		assertEquals(e, a);

		store.close();
	}

	@Test
	public void test26a() {
		ModuleStore store = new ModuleStoreSail("http://example.org#");

		GeometryFactory gf = new GeometryFactory();
		DateTimeFormatter dtf = ISODateTimeFormat.dateTimeParser()
				.withOffsetParsed();

		Polygon p1 = gf.createPolygon(new Coordinate[] {
				new Coordinate(0.0, 0.0), new Coordinate(0.0, 1.0),
				new Coordinate(1.0, 1.0), new Coordinate(1.0, 0.0),
				new Coordinate(0.0, 0.0) });
		Polygon p2 = gf.createPolygon(new Coordinate[] {
				new Coordinate(3.0, 3.0), new Coordinate(3.0, 4.0),
				new Coordinate(4.0, 4.0), new Coordinate(4.0, 3.0),
				new Coordinate(3.0, 3.0) });
		LineString ls = gf.createLineString(new Coordinate[] {
				new Coordinate(0.0, 0.0), new Coordinate(2.0, 2.0) });

		Situation s1 = new SituationBase("http://example.org#s1");
		ElementaryInfon i1 = new ElementaryInfonBase("http://example.org#i1");
		s1.addSupportedInfon(i1);
		i1.setRelation(new RelationBase("http://example.org#storm"));
		i1.setPolarity(Polarity.True);
		i1.addRelevantObject(new RelevantIndividualBase(
				"http://example.org#storm-1"));
		i1.addRelevantObject(new AttributeTemporalLocation(
				new TemporalLocationInterval(
						"http://example.org#tl11",
						new TemporalLocationDateTime(
								"http://example.org#dt11",
								dtf.parseDateTime("2014-03-03T16:00:00.000+02:00")),
						new TemporalLocationDateTime(
								"http://example.org#dt12",
								dtf.parseDateTime("2014-03-03T17:00:00.000+02:00")))));
		i1.addRelevantObject(new AttributeSpatialLocation(
				new SpatialLocationRegion("http://example.org#sl1",
						new SpatialGeometryPolygon(p1))));

		Situation s2 = new SituationBase("http://example.org#s2");
		ElementaryInfon i2 = new ElementaryInfonBase("http://example.org#i2");
		s2.addSupportedInfon(i2);
		i2.setRelation(new RelationBase("http://example.org#storm"));
		i2.setPolarity(Polarity.True);
		i2.addRelevantObject(new RelevantIndividualBase(
				"http://example.org#storm-2"));
		i2.addRelevantObject(new AttributeTemporalLocation(
				new TemporalLocationInterval(
						"http://example.org#tl21",
						new TemporalLocationDateTime(
								"http://example.org#dt21",
								dtf.parseDateTime("2014-03-03T16:00:00.000+02:00")),
						new TemporalLocationDateTime(
								"http://example.org#dt22",
								dtf.parseDateTime("2014-03-03T17:00:00.000+02:00")))));
		i2.addRelevantObject(new AttributeSpatialLocation(
				new SpatialLocationRegion("http://example.org#sl2",
						new SpatialGeometryPolygon(p2))));

		Situation s3 = new SituationBase("http://example.org#s3");
		ElementaryInfon i3 = new ElementaryInfonBase("http://example.org#i3");
		s3.addSupportedInfon(i3);
		i3.setRelation(new RelationBase("http://example.org#driver"));
		i3.setPolarity(Polarity.True);
		i3.addRelevantObject(new RelevantIndividualBase(
				"http://example.org#driver-1"));
		i3.addRelevantObject(new AttributeTemporalLocation(
				new TemporalLocationInterval(
						"http://example.org#tl31",
						new TemporalLocationDateTime(
								"http://example.org#dt31",
								dtf.parseDateTime("2014-03-03T15:00:00.000+02:00")),
						new TemporalLocationDateTime(
								"http://example.org#dt32",
								dtf.parseDateTime("2014-03-03T18:00:00.000+02:00")))));
		i3.addRelevantObject(new AttributeSpatialLocation(
				new SpatialLocationRegion("http://example.org#sl3",
						new SpatialGeometryLineString(ls))));

		store.consider(s1);
		store.consider(s2);
		store.consider(s3);

		Iterator<Situation> it = store.getSituations(new RelationBase(
				"http://example.org#driver"));

		Set<Situation> a = new HashSet<Situation>(Iterators.asList(it));

		Set<Situation> e = new HashSet<Situation>();
		e.add(s3);

		assertEquals(e, a);
	}

	@Test
	public void test27a() {
		ModuleStore store = new ModuleStoreSail("http://example.org#");

		Relation r1 = new RelationBase("r1");

		store.consider(r1);

		Iterator<Relation> it = store.getRelations();

		List<Relation> a = Iterators.asList(it);

		List<Relation> e = new ArrayList<Relation>();

		Relation r2 = new RelationBase("http://example.org#r1");
		e.add(r2);

		assertEquals(e, a);

		store.close();
	}

	@Test
	public void test27b() {
		ModuleStore store = new ModuleStoreSail("http://example.org#");

		Relation r1 = new RelationBase("r1");

		store.consider(r1);

		Iterator<Relation> it = store.getRelations();

		List<Relation> a = Iterators.asList(it);

		List<Relation> e = new ArrayList<Relation>();

		Relation r2 = new RelationBase("http://example.org#r2");
		e.add(r2);

		assertNotEquals(e, a);

		store.close();
	}

	@Test
	public void test28a() {
		ModuleStore store = new ModuleStoreSail("http://example.org#");

		Situation s1 = new SituationBase("http://example.org#s1");
		ElementaryInfon i1 = new ElementaryInfonBase("http://example.org#i1");
		s1.addSupportedInfon(i1);
		i1.setRelation(new RelationBase("http://example.org#storm"));

		Situation s2 = new SituationBase("http://example.org#s2");
		ElementaryInfon i2 = new ElementaryInfonBase("http://example.org#i2");
		s2.addSupportedInfon(i2);
		i2.setRelation(new RelationBase("http://example.org#pollen"));

		Situation s3 = new SituationBase("http://example.org#s3");
		ElementaryInfon i3 = new ElementaryInfonBase("http://example.org#i3");
		s3.addSupportedInfon(i3);
		i3.setRelation(new RelationBase("http://example.org#driver"));

		store.consider(s1);
		store.consider(s2);
		store.consider(s3);

		Iterator<Situation> it = store.getSituations(new RelationBase(
				"http://example.org#driver"), new RelationBase(
				"http://example.org#pollen"));

		Set<Situation> a = new HashSet<Situation>(Iterators.asList(it));

		Set<Situation> e = new HashSet<Situation>();
		e.add(s2);
		e.add(s3);

		assertEquals(e, a);
	}

	@Test
	public void test28b() {
		ModuleStore store = new ModuleStoreSail("http://example.org#");

		Situation s1 = new SituationBase("http://example.org#s1");
		ElementaryInfon i1 = new ElementaryInfonBase("http://example.org#i1");
		s1.addSupportedInfon(i1);
		i1.setRelation(new RelationBase("http://example.org#storm"));

		Situation s2 = new SituationBase("http://example.org#s2");
		ElementaryInfon i2 = new ElementaryInfonBase("http://example.org#i2");
		s2.addSupportedInfon(i2);
		i2.setRelation(new RelationBase("http://example.org#pollen"));

		Situation s3 = new SituationBase("http://example.org#s3");
		ElementaryInfon i3 = new ElementaryInfonBase("http://example.org#i3");
		s3.addSupportedInfon(i3);
		i3.setRelation(new RelationBase("http://example.org#driver"));

		store.consider(s1);
		store.consider(s2);
		store.consider(s3);

		Iterator<Situation> it = store.getSituations();

		Set<Situation> a = new HashSet<Situation>(Iterators.asList(it));

		Set<Situation> e = new HashSet<Situation>();
		e.add(s1);
		e.add(s2);
		e.add(s3);

		assertEquals(e, a);
	}

	@Test
	public void test29a() {
		ModuleStore store = new ModuleStoreSail("http://example.org#");

		DateTimeFormatter dtf = ISODateTimeFormat.dateTimeParser()
				.withOffsetParsed();

		Situation s1 = new SituationBase("http://example.org#s1");
		ElementaryInfon i1 = new ElementaryInfonBase("http://example.org#i1");
		s1.addSupportedInfon(i1);
		i1.setRelation(new RelationBase("http://example.org#storm"));
		i1.setPolarity(Polarity.True);
		i1.addRelevantObject(new RelevantIndividualBase(
				"http://example.org#storm-1"));
		i1.addRelevantObject(new AttributeTemporalLocation(
				new TemporalLocationDateTime("http://example.org#dt11", dtf
						.parseDateTime("2014-03-03T16:00:00.000+02:00"))));

		Situation s2 = new SituationBase("http://example.org#s2");
		ElementaryInfon i2 = new ElementaryInfonBase("http://example.org#i2");
		s2.addSupportedInfon(i2);
		i2.setRelation(new RelationBase("http://example.org#storm"));
		i2.setPolarity(Polarity.True);
		i2.addRelevantObject(new RelevantIndividualBase(
				"http://example.org#storm-2"));
		i2.addRelevantObject(new AttributeTemporalLocation(
				new TemporalLocationDateTime("http://example.org#dt12", dtf
						.parseDateTime("2014-03-03T17:00:00.000+02:00"))));

		store.consider(s1);
		store.consider(s2);

		Iterator<Situation> it = store
				.getSituations(new TemporalLocationInterval(
						new TemporalLocationDateTime(dtf
								.parseDateTime("2014-03-03T15:00:00.000+02:00")),
						new TemporalLocationDateTime(dtf
								.parseDateTime("2014-03-03T18:00:00.000+02:00"))));

		Set<Situation> a = new HashSet<Situation>(Iterators.asList(it));

		Set<Situation> e = new HashSet<Situation>();
		e.add(s1);
		e.add(s2);

		assertEquals(e, a);
	}

	@Test
	public void test29b() {
		ModuleStore store = new ModuleStoreSail("http://example.org#");

		DateTimeFormatter dtf = ISODateTimeFormat.dateTimeParser()
				.withOffsetParsed();

		Situation s1 = new SituationBase("http://example.org#s1");
		ElementaryInfon i1 = new ElementaryInfonBase("http://example.org#i1");
		s1.addSupportedInfon(i1);
		i1.setRelation(new RelationBase("http://example.org#storm"));
		i1.setPolarity(Polarity.True);
		i1.addRelevantObject(new RelevantIndividualBase(
				"http://example.org#storm-1"));
		i1.addRelevantObject(new AttributeTemporalLocation(
				new TemporalLocationDateTime("http://example.org#dt11", dtf
						.parseDateTime("2014-03-03T16:00:00.000+02:00"))));

		Situation s2 = new SituationBase("http://example.org#s2");
		ElementaryInfon i2 = new ElementaryInfonBase("http://example.org#i2");
		s2.addSupportedInfon(i2);
		i2.setRelation(new RelationBase("http://example.org#driver"));
		i2.setPolarity(Polarity.True);
		i2.addRelevantObject(new RelevantIndividualBase(
				"http://example.org#storm-2"));
		i2.addRelevantObject(new AttributeTemporalLocation(
				new TemporalLocationDateTime("http://example.org#dt12", dtf
						.parseDateTime("2014-03-03T17:00:00.000+02:00"))));

		store.consider(s1);
		store.consider(s2);

		Iterator<Situation> it = store
				.getSituations(
						new TemporalLocationInterval(
								new TemporalLocationDateTime(
										dtf.parseDateTime("2014-03-03T15:00:00.000+02:00")),
								new TemporalLocationDateTime(
										dtf.parseDateTime("2014-03-03T18:00:00.000+02:00"))),
						new RelationBase("http://example.org#storm"));

		Set<Situation> a = new HashSet<Situation>(Iterators.asList(it));

		Set<Situation> e = new HashSet<Situation>();
		e.add(s1);

		assertEquals(e, a);
	}

	@Test
	public void test29c() {
		ModuleStore store = new ModuleStoreSail("http://example.org#");

		DateTimeFormatter dtf = ISODateTimeFormat.dateTimeParser()
				.withOffsetParsed();

		Situation s1 = new SituationBase("http://example.org#s1");
		ElementaryInfon i1 = new ElementaryInfonBase("http://example.org#i1");
		s1.addSupportedInfon(i1);
		i1.setRelation(new RelationBase("http://example.org#storm"));
		i1.setPolarity(Polarity.True);
		i1.addRelevantObject(new RelevantIndividualBase(
				"http://example.org#storm-1"));
		i1.addRelevantObject(new AttributeTemporalLocation(
				new TemporalLocationDateTime("http://example.org#dt11", dtf
						.parseDateTime("2014-03-03T16:00:00.000+02:00"))));

		Situation s2 = new SituationBase("http://example.org#s2");
		ElementaryInfon i2 = new ElementaryInfonBase("http://example.org#i2");
		s2.addSupportedInfon(i2);
		i2.setRelation(new RelationBase("http://example.org#driver"));
		i2.setPolarity(Polarity.True);
		i2.addRelevantObject(new RelevantIndividualBase(
				"http://example.org#storm-2"));
		i2.addRelevantObject(new AttributeTemporalLocation(
				new TemporalLocationDateTime("http://example.org#dt12", dtf
						.parseDateTime("2014-03-03T17:00:00.000+02:00"))));

		store.consider(s1);
		store.consider(s2);

		Iterator<Situation> it = store
				.getSituations(
						new TemporalLocationInterval(
								new TemporalLocationDateTime(
										dtf.parseDateTime("2014-03-03T15:00:00.000+02:00")),
								new TemporalLocationDateTime(
										dtf.parseDateTime("2014-03-03T18:00:00.000+02:00"))),
						new RelationBase("http://example.org#storm"),
						new RelationBase("http://example.org#driver"));

		Set<Situation> a = new HashSet<Situation>(Iterators.asList(it));

		Set<Situation> e = new HashSet<Situation>();
		e.add(s1);
		e.add(s2);

		assertEquals(e, a);
	}

	@Test
	public void test29d() {
		ModuleStore store = new ModuleStoreSail("http://example.org#");

		DateTimeFormatter dtf = ISODateTimeFormat.dateTimeParser()
				.withOffsetParsed();

		Situation s1 = new SituationBase("http://example.org#s1");
		ElementaryInfon i1 = new ElementaryInfonBase("http://example.org#i1");
		s1.addSupportedInfon(i1);
		i1.setRelation(new RelationBase("http://example.org#storm"));
		i1.setPolarity(Polarity.True);
		i1.addRelevantObject(new RelevantIndividualBase(
				"http://example.org#storm-1"));
		i1.addRelevantObject(new AttributeTemporalLocation(
				new TemporalLocationDateTime("http://example.org#dt11", dtf
						.parseDateTime("2014-03-03T16:00:00.000+02:00"))));

		Situation s2 = new SituationBase("http://example.org#s2");
		ElementaryInfon i2 = new ElementaryInfonBase("http://example.org#i2");
		s2.addSupportedInfon(i2);
		i2.setRelation(new RelationBase("http://example.org#driver"));
		i2.setPolarity(Polarity.True);
		i2.addRelevantObject(new RelevantIndividualBase(
				"http://example.org#driver-1"));
		i2.addRelevantObject(new AttributeTemporalLocation(
				new TemporalLocationDateTime("http://example.org#dt12", dtf
						.parseDateTime("2014-03-03T18:30:00.000+02:00"))));

		store.consider(s1);
		store.consider(s2);

		Iterator<Situation> it = store
				.getSituations(
						new TemporalLocationInterval(
								new TemporalLocationDateTime(
										dtf.parseDateTime("2014-03-03T15:30:00.000+02:00")),
								new TemporalLocationDateTime(
										dtf.parseDateTime("2014-03-03T17:30:00.000+02:00"))),
						new RelationBase("http://example.org#storm"),
						new RelationBase("http://example.org#driver"));

		Set<Situation> a = new HashSet<Situation>(Iterators.asList(it));

		Set<Situation> e = new HashSet<Situation>();
		e.add(s1);

		assertEquals(e, a);
	}

	@Test
	@Ignore
	public void test29e() {
		// This seems to work in Stardog but gives an error in Sesame!
		ModuleStore store = new ModuleStoreSail("http://example.org#");

		DateTimeFormatter dtf = ISODateTimeFormat.dateTimeParser()
				.withOffsetParsed();

		Situation s1 = new SituationBase("http://example.org#s1");
		ElementaryInfon i1 = new ElementaryInfonBase("http://example.org#i1");
		s1.addSupportedInfon(i1);
		i1.setRelation(new RelationBase("http://example.org#storm"));
		i1.setPolarity(Polarity.True);
		i1.addRelevantObject(new AttributeTemporalLocation(
				new TemporalLocationInterval(
						"http://example.org#tl11",
						new TemporalLocationDateTime(
								"http://example.org#dt11",
								dtf.parseDateTime("2014-03-03T16:00:00.000+02:00")),
						new TemporalLocationDateTime(
								"http://example.org#dt12",
								dtf.parseDateTime("2014-03-03T17:00:00.000+02:00")))));

		Situation s2 = new SituationBase("http://example.org#s2");
		ElementaryInfon i2 = new ElementaryInfonBase("http://example.org#i2");
		s2.addSupportedInfon(i2);
		i2.setRelation(new RelationBase("http://example.org#storm"));
		i2.setPolarity(Polarity.True);
		i2.addRelevantObject(new AttributeTemporalLocation(
				new TemporalLocationInterval(
						"http://example.org#tl21",
						new TemporalLocationDateTime(
								"http://example.org#dt21",
								dtf.parseDateTime("2014-03-03T18:00:00.000+02:00")),
						new TemporalLocationDateTime(
								"http://example.org#dt22",
								dtf.parseDateTime("2014-03-03T19:00:00.000+02:00")))));

		store.consider(s1);
		store.consider(s2);

		Iterator<Situation> it = store
				.getSituations(
						new TemporalLocationInterval(
								new TemporalLocationDateTime(
										dtf.parseDateTime("2014-03-03T15:00:00.000+02:00")),
								new TemporalLocationDateTime(
										dtf.parseDateTime("2014-03-03T20:00:00.000+02:00"))),
						new RelationBase("http://example.org#storm"));

		Set<Situation> a = new HashSet<Situation>(Iterators.asList(it));

		Set<Situation> e = new HashSet<Situation>();
		e.add(s1);
		e.add(s2);

		assertEquals(e, a);
	}

}
