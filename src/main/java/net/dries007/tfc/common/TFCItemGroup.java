/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common;

import java.util.function.Supplier;

import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.util.Lazy;

import net.dries007.tfc.common.blocks.TFCBlocks;
import net.dries007.tfc.common.blocks.plant.Plant;
import net.dries007.tfc.common.blocks.rock.Ore;
import net.dries007.tfc.common.blocks.rock.Rock;
import net.dries007.tfc.common.blocks.wood.Wood;
import net.dries007.tfc.common.capabilities.food.FoodCapability;
import net.dries007.tfc.common.capabilities.food.IFood;
import net.dries007.tfc.common.items.Food;
import net.dries007.tfc.common.items.TFCItems;
import net.dries007.tfc.util.Metal;

import static net.dries007.tfc.TerraFirmaCraft.MOD_ID;

public final class TFCItemGroup extends CreativeModeTab
{
    public static final CreativeModeTab EARTH = new TFCItemGroup("earth", () -> new ItemStack(TFCBlocks.ROCK_BLOCKS.get(net.dries007.tfc.common.blocks.rock.Rock.QUARTZITE).get(net.dries007.tfc.common.blocks.rock.Rock.BlockType.RAW).get()));
    public static final CreativeModeTab ORES = new TFCItemGroup("ores", () -> new ItemStack(TFCItems.GRADED_ORES.get(Ore.NATIVE_COPPER).get(Ore.Grade.NORMAL).get()));
    public static final CreativeModeTab ROCK_STUFFS = new TFCItemGroup("rock", () -> new ItemStack(TFCBlocks.ROCK_BLOCKS.get(net.dries007.tfc.common.blocks.rock.Rock.ANDESITE).get(Rock.BlockType.RAW).get()));
    public static final CreativeModeTab METAL = new TFCItemGroup("metals", () -> new ItemStack(TFCItems.METAL_ITEMS.get(Metal.Default.WROUGHT_IRON).get(Metal.ItemType.INGOT).get()));
    public static final CreativeModeTab WOOD = new TFCItemGroup("wood", () -> new ItemStack(TFCBlocks.WOODS.get(Wood.DOUGLAS_FIR).get(Wood.BlockType.LOG).get()));
    public static final CreativeModeTab FOOD = new TFCItemGroup("food", () -> new ItemStack(TFCItems.FOOD.get(Food.RED_APPLE).get()));
    public static final CreativeModeTab FLORA = new TFCItemGroup("flora", () -> new ItemStack(TFCBlocks.PLANTS.get(Plant.GOLDENROD).get()));
    public static final CreativeModeTab DECORATIONS = new TFCItemGroup("decorations", () -> new ItemStack(TFCBlocks.ALABASTER_BRICKS.get(DyeColor.CYAN).get()));
    public static final CreativeModeTab MISC = new TFCItemGroup("misc", () -> new ItemStack(TFCItems.FIRESTARTER.get()));

    private final Lazy<ItemStack> iconStack;

    private TFCItemGroup(String label, Supplier<ItemStack> iconSupplier)
    {
        super(MOD_ID + "." + label);
        this.iconStack = Lazy.of(() -> FoodCapability.setStackNonDecaying(iconSupplier.get()));
    }

    @Override
    public ItemStack makeIcon()
    {
        return iconStack.get();
    }
}