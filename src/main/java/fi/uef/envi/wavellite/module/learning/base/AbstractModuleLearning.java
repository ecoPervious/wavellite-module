/*
 * Copyright (C) 2013 see CREDITS.txt
 * All rights reserved.
 */

package fi.uef.envi.wavellite.module.learning.base;

import java.util.Collections;

import fi.uef.envi.wavellite.entity.derivation.DatasetObservation;
import fi.uef.envi.wavellite.entity.situation.Situation;
import fi.uef.envi.wavellite.module.base.AbstractModuleResult;
import fi.uef.envi.wavellite.module.learning.ModuleLearning;

/**
 * <p>
 * Title: AbstractModuleLearning
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

public abstract class AbstractModuleLearning extends
		AbstractModuleResult<DatasetObservation, Situation> implements ModuleLearning {

	@Override
	public void consider(DatasetObservation observation) {
		considerAll(Collections.singleton(observation));
	}

}
