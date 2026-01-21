package mods.players;

import net.sf.l2j.gameserver.extension.listener.OnMoveListener;
import net.sf.l2j.gameserver.model.Location;
import net.sf.l2j.gameserver.model.actor.Creature;
import net.sf.l2j.gameserver.model.actor.Player;

public class MovementTracker implements OnMoveListener
{
	@Override
	public void onMove(Creature creature, Location destination)
	{
		if (!(creature instanceof Player))
			return;
		
	}
}