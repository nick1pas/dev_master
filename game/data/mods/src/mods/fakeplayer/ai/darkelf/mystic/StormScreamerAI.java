package mods.fakeplayer.ai.darkelf.mystic;

import net.sf.l2j.gameserver.model.ShotType;

import mods.fakeplayer.actor.FakePlayer;
import mods.fakeplayer.ai.combat.CombatBehaviorAI;
import mods.fakeplayer.enums.CombatKit;
import mods.fakeplayer.skills.SkillAction;
import mods.fakeplayer.skills.SkillCombo;

public class StormScreamerAI extends CombatBehaviorAI
{
	
	
	private int ARCANE_POWER = 337;
	
	public StormScreamerAI(FakePlayer character)
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
		handleConsumable(_fakePlayer, boneId);
		handleShots(_fakePlayer);
		handleBuffers(_fakePlayer);
		handlePetToggleBuff(ARCANE_POWER, false);
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
		return ShotType.BLESSED_SPIRITSHOT;
	}
	
	@Override
	public boolean isMage()
	{
		return true;
	}
	
	@Override
	public CombatKit getCombatKit()
	{
		return CombatKit.MAGE_NUKE;
	}
	
	protected void LoadSkills()
	{
		combo = new SkillCombo(new SkillAction(getBestSkill(1239)), new SkillAction(getBestSkill(1341)), new SkillAction(getBestSkill(1343)), new SkillAction(getBestSkill(1074)), new SkillAction(getBestSkill(1234)), new SkillAction(getBestSkill(1148)));
	}
}