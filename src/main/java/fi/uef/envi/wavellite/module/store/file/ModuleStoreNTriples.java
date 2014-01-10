/*
 * Copyright (C) 2013 see CREDITS.txt
 * All rights reserved.
 */

package fi.uef.envi.wavellite.module.store.file;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Set;

import org.openrdf.model.Statement;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.RDFWriter;
import org.openrdf.rio.Rio;

import fi.uef.envi.wavellite.module.store.base.AbstractModuleStoreRdf;

/**
 * <p>
 * Title: ModuleStoreNTriples
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

public class ModuleStoreNTriples extends AbstractModuleStoreRdf {

	private FileOutputStream outputStream;
	private RDFWriter rdfWriter;

	public ModuleStoreNTriples(File file) {
		this(file, null);

	}

	public ModuleStoreNTriples(File file, String defaultNamespace) {
		super(defaultNamespace);

		try {
			outputStream = new FileOutputStream(file);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		rdfWriter = Rio.createWriter(RDFFormat.NTRIPLES, outputStream);

		try {
			rdfWriter.startRDF();
		} catch (RDFHandlerException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void storeAll(Set<Statement> statements) {
		for (Statement statement : statements)
			store(statement);
	}

	@Override
	public void store(Statement statement) {
		try {
			rdfWriter.handleStatement(statement);
		} catch (RDFHandlerException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void close() {
		try {
			rdfWriter.endRDF();
		} catch (RDFHandlerException e) {
			throw new RuntimeException(e);
		}

		try {
			outputStream.close();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

}
