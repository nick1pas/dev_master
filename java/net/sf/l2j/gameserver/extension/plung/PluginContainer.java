package net.sf.l2j.gameserver.extension.plung;

import java.net.URLClassLoader;

import net.sf.l2j.gameserver.extension.L2JExtension;

public class PluginContainer
{
	public String _name;
	public L2JExtension _instance;
	public URLClassLoader _classLoader;
	
	public PluginContainer(String name, L2JExtension instance, URLClassLoader classLoader)
	{
		_name = name;
		_instance = instance;
		_classLoader = classLoader;
	}
}
