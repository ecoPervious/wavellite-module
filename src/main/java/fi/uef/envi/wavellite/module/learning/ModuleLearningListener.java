/*
 * Copyright (C) 2013 see CREDITS.txt
 * All rights reserved.
 */

package fi.uef.envi.wavellite.module.learning;

import fi.uef.envi.wavellite.entity.situation.Situation;

/**
 * <p>
 * Title: ModuleLearningListener
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

public interface ModuleLearningListener {

	public void onSituation(Situation situation);
	
}
