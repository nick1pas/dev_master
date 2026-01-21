package mods.zone;

import net.sf.l2j.gameserver.extension.listener.zone.OnZoneEnterLeaveListener;
import net.sf.l2j.gameserver.model.actor.Creature;
import net.sf.l2j.gameserver.model.actor.Player;
import net.sf.l2j.gameserver.model.zone.L2ZoneType;
import net.sf.l2j.gameserver.model.zone.ZoneId;

public class PvPZoneListener implements OnZoneEnterLeaveListener
{
	@Override
	public void onEnter(L2ZoneType zone, Creature creature)
	{
		if (!(creature instanceof Player))
			return;
		
		Player player = creature.getActingPlayer();
		
		if (player.isInsideZone(ZoneId.FLAG))
			player.sendMessage("Voce entrou na zona: " + "Chatoic Flag");
	}
	
	@Override
	public void onExit(L2ZoneType zone, Creature creature)
	{
		if (!(creature instanceof Player))
			return;
		
		Player player = creature.getActingPlayer();
		
		if (player.isInsideZone(ZoneId.FLAG))
			player.sendMessage("Voce saiu da zona: " + "Chatoic Flag");
	}
}