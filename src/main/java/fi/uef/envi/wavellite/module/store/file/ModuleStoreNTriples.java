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
import fi.uef.envi.wavellite.vocabulary.WTO;

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
		this(file, WTO.ns);

	}

	public ModuleStoreNTriples(File file, String defaultNamespace) {
		super(defaultNamespace);

		try {
			outputStream = new FileOutputStream(file);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		rdfWriter = Rio.createWriter(RDFFormat.NTRIPLES, outputStream);
	}

	@Override
	public void store(Set<Statement> statements) {
		try {
			rdfWriter.startRDF();
			
			for (Statement statement : statements) {
				rdfWriter.handleStatement(statement);
			}
			
			rdfWriter.endRDF();
		} catch (RDFHandlerException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void close() {
		try {
			outputStream.close();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

}
