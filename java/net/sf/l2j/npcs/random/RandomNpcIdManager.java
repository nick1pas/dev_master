package net.sf.l2j.npcs.random;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.*;

public class RandomNpcIdManager
{
	private static final Logger _log = Logger.getLogger(RandomNpcIdManager.class.getName());

	private static final Map<Integer, int[]> _randomNpcMap = new HashMap<>();

	private static final String FILE_PATH = "data/xml/custom/RandomNpcsID.xml";

	private RandomNpcIdManager()
	{
		load();
	}

	private static void load()
	{
		_randomNpcMap.clear();

		try
		{
			File file = new File(FILE_PATH);
			if (!file.exists())
			{
				_log.warning("[RandomNpcIdManager] Arquivo não encontrado: " + FILE_PATH);
				return;
			}

			Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(file);
			doc.getDocumentElement().normalize();

			NodeList nodeList = doc.getElementsByTagName("item");

			for (int i = 0; i < nodeList.getLength(); i++)
			{
				Node node = nodeList.item(i);
				if (node.getNodeType() != Node.ELEMENT_NODE)
					continue;

				Element element = (Element) node;

				int npcMain = Integer.parseInt(element.getAttribute("NpcMain"));
				String[] idStrings = element.getAttribute("NewIdListFake").split(",");
				int[] ids = new int[idStrings.length];

				for (int j = 0; j < idStrings.length; j++)
					ids[j] = Integer.parseInt(idStrings[j].trim());

				_randomNpcMap.put(npcMain, ids);
			}

			_log.info("[RandomNpcIdManager] Carregados " + _randomNpcMap.size() + " NPCs com FakeIDs.");
		}
		catch (Exception e)
		{
			_log.severe("[RandomNpcIdManager] Erro ao carregar " + FILE_PATH + ": " + e.getMessage());
			e.printStackTrace();
		}
	}

	public static boolean isRandomNpc(int npcId)
	{
		return _randomNpcMap.containsKey(npcId);
	}

	public static int[] getFakeIds(int npcId)
	{
		return _randomNpcMap.get(npcId);
	}

	public static RandomNpcIdManager getInstance()
	{
		return SingletonHolder.INSTANCE;
	}

	private static class SingletonHolder
	{
		private static final RandomNpcIdManager INSTANCE = new RandomNpcIdManager();
	}
	/**
	 * Retorna o NPC original (NpcMain) para um dado npcId (que pode ser fakeId).
	 * Retorna -1 se o npcId não for fake (ou seja, já for original).
	 * @param npcId 
	 * @return 
	 */
	public static int getOriginalNpcId(int npcId)
	{
	    // Primeiro verifica se é original
	    if (_randomNpcMap.containsKey(npcId))
	        return -1; // npcId é original (NpcMain)

	    // Se não for original, procura entre os valores fakeIds
	    for (Map.Entry<Integer, int[]> entry : _randomNpcMap.entrySet())
	    {
	        int originalId = entry.getKey();
	        int[] fakeIds = entry.getValue();
	        for (int fakeId : fakeIds)
	        {
	            if (fakeId == npcId)
	                return originalId;
	        }
	    }

	    // Não achou original para esse id, retorna -1
	    return -1;
	}

}
