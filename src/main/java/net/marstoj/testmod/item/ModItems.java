package net.marstoj.testmod.item;

import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroupEntries;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.marstoj.testmod.TestMod;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.AttributeModifiersComponent;
import net.minecraft.item.*;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModItems {

    public static final Item STEEL_INGOT = registerItem("steel_ingot", new Item(new Item.Settings()));
    public static final Item RAW_STEEL = registerItem("raw_steel", new Item(new Item.Settings()));
    public static final Item STEEL_SHEET = registerItem("steel_sheet", new Item(new Item.Settings()));
    public static final Item STEEL_SCRAP = registerItem("steel_scrap", new Item(new Item.Settings()));
    public static final Item STEEL_PICKAXE = registerItem("steel_pickaxe", new PickaxeItem(ModToolMaterial.STEEL, new Item.Settings().maxCount(1).attributeModifiers(PickaxeItem.createAttributeModifiers(ModToolMaterial.STEEL, 1.0f,-2.8f ))));
    public static final Item STEEL_AXE = registerItem("steel_axe", new AxeItem(ModToolMaterial.STEEL, new Item.Settings().maxCount(1).attributeModifiers(AxeItem.createAttributeModifiers(ModToolMaterial.STEEL, 5.0f,-3.0f ))));
    public static final Item STEEL_SWORD = registerItem("steel_sword", new SwordItem(ModToolMaterial.STEEL, new Item.Settings().maxCount(1).attributeModifiers(SwordItem.createAttributeModifiers(ModToolMaterial.STEEL, 3,-2.4f ))));


    private static void addItemsToIngredientItemGroup(FabricItemGroupEntries entries) {
        entries.add(STEEL_INGOT);
        entries.add(RAW_STEEL);
        entries.add(STEEL_SHEET);
        entries.add(STEEL_SCRAP);
    }

    private static void addItemsToToolItemGroup(FabricItemGroupEntries entries) {
        entries.add(STEEL_PICKAXE);
        entries.add(STEEL_AXE);
    }

    private static void addItemsToCombatItemGroup(FabricItemGroupEntries entries) {
        entries.add(STEEL_SWORD);
        entries.add(STEEL_AXE);
    }

    private static Item registerItem(String name, Item item) {
        return Registry.register(Registries.ITEM, new Identifier(TestMod.MOD_ID, name), item);
    }

    public static void registerModItems() {
        TestMod.LOGGER.info("Registering Mod Items for " + TestMod.MOD_ID);

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.INGREDIENTS).register(ModItems::addItemsToIngredientItemGroup);
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.TOOLS).register(ModItems::addItemsToToolItemGroup);
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.COMBAT).register(ModItems::addItemsToCombatItemGroup);
    }
}
