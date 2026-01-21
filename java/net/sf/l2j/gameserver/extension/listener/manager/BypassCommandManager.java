package net.sf.l2j.gameserver.extension.listener.manager;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import net.sf.l2j.gameserver.extension.listener.command.OnBypassCommandListener;
import net.sf.l2j.gameserver.model.actor.Player;

public class BypassCommandManager
{
	private static final BypassCommandManager INSTANCE = new BypassCommandManager();
	
	private final List<OnBypassCommandListener> listeners = new CopyOnWriteArrayList<>();
	
	public static BypassCommandManager getInstance()
	{
		return INSTANCE;
	}
	
	public void registerBypassListener(OnBypassCommandListener listener)
	{
		listeners.add(listener);
	}
	
	public void unregisterBypassListener(OnBypassCommandListener listener)
	{
		listeners.remove(listener);
	}
	
	public boolean notify(Player player, String command)
	{
		for (OnBypassCommandListener listener : listeners)
		{
			if (listener.onBypass(player, command))
			{
				return true;
			}
		}
		return false;
	}
}