package mods.fakeplayer.ai.orc.fighter;

import net.sf.l2j.gameserver.model.ShotType;

import mods.fakeplayer.actor.FakePlayer;
import mods.fakeplayer.ai.combat.CombatBehaviorAI;
import mods.fakeplayer.enums.CombatKit;
import mods.fakeplayer.skills.SkillAction;
import mods.fakeplayer.skills.SkillCombo;

public class GrandKhavatariAI extends CombatBehaviorAI
{
	
	
	public GrandKhavatariAI(FakePlayer character)
	{
		super(character);
		loadSkills();
	}
	
	@Override
	public void onAiTick()
	{
		super.onAiTick();
		if (handlePickUp(_fakePlayer))
			return;
		handleEquipes(_fakePlayer);
		handleConsumable(_fakePlayer, energyStone);
		handleLevel(_fakePlayer);
		handlePotions(_fakePlayer);
		handleShots(_fakePlayer);
		handleBuffers(_fakePlayer);
		handleTargetSelection(_fakePlayer);
		handleAttackTarget(_fakePlayer, _target);
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
		return CombatKit.FIGHTER_DPS;
	}
	
	@Override
	public SkillCombo getCombatCombo()
	{
		return combo;
	}
	
	protected void loadSkills()
	{
		combo = new SkillCombo(new SkillAction(getBestSkill(284)), new SkillAction(getBestSkill(281)), new SkillAction(getBestSkill(280)), new SkillAction(getBestSkill(54)), new SkillAction(getBestSkill(346)));
	}
	
}
