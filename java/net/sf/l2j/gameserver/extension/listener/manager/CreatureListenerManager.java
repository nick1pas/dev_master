package net.sf.l2j.gameserver.extension.listener.manager;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import net.sf.l2j.gameserver.extension.listener.OnAttackHitListener;
import net.sf.l2j.gameserver.extension.listener.OnAttackListener;
import net.sf.l2j.gameserver.extension.listener.OnCurrentHpDamageListener;
import net.sf.l2j.gameserver.extension.listener.OnDeathListener;
import net.sf.l2j.gameserver.extension.listener.OnKillListener;
import net.sf.l2j.gameserver.extension.listener.OnMoveListener;
import net.sf.l2j.gameserver.extension.listener.OnReviveListener;
import net.sf.l2j.gameserver.model.L2Skill;
import net.sf.l2j.gameserver.model.Location;
import net.sf.l2j.gameserver.model.actor.Creature;
import net.sf.l2j.gameserver.model.actor.Player;

public class CreatureListenerManager
{
	private static final CreatureListenerManager INSTANCE = new CreatureListenerManager();
	
	private final List<OnReviveListener> reviveListeners = new CopyOnWriteArrayList<>();
	private final List<OnMoveListener> moveListeners = new CopyOnWriteArrayList<>();
	private final List<OnKillListener> killListeners = new CopyOnWriteArrayList<>();
	private final List<OnDeathListener> deathListeners = new CopyOnWriteArrayList<>();
	private final List<OnCurrentHpDamageListener> hpDamageListeners = new CopyOnWriteArrayList<>();
	private final List<OnAttackListener> attackListeners = new CopyOnWriteArrayList<>();
	private final List<OnAttackHitListener> attackHitListeners = new CopyOnWriteArrayList<>();
	
	private CreatureListenerManager()
	{
	}
	
	public static CreatureListenerManager getInstance()
	{
		return INSTANCE;
	}

	public void addReviveListener(OnReviveListener listener)
	{
		reviveListeners.add(listener);
	}
	
	public void addMoveListener(OnMoveListener listener)
	{
		moveListeners.add(listener);
	}
	
	public void addKillListener(OnKillListener listener)
	{
		killListeners.add(listener);
	}
	
	public void addDeathListener(OnDeathListener listener)
	{
		deathListeners.add(listener);
	}
	
	public void addHpDamageListener(OnCurrentHpDamageListener listener)
	{
		hpDamageListeners.add(listener);
	}
	
	public void addAttackListener(OnAttackListener listener)
	{
		attackListeners.add(listener);
	}
	
	public void addAttackHitListener(OnAttackHitListener listener)
	{
		attackHitListeners.add(listener);
	}

	public void notifyRevive(Creature creature)
	{
		for (OnReviveListener listener : reviveListeners)
			listener.onRevive(creature);
	}
	
	public void notifyMove(Creature creature, Location loc)
	{
		for (OnMoveListener listener : moveListeners)
			listener.onMove(creature, loc);
	}
	
	public void notifyKill(Creature killer, Creature victim)
	{
		for (OnKillListener listener : killListeners)
		{
			
			if (((Player) killer).hasPet() || ((Player) killer).getPet() != null)
			{
				if (listener.ignorePetOrSummon())
					continue;
			}
			listener.onKill(killer, victim);
		}
	}
	
	public void notifyDeath(Creature player)
	{
		for (OnDeathListener listener : deathListeners)
			listener.onDeath(player);
	}
	
	public void notifyHpDamage(Creature creature, double damageHp, Creature target, L2Skill skill)
	{
		for (OnCurrentHpDamageListener listener : hpDamageListeners)
			listener.onCurrentHpDamage(creature, damageHp, target, skill);
	}
	
	public void notifyAttack(Creature creature, Creature target)
	{
		for (OnAttackListener listener : attackListeners)
			listener.onAttack(creature, target);
	}
	
	public void notifyAttackHit(Creature creature, Creature target, int damage)
	{
		for (OnAttackHitListener listener : attackHitListeners)
			listener.onAttackHit(creature, target, damage);
	}
}