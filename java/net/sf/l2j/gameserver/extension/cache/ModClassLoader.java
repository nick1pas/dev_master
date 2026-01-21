package net.sf.l2j.gameserver.extension.cache;

import java.util.Map;

public final class ModClassLoader extends ClassLoader
{
	private final Map<String, byte[]> classes;
	
	public ModClassLoader(Map<String, byte[]> classes, ClassLoader parent)
	{
		super(parent);
		this.classes = classes;
	}
	
	@Override
	protected Class<?> findClass(String name) throws ClassNotFoundException
	{
		byte[] bytes = classes.get(name);
		if (bytes == null)
			throw new ClassNotFoundException(name);
		
		return defineClass(name, bytes, 0, bytes.length);
	}
}
