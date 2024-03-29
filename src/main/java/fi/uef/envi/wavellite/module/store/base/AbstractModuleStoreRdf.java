/*
 * Copyright (C) 2013 see CREDITS.txt
 * All rights reserved.
 */

package fi.uef.envi.wavellite.module.store.base;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.collections4.IteratorUtils;
import org.joda.time.DateTime;
import org.joda.time.Interval;
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
import fi.uef.envi.wavellite.representation.rdf.EntityRepresentationRdfProv;
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
	protected EntityRepresentationRdfGeo entityRepresentationGeo;
	protected EntityRepresentationRdfTime entityRepresentationTime;
	protected EntityRepresentationRdfProv entityRepresentationProv;
	protected EntityRepresentationRdfSsn entityRepresentationSsn;
	protected EntityRepresentationRdfQb entityRepresentationQb;
	protected EntityRepresentationRdfSto entityRepresentationSto;
	protected MeasurementResultTranslatorBase measurementResultConversion;
	protected boolean isOpen = false;

	private EntityVisitor entityVisitor = new ThisEntityVisitor();
	private Comparator<SensorObservation> sensorObservationTimeComparator = new SensorObservationTimeComparator();
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
		if (entityRepresentationGeo == null)
			entityRepresentationGeo = new EntityRepresentationRdfGeo(
					defaultNamespace);
		if (entityRepresentationTime == null)
			entityRepresentationTime = new EntityRepresentationRdfTime(
					defaultNamespace);
		if (entityRepresentationProv == null)
			entityRepresentationProv = new EntityRepresentationRdfProv(
					defaultNamespace);
		if (entityRepresentationSsn == null)
			entityRepresentationSsn = new EntityRepresentationRdfSsn(
					defaultNamespace, entityRepresentationGeo,
					entityRepresentationTime);
		if (entityRepresentationQb == null)
			entityRepresentationQb = new EntityRepresentationRdfQb(
					defaultNamespace, entityRepresentationGeo,
					entityRepresentationTime);
		if (entityRepresentationSto == null)
			entityRepresentationSto = new EntityRepresentationRdfSto(
					defaultNamespace, entityRepresentationGeo,
					entityRepresentationTime);
		if (measurementResultConversion == null)
			measurementResultConversion = new MeasurementResultTranslatorBase();

		isOpen = true;
	}

	@Override
	public void close() {
		isOpen = false;
	}

	@Override
	public void flush() {

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
		return getSensorObservations(sensor, property, feature, interval, false);
	}

	@Override
	public Iterator<SensorObservation> getSensorObservations(Sensor sensor,
			Property property, Feature feature,
			TemporalLocationInterval interval, boolean sort) {
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
		sb.append("?endDateTime < \"" + to + "\"^^<" + XMLSchema.DATETIME + ">");
		sb.append(")");
		sb.append("}");

		Iterator<SensorObservation> it = createSensorObservations(executeSelectQuery(sb
				.toString()));

		if (!sort)
			return it;

		List<SensorObservation> list = IteratorUtils.toList(it);

		Collections.sort(list, sensorObservationTimeComparator);

		return list.iterator();
	}

	@Override
	public Iterator<DatasetObservation> getDatasetObservations(Dataset dataset) {
		return getDatasetObservations(dataset, null, null, null);
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
			if (log.isLoggable(Level.INFO))
				log.info("Unconstrained query [property = null]");
		}

		Object fromValue = null;
		Object toValue = null;
		String propertyId = null;

		if (property != null) {
			propertyId = property.getId();

			if (from == null) {
				if (log.isLoggable(Level.INFO))
					log.info("Returned empty iterator [property = " + property
							+ "; from = null]");

				return Iterators.emptyIterator();
			}

			if (to == null) {
				if (log.isLoggable(Level.INFO))
					log.info("Returned empty iterator [property = " + property
							+ "; to = null]");

				return Iterators.emptyIterator();
			}

			fromValue = from.getValue();
			toValue = to.getValue();

			if (fromValue == null) {
				if (log.isLoggable(Level.SEVERE))
					log.severe("Returned empty iterator [property = "
							+ property + "; fromValue = null]");

				return Iterators.emptyIterator();
			}

			if (toValue == null) {
				if (log.isLoggable(Level.SEVERE))
					log.severe("Returned empty iterator [property = "
							+ property + "; toValue = null]");

				return Iterators.emptyIterator();
			}

			if (!(fromValue.getClass().isInstance(toValue))) {
				if (log.isLoggable(Level.SEVERE))
					log.severe("Returned empty iterator [fromValue = "
							+ fromValue.getClass().getName() + "; toValue = "
							+ toValue.getClass().getName() + "]");

				return Iterators.emptyIterator();
			}
		}

		String datasetId = dataset.getId();

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
		// PropertyId may be null in case of an unconstrained query
		if (propertyId != null) {
			// The case in which the filtered property is for a time point
			sb.append("{");
			sb.append("?observationId <" + propertyId
					+ "> ?filteredComponentPropertyValue .");
			if (fromValue instanceof TemporalLocation) {
				sb.append("{");
				sb.append("?filteredComponentPropertyValue <"
						+ RDF.TYPE.stringValue() + "> <" + WOE.TimePoint
						+ "> .");
				sb.append("?filteredComponentPropertyValue <"
						+ Time.inXSDDateTime + "> ?filteredBeginningDateTime .");
				sb.append("?filteredComponentPropertyValue <"
						+ Time.inXSDDateTime + "> ?filteredEndDateTime .");
				sb.append("}");
				sb.append(" union ");
				sb.append("{");
				sb.append("?filteredComponentPropertyValue <"
						+ RDF.TYPE.stringValue() + "> <" + WOE.TimeInterval
						+ "> .");
				sb.append("?filteredComponentPropertyValue <"
						+ Time.hasBeginning + "> ?filteredBeginningId .");
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
				sb.append("?filteredEndDateTime < \""
						+ dtf.print(to.getValueAsTemporalLocation()
								.getValueAsDateTime()) + "\"^^<"
						+ XMLSchema.DATETIME + ">");
				sb.append(")");
			}
			// The case in which the filtered property is for a double value
			else if (fromValue instanceof Double) {
				sb.append(" filter (");
				sb.append("?filteredComponentPropertyValue >= \""
						+ from.getValueAsDouble() + "\"^^<" + XMLSchema.DOUBLE
						+ ">");
				sb.append(" && ");
				sb.append("?filteredComponentPropertyValue <= \""
						+ to.getValueAsDouble() + "\"^^<" + XMLSchema.DOUBLE
						+ ">");
				sb.append(")");
			}
			// The case in which the filtered property is for a integer value
			else if (fromValue instanceof Integer) {
				sb.append(" filter (");
				sb.append("?filteredComponentPropertyValue >= \""
						+ from.getValueAsInteger() + "\"^^<" + XMLSchema.INT
						+ ">");
				sb.append(" && ");
				sb.append("?filteredComponentPropertyValue <= \""
						+ to.getValueAsInteger() + "\"^^<" + XMLSchema.INT
						+ ">");
				sb.append(")");
			}
			// TODO how about other types?
			sb.append("}");
		}
		sb.append("}");

		return createDatasetObservations(executeSelectQuery(sb.toString()));
	}

	public Iterator<Situation> getSituations() {
		return getSituations(new Relation[] {});
	}

	public Iterator<Situation> getSituations(Relation... relations) {
		return getSituations(null, relations);
	}

	public Iterator<Situation> getSituations(TemporalLocationInterval interval) {
		return getSituations(interval, new Relation[] {});
	}

	public Iterator<Situation> getSituations(TemporalLocationInterval interval,
			Relation relation) {
		if (relation == null)
			return getSituations(interval, new Relation[] {});

		return getSituations(interval, new Relation[] { relation });
	}

	public Iterator<Situation> getSituations(TemporalLocationInterval interval,
			Relation... relations) {
		if (relations == null) {
			relations = new Relation[] {};
		}

		if (relations.length == 0) {
			if (log.isLoggable(Level.WARNING))
				log.severe("Unconstrained relation [relations.length = 0]");
		}

		StringBuffer sb = new StringBuffer();

		sb.append("construct {");
		sb.append("?situationId <" + RDF.TYPE.stringValue() + "> <"
				+ STO.Situation + "> .");
		sb.append("?situationId <" + STO.supportedInfon
				+ "> ?elementaryInfonId .");
		sb.append("?elementaryInfonId <" + RDF.TYPE.stringValue() + "> <"
				+ STO.ElementaryInfon + "> .");
		sb.append("?elementaryInfonId <" + STO.relation + "> ?relationId .");
		sb.append("?relationId <" + RDF.TYPE.stringValue() + "> <"
				+ STO.Relation + "> .");
		sb.append("?elementaryInfonId <" + STO.polarity + "> ?polarityId .");
		// If anchorN relates to a relevant individual
		sb.append("?elementaryInfonId ?anchorA ?relevantIndividualId .");
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
		// This is somehow problematic because it will match also, e.g. temporal
		// locations
		// sb.append("?elementaryInfonId ?anchorB ?attributeUri .");
		// If anchorN relates to an attribute temporal location
		// TimePoint
		sb.append("?elementaryInfonId ?anchorC ?attributeTimePointId .");
		sb.append("?attributeTimePointId <" + RDF.TYPE.stringValue() + "> <"
				+ WOE.TimePoint + "> .");
		sb.append("?attributeTimePointId <" + Time.inXSDDateTime
				+ "> ?beginningDateTime .");
		// TimeInterval
		sb.append("?elementaryInfonId ?anchorD ?attributeTimeIntervalId .");
		sb.append("?attributeTimeIntervalId <" + RDF.TYPE.stringValue() + "> <"
				+ WOE.TimeInterval + "> .");
		sb.append("?attributeTimeIntervalId <" + Time.hasBeginning
				+ "> ?beginningId .");
		sb.append("?attributeTimeIntervalId <" + Time.hasEnd + "> ?endId .");
		sb.append("?beginningId <" + RDF.TYPE.stringValue() + "> <"
				+ WOE.TimePoint + "> .");
		sb.append("?beginningId <" + Time.inXSDDateTime
				+ "> ?beginningDateTime .");
		sb.append("?endId <" + RDF.TYPE.stringValue() + "> <" + WOE.TimePoint
				+ "> .");
		sb.append("?endId <" + Time.inXSDDateTime + "> ?endDateTime .");
		// If anchorN relates to an attribute spatial location
		sb.append("?elementaryInfonId ?anchorE ?attributeSpatialPlaceId .");
		// If spatial location is spatial place
		sb.append("?attributeSpatialPlaceId <" + RDF.TYPE.stringValue() + "> <"
				+ WOE.SpatialPlace + "> .");
		sb.append("?attributeSpatialPlaceId <" + RDFS.LABEL.stringValue()
				+ "> ?spatialLocationLabel .");
		sb.append("?attributeSpatialPlaceId <" + OWL.SAMEAS.stringValue()
				+ "> ?spatialLocationSameAs .");
		// If spatial location is spatial region
		sb.append("?elementaryInfonId ?anchorF ?attributeSpatialRegionId .");
		sb.append("?attributeSpatialRegionId <" + RDF.TYPE.stringValue()
				+ "> <" + WOE.SpatialRegion + "> .");
		sb.append("?attributeSpatialRegionId <" + GeoSPARQL.hasGeometry
				+ "> ?geometryId .");
		sb.append("?geometryId <" + RDF.TYPE.stringValue()
				+ "> ?geometryType .");
		sb.append("?geometryId <" + GeoSPARQL.asWKT + "> ?wktLiteral .");
		sb.append("?geometryId <" + GeoSPARQL.asGML + "> ?gmlLiteral .");
		// Value
		sb.append("?elementaryInfonId ?anchorG ?valueId .");
		sb.append("?valueId <" + RDF.TYPE.stringValue() + "> <" + STO.Value
				+ "> .");
		sb.append("?valueId <" + STO.asURI.attributeValue + "> ?value .");
		// WHERE
		sb.append("} where {");
		sb.append("?situationId <" + RDF.TYPE.stringValue() + "> <"
				+ STO.Situation + "> .");
		sb.append("?situationId <" + STO.supportedInfon
				+ "> ?elementaryInfonId .");
		sb.append("?elementaryInfonId <" + STO.relation + "> ?relationId .");
		sb.append("?elementaryInfonId <" + STO.polarity + "> ?polarityId .");
		// Match relevant objects that are relevant individuals
		sb.append(" optional {");
		sb.append("?elementaryInfonId ?anchorA ?relevantIndividualId .");
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
		// // Match relevant object that are attribute uri
		// sb.append(" optional {");
		// sb.append("?elementaryInfonId ?anchorB ?attributeUri .");
		// sb.append("}");
		// Match relevant object that are attribute temporal location
		// TimePoint
		sb.append(" optional {");
		sb.append("?elementaryInfonId ?anchorC ?attributeTimePointId .");
		sb.append("?attributeTimePointId <" + RDF.TYPE.stringValue() + "> <"
				+ WOE.TimePoint + "> .");
		sb.append("?attributeTimePointId <" + Time.inXSDDateTime
				+ "> ?beginningDateTime .");
		sb.append("?attributeTimePointId <" + Time.inXSDDateTime
				+ "> ?endDateTime .");
		sb.append("}");
		// TimeInterval
		sb.append(" optional {");
		sb.append("?elementaryInfonId ?anchorD ?attributeTimeIntervalId .");
		sb.append("?attributeTimeIntervalId <" + RDF.TYPE.stringValue() + "> <"
				+ WOE.TimeInterval + "> .");
		sb.append("?attributeTimeIntervalId <" + Time.hasBeginning
				+ "> ?beginningId .");
		sb.append("?beginningId <" + RDF.TYPE.stringValue() + "> <"
				+ WOE.TimePoint + "> .");
		sb.append("?attributeTimeIntervalId <" + Time.hasEnd + "> ?endId .");
		sb.append("?endId <" + RDF.TYPE.stringValue() + "> <" + WOE.TimePoint
				+ "> .");
		sb.append("?beginningId <" + Time.inXSDDateTime
				+ "> ?beginningDateTime .");
		sb.append("?endId <" + Time.inXSDDateTime + "> ?endDateTime .");
		sb.append("}");
		// Match relevant object that are attribute spatial location
		sb.append(" optional {");
		// SpatialPlace
		sb.append("?elementaryInfonId ?anchorE ?attributeSpatialPlaceId .");
		sb.append("?attributeSpatialPlaceId <" + RDF.TYPE.stringValue() + "> <"
				+ WOE.SpatialPlace + "> .");
		sb.append(" optional {");
		sb.append("?attributeSpatialPlaceId <" + RDFS.LABEL.stringValue()
				+ "> ?spatialLocationLabel .");
		sb.append("}");
		sb.append(" optional {");
		sb.append("?attributeSpatialPlaceId <" + OWL.SAMEAS.stringValue()
				+ "> ?spatialLocationSameAs .");
		sb.append("}");
		sb.append("}");
		sb.append(" optional {");
		// SpatialRegion
		sb.append("?elementaryInfonId ?anchorF ?attributeSpatialRegionId .");
		sb.append("?attributeSpatialRegionId <" + RDF.TYPE.stringValue()
				+ "> <" + WOE.SpatialRegion + "> .");
		sb.append("?attributeSpatialRegionId <" + GeoSPARQL.hasGeometry
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
		sb.append(" optional {");
		// Value
		sb.append("?elementaryInfonId ?anchorG ?valueId .");
		sb.append("?valueId <" + RDF.TYPE.stringValue() + "> <" + STO.Value
				+ "> .");
		sb.append("?valueId <" + STO.asURI.attributeValue + "> ?value .");
		sb.append("}");

		// Filter relations
		if (relations.length > 0) {
			sb.append("filter (");

			for (int i = 0; i < relations.length; i++) {
				if (i > 0)
					sb.append(" || ");

				sb.append("?relationId = <" + relations[i].getId() + ">");
			}

			sb.append(")");
		}

		// Filter beginning and end date time
		if (interval != null) {
			sb.append("filter (");

			TemporalLocationDateTime start = interval.getStart();
			TemporalLocationDateTime end = interval.getEnd();

			if (start != null) {
				sb.append("?beginningDateTime >= \""
						+ dtf.print(start.getValueAsDateTime()) + "\"^^<"
						+ XMLSchema.DATETIME + ">");
			}

			if (start != null && end != null) {
				sb.append(" && ");
			}

			if (end != null) {
				sb.append("?endDateTime < \""
						+ dtf.print(end.getValueAsDateTime()) + "\"^^<"
						+ XMLSchema.DATETIME + ">");
			}

			sb.append(")");
		}

		sb.append("}");

		return createSituations(executeSelectQuery(sb.toString()));
	}

	public Iterator<Situation> getSituations(Relation relation) {
		return getSituations(Collections.singletonList(relation).toArray(
				new Relation[] {}));
	}

	public void deleteSituation(Situation situation) {
		StringBuffer sb = new StringBuffer();

		sb.append("delete {");
		sb.append("<" + situation.getId() + "> <" + RDF.TYPE.stringValue()
				+ "> <" + STO.Situation + "> .");
		sb.append("<" + situation.getId() + "> <" + STO.supportedInfon
				+ "> ?supportedInfon .");
		sb.append("?supportedInfon ?property ?object .");
		sb.append("} where {");
		sb.append("<" + situation.getId() + "> <" + RDF.TYPE.stringValue()
				+ "> <" + STO.Situation + "> .");
		sb.append("<" + situation.getId() + "> <" + STO.supportedInfon
				+ "> ?supportedInfon .");
		sb.append("?supportedInfon ?property ?object .");
		sb.append("}");

		executeDeleteQuery(sb.toString());
	}

	public Iterator<Relation> getRelations() {
		StringBuffer sb = new StringBuffer();

		sb.append("construct {");
		sb.append("?relationId <" + RDF.TYPE.stringValue() + "> <"
				+ STO.Relation + ">");
		sb.append("} where {");
		sb.append("?relationId <" + RDF.TYPE.stringValue() + "> <"
				+ STO.Relation + ">");
		sb.append("}");

		return createRelations(executeSelectQuery(sb.toString()));
	}

	protected abstract void executeDeleteQuery(String sparql);

	protected abstract Model executeSelectQuery(String sparql);

	protected Iterator<SensorObservation> createSensorObservations(Model model) {
		if (!isOpen)
			open();

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
		if (!isOpen)
			open();

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
		if (!isOpen)
			open();

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

	protected Iterator<Relation> createRelations(Model model) {
		if (!isOpen)
			open();

		List<Relation> ret = new ArrayList<Relation>();

		Iterator<Statement> it = model.filter(null, RDF.TYPE,
				vf.createURI(STO.Relation)).iterator();

		while (it.hasNext()) {
			Statement statement = it.next();
			Resource subject = statement.getSubject();
			Set<Statement> statements = Collections
					.newSetFromMap(new ConcurrentHashMap<Statement, Boolean>());
			getStatements(model, subject, statements);
			ret.add(entityRepresentationSto.createRelation(statements));
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
			// TODO this should perhaps be an optional feature
			storeAll(entityRepresentationProv.createRepresentation(entity));
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
		public void visit(Relation entity) {
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

	private class SensorObservationTimeComparator implements
			Comparator<SensorObservation> {

		@Override
		public int compare(SensorObservation o1, SensorObservation o2) {
			TemporalLocation l1 = o1.getTemporalLocation();
			TemporalLocation l2 = o2.getTemporalLocation();

			if (l1 == null || l2 == null)
				throw new NullPointerException("Cannot compare time [l1 = "
						+ l1 + "; l2 = " + l2 + "]");

			if (l1 instanceof TemporalLocationDateTime
					&& l2 instanceof TemporalLocationDateTime) {
				DateTime t1 = l1.getValueAsDateTime();
				DateTime t2 = l2.getValueAsDateTime();

				if (t1 == null || t2 == null)
					throw new NullPointerException("Cannot compare time [t1 = "
							+ t1 + "; t2 = " + t2 + "]");

				if (t1.isBefore(t2))
					return -1;
				if (t1.isAfter(t2))
					return 1;

				return 0;
			}

			if (l1 instanceof TemporalLocationInterval
					&& l2 instanceof TemporalLocationInterval) {
				Interval i1 = l1.getValueAsInterval();
				Interval i2 = l2.getValueAsInterval();

				if (i1 == null || i2 == null)
					throw new NullPointerException("Cannot compare time [i1 = "
							+ i1 + "; i2 = " + i2 + "]");

				if (i1.isBefore(i2))
					return -1;
				if (i1.isAfter(i2))
					return 1;

				return 0;
			}

			throw new RuntimeException("Cannot compare time [l1 = " + l1
					+ "; l2 = " + l2 + "]");
		}

	}

}
