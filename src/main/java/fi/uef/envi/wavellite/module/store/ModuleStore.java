/*
 * Copyright (C) 2013 see CREDITS.txt
 * All rights reserved.
 */

package fi.uef.envi.wavellite.module.store;

import java.util.Iterator;
import java.util.Set;

import org.openrdf.model.Statement;

import fi.uef.envi.wavellite.entity.core.Entity;
import fi.uef.envi.wavellite.entity.core.Feature;
import fi.uef.envi.wavellite.entity.core.Property;
import fi.uef.envi.wavellite.entity.core.Sensor;
import fi.uef.envi.wavellite.entity.core.base.TemporalLocationInterval;
import fi.uef.envi.wavellite.entity.observation.SensorObservation;
import fi.uef.envi.wavellite.module.Module;

/**
 * <p>
 * Title: ModuleStore
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

public interface ModuleStore extends Module<Entity> {

	public void store(Statement statement);

	public void storeAll(Set<Statement> statements);
	
	public Iterator<SensorObservation> getSensorObservations(Sensor sensor,
			Property property, Feature feature,
			TemporalLocationInterval interval);

	public String getNamespace();

	public void open();

	public void close();

}
