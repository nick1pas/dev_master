package mods.players;

import net.sf.l2j.gameserver.extension.listener.OnCurrentHpDamageListener;
import net.sf.l2j.gameserver.model.L2Skill;
import net.sf.l2j.gameserver.model.actor.Creature;
import net.sf.l2j.gameserver.model.actor.Player;

public class ReduceHPCurrentListener implements OnCurrentHpDamageListener
{
	
	@Override
	public void onCurrentHpDamage(Creature attacker, double damage, Creature target, L2Skill skill)
	{
		if (!(attacker instanceof Player))
			return;
		
		Player atk = (Player) attacker;
		if (skill != null)
			atk.sendMessage("Voce rancou " + (int) damage + " de HP em " + target.getName() + " Skill Name: " + skill.getName());
		else
			atk.sendMessage("Voce rancou " + (int) damage + " de HP em " + target.getName());
		
	}
	
}
