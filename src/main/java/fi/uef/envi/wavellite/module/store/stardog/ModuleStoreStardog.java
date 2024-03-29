/*
 * Copyright (C) 2012 see CREDITS.txt
 * All rights reserved.
 */

package fi.uef.envi.wavellite.module.store.stardog;

import java.util.Collections;
import java.util.Set;

import org.openrdf.model.Model;
import org.openrdf.model.Namespace;
import org.openrdf.model.Statement;
import org.openrdf.model.impl.LinkedHashModel;
import org.openrdf.query.GraphQueryResult;
import org.openrdf.query.QueryEvaluationException;

import com.complexible.common.rdf.model.Namespaces;
import com.complexible.stardog.StardogException;
import com.complexible.stardog.api.Adder;
import com.complexible.stardog.api.Connection;
import com.complexible.stardog.api.ConnectionConfiguration;
import com.complexible.stardog.api.GraphQuery;
import com.complexible.stardog.api.UpdateQuery;
import com.complexible.stardog.reasoning.api.ReasoningType;

import fi.uef.envi.wavellite.module.store.base.AbstractModuleStoreRdf;

/**
 * <p>
 * Title: StoreModuleStardog
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Project: Wavellite Module
 * </p>
 * <p>
 * Copyright: Copyright (C) 2012
 * </p>
 * 
 * @author Markus Stocker, markus.stocker@uef.fi
 */

public class ModuleStoreStardog extends AbstractModuleStoreRdf {

	private String protocol;
	private String host;
	private int port;
	private String database;
	private String user;
	private String password;
	private ReasoningType reasoningType;
	private String url;
	private Connection conn;
	private Namespaces namespaces;

	public ModuleStoreStardog(String host, String database) {
		this("snarl", host, 5820, database, "admin", "admin",
				ReasoningType.NONE, null);
	}

	public ModuleStoreStardog(String host, String database,
			String defaultNamespace) {
		this("snarl", host, 5820, database, "admin", "admin",
				ReasoningType.NONE, defaultNamespace);
	}

	public ModuleStoreStardog(String protocol, String host, int port,
			String database) {
		this(protocol, host, port, database, "admin", "admin",
				ReasoningType.NONE, null);
	}

	public ModuleStoreStardog(String protocol, String host, int port,
			String database, String defaultNamespace) {
		this(protocol, host, port, database, "admin", "admin",
				ReasoningType.NONE, defaultNamespace);
	}

	public ModuleStoreStardog(String protocol, String host, int port,
			String database, String user, String password,
			String defaultNamespace) {
		this(protocol, host, port, database, user, password,
				ReasoningType.NONE, defaultNamespace);
	}

	public ModuleStoreStardog(String protocol, String host, int port,
			String database, String user, String password) {
		this(protocol, host, port, database, user, password,
				ReasoningType.NONE, null);
	}

	public ModuleStoreStardog(String protocol, String host, int port,
			String database, String user, String password,
			ReasoningType reasoningType) {
		this(protocol, host, port, database, user, password, reasoningType,
				null);
	}

	public ModuleStoreStardog(String protocol, String host, int port,
			String database, String user, String password,
			ReasoningType reasoningType, String defaultNamespace) {
		if (protocol == null)
			throw new NullPointerException("[protocol = null]");
		if (!protocol.equals("snarl"))
			throw new RuntimeException("Unsupported protocol [protocol = "
					+ protocol + "]");
		if (host == null)
			throw new NullPointerException("[host = null]");
		if (port < 0 || port > 65535)
			throw new RuntimeException("Invalid port [port = " + port + "]");
		if (database == null)
			throw new NullPointerException("[database = null]");
		if (user == null)
			throw new NullPointerException("[user = null]");
		if (password == null)
			throw new NullPointerException("[password = null]");
		if (reasoningType == null)
			reasoningType = ReasoningType.NONE;

		this.protocol = protocol;
		this.host = host;
		this.port = port;
		this.database = database;
		this.user = user;
		this.password = password;
		this.reasoningType = reasoningType;
		this.defaultNamespace = defaultNamespace;

		this.url = protocol + "://" + host + ":" + port;

		open();
	}

	@Override
	public void open() {		
		ConnectionConfiguration conf = ConnectionConfiguration.to(database)
				.server(url).credentials(user, password);

		try {
			conn = conf.connect();
			namespaces = conn.namespaces();

			if (defaultNamespace == null) {
				for (Namespace namespace : namespaces) {
					if (namespace.equals(Namespaces.DEFAULT))
						defaultNamespace = namespace.getName();
				}
			}
		} catch (StardogException e) {
			e.printStackTrace();
		}
		
		super.open();
	}
	
	public String getProtocol() {
		return protocol;
	}

	public String getHost() {
		return host;
	}

	public int getPort() {
		return port;
	}

	public String getDatabase() {
		return database;
	}

	public String getUser() {
		return user;
	}

	public String getPassword() {
		return password;
	}

	public ReasoningType getReasoningType() {
		return reasoningType;
	}

	@Override
	public void store(Statement statement) {
		storeAll(Collections.singleton(statement));
	}

	@Override
	public void storeAll(Set<Statement> statements) {
		try {
			conn.begin();

			Adder adder = conn.add();

			for (Statement statement : statements) {
				adder.statement(statement);
			}

			conn.commit();
		} catch (StardogException e) {
			e.printStackTrace();
		}
	}

	@Override
	protected Model executeSelectQuery(String sparql) {
		Model ret = new LinkedHashModel();

		try {
			GraphQuery graphQuery = conn.graph(sparql);
			GraphQueryResult result = graphQuery.execute();

			while (result.hasNext()) {
				ret.add(result.next());
			}
		} catch (StardogException | QueryEvaluationException e) {
			throw new RuntimeException(e);
		}

		return ret;
	}
	
	@Override
	protected void executeDeleteQuery(String sparql) {
		try {
			UpdateQuery updateQuery = conn.update(sparql);
			updateQuery.execute();
		} catch (StardogException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public long size() {
		try {
			return conn.size();
		} catch (StardogException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void close() {
		try {
			conn.close();
		} catch (StardogException e) {
			e.printStackTrace();
		}
	}

}
