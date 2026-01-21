package net.sf.l2j.gameserver.extension.listener.actor.player;

import net.sf.l2j.gameserver.model.actor.Player;
import net.sf.l2j.gameserver.model.item.instance.ItemInstance;

public interface OnItemDestroyListener
{
	void onItemDestroy(Player player, ItemInstance item);
}