package mods.players;

import net.sf.l2j.gameserver.extension.listener.OnKillListener;
import net.sf.l2j.gameserver.model.actor.Creature;
import net.sf.l2j.gameserver.model.actor.Player;

public class PvPKillListener implements OnKillListener
{
	@Override
	public void onKill(Creature killer, Creature victim)
	{
		if (!(killer instanceof Player))
			return;
		
		Player pk = (Player) killer;
		Creature dead = victim;
		
		pk.sendMessage("Voce matou " + dead.getName());
	}
	
	@Override
	public boolean ignorePetOrSummon()
	{
		return true;
	}
}