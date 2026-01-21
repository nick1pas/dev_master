package mods.players;

import net.sf.l2j.gameserver.extension.listener.OnReviveListener;
import net.sf.l2j.gameserver.model.actor.Creature;
import net.sf.l2j.gameserver.model.actor.Player;

public class ReviveMessageListener implements OnReviveListener
{
	@Override
	public void onRevive(Creature creature)
	{
		if (!(creature instanceof Player))
			return;
		
		Player player = (Player) creature;
		player.sendMessage("Voce reviveu com sucesso!");
	}
}