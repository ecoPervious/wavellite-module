/*
 * Copyright (C) 2013 see CREDITS.txt
 * All rights reserved.
 */

package fi.uef.envi.wavellite.module.store.stardog.test;

import java.util.Set;

import org.junit.Test;
import org.openrdf.model.Statement;

import com.complexible.stardog.reasoning.api.ReasoningType;

import fi.uef.envi.wavellite.entity.core.Feature;
import fi.uef.envi.wavellite.entity.core.Property;
import fi.uef.envi.wavellite.entity.core.Sensor;
import fi.uef.envi.wavellite.entity.core.base.FeatureBase;
import fi.uef.envi.wavellite.entity.core.base.PropertyBase;
import fi.uef.envi.wavellite.entity.core.base.SensorBase;
import fi.uef.envi.wavellite.entity.observation.SensorObservation;
import fi.uef.envi.wavellite.entity.observation.base.SensorObservationBase;
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
		StoreModuleStardog store = new StoreModuleStardog("snarl", "localhost", 5820,
				"test", "admin", "admin", ReasoningType.NONE);
		
		SensorObservation so = new SensorObservationBase();
		Sensor s = new SensorBase("http://example.org#s1");
		Property p = new PropertyBase("http://example.org#p1");
		Feature f = new FeatureBase("http://example.org#f1");
		
		so.setSensor(s);
		so.setProperty(p);
		so.setFeature(f);
		
		EntityRepresentationRdfSsn er = new EntityRepresentationRdfSsn("http://example.org");
		
		Set<Statement> statements = er.createRepresentation(so);
		
		store.store(statements);
		
		store.close();
	}

}
