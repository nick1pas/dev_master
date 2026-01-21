package net.sf.l2j.gameserver.extension.listener.siege;

import net.sf.l2j.gameserver.model.entity.Siege;

public interface OnSiegeListener
{
	void onSiegeStart(Siege siege);
	
	void onSiegeEnd(Siege siege);
}