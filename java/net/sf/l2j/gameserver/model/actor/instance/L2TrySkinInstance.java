package net.sf.l2j.gameserver.model.actor.instance;

import net.sf.l2j.dresmee.DressMe;
import net.sf.l2j.gameserver.ThreadPool;
import net.sf.l2j.gameserver.instancemanager.custom.DressMeData;
import net.sf.l2j.gameserver.model.L2Object;
import net.sf.l2j.gameserver.model.actor.Player;
import net.sf.l2j.gameserver.model.actor.template.NpcTemplate;
import net.sf.l2j.gameserver.model.itemcontainer.Inventory;
import net.sf.l2j.gameserver.model.zone.ZoneId;
import net.sf.l2j.gameserver.network.serverpackets.ItemList;
import net.sf.l2j.gameserver.network.serverpackets.MagicSkillUse;
import net.sf.l2j.gameserver.network.serverpackets.NpcHtmlMessage;
import net.sf.l2j.gameserver.network.serverpackets.SetupGauge;
import net.sf.l2j.gameserver.util.Broadcast;

public class L2TrySkinInstance extends L2NpcInstance
{
    public L2TrySkinInstance(int objectId, NpcTemplate template)
    {
        super(objectId, template);
    }

    @Override
    public void onBypassFeedback(Player player, String command)
    {
        if (command.equalsIgnoreCase("skin"))
        {
            showMainMenu(player);
        }
        else if (command.equalsIgnoreCase("skinArmor"))
        {
            showSkinArmorHtml(player);
        }
        else if (command.equalsIgnoreCase("skinWeapon"))
        {
            showSkinWeaponHtml(player);
        }
        else if (command.equalsIgnoreCase("skinArmorPage2"))
        {
        	showSkinArmorHtml2(player);
        }
        else if (command.equalsIgnoreCase("skinWeaponPage2"))
        {
        	showSkinWeaponHtml2(player);
        }
        else if (command.startsWith("trySkin"))
        {
            handleTrySkinCommand(player, command);
        }
        else if (command.startsWith("tryWepSkin"))
        {
            handleTryWepSkinCommand(player, command);
        }
        else
        {
            super.onBypassFeedback(player, command);
        }
    }

    private void showMainMenu(Player player)
    {
        NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
        html.setFile("data/html/mods/SkinNpc/Main.htm");
        html.replace("%objectId%", String.valueOf(getObjectId())); // <-- ESSENCIAL
        player.sendPacket(html);
    }

    private void showSkinArmorHtml(Player player)
    {
        NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
        html.setFile("data/html/mods/SkinNpc/TrySkinArmor.htm");
        html.replace("%objectId%", String.valueOf(getObjectId())); // <-- ESSENCIAL
        player.sendPacket(html);
    }
    private void showSkinArmorHtml2(Player player)
    {
        NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
        html.setFile("data/html/mods/SkinNpc/TrySkinArmorPage2.htm");
        html.replace("%objectId%", String.valueOf(getObjectId())); // <-- ESSENCIAL
        player.sendPacket(html);
    }

    private void showSkinWeaponHtml(Player player)
    {
        NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
        html.setFile("data/html/mods/SkinNpc/TrySkinWeapon.htm");
        html.replace("%objectId%", String.valueOf(getObjectId())); // <-- ESSENCIAL
        player.sendPacket(html);
    }
    private void showSkinWeaponHtml2(Player player)
    {
        NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
        html.setFile("data/html/mods/SkinNpc/TrySkinWeaponPage2.htm");
        html.replace("%objectId%", String.valueOf(getObjectId())); // <-- ESSENCIAL
        player.sendPacket(html);
    }

