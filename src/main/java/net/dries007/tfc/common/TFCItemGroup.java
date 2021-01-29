/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common;

import java.util.function.Supplier;

import net.minecraft.item.DyeColor;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraftforge.common.util.Lazy;

import net.dries007.tfc.common.blocks.TFCBlocks;
import net.dries007.tfc.common.blocks.plant.Plant;
import net.dries007.tfc.common.items.TFCItems;
import net.dries007.tfc.common.types.Metal;
import net.dries007.tfc.common.types.Rock;
import net.dries007.tfc.common.types.Wood;

import static net.dries007.tfc.TerraFirmaCraft.MOD_ID;

public final class TFCItemGroup extends ItemGroup
{
    // todo: replace this eventually with actual items
    public static final Supplier<ItemStack> MISSING_ITEM = () -> new ItemStack(Items.JACK_O_LANTERN);

    public static final ItemGroup EARTH = new TFCItemGroup("earth", () -> new ItemStack(TFCBlocks.ROCK_BLOCKS.get(Rock.Default.QUARTZITE).get(Rock.BlockType.RAW).get()));
    public static final ItemGroup ORES = new TFCItemGroup("ores", MISSING_ITEM);
    public static final ItemGroup ROCK_STUFFS = new TFCItemGroup("rock", MISSING_ITEM);
    public static final ItemGroup METAL = new TFCItemGroup("metals", () -> new ItemStack(TFCItems.METAL_ITEMS.get(Metal.Default.WROUGHT_IRON).get(Metal.ItemType.INGOT).get()));
    public static final ItemGroup WOOD = new TFCItemGroup("wood", () -> new ItemStack(TFCBlocks.WOODS.get(Wood.Default.DOUGLAS_FIR).get(Wood.BlockType.LOG).get()));
    public static final ItemGroup FOOD = new TFCItemGroup("food", MISSING_ITEM);
    public static final ItemGroup FLORA = new TFCItemGroup("flora", () -> new ItemStack(TFCBlocks.PLANTS.get(Plant.GOLDENROD).get()));
    public static final ItemGroup DECORATIONS = new TFCItemGroup("decorations", () -> new ItemStack(TFCBlocks.ALABASTER_BRICKS.get(DyeColor.CYAN).get()));
    public static final ItemGroup MISC = new TFCItemGroup("misc", MISSING_ITEM);

    private final Lazy<ItemStack> iconStack;

    private TFCItemGroup(String label, Supplier<ItemStack> iconSupplier)
    {
        super(MOD_ID + "." + label);
        this.iconStack = Lazy.of(iconSupplier);
    }

    @Override
    public ItemStack makeIcon()
    {
        return iconStack.get();
    }
}