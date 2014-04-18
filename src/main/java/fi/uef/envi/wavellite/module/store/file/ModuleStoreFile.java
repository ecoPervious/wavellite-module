/*
 * Copyright (C) 2013 see CREDITS.txt
 * All rights reserved.
 */

package fi.uef.envi.wavellite.module.store.file;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Set;

import org.openrdf.model.Model;
import org.openrdf.model.Statement;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.RDFWriter;
import org.openrdf.rio.Rio;

import fi.uef.envi.wavellite.module.store.base.AbstractModuleStoreRdf;

/**
 * <p>
 * Title: ModuleStoreFile
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

public class ModuleStoreFile extends AbstractModuleStoreRdf {

	private long size = 0L;
	private FileOutputStream outputStream;
	private RDFWriter rdfWriter;
	private File file;
	private RDFFormat rdfFormat;

	public ModuleStoreFile(File file) {
		this(file, null);

	}

	public ModuleStoreFile(File file, String defaultNamespace) {
		this(file, defaultNamespace, RDFFormat.NTRIPLES);
	}

	public ModuleStoreFile(File file, String defaultNamespace,
			RDFFormat rdfFormat) {
		super(defaultNamespace);

		if (file == null)
			throw new NullPointerException("[file = null]");
		if (rdfFormat == null)
			rdfFormat = RDFFormat.NTRIPLES;
		
		this.file = file;
		this.rdfFormat = rdfFormat;

		open();
	}

	@Override
	public void open() {
		if (isOpen)
			return;

		super.open();

		// Make sure the file exists
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}

		try {
			outputStream = new FileOutputStream(file, true);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		rdfWriter = Rio.createWriter(rdfFormat, outputStream);

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
			size++;
		} catch (RDFHandlerException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public Model executeSparql(String sparql) {
		throw new UnsupportedOperationException();
	}

	@Override
	public long size() {
		return size;
	}

	@Override
	public void flush() {
		try {
			outputStream.flush();
		} catch (IOException e) {
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

		super.close();
	}

}
