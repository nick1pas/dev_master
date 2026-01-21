package mods.players;

import net.sf.l2j.gameserver.extension.listener.OnAttackListener;
import net.sf.l2j.gameserver.model.actor.Creature;
import net.sf.l2j.gameserver.model.actor.Player;

public class AttackCheckListener implements OnAttackListener
{
	@Override
	public void onAttack(Creature attacker, Creature target)
	{
		if (!(attacker instanceof Player))
			return;
		
	}
}