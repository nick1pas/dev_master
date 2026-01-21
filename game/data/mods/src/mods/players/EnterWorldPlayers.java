package mods.players;

import net.sf.l2j.gameserver.extension.listener.actor.player.OnPlayerEnterListener;
import net.sf.l2j.gameserver.model.actor.Player;

public class EnterWorldPlayers implements OnPlayerEnterListener
{
	@Override
	public void onPlayerEnter(Player player)
	{
		player.sendMessage("You use L2jDev Project Interlude :)");
	}
	
}