package mods.fakeplayer.ai.elf.mystic;

import net.sf.l2j.gameserver.model.ShotType;

import mods.fakeplayer.actor.FakePlayer;
import mods.fakeplayer.ai.combat.CombatBehaviorAI;
import mods.fakeplayer.enums.CombatKit;
import mods.fakeplayer.skills.SkillAction;
import mods.fakeplayer.skills.SkillCombo;

public class ElfMysticAI extends CombatBehaviorAI
{
	
	
	public ElfMysticAI(FakePlayer character)
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
		// handlePotions(_fakePlayer);
		handleShots(_fakePlayer);
		// handleBuffers(_fakePlayer);
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
		combo = new SkillCombo(new SkillAction(getBestSkill(1177)));
	}
	
}