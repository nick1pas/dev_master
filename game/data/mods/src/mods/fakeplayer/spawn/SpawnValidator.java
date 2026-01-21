package mods.fakeplayer.spawn;

import net.sf.l2j.gameserver.model.Location;

public final class SpawnValidator
{
	private SpawnValidator()
	{
	}
	
	public static Location validate(int x, int y, int z)
	{
		return new Location(x, y, z);
	}
}
