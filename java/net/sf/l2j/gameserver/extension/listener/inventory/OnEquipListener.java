package net.sf.l2j.gameserver.extension.listener.inventory;

import net.sf.l2j.gameserver.model.actor.Playable;
import net.sf.l2j.gameserver.model.item.instance.ItemInstance;

public interface OnEquipListener
{
	void onEquip(int slotId, ItemInstance item, Playable playable);

	void onUnequip(int slotId, ItemInstance item, Playable playable);

}