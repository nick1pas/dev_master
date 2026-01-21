package net.sf.l2j.gameserver.network.clientpackets;

import net.sf.l2j.Config;
import net.sf.l2j.gameserver.LoginServerThread;
import net.sf.l2j.gameserver.LoginServerThread.SessionKey;
import net.sf.l2j.gameserver.network.L2GameClient;
import net.sf.l2j.hwid.Hwid;

/**
 * loginName + keys must match what the loginserver used.
 */
/*public final class AuthLogin extends L2GameClientPacket
{
	private String _loginName;
	private int _playKey1;
	private int _playKey2;
	private int _loginKey1;
	private int _loginKey2;
	
	@Override
	protected void readImpl()
	{
		_loginName = readS().toLowerCase();
		_playKey2 = readD();
		_playKey1 = readD();
		_loginKey1 = readD();
		_loginKey2 = readD();
	}
	
	@Override
	protected void runImpl()
	{
		/*if (getClient().getAccountName() == null)
		{
			if (LoginServerThread.getInstance().addGameServerLogin(_loginName, getClient()))
			{
				getClient().setAccountName(_loginName);
				LoginServerThread.getInstance().addWaitingClientAndSendRequest(_loginName, getClient(), new SessionKey(_loginKey1, _loginKey2, _playKey1, _playKey2));
			}
			else
				getClient().close((L2GameServerPacket) null);
		}
		
		if (getClient().getAccountName() == null)
		{
			if (LoginServerThread.getInstance().addGameServerLogin(_loginName, getClient()))
			{
				getClient().setAccountName(_loginName);
				LoginServerThread.getInstance().addWaitingClientAndSendRequest(_loginName, getClient(), new SessionKey(_loginKey1, _loginKey2, _playKey1, _playKey2));
			}
			else
				getClient().close((L2GameServerPacket) null);
		}
	}
}*/
public final class AuthLogin extends L2GameClientPacket
{
	private String _loginName;
	private int _playKey1;
	private int _playKey2;
	private int _loginKey1;
	private int _loginKey2;
	private final byte[] _data = new byte[48];
	
	@Override
	protected void readImpl()
	{
		_loginName = readS().toLowerCase();
		_playKey2 = readD();
		_playKey1 = readD();
		_loginKey1 = readD();
		_loginKey2 = readD();
	}
	
	@Override
	protected void runImpl()
	{
		if (Hwid.isProtectionOn())
		{
			if (!Hwid.doAuthLogin(getClient(), _data, _loginName))
				return;
		}
					
		final SessionKey key = new SessionKey(_loginKey1, _loginKey2, _playKey1, _playKey2);
		
		if (Config.DEBUG)
			_log.info("DEBUG " + getType() + ": user: " + _loginName + " key:" + key);
		
		final L2GameClient client = getClient();
		
		// avoid potential exploits
		if (client.getAccountName() == null)
		{
			// Preventing duplicate login in case client login server socket was
			// disconnected or this packet was not sent yet
			if (LoginServerThread.getInstance().addGameServerLogin(_loginName, client))
			{
				client.setAccountName(_loginName);
				LoginServerThread.getInstance().addWaitingClientAndSendRequest(_loginName, client, key);
			}
			else
			{
				client.closeNow();
			}
		}
	}
}