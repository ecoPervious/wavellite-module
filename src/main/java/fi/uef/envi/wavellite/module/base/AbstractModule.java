/*
 * Copyright (C) 2014 see CREDITS.txt
 * All rights reserved.
 */

package fi.uef.envi.wavellite.module.base;

import java.util.Collections;

import fi.uef.envi.wavellite.module.Module;

/**
 * <p>
 * Title: AbstractModule
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

public abstract class AbstractModule<T> implements Module<T> {
	
	@Override
	public void consider(T entity) {
		considerAll(Collections.singleton(entity));
	}
	
}
