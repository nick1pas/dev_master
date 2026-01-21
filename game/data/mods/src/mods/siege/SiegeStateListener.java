package mods.siege;

import net.sf.l2j.gameserver.extension.listener.siege.OnSiegeListener;
import net.sf.l2j.gameserver.model.entity.Siege;
import net.sf.l2j.gameserver.util.Broadcast;

public class SiegeStateListener implements OnSiegeListener
{
	@Override
	public void onSiegeStart(Siege siege)
	{
		Broadcast.announceToOnlinePlayers("[Siege] Siege iniciada em: " + siege.getCastle().getName(), true);
	}

	@Override
	public void onSiegeEnd(Siege siege)
	{
		Broadcast.announceToOnlinePlayers("[Siege] Siege finalizada em: " + siege.getCastle().getName(), true);
	}
}
