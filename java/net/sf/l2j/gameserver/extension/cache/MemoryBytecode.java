package net.sf.l2j.gameserver.extension.cache;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.net.URI;

import javax.tools.SimpleJavaFileObject;

public final class MemoryBytecode extends SimpleJavaFileObject
{
	private final ByteArrayOutputStream baos = new ByteArrayOutputStream();
	
	public MemoryBytecode(String className)
	{
		super(URI.create("mem:///" + className.replace('.', '/') + Kind.CLASS.extension), Kind.CLASS);
	}
	
	@Override
	public OutputStream openOutputStream()
	{
		return baos;
	}
	
	byte[] getBytes()
	{
		return baos.toByteArray();
	}
}