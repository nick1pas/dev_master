package net.sf.l2j.gameserver.instancemanager.custom;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import net.sf.l2j.dresmee.DressMe;
import net.sf.l2j.gameserver.xmlfactory.XMLDocument;

import org.w3c.dom.Document;


public class DressMeData extends XMLDocument
{
	private final List<DressMe> _entries = new ArrayList<>();
	
	public DressMeData()
	{
		load();
	}
	
	public void reload()
	{
		_entries.clear();
		load();
	}
	
	public void reloadPL()
	{
		_entries.clear();
		Playerload();
	}
	
	@Override
	public void load()
	{
		loadDocument("./data/xml/custom/DressMe.xml");
		LOGGER.info("Loaded {} dressme templates.", _entries.size());
		
		
	}
	
	public void Playerload()
	{
		loadDocument("./data/xml/custom/DressMe.xml");	
	}

	@Override
	public void parseDocument(Document doc, File f)
	{
		forEach(doc, "list", listNode -> forEach(listNode, "dressme", dressNode -> _entries.add(new DressMe(parseAttributes(dressNode)))));
	}
	
	public DressMe getItemId(int itemId)
	{
		return _entries.stream().filter(x -> x.getItemId() == itemId).findFirst().orElse(null);
	}
	
	public List<DressMe> getEntries(int skinId)
	{
		return _entries;
	}
	
	public static DressMeData getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		protected static final DressMeData INSTANCE = new DressMeData();
	}


}