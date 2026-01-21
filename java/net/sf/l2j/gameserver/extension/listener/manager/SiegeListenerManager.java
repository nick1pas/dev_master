package net.sf.l2j.gameserver.extension.listener.manager;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import net.sf.l2j.gameserver.extension.listener.siege.OnSiegeClanWinnerListener;
import net.sf.l2j.gameserver.extension.listener.siege.OnSiegeListener;
import net.sf.l2j.gameserver.extension.listener.siege.OnSiegeRegisterAttackerListener;
import net.sf.l2j.gameserver.extension.listener.siege.OnSiegeRegisterDefenderListener;
import net.sf.l2j.gameserver.model.L2Clan;
import net.sf.l2j.gameserver.model.actor.Player;
import net.sf.l2j.gameserver.model.entity.Siege;

public class SiegeListenerManager
{
	private static final SiegeListenerManager INSTANCE = new SiegeListenerManager();
	private final List<OnSiegeListener> listeners = new CopyOnWriteArrayList<>();
	private final List<OnSiegeRegisterAttackerListener> attackerListeners = new CopyOnWriteArrayList<>();
	private final List<OnSiegeRegisterDefenderListener> defenderListeners = new CopyOnWriteArrayList<>();
	private final List<OnSiegeClanWinnerListener> winnerListeners = new CopyOnWriteArrayList<>();

	
	private SiegeListenerManager()
	{
	}
	
	public static SiegeListenerManager getInstance()
	{
		return INSTANCE;
	}
	
	public void registerListener(OnSiegeListener listener)
	{
		listeners.add(listener);
	}
	
	public void unregisterListener(OnSiegeListener listener)
	{
		listeners.remove(listener);
	}
	
	public void notifySiegeStart(Siege siege)
	{
		for (OnSiegeListener l : listeners)
		{
			try
			{
				l.onSiegeStart(siege);
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
	}
	
	public void notifySiegeEnd(Siege siege)
	{
		for (OnSiegeListener l : listeners)
		{
			try
			{
				l.onSiegeEnd(siege);
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
	}
	
	// Attacker
	public void registerAttackerListener(OnSiegeRegisterAttackerListener listener)
	{
		attackerListeners.add(listener);
	}
	
	public void unregisterAttackerListener(OnSiegeRegisterAttackerListener listener)
	{
		attackerListeners.remove(listener);
	}
	
	// Defender
	public void registerDefenderListener(OnSiegeRegisterDefenderListener listener)
	{
		defenderListeners.add(listener);
	}
	
	public void unregisterDefenderListener(OnSiegeRegisterDefenderListener listener)
	{
		defenderListeners.remove(listener);
	}
	
	public void notifyRegisterAttacker(Siege siege, Player player)
	{
		for (OnSiegeRegisterAttackerListener l : attackerListeners)
		{
			try
			{
				l.onRegisterAttacker(siege, player);
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
	}
	
	public void notifyRegisterDefender(Siege siege, Player player)
	{
		for (OnSiegeRegisterDefenderListener l : defenderListeners)
		{
			try
			{
				l.onRegisterDefender(siege, player);
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
	}
	
	public void registerWinnerListener(OnSiegeClanWinnerListener listener)
	{
		winnerListeners.add(listener);
	}

	public void unregisterWinnerListener(OnSiegeClanWinnerListener listener)
	{
		winnerListeners.remove(listener);
	}

	public void notifyWinnerClan(Siege siege, L2Clan winnerClan)
	{
		for (OnSiegeClanWinnerListener l : winnerListeners)
		{
			try
			{
				l.onSiegeClanWinner(siege, winnerClan);
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
	}

}