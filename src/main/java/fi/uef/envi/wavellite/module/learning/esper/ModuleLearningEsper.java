/*
 * Copyright (C) 2013 see CREDITS.txt
 * All rights reserved.
 */

package fi.uef.envi.wavellite.module.learning.esper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.joda.time.DateTime;

import com.espertech.esper.client.Configuration;
import com.espertech.esper.client.ConfigurationOperations;
import com.espertech.esper.client.EPAdministrator;
import com.espertech.esper.client.EPRuntime;
import com.espertech.esper.client.EPServiceProvider;
import com.espertech.esper.client.EPServiceProviderManager;
import com.espertech.esper.client.EPStatement;
import com.espertech.esper.client.EventBean;
import com.espertech.esper.client.UpdateListener;
import com.espertech.esper.client.time.CurrentTimeEvent;
import com.espertech.esper.event.map.MapEventType;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import fi.uef.envi.wavellite.entity.core.TemporalLocation;
import fi.uef.envi.wavellite.entity.core.base.TemporalLocationDateTime;
import fi.uef.envi.wavellite.entity.derivation.ComponentProperty;
import fi.uef.envi.wavellite.entity.derivation.ComponentPropertyValue;
import fi.uef.envi.wavellite.entity.derivation.Dataset;
import fi.uef.envi.wavellite.entity.derivation.DatasetObservation;
import fi.uef.envi.wavellite.entity.derivation.base.ComponentPropertyBase;
import fi.uef.envi.wavellite.entity.derivation.base.ComponentPropertyValueTemporalLocation;
import fi.uef.envi.wavellite.entity.situation.Situation;
import fi.uef.envi.wavellite.entity.situation.base.SituationBase;
import fi.uef.envi.wavellite.module.learning.ModuleLearning;
import fi.uef.envi.wavellite.module.learning.ModuleLearningListener;
import fi.uef.envi.wavellite.vocabulary.SDMX;

/**
 * <p>
 * Title: ModuleLearningEsper
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

public class ModuleLearningEsper implements ModuleLearning {

	private EPServiceProvider serviceProvider;
	private EPAdministrator administrator;
	private EPRuntime runtime;
	private Configuration configuration;
	private Set<EPStatement> statements;
	private Set<ModuleLearningListener> listeners;
	private boolean internalTimerEnabled = false;
	private ComponentProperty timePeriodComponentProperty;

	private static final Logger log = Logger
			.getLogger(ModuleLearningEsper.class.getName());

	public ModuleLearningEsper() {
		configuration = new Configuration();
		
		enableInternalTimer(internalTimerEnabled);
		
		serviceProvider = EPServiceProviderManager.getDefaultProvider(configuration);
		administrator = serviceProvider.getEPAdministrator();
		runtime = serviceProvider.getEPRuntime();
		statements = new HashSet<EPStatement>();
		listeners = new HashSet<ModuleLearningListener>();

		initEventTypes();
		setTimePeriodComponentProperty(new ComponentPropertyBase(
				SDMX.Dimension.timePeriod));
	}

	public void addExpression(String expression) {
		EPStatement statement = administrator.createEPL(expression);
		statement.addListener(new ThisUpdateListener());
		statements.add(statement);
	}

	@Override
	public void addDatasetObservation(DatasetObservation observation) {
		setTime(observation);

		runtime.sendEvent(toEvent(observation), "DatasetObservationType");
	}

	@Override
	public void addListener(ModuleLearningListener listener) {
		listeners.add(listener);
	}

	public void enableInternalTimer(boolean internalTimerEnabled) {
		configuration.getEngineDefaults().getThreading()
				.setInternalTimerEnabled(internalTimerEnabled);

		this.internalTimerEnabled = internalTimerEnabled;
	}

	public void setTimePeriodComponentProperty(ComponentProperty property) {
		this.timePeriodComponentProperty = property;
	}

	private void setTime(DatasetObservation observation) {
		if (internalTimerEnabled)
			return;

		ComponentPropertyValue value = observation
				.getComponentPropertyValue(timePeriodComponentProperty);

		if (value == null)
			throw new NullPointerException(
					"No time period component property [timePeriodComponentProperty = "
							+ timePeriodComponentProperty + "; observation = "
							+ observation + "]");

		if (!(value instanceof ComponentPropertyValueTemporalLocation))
			throw new RuntimeException("Expected temporal location [value = "
					+ value + "]");

		TemporalLocation location = ((ComponentPropertyValueTemporalLocation) value)
				.getValue();

		if (location == null)
			throw new NullPointerException("No value [value = " + value + "]");

		if (!(location instanceof TemporalLocationDateTime))
			throw new RuntimeException("Expected date time [location = "
					+ location + "]");

		DateTime dt = ((TemporalLocationDateTime) location).getValue();
		
		Long millis = dt.getMillis();
		
		runtime.sendEvent(new CurrentTimeEvent(millis));
	}

	private static Map<String, Object> toEvent(DatasetObservation observation) {
		Map<String, Object> m1 = new HashMap<String, Object>();
		m1.put("id", observation.getId());
		m1.put("type", observation.getType());

		Map<String, Object> m2 = new HashMap<String, Object>();
		m1.put("dataset", m2);
		Dataset dataset = observation.getDataset();

		if (dataset == null) {
			if (log.isLoggable(Level.INFO))
				log.info("No dataset [observation = " + observation + "]");
		} else {
			m2.put("id", dataset.getId());
			m2.put("type", dataset.getType());
		}

		List<Map<String, Object>> m3 = new ArrayList<Map<String, Object>>();

		for (ComponentProperty property : observation.getComponentProperties()) {
			ComponentPropertyValue value = observation
					.getComponentPropertyValue(property);
			Map<String, Object> m4 = new HashMap<String, Object>();
			Map<String, Object> m5 = new HashMap<String, Object>();
			Map<String, Object> m6 = new HashMap<String, Object>();
			m3.add(m4);
			m4.put("componentProperty", m5);
			m4.put("componentPropertyValue", m6);
			m5.put("id", property.getId());
			m5.put("type", property.getType());
			m6.put("value", value.getValue());
		}

		m1.put("components", m3.toArray());

		return m1;
	}

	private void initEventTypes() {
		ConfigurationOperations configuration = administrator
				.getConfiguration();

		Map<String, Object> componentProperty = new HashMap<String, Object>();
		componentProperty.put("id", String.class);
		componentProperty.put("type", String.class);
		configuration.addEventType("ComponentPropertyType", componentProperty);

		Map<String, Object> componentPropertyValue = new HashMap<String, Object>();
		componentPropertyValue.put("value", Double.class);
		configuration.addEventType("ComponentPropertyValueType",
				componentPropertyValue);

		Map<String, Object> components = new HashMap<String, Object>();
		components.put("componentProperty", "ComponentPropertyType");
		components.put("componentPropertyValue", "ComponentPropertyValueType");
		configuration.addEventType("ComponentType", components);

		Map<String, Object> dataset = new HashMap<String, Object>();
		dataset.put("id", String.class);
		dataset.put("type", String.class);
		configuration.addEventType("DatasetType", components);

		Map<String, Object> datasetObservation = new HashMap<String, Object>();
		datasetObservation.put("id", String.class);
		datasetObservation.put("type", String.class);
		datasetObservation.put("dataset", "DatasetType");
		datasetObservation.put("components", "ComponentType[]");
		configuration
				.addEventType("DatasetObservationType", datasetObservation);
	}

	private class ThisUpdateListener implements UpdateListener {

		public void update(EventBean[] newEvents, EventBean[] oldEvents) {
			System.out.println(newEvents[0].getUnderlying());
			// Situation s = new SituationBase();
			//
			// for (ModuleLearningListener listener : listeners) {
			// listener.onSituation(s);
			// }
		}

	}

}
