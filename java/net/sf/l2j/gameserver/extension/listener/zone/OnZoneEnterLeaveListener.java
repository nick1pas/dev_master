package net.sf.l2j.gameserver.extension.listener.zone;

import net.sf.l2j.gameserver.model.actor.Creature;
import net.sf.l2j.gameserver.model.zone.L2ZoneType;

public interface OnZoneEnterLeaveListener
{
	void onEnter(L2ZoneType zone, Creature creature);
	
	void onExit(L2ZoneType zone, Creature creature);
}