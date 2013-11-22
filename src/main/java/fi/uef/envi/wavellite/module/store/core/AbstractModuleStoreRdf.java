/*
 * Copyright (C) 2013 see CREDITS.txt
 * All rights reserved.
 */

package fi.uef.envi.wavellite.module.store.core;

import fi.uef.envi.wavellite.entity.derivation.DatasetObservation;
import fi.uef.envi.wavellite.entity.measurement.MeasurementResult;
import fi.uef.envi.wavellite.entity.observation.SensorObservation;
import fi.uef.envi.wavellite.entity.situation.Situation;
import fi.uef.envi.wavellite.function.observation.MeasurementResultConversion;
import fi.uef.envi.wavellite.module.store.ModuleStore;
import fi.uef.envi.wavellite.representation.rdf.EntityRepresentationRdfQb;
import fi.uef.envi.wavellite.representation.rdf.EntityRepresentationRdfSsn;
import fi.uef.envi.wavellite.representation.rdf.EntityRepresentationRdfSto;

/**
 * <p>
 * Title: AbstractModuleStore
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

public abstract class AbstractModuleStoreRdf implements ModuleStore {

	protected String defaultNamespace;
	protected EntityRepresentationRdfSsn entityRepresentationSsn;
	protected EntityRepresentationRdfQb entityRepresentationQb;
	protected EntityRepresentationRdfSto entityRepresentationSto;
	protected MeasurementResultConversion measurementResultConversion;

	public AbstractModuleStoreRdf() {
		measurementResultConversion = new MeasurementResultConversion();
		entityRepresentationSsn = new EntityRepresentationRdfSsn();
		entityRepresentationQb = new EntityRepresentationRdfQb();
		entityRepresentationSto = new EntityRepresentationRdfSto();
	}

	public AbstractModuleStoreRdf(String ns) {
		measurementResultConversion = new MeasurementResultConversion();
		entityRepresentationSsn = new EntityRepresentationRdfSsn(ns);
		entityRepresentationQb = new EntityRepresentationRdfQb(ns);
		entityRepresentationSto = new EntityRepresentationRdfSto(ns);
	}

	@Override
	public void store(MeasurementResult result) {
		store(measurementResultConversion.convert(result));
	}

	@Override
	public void store(SensorObservation observation) {
		store(entityRepresentationSsn.createRepresentation(observation));
	}

	@Override
	public void store(DatasetObservation observation) {
		store(entityRepresentationQb.createRepresentation(observation));
	}

	@Override
	public void store(Situation situation) {
		store(entityRepresentationSto.createRepresentation(situation));
	}

	@Override
	public String getDefaultNamespace() {
		return defaultNamespace;
	}

}
