package net.sf.l2j.gameserver.handler.voicedcommandhandlers;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.concurrent.ConcurrentHashMap;

import net.sf.l2j.Config;
import net.sf.l2j.gameserver.datatables.ItemTable;
import net.sf.l2j.gameserver.datatables.NpcTable;
import net.sf.l2j.gameserver.datatables.xml.IconTable;
import net.sf.l2j.gameserver.handler.IVoicedCommandHandler;
import net.sf.l2j.gameserver.instancemanager.custom.RaidBossInfoManager;
import net.sf.l2j.gameserver.model.actor.Player;
import net.sf.l2j.gameserver.model.actor.template.NpcTemplate;
import net.sf.l2j.gameserver.model.item.DropCategory;
import net.sf.l2j.gameserver.model.item.DropData;
import net.sf.l2j.gameserver.model.item.kind.Item;
import net.sf.l2j.gameserver.network.serverpackets.NpcHtmlMessage;

public class VoicedBossSpawn implements IVoicedCommandHandler {
    protected static final String[] _voicedCommands = { "shifffmodddrop", "boss" }; // Comando para "shifffmodddrop" e "boss"
    protected static final int PAGE_LIMIT = 7; // Limite de Raid Bosses por página
    protected static final Map<Integer, Integer> LAST_PAGE = new ConcurrentHashMap<>();

    @Override
    public boolean useVoicedCommand(String command, Player activeChar, String target) {
        if (command.startsWith("boss")) {
            String[] parts = command.split(" ");
            int page = 1;
            if (parts.length > 1) {
                try {
                    page = Integer.parseInt(parts[1]);
                } catch (NumberFormatException e) {
                    page = 1;
                }
            }
            showBossListWindow(activeChar, page);
        } else if (command.startsWith("shifffmodddrop")) {
            final StringTokenizer st = new StringTokenizer(command, " ");
            st.nextToken();
            int npcId = Integer.parseInt(st.nextToken());
            int page = (st.hasMoreTokens()) ? Integer.parseInt(st.nextToken()) : 1;
            ShiffNpcDropList(activeChar, npcId, page);
        }
        return true;
    }

    public static void showBossListWindow(Player player, int page) {
        List<Integer> raidBosses = new ArrayList<>(Config.LIST_RAID_BOSS_IDS);  // Lista de Raid Bosses
        List<Integer> grandBosses = new ArrayList<>(Config.LIST_GRAND_BOSS_IDS);  // Lista de Grand Bosses

        final StringBuilder sb = new StringBuilder();
        sb.append("<html>");
        sb.append("<body>");

        sb.append("<table width=300>");
        sb.append("<tr><td align=left><font color='FFFF00' size='5'><b>Boss List for " + player.getName() + "</b></font></td></tr>");
        sb.append("</table>");
        
        sb.append("<br>");

        // Divida os bosses entre vivos e mortos
        List<Integer> raidBossesAlive = new ArrayList<>();
        List<Integer> raidBossesDead = new ArrayList<>();
        
        // Classificar Raid Bosses em vivos e mortos
        for (int bossId : raidBosses) {
            final NpcTemplate template = NpcTable.getInstance().getTemplate(bossId);
            if (template == null) continue;

            final long respawnTime = RaidBossInfoManager.getInstance().getRaidBossRespawnTime(bossId);
            
            if (respawnTime <= System.currentTimeMillis()) {
                raidBossesAlive.add(bossId);
            } else {
                raidBossesDead.add(bossId);
            }
        }

        // Exibição dos Raid Bosses
        sb.append("<table width=\"300\" bgcolor=\"000000\" cellpadding=\"0\" cellspacing=\"0\">");
        sb.append("<tr><td align=\"center\"><font color=\"FF8C00\"><b>Raid Bosses</b></font></td></tr>");
        sb.append("</table>");
        sb.append("<img src=\"L2UI.SquareGray\" width=300 height=1>");

        // Calculando o total de bosses e paginando
        List<Integer> allRaidBosses = new ArrayList<>();
        allRaidBosses.addAll(raidBossesAlive); // Comece com os bosses vivos
        allRaidBosses.addAll(raidBossesDead); // Adicione os mortos depois

        // Paginando os bosses
        int totalRaidBosses = allRaidBosses.size();
        int startIndex = (page - 1) * PAGE_LIMIT;
        int endIndex = Math.min(startIndex + PAGE_LIMIT, totalRaidBosses);

        // Exibe os bosses da página atual
        for (int i = startIndex; i < endIndex; i++) {
            final int bossId = allRaidBosses.get(i);
            final NpcTemplate template = NpcTable.getInstance().getTemplate(bossId);
            if (template == null) continue;

            String bossName = template.getName();
            if (bossName.length() > 32) bossName = bossName.substring(0, 32) + "...";

            final long respawnTime = RaidBossInfoManager.getInstance().getRaidBossRespawnTime(bossId);
            sb.append("<table width=300 cellpadding=\"0\" cellspacing=\"0\">");
            sb.append("<tr>");
            sb.append("<td width=200><a action=\"bypass  voiced_shifffmodddrop " + bossId + "\">" + bossName + "</a></td>");
            
            if (respawnTime <= System.currentTimeMillis()) {
                sb.append("<td width=100 align=center><font color=\"9CC300\">Alive</font></td>");
            } else {
                // Calculando a data de respawn
                String respawnDate = calculateTimeLeft(respawnTime);
                
                // Modificado para alinhar à esquerda
                sb.append("<td width=100 align=left>");
                sb.append("<font color=\"FB5858\">Dead - " + respawnDate + "</font>");
                sb.append("</td>");
            }
            sb.append("</tr>");
            sb.append("</table>");
        }

        // Adiciona links de paginação abaixo dos Raid Bosses
        sb.append("<br><table width=300><tr><td align=\"center\">");

        // Paginação: Mostrar "Previous" e "Next"
        if (page > 1) {
            sb.append("<a action=\"bypass  voiced_boss " + (page - 1) + "\" style=\"font-size: 14px; padding-right: 20px; display:inline-block;\">Previous</a>");
        }
        if (endIndex < totalRaidBosses) {
            sb.append("<a action=\"bypass  voiced_boss " + (page + 1) + "\" style=\"font-size: 14px; padding-left: 20px; display:inline-block;\">Next</a>");
        } else {
            sb.append("<span style=\"font-size: 14px; padding-left: 20px; display:inline-block;\">Next</span>");
        }

        sb.append("</td></tr></table><br>");
        
        // Exibição dos Grand Bosses
        sb.append("<img src=\"L2UI.SquareGray\" width=300 height=1>");
        sb.append("<table width=300 bgcolor=\"000000\" cellpadding=\"0\" cellspacing=\"0\">");
        sb.append("<tr><td align=\"center\"><font color=\"FF8C00\"><b>Grand Bosses</b></font></td></tr>");
        sb.append("</table>");
        sb.append("<img src=\"L2UI.SquareGray\" width=300 height=1>");

        List<Integer> grandBossesAlive = new ArrayList<>();
        List<Integer> grandBossesDead = new ArrayList<>();

        // Classificar Grand Bosses em vivos e mortos
        for (int bossId : grandBosses) {
            final NpcTemplate template = NpcTable.getInstance().getTemplate(bossId);
            if (template == null) continue;

            final long respawnTime = RaidBossInfoManager.getInstance().getRaidBossRespawnTime(bossId);
            
            if (respawnTime <= System.currentTimeMillis()) {
                grandBossesAlive.add(bossId);
            } else {
                grandBossesDead.add(bossId);
            }
        }

        // Divida os bosses grand em vivos e mortos
        List<Integer> allGrandBosses = new ArrayList<>();
        allGrandBosses.addAll(grandBossesAlive); // Comece com os bosses vivos
        allGrandBosses.addAll(grandBossesDead); // Adicione os mortos depois

        int totalGrandBosses = allGrandBosses.size();
        for (int i = 0; i < totalGrandBosses; i++) {
            final int bossId = allGrandBosses.get(i);
            final NpcTemplate template = NpcTable.getInstance().getTemplate(bossId);
            if (template == null) continue;

            String bossName = template.getName();
            if (bossName.length() > 32) bossName = bossName.substring(0, 32) + "...";

            final long respawnTime = RaidBossInfoManager.getInstance().getRaidBossRespawnTime(bossId);
            sb.append("<table width=300 cellpadding=\"0\" cellspacing=\"0\">");
            sb.append("<tr>");
            sb.append("<td width=200><a action=\"bypass  voiced_shifffmodddrop " + bossId + "\">" + bossName + "</a></td>");
            
            if (respawnTime <= System.currentTimeMillis()) {
                sb.append("<td width=100 align=center><font color=\"9CC300\">Alive</font></td>");
            } else {
                // Calculando a data de respawn
                String respawnDate = calculateTimeLeft(respawnTime);
                
                // Modificado para alinhar à esquerda
                sb.append("<td width=100 align=left>");
                sb.append("<font color=\"FB5858\">Dead - " + respawnDate + "</font>");
                sb.append("</td>");
            }
            sb.append("</tr>");
            sb.append("</table>");
        }

        // Adiciona o link "Return"
        sb.append("<br><table width=300><tr><td align=\"center\"><a action=\"bypass  voiced_menu\">Return</a></td></tr></table>");
        
        sb.append("</body>");
        sb.append("</html>");
        
        final NpcHtmlMessage htm = new NpcHtmlMessage(0);
        htm.setHtml(sb.toString());
        player.sendPacket(htm);
    }

    // Função para calcular o tempo restante de respawn
 // Função para calcular e formatar a data de respawn
    private static String calculateTimeLeft(long respawnTime) {
        long timeLeft = respawnTime - System.currentTimeMillis();
        
        if (timeLeft <= 0) {
            return "Alive";  // Se o boss já tiver ressurgido, mostramos "Alive"
        }
        
        // Se o tempo restante for positivo, calculamos a data exata de respawn
        Date respawnDate = new Date(respawnTime);  // Criando um objeto Date a partir do timestamp do respawn
        
        // Formatando a data para o formato "dd/MM HH:mm"
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM HH:mm");
        return sdf.format(respawnDate);  // Retorna a data formatada
    }







    private static void ShiffNpcDropList(Player player, int npcId, int page) {
        final NpcTemplate template = NpcTable.getInstance().getTemplate(npcId);
        if (template == null)
            return;

        if (template.getDropData().isEmpty()) {
            player.sendMessage("This target have not drop info.");
            return;
        }

        final List<DropCategory> list = new ArrayList<>();
        template.getDropData().forEach(c -> list.add(c));
        Collections.reverse(list);

        int myPage = 1;
        int i = 0;
        int shown = 0;
        boolean hasMore = false;

        final StringBuilder sb = new StringBuilder();
        sb.append("<html>");
        sb.append("<body>");
        sb.append("<table width=\"256\"></table>");
        sb.append("<table width=\"224\" bgcolor=\"000000\"></table>");
        sb.append("<br>");

        for (DropCategory cat : list) {
            if (shown == PAGE_LIMIT) {
                hasMore = true;
                break;
            }

            for (DropData drop : cat.getAllDrops()) {
                double mind = 0;
                double maxd = 0;
                double chance = (double) drop.getChance() / 10000;

                mind = Config.RATE_DROP_ITEMS * drop.getMinDrop();
                maxd = Config.RATE_DROP_ITEMS * drop.getMaxDrop();

                String smind = null, smaxd = null, drops = null;
                if (mind > 999999) {
                    DecimalFormat df = new DecimalFormat("###.#");
                    smind = df.format(((mind)) / 1000000) + "KK";
                    smaxd = df.format(((maxd)) / 1000000) + "KK";
                } else if (mind > 999) {
                    smind = (mind / 1000) + "K";
                    smaxd = (maxd / 1000) + "K";
                } else {
                    smind = Float.toString((float) mind);
                    smaxd = Float.toString((float) maxd);
                }

                if (chance <= 0.001) {
                    DecimalFormat df = new DecimalFormat("#.####");
                    drops = df.format(chance);
                } else if (chance <= 0.01) {
                    DecimalFormat df = new DecimalFormat("#.###");
                    drops = df.format(chance);
                } else {
                    DecimalFormat df = new DecimalFormat("##.##");
                    drops = df.format(chance);
                }

                Item item = ItemTable.getInstance().getTemplate(drop.getItemId());
                String name = item.getName();

                if (name.length() >= 22)
                    name = name.substring(0, 21) + "...";

                if (myPage != page) {
                    i++;
                    if (i == PAGE_LIMIT) {
                        myPage++;
                        i = 0;
                    }
                    continue;
                }

                if (shown == PAGE_LIMIT) {
                    hasMore = true;
                    break;
                }

                sb.append("<table width=295 bgcolor=000000>");
                sb.append("<tr>");
                sb.append("<td align=left width=32><button width=32 height=32 back=" + IconTable.getIcon(item.getItemId()) + " fore=" + IconTable.getIcon(item.getItemId()) + "></td>");
                sb.append("<td align=left width=263>");
                sb.append("<table>");
                sb.append("<tr><td align=left width=263>" + (cat.isSweep() ? "<font color=FF0099>Sweep Chance</font>" : "<font color=00FF00>Drop Chance</font>") + " : (" + drops + "%)</td></tr>");
                sb.append("<tr><td align=left width=263><font color=F9FF00>" + name + "</font> - Min: <font color=00ECFF>" + smind + "</font> Max: <font color=FF0C0C>" + smaxd + "</font></td></tr>");
                sb.append("</table>");
                sb.append("</td>");
                sb.append("</tr>");
                sb.append("</table>");
                shown++;
            }
        }

        sb.append("<br><table width=300><tr><td align=\"center\">");

        // Paginação: anterior (só aparece se houver mais de uma página)
        if (page > 1) {
            sb.append("<a action=\"bypass  voiced_shifffmodddrop " + npcId + " " + (page - 1) + "\" style=\"font-size: 14px; padding-right: 20px; display:inline-block;\">Previous</a>");
        }

        // Paginação: próximo (só aparece se houver mais de uma página)
        if (hasMore) {
            sb.append("<a action=\"bypass  voiced_shifffmodddrop " + npcId + " " + (page + 1) + "\" style=\"font-size: 14px; padding-left: 20px; display:inline-block;\">Next</a>");
        }

        sb.append("</td></tr></table>");
        sb.append("<br><center><a action=\"bypass  voiced_boss\">Back to Boss List</a></center>");
        sb.append("</body>");
        sb.append("</html>");

        final NpcHtmlMessage htm = new NpcHtmlMessage(0);
        htm.setHtml(sb.toString());
        player.sendPacket(htm);
    }


    @Override
    public String[] getVoicedCommandList() {
        return _voicedCommands;
    }
}
