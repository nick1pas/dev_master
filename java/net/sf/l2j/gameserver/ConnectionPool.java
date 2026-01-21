package net.sf.l2j.gameserver;

import java.sql.Connection;
import java.sql.SQLException;

import net.sf.l2j.Config;
import net.sf.l2j.commons.lang.CLogger;

import org.mariadb.jdbc.MariaDbPoolDataSource;

public final class ConnectionPool
{
	private ConnectionPool()
	{
		throw new IllegalStateException("Utility class");
	}
	
	private static final CLogger LOGGER = new CLogger(ConnectionPool.class.getName());
	
	private static MariaDbPoolDataSource _source;
	
	public static void init()
	{
		try
		{
			_source = new MariaDbPoolDataSource();
			_source.setUrl(Config.DATABASE_URL);
			
			LOGGER.info("MariaDB ConnectionPool iniciado.");
		}
		catch (SQLException e)
		{
			LOGGER.error("Erro ao inicializar o pool MariaDB.", e);
		}
	}
	
	public static void shutdown()
	{
		if (_source != null)
		{
			_source.close();
			_source = null;
		}
	}
	
	public static Connection getConnection() throws SQLException
	{
		return _source.getConnection();
	}
}