package mods.players;

import net.sf.l2j.gameserver.extension.listener.actor.player.OnLevelUpListener;
import net.sf.l2j.gameserver.model.actor.Player;

public class LevelUpListener implements OnLevelUpListener
{
	@Override
	public void onLevelUp(Player player)
	{
		player.sendMessage("Parabens! Voce subiu o level agora voce e Lv: " + player.getLevel());
	}
}