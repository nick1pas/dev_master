package net.sf.l2j.gameserver.extension.listener.manager;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import net.sf.l2j.gameserver.extension.listener.command.OnTutorialLinkListener;
import net.sf.l2j.gameserver.model.actor.Player;

public class TutorialLinkCommandManager
{
	private static final TutorialLinkCommandManager INSTANCE = new TutorialLinkCommandManager();
	
	private final List<OnTutorialLinkListener> listeners = new CopyOnWriteArrayList<>();
	
	public static TutorialLinkCommandManager getInstance()
	{
		return INSTANCE;
	}
	
	public void registerListener(OnTutorialLinkListener listener)
	{
		listeners.add(listener);
	}
	
	public void unregisterListener(OnTutorialLinkListener listener)
	{
		listeners.remove(listener);
	}
	
	public boolean notify(Player player, String command)
	{
		for (OnTutorialLinkListener listener : listeners)
		{
			if (listener.onBypass(player, command))
			{
				return true;
			}
		}
		return false;
	}
}