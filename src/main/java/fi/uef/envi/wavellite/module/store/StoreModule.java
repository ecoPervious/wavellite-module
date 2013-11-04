/*
 * Copyright (C) 2013 see CREDITS.txt
 * All rights reserved.
 */

package fi.uef.envi.wavellite.module.store;

import java.util.Set;

import org.openrdf.model.Statement;

import fi.uef.envi.wavellite.entity.derivation.DatasetObservation;
import fi.uef.envi.wavellite.entity.measurement.MeasurementResult;
import fi.uef.envi.wavellite.entity.observation.SensorObservation;
import fi.uef.envi.wavellite.entity.situation.Situation;

/**
 * <p>
 * Title: StoreModule
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

public interface StoreModule {

	public void store(MeasurementResult result);
	
	public void store(SensorObservation observation);
	
	public void store(DatasetObservation observation);
	
	public void store(Situation situation);
	
	public void store(Set<Statement> statements);
	
	public void close();
	
}
