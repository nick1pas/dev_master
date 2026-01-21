package mods.npc;

import net.sf.l2j.gameserver.extension.listener.actor.npc.OnInteractListener;
import net.sf.l2j.gameserver.model.actor.L2Npc;
import net.sf.l2j.gameserver.model.actor.Player;

public class NpcInteract implements OnInteractListener
{
	@Override
	public boolean onInteract(L2Npc npc, Player player)
	{
		return false;
	}
}
