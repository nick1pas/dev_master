package net.sf.l2j.gameserver.model.actor.instance;

import java.util.Calendar;
import java.util.Map;
import java.util.StringTokenizer;

import net.sf.l2j.dungeon.DungeonConfig;
import net.sf.l2j.dungeon.DungeonManager;
import net.sf.l2j.gameserver.model.actor.Player;
import net.sf.l2j.gameserver.model.actor.template.NpcTemplate;
import net.sf.l2j.gameserver.network.serverpackets.NpcHtmlMessage;

/**
 * @author Anarchy
 * @Modificador -= TioPatinhas =- Modificado para suportar VIP e entrada única por horário.
 */
public class L2DungeonManagerInstance extends L2NpcInstance
{
	public L2DungeonManagerInstance(int objectId, NpcTemplate template)
	{
		super(objectId, template);
	}
	
	@Override
	public void onBypassFeedback(Player player, String command)
	{
		if (command.startsWith("dungeon"))
		{
			if (DungeonManager.getInstance().isInDungeon(player) || player.isInOlympiadMode())
			{
				player.sendMessage("You are currently unable to enter a Dungeon. Please try again later.");
				return;
			}
			
			int dungeonId = Integer.parseInt(command.substring(8));
			
			if (!player.isVip())
			{
				player.sendMessage(DungeonConfig.VIP_REQUIRED_MESSAGE);
				return;
			}
			
			if (!DungeonConfig.isDungeonTime())
			{
				player.sendMessage(String.format(DungeonConfig.DUNGEON_TIME_MESSAGE, DungeonConfig.getFormattedHours(), DungeonConfig.DUNGEON_DURATION_MINUTES));
				return;
			}
			
			if (dungeonId == 1 || dungeonId == 2)
			{
				DungeonManager.getInstance().enterDungeon(dungeonId, player);
			}
		}
		else
		{
			super.onBypassFeedback(player, command);
		}
	}
	
	public static String getPlayerStatus(Player player, int dungeonId)
	{
		if (player == null)
			return "Error";
		
		String s = "You can enter";
		String key = String.valueOf(player.getObjectId());
		
		Map<String, Long[]> playerData = DungeonManager.getInstance().getPlayerData();
		if (playerData == null || !playerData.containsKey(key))
			return s;
		
		Long[] data = playerData.get(key);
		if (data == null || data.length == 0 || data[0] == null || data[0] == 0L)
			return s;
		
		String currentEventTime = getCurrentEventTime();
		String lastTime = getLastEnteredTime(key);
		
		if (currentEventTime != null && currentEventTime.equals(lastTime))
		{
			s = "Already entered at " + lastTime;
		}
		
		return s;
	}
	
	@Override
	public void showChatWindow(Player player, int val)
	{
		NpcHtmlMessage htm = new NpcHtmlMessage(0);
		htm.setFile("data/html/mods/dungeon/" + getNpcId() + (val == 0 ? "" : "-" + val) + ".htm");
		
		htm.replace("%objectId%", String.valueOf(getObjectId()));
		
		String isVip = player.isVip() ? "<font color=\"00FF00\">Yes</font>" : "<font color=\"FF0000\">No (VIP Required)</font>";
		htm.replace("%isVip%", isVip);
		
		String isDungeonTime = DungeonConfig.isDungeonTime() ? "<font color=\"00FF00\">Now Open! (" + getCurrentTime() + ")</font>" : "<font color=\"FF0000\">Closed - Next at: " + DungeonConfig.getFormattedHours() + " (for " + DungeonConfig.DUNGEON_DURATION_MINUTES + " min)</font>";
		htm.replace("%isDungeonTime%", isDungeonTime);
		
		String button1 = getEnterButton(player, 1);
		String button2 = getEnterButton(player, 2);
		htm.replace("%button1%", button1);
		htm.replace("%button2%", button2);
		
		String[] s = htm.getHtml().split("%");
		for (int i = 0; i < s.length; i++)
		{
			if (i % 2 > 0 && s[i].contains("dung "))
			{
				StringTokenizer st = new StringTokenizer(s[i]);
				st.nextToken();
				htm.replace("%" + s[i] + "%", getPlayerStatus(player, Integer.parseInt(st.nextToken())));
			}
		}
		
		player.sendPacket(htm);
	}
	
	private String getEnterButton(Player player, int dungeonId)
	{
		if (!player.isVip())
		{
			return "<font color=\"FF0000\">[VIP Required]</font>";
		}
		
		if (!DungeonConfig.isDungeonTime())
		{
			return "<font color=\"FF0000\">[Closed]</font>";
		}
		
		String action = "bypass -h npc_" + getObjectId() + "_dungeon " + dungeonId;
		String label = (dungeonId == 1) ? "Enter Alone" : "Enter with Party";
		
		return "<button value=\"" + label + "\" action=\"" + action + "\" width=134 height=21 back=\"L2UI_ch3.BigButton3_over\" fore=\"L2UI_ch3.BigButton3\">";
	}
	
	private static String getCurrentTime()
	{
		Calendar cal = Calendar.getInstance();
		return String.format("%02d:%02d", cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE));
	}
	
	private static String getCurrentEventTime()
	{
		int currentHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
		int currentMinute = Calendar.getInstance().get(Calendar.MINUTE);
		int currentTotalMinutes = currentHour * 60 + currentMinute;
		
		for (String interval : DungeonConfig.DUNGEON_EVENT_INTERVAL)
		{
			if (!interval.contains(":"))
				continue;
			
			String[] parts = interval.split(":");
			int hour = Integer.parseInt(parts[0]);
			int minute = Integer.parseInt(parts[1]);
			int scheduledTotalMinutes = hour * 60 + minute;
			
			if (currentTotalMinutes >= scheduledTotalMinutes && currentTotalMinutes < scheduledTotalMinutes + DungeonConfig.DUNGEON_DURATION_MINUTES)
			{
				return interval;
			}
		}
		return null;
	}
	
	private static String getLastEnteredTime(String key)
	{
		Map<String, Long[]> playerData = DungeonManager.getInstance().getPlayerData();
		if (!playerData.containsKey(key))
			return null;
		
		Long hash = playerData.get(key)[0];
		if (hash == null || hash == 0L)
			return null;
		
		for (String interval : DungeonConfig.DUNGEON_EVENT_INTERVAL)
		{
			if (interval.hashCode() == hash)
				return interval;
		}
		return null;
	}
	
	@Override
	public String getHtmlPath(int npcId, int val)
	{
		String filename = (val == 0) ? String.valueOf(npcId) : (npcId + "-" + val);
		return "data/html/mods/dungeon/" + filename + ".htm";
	}
}