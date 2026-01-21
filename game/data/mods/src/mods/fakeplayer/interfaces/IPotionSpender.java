package mods.fakeplayer.interfaces;

import mods.fakeplayer.actor.FakePlayer;

public interface IPotionSpender
{
	default void handlePotions(FakePlayer fakePlayer)
	{
		fakePlayer.tryUsePotion();
	}
}