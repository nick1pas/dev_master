package net.sf.l2j.gameserver.extension.listener.actor.player;

import net.sf.l2j.gameserver.model.actor.Player;

public interface OnTeleportListener
{
	void onTeleport(Player player, int x, int y, int z);
}