package net.sf.l2j.gameserver.handler.voicedcommandhandlers;

import java.util.StringTokenizer;

import net.sf.l2j.Config;
import net.sf.l2j.dresmee.DressMe;
import net.sf.l2j.gameserver.ThreadPool;
import net.sf.l2j.gameserver.handler.IVoicedCommandHandler;
import net.sf.l2j.gameserver.instancemanager.custom.DressMeData;
import net.sf.l2j.gameserver.model.actor.Player;
import net.sf.l2j.gameserver.model.zone.ZoneId;
import net.sf.l2j.gameserver.network.serverpackets.NpcHtmlMessage;

public class SkinsVIP implements IVoicedCommandHandler
{
	private static final String[] VOICED_COMMANDS = { "skin", "trySkin" };

	@Override
	public boolean useVoicedCommand(String command, Player activeChar, String target)
	{
		if (activeChar == null)
			return false;

		switch (command.split(" ")[0]) // pega apenas o nome do comando sem os parâmetros
		{
			case "skin":
				if (Config.ENABLE_COMAND_SKIN)
					showTrySkinHtml(activeChar);
				break;

			case "trySkin":
				handleTrySkinCommand(command, activeChar);
				break;
		}

		return true;
	}

	private static void showTrySkinHtml(Player activeChar)
	{
		NpcHtmlMessage html = new NpcHtmlMessage(0);
		html.setFile("data/html/mods/menu/trySkin.htm");
		activeChar.sendPacket(html);
	}

	private static void handleTrySkinCommand(String command, Player activeChar)
	{
		if (!activeChar.isInsideZone(ZoneId.TOWN))
		{
			activeChar.sendMessage("This command can only be used within a city.");
			return;
		}

		if (activeChar.getDress() != null)
		{
			activeChar.sendMessage("Wait, you are already trying a skin.");
			return;
		}

		StringTokenizer st = new StringTokenizer(command);
		st.nextToken(); // skip "trySkin"
		if (!st.hasMoreTokens())
		{
			activeChar.sendMessage("Skin ID missing.");
			return;
		}

		int skinId;
		try
		{
			skinId = Integer.parseInt(st.nextToken());
		}
		catch (NumberFormatException e)
		{
			activeChar.sendMessage("Invalid skin ID.");
			return;
		}

		final DressMe dress = DressMeData.getInstance().getItemId(skinId);
		if (dress == null)
		{
			activeChar.sendMessage("Invalid skin.");
			return;
		}

		activeChar.setDress(dress);
		activeChar.broadcastUserInfo();

		// Remove skin após 3 segundos
		ThreadPool.schedule(() ->
		{
			activeChar.setDress(DressMeData.getInstance().getItemId(0));
			activeChar.broadcastUserInfo();
		}, 3000L);
	}

	@Override
	public String[] getVoicedCommandList()
	{
		return VOICED_COMMANDS;
	}
}
