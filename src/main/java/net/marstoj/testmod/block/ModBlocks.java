package net.marstoj.testmod.block;

import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroupEntries;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.marstoj.testmod.TestMod;
import net.marstoj.testmod.item.ModItems;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.enums.Instrument;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModBlocks {

    public static final Block STEEL_ORE = registerBlock("steel_ore",
            new Block(Block.Settings.copy(Blocks.IRON_ORE)));
    public static final Block ELECTRIC_FURNACE = registerBlock("electric_furnace", new ElectricFurnaceBlock(AbstractBlock.Settings.create().mapColor(MapColor.STONE_GRAY).instrument(Instrument.BASEDRUM).requiresTool().strength(3.5f).luminance(Blocks.createLightLevelFromLitBlockState(13))));

    public static final BlockEntityType<ElectricFurnaceBlockEntity> ELECTRIC_FURNACE_BLOCK_ENTITY = Registry.register(
            Registries.BLOCK_ENTITY_TYPE,
            new Identifier(TestMod.MOD_ID, "electric_furnace_block_entity"),
            BlockEntityType.Builder.create(ElectricFurnaceBlockEntity::new, ELECTRIC_FURNACE).build());


    private static void addItemsToNaturalBlockItemGroup(FabricItemGroupEntries entries) {
      entries.add(STEEL_ORE);
      entries.add(ELECTRIC_FURNACE);
    }

    private static Block registerBlock(String name, Block block) {
        registerBlockItem(name, block);
        return Registry.register(Registries.BLOCK, new Identifier(TestMod.MOD_ID, name), block);
    }

    private static Item registerBlockItem(String name, Block block) {
        return Registry.register(Registries.ITEM, new Identifier(TestMod.MOD_ID, name),
                new BlockItem(block, new Item.Settings()));
    }

    public static void registerModBlocks() {
        TestMod.LOGGER.info("Registering ModBlocks for " + TestMod.MOD_ID);
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.NATURAL).register(ModBlocks::addItemsToNaturalBlockItemGroup);
    }
}
