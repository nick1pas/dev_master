package net.sf.l2j.gameserver.extension.listener.siege;

import net.sf.l2j.gameserver.model.L2Clan;
import net.sf.l2j.gameserver.model.entity.Siege;

public interface OnSiegeClanWinnerListener
{
	void onSiegeClanWinner(Siege siege, L2Clan winnerClan);
	
}