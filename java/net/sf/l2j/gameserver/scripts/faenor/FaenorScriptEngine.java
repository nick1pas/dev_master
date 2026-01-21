package net.sf.l2j.gameserver.scripts.faenor;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Logger;
import java.util.logging.Level;
import javax.script.ScriptContext;

import net.sf.l2j.gameserver.scripts.Parser;
import net.sf.l2j.gameserver.scripts.ParserNotCreatedException;
import net.sf.l2j.gameserver.scripts.ScriptDocument;
import net.sf.l2j.gameserver.scripts.ScriptEngine;
import net.sf.l2j.util.XMLFilter;

import org.w3c.dom.Node;

public class FaenorScriptEngine extends ScriptEngine
{
	private static final Logger _log = Logger.getLogger(FaenorScriptEngine.class.getName());

	protected FaenorScriptEngine()
	{
		final File packDirectory = new File("data/xml/faenor/");
		final File[] files = packDirectory.listFiles(new XMLFilter());
		for (File file : files)
		{
			try (InputStream in = new FileInputStream(file))
			{
				parseScript(new ScriptDocument(file.getName(), in), null);
			}
			catch (IOException e)
			{
				_log.log(Level.WARNING, e.getMessage(), e);
			}
		}
	}

	public void parseScript(ScriptDocument script, ScriptContext context)
	{
		Node node = script.getDocument().getFirstChild();
		String parserClass = "faenor.Faenor" + node.getNodeName() + "Parser";
		
		Parser parser = null;
		try
		{
			parser = createParser(parserClass);
		}
		catch (ParserNotCreatedException e)
		{
			_log.log(Level.WARNING, "ERROR: No parser registered for Script: " + parserClass + ": " + e.getMessage(), e);
		}
		
		if (parser == null)
		{
			_log.warning("Unknown Script Type: " + script.getName());
			return;
		}
		
		try
		{
			parser.parseScript(node, context);
			_log.info(getClass().getSimpleName() + ": Loaded  " + script.getName() + " successfully.");
		}
		catch (Exception e)
		{
			_log.log(Level.WARNING, "Script Parsing Failed: " + e.getMessage(), e);
		}
	}

	public static FaenorScriptEngine getInstance()
	{
		return SingletonHolder._instance;
	}
	
	private static class SingletonHolder
	{
		protected static final FaenorScriptEngine _instance = new FaenorScriptEngine();
	}
}