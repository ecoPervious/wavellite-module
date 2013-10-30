/*
 * Copyright (C) 2013 see CREDITS.txt
 * All rights reserved.
 */

package fi.uef.envi.wavellite.module.store;

import java.util.Set;

import org.openrdf.model.Statement;

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

	public void store(Set<Statement> statements);
	
	public void close();
	
}
