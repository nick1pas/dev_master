package mods.players;

import net.sf.l2j.gameserver.extension.listener.inventory.OnEquipListener;
import net.sf.l2j.gameserver.model.actor.Playable;
import net.sf.l2j.gameserver.model.actor.Player;
import net.sf.l2j.gameserver.model.item.instance.ItemInstance;

public class PlayerEquipeListener implements OnEquipListener
{
	
	@Override
	public void onEquip(int BodyPart, ItemInstance item, Playable playable)
	{
		if (!(playable instanceof Player))
			return;
		
		Player player = (Player) playable;
		
		player.sendMessage("Voce desequipou " + item.getItem().getName());
		
	}
	
	@Override
	public void onUnequip(int BodyPart, ItemInstance item, Playable playable)
	{
		if (!(playable instanceof Player))
			return;
		
		Player player = (Player) playable;
		
		player.sendMessage("Voce equipou " + item.getItem().getName());
	}
	
}
