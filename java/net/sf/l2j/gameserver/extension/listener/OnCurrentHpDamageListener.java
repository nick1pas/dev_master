package net.sf.l2j.gameserver.extension.listener;

import net.sf.l2j.gameserver.model.L2Skill;
import net.sf.l2j.gameserver.model.actor.Creature;

public interface OnCurrentHpDamageListener
{
	void onCurrentHpDamage(Creature creature, double damageHp, Creature target, L2Skill skill);
}