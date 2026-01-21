package net.sf.l2j.gameserver.extension.listener;

import net.sf.l2j.gameserver.model.actor.Creature;

public interface OnAttackHitListener
{
	void onAttackHit(Creature creature, Creature target, int damage);
}