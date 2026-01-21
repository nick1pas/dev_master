package mods.game;

import java.util.logging.Logger;

import net.sf.l2j.gameserver.GameServer;
import net.sf.l2j.gameserver.extension.listener.game.OnCharacterDeleteListener;

public class CharacterDeleteCleanupListener implements OnCharacterDeleteListener
{
	public static Logger _log = Logger.getLogger(GameServer.class.getName());
	
	@Override
	public void onCharacterDelete(int objectId)
	{
		
	}
}