package net.sf.l2j.gameserver.extension.listener.manager;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import net.sf.l2j.gameserver.extension.listener.actor.npc.OnDecayListener;
import net.sf.l2j.gameserver.extension.listener.actor.npc.OnInteractListener;
import net.sf.l2j.gameserver.extension.listener.actor.npc.OnNpcKillListener;
import net.sf.l2j.gameserver.extension.listener.actor.npc.OnSpawnListener;
import net.sf.l2j.gameserver.model.actor.L2Npc;
import net.sf.l2j.gameserver.model.actor.Player;

public class NpcListenerManager
{
	private static final NpcListenerManager INSTANCE = new NpcListenerManager();
	
	private final List<OnSpawnListener> npcSpawnListeners = new CopyOnWriteArrayList<>();
	private final List<OnDecayListener> npcDecayListeners = new CopyOnWriteArrayList<>();
	private final List<OnInteractListener> npcInteractListeners = new CopyOnWriteArrayList<>();
	private final List<OnNpcKillListener> npcKillListeners = new CopyOnWriteArrayList<>();

	private NpcListenerManager()
	{
	}
	
	public static NpcListenerManager getInstance()
	{
		return INSTANCE;
	}
	
	public void registerNpcSpawnListener(OnSpawnListener listener)
	{
		npcSpawnListeners.add(listener);
	}
	
	public void unregisterNpcSpawnListener(OnSpawnListener listener)
	{
		npcSpawnListeners.remove(listener);
	}
	
	public void notifyNpcSpawn(L2Npc npc)
	{
		for (OnSpawnListener listener : npcSpawnListeners)
		{
			listener.onSpawn(npc);
		}
	}
	
	public void registerNpcDecayListener(OnDecayListener listener)
	{
		npcDecayListeners.add(listener);
	}
	
	public void unregisterNpcDecayListener(OnDecayListener listener)
	{
		npcDecayListeners.remove(listener);
	}
	
	public void notifyNpcDecay(L2Npc npc)
	{
		for (OnDecayListener listener : npcDecayListeners)
		{
			listener.onDecay(npc);
		}
	}
	
	public void registerNpcInteractListener(OnInteractListener listener)
	{
	    npcInteractListeners.add(listener);
	}

	public void unregisterNpcInteractListener(OnInteractListener listener)
	{
	    npcInteractListeners.remove(listener);
	}

	public boolean notifyNpcInteract(L2Npc npc, Player player)
	{
	    for (OnInteractListener listener : npcInteractListeners)
	    {
	        if (listener.onInteract(npc, player))
	            return true;
	    }
	    return false;
	}
	
	public void registerNpcKillListener(OnNpcKillListener listener)
	{
	    npcKillListeners.add(listener);
	}

	public void unregisterNpcKillListener(OnNpcKillListener listener)
	{
	    npcKillListeners.remove(listener);
	}

	public void notifyNpcKill(L2Npc npc, Player killer)
	{
	    for (OnNpcKillListener listener : npcKillListeners)
	    {
	        listener.onKill(npc, killer);
	    }
	}
}