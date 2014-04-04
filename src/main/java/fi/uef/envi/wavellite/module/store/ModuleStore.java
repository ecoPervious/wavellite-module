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
import fi.uef.envi.wavellite.entity.derivation.ComponentProperty;
import fi.uef.envi.wavellite.entity.derivation.ComponentPropertyValue;
import fi.uef.envi.wavellite.entity.derivation.Dataset;
import fi.uef.envi.wavellite.entity.derivation.DatasetObservation;
import fi.uef.envi.wavellite.entity.observation.SensorObservation;
import fi.uef.envi.wavellite.entity.situation.Relation;
import fi.uef.envi.wavellite.entity.situation.Situation;
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

	public Iterator<DatasetObservation> getDatasetObservations(Dataset dataset,
			ComponentProperty property, ComponentPropertyValue from,
			ComponentPropertyValue to);

	public Iterator<Situation> getSituations(Relation relation);

	public Iterator<Situation> getSituations();

	public Iterator<Situation> getSituations(Relation... relations);

	public Iterator<Situation> getSituations(TemporalLocationInterval interval);

	public Iterator<Situation> getSituations(TemporalLocationInterval interval,
			Relation... relations);

	public Iterator<Relation> getRelations();

	public String getNamespace();

	public void open();

	public long size();

	public void close();

}
