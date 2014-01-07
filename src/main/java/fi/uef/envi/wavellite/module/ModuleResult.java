/*
 * Copyright (C) 2014 see CREDITS.txt
 * All rights reserved.
 */

package fi.uef.envi.wavellite.module;

import java.util.Queue;

/**
 * <p>
 * Title: ModuleResult
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

public interface ModuleResult<U,V> extends Module<U> {

	public Queue<V> result();
	
}
