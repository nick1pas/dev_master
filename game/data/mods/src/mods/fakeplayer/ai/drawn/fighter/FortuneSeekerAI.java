package mods.fakeplayer.ai.drawn.fighter;

import net.sf.l2j.gameserver.model.ShotType;

import mods.fakeplayer.actor.FakePlayer;
import mods.fakeplayer.ai.combat.CombatBehaviorAI;
import mods.fakeplayer.enums.CombatKit;
import mods.fakeplayer.skills.SkillAction;
import mods.fakeplayer.skills.SkillCombo;

public class FortuneSeekerAI extends CombatBehaviorAI
{
	
	
	public FortuneSeekerAI(FakePlayer character)
	{
		super(character);
		loadSkills();
		_fakePlayer.registerRecipes();
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
		handleBuffers(_fakePlayer);
		handlePetToggleBuff(4, false);
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
		return CombatKit.DAGGER;
	}
	
	protected void loadSkills()
	{
		combo = new SkillCombo(new SkillAction(getBestSkill(358)), new SkillAction(getBestSkill(344)), new SkillAction(getBestSkill(30)), new SkillAction(getBestSkill(263)));
	}
}