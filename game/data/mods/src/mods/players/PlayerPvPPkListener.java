package mods.players;

import net.sf.l2j.gameserver.extension.listener.actor.player.OnPvpPkKillListener;
import net.sf.l2j.gameserver.model.actor.Player;

public class PlayerPvPPkListener implements OnPvpPkKillListener
{

	@Override
	public void onPvpPkKill(Player player, Player target, boolean pvpflag)
	{
		if(pvpflag)
		{
			player.sendMessage("Voce ganhou +1 PvP Point.");
		}
		else
		{
			player.sendMessage("Voce ganhou +1 Pk Point.");
		}
		
	}
	
}
