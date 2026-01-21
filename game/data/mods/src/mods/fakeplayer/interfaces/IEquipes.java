package mods.fakeplayer.interfaces;

import net.sf.l2j.commons.random.Rnd;
import net.sf.l2j.gameserver.datatables.ItemTable;
import net.sf.l2j.gameserver.model.item.instance.ItemInstance;
import net.sf.l2j.gameserver.model.item.kind.Item;
import net.sf.l2j.gameserver.model.item.type.CrystalType;

import mods.fakeplayer.actor.FakePlayer;
import mods.fakeplayer.data.FakePlayerData;
import mods.fakeplayer.holder.FakeItem;
import mods.fakeplayer.holder.FakeTemplate;

public interface IEquipes
{
	default void handleEquipes(FakePlayer fake)
	{
		if (fake.getActiveWeaponInstance() != null)
			return;
		
		equipMissingWeapon(fake);
	}
	
	private static void equipMissingWeapon(FakePlayer fake)
	{
		final FakeTemplate tpl = FakePlayerData.getInstance().getTemplate(fake.getClassId().getId());
		if (tpl == null)
			return;
		
		for (FakeItem fi : tpl.getItems())
		{
			if (fi.getItemId() <= 0)
				continue;
			
			Item template = ItemTable.getInstance().getTemplate(fi.getItemId());
			if (template == null)
				continue;
				
			// ===============================
			// IDENTIFICA SE É WEAPON
			// ===============================
			boolean isWeapon = template.getType2() == Item.TYPE2_WEAPON || (template.getBodyPart() & Item.SLOT_R_HAND) != 0;
			
			if (!isWeapon)
				continue;
				
			// ===============================
			// CRIA E EQUIPA
			// ===============================
			ItemInstance inst = fake.addItem("FakeEquip", fi.getItemId(), 1, fake, false);
			
			if (inst == null)
				return;
			
			if (template.getCrystalType() != CrystalType.NONE)
				inst.setEnchantLevel(Rnd.get(3, 11));
			
			fake.getInventory().equipItem(inst);
			fake.broadcastUserInfo();
			return;
		}
	}
}
