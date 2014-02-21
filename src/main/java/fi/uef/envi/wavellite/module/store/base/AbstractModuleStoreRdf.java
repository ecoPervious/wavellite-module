/*
 * Copyright (C) 2013 see CREDITS.txt
 * All rights reserved.
 */

package fi.uef.envi.wavellite.module.store.base;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;
import org.openrdf.model.Model;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.ValueFactoryImpl;
import org.openrdf.model.vocabulary.OWL;
import org.openrdf.model.vocabulary.RDF;
import org.openrdf.model.vocabulary.RDFS;
import org.openrdf.model.vocabulary.XMLSchema;

import com.google.common.collect.Iterators;

import fi.uef.envi.wavellite.entity.core.Entity;
import fi.uef.envi.wavellite.entity.core.EntityVisitor;
import fi.uef.envi.wavellite.entity.core.Feature;
import fi.uef.envi.wavellite.entity.core.Property;
import fi.uef.envi.wavellite.entity.core.Sensor;
import fi.uef.envi.wavellite.entity.core.SpatialLocation;
import fi.uef.envi.wavellite.entity.core.TemporalLocation;
import fi.uef.envi.wavellite.entity.core.base.EntityVisitorBase;
import fi.uef.envi.wavellite.entity.core.base.TemporalLocationDateTime;
import fi.uef.envi.wavellite.entity.core.base.TemporalLocationInterval;
import fi.uef.envi.wavellite.entity.derivation.ComponentProperty;
import fi.uef.envi.wavellite.entity.derivation.ComponentPropertyValue;
import fi.uef.envi.wavellite.entity.derivation.Dataset;
import fi.uef.envi.wavellite.entity.derivation.DatasetObservation;
import fi.uef.envi.wavellite.entity.measurement.MeasurementResult;
import fi.uef.envi.wavellite.entity.observation.SensorObservation;
import fi.uef.envi.wavellite.entity.situation.Relation;
import fi.uef.envi.wavellite.entity.situation.Situation;
import fi.uef.envi.wavellite.module.store.ModuleStore;
import fi.uef.envi.wavellite.operator.translation.base.MeasurementResultTranslatorBase;
import fi.uef.envi.wavellite.representation.rdf.EntityRepresentationRdfGeo;
import fi.uef.envi.wavellite.representation.rdf.EntityRepresentationRdfQb;
import fi.uef.envi.wavellite.representation.rdf.EntityRepresentationRdfSsn;
import fi.uef.envi.wavellite.representation.rdf.EntityRepresentationRdfSto;
import fi.uef.envi.wavellite.representation.rdf.EntityRepresentationRdfTime;
import fi.uef.envi.wavellite.vocabulary.DUL;
import fi.uef.envi.wavellite.vocabulary.GeoSPARQL;
import fi.uef.envi.wavellite.vocabulary.QB;
import fi.uef.envi.wavellite.vocabulary.SSN;
import fi.uef.envi.wavellite.vocabulary.STO;
import fi.uef.envi.wavellite.vocabulary.Time;
import fi.uef.envi.wavellite.vocabulary.WOE;

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
	protected EntityRepresentationRdfGeo entityRepresentationGeo = new EntityRepresentationRdfGeo();
	protected EntityRepresentationRdfTime entityRepresentationTime = new EntityRepresentationRdfTime();
	protected EntityRepresentationRdfSsn entityRepresentationSsn = new EntityRepresentationRdfSsn();
	protected EntityRepresentationRdfQb entityRepresentationQb = new EntityRepresentationRdfQb();
	protected EntityRepresentationRdfSto entityRepresentationSto = new EntityRepresentationRdfSto();
	protected MeasurementResultTranslatorBase measurementResultConversion = new MeasurementResultTranslatorBase();

	private boolean isOpen = false;
	private EntityVisitor entityVisitor = new ThisEntityVisitor();
	private static final ValueFactory vf = ValueFactoryImpl.getInstance();
	private static final DateTimeFormatter dtf = ISODateTimeFormat.dateTime()
			.withOffsetParsed();
	private static final Logger log = Logger
			.getLogger(AbstractModuleStoreRdf.class.getName());

	public AbstractModuleStoreRdf() {
	}

	public AbstractModuleStoreRdf(String defaultNamespace) {
		this.defaultNamespace = defaultNamespace;
	}

	@Override
	public String getNamespace() {
		return defaultNamespace;
	}

	@Override
	public void open() {
		entityRepresentationGeo.setNamespace(defaultNamespace);
		entityRepresentationTime.setNamespace(defaultNamespace);
		entityRepresentationSsn.setNamespace(defaultNamespace);
		entityRepresentationQb.setNamespace(defaultNamespace);
		entityRepresentationSto.setNamespace(defaultNamespace);

		isOpen = true;
	}

	@Override
	public void consider(Entity entity) {
		if (!isOpen)
			open();

		entity.accept(entityVisitor);
	}

	@Override
	public void considerAll(Collection<Entity> entities) {
		for (Entity entity : entities)
			consider(entity);
	}

	@Override
	public Iterator<SensorObservation> getSensorObservations(Sensor sensor,
			Property property, Feature feature,
			TemporalLocationInterval interval) {
		if (interval == null) {
			if (log.isLoggable(Level.SEVERE))
				log.severe("Returned empty iterator [interval = null]");

			return Iterators.emptyIterator();
		}

		String sensorId = "?sensorId";
		String propertyId = "?propertyId";
		String featureId = "?featureId";
		String observedBy = "OPTIONAL { ?observationId <" + SSN.observedBy
				+ "> " + sensorId + " }";
		String observedProperty = "OPTIONAL { ?observationId <"
				+ SSN.observedProperty + "> " + propertyId + " }";
		String featureOfInterest = "OPTIONAL { ?observationId <"
				+ SSN.featureOfInterest + "> " + featureId + " }";

		// URIs are assumed here!
		if (sensor != null) {
			sensorId = "<" + sensor.getId() + ">";
			observedBy = "?observationId <" + SSN.observedBy + "> " + sensorId;
		}
		if (property != null) {
			propertyId = "<" + property.getId() + ">";
			observedProperty = "?observationId <" + SSN.observedProperty + "> "
					+ propertyId;
		}
		if (feature != null) {
			featureId = "<" + feature.getId() + ">";
			featureOfInterest = "?observationId <" + SSN.featureOfInterest
					+ "> " + featureId;
		}

		TemporalLocationDateTime start = interval.getStart();
		TemporalLocationDateTime end = interval.getEnd();

		if (start == null) {
			if (log.isLoggable(Level.SEVERE))
				log.severe("Returned empty iterator [start = null; interval = "
						+ interval + "]");

			return Iterators.emptyIterator();
		}

		if (end == null) {
			if (log.isLoggable(Level.SEVERE))
				log.severe("Returned empty iterator [end = null; interval = "
						+ interval + "]");

			return Iterators.emptyIterator();
		}

		DateTime startValue = start.getValue();
		DateTime endValue = end.getValue();

		if (startValue == null) {
			if (log.isLoggable(Level.SEVERE))
				log.severe("Returned empty iterator [startValue = null; interval = "
						+ interval + "]");

			return Iterators.emptyIterator();
		}

		if (endValue == null) {
			if (log.isLoggable(Level.SEVERE))
				log.severe("Returned empty iterator [endValue = null; interval = "
						+ interval + "]");

			return Iterators.emptyIterator();
		}

		if (startValue.isAfter(endValue)) {
			if (log.isLoggable(Level.SEVERE))
				log.severe("Returned empty iterator, start is after end [startValue = "
						+ startValue + "; endValue = " + endValue + "]");

			return Iterators.emptyIterator();
		}

		String from = dtf.print(interval.getStart().getValue());
		String to = dtf.print(interval.getEnd().getValue());

		StringBuffer sb = new StringBuffer();

		sb.append("construct {");
		sb.append("?observationId <" + RDF.TYPE.stringValue() + "> <"
				+ WOE.SensorObservation + "> .");
		sb.append("?observationId <" + SSN.observedBy + "> " + sensorId + " .");
		sb.append(sensorId + " <" + RDF.TYPE.stringValue() + "> <" + SSN.Sensor
				+ "> .");
		sb.append("?observationId <" + SSN.observedProperty + "> " + propertyId
				+ " .");
		sb.append(propertyId + " <" + RDF.TYPE.stringValue() + "> <"
				+ SSN.Property + "> .");
		sb.append("?observationId <" + SSN.featureOfInterest + "> " + featureId
				+ " .");
		sb.append(featureId + " <" + RDF.TYPE.stringValue() + "> <"
				+ SSN.FeatureOfInterest + "> .");
		sb.append("?observationId <" + SSN.observationResultTime
				+ "> ?temporalLocationId .");
		sb.append("?temporalLocationId <" + RDF.TYPE.stringValue()
				+ "> ?temporalLocationType .");
		// If result time is a time point
		sb.append("?temporalLocationId <" + Time.inXSDDateTime
				+ "> ?beginningDateTime .");
		// If result time is a time interval
		sb.append("?temporalLocationId <" + Time.hasBeginning
				+ "> ?beginningId .");
		sb.append("?temporalLocationId <" + Time.hasEnd + "> ?endId .");
		sb.append("?beginningId <" + RDF.TYPE.stringValue() + "> <"
				+ WOE.TimePoint + "> .");
		sb.append("?beginningId <" + Time.inXSDDateTime
				+ "> ?beginningDateTime .");
		sb.append("?endId <" + RDF.TYPE.stringValue() + "> <" + WOE.TimePoint
				+ "> .");
		sb.append("?endId <" + Time.inXSDDateTime + "> ?endDateTime .");
		sb.append("?observationId <" + SSN.observationResult
				+ "> ?sensorOutputId .");
		sb.append("?sensorOutputId <" + RDF.TYPE.stringValue() + "> <"
				+ SSN.SensorOutput + "> .");
		sb.append("?sensorOutputId <" + DUL.hasRegion
				+ "> ?observationValueId .");
		sb.append("?observationValueId <" + RDF.TYPE.stringValue() + "> <"
				+ SSN.ObservationValue + "> .");
		sb.append("?observationValueId <" + DUL.hasRegionDataValue
				+ "> ?observationValue .");
		sb.append("?observationId <" + WOE.observationResultLocation
				+ "> ?spatialLocationId .");
		sb.append("?spatialLocationId <" + RDF.TYPE.stringValue()
				+ "> ?spatialLocationType .");
		// If spatial location is spatial place
		sb.append("?spatialLocationId <" + RDFS.LABEL.stringValue()
				+ "> ?spatialLocationLabel .");
		sb.append("?spatialLocationId <" + OWL.SAMEAS.stringValue()
				+ "> ?spatialLocationSameAs .");
		// If spatial location is spatial region
		sb.append("?spatialLocationId <" + GeoSPARQL.hasGeometry
				+ "> ?geometryId .");
		sb.append("?geometryId <" + RDF.TYPE.stringValue()
				+ "> ?geometryType .");
		sb.append("?geometryId <" + GeoSPARQL.asWKT + "> ?wktLiteral .");
		sb.append("?geometryId <" + GeoSPARQL.asGML + "> ?gmlLiteral .");
		sb.append("} where {");
		sb.append("?observationId <" + RDF.TYPE.stringValue() + "> <"
				+ WOE.SensorObservation + "> .");
		sb.append(observedBy + " .");
		sb.append(observedProperty + " .");
		sb.append(featureOfInterest + " .");
		sb.append("?observationId <" + SSN.observationResultTime
				+ "> ?temporalLocationId .");
		sb.append("?temporalLocationId <" + RDF.TYPE.stringValue()
				+ "> ?temporalLocationType .");
		sb.append("{");
		sb.append("?temporalLocationId <" + Time.hasBeginning
				+ "> ?beginningId .");
		sb.append("?temporalLocationId <" + Time.hasEnd + "> ?endId .");
		sb.append("?beginningId <" + Time.inXSDDateTime
				+ "> ?beginningDateTime .");
		sb.append("?endId <" + Time.inXSDDateTime + "> ?endDateTime .");
		sb.append("}");
		sb.append(" UNION ");
		sb.append("{");
		sb.append("?temporalLocationId <" + Time.inXSDDateTime
				+ "> ?beginningDateTime .");
		sb.append("?temporalLocationId <" + Time.inXSDDateTime
				+ "> ?endDateTime .");
		sb.append("}");
		sb.append("OPTIONAL {");
		sb.append("?observationId <" + SSN.observationResult
				+ "> ?sensorOutputId .");
		sb.append("?sensorOutputId <" + DUL.hasRegion
				+ "> ?observationValueId .");
		sb.append("?observationValueId <" + DUL.hasRegionDataValue
				+ "> ?observationValue .");
		sb.append("}");
		sb.append("OPTIONAL {");
		sb.append("?observationId <" + WOE.observationResultLocation
				+ ">  ?spatialLocationId .");
		sb.append("?spatialLocationId <" + RDF.TYPE.stringValue()
				+ "> ?spatialLocationType .");
		sb.append("{");
		sb.append("?spatialLocationId <" + RDFS.LABEL.stringValue()
				+ "> ?spatialLocationLabel .");
		sb.append("?spatialLocationId <" + OWL.SAMEAS.stringValue()
				+ "> ?spatialLocationSameAs .");
		sb.append("}");
		sb.append(" UNION ");
		sb.append("{");
		sb.append("?spatialLocationId <" + GeoSPARQL.hasGeometry
				+ "> ?geometryId .");
		sb.append("?geometryId <" + RDF.TYPE.stringValue()
				+ "> ?geometryType .");
		sb.append("{");
		sb.append("?geometryId <" + GeoSPARQL.asWKT + "> ?wktLiteral .");
		sb.append("}");
		sb.append(" UNION ");
		sb.append("{");
		sb.append("?geometryId <" + GeoSPARQL.asGML + "> ?gmlLiteral .");
		sb.append("}");
		sb.append("}");
		sb.append("}");
		sb.append(" FILTER (");
		sb.append("?beginningDateTime >= \"" + from + "\"^^<"
				+ XMLSchema.DATETIME + ">");
		sb.append(" && ");
		sb.append("?endDateTime <= \"" + to + "\"^^<" + XMLSchema.DATETIME
				+ ">");
		sb.append(")");
		sb.append("}");

		return createSensorObservations(executeSparql(sb.toString()));
	}

	@Override
	public Iterator<DatasetObservation> getDatasetObservations(Dataset dataset,
			ComponentProperty property, ComponentPropertyValue from,
			ComponentPropertyValue to) {
		if (dataset == null) {
			if (log.isLoggable(Level.SEVERE))
				log.severe("Returned empty iterator [dataset = null]");

			return Iterators.emptyIterator();
		}

		if (property == null) {
			if (log.isLoggable(Level.SEVERE))
				log.severe("Returned empty iterator [property = null]");

			return Iterators.emptyIterator();
		}

		if (from == null) {
			if (log.isLoggable(Level.SEVERE))
				log.severe("Returned empty iterator [from = null]");

			return Iterators.emptyIterator();
		}

		if (to == null) {
			if (log.isLoggable(Level.SEVERE))
				log.severe("Returned empty iterator [to = null]");

			return Iterators.emptyIterator();
		}

		Object fromValue = from.getValue();
		Object toValue = to.getValue();

		if (fromValue == null) {
			if (log.isLoggable(Level.SEVERE))
				log.severe("Returned empty iterator [fromValue = null]");

			return Iterators.emptyIterator();
		}

		if (toValue == null) {
			if (log.isLoggable(Level.SEVERE))
				log.severe("Returned empty iterator [toValue = null]");

			return Iterators.emptyIterator();
		}

		if (!(fromValue.getClass().isInstance(toValue))) {
			if (log.isLoggable(Level.SEVERE))
				log.severe("Returned empty iterator [fromValue = "
						+ fromValue.getClass().getName() + "; toValue = "
						+ toValue.getClass().getName() + "]");

			return Iterators.emptyIterator();
		}

		String datasetId = dataset.getId();
		String propertyId = property.getId();

		StringBuffer sb = new StringBuffer();

		sb.append("construct {");
		sb.append("?observationId <" + RDF.TYPE.stringValue() + "> <"
				+ WOE.DatasetObservation + "> .");
		sb.append("?observationId <" + QB.dataSet + "> <" + datasetId + "> .");
		sb.append("<" + datasetId + "> <" + RDF.TYPE.stringValue() + "> <"
				+ QB.DataSet + "> .");
		sb.append("?observationId ?componentProperty ?componentPropertyValue .");
		// This seems not to work, perhaps because of mixing ABox/TBox
		// sb.append("?componentProperty <" + RDF.TYPE.stringValue() + "> <"
		// + QB.ComponentProperty + "> .");
		sb.append("?componentPropertyValue <" + RDF.TYPE.stringValue()
				+ "> ?temporalLocationType .");
		// If the component property value is a time point
		sb.append("?componentPropertyValue <" + Time.inXSDDateTime
				+ "> ?dateTime .");
		// If the component property value is a time interval
		sb.append("?componentPropertyValue <" + Time.hasBeginning
				+ "> ?beginningId .");
		sb.append("?componentPropertyValue <" + Time.hasEnd + "> ?endId .");
		sb.append("?beginningId <" + RDF.TYPE.stringValue() + "> <"
				+ WOE.TimePoint + "> .");
		sb.append("?beginningId <" + Time.inXSDDateTime
				+ "> ?beginningDateTime .");
		sb.append("?endId <" + RDF.TYPE.stringValue() + "> <" + WOE.TimePoint
				+ "> .");
		sb.append("?endId <" + Time.inXSDDateTime + "> ?endDateTime .");
		sb.append("?componentPropertyValue <" + RDF.TYPE.stringValue()
				+ "> ?spatialLocationType .");
		// If spatial location is spatial place
		sb.append("?componentPropertyValue <" + RDFS.LABEL.stringValue()
				+ "> ?spatialLocationLabel .");
		sb.append("?componentPropertyValue <" + OWL.SAMEAS.stringValue()
				+ "> ?spatialLocationSameAs .");
		// If spatial location is spatial region
		sb.append("?componentPropertyValue <" + GeoSPARQL.hasGeometry
				+ "> ?geometryId .");
		sb.append("?geometryId <" + RDF.TYPE.stringValue()
				+ "> ?geometryType .");
		sb.append("?geometryId <" + GeoSPARQL.asWKT + "> ?wktLiteral .");
		sb.append("?geometryId <" + GeoSPARQL.asGML + "> ?gmlLiteral .");
		sb.append("} where {");
		sb.append("?observationId <" + QB.dataSet + "> <" + datasetId + "> .");
		sb.append("?observationId ?componentProperty ?componentPropertyValue .");
		// If the component property value is a temporal location we need to
		// resolve it; first the case for WOE.TimePoint, then for
		// WOE.TimeInterval
		// TimePoint
		sb.append(" optional {");
		sb.append("?componentPropertyValue <" + RDF.TYPE.stringValue()
				+ "> ?temporalLocationType .");
		sb.append("?componentPropertyValue <" + Time.inXSDDateTime
				+ "> ?dateTime .");
		sb.append("}");
		// TimeInterval
		sb.append(" optional {");
		sb.append("?componentPropertyValue <" + RDF.TYPE.stringValue()
				+ "> ?temporalLocationType .");
		sb.append("?componentPropertyValue <" + Time.hasBeginning
				+ "> ?beginningId .");
		sb.append("?componentPropertyValue <" + Time.hasEnd + "> ?endId .");
		sb.append("?beginningId <" + Time.inXSDDateTime
				+ "> ?beginningDateTime .");
		sb.append("?endId <" + Time.inXSDDateTime + "> ?endDateTime .");
		sb.append("}");
		// SpatialLocationPlace
		sb.append(" optional {");
		sb.append("?componentPropertyValue <" + RDF.TYPE.stringValue()
				+ "> ?spatialLocationType .");
		sb.append(" {");
		sb.append("?componentPropertyValue <" + RDFS.LABEL.stringValue()
				+ "> ?spatialLocationLabel .");
		sb.append("?componentPropertyValue <" + OWL.SAMEAS.stringValue()
				+ "> ?spatialLocationSameAs .");
		sb.append("} union {");
		// SpatialLocationRegion
		sb.append("?componentPropertyValue <" + GeoSPARQL.hasGeometry
				+ "> ?geometryId .");
		sb.append("?geometryId <" + RDF.TYPE.stringValue()
				+ "> ?geometryType .");
		sb.append("{");
		sb.append("?geometryId <" + GeoSPARQL.asWKT + "> ?wktLiteral .");
		sb.append("}");
		sb.append(" union ");
		sb.append("{");
		sb.append("?geometryId <" + GeoSPARQL.asGML + "> ?gmlLiteral .");
		sb.append("}");
		sb.append("}");
		sb.append("}");
		// The case in which the filtered property is for a time point
		sb.append("{");
		sb.append("?observationId <" + propertyId
				+ "> ?filteredComponentPropertyValue .");
		if (from.getValue() instanceof TemporalLocation) {
			sb.append("{");
			sb.append("?filteredComponentPropertyValue <"
					+ RDF.TYPE.stringValue() + "> <" + WOE.TimePoint + "> .");
			sb.append("?filteredComponentPropertyValue <" + Time.inXSDDateTime
					+ "> ?filteredBeginningDateTime .");
			sb.append("?filteredComponentPropertyValue <" + Time.inXSDDateTime
					+ "> ?filteredEndDateTime .");
			sb.append("}");
			sb.append(" union ");
			sb.append("{");
			sb.append("?filteredComponentPropertyValue <"
					+ RDF.TYPE.stringValue() + "> <" + WOE.TimeInterval + "> .");
			sb.append("?filteredComponentPropertyValue <" + Time.hasBeginning
					+ "> ?filteredBeginningId .");
			sb.append("?filteredComponentPropertyValue <" + Time.hasEnd
					+ "> ?filteredEndId .");
			sb.append("?filteredBeginningId <" + Time.inXSDDateTime
					+ "> ?filteredBeginningDateTime .");
			sb.append("?filteredEndId <" + Time.inXSDDateTime
					+ "> ?filteredEndDateTime .");
			sb.append("}");
			sb.append(" filter (");
			sb.append("?filteredBeginningDateTime >= \""
					+ dtf.print(from.getValueAsTemporalLocation()
							.getValueAsDateTime()) + "\"^^<"
					+ XMLSchema.DATETIME + ">");
			sb.append(" && ");
			sb.append("?filteredEndDateTime <= \""
					+ dtf.print(to.getValueAsTemporalLocation()
							.getValueAsDateTime()) + "\"^^<"
					+ XMLSchema.DATETIME + ">");
			sb.append(")");
		}
		// The case in which the filtered property is for a double value
		else if (fromValue instanceof Double && toValue instanceof Double) {
			sb.append(" filter (");
			sb.append("?filteredComponentPropertyValue >= \""
					+ from.getValueAsDouble() + "\"^^<" + XMLSchema.DOUBLE
					+ ">");
			sb.append(" && ");
			sb.append("?filteredComponentPropertyValue <= \""
					+ to.getValueAsDouble() + "\"^^<" + XMLSchema.DOUBLE + ">");
			sb.append(")");
		}
		// The case in which the filtered property is for a integer value
		else if (fromValue instanceof Integer && toValue instanceof Integer) {
			sb.append(" filter (");
			sb.append("?filteredComponentPropertyValue >= \""
					+ from.getValueAsInteger() + "\"^^<" + XMLSchema.INT + ">");
			sb.append(" && ");
			sb.append("?filteredComponentPropertyValue <= \""
					+ to.getValueAsInteger() + "\"^^<" + XMLSchema.INT + ">");
			sb.append(")");
		}
		sb.append("}");
		sb.append("}");

		return createDatasetObservations(executeSparql(sb.toString()));
	}

	public Iterator<Situation> getSituations(Relation relation) {
		if (relation == null) {
			if (log.isLoggable(Level.SEVERE))
				log.severe("Returned empty iterator [relation = null]");

			return Iterators.emptyIterator();
		}

		String relationId = relation.getId();

		StringBuffer sb = new StringBuffer();

		sb.append("construct {");
		sb.append("?situationId <" + RDF.TYPE.stringValue() + "> <"
				+ STO.Situation + "> .");
		sb.append("?situationId <" + STO.supportedInfon
				+ "> ?elementaryInfonId .");
		sb.append("?elementaryInfonId <" + RDF.TYPE.stringValue() + "> <"
				+ STO.ElementaryInfon + "> .");
		sb.append("?elementaryInfonId <" + STO.relation + "> <" + relationId
				+ "> .");
		sb.append("<" + relationId + "> <" + RDF.TYPE.stringValue() + "> <"
				+ STO.Relation + "> .");
		sb.append("?elementaryInfonId <" + STO.polarity + "> ?polarityId .");
		// If anchorN relates to a relevant individual
		sb.append("?elementaryInfonId ?anchorN ?relevantIndividualId .");
		sb.append("?relevantIndividualId <" + RDF.TYPE.stringValue() + "> <"
				+ STO.RelevantIndividual + "> .");
		sb.append("?relevantIndividualId <" + STO.hasAttribute
				+ "> ?attributeId .");
		sb.append("?attributeId <" + RDF.TYPE.stringValue() + "> <"
				+ STO.Attribute + "> .");
		sb.append("?attributeId <" + STO.hasAttributeValue
				+ "> ?attributeValueId .");
		sb.append("?attributeValueId <" + RDF.TYPE.stringValue() + "> <"
				+ STO.Value + "> .");
		sb.append("?attributeValueId <" + STO.attributeValue
				+ "> ?attributeValue .");
		// If anchorN relates to an attribute uri
		sb.append("?elementaryInfonId ?anchorN ?attributeUri .");
//		sb.append("?attributeUri <" + RDF.TYPE.stringValue() + "> <"
//				+ STO.Attribute + "> .");
		// If anchorN relates to an attribute temporal location
		sb.append("?elementaryInfonId ?anchorN ?attributeTemporalLocation .");
		sb.append("?attributeTemporalLocation <" + RDF.TYPE.stringValue()
				+ "> <" + STO.Attribute + "> .");
		sb.append("?attributeTemporalLocation <" + RDF.TYPE.stringValue()
				+ "> ?temporalLocationType .");
		// TimePoint
		sb.append("?attributeTemporalLocation <" + Time.inXSDDateTime
				+ "> ?dateTime .");
		// TimeInterval
		sb.append("?attributeTemporalLocation <" + Time.hasBeginning
				+ "> ?beginningId .");
		sb.append("?attributeTemporalLocation <" + Time.hasEnd + "> ?endId .");
		sb.append("?beginningId <" + RDF.TYPE.stringValue() + "> <"
				+ WOE.TimePoint + "> .");
		sb.append("?beginningId <" + Time.inXSDDateTime
				+ "> ?beginningDateTime .");
		sb.append("?endId <" + RDF.TYPE.stringValue() + "> <" + WOE.TimePoint
				+ "> .");
		sb.append("?endId <" + Time.inXSDDateTime + "> ?endDateTime .");
		sb.append("?elementaryInfonId ?anchorN ?attributeSpatialLocation .");
		// If anchorN relates to an attribute spatial location
		sb.append("?attributeSpatialLocation <" + RDF.TYPE.stringValue()
				+ "> ?spatialLocationType .");
		sb.append("?attributeSpatialLocation <" + RDF.TYPE.stringValue()
				+ "> <" + STO.Attribute + "> .");
		// If spatial location is spatial place
		sb.append("?attributeSpatialLocation <" + RDFS.LABEL.stringValue()
				+ "> ?spatialLocationLabel .");
		sb.append("?attributeSpatialLocation <" + OWL.SAMEAS.stringValue()
				+ "> ?spatialLocationSameAs .");
		// If spatial location is spatial region
		sb.append("?attributeSpatialLocation <" + GeoSPARQL.hasGeometry
				+ "> ?geometryId .");
		sb.append("?geometryId <" + RDF.TYPE.stringValue()
				+ "> ?geometryType .");
		sb.append("?geometryId <" + GeoSPARQL.asWKT + "> ?wktLiteral .");
		sb.append("?geometryId <" + GeoSPARQL.asGML + "> ?gmlLiteral .");
		// WHERE
		sb.append("} where {");
		sb.append("?situationId <" + RDF.TYPE.stringValue() + "> <"
				+ STO.Situation + "> .");
		sb.append("?situationId <" + STO.supportedInfon
				+ "> ?elementaryInfonId .");
		sb.append("?elementaryInfonId <" + STO.relation + "> <" + relationId
				+ "> .");
		sb.append("?elementaryInfonId <" + STO.polarity + "> ?polarityId .");
		// Match relevant objects that are relevant individuals
		sb.append(" optional {");
		sb.append("?elementaryInfonId ?anchorN ?relevantIndividualId .");
		sb.append("?relevantIndividualId <" + RDF.TYPE.stringValue() + "> <"
				+ STO.RelevantIndividual + "> .");
		sb.append(" optional {");
		sb.append("?relevantIndividualId <" + STO.hasAttribute
				+ "> ?attributeId .");
		// This may be optional in case attribute is a URI, temporal or spatial
		// location
		sb.append(" optional {");
		sb.append("?attributeId <" + STO.hasAttributeValue
				+ "> ?attributeValueId .");
		sb.append("?attributeValueId <" + STO.attributeValue
				+ "> ?attributeValue .");
		sb.append("}");
		sb.append("}");
		sb.append("}");
		// Match relevant object that are attribute uri
		sb.append(" optional {");
		sb.append("?elementaryInfonId ?anchorN ?attributeUri .");
		sb.append("}");
		// Match relevant object that are attribute temporal location
		sb.append(" optional {");
		sb.append("?elementaryInfonId ?anchorN ?attributeTemporalLocation .");
		// TimePoint
		sb.append(" optional {");
		sb.append("?attributeTemporalLocation <" + RDF.TYPE.stringValue()
				+ "> ?temporalLocationType .");
		sb.append("?attributeTemporalLocation <" + Time.inXSDDateTime
				+ "> ?dateTime .");
		sb.append("}");
		// TimeInterval
		sb.append(" optional {");
		sb.append("?attributeTemporalLocation <" + RDF.TYPE.stringValue()
				+ "> ?temporalLocationType .");
		sb.append("?attributeTemporalLocation <" + Time.hasBeginning
				+ "> ?beginningId .");
		sb.append("?attributeTemporalLocation <" + Time.hasEnd + "> ?endId .");
		sb.append("?beginningId <" + Time.inXSDDateTime
				+ "> ?beginningDateTime .");
		sb.append("?endId <" + Time.inXSDDateTime + "> ?endDateTime .");
		sb.append("}");
		sb.append("}");
		// Match relevant object that are attribute spatial location
		sb.append(" optional {");
		sb.append("?elementaryInfonId ?anchorN ?attributeSpatialLocation .");
		sb.append("?attributeSpatialLocation <" + RDF.TYPE.stringValue()
				+ "> ?spatialLocationType .");
		// SpatialLocationPlace
		sb.append("{");
		sb.append("?attributeSpatialLocation <" + RDFS.LABEL.stringValue()
				+ "> ?spatialLocationLabel .");
		sb.append("?attributeSpatialLocation <" + OWL.SAMEAS.stringValue()
				+ "> ?spatialLocationSameAs .");
		sb.append("} union {");
		// SpatialLocationRegion
		sb.append("?attributeSpatialLocation <" + GeoSPARQL.hasGeometry
				+ "> ?geometryId .");
		sb.append("?geometryId <" + RDF.TYPE.stringValue()
				+ "> ?geometryType .");
		sb.append("{");
		sb.append("?geometryId <" + GeoSPARQL.asWKT + "> ?wktLiteral .");
		sb.append("}");
		sb.append(" union ");
		sb.append("{");
		sb.append("?geometryId <" + GeoSPARQL.asGML + "> ?gmlLiteral .");
		sb.append("}");
		sb.append("}");
		sb.append("}");
		sb.append("}");

		return createSituations(executeSparql(sb.toString()));
	}

	protected abstract Model executeSparql(String sparql);

	protected Iterator<SensorObservation> createSensorObservations(Model model) {
		List<SensorObservation> ret = new ArrayList<SensorObservation>();

		Iterator<Statement> it = model.filter(null, RDF.TYPE,
				vf.createURI(WOE.SensorObservation)).iterator();

		while (it.hasNext()) {
			Statement statement = it.next();
			Resource subject = statement.getSubject();
			Set<Statement> statements = new HashSet<Statement>();
			getStatements(model, subject, statements);
			ret.add(entityRepresentationSsn.createSensorObservation(statements));
		}

		return ret.iterator();
	}

	protected Iterator<DatasetObservation> createDatasetObservations(Model model) {
		List<DatasetObservation> ret = new ArrayList<DatasetObservation>();

		Iterator<Statement> it = model.filter(null, RDF.TYPE,
				vf.createURI(WOE.DatasetObservation)).iterator();

		while (it.hasNext()) {
			Statement statement = it.next();
			Resource subject = statement.getSubject();
			// This set is concurrent
			Set<Statement> statements = Collections
					.newSetFromMap(new ConcurrentHashMap<Statement, Boolean>());
			getStatements(model, subject, statements);
			// See comment in construct query for dataset observations
			inferComponentProperties(statements, subject);
			ret.add(entityRepresentationQb.createDatasetObservation(statements));
		}

		return ret.iterator();
	}

	protected Iterator<Situation> createSituations(Model model) {
		List<Situation> ret = new ArrayList<Situation>();

		Iterator<Statement> it = model.filter(null, RDF.TYPE,
				vf.createURI(STO.Situation)).iterator();

		while (it.hasNext()) {
			Statement statement = it.next();
			Resource subject = statement.getSubject();
			Set<Statement> statements = Collections
					.newSetFromMap(new ConcurrentHashMap<Statement, Boolean>());
			getStatements(model, subject, statements);
			ret.add(entityRepresentationSto.createSituation(statements));
		}

		return ret.iterator();
	}

	private void getStatements(Model model, Resource subject,
			Set<Statement> statements) {
		Iterator<Statement> it = model.filter(subject, null, null).iterator();

		while (it.hasNext()) {
			Statement statement = it.next();
			statements.add(statement);

			if (statement.getObject() instanceof URI)
				getStatements(model, (Resource) statement.getObject(),
						statements);
		}
	}

	private void inferComponentProperties(Set<Statement> statements,
			Resource observation) {
		for (Statement statement : statements) {
			if (!statement.getSubject().equals(observation))
				continue;

			URI p = statement.getPredicate();

			// TODO it may not be safe to assume that whatever property not
			// listed here is a component property!
			if (p.equals(RDF.TYPE))
				continue;
			if (p.equals(QB.asURI.dataSet))
				continue;

			statements.add(vf.createStatement(p, RDF.TYPE,
					QB.asURI.ComponentProperty));

			if (log.isLoggable(Level.INFO))
				log.info("Predicate inferred to be a component property [p = "
						+ p + "]");
		}
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
			storeAll(entityRepresentationGeo.createRepresentation(entity));
		}

		@Override
		public void visit(Sensor entity) {
			storeAll(entityRepresentationSsn.createRepresentation(entity));
		}

		@Override
		public void visit(Property entity) {
			storeAll(entityRepresentationSsn.createRepresentation(entity));
		}

		@Override
		public void visit(Feature entity) {
			storeAll(entityRepresentationSsn.createRepresentation(entity));
		}

		@Override
		public void visit(Dataset entity) {
			storeAll(entityRepresentationQb.createRepresentation(entity));
		}

	}

}
