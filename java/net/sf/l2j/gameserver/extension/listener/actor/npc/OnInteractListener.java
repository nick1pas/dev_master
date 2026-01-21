package net.sf.l2j.gameserver.extension.listener.actor.npc;

import net.sf.l2j.gameserver.model.actor.L2Npc;
import net.sf.l2j.gameserver.model.actor.Player;

public interface OnInteractListener
{
    boolean onInteract(L2Npc npc, Player player);
}