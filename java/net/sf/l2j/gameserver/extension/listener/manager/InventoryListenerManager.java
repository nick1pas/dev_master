package net.sf.l2j.gameserver.extension.listener.manager;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import net.sf.l2j.gameserver.extension.listener.inventory.OnEquipListener;
import net.sf.l2j.gameserver.model.actor.Playable;
import net.sf.l2j.gameserver.model.item.instance.ItemInstance;

public class InventoryListenerManager
{
	private static final InventoryListenerManager INSTANCE = new InventoryListenerManager();
	
	private final List<OnEquipListener> equipListeners = new CopyOnWriteArrayList<>();
	
	private InventoryListenerManager()
	{
	}
	
	public static InventoryListenerManager getInstance()
	{
		return INSTANCE;
	}
	
	public void registerEquipListener(OnEquipListener listener)
	{
		equipListeners.add(listener);
	}
	
	public void unregisterEquipListener(OnEquipListener listener)
	{
		equipListeners.remove(listener);
	}
	
	public void notifyEquip(int slotId, ItemInstance item, Playable playable)
	{
		for (OnEquipListener listener : equipListeners)
		{
			listener.onEquip(slotId, item, playable);
		}
	}
	
	public void notifyUnequip(int slotId, ItemInstance item, Playable playable)
	{
		for (OnEquipListener listener : equipListeners)
		{
			listener.onUnequip(slotId, item, playable);
		}
	}
}