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

import com.espertech.esper.client.EPAdministrator;
import com.espertech.esper.client.EPRuntime;
import com.espertech.esper.client.EPServiceProvider;
import com.espertech.esper.client.EPServiceProviderManager;
import com.espertech.esper.client.EPStatement;
import com.espertech.esper.client.EventBean;
import com.espertech.esper.client.UpdateListener;
import com.espertech.esper.event.map.MapEventType;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import fi.uef.envi.wavellite.entity.derivation.ComponentProperty;
import fi.uef.envi.wavellite.entity.derivation.ComponentPropertyValue;
import fi.uef.envi.wavellite.entity.derivation.DatasetObservation;
import fi.uef.envi.wavellite.entity.situation.Situation;
import fi.uef.envi.wavellite.entity.situation.base.SituationBase;
import fi.uef.envi.wavellite.module.learning.ModuleLearning;
import fi.uef.envi.wavellite.module.learning.ModuleLearningListener;

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
	private Set<EPStatement> statements;
	private Set<ModuleLearningListener> listeners;
	private ObjectMapper mapper = new ObjectMapper();

	public ModuleLearningEsper() {
		serviceProvider = EPServiceProviderManager.getDefaultProvider();
		administrator = serviceProvider.getEPAdministrator();
		runtime = serviceProvider.getEPRuntime();
		statements = new HashSet<EPStatement>();
		listeners = new HashSet<ModuleLearningListener>();

		Map<String, Object> componentProperty = new HashMap<String, Object>();
		componentProperty.put("id", String.class);
		componentProperty.put("type", String.class);
		administrator.getConfiguration().addEventType("ComponentProperty",
				componentProperty);

		Map<String, Object> componentPropertyValue = new HashMap<String, Object>();
		componentPropertyValue.put("value", Object.class);
		administrator.getConfiguration().addEventType("ComponentPropertyValue",
				componentPropertyValue);

		Map<String, Object> components = new HashMap<String, Object>();
		components.put("componentProperty", "ComponentProperty");
		components.put("componentPropertyValue", "ComponentPropertyValue");
		administrator.getConfiguration().addEventType("Components", components);

		Map<String, Object> datasetObservation = new HashMap<String, Object>();
		datasetObservation.put("id", String.class);
		datasetObservation.put("type", String.class);
		datasetObservation.put("components", "Components[]");

		administrator.getConfiguration().addEventType("DatasetObservation",
				datasetObservation);
	}

	public void addExpression(String expression) {
		EPStatement statement = administrator.createEPL(expression);
		statement.addListener(new ThisUpdateListener());
		statements.add(statement);
	}

	@Override
	public void addDatasetObservation(DatasetObservation observation) {
		Map<String, Object> m1 = new HashMap<String, Object>();
		m1.put("id", observation.getId());
		m1.put("type", observation.getType());
		List<Map<String, Object>> m2 = new ArrayList<Map<String, Object>>();

		for (ComponentProperty property : observation.getComponentProperties()) {
			ComponentPropertyValue value = observation
					.getComponentPropertyValue(property);
			Map<String, Object> m3 = new HashMap<String, Object>();
			Map<String, Object> m4 = new HashMap<String, Object>();
			Map<String, Object> m5 = new HashMap<String, Object>();
			m3.put("componentProperty", m4);
			m3.put("componentPropertyValue", m5);
			m2.add(m3);
			m4.put("id", property.getId());
			m4.put("type", property.getType());
			m5.put("value", value.getValue());
		}

		m1.put("components", m2.toArray(new Map[] {}));

		runtime.sendEvent(m1, "DatasetObservation");
	}

	@Override
	public void addListener(ModuleLearningListener listener) {
		listeners.add(listener);
	}

	private class ThisUpdateListener implements UpdateListener {

		public void update(EventBean[] newEvents, EventBean[] oldEvents) {
			System.out.println(newEvents[0].getUnderlying());
//			Situation s = new SituationBase();
//
//			for (ModuleLearningListener listener : listeners) {
//				listener.onSituation(s);
//			}
		}

	}

}
