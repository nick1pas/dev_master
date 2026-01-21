package net.sf.l2j.npcs.random;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;

import net.sf.l2j.Config;
import net.sf.l2j.gameserver.ThreadPool;
import net.sf.l2j.gameserver.datatables.NpcTable;
import net.sf.l2j.gameserver.datatables.SpawnTable;
import net.sf.l2j.gameserver.model.L2Object;
import net.sf.l2j.gameserver.model.L2Spawn;
import net.sf.l2j.gameserver.model.L2World;
import net.sf.l2j.gameserver.model.L2WorldRegion;
import net.sf.l2j.gameserver.model.actor.L2Npc;
import net.sf.l2j.gameserver.model.actor.template.NpcTemplate;

public class FakeNpcSwitcher implements Runnable
{
    private ScheduledFuture<?> _task;

    // Map: npcMain (realId original) -> índice atual do fakeId para todos NPCs desse tipo
    private final Map<Integer, Integer> _globalIndex = new ConcurrentHashMap<>();

    // Map: objectId NPC -> npcMain (realId original)
    private final Map<Integer, Integer> _trackedNpcs = new ConcurrentHashMap<>();

    public void start()
    {
        int timeInMillis = Config.TIME_FOR_NEW_NPC_FAKES * 1000;
        _task = ThreadPool.scheduleAtFixedRate(this, timeInMillis, timeInMillis);
    }


    public void stop()
    {
        if (_task != null)
            _task.cancel(false);
    }

    @Override
    public void run()
    {
      //  System.out.println("[FakeNpcSwitcher] Iniciando ciclo de troca...");

        // Atualiza NPCs trackeados (só novos NPCs originais)
        updateTrackedNpcs();

        // Organiza NPCs trackeados por npcMain
        Map<Integer, List<L2Npc>> npcsByType = new HashMap<>();

        for (Map.Entry<Integer, Integer> entry : _trackedNpcs.entrySet())
        {
            int objectId = entry.getKey();
            int realId = entry.getValue();

            L2Npc npc = (L2Npc) L2World.getInstance().getObject(objectId);
            if (npc == null || npc.isDead())
            {
                _trackedNpcs.remove(objectId);
                continue;
            }

            npcsByType.computeIfAbsent(realId, k -> new ArrayList<>()).add(npc);
        }

        @SuppressWarnings("unused")
		int totalTrocas = 0;

        // Para cada tipo (npcMain), troca todos NPCs sincronizadamente
        for (Map.Entry<Integer, List<L2Npc>> entry : npcsByType.entrySet())
        {
            int realId = entry.getKey();
            List<L2Npc> npcList = entry.getValue();

            int[] fakeIds = RandomNpcIdManager.getFakeIds(realId);
            if (fakeIds == null || fakeIds.length == 0)
                continue;

            int currentIndex = _globalIndex.getOrDefault(realId, -1);
            int nextIndex = (currentIndex + 1) % fakeIds.length;
            int nextNpcId = fakeIds[nextIndex];

            NpcTemplate nextTemplate = NpcTable.getInstance().getTemplate(nextNpcId);
            if (nextTemplate == null)
                continue;

            for (L2Npc npc : npcList)
            {
                if (npc.getNpcId() == nextNpcId)
                    continue; // Já está na fakeId correta

                try
                {
                    final int x = npc.getX();
                    final int y = npc.getY();
                    final int z = npc.getZ();
                    final int h = npc.getHeading();

                    if (npc.getSpawn() != null)
                    {
                        npc.getSpawn().setRespawnState(false);
                        SpawnTable.getInstance().deleteSpawn(npc.getSpawn(), false);
                    }

                    npc.deleteMe();

                    L2Spawn spawn = new L2Spawn(nextTemplate);
                    spawn.setLoc(x, y, z, h);
                    SpawnTable.getInstance().addNewSpawn(spawn, false);
                    L2Npc newNpc = spawn.doSpawn(true);

                    // Atualiza tracking para novo NPC
                    _trackedNpcs.remove(npc.getObjectId());
                    _trackedNpcs.put(newNpc.getObjectId(), realId);

                    totalTrocas++;
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }

            _globalIndex.put(realId, nextIndex);
        }

       // System.out.println("[FakeNpcSwitcher] Trocas realizadas neste ciclo: " + totalTrocas);
    }

    private void updateTrackedNpcs()
    {
        L2WorldRegion[][] regions = L2World.getInstance().getWorldRegions();

        for (L2WorldRegion[] regionRow : regions)
        {
            for (L2WorldRegion region : regionRow)
            {
                if (region == null)
                    continue;

                for (L2Object obj : region.getVisibleObjects().values())
                {
                    if (!(obj instanceof L2Npc) )
                        continue;

                    L2Npc npc = (L2Npc) obj;
                    
                    int realId = npc.getNpcId();

                    if (RandomNpcIdManager.isRandomNpc(realId))
                    {
                        // Só trackea NPC original (npcMain)
                        if (!_trackedNpcs.containsKey(npc.getObjectId()) && isOriginalNpc(realId))
                        {
                            _trackedNpcs.put(npc.getObjectId(), realId);
                       //     System.out.println("[FakeNpcSwitcher] NPC trackeado: objId=" + npc.getObjectId() + ", realId=" + realId);
                        }
                    }
                }
            }
        }
    }

    private static boolean isOriginalNpc(int npcId)
    {
        // Se quiser, filtre IDs para garantir só os originais
        return RandomNpcIdManager.isRandomNpc(npcId);
    }
}