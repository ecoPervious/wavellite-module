/*
 * Copyright (C) 2012 see CREDITS.txt
 * All rights reserved.
 */

package fi.uef.envi.wavellite.module.store.stardog;

import java.util.Set;

import org.openrdf.model.Namespace;
import org.openrdf.model.Statement;

import com.complexible.common.rdf.model.Namespaces;
import com.complexible.stardog.StardogException;
import com.complexible.stardog.api.Adder;
import com.complexible.stardog.api.Connection;
import com.complexible.stardog.api.ConnectionConfiguration;
import com.complexible.stardog.reasoning.api.ReasoningType;

import fi.uef.envi.wavellite.module.store.StoreModule;

/**
 * <p>
 * Title: StoreModuleStardog
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Project: Wavellite Module
 * </p>
 * <p>
 * Copyright: Copyright (C) 2012
 * </p>
 * 
 * @author Markus Stocker, markus.stocker@uef.fi
 */

public class StoreModuleStardog implements StoreModule {

	private String protocol;
	private String host;
	private int port;
	private String database;
	private String user;
	private String password;
	private ReasoningType reasoningType;
	private String url;
	private Connection conn;
	private Namespaces namespaces;
	private String defaultNamespace;

	public StoreModuleStardog(String database) {
		this("localhost", database);
	}
	
	public StoreModuleStardog(String host, String database) {
		this(host, 5820, database, "admin", "admin");
	}
	
	public StoreModuleStardog(String host, int port, String database,
			String user, String password) {
		this("snarl", host, port, database, user, password, ReasoningType.NONE);
	}
	
	public StoreModuleStardog(String protocol, String host, int port,
			String database, String user, String password,
			ReasoningType reasoningType) {
		if (protocol == null)
			throw new NullPointerException("[protocol = null]");
		if (!protocol.equals("snarl"))
			throw new RuntimeException("Unsupported protocol [protocol = "
					+ protocol + "]");
		if (host == null)
			throw new NullPointerException("[host = null]");
		if (port < 0 || port > 65535)
			throw new RuntimeException("Invalid port [port = " + port + "]");
		if (database == null)
			throw new NullPointerException("[database = null]");
		if (user == null)
			throw new NullPointerException("[user = null]");
		if (password == null)
			throw new NullPointerException("[password = null]");
		if (reasoningType == null)
			reasoningType = ReasoningType.NONE;

		this.protocol = protocol;
		this.host = host;
		this.port = port;
		this.database = database;
		this.user = user;
		this.password = password;
		this.reasoningType = reasoningType;

		this.url = protocol + "://" + host + ":" + port;

		ConnectionConfiguration conf = ConnectionConfiguration.to(database)
				.server(url).credentials(user, password);

		try {
			this.conn = conf.connect();
			this.namespaces = conn.namespaces();

			for (Namespace namespace : namespaces) {
				if (namespace.equals(Namespaces.DEFAULT))
					this.defaultNamespace = namespace.getName();
			}
		} catch (StardogException e) {
			e.printStackTrace();
		}

	}

	public String getDefaultNamespace() {
		return defaultNamespace;
	}
	
