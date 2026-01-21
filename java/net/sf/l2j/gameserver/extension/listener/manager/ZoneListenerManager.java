package net.sf.l2j.gameserver.extension.listener.manager;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import net.sf.l2j.gameserver.extension.listener.zone.OnZoneEnterLeaveListener;
import net.sf.l2j.gameserver.model.actor.Creature;
import net.sf.l2j.gameserver.model.zone.L2ZoneType;

public class ZoneListenerManager
{
	private static final ZoneListenerManager INSTANCE = new ZoneListenerManager();
	
	private final List<OnZoneEnterLeaveListener> zoneListeners = new CopyOnWriteArrayList<>();
	
	private ZoneListenerManager()
	{
	}
	
	public static ZoneListenerManager getInstance()
	{
		return INSTANCE;
	}
	
	public void addZoneListener(OnZoneEnterLeaveListener listener)
	{
		zoneListeners.add(listener);
	}
	
	public void removeZoneListener(OnZoneEnterLeaveListener listener)
	{
		zoneListeners.remove(listener);
	}
	
	public void notifyZoneEnter(L2ZoneType zone, Creature creature)
	{
		for (OnZoneEnterLeaveListener listener : zoneListeners)
		{
			try
			{
				listener.onEnter(zone, creature);
			}
			catch (Exception e)
			{
				System.err.println("[ZoneListenerManager] Erro no onEnter: " + e.getMessage());
				e.printStackTrace();
			}
		}
	}
	
	public void notifyZoneLeave(L2ZoneType zone, Creature creature)
	{
		for (OnZoneEnterLeaveListener listener : zoneListeners)
		{
			try
			{
				listener.onExit(zone, creature);
			}
			catch (Exception e)
			{
				System.err.println("[ZoneListenerManager] Erro no onExit: " + e.getMessage());
				e.printStackTrace();
			}
		}
	}
}