package mods.fakeplayer.interfaces;

import net.sf.l2j.gameserver.model.base.Experience;

import mods.fakeplayer.actor.FakePlayer;
import mods.fakeplayer.ai.combat.CombatBehaviorAI;
import mods.fakeplayer.data.FakePlayerData;
import mods.fakeplayer.holder.FakeTemplate;

public interface ILevel
{
	int LEVEL_TOLERANCE = 2;
	long LEVEL_CHECK_INTERVAL = 45 * 1000L; // 45 second
	
	String VAR_LAST_LEVEL_CHECK = "fake_last_level_check";
	
	default void handleLevel(FakePlayer fakePlayer)
	{
		if (!(fakePlayer.getFakeAi() instanceof CombatBehaviorAI))
			return;
		
		long now = System.currentTimeMillis();
		long lastCheck = fakePlayer.getMemos().getLong(VAR_LAST_LEVEL_CHECK, 0L);
		
		if ((now - lastCheck) < LEVEL_CHECK_INTERVAL)
			return;
		
		fakePlayer.getMemos().set(VAR_LAST_LEVEL_CHECK, now);
		fakePlayer.getMemos().hasChanges();
		final FakeTemplate tpl = FakePlayerData.getInstance().getTemplate(fakePlayer.getClassId().getId());
		
		if (tpl == null)
			return;
		
		int templateLevel = tpl.getLevel();
		int currentLevel = fakePlayer.getLevel();
		
		if (currentLevel <= templateLevel + LEVEL_TOLERANCE)
			return;
		
		restoreToTemplateLevel(fakePlayer, templateLevel);
	}
	
	private static void restoreToTemplateLevel(FakePlayer fakePlayer, int level)
	{
		if (level < 1 || level > Experience.MAX_LEVEL)
			return;
		
		long targetXp = Experience.LEVEL[level];
		long currentXp = fakePlayer.getExp();
		
		if (currentXp > targetXp)
			fakePlayer.removeExpAndSp(currentXp - targetXp, 0);
		else if (currentXp < targetXp)
			fakePlayer.addExpAndSp(targetXp - currentXp, 0);

	}
}
