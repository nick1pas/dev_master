package net.sf.l2j.gameserver.extension;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;

import net.sf.l2j.gameserver.extension.cache.MemoryFileManager;
import net.sf.l2j.gameserver.extension.cache.ModClassLoader;

public final class L2JModEngine
{
	private static final Logger LOG = Logger.getLogger(L2JModEngine.class.getName());
	
	private static final File SRC_DIR = new File("data/mods/src");
	
	private static final List<L2JMod> LOADED = new ArrayList<>();
	private static ModClassLoader CLASS_LOADER;
	
	// ===============================
	// LOAD
	// ===============================
	public static synchronized void load()
	{
		unload();
		compileAndLoad();
	}
	
	// ===============================
	// HOT RELOAD
	// ===============================
	public static synchronized void reload()
	{
		LOG.info("[ModEngine] Hot reload iniciado...");
		load();
		LOG.info("[ModEngine] Hot reload finalizado.");
	}
	
	// ===============================
	// UNLOAD
	// ===============================
	private static void unload()
	{
		for (L2JMod mod : LOADED)
		{
			try
			{
				mod.onUnload();
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
		
		LOADED.clear();
		CLASS_LOADER = null;
		
		System.gc(); // hint
	}
	
	// ===============================
	// COMPILE + LOAD
	// ===============================
	private static void compileAndLoad()
	{
		JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
		if (compiler == null)
			throw new IllegalStateException("Servidor precisa rodar com JDK.");
		
		List<File> sources = new ArrayList<>();
		scanSources(SRC_DIR, sources);
		
		if (sources.isEmpty())
			return;
		
		StandardJavaFileManager standard = compiler.getStandardFileManager(null, null, null);
		MemoryFileManager manager = new MemoryFileManager(standard);
		
		Iterable<? extends JavaFileObject> units = standard.getJavaFileObjectsFromFiles(sources);
		
		List<String> options = List.of("-encoding", "UTF-8", "-classpath", System.getProperty("java.class.path"));
		
		boolean ok = compiler.getTask(null, manager, null, options, null, units).call();
		
		if (!ok)
			throw new RuntimeException("Erro ao compilar mods");
		
		CLASS_LOADER = new ModClassLoader(manager.getAllClasses(), L2JModEngine.class.getClassLoader());
		
		loadClasses(manager.getAllClasses().keySet());
	}
	
	// ===============================
	// LOAD CLASSES
	// ===============================
	private static void loadClasses(Set<String> classNames)
	{
		for (String name : classNames)
		{
			try
			{
				Class<?> c = CLASS_LOADER.loadClass(name);
				
				if (!L2JMod.class.isAssignableFrom(c))
					continue;
				
				L2JMod mod = (L2JMod) c.getDeclaredConstructor().newInstance();
				mod.onLoad();
				
				LOADED.add(mod);
				LOG.info("[ModEngine] Mod carregado: " + name);
			}
			catch (Exception e)
			{
				LOG.severe("[ModEngine] Falha ao carregar: " + name);
				e.printStackTrace();
			}
		}
	}
	
	// ===============================
	// UTILS
	// ===============================
	private static void scanSources(File dir, List<File> list)
	{
		if (!dir.exists())
			return;
		
		for (File f : dir.listFiles())
		{
			if (f.isDirectory())
				scanSources(f, list);
			else if (f.getName().endsWith(".java"))
				list.add(f);
		}
	}
}