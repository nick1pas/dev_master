package mods.players;

import net.sf.l2j.gameserver.extension.listener.OnAttackHitListener;
import net.sf.l2j.gameserver.model.actor.Creature;
import net.sf.l2j.gameserver.model.actor.Player;

public class DamageLoggerListener implements OnAttackHitListener
{
	@Override
	public void onAttackHit(Creature attacker, Creature target, int damage)
	{
		if (!(attacker instanceof Player))
			return;
			
	}
}
