package mods.fakeplayer.ai.human.fighter;

import net.sf.l2j.gameserver.model.ShotType;

import mods.fakeplayer.actor.FakePlayer;
import mods.fakeplayer.ai.combat.CombatBehaviorAI;
import mods.fakeplayer.enums.CombatKit;
import mods.fakeplayer.skills.SkillAction;
import mods.fakeplayer.skills.SkillCombo;

public class SaggitariusAI extends CombatBehaviorAI
{
	
	
	public SaggitariusAI(FakePlayer character)
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
		
		handleLevel(_fakePlayer);
		handleConsumable(_fakePlayer, getArrowId());
		handlePotions(_fakePlayer);
		handleShots(_fakePlayer);
		handleBuffers(_fakePlayer);
		handlePetToggleBuff(99, false);
		handlePetToggleBuff(4, false);
		handlePetToggleBuff(312, false);
		handlePetToggleBuff(256, false);
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
		return CombatKit.ARCHER_KITE;
	}
	
	@Override
	public SkillCombo getCombatCombo()
	{
		return combo;
	}
	
	protected void loadSkills()
	{
		combo = new SkillCombo(new SkillAction(getBestSkill(101)) // Stun
		);
	}
	
}
