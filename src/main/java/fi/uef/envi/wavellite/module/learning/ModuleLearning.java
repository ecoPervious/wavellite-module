/*
 * Copyright (C) 2013 see CREDITS.txt
 * All rights reserved.
 */

package fi.uef.envi.wavellite.module.learning;

import java.util.Collection;
import java.util.Queue;
import java.util.Set;

import fi.uef.envi.wavellite.entity.derivation.DatasetObservation;
import fi.uef.envi.wavellite.entity.situation.Situation;
import fi.uef.envi.wavellite.module.core.Module;

/**
 * <p>
 * Title: ModuleLearning
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

public interface ModuleLearning extends Module {

	public void set(Queue<Situation> situations);

	public void add(DatasetObservation observation);
	
	public void addAll(Collection<DatasetObservation> observations);
	
}
