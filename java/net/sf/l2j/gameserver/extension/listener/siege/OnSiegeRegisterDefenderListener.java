package net.sf.l2j.gameserver.extension.listener.siege;

import net.sf.l2j.gameserver.model.actor.Player;
import net.sf.l2j.gameserver.model.entity.Siege;

public interface OnSiegeRegisterDefenderListener
{
    void onRegisterDefender(Siege siege, Player player);
}