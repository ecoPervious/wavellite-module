/*
 * Copyright (C) 2013 see CREDITS.txt
 * All rights reserved.
 */

package fi.uef.envi.wavellite.module.learning.base;

import java.util.Collection;
import java.util.Queue;

import fi.uef.envi.wavellite.entity.derivation.DatasetObservation;
import fi.uef.envi.wavellite.entity.situation.Situation;
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

public abstract class AbstractModuleLearning implements ModuleLearning {

	protected Queue<Situation> situations;
	
	@Override
	public void set(Queue<Situation> situations) {
		this.situations = situations;
	}

	@Override
	public void addAll(Collection<DatasetObservation> observations) {
		for (DatasetObservation observation : observations)
			add(observation);
	}

}
