package net.sf.l2j.dungeon;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.sf.l2j.gameserver.ConnectionPool;
import net.sf.l2j.gameserver.ThreadPool;
import net.sf.l2j.gameserver.model.Location;
import net.sf.l2j.gameserver.model.actor.Player;
import net.sf.l2j.gameserver.xmlfactory.XMLDocumentFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

/**
 * @author Anarchy
 * @Modificador -= TioPatinhas =- Atualizado para suporte completo a VIP, horários e cooldown por horário.
 */
public class DungeonManager
{
	private static Logger log = Logger.getLogger(DungeonManager.class.getName());
	
	private Map<Integer, DungeonTemplate> templates;
	private List<Dungeon> running;
	private List<Integer> dungeonParticipants;
	private boolean reloading = false;
	private Map<String, Long[]> dungeonPlayerData;
	
	protected DungeonManager()
	{
		templates = new ConcurrentHashMap<>();
		running = new CopyOnWriteArrayList<>();
		dungeonParticipants = new ArrayList<>();
		dungeonPlayerData = new ConcurrentHashMap<>();
		
		load();
		ThreadPool.scheduleAtFixedRate(() -> updateDatabase(), 1000 * 60 * 30, 1000 * 60 * 60);
	}
	
	private void updateDatabase()
	{
		try (Connection con = ConnectionPool.getConnection())
		{
			PreparedStatement stm = con.prepareStatement("DELETE FROM dungeon");
			stm.execute();
			
			for (String key : dungeonPlayerData.keySet())
			{
				Long[] data = dungeonPlayerData.get(key);
				if (data == null)
					continue;
				
				for (int i = 0; i < data.length; i++)
				{
					if (data[i] == null)
						continue;
					
					PreparedStatement stm2 = con.prepareStatement("INSERT INTO dungeon VALUES (?,?,?)");
					stm2.setInt(1, i);
					stm2.setString(2, key);
					stm2.setLong(3, data[i]);
					stm2.execute();
					stm2.close();
				}
			}
			
			stm.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public boolean reload()
	{
		if (!running.isEmpty())
		{
			reloading = true;
			return false;
		}
		
		templates.clear();
		load();
		return true;
	}
	
	private void load()
	{
		try
		{
			File f = new File("./data/xml/dungeons.xml");
			Document doc = XMLDocumentFactory.getInstance().loadDocument(f);
			
			Node n = doc.getFirstChild();
			for (Node d = n.getFirstChild(); d != null; d = d.getNextSibling())
			{
				if (d.getNodeName().equals("dungeon"))
				{
					int id = Integer.parseInt(d.getAttributes().getNamedItem("id").getNodeValue());
					String name = d.getAttributes().getNamedItem("name").getNodeValue();
					int players = Integer.parseInt(d.getAttributes().getNamedItem("players").getNodeValue());
					Map<Integer, Integer> rewards = new HashMap<>();
					String rewardHtm = d.getAttributes().getNamedItem("rewardHtm").getNodeValue();
					Map<Integer, DungeonStage> stages = new HashMap<>();
					
					String rewards_data = d.getAttributes().getNamedItem("rewards").getNodeValue();
					if (!rewards_data.isEmpty())
					{
						String[] rewards_data_split = rewards_data.split(";");
						for (String reward : rewards_data_split)
						{
							String[] reward_split = reward.split(",");
							rewards.put(Integer.parseInt(reward_split[0]), Integer.parseInt(reward_split[1]));
						}
					}
					
					for (Node cd = d.getFirstChild(); cd != null; cd = cd.getNextSibling())
					{
						NamedNodeMap attrs = cd.getAttributes();
						
						if (cd.getNodeName().equals("stage"))
						{
							int order = Integer.parseInt(attrs.getNamedItem("order").getNodeValue());
							String loc_data = attrs.getNamedItem("loc").getNodeValue();
							String[] loc_data_split = loc_data.split(",");
							Location loc = new Location(Integer.parseInt(loc_data_split[0]), Integer.parseInt(loc_data_split[1]), Integer.parseInt(loc_data_split[2]));
							boolean teleport = Boolean.parseBoolean(attrs.getNamedItem("teleport").getNodeValue());
							int minutes = Integer.parseInt(attrs.getNamedItem("minutes").getNodeValue());
							Map<Integer, List<Location>> mobs = new HashMap<>();
							
							for (Node ccd = cd.getFirstChild(); ccd != null; ccd = ccd.getNextSibling())
							{
								NamedNodeMap attrs2 = ccd.getAttributes();
								
								if (ccd.getNodeName().equals("mob"))
								{
									int npcId = Integer.parseInt(attrs2.getNamedItem("npcId").getNodeValue());
									List<Location> locs = new ArrayList<>();
									
									String locs_data = attrs2.getNamedItem("locs").getNodeValue();
									String[] locs_data_split = locs_data.split(";");
									for (String locc : locs_data_split)
									{
										String[] locc_data = locc.split(",");
										locs.add(new Location(Integer.parseInt(locc_data[0]), Integer.parseInt(locc_data[1]), Integer.parseInt(locc_data[2])));
									}
									
									mobs.put(npcId, locs);
								}
							}
							
							stages.put(order, new DungeonStage(order, loc, teleport, minutes, mobs));
						}
					}
					
					templates.put(id, new DungeonTemplate(id, name, players, rewards, rewardHtm, stages));
				}
			}
		}
		catch (Exception e)
		{
			log.log(Level.WARNING, "DungeonManager: Error loading dungeons.xml", e);
			e.printStackTrace();
		}
		
		try (Connection con = ConnectionPool.getConnection())
		{
			PreparedStatement stm = con.prepareStatement("SELECT * FROM dungeon");
			ResultSet rset = stm.executeQuery();
			
			while (rset.next())
			{
				int dungid = rset.getInt("dungid");
				String key = rset.getString("ipaddr");
				long lastjoin = rset.getLong("lastjoin");
				
				if (!dungeonPlayerData.containsKey(key))
				{
					Long[] times = new Long[templates.size() + 1];
					for (int i = 0; i < times.length; i++)
						times[i] = 0L;
					times[dungid] = lastjoin;
					dungeonPlayerData.put(key, times);
				}
				else
				{
					Long[] data = dungeonPlayerData.get(key);
					if (data != null && data.length > dungid)
					{
						data[dungid] = lastjoin;
					}
				}
			}
			
			rset.close();
			stm.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		
		log.info("DungeonManager: Loaded " + templates.size() + " dungeon templates");
	}
	
	public synchronized void removeDungeon(Dungeon dungeon)
	{
		if (dungeon == null)
			return;
		
		running.remove(dungeon);
		
		if (reloading && running.isEmpty())
		{
			reloading = false;
			reload();
		}
	}
	
	public synchronized void enterDungeon(int id, Player player)
	{
		if (player == null || reloading)
		{
			if (player != null)
				player.sendMessage("The Dungeon system is reloading, please try again in a few minutes.");
			return;
		}
		
		DungeonTemplate template = templates.get(id);
		if (template == null)
		{
			player.sendMessage("Invalid dungeon ID.");
			return;
		}
		
		if (template.getPlayers() > 1 && (!player.isInParty() || player.getParty().getMemberCount() != template.getPlayers()))
		{
			player.sendMessage("You need a party of " + template.getPlayers() + " players to enter this Dungeon.");
			return;
		}
		else if (template.getPlayers() == 1 && player.isInParty())
		{
			player.sendMessage("You can only enter this Dungeon alone.");
			return;
		}
		
		String currentEventTime = getCurrentEventTime();
		if (currentEventTime == null)
		{
			player.sendMessage(String.format(DungeonConfig.DUNGEON_TIME_MESSAGE, DungeonConfig.getFormattedHours(), DungeonConfig.DUNGEON_DURATION_MINUTES));
			return;
		}
		
		List<Player> players = new ArrayList<>();
		
		if (player.isInParty())
		{
			for (Player pm : player.getParty().getPartyMembers())
			{
				if (pm == null)
					continue;
				String key = String.valueOf(pm.getObjectId());
				String lastTime = getLastEnteredTime(key);
				
				if (currentEventTime.equals(lastTime))
				{
					player.sendMessage("One of your party members has already entered the dungeon at " + currentEventTime + ".");
					return;
				}
			}
			
			for (Player pm : player.getParty().getPartyMembers())
			{
				if (pm == null)
					continue;
				String key = String.valueOf(pm.getObjectId());
				
				dungeonParticipants.add(pm.getObjectId());
				players.add(pm);
				setLastEnteredTime(key, currentEventTime);
			}
		}
		else
		{
			String key = String.valueOf(player.getObjectId());
			String lastTime = getLastEnteredTime(key);
			
			if (currentEventTime.equals(lastTime))
			{
				player.sendMessage("You have already entered the dungeon at " + currentEventTime + ".");
				return;
			}
			
			dungeonParticipants.add(player.getObjectId());
			players.add(player);
			setLastEnteredTime(key, currentEventTime);
		}
		
		running.add(new Dungeon(template, players));
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
	
	private void setLastEnteredTime(String key, String time)
	{
		if (dungeonPlayerData.containsKey(key))
		{
			dungeonPlayerData.get(key)[0] = (long) time.hashCode();
		}
		else
		{
			Long[] times = new Long[templates.size() + 1];
			for (int i = 0; i < times.length; i++)
				times[i] = 0L;
			times[0] = (long) time.hashCode();
			dungeonPlayerData.put(key, times);
		}
	}
	
	private String getLastEnteredTime(String key)
	{
		if (!dungeonPlayerData.containsKey(key))
			return null;
		
		Long hash = dungeonPlayerData.get(key)[0];
		if (hash == null || hash == 0L)
			return null;
		
		for (String interval : DungeonConfig.DUNGEON_EVENT_INTERVAL)
		{
			if (interval.hashCode() == hash)
				return interval;
		}
		return null;
	}
	
	public boolean isReloading()
	{
		return reloading;
	}
	
	public boolean isInDungeon(Player player)
	{
		if (player == null)
			return false;
		
		for (Dungeon dungeon : running)
		{
			if (dungeon == null)
				continue;
			for (Player p : dungeon.getPlayers())
			{
				if (p == player)
					return true;
			}
		}
		
		return false;
	}
	
	public Map<String, Long[]> getPlayerData()
	{
		return dungeonPlayerData;
	}
	
	public List<Integer> getDungeonParticipants()
	{
		return dungeonParticipants;
	}
	
	public static DungeonManager getInstance()
	{
		return SingletonHolder.instance;
	}
	
	private static class SingletonHolder
	{
		protected static final DungeonManager instance = new DungeonManager();
	}
}