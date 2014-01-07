/*
 * Copyright (C) 2014 see CREDITS.txt
 * All rights reserved.
 */

package fi.uef.envi.wavellite.module.processing.base;

import java.util.Collection;

import fi.uef.envi.wavellite.entity.measurement.MeasurementResult;
import fi.uef.envi.wavellite.entity.observation.SensorObservation;
import fi.uef.envi.wavellite.operator.translation.MeasurementResultTranslator;
import fi.uef.envi.wavellite.operator.translation.base.MeasurementResultTranslatorBase;

/**
 * <p>
 * Title: ModuleProcessingMeasurementResult
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Project: Wavellite Module
 * </p>
 * <p>
 * Copyright: Copyright (C) 2014
 * </p>
 * 
 * @author Markus Stocker
 */

public class ModuleProcessingMeasurementResult extends
		AbstractModuleProcessing<MeasurementResult, SensorObservation> {

	private MeasurementResultTranslator operator = new MeasurementResultTranslatorBase();

	@Override
	public void considerAll(Collection<MeasurementResult> entities) {
		for (MeasurementResult entity : entities)
			queue.add(operator.translate(entity));
	}

}
