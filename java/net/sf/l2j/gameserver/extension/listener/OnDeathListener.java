package net.sf.l2j.gameserver.extension.listener;

import net.sf.l2j.gameserver.model.actor.Creature;

public interface OnDeathListener
{
	void onDeath(Creature player);
}