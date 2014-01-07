/*
 * Copyright (C) 2014 see CREDITS.txt
 * All rights reserved.
 */

package fi.uef.envi.wavellite.module.base;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import fi.uef.envi.wavellite.module.ModuleResult;

/**
 * <p>
 * Title: AbstractModuleResult
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

public abstract class AbstractModuleResult<U, V> extends AbstractModule<U> implements
		ModuleResult<U, V> {

	protected Queue<V> queue;
	
	public AbstractModuleResult() {
		queue = new ConcurrentLinkedQueue<V>();
	}
	
	public Queue<V> result() {
		return queue;
	}
	
}
