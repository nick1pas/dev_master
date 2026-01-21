package net.sf.l2j.gameserver.extension.log;

import java.util.logging.Logger;

import net.sf.l2j.commons.lang.StringUtil;

public final class PlugsLogger
{
	public static Logger LOGGER = Logger.getLogger(PlugsLogger.class.getName());
	
	public static void header()
	{
		StringUtil.printSection("Plungs");
	}
	
	public static void info(String msg)
	{
		LOGGER.info("[Plungs] " + msg);
	}
	
	public static void warn(String msg)
	{
		LOGGER.info("[Plungs] " + msg);
	}
	
	public static void error(String msg, Throwable t)
	{
		LOGGER.info("[Plungs][ERROR] " + msg);
		if (t != null)
			t.printStackTrace(System.out);
	}
}
