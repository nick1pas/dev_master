package mods.players;

import net.sf.l2j.gameserver.extension.listener.actor.player.OnSetLevelListener;
import net.sf.l2j.gameserver.model.actor.Player;

public class SetLevelListener implements OnSetLevelListener
{
	@Override
	public void onSetLevel(Player player, int val)
	{
		player.sendMessage("Seu level foi ajustado para: " + val);
	}
}