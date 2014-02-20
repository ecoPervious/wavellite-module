/*
 * Copyright (C) 2014 see CREDITS.txt
 * All rights reserved.
 */

package fi.uef.envi.wavellite.module.store.sesame;

import java.io.File;
import java.util.Set;

import org.openrdf.model.Model;
import org.openrdf.model.Statement;
import org.openrdf.model.impl.LinkedHashModel;
import org.openrdf.query.GraphQuery;
import org.openrdf.query.GraphQueryResult;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.QueryLanguage;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.sail.Sail;
import org.openrdf.sail.memory.MemoryStore;
import org.openrdf.sail.nativerdf.NativeStore;

import fi.uef.envi.wavellite.module.store.base.AbstractModuleStoreRdf;

/**
 * <p>
 * Title: ModuleStoreSail
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

public class ModuleStoreSail extends AbstractModuleStoreRdf {

	private Repository repo;
	private RepositoryConnection conn;

	public ModuleStoreSail() {
		this(new MemoryStore());
	}

	public ModuleStoreSail(File directory) {
		// Creates a persistent store, see
		// http://openrdf.callimachus.net/sesame/2.7/docs/users.docbook?view#Creating_a_Native_RDF_Repository
		this(new NativeStore(directory));
	}

	public ModuleStoreSail(File directory, String defaultNamespace) {
		this(new NativeStore(directory), defaultNamespace);
	}

	public ModuleStoreSail(String defaultNamespace) {
		this(new MemoryStore(), defaultNamespace);
	}

	public ModuleStoreSail(Sail sail) {
		this(sail, null);
	}

	public ModuleStoreSail(Sail sail, String defaultNamespace) {
		super(defaultNamespace);

		try {
			repo = new SailRepository(sail);
			repo.initialize();
			conn = repo.getConnection();
		} catch (RepositoryException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void store(Statement statement) {
		try {
			conn.add(statement);
		} catch (RepositoryException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void storeAll(Set<Statement> statements) {
		for (Statement statement : statements)
			store(statement);
	}
	
	@Override
	public long size() {
		try {
			return conn.size();
		} catch (RepositoryException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void close() {
		try {
			conn.close();
			repo.shutDown();
		} catch (RepositoryException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	protected Model executeSparql(String sparql) {
		Model ret = new LinkedHashModel();

		try {
			GraphQuery graphQuery = conn.prepareGraphQuery(
					QueryLanguage.SPARQL, sparql);
			GraphQueryResult result = graphQuery.evaluate();

			try {
				while (result.hasNext()) {
					ret.add(result.next());
				}
			} finally {
				result.close();
			}
		} catch (MalformedQueryException | RepositoryException
				| QueryEvaluationException e) {
			throw new RuntimeException(e);
		}
		
		return ret;
	}

}
