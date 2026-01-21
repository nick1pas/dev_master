package mods.fakeplayer.ai.combat;

import net.sf.l2j.gameserver.model.ShotType;

import mods.fakeplayer.actor.FakePlayer;
import mods.fakeplayer.enums.CombatKit;
import mods.fakeplayer.skills.SkillCombo;

public class DefaultCombatBehaviorAI extends CombatBehaviorAI
{
	
	
	public DefaultCombatBehaviorAI(FakePlayer character)
	{
		super(character);
	}
	
	@Override
	public void onAiTick()
	{
		
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
	
}
