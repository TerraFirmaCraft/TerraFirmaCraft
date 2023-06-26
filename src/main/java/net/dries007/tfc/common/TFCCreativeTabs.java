/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common;

import java.util.function.Supplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

import net.dries007.tfc.common.blocks.TFCBlocks;
import net.dries007.tfc.common.blocks.plant.Plant;
import net.dries007.tfc.common.blocks.rock.Ore;
import net.dries007.tfc.common.blocks.rock.Rock;
import net.dries007.tfc.common.blocks.wood.Wood;
import net.dries007.tfc.common.items.Food;
import net.dries007.tfc.common.items.TFCItems;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.Metal;

import static net.dries007.tfc.TerraFirmaCraft.*;

@SuppressWarnings("unused")
public final class TFCCreativeTabs
{
    public static final DeferredRegister<CreativeModeTab> CREATIVE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MOD_ID);


    public static final RegistryObject<CreativeModeTab> EARTH = register("earth", () -> new ItemStack(TFCBlocks.ROCK_BLOCKS.get(Rock.QUARTZITE).get(Rock.BlockType.RAW).get()), (parameters, out) -> {

    });
    public static final RegistryObject<CreativeModeTab> ORES = register("ores", () -> new ItemStack(TFCItems.GRADED_ORES.get(Ore.NATIVE_COPPER).get(Ore.Grade.NORMAL).get()), (parameters, out) -> {
        TFCItems.ORES.values().forEach(e -> out.accept(e.get()));
    });
    public static final RegistryObject<CreativeModeTab> ROCK_STUFFS = register("rock", () -> new ItemStack(TFCBlocks.ROCK_BLOCKS.get(Rock.ANDESITE).get(Rock.BlockType.RAW).get()), (parameters, out) -> {

    });
    public static final RegistryObject<CreativeModeTab> METAL = register("metals", () -> new ItemStack(TFCItems.METAL_ITEMS.get(Metal.Default.WROUGHT_IRON).get(Metal.ItemType.INGOT).get()), (parameters, out) -> {

    });
    public static final RegistryObject<CreativeModeTab> WOOD = register("wood", () -> new ItemStack(TFCBlocks.WOODS.get(Wood.DOUGLAS_FIR).get(Wood.BlockType.LOG).get()), (parameters, out) -> {

    });
    public static final RegistryObject<CreativeModeTab> FOOD = register("food", () -> new ItemStack(TFCItems.FOOD.get(Food.RED_APPLE).get()), (parameters, out) -> {

    });
    public static final RegistryObject<CreativeModeTab> FLORA = register("flora", () -> new ItemStack(TFCBlocks.PLANTS.get(Plant.GOLDENROD).get()), (parameters, out) -> {

    });
    public static final RegistryObject<CreativeModeTab> DECORATIONS = register("decorations", () -> new ItemStack(TFCBlocks.ALABASTER_BRICKS.get(DyeColor.CYAN).get()), (parameters, out) -> {

    });
    public static final RegistryObject<CreativeModeTab> MISC = register("misc", () -> new ItemStack(TFCItems.FIRESTARTER.get()), (parameters, out) -> {

    });

    private static RegistryObject<CreativeModeTab> register(String name, Supplier<ItemStack> icon, CreativeModeTab.DisplayItemsGenerator displayItems)
    {
        return CREATIVE_TABS.register(name, () -> CreativeModeTab.builder()
            .icon(icon)
            .title(Helpers.translatable("tfc.itemGroup." + name))
            .displayItems(displayItems)
            .build());
    }
}