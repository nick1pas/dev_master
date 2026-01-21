package mods.players;

import net.sf.l2j.gameserver.extension.listener.actor.player.OnItemPickupListener;
import net.sf.l2j.gameserver.model.actor.Player;
import net.sf.l2j.gameserver.model.item.instance.ItemInstance;

public class ItemPickupListener implements OnItemPickupListener
{
	@Override
	public void onItemPickup(Player player, ItemInstance item)
	{
		player.sendMessage("Item coletado: " + item.getItemName());
	}
}
