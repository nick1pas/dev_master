package net.sf.l2j.gameserver.scripts.faenor;


import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import net.sf.l2j.Config;
import net.sf.l2j.gameserver.datatables.AnnouncementTable;
import net.sf.l2j.gameserver.model.actor.template.NpcTemplate;
import net.sf.l2j.gameserver.model.item.DropCategory;
import net.sf.l2j.gameserver.model.item.DropData;
import net.sf.l2j.gameserver.scripts.DateRange;
import net.sf.l2j.gameserver.scripts.EngineInterface;
import net.sf.l2j.gameserver.scripts.EventDroplist;

public class FaenorInterface implements EngineInterface
{
	protected static final Logger LOGGER = Logger.getLogger(FaenorInterface.class.getName());
	
	public static FaenorInterface getInstance()
	{
		return SingletonHolder._instance;
	}
	
	private FaenorInterface()
	{
	}
	
	public List<?> getAllPlayers()
	{
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public void addQuestDrop(final int npcID, final int itemID, final int min, final int max, final int chance, final String questID, final String[] states)
	{
		final NpcTemplate npc = npcTable.getTemplate(npcID);
		if (npc == null)
		{
			LOGGER.info("FeanorInterface: Npc " + npcID + " is null..");
			return;
		}
	//	final DropData drop = new DropData(itemID, min, max, chance);
		final DropData drop = new DropData();
		drop.setItemId(itemID);
		drop.setMinDrop(min);
		drop.setMaxDrop(max);
		drop.setChance(chance);
		drop.setQuestID(questID);
		drop.addStates(states);
		addDrop(npc, drop, false);
	}
	
	/**
	 * Adds a new Drop to an NPC
	 * @param npcID
	 * @param itemID
	 * @param min
	 * @param max
	 * @param sweep
	 * @param chance
	 * @throws NullPointerException
	 */
	public void addDrop(final int npcID, final int itemID, final int min, final int max, final boolean sweep, final int chance) throws NullPointerException
	{
		final NpcTemplate npc = npcTable.getTemplate(npcID);
		if (npc == null)
		{
			if (Config.DEBUG_PATH)
			{
				LOGGER.warning("Npc doesnt Exist");
			}
			throw new NullPointerException();
		}
		final DropData drop = new DropData();
	//	final DropData drop = new DropData(itemID, min, max, chance);
		drop.setItemId(itemID);
		drop.setMinDrop(min);
		drop.setMaxDrop(max);
		drop.setChance(chance);
		
		addDrop(npc, drop, sweep);
	}
	
	/**
	 * Adds a new drop to an NPC. If the drop is sweep, it adds it to the NPC's Sweep category If the drop is non-sweep, it creates a new category for this drop.
	 * @param npc
	 * @param drop
	 * @param sweep
	 */
	public void addDrop(final NpcTemplate npc, final DropData drop, final boolean sweep)
	{
		if (sweep)
		{
			addDrop(npc, drop, -1);
		}
		else
		{
			int maxCategory = -1;
			
			if (npc.getDropData() != null)
			{
				for (final DropCategory cat : npc.getDropData())
				{
					if (maxCategory < cat.getCategoryType())
					{
						maxCategory = cat.getCategoryType();
					}
				}
			}
			maxCategory++;
			npc.addDropData(drop, maxCategory);
		}
		
	}
	
	/**
	 * Adds a new drop to an NPC, in the specified category. If the category does not exist, it is created.
	 * @param npc
	 * @param drop
	 * @param category
	 */
	public void addDrop(final NpcTemplate npc, final DropData drop, final int category)
	{
		npc.addDropData(drop, category);
	}
	
	/**
	 * @param npcID
	 * @return Returns the _questDrops.
	 */
	public List<DropData> getQuestDrops(final int npcID)
	{
		final NpcTemplate npc = npcTable.getTemplate(npcID);
		if (npc == null)
			return null;
		final List<DropData> questDrops = new ArrayList<>();
		if (npc.getDropData() != null)
		{
			for (final DropCategory cat : npc.getDropData())
			{
				for (final DropData drop : cat.getAllDrops())
				{
					if (drop.getQuestID() != null)
					{
						questDrops.add(drop);
					}
				}
			}
		}
		return questDrops;
	}
	
	@Override
	public void addEventDrop(final int[] items, final int[] count, final double chance, final DateRange range)
	{
		EventDroplist.getInstance().addGlobalDrop(items, count, (int) (chance * DropData.MAX_CHANCE), range);
	}
	
	@Override
	public void onPlayerLogin(final String[] message, final DateRange validDateRange)
	{
		AnnouncementTable.getInstance().addEventAnnouncement(validDateRange, message);
	}
	
	/*public void addPetData(final ScriptContext context, final int petID, final int levelStart, final int levelEnd, final Map<String, String> stats)
	{
		final L2PetData[] petData = new L2PetData[levelEnd - levelStart + 1];
		int value = 0;
		for (int level = levelStart; level <= levelEnd; level++)
		{
			petData[level - 1] = new L2PetData();
			petData[level - 1].setPetID(petID);
			petData[level - 1].setPetLevel(level);
			
			context.setAttribute("level", Double.valueOf(level), ScriptContext.ENGINE_SCOPE);
			for (final String stat : stats.keySet())
			{
				value = ((Number) Expression.eval(context, "beanshell", stats.get(stat))).intValue();
				petData[level - 1].setStat(stat, value);
			}
			context.removeAttribute("level", ScriptContext.ENGINE_SCOPE);
		}
	}*/
	
	private static class SingletonHolder
	{
		protected static final FaenorInterface _instance = new FaenorInterface();
	}
}