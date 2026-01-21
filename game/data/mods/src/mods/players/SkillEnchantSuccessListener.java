package mods.players;

import net.sf.l2j.gameserver.datatables.SkillTable;
import net.sf.l2j.gameserver.extension.listener.actor.player.OnSkillEnchantSuccessListener;
import net.sf.l2j.gameserver.model.actor.Player;

public class SkillEnchantSuccessListener implements OnSkillEnchantSuccessListener
{
	@Override
	public void onSkillEnchantSuccess(Player player, int skillId, int skillLevel)
	{
		player.sendMessage("Skill enchantada com sucesso! " + SkillTable.getInstance().getInfo(skillId, skillLevel).getName() + " Level: " + skillLevel);
	}
}