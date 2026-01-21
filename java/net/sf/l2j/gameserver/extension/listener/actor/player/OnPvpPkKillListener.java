package net.sf.l2j.gameserver.extension.listener.actor.player;

import net.sf.l2j.gameserver.model.actor.Player;

public interface OnPvpPkKillListener
{
	void onPvpPkKill(Player killer, Player victim, boolean isPvp);
}