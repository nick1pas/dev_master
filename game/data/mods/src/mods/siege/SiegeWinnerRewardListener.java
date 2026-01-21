package mods.siege;

import net.sf.l2j.gameserver.extension.listener.siege.OnSiegeClanWinnerListener;
import net.sf.l2j.gameserver.model.L2Clan;
import net.sf.l2j.gameserver.model.L2ClanMember;
import net.sf.l2j.gameserver.model.actor.Player;
import net.sf.l2j.gameserver.model.entity.Siege;

public class SiegeWinnerRewardListener implements OnSiegeClanWinnerListener
{
	@Override
	public void onSiegeClanWinner(Siege siege, L2Clan winnerClan)
	{
		for (L2ClanMember member : winnerClan.getMembers())
		{
			if (member == null)
				continue;
			
			Player player = member.getPlayerInstance();
			if (player == null)
				continue;
			
			if (member.getClan().getLeader() != null)
			{
				player.sendMessage("Parabens, lider! Seu clan venceu a siege de " + siege.getCastle().getName() + "!");
			}
			else
			{
				player.sendMessage("Seu clan venceu a siege de " + siege.getCastle().getName() + "!");
			}
		}
	}
}
