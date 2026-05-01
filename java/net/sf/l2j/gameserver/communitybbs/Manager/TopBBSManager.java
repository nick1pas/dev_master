package net.sf.l2j.gameserver.communitybbs.Manager;

import java.util.Calendar;
import java.util.StringTokenizer;

import net.sf.l2j.commons.lang.StringUtil;
import net.sf.l2j.commons.math.MathUtil;
import net.sf.l2j.gameserver.cache.HtmCache;
import net.sf.l2j.gameserver.model.L2World;
import net.sf.l2j.gameserver.model.actor.Player;
import net.sf.l2j.gameserver.taskmanager.GameTimeController;

public class TopBBSManager extends BaseBBSManager
{
	protected TopBBSManager()
	{
	}
	
	public static TopBBSManager getInstance()
	{
		return SingletonHolder._instance;
	}
	
	@Override
	public void parseCmd(String command, Player activeChar)
	{
		if (command.equals("_bbshome"))
		{
			String content = HtmCache.getInstance().getHtm(CB_PATH + "interlude/index.htm");
			content = applyPlayerStats(content, activeChar);
			separateAndSend(content, activeChar);
		}
		
		else if (command.startsWith("_bbshome;"))
		{
			StringTokenizer st = new StringTokenizer(command, ";");
			st.nextToken();
			
			loadStaticHtm(st.nextToken(), activeChar);
		}
		else
			super.parseCmd(command, activeChar);
	}
	
	@Override
	protected void loadStaticHtm(String path, Player activeChar)
	{
		String content = HtmCache.getInstance().getHtm(CB_PATH + getFolder() + path);
		
		if (content == null)
		{
			separateAndSend("<html><body>File not found: " + path + "</body></html>", activeChar);
			return;
		}
		
		content = applyPlayerStats(content, activeChar);
		
		separateAndSend(content, activeChar);
	}
	
	public String applyPlayerStats(String content, Player activeChar)
	{
		if (content == null || activeChar == null)
			return content;
		
		content = content.replace("%playerName%", activeChar.getName());
		
		content = content.replace("%CP%", (int) activeChar.getStatus().getCurrentCp() + " / " + StringUtil.formatNumber(activeChar.getStat().getMaxCp()));
		content = content.replace("%HP%", (int) activeChar.getStatus().getCurrentHp() + " / " + StringUtil.formatNumber(activeChar.getStat().getMaxHp()));
		content = content.replace("%MP%", (int) activeChar.getStatus().getCurrentMp() + " / " + StringUtil.formatNumber(activeChar.getStat().getMaxMp()));
		
		content = content.replace("%LV%", String.valueOf(activeChar.getStat().getLevel()));
		
		content = content.replace("%clanName%", activeChar.getClan() != null ? activeChar.getClan().getName() : "No");
		content = content.replace("%clanReputation%", activeChar.getClan() != null ? String.valueOf(activeChar.getClan().getReputationScore()) : "0");
		
		content = content.replace("%classeName%", activeChar.getTemplate().getClassName());
		content = content.replace("%Adena%", StringUtil.formatNumber(activeChar.getInventory().getAdena()));
		
		content = content.replace("%pvpPk%", activeChar.getPvpKills() + " / " + activeChar.getPkKills());
		content = content.replace("%fame%", activeChar.getKarma() > 0 ? String.valueOf(activeChar.getKarma()) : "0");
		
		content = content.replace("%playersConected%", String.valueOf(L2World.getInstance().getPlayers().size()));
		
		int t = GameTimeController.getInstance().getGameTime();
		int h = t / 60;
		int m = t % 60;
		
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.HOUR_OF_DAY, h);
		cal.set(Calendar.MINUTE, m);
		
		content = content.replace("%time%", MathUtil.formatDate(cal.getTime(), "h:mm a"));
		
		return content;
	}
	
	@Override
	protected String getFolder()
	{
		return "interlude/";
	}
	
	private static class SingletonHolder
	{
		protected static final TopBBSManager _instance = new TopBBSManager();
	}
}