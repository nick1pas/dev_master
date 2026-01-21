package mods.players;

import net.sf.l2j.gameserver.extension.listener.OnDeathListener;
import net.sf.l2j.gameserver.model.actor.Creature;
import net.sf.l2j.gameserver.model.actor.Player;

public class DeathPenaltyListener implements OnDeathListener
{
	@Override
	public void onDeath(Creature victim)
	{
		if (!(victim instanceof Player))
			return;
		
		Player player = (Player) victim;
		player.sendMessage("Voce morreu. Tenha mais cuidado!");
	}
}