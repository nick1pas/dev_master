package net.sf.l2j.gameserver.extension.listener.actor.player;

import net.sf.l2j.gameserver.model.actor.Player;

public interface OnSkillEnchantSuccessListener
{
	void onSkillEnchantSuccess(Player player, int skillId, int skillLevel);
}