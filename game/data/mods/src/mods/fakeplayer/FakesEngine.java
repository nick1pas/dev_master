package mods.fakeplayer;

import java.util.logging.Logger;

import net.sf.l2j.gameserver.extension.L2JMod;
import net.sf.l2j.gameserver.handler.AdminCommandHandler;

import mods.fakeplayer.admin.AdminFakePlayer;
import mods.fakeplayer.data.FakeChatData;
import mods.fakeplayer.data.FakeNameData;
import mods.fakeplayer.data.FakePlayerData;
import mods.fakeplayer.data.FakePrivateBuyData;
import mods.fakeplayer.data.FakePrivateSellData;
import mods.fakeplayer.engine.FakePlayerRestoreEngine;
import mods.fakeplayer.gui.PhantomPanel;
import mods.fakeplayer.task.FakePlayerAiEngine;

public class FakesEngine implements L2JMod
{
	public static final Logger LOGGER = Logger.getLogger(FakesEngine.class.getName());
	
	@Override
	public void onLoad()
	{
		FakePlayerRestoreEngine.getInstance().collectAll();
		loadData();
		registerAdmin();
		FakePlayerAiEngine.start();
		new PhantomPanel();
	}
	
	@Override
	public void onUnload()
	{
	}
	
	private static void loadData()
	{
		FakeChatData.getInstance();
		FakePlayerData.getInstance();
		FakeNameData.getInstance();
		FakePrivateSellData.getInstance();
		FakePrivateBuyData.getInstance();
	}
	
	private static void registerAdmin()
	{
		AdminCommandHandler.getInstance().registerAdminCommandHandler(new AdminFakePlayer());
	}
	
}
