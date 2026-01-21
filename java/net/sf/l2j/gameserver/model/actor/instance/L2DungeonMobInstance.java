package net.sf.l2j.gameserver.model.actor.instance;

import net.sf.l2j.dungeon.Dungeon;
import net.sf.l2j.gameserver.ThreadPool;
import net.sf.l2j.gameserver.model.actor.Creature;
import net.sf.l2j.gameserver.model.actor.template.NpcTemplate;

/**
 * @author Anarchy
 * @Modificador -= TioPatinhas =-
 */
public class L2DungeonMobInstance extends L2MonsterInstance
{
	private Dungeon dungeon;
	
	public L2DungeonMobInstance(int objectId, NpcTemplate template)
	{
		super(objectId, template);
	}
	
	@Override
	public boolean doDie(Creature killer)
	{
		if (!super.doDie(killer))
			return false;
		
		if (dungeon != null)
			ThreadPool.schedule(() -> dungeon.onMobKill(this), 1000 * 2);
		
		return true;
	}
	
	public void setDungeon(Dungeon dungeon)
	{
		this.dungeon = dungeon;
	}
}
