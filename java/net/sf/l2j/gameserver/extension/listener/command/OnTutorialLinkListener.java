package net.sf.l2j.gameserver.extension.listener.command;

import net.sf.l2j.gameserver.model.actor.Player;

public interface OnTutorialLinkListener
{
	boolean onBypass(Player player, String command);
}