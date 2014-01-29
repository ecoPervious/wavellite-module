/*
 * Copyright (C) 2013 see CREDITS.txt
 * All rights reserved.
 */

package fi.uef.envi.wavellite.module.store.base;

import java.util.Collection;

import fi.uef.envi.wavellite.entity.core.Entity;
import fi.uef.envi.wavellite.entity.core.EntityVisitor;
import fi.uef.envi.wavellite.entity.core.SpatialLocation;
import fi.uef.envi.wavellite.entity.core.base.EntityVisitorBase;
import fi.uef.envi.wavellite.entity.derivation.DatasetObservation;
import fi.uef.envi.wavellite.entity.measurement.MeasurementResult;
import fi.uef.envi.wavellite.entity.observation.SensorObservation;
import fi.uef.envi.wavellite.entity.situation.Situation;
import fi.uef.envi.wavellite.module.store.ModuleStore;
import fi.uef.envi.wavellite.operator.translation.base.MeasurementResultTranslatorBase;
import fi.uef.envi.wavellite.representation.rdf.EntityRepresentationRdf;
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
	protected EntityRepresentationRdf entityRepresentation = new EntityRepresentationRdf();
	protected EntityRepresentationRdfSsn entityRepresentationSsn = new EntityRepresentationRdfSsn();
	protected EntityRepresentationRdfQb entityRepresentationQb = new EntityRepresentationRdfQb();
	protected EntityRepresentationRdfSto entityRepresentationSto = new EntityRepresentationRdfSto();
	protected MeasurementResultTranslatorBase measurementResultConversion = new MeasurementResultTranslatorBase();

	private EntityVisitor entityVisitor = new ThisEntityVisitor();

	public AbstractModuleStoreRdf() {
	}

	public AbstractModuleStoreRdf(String ns) {
		entityRepresentation.setNamespace(ns);
		entityRepresentationSsn.setNamespace(ns);
		entityRepresentationQb.setNamespace(ns);
		entityRepresentationSto.setNamespace(ns);
	}

	@Override
	public void consider(Entity entity) {
		entity.accept(entityVisitor);
	}

	@Override
	public void considerAll(Collection<Entity> entities) {
		for (Entity entity : entities)
			consider(entity);
	}

	@Override
	public String getNamespace() {
		return defaultNamespace;
	}

	private class ThisEntityVisitor extends EntityVisitorBase {

		@Override
		public void visit(MeasurementResult entity) {
			visit(measurementResultConversion.translate(entity));
		}

		@Override
		public void visit(SensorObservation entity) {
			storeAll(entityRepresentationSsn.createRepresentation(entity));
		}

		@Override
		public void visit(DatasetObservation entity) {
			storeAll(entityRepresentationQb.createRepresentation(entity));
		}

		@Override
		public void visit(Situation entity) {
			storeAll(entityRepresentationSto.createRepresentation(entity));
		}

		@Override
		public void visit(SpatialLocation entity) {
			storeAll(entityRepresentation.createRepresentation(entity));
		}

	}

}
