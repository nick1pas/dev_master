package mods.fakeplayer.ai.combat;

import java.util.HashSet;
import java.util.Set;

import net.sf.l2j.commons.random.Rnd;
import net.sf.l2j.gameserver.datatables.SkillTable;
import net.sf.l2j.gameserver.instancemanager.PrivateMessageManager.PrivateMessage;
import net.sf.l2j.gameserver.model.L2Skill;
import net.sf.l2j.gameserver.model.ShotType;
import net.sf.l2j.gameserver.model.actor.Creature;
import net.sf.l2j.gameserver.network.clientpackets.Say2;

import mods.fakeplayer.actor.FakePlayer;
import mods.fakeplayer.ai.AbstractFakePlayerAI;
import mods.fakeplayer.data.FakeChatData;
import mods.fakeplayer.data.FakeChatData.ChatContext;
import mods.fakeplayer.enums.CombatKit;
import mods.fakeplayer.interfaces.IAttacker;
import mods.fakeplayer.interfaces.IBufferSpender;
import mods.fakeplayer.interfaces.IConsumableSpender;
import mods.fakeplayer.interfaces.ICrafter;
import mods.fakeplayer.interfaces.IDeath;
import mods.fakeplayer.interfaces.IEquipes;
import mods.fakeplayer.interfaces.IEventRegister;
import mods.fakeplayer.interfaces.IExplorerWalker;
import mods.fakeplayer.interfaces.ILevel;
import mods.fakeplayer.interfaces.IPartyRange;
import mods.fakeplayer.interfaces.IPickup;
import mods.fakeplayer.interfaces.IPotionSpender;
import mods.fakeplayer.interfaces.IPrivateBuy;
import mods.fakeplayer.interfaces.IPrivateSeller;
import mods.fakeplayer.interfaces.IShotsSpender;
import mods.fakeplayer.interfaces.ITargetSelect;
import mods.fakeplayer.interfaces.ITeleport;
import mods.fakeplayer.interfaces.ITownStore;
import mods.fakeplayer.skills.SkillCombo;

public abstract class CombatBehaviorAI extends AbstractFakePlayerAI implements IDeath, ITownStore, IEventRegister, IPartyRange, IExplorerWalker, ITeleport, ICrafter, IPickup, ILevel, IShotsSpender, IPrivateSeller, IPrivateBuy, IPotionSpender, IEquipes, IConsumableSpender, IBufferSpender, IAttacker, ITargetSelect
{
	protected Creature _target;
	protected SkillCombo combo;
	protected int skillStreak = 0;
	protected long lastBackstabTry = 0;
	protected long _lastChatTime = 0;
	protected long nextArcherDecision = 0;
	protected long ARCHER_DECISION_DELAY = 1500; // ms
	protected Set<String> _usedResponses = new HashSet<>();
	protected final Set<String> _used = new HashSet<>();
	
	public CombatBehaviorAI(FakePlayer character)
	{
		super(character);
	}
	
	@Override
	public void onAiTick()
	{
		if (handleDeath(_fakePlayer))
			return;
		
		handlePartyCohesion(_fakePlayer);
		handleExplorer(_fakePlayer, _target);
		HandlerTeleport(_fakePlayer);
		handleTvTRegister(_fakePlayer);
		handleTownActivity(_fakePlayer);
	}
	
	public void clearTarget()
	{
		_target = null;
		_fakePlayer.setTarget(null);
	}
	
	public Creature getTarget()
	{
		return _target;
	}
	
	public void setTarget(Creature target)
	{
		_target = target;
		_fakePlayer.setTarget(target);
	}
	
	public FakePlayer getPlayer()
	{
		return _fakePlayer;
	}
	
	public abstract ShotType getShotType();
	
	public abstract boolean isMage();
	
	public abstract CombatKit getCombatKit();
	
	public abstract SkillCombo getCombatCombo();
	
	protected int getArrowId()
	{
		int playerLevel = _fakePlayer.getLevel();
		if (playerLevel < 20)
			return 17; // wooden arrow
		if (playerLevel >= 20 && playerLevel < 40)
			return 1341; // bone arrow
		if (playerLevel >= 40 && playerLevel < 52)
			return 1342; // steel arrow
		if (playerLevel >= 52 && playerLevel < 61)
			return 1343; // Silver arrow
		if (playerLevel >= 61 && playerLevel < 76)
			return 1344; // Mithril Arrow
		if (playerLevel >= 76)
			return 1345; // shining
			
		return 0;
	}
	
	protected L2Skill getBestSkill(int skillId)
	{
		int maxLevel = SkillTable.getInstance().getMaxLevel(skillId);
		if (maxLevel <= 0)
			return null;
		return SkillTable.getInstance().getInfo(skillId, maxLevel);
	}
	
	protected void handlePetToggleBuff(int skillId, boolean checkPet)
	{
		if (checkPet)
		{
			if (_fakePlayer.getPet() == null || _fakePlayer.getPet().isDead())
				return;
		}
		
		L2Skill skill = SkillTable.getInstance().getInfo(skillId, _fakePlayer.getSkillLevel(skillId));
		
		if (skill == null)
			return;
		
		if (_fakePlayer.getFirstEffect(skill) != null)
			return;
		
		_fakePlayer.useMagic(skill, false, false);
	}
	
	public void onPrivateMessage(PrivateMessage msg)
	{
		if (_fakePlayer.isPrivateBuying() || _fakePlayer.isPrivateSelling() || _fakePlayer.isPrivateManufactureing())
			return;
		
		if (msg.isAnswered())
			return;
		
		long now = System.currentTimeMillis();
		
		// Cooldown global
		if (now - _lastChatTime < 8000)
			return;
		
		// Chance de responder
		if (Rnd.get(100) < 5)
			return;
		
		ChatContext ctx = _fakePlayer.resolveContext();
		
		String response = FakeChatData.getInstance().getRandomResponse(msg.getTypeId(), _fakePlayer.getLang(), ctx, _usedResponses);
		
		if (response == null)
			return;
		
		_lastChatTime = now;
		_usedResponses.add(response);
		
		msg.markSeen();
		msg.markAnswered();
		
		_fakePlayer.sendTell(msg.getSenderName(), response, msg.getTypeId());
	}
	
	public void onGlobalChat()
	{
		if (_fakePlayer.isPrivateBuying() || _fakePlayer.isPrivateSelling() || _fakePlayer.isPrivateManufactureing())
			return;
		
		long now = System.currentTimeMillis();
		
		int chatId = Say2.ALL;
		
		if (Rnd.get(100) > 50)
		{
			chatId = Say2.SHOUT;
		}
		
		ChatContext ctx = _fakePlayer.resolveContext();
		
		String response = FakeChatData.getInstance().getRandomResponse(chatId, _fakePlayer.getLang(), ctx, _used);
		
		if (response == null)
			return;
		
		_used.add(response);
		
		_fakePlayer.setLastChatGlobalTime(now);
		_fakePlayer.sendGlobalMessage(response, chatId);
		
		if (_used.size() > 30)
			_used.clear();
		
	}
	
	public void resetSkillStreak()
	{
		skillStreak = 0;
	}
	
	public void incSkillStreak()
	{
		skillStreak++;
	}
	
	public int getSkillStreak()
	{
		return skillStreak;
	}
	
	public boolean canTryBackstab()
	{
		return System.currentTimeMillis() - lastBackstabTry > 1500;
	}
	
	public void markBackstabTry()
	{
		lastBackstabTry = System.currentTimeMillis();
	}
	
	public boolean canReposition()
	{
		return System.currentTimeMillis() >= nextArcherDecision;
	}
	
	public void lockReposition()
	{
		nextArcherDecision = System.currentTimeMillis() + ARCHER_DECISION_DELAY;
	}
	
}
