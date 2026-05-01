package net.sf.l2j.gameserver.network.clientpackets;

import java.nio.BufferUnderflowException;

import net.sf.l2j.Config;
import net.sf.l2j.gameserver.ai.CtrlIntention;
import net.sf.l2j.gameserver.geoengine.GeoEngine;
import net.sf.l2j.gameserver.model.Location;
import net.sf.l2j.gameserver.model.actor.Player;
import net.sf.l2j.gameserver.network.SystemMessageId;
import net.sf.l2j.gameserver.network.serverpackets.ActionFailed;
import net.sf.l2j.gameserver.network.serverpackets.EnchantResult;
import net.sf.l2j.gameserver.network.serverpackets.StopMove;

public class MoveBackwardToLocation extends L2GameClientPacket
{
	private static final int MAX_MOVE_DISTANCE = 9900;
	private static final int MAX_ORIGIN_DESYNC = 750;
	private static final int MAX_Z_DIFF_WITHOUT_GEO = 500;
	
	private int _targetX;
	private int _targetY;
	private int _targetZ;
	
	private int _originX;
	private int _originY;
	private int _originZ;
	
	private boolean _malformed;
	
	@Override
	protected void readImpl()
	{
		try
		{
			_targetX = readD();
			_targetY = readD();
			_targetZ = readD();
			
			_originX = readD();
			_originY = readD();
			_originZ = readD();
			
			readD();
		}
		catch (BufferUnderflowException e)
		{
			_malformed = true;
		}
	}
	
	@Override
	protected void runImpl()
	{
		final Player player = getClient().getActiveChar();
		if (player == null)
			return;
		
		if (_malformed)
		{
			player.sendPacket(ActionFailed.STATIC_PACKET);
			
			if (Config.L2WALKER_PROTECTION)
				player.increaseMovePacketViolation();
			
			return;
		}
		
		if (player.isOutOfControl() || player.isMovementDisabled())
		{
			player.sendPacket(ActionFailed.STATIC_PACKET);
			return;
		}
		
		cancelEnchant(player);
		
		if (isSameLocation())
		{
			player.sendPacket(new StopMove(player));
			return;
		}
		
		if (handleTeleportMode(player))
			return;
		
		if (!isValidOrigin(player))
		{
			player.sendPacket(ActionFailed.STATIC_PACKET);
			
			if (Config.L2WALKER_PROTECTION)
				player.increaseMovePacketViolation();
			
			return;
		}
		
		if (!isValidDistance())
		{
			player.sendPacket(ActionFailed.STATIC_PACKET);
			
			if (Config.L2WALKER_PROTECTION)
				player.increaseMovePacketViolation();
			
			return;
		}
		
		Location destination = buildValidatedDestination(player);
		
		if (destination == null)
		{
			player.sendPacket(ActionFailed.STATIC_PACKET);
			
			if (Config.L2WALKER_PROTECTION)
				player.increaseMovePacketViolation();
			
			return;
		}
		
		player.getAI().setIntention(CtrlIntention.MOVE_TO, destination);
	}
	
	private static void cancelEnchant(Player player)
	{
		if (player.getActiveEnchantItem() == null)
			return;
		
		player.setActiveEnchantItem(null);
		player.sendPacket(EnchantResult.CANCELLED);
		player.sendPacket(SystemMessageId.ENCHANT_SCROLL_CANCELLED);
	}
	
	private boolean isSameLocation()
	{
		return _targetX == _originX && _targetY == _originY && _targetZ == _originZ;
	}
	
	private boolean handleTeleportMode(Player player)
	{
		if (player.getTeleMode() <= 0)
			return false;
		
		if (!player.isGM())
		{
			player.setTeleMode(0);
			player.sendPacket(ActionFailed.STATIC_PACKET);
			return true;
		}
		
		if (player.getTeleMode() == 1)
			player.setTeleMode(0);
		
		player.sendPacket(ActionFailed.STATIC_PACKET);
		player.teleToLocation(_targetX, _targetY, _targetZ, 0);
		return true;
	}
	
	private boolean isValidOrigin(Player player)
	{
		double dx = _originX - player.getX();
		double dy = _originY - player.getY();
		double dz = _originZ - player.getZ();
		
		return (dx * dx + dy * dy + dz * dz) <= MAX_ORIGIN_DESYNC * MAX_ORIGIN_DESYNC;
	}
	
	private boolean isValidDistance()
	{
		double dx = _targetX - _originX;
		double dy = _targetY - _originY;
		
		return (dx * dx + dy * dy) <= MAX_MOVE_DISTANCE * MAX_MOVE_DISTANCE;
	}
	
	private Location buildValidatedDestination(Player player)
	{
		int targetX = _targetX;
		int targetY = _targetY;
		double targetZ = _targetZ + player.getCollisionHeight();
		
		if (Config.ENABLE_GEODATA)
		{
			if (!GeoEngine.getInstance().canMoveToTarget(player.getX(), player.getY(), player.getZ(), targetX, targetY, (int) targetZ))
			{
				Location validLoc = GeoEngine.getInstance().canMoveToTargetLoc(player.getX(), player.getY(), player.getZ(), targetX, targetY, (int) targetZ);
				
				if (validLoc == null)
					return null;
				
				return validLoc;
			}
			
			int geoZ = GeoEngine.getInstance().getHeight(targetX, targetY,(int) targetZ);
			return new Location(targetX, targetY, geoZ);
		}
		
		int zDiff = Math.abs((int)targetZ - player.getZ());
		
		if (zDiff > MAX_Z_DIFF_WITHOUT_GEO)
			return null;
		
		return new Location(targetX, targetY, (int)targetZ);
	}
}