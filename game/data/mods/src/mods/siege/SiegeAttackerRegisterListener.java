package mods.siege;

import net.sf.l2j.gameserver.extension.listener.siege.OnSiegeRegisterAttackerListener;
import net.sf.l2j.gameserver.model.L2Clan;
import net.sf.l2j.gameserver.model.L2ClanMember;
import net.sf.l2j.gameserver.model.actor.Player;
import net.sf.l2j.gameserver.model.entity.Siege;

public class SiegeAttackerRegisterListener implements OnSiegeRegisterAttackerListener
{
	@Override
	public void onRegisterAttacker(Siege siege, Player player)
	{
		L2Clan clan = player.getClan();
		if (clan == null)
			return;
		
		for (L2ClanMember member : clan.getMembers())
		{
			if (member == null)
				continue;
			
			Player memberPlayer = member.getPlayerInstance();
			if (memberPlayer == null)
				continue;
			
			if (member.getClan().getLeader() != null)
			{
				memberPlayer.sendMessage("Voce registrou seu clan como ATACANTE na siege de " + siege.getCastle().getName());
			}
			else
			{
				memberPlayer.sendMessage("Seu clan foi registrado como ATACANTE na siege de " + siege.getCastle().getName());
			}
		}
	}
}
