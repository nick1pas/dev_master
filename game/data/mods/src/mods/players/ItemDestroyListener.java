package mods.players;

import net.sf.l2j.gameserver.extension.listener.actor.player.OnItemDestroyListener;
import net.sf.l2j.gameserver.model.actor.Player;
import net.sf.l2j.gameserver.model.item.instance.ItemInstance;

public class ItemDestroyListener implements OnItemDestroyListener
{
	@Override
	public void onItemDestroy(Player player, ItemInstance item)
	{
		player.sendMessage("Voce destruiu o item: " + item.getItemName());
	}
}