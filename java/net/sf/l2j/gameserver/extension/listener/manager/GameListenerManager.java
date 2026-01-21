package net.sf.l2j.gameserver.extension.listener.manager;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import net.sf.l2j.gameserver.extension.listener.game.OnCharacterDeleteListener;
import net.sf.l2j.gameserver.extension.listener.game.OnShutdownListener;
import net.sf.l2j.gameserver.extension.listener.game.OnStartListener;

public class GameListenerManager
{
	private static final GameListenerManager INSTANCE = new GameListenerManager();
	
	private final List<OnCharacterDeleteListener> characterDeleteListeners = new CopyOnWriteArrayList<>();
	private final List<OnShutdownListener> shutdownListeners = new CopyOnWriteArrayList<>();
	private final List<OnStartListener> startListeners = new CopyOnWriteArrayList<>();
	
	private GameListenerManager()
	{
	}
	
	public static GameListenerManager getInstance()
	{
		return INSTANCE;
	}
	
	public void registerCharacterDeleteListener(OnCharacterDeleteListener listener)
	{
		characterDeleteListeners.add(listener);
	}
	
	public void unregisterCharacterDeleteListener(OnCharacterDeleteListener listener)
	{
		characterDeleteListeners.remove(listener);
	}
	
	public void registerShutdownListener(OnShutdownListener listener)
	{
		shutdownListeners.add(listener);
	}
	
	public void unregisterShutdownListener(OnShutdownListener listener)
	{
		shutdownListeners.remove(listener);
	}
	
	public void registerStartListener(OnStartListener listener)
	{
		startListeners.add(listener);
	}
	
	public void unregisterStartListener(OnStartListener listener)
	{
		startListeners.remove(listener);
	}
	
	public void notifyCharacterDelete(int objectId)
	{
		for (OnCharacterDeleteListener listener : characterDeleteListeners)
		{
			listener.onCharacterDelete(objectId);
		}
	}
	
	public void notifyShutdown()
	{
		for (OnShutdownListener listener : shutdownListeners)
		{
			listener.onShutdown();
		}
	}
	
	public void notifyStart()
	{
		for (OnStartListener listener : startListeners)
		{
			listener.onStart();
		}
	}
}