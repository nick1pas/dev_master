package net.sf.l2j.gameserver.extension.listener.actor.door;

import net.sf.l2j.gameserver.model.actor.instance.L2DoorInstance;

public interface OnOpenCloseListener
{
	void onOpen(L2DoorInstance door);
	
	void onClose(L2DoorInstance door);
}