package mods.fakeplayer.ai.darkelf.fighter;

import net.sf.l2j.gameserver.model.ShotType;

import mods.fakeplayer.actor.FakePlayer;
import mods.fakeplayer.ai.combat.CombatBehaviorAI;
import mods.fakeplayer.enums.CombatKit;
import mods.fakeplayer.skills.SkillAction;
import mods.fakeplayer.skills.SkillCombo;

public class GhostSentinelAI extends CombatBehaviorAI
{
	
	
	public GhostSentinelAI(FakePlayer character)
	{
		super(character);
		LoadSkills();
	}
	
	@Override
	public void onAiTick()
	{
		super.onAiTick();
		if (handlePickUp(_fakePlayer))
			return;
		handleEquipes(_fakePlayer);
		handleLevel(_fakePlayer);
		handlePotions(_fakePlayer);
		handleShots(_fakePlayer);
		handleConsumable(_fakePlayer, getArrowId());
		handleBuffers(_fakePlayer);
		handleTargetSelection(_fakePlayer);
		handleAttackTarget(_fakePlayer, _target);
	}
	
	@Override
	public SkillCombo getCombatCombo()
	{
		return combo;
	}
	
	@Override
	public ShotType getShotType()
	{
		return ShotType.SOULSHOT;
	}
	
	@Override
	public boolean isMage()
	{
		return false;
	}
	
	@Override
	public CombatKit getCombatKit()
	{
		return CombatKit.ARCHER_KITE;
	}
	
	protected void LoadSkills()
	{
		combo = new SkillCombo(new SkillAction(getBestSkill(112)), new SkillAction(getBestSkill(343)), new SkillAction(getBestSkill(101)));
	}
}