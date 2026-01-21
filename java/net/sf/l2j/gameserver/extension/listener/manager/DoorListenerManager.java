package net.sf.l2j.gameserver.extension.listener.manager;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import net.sf.l2j.gameserver.extension.listener.actor.door.OnOpenCloseListener;
import net.sf.l2j.gameserver.model.actor.instance.L2DoorInstance;

public class DoorListenerManager
{
	private static final DoorListenerManager INSTANCE = new DoorListenerManager();
	
	private final List<OnOpenCloseListener> openCloseListeners = new CopyOnWriteArrayList<>();
	
	private DoorListenerManager()
	{
	}
	
	public static DoorListenerManager getInstance()
	{
		return INSTANCE;
	}
	
	public void registerOpenCloseListener(OnOpenCloseListener listener)
	{
		openCloseListeners.add(listener);
	}
	
	public void unregisterOpenCloseListener(OnOpenCloseListener listener)
	{
		openCloseListeners.remove(listener);
	}
	
	public void notifyDoorOpen(L2DoorInstance door)
	{
		for (OnOpenCloseListener listener : openCloseListeners)
		{
			listener.onOpen(door);
		}
	}

	public void notifyDoorClose(L2DoorInstance door)
	{
		for (OnOpenCloseListener listener : openCloseListeners)
		{
			listener.onClose(door);
		}
	}
}