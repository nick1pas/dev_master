package mods;

import net.sf.l2j.gameserver.extension.L2JMod;
import net.sf.l2j.gameserver.extension.listener.manager.BypassCommandManager;
import net.sf.l2j.gameserver.extension.listener.manager.CreatureListenerManager;
import net.sf.l2j.gameserver.extension.listener.manager.DoorListenerManager;
import net.sf.l2j.gameserver.extension.listener.manager.GameListenerManager;
import net.sf.l2j.gameserver.extension.listener.manager.InventoryListenerManager;
import net.sf.l2j.gameserver.extension.listener.manager.NpcListenerManager;
import net.sf.l2j.gameserver.extension.listener.manager.PlayerListenerManager;
import net.sf.l2j.gameserver.extension.listener.manager.SiegeListenerManager;
import net.sf.l2j.gameserver.extension.listener.manager.TutorialLinkCommandManager;
import net.sf.l2j.gameserver.extension.listener.manager.ZoneListenerManager;

import mods.communityBoard.MerchantBBSManager;
import mods.door.DoorManagerListener;
import mods.game.CharacterDeleteCleanupListener;
import mods.game.ServerShutdownListener;
import mods.game.ServerStartListener;
import mods.npc.NpcDecay;
import mods.npc.NpcInteract;
import mods.npc.NpcKillReward;
import mods.npc.NpcSpawn;
import mods.players.AttackCheckListener;
import mods.players.AutoSoulshotListener;
import mods.players.DamageLoggerListener;
import mods.players.DeathPenaltyListener;
import mods.players.EnterWorldPlayers;
import mods.players.ItemDestroyListener;
import mods.players.ItemPickupListener;
import mods.players.LevelUpListener;
import mods.players.MovementTracker;
import mods.players.PlayerEquipeListener;
import mods.players.PlayerExitListener;
import mods.players.PlayerPvPPkListener;
import mods.players.PvPKillListener;
import mods.players.ReduceHPCurrentListener;
import mods.players.ReviveMessageListener;
import mods.players.SetClassListener;
import mods.players.SetLevelListener;
import mods.players.SkillEnchantSuccessListener;
import mods.players.TeleportListener;
import mods.siege.SiegeAttackerRegisterListener;
import mods.siege.SiegeDefenderRegisterListener;
import mods.siege.SiegeStateListener;
import mods.siege.SiegeWinnerRewardListener;
import mods.tutorialLink.TutorialLinkBypass;
import mods.zone.PvPZoneListener;

public class EngineListener implements L2JMod
{
	@Override
	public void onLoad()
	{
		
		BypassCommandManager.getInstance().registerBypassListener(new MerchantBBSManager());
		TutorialLinkCommandManager.getInstance().registerListener(new TutorialLinkBypass());
		InventoryListenerManager.getInstance().registerEquipListener(new PlayerEquipeListener());
		
		CreatureListenerManager mgr = CreatureListenerManager.getInstance();
		mgr.addReviveListener(new ReviveMessageListener());
		mgr.addMoveListener(new MovementTracker());
		mgr.addKillListener(new PvPKillListener());
		mgr.addDeathListener(new DeathPenaltyListener());
		mgr.addAttackListener(new AttackCheckListener());
		mgr.addAttackHitListener(new DamageLoggerListener());
		mgr.addHpDamageListener(new ReduceHPCurrentListener());
		
		SiegeListenerManager siegeMgr = SiegeListenerManager.getInstance();
		siegeMgr.registerListener(new SiegeStateListener());
		siegeMgr.registerAttackerListener(new SiegeAttackerRegisterListener());
		siegeMgr.registerDefenderListener(new SiegeDefenderRegisterListener());
		siegeMgr.registerWinnerListener(new SiegeWinnerRewardListener());
		
		ZoneListenerManager.getInstance().addZoneListener(new PvPZoneListener());
		
		GameListenerManager gameMgr = GameListenerManager.getInstance();
		gameMgr.registerStartListener(new ServerStartListener());
		gameMgr.registerShutdownListener(new ServerShutdownListener());
		gameMgr.registerCharacterDeleteListener(new CharacterDeleteCleanupListener());
		
		DoorListenerManager.getInstance().registerOpenCloseListener(new DoorManagerListener());
		
		PlayerListenerManager playermgr = PlayerListenerManager.getInstance();
		
		playermgr.registerAutoSoulShotListener(new AutoSoulshotListener());
		playermgr.registerItemDestroyListener(new ItemDestroyListener());
		playermgr.registerItemPickupListener(new ItemPickupListener());
		playermgr.registerLevelUpListener(new LevelUpListener());
		playermgr.registerExitListener(new PlayerExitListener());
		playermgr.registerSetClassListener(new SetClassListener());
		playermgr.registerSetLevelListener(new SetLevelListener());
		playermgr.registerSkillEnchantSuccessListener(new SkillEnchantSuccessListener());
		playermgr.registerTeleportListener(new TeleportListener());
		playermgr.registerEnterListener(new EnterWorldPlayers());
		playermgr.registerPvpPkKillListener(new PlayerPvPPkListener());
		
		NpcListenerManager npcMgr = NpcListenerManager.getInstance();
		npcMgr.registerNpcSpawnListener(new NpcSpawn());
		npcMgr.registerNpcDecayListener(new NpcDecay());
		npcMgr.registerNpcInteractListener(new NpcInteract());
		npcMgr.registerNpcKillListener(new NpcKillReward());
	}

	@Override
	public void onUnload()
	{
		
	}
	
}