	@Override
	public void store(Set<Statement> statements) {
		try {
			conn.begin();
			
			Adder adder = conn.add();
			
			for (Statement statement: statements) {
				adder.statement(statement);
			}
		
			conn.commit();
		} catch (StardogException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void close() {
		try {
			conn.close();
		} catch (StardogException e) {
			e.printStackTrace();
		}
	}
	
	// private ReasoningConnection rc;
	// // The base connection with no reasoning
	// private Connection bc;
	// // TODO Remove
	// // private ElementaryInfonObjectVisitor relevantObjectVisitor;
	// private static final Logger log = Logger
	// .getLogger(StoreModuleStardog.class.getName());
	// private final DateTimeFormatter dtf = ISODateTimeFormat.dateTime()
	// .withOffsetParsed();
	//
	// public StoreModuleStardog(String url, String database, String user,
	// String password) {
	// this(url, database, user, password, ReasoningType.QL);
	// }
	//
	// public StoreModuleStardog(String url, String database, String user,
	// String password, ReasoningType reasoningType) {
	// this(null, WO.KnowledgeStore, url, database, user, password,
	// ReasoningType.QL);
	// }
	//
	// public StoreModuleStardog(String id, String type, String url,
	// String database, String user, String password,
	// ReasoningType reasoningType) {
	// super(id, type);
	//
	// if (user == null) {
	// throw new NullPointerException(
	// "KnowledgeStoreStardog#KnowledgeStoreStardog NullPointerException [user = "
	// + user + "]");
	// }
	//
	// if (password == null) {
	// throw new NullPointerException(
	// "KnowledgeStoreStardog#KnowledgeStoreStardog NullPointerException [password = "
	// + password + "]");
	// }
	//
	// if (reasoningType == null)
	// reasoningType = ReasoningType.QL;
	//
	// try {
	// ConnectionConfiguration c = ConnectionConfiguration.to(database)
	// .url(url).credentials(user, password);
	//
	// this.rc = c.connect(reasoningType);
	// this.bc = rc.getBaseConnection();
	//
	// // This is not really needed at this stage
	// // init();
	// } catch (StardogException e) {
	// throw new RuntimeException(e);
	// }
	// }
	//
	// @Override
	// public void begin() {
	// try {
	// bc.begin();
	// } catch (StardogException e) {
	// close();
	// throw new RuntimeException(e);
	// }
	// }
	//
	// @Override
	// public void commit() {
	// try {
	// bc.commit();
	// } catch (StardogException e) {
	// close();
	// throw new RuntimeException(e);
	// }
	// }
	//
	// @Override
	// public void close() {
	// try {
	// bc.close();
	// rc.close();
	//
	// if (log.isLoggable(Level.INFO))
	// log.info("KnowledgeStoreStardog#close");
	// } catch (StardogException e) {
	// throw new RuntimeException(e);
	// }
	// }
	//
	// @Override
	// protected void store(Statement[] statements) {
	// try {
	// if (!bc.isOpen())
	// return;
	//
	// begin();
	//
	// for (Statement statement : statements) {
	// bc.add().statement(
	// JenaSesameUtils.statement(statement.asTriple()));
	// }
	//
	// commit();
	// } catch (StardogException e) {
	// throw new RuntimeException(e);
	// }
	// }
	//
	// // private void init() {
	// // initSensors();
	// // initFeatures();
	// // initProperties();
	// //
	// // loadSensors();
	// // loadFeatures();
	// // }
	//
	// // private void initSensors() {
	// // // It seems that this has to be done in two steps, first on the
	// // // reasoning connection to get all subclasses of ssn:Sensor and then
	// on
	// // // a base connection to get the individuals that are direct instances
	// of
	// // // each class. Extracting the individuals with a reasoning connection
	// // // returns all types individuals are instance of. As long as a Sensor
	// // // supports only one type, we want to acquire the direct type an
	// // // individual is an instance of.
	// // try {
	// // // Get all the classes that are subclasses of ssn:Sensor
	// // TupleQueryResult rs1 = select(
	// // "SELECT ?type WHERE { ?type rdfs:subClassOf <" + SSN.Sensor
	// // + "> FILTER (?type != owl:Nothing) }", true);
	// //
	// // while (rs1.hasNext()) {
	// // BindingSet bs1 = rs1.next();
	// // String type = get(bs1, "type");
	// //
	// // // Query for all individuals that are direct instances of the
	// // // given sensor type
	// // TupleQueryResult rs2 = select(
	// // "SELECT ?id WHERE { ?id rdf:type <" + type + "> }",
	// // false);
	// //
	// // while (rs2.hasNext()) {
	// // BindingSet bs2 = rs2.next();
	// // String id = get(bs2, "id");
	// //
	// // SensorBase sensor = new SensorBase(id, type);
	// //
	// // sensorsMap.put(sensor.getId(), sensor);
	// //
	// // if (log.isLoggable(Level.INFO))
	// // log.info("KnowledgeStoreStardog::initSensors ["
	// // + sensor + "]");
	// //
	// // }
	// //
	// // rs2.close();
	// // }
	// //
	// // rs1.close();
	// // } catch (QueryEvaluationException e) {
	// // throw new RuntimeException(e);
	// // }
	// // }
	// //
	// // private void loadSensors() {
	// // for (Sensor sensor : getSensors()) {
	// // for (Property property : getObservedProperties(sensor)) {
	// // sensor.addObservedProperty(property);
	// //
	// // for (Feature feature : getFeatures(property)) {
	// // sensor.addObservedFeature(feature);
	// // }
	// // }
	// // }
	// // }
	//
	// @Override
	// public Collection<fi.uef.envi.wavellite.observation.Observation>
	// getObservations(
	// String procedureId, String propertyId, String featureId,
	// DateTime timeFrom, DateTime timeTo) {
	// String qProcedureId, qPropertyId, qFeatureId;
	// List<fi.uef.envi.wavellite.observation.Observation> ret = new
	// ArrayList<fi.uef.envi.wavellite.observation.Observation>();
	// StringBuffer sb = new StringBuffer();
	//
	// sb.append("SELECT ?observationId ?observationType ?procedureType ?propertyType ?featureType ?timePeriod ?obsValue ?observationResultTimeId ?observationResultId ?observationResultValueId ?observationResultTimeType ?observationResultType ?observationResultValueType ");
	//
	// if (procedureId == null) {
	// qProcedureId = "?procedureId";
	// sb.append(qProcedureId + " ");
	// } else {
	// qProcedureId = "<" + procedureId + ">";
	// }
	// if (propertyId == null) {
	// qPropertyId = "?propertyId";
	// sb.append(qPropertyId + " ");
	// } else {
	// qPropertyId = "<" + propertyId + ">";
	// }
	// if (featureId == null) {
	// qFeatureId = "?featureId";
	// sb.append(qFeatureId + " ");
	// } else {
	// qFeatureId = "<" + featureId + ">";
	// }
	//
	// // Having the types will generate multiple results for the same
	// // observation; i.e. duplicated observations. TODO Need to figure out
	// // what to do here. It would be good to know the direct types. Stardog
	// // seems to support sdle:directType (http://pellet.owldl.com/ns/sdle#)
	// // but queries seem to a be a bit slow. Need to investigate more.
	// sb.append("WHERE { ");
	// // sb.append("?observationId rdf:type ?observationType . ");
	// sb.append("?observationId ssn:observedBy " + qProcedureId + ". ");
	// sb.append("?observationId ssn:observedProperty " + qPropertyId + ". ");
	// sb.append("?observationId ssn:featureOfInterest " + qFeatureId + ". ");
	// // sb.append(qProcedureId + " rdf:type ?procedureType . ");
	// // sb.append(qPropertyId + " rdf:type ?propertyType . ");
	// // sb.append(qFeatureId + " rdf:type ?featureType . ");
	// sb.append("?observationId ssn:observationResultTime ?observationResultTimeId . ");
	// //
	// sb.append("?observationResultTimeId rdf:type ?observationResultTimeType . ");
	// sb.append("?observationResultTimeId dul:hasRegionDataValue ?timePeriod . ");
	// sb.append("?observationId ssn:observationResult ?observationResultId . ");
	// // sb.append("?observationResultId rdf:type ?observationResultType . ");
	// sb.append("?observationResultId ssn:hasValue ?observationResultValueId . ");
	// //
	// sb.append("?observationResultValueId rdf:type ?observationResultValueType . ");
	// sb.append("?observationResultValueId dul:hasRegionDataValue ?obsValue . ");
	//
	// if (timeFrom != null)
	// sb.append("FILTER (?timePeriod > '" + dtf.print(timeFrom)
	// + "'^^xsd:dateTime) ");
	//
	// if (timeTo != null)
	// sb.append("FILTER (?timePeriod <= '" + dtf.print(timeTo)
	// + "'^^xsd:dateTime) ");
	//
	// sb.append("} ORDER BY ASC(?timePeriod)");
	//
	// TupleQueryResult rs1 = select(sb.toString(), false);
	//
	// try {
	// while (rs1.hasNext()) {
	// BindingSet bs1 = rs1.next();
	//
	// String rsProcedureId, rsPropertyId, rsFeatureId;
	//
	// if (procedureId == null)
	// rsProcedureId = get(bs1, "procedureId");
	// else
	// rsProcedureId = procedureId;
	//
	// if (propertyId == null)
	// rsPropertyId = get(bs1, "propertyId");
	// else
	// rsPropertyId = propertyId;
	//
	// if (featureId == null)
	// rsFeatureId = get(bs1, "featureId");
	// else
	// rsFeatureId = featureId;
	//
	// String rsObservationId = get(bs1, "observationId");
	// // String rsObservationType = get(bs1, "observationType");
	// // String rsProcedureType = get(bs1, "procedureType");
	// // String rsPropertyType = get(bs1, "propertyType");
	// // String rsFeatureType = get(bs1, "featureType");
	// String rsObservationResultTimeId = get(bs1,
	// "observationResultTimeId");
	// String rsObservationResultId = get(bs1, "observationResultId");
	// String rsObservationResultValueId = get(bs1,
	// "observationResultValueId");
	// // String rsObservationResultTimeType = get(bs1,
	// // "observationResultTimeType");
	// // String rsObservationResultType = get(bs1,
	// // "observationResultType");
	// // String rsObservationResultValueType = get(bs1,
	// // "observationResultValueType");
	//
	// Procedure procedure = new SensorBase(rsProcedureId);
	// Property property = new PropertyBase(rsPropertyId);
	// Feature feature = new FeatureBase(rsFeatureId);
	// DateTime timePeriod = dtf.parseDateTime(get(bs1, "timePeriod"));
	// Double obsValue = Double.parseDouble(get(bs1, "obsValue"));
	//
	// fi.uef.envi.wavellite.observation.Observation observation = new
	// fi.uef.envi.wavellite.observation.impl.ObservationBase(
	// rsObservationId);
	// observation.setProcedure(procedure);
	// observation.setObservedProperty(property);
	// observation.setFeatureOfInterest(feature);
	// observation.setObservationResultTime(new TimePointBase(
	// rsObservationResultTimeId, timePeriod));
	// observation.setObservationResult(new SensorOutputBase(
	// rsObservationResultId, new ObservationValueBase(
	// rsObservationResultValueId, obsValue)));
	//
	// ret.add(observation);
	// }
	// } catch (QueryEvaluationException e) {
	// throw new RuntimeException(e);
	// }
	//
	// return ret;
	// }
	//
	// @Override
	// public Collection<fi.uef.envi.wavellite.derivation.Observation>
	// getDerivations(
	// String datasetId, String domainComponentPropertyId,
	// DateTime timeFrom, DateTime timeTo) {
	// if (datasetId == null)
	// throw new RuntimeException(
	// "KnowledgeStoreStardog#getDerivations [datasetId = "
	// + datasetId + "]");
	// if (domainComponentPropertyId == null)
	// throw new RuntimeException(
	// "KnowledgeStoreStardog#getDerivations [domainComponentPropertyId = "
	// + domainComponentPropertyId + "]");
	//
	// List<fi.uef.envi.wavellite.derivation.Observation> ret = new
	// ArrayList<fi.uef.envi.wavellite.derivation.Observation>();
	//
	// StringBuffer sb = new StringBuffer();
	//
	// sb.append("SELECT ?dataStructureDefinitionId ?componentSpecificationId ?componentPropertyId ");
	// sb.append("WHERE { ");
	// sb.append("<" + datasetId + "> rdf:type qb:DataSet . ");
	// sb.append("<" + datasetId
	// + "> qb:structure ?dataStructureDefinitionId . ");
	// sb.append("?dataStructureDefinitionId qb:component ?componentSpecificationId . ");
	// sb.append("?componentSpecificationId qb:componentProperty ?componentPropertyId . ");
	// sb.append("OPTIONAL { ?componentSpecificationId qb:order ?order } ");
	// sb.append("} ");
	// sb.append("ORDER BY ASC(?order)");
	//
	// String dataStructureDefinitionId = null;
	// TupleQueryResult rs = select(sb.toString(), true);
	// List<String> componentPropertyIds = new ArrayList<String>();
	// List<ComponentSpecification> componentSpecifications = new
	// ArrayList<ComponentSpecification>();
	//
	// try {
	// while (rs.hasNext()) {
	// BindingSet bs = rs.next();
	// dataStructureDefinitionId = get(bs, "dataStructureDefinitionId");
	// String componentSpecificationId = get(bs,
	// "componentSpecificationId");
	// String componentPropertyId = get(bs, "componentPropertyId");
	// ComponentSpecification componentSpecification = new
	// ComponentSpecificationBase(
	// componentSpecificationId);
	// componentSpecification.set(new ComponentPropertyBase(
	// componentPropertyId));
	// componentSpecifications.add(componentSpecification);
	// componentPropertyIds.add(componentPropertyId);
	// }
	// } catch (QueryEvaluationException e) {
	// throw new RuntimeException(e);
	// }
	//
	// if (dataStructureDefinitionId == null)
	// throw new NullPointerException(
	// "KnowledgeStoreStardog#getDerivations [dataStructureDefinitionId = "
	// + dataStructureDefinitionId + "]");
	//
	// DataStructureDefinition structure = new DataStructureDefinitionBase(
	// dataStructureDefinitionId);
	//
	// for (ComponentSpecification componentSpecification :
	// componentSpecifications) {
	// structure.addComponentSpecification(componentSpecification);
	// }
	//
	// sb = new StringBuffer();
	//
	// sb.append("SELECT ?observationId ");
	//
	// for (int i = 0; i < componentPropertyIds.size(); i++) {
	// sb.append("?componentPropertyValue" + i + " ");
	// }
	//
	// sb.append("WHERE {");
	// sb.append("?observationId qb:dataSet <" + datasetId + "> . ");
	//
	// String domainComponentPropertyValueVar = null;
	//
	// for (int i = 0; i < componentPropertyIds.size(); i++) {
	// String componentPropertyId = componentPropertyIds.get(i);
	// String componentPropertyValueVar = "?componentPropertyValue" + i;
	//
	// if (componentPropertyId.equals(domainComponentPropertyId))
	// domainComponentPropertyValueVar = componentPropertyValueVar;
	//
	// sb.append("?observationId <" + componentPropertyId + "> "
	// + componentPropertyValueVar + " . ");
	// }
	//
	// if (domainComponentPropertyValueVar == null)
	// throw new NullPointerException(
	// "KnowledgeStoreStardog#getDerivations [domainComponentPropertyValueVar = "
	// + domainComponentPropertyValueVar + "]");
	//
	// if (timeFrom != null)
	// sb.append("FILTER (" + domainComponentPropertyValueVar + " > '"
	// + dtf.print(timeFrom) + "'^^xsd:dateTime) ");
	//
	// if (timeTo != null)
	// sb.append("FILTER (" + domainComponentPropertyValueVar + " <= '"
	// + dtf.print(timeTo) + "'^^xsd:dateTime) ");
	//
	// sb.append("} ORDER BY ASC(" + domainComponentPropertyValueVar + ")");
	//
	// TupleQueryResult rs1 = select(sb.toString(), false);
	//
	// try {
	// while (rs1.hasNext()) {
	// BindingSet bs1 = rs1.next();
	//
	// String rsObservationId = get(bs1, "observationId");
	//
	// fi.uef.envi.wavellite.derivation.Observation observation = new
	// fi.uef.envi.wavellite.derivation.impl.ObservationBase(
	// rsObservationId);
	// Dataset dataset = new DatasetBase(datasetId);
	// dataset.setStructure(structure);
	// observation.setDataset(dataset);
	// ret.add(observation);
	//
	// for (int i = 0; i < componentPropertyIds.size(); i++) {
	// String rsComponentPropertyId = componentPropertyIds.get(i);
	//
	// Object rsComponentPropertyValue;
	//
	// // Assumes that domain component property is a date time
	// if (rsComponentPropertyId.equals(domainComponentPropertyId))
	// rsComponentPropertyValue = dtf.parseDateTime(get(bs1,
	// "componentPropertyValue" + i));
	// else {
	// try {
	// rsComponentPropertyValue = Double.valueOf(get(bs1,
	// "componentPropertyValue" + i));
	// } catch (NumberFormatException e) {
	// rsComponentPropertyValue = get(bs1,
	// "componentPropertyValue" + i);
	// }
	// }
	//
	// observation.addComponentProperty(new ComponentPropertyBase(
	// rsComponentPropertyId),
	// new ComponentPropertyValueBase(
	// rsComponentPropertyValue));
	// }
	// }
	// } catch (QueryEvaluationException e) {
	// throw new RuntimeException(e);
	// }
	//
	// return ret;
	// }
	//
	// // private void initFeatures() {
	// // try {
	// // TupleQueryResult rs1 = select(
	// // "SELECT ?type WHERE { ?type rdfs:subClassOf <"
	// // + SSN.FeatureOfInterest
	// // + "> FILTER (?type != owl:Nothing) }", true);
	// //
	// // while (rs1.hasNext()) {
	// // BindingSet bs1 = rs1.next();
	// // String type = get(bs1, "type");
	// //
	// // TupleQueryResult rs2 = select(
	// // "SELECT ?id WHERE { ?id rdf:type <" + type + "> }",
	// // false);
	// //
	// // while (rs2.hasNext()) {
	// // BindingSet bs2 = rs2.next();
	// // String id = get(bs2, "id");
	// //
	// // Feature feature = new FeatureBase(id, type);
	// //
	// // featuresMap.put(feature.getId(), feature);
	// //
	// // if (log.isLoggable(Level.INFO))
	// // log.info("KnowledgeStoreStardog::initFeatures ["
	// // + feature + "]");
	// //
	// // }
	// //
	// // rs2.close();
	// // }
	// //
	// // rs1.close();
	// // } catch (QueryEvaluationException e) {
	// // throw new RuntimeException(e);
	// // }
	// // }
	// //
	// // private void loadFeatures() {
	// // for (Feature feature : getFeatures()) {
	// // for (Property property : getProperties(feature)) {
	// // feature.addProperty(property);
	// // }
	// // }
	// // }
	// //
	// // private void initProperties() {
	// // try {
	// // TupleQueryResult rs1 = select(
	// // "SELECT ?type WHERE { ?type rdfs:subClassOf <"
	// // + SSN.Property
	// // + "> FILTER (?type != owl:Nothing) }", true);
	// //
	// // while (rs1.hasNext()) {
	// // BindingSet bs1 = rs1.next();
	// // String type = get(bs1, "type");
	// //
	// // TupleQueryResult rs2 = select(
	// // "SELECT ?id WHERE { ?id rdf:type <" + type + "> }",
	// // false);
	// //
	// // while (rs2.hasNext()) {
	// // BindingSet bs2 = rs2.next();
	// // String id = get(bs2, "id");
	// //
	// // Property property = new PropertyBase(id, type);
	// //
	// // propertiesMap.put(property.getId(), property);
	// //
	// // if (log.isLoggable(Level.INFO))
	// // log.info("KnowledgeStoreStardog::initProperties ["
	// // + property + "]");
	// //
	// // }
	// //
	// // rs2.close();
	// // }
	// //
	// // rs1.close();
	// // } catch (QueryEvaluationException e) {
	// // throw new RuntimeException(e);
	// // }
	// // }
	// //
	// // // Properties have no associations to other SSN entities
	// // private Collection<Property> getObservedProperties(Sensor sensor) {
	// // Set<Property> ret = new HashSet<Property>();
	// //
	// // String sensorId = sensor.getId();
	// //
	// // TupleQueryResult rs = select(
	// // "SELECT ?id ?type WHERE { <"
	// // + sensorId
	// // + "> <"
	// // + SSN.observes
	// // +
	// "> ?id . ?id rdf:type ?type FILTER (?type != owl:NamedIndividual) }",
	// // false);
	// //
	// // try {
	// // while (rs.hasNext()) {
	// // BindingSet bs = rs.next();
	// // String id = get(bs, "id");
	// // String type = get(bs, "type");
	// //
	// // ret.add(new PropertyBase(id, type));
	// // }
	// //
	// // rs.close();
	// // } catch (QueryEvaluationException e) {
	// // throw new RuntimeException(e);
	// // }
	// //
	// // return ret;
	// // }
	// //
	// // // Features have associations to SSN properties
	// // private Collection<Feature> getFeatures(Property property) {
	// // Set<Feature> ret = new HashSet<Feature>();
	// //
	// // String propertyId = property.getId();
	// //
	// // TupleQueryResult rs = select(
	// // "SELECT ?id ?type WHERE { <"
	// // + propertyId
	// // + "> <"
	// // + SSN.isPropertyOf
	// // + "> ?id . ?id rdf:type ?type FILTER (?type != owl:NamedIndividual)}",
	// // false);
	// //
	// // try {
	// // while (rs.hasNext()) {
	// // BindingSet bs = rs.next();
	// // String id = get(bs, "id");
	// // String type = get(bs, "type");
	// //
	// // Feature feature = new FeatureBase(id, type);
	// // feature.addProperties(getProperties(feature));
	// //
	// // ret.add(feature);
	// // }
	// //
	// // rs.close();
	// // } catch (QueryEvaluationException e) {
	// // throw new RuntimeException(e);
	// // }
	// //
	// // return ret;
	// // }
	//
	// // // Properties have no association to other SSN entities
	// // private Collection<Property> getProperties(Feature feature) {
	// // Set<Property> ret = new HashSet<Property>();
	// //
	// // String featureId = feature.getId();
	// //
	// // TupleQueryResult rs = select(
	// // "SELECT ?id ?type WHERE { <"
	// // + featureId
	// // + "> <"
	// // + SSN.hasProperty
	// // + "> ?id . ?id rdf:type ?type FILTER (?type != owl:NamedIndividual)}",
	// // false);
	// //
	// // try {
	// // while (rs.hasNext()) {
	// // BindingSet bs = rs.next();
	// // String id = get(bs, "id");
	// // String type = get(bs, "type");
	// //
	// // ret.add(new PropertyBase(id, type));
	// // }
	// //
	// // rs.close();
	// // } catch (QueryEvaluationException e) {
	// // throw new RuntimeException(e);
	// // }
	// //
	// // return ret;
	// // }
	//
	// private String get(BindingSet bs, String key) {
	// return bs.getBinding(key).getValue().stringValue();
	// }
	//
	// private TupleQueryResult select(String query, boolean inference) {
	// return execute(query(query, inference));
	// }
	//
	// private Query query(String query, boolean inference) {
	// Connection c = bc;
	//
	// if (inference)
	// c = rc;
	//
	// try {
	// return
	// c.query("PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> "
	// + "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> "
	// + "PREFIX ssn: <http://purl.oclc.org/NET/ssnx/ssn#> "
	// + "PREFIX owl: <http://www.w3.org/2002/07/owl#> "
	// + "PREFIX dul: <http://www.loa-cnr.it/ontologies/DUL.owl#>"
	// + query);
	// } catch (StardogException e) {
	// throw new RuntimeException(e);
	// }
	// }
	//
	// private TupleQueryResult execute(Query query) {
	// try {
	// return query.executeSelect();
	// } catch (StardogException e) {
	// throw new RuntimeException(e);
	// }
	// }
	//
	// // TODO Remove
	// // private URI getURI(Entity e) {
	// // String id = e.getId();
	// //
	// // URI uri = getURI(id);
	// //
	// // e.setId(uri.stringValue());
	// //
	// // return uri;
	// // }
	//
	// // TODO Remove
	// // private URI getURI(String uri) {
	// // if (!urlValidator.isValid(uri))
	// // uri = ns + uri;
	// //
	// // return Values.uri(uri);
	// // }
	//
	// // TODO Remove
	// // private class ElementaryInfonObjectVisitor implements
	// // RelevantObjectVisitor {
	// //
	// // private URI infonI;
	// // private int anchorIndex = 0;
	// //
	// // public void set(URI infonI) {
	// // this.infonI = infonI;
	// // this.anchorIndex = 0;
	// // }
	// //
	// // @Override
	// // public void visit(Individual object) {
	// // // If the individual involved in the situation is one of the modeled
	// // // sensors, skip it
	// // if (sensorsMap.containsKey(object.getId()))
	// // return;
	// //
	// // try {
	// // URI individualI = getURI(object);
	// // // anchorN(infonI, individualI);
	// // adder.statement(infonI, getURI(STO.ns + "#anchor"
	// // + anchorIndex++), individualI);
	// // // Individual(individualI)
	// // adder.statement(individualI, RDF.TYPE, getURI(object.getType()));
	// // // TODO: Add attributes
	// // } catch (StardogException e) {
	// // throw new RuntimeException(e);
	// // }
	// // }
	// //
	// // @Override
	// // public void visit(SpatialLocation object) {
	// // throw new UnsupportedOperationException("");
	// //
	// // }
	// //
	// // @Override
	// // public void visit(TemporalLocation object) {
	// // try {
	// // URI timeI = getURI(object);
	// // // anchorN(infonI, timeI)
	// // adder.statement(infonI, getURI(STO.ns + "#anchor"
	// // + anchorIndex++), timeI);
	// // // Time(timeI)
	// // adder.statement(timeI, RDF.TYPE, getURI(object.getType()));
	// // // hasAttributeValue(timeI, attributeValueI)
	// // URI attributeValueI = getURI(ns + UUID.randomUUID().toString());
	// // adder.statement(timeI, getURI(STO.hasAttributeValue),
	// // attributeValueI);
	// // // Value(attributeValueI)
	// // adder.statement(attributeValueI, RDF.TYPE, getURI(STO.Value));
	// // // attributeValue(attributeValueI, value)
	// // adder.statement(attributeValueI, getURI(STO.attributeValue),
	// // Values.literal(object.getTime().toString()));
	// // } catch (StardogException e) {
	// // throw new RuntimeException(e);
	// // }
	// //
	// // }
	// //
	// // }

}
