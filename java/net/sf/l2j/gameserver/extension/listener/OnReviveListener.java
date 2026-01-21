package net.sf.l2j.gameserver.extension.listener;

import net.sf.l2j.gameserver.model.actor.Creature;

public interface OnReviveListener
{
	void onRevive(Creature creature);
}