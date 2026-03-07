package net.chefcraft.core.database;

import java.sql.*;
import java.util.Properties;
import java.util.logging.Logger;

public class ChefDriver implements Driver {

	private final Driver delegate;
	
	public ChefDriver(Driver delegate) {
		this.delegate = delegate;
	}
	
	@Override
	public Connection connect(String url, Properties info) throws SQLException {
		return this.delegate.connect(url, info);
	}

	@Override
	public boolean acceptsURL(String url) throws SQLException {
		return this.delegate.acceptsURL(url);
	}

	@Override
	public DriverPropertyInfo[] getPropertyInfo(String url, Properties info) throws SQLException {
		return this.delegate.getPropertyInfo(url, info);
	}

	@Override
	public int getMajorVersion() {
		return this.delegate.getMajorVersion();
	}

	@Override
	public int getMinorVersion() {
		return this.delegate.getMinorVersion();
	}

	@Override
	public boolean jdbcCompliant() {
		return this.delegate.jdbcCompliant();
	}

	@Override
	public Logger getParentLogger() throws SQLFeatureNotSupportedException {
		return this.delegate.getParentLogger();
	}
}
