package mods.fakeplayer.interfaces;

import net.sf.l2j.commons.random.Rnd;
import net.sf.l2j.gameserver.model.actor.Player;

import mods.fakeplayer.actor.FakePlayer;

public interface IPartyRange
{
	String VAR_LAST_PARTY_FOLLOW = "fake_last_party_follow";
	long PARTY_FOLLOW_COOLDOWN = 1000L; // 4 segundos
	
	int PARTY_COMFORT_RANGE = 1010;
	int PARTY_MAX_RANGE = 1500;
	
	default void handlePartyCohesion(FakePlayer player)
	{
		if (player.getParty() == null)
			return;
		
		Player leader = player.getParty().getLeader();
		if (leader == null || leader == player)
			return;
		
		long now = System.currentTimeMillis();
		var memos = player.getMemos();
		long lastFollow = memos.getLong(VAR_LAST_PARTY_FOLLOW, 0);
		if (now - lastFollow < PARTY_FOLLOW_COOLDOWN)
			return;
		
		// Distância atual
		double distance = player.getDistanceSq(leader);
		
		if (distance <= PARTY_COMFORT_RANGE)
			return;
		
		if (distance > PARTY_MAX_RANGE)
		{
			player.getAI().startFollow(leader, PARTY_COMFORT_RANGE - 100);
			memos.set(VAR_LAST_PARTY_FOLLOW, now);
			return;
		}
		
		// Zona intermediária → chance de seguir (comportamento humano)
		if (Rnd.get(100) < 40)
		{
			player.getAI().startFollow(leader, PARTY_COMFORT_RANGE - 100);
			memos.set(VAR_LAST_PARTY_FOLLOW, now);
		}
	}
}
