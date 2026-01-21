package net.sf.l2j.gameserver.instancemanager;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import net.sf.l2j.gameserver.model.actor.Player;

public class PrivateMessageManager
{
	public final class PrivateMessage
	{
		private final int senderId;
		private final int receiverId;
		private final String senderName;
		private final String text;
		private final long time;
		
		private boolean seen;
		private boolean answered;
		
		public PrivateMessage(int senderId, int receiverId, String senderName, String text)
		{
			this.senderId = senderId;
			this.receiverId = receiverId;
			this.senderName = senderName;
			this.text = text;
			this.time = System.currentTimeMillis();
		}
		
		public int getSenderId()
		{
			return senderId;
		}
		
		public int getReceiverId()
		{
			return receiverId;
		}
		
		public String getSenderName()
		{
			return senderName;
		}
		
		public String getText()
		{
			return text;
		}
		
		public long getTime()
		{
			return time;
		}
		
		public boolean isSeen()
		{
			return seen;
		}
		
		public void markSeen()
		{
			seen = true;
		}
		
		public boolean isAnswered()
		{
			return answered;
		}
		
		public void markAnswered()
		{
			answered = true;
		}
	}
	
	private final Map<Integer, List<PrivateMessage>> _messages = new ConcurrentHashMap<>();
	
	public static PrivateMessageManager getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	public void onTell(Player sender, Player receiver, String text)
	{
		PrivateMessage msg = new PrivateMessage(sender.getObjectId(), receiver.getObjectId(), sender.getName(), text);
		
		_messages.computeIfAbsent(receiver.getObjectId(), k -> new CopyOnWriteArrayList<>()).add(msg);
		
		receiver.onPrivateMessage(msg);
	}
	
	public List<PrivateMessage> getMessages(int playerId)
	{
		return _messages.getOrDefault(playerId, List.of());
	}
	
	public void markSeen(int playerId)
	{
		getMessages(playerId).forEach(m -> m.seen = true);
	}
	
	private static class SingletonHolder
	{
		private static final PrivateMessageManager INSTANCE = new PrivateMessageManager();
	}
	
}
