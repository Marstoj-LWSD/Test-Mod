package net.marstoj.testmod;

import net.fabricmc.api.ClientModInitializer;
import net.marstoj.testmod.block.ModBlocks;
import net.marstoj.testmod.screen.ElectricFurnaceScreen;
import net.minecraft.client.gui.screen.ingame.HandledScreens;

public class TestModClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        HandledScreens.register(ModBlocks.ELECTRIC_FURNACE_SCREEN_HANDLER, ElectricFurnaceScreen::new);
    }
}
