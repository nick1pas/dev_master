package mods.players;

import net.sf.l2j.gameserver.extension.listener.actor.player.OnSetClassListener;
import net.sf.l2j.gameserver.model.actor.Player;

public class SetClassListener implements OnSetClassListener
{
	@Override
	public void onSetClass(Player player, int id)
	{
		player.sendMessage("Classe alterada para ID: " + id);
	}
}