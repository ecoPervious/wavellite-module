/*
 * Copyright (C) 2013 see CREDITS.txt
 * All rights reserved.
 */

package fi.uef.envi.wavellite.module.store.stardog.test;

import java.util.Set;

import org.joda.time.DateTime;
import org.junit.Test;
import org.openrdf.model.Statement;

import com.complexible.stardog.reasoning.api.ReasoningType;

import fi.uef.envi.wavellite.entity.core.Feature;
import fi.uef.envi.wavellite.entity.core.Property;
import fi.uef.envi.wavellite.entity.core.Sensor;
import fi.uef.envi.wavellite.entity.core.base.FeatureBase;
import fi.uef.envi.wavellite.entity.core.base.PropertyBase;
import fi.uef.envi.wavellite.entity.core.base.SensorBase;
import fi.uef.envi.wavellite.entity.core.base.TemporalLocationDateTime;
import fi.uef.envi.wavellite.entity.measurement.MeasurementResult;
import fi.uef.envi.wavellite.entity.measurement.MeasurementValue;
import fi.uef.envi.wavellite.entity.measurement.MeasurementValueContext;
import fi.uef.envi.wavellite.entity.measurement.base.MeasurementResultBase;
import fi.uef.envi.wavellite.entity.measurement.base.MeasurementValueContextBase;
import fi.uef.envi.wavellite.entity.measurement.base.MeasurementValueDouble;
import fi.uef.envi.wavellite.entity.observation.SensorObservation;
import fi.uef.envi.wavellite.function.observation.MeasurementResultConverter;
import fi.uef.envi.wavellite.module.store.stardog.StoreModuleStardog;
import fi.uef.envi.wavellite.representation.rdf.EntityRepresentationRdfSsn;

/**
 * <p>
 * Title: StoreModuleStardogTest
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

public class StoreModuleStardogTest {

	@Test
	public void test1() {
		StoreModuleStardog store = new StoreModuleStardog("snarl", "localhost",
				5820, "test", "admin", "admin", ReasoningType.NONE);

		MeasurementResult mr = new MeasurementResultBase();
		MeasurementValue mv = new MeasurementValueDouble(0.5);
		MeasurementValueContext mvc = new MeasurementValueContextBase();
		mr.setValue(mv);
		mr.setContext(mvc);

		Sensor s = new SensorBase("http://example.org#s1");
		Property p = new PropertyBase("http://example.org#p1");
		Feature f = new FeatureBase("http://example.org#f1");

		mvc.setSensor(s);
		mvc.setProperty(p);
		mvc.setFeature(f);
		mvc.setTemporalLocation(new TemporalLocationDateTime(new DateTime(2013,
				10, 31, 1, 0, 0)));

		MeasurementResultConverter mrc = new MeasurementResultConverter();
		EntityRepresentationRdfSsn er = new EntityRepresentationRdfSsn(
				"http://example.org");

		SensorObservation so = mrc.convert(mr);
		Set<Statement> statements = er.createRepresentation(so);

		store.store(statements);

		store.close();
	}

}
