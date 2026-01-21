package mods.fakeplayer.ai.drawn.fighter;

import net.sf.l2j.gameserver.model.ShotType;

import mods.fakeplayer.actor.FakePlayer;
import mods.fakeplayer.ai.combat.CombatBehaviorAI;
import mods.fakeplayer.enums.CombatKit;
import mods.fakeplayer.skills.SkillCombo;

public class DrawnFighterAI extends CombatBehaviorAI
{
	public DrawnFighterAI(FakePlayer character)
	{
		super(character);
	}
	
	@Override
	public void onAiTick()
	{
		super.onAiTick();
		if (handlePickUp(_fakePlayer))
			return;
		
		handleEquipes(_fakePlayer);
		handleLevel(_fakePlayer);
		// handlePotions(_fakePlayer);
		handleShots(_fakePlayer);
		// handleBuffers(_fakePlayer);
		handleTargetSelection(_fakePlayer);
		handleAttackTarget(_fakePlayer, _target);
		
	}
	
	@Override
	public SkillCombo getCombatCombo()
	{
		return SkillCombo.EMPTY;
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
}