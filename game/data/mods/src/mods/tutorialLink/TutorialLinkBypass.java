package mods.tutorialLink;

import net.sf.l2j.gameserver.extension.listener.command.OnTutorialLinkListener;
import net.sf.l2j.gameserver.model.actor.Player;

public class TutorialLinkBypass implements OnTutorialLinkListener
{

	@Override
	public boolean onBypass(Player player, String command)
	{
		if(command.startsWith("custom"))
		{
			player.sendMessage("Command custom Work :)");
		}
		
		return false;
	}
	
}
