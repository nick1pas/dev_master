package mods.communityBoard;

import java.util.StringTokenizer;

import net.sf.l2j.gameserver.datatables.MultisellData;
import net.sf.l2j.gameserver.extension.listener.command.OnBypassCommandListener;
import net.sf.l2j.gameserver.model.actor.Player;

public class MerchantBBSManager implements OnBypassCommandListener
{
	
	@Override
	public boolean onBypass(Player player, String command)
	{
		if (command.startsWith("_bbsMultisell"))
		{
			StringTokenizer st = new StringTokenizer(command, " ");
			st.nextToken();
			if (st.countTokens() < 1)
				return true;
			
			MultisellData.getInstance().separateAndSend(st.nextToken(), player, null, false);
		}
		return false;
	}
	
}
