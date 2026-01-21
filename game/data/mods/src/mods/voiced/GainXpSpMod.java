package mods.voiced;

import net.sf.l2j.Config;
import net.sf.l2j.gameserver.extension.L2JMod;
import net.sf.l2j.gameserver.handler.IVoicedCommandHandler;
import net.sf.l2j.gameserver.handler.VoicedCommandHandler;
import net.sf.l2j.gameserver.model.actor.Player;

public class GainXpSpMod implements L2JMod, IVoicedCommandHandler
{
	@Override
	public void onLoad()
	{
		if (Config.ENABLE_COMMAND_XPON_OFF)
		{
			VoicedCommandHandler.getInstance().registerHandler(this);
		}
	}
	
	private static final String[] COMMANDS =
	{
		"xpon",
		"xpoff"
	};
	
	@Override
	public boolean useVoicedCommand(String command, Player player, String params)
	{
		if (player == null)
			return false;
		
		if (command.equalsIgnoreCase("xpon"))
		{
			player.setGainXpSpEnable(true);
			player.sendMessage(Config.MESSAGE_XPON);
			return true;
		}
		
		if (command.equalsIgnoreCase("xpoff"))
		{
			player.setGainXpSpEnable(false);
			player.sendMessage(Config.MESSAGE_XPOFF);
			return true;
		}
		
		return false;
	}
	
	@Override
	public String[] getVoicedCommandList()
	{
		return COMMANDS;
	}

	@Override
	public void onUnload()
	{
			
	}
}