    private static void handleTrySkinCommand(Player player, String command)
    {
    	if (player.isCastingNow() || player.isCastingSimultaneouslyNow())
    	{
    	    player.sendMessage("You cannot try a skin while casting a skill.");
    	    return;
    	}

        if (!player.isInsideZone(ZoneId.TOWN))
        {
            player.sendMessage("You can only try skins while inside a city.");
            return;
        }
        if (player.isParalyzed())
        {
            player.sendMessage("Please wait until the current skin test finishes.");
            return;
        }

        
        if (player.getDress() != null)
        {
            player.sendMessage("You are already trying a skin. Please wait.");
            return;
        }

        String[] parts = command.split(" ");
        if (parts.length != 2)
        {
            player.sendMessage("Usage: trySkin <skinId>");
            return;
        }

        int skinId;
        try
        {
            skinId = Integer.parseInt(parts[1]);
        }
        catch (NumberFormatException e)
        {
            player.sendMessage("Invalid skin ID.");
            return;
        }

        DressMe dress = DressMeData.getInstance().getItemId(skinId);
        if (dress == null)
        {
            player.sendMessage("Skin not found.");
            return;
        }

        // Bloqueia o jogador e toca animação
        player.setIsParalyzed(true);

        final L2Object oldTarget = player.getTarget();
        player.setTarget(player);        
        Broadcast.toSelfAndKnownPlayers(player, new MagicSkillUse(player, 1036, 1, 3000, 0));
        player.setTarget(oldTarget);
        player.sendPacket(new SetupGauge(0, 3000));

        // Após 3 segundos aplica a skin e libera o player
        ThreadPool.schedule(() -> {
            player.setIsParalyzed(false);

            player.setDress(dress);
            player.broadcastUserInfo();

            // Remove a skin após mais 3 segundos (se quiser)
            ThreadPool.schedule(() -> {
                player.setDress(DressMeData.getInstance().getItemId(0));
                player.broadcastUserInfo();
            }, 3000L);

        }, 3000L);
    }

    private static void handleTryWepSkinCommand(Player player, String command)
    {
        if (player.isCastingNow() || player.isCastingSimultaneouslyNow())
        {
            player.sendMessage("You cannot try a skin while casting a skill.");
            return;
        }

        if (player.isParalyzed())
        {
            player.sendMessage("Please wait until the current weapon skin test finishes.");
            return;
        }
        
        if (player.getFakeWeaponObjectId() != 0)
        {
            player.sendMessage("Wait, you are experiencing a weapon skin.");
            return;
        }

        // Verifica se tem arma equipada (mão direita)
        if (player.getActiveWeaponInstance() != null)
        {
            player.sendMessage("You must unequip your weapon before testing a weapon skin.");
            return;
        }

        // Verifica se tem escudo equipado (mão esquerda)
        if (player.getInventory().getPaperdollItem(Inventory.PAPERDOLL_LHAND) != null)
        {
            player.sendMessage("You must unequip your shield before testing a weapon skin.");
            return;
        }

        if (!player.isInsideZone(ZoneId.TOWN))
        {
            player.sendMessage("This command can only be used within a city.");
            return;
        }

        String[] parts = command.split(" ");
        if (parts.length != 2)
        {
            player.sendMessage("Usage: tryWepSkin <skinId>");
            return;
        }

        int skinId;
        try
        {
            skinId = Integer.parseInt(parts[1]);
        }
        catch (NumberFormatException e)
        {
            player.sendMessage("Invalid skin ID.");
            return;
        }

        if (skinId == 0)
        {
            player.sendMessage("Invalid skin.");
            return;
        }

        // Começa animação e paralisia para efeito
        player.setIsParalyzed(true);
        final L2Object oldTarget = player.getTarget();
        player.setTarget(player);
        Broadcast.toSelfAndKnownPlayers(player, new MagicSkillUse(player, 1036, 1, 3000, 0));
        player.setTarget(oldTarget);
        player.sendPacket(new SetupGauge(0, 3000));

        ThreadPool.schedule(() -> {
            player.setIsParalyzed(false);

            player.setFakeWeaponObjectId(skinId);
            player.setFakeWeaponItemId(skinId);
            player.broadcastUserInfo();
            player.sendPacket(new ItemList(player, false));
            player.sendMessage("Weapon skin activated.");

            // Remove skin após 3 segundos
            ThreadPool.schedule(() -> {
                player.removeFakeWeapon();
                player.broadcastUserInfo();
                player.sendMessage("Weapon skin test ended.");
            }, 3000L);

        }, 3000L);
    }


    @Override
    public void showChatWindow(Player player)
    {
        showMainMenu(player); // Mostra o menu inicial com os botões
    }
}
