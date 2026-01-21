package mods.fakeplayer.data;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.sf.l2j.commons.data.xml.IXmlReader;
import net.sf.l2j.commons.random.Rnd;
import net.sf.l2j.gameserver.model.base.Sex;

import org.w3c.dom.Document;

public class FakeNameData implements IXmlReader
{
	private final List<String> _maleNames = new ArrayList<>();
	private final List<String> _femaleNames = new ArrayList<>();
	private final Set<String> _usedNames = new HashSet<>();
	private static final String[] PREFIX =
	{
		"Ar",
		"Bel",
		"Cal",
		"Dar",
		"El",
		"Fen",
		"Gal",
		"Kor",
		"Lan",
		"Mor",
		"Nal",
		"Or",
		"Rav",
		"Sel",
		"Tal",
		"Vor"
	};
	
	private static final String[] MIDDLE =
	{
		"a",
		"e",
		"i",
		"o",
		"u",
		"ae",
		"ia",
		"or",
		"en"
	};
	
	private static final String[] SUFFIX =
	{
		"ion",
		"ar",
		"en",
		"is",
		"or",
		"eth",
		"mir",
		"dan",
		"ius"
	};
	
	private FakeNameData()
	{
		load();
	}
	
	@Override
	public void load()
	{
		_maleNames.clear();
		_femaleNames.clear();
		_usedNames.clear();
		parseFile("data/mods/fake_names.xml");
	}
	
	@Override
	public void parseDocument(Document doc, Path path)
	{
		forEach(doc, "fakeNames", root -> {
			forEach(root, "male", male -> {
				forEach(male, "name", n -> {
					final String name = n.getTextContent().trim();
					if (!name.isEmpty())
						_maleNames.add(name);
				});
			});
			
			forEach(root, "female", female -> {
				forEach(female, "name", n -> {
					final String name = n.getTextContent().trim();
					if (!name.isEmpty())
						_femaleNames.add(name);
				});
			});
		});
	}
	
	public synchronized String getRandomName(Sex sex)
	{
		final List<String> pool = (sex == Sex.FEMALE) ? _femaleNames : _maleNames;
		
		if (pool.isEmpty())
			return fallback();
		
		for (int i = 0; i < pool.size(); i++)
		{
			final String name = pool.get(Rnd.get(pool.size()));
			
			if (_usedNames.add(name))
				return name;
		}
		
		// todos estão em uso
		return fallback();
	}
	
	public synchronized void releaseName(String name)
	{
		_usedNames.remove(name);
	}
	
	private static String fallback()
	{
		for (int i = 0; i < 50; i++) // tenta algumas vezes
		{
			String name = PREFIX[Rnd.get(PREFIX.length)] + MIDDLE[Rnd.get(MIDDLE.length)] + SUFFIX[Rnd.get(SUFFIX.length)];
			
			// Capitalização correta
			name = Character.toUpperCase(name.charAt(0)) + name.substring(1);
			
			if (FakeNameData.getInstance()._usedNames.add(name))
				return name;
		}
		
		// Último recurso (quase nunca)
		return "Player" + Rnd.get(10_000);
	}
	
	public static FakeNameData getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		private static final FakeNameData INSTANCE = new FakeNameData();
	}
}
