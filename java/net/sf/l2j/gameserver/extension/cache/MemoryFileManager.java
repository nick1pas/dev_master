package net.sf.l2j.gameserver.extension.cache;

import java.util.HashMap;
import java.util.Map;

import javax.tools.FileObject;
import javax.tools.ForwardingJavaFileManager;
import javax.tools.JavaFileManager;
import javax.tools.JavaFileObject;

public final class MemoryFileManager extends ForwardingJavaFileManager<JavaFileManager>
{
	private final Map<String, MemoryBytecode> classes = new HashMap<>();
	
	public MemoryFileManager(JavaFileManager fileManager)
	{
		super(fileManager);
	}
	
	@Override
	public JavaFileObject getJavaFileForOutput(Location location, String className, JavaFileObject.Kind kind, FileObject sibling)
	{
		MemoryBytecode bc = new MemoryBytecode(className);
		classes.put(className, bc);
		return bc;
	}
	
	public Map<String, byte[]> getAllClasses()
	{
		Map<String, byte[]> out = new HashMap<>();
		classes.forEach((k, v) -> out.put(k, v.getBytes()));
		return out;
	}
}
