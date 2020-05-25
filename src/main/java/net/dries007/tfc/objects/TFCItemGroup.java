/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects;

import java.util.function.Supplier;

import net.dries007.tfc.TerraFirmaCraft;
import net.dries007.tfc.api.Gem;
import net.dries007.tfc.objects.items.TFCItems;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.util.Lazy;

import net.dries007.tfc.api.Rock;
import net.dries007.tfc.objects.blocks.TFCBlocks;

public final class TFCItemGroup extends ItemGroup
{
    public static final ItemGroup ROCK_BLOCKS = new TFCItemGroup("rock.blocks", () -> new ItemStack(TFCBlocks.ROCKS.get(Rock.Default.GRANITE).get(Rock.BlockType.RAW).get()));
    //public static final ItemGroup ROCK_ITEMS = new TFCItemGroup("rock.items", "tfc:ore/tetrahedrite");
    //public static final ItemGroup WOOD = new TFCItemGroup("wood", "tfc:wood/log/pine");
    //public static final ItemGroup DECORATIONS = new TFCItemGroup("decorations", "tfc:wall/cobble/granite");
    //public static final ItemGroup METAL = new TFCItemGroup("metal", "tfc:metal/ingot/bronze");
    public static final ItemGroup GEM = new TFCItemGroup(TerraFirmaCraft.MOD_ID + ".gems", () -> new ItemStack(TFCItems.GEMS.get(Gem.Default.RUBY).get(Gem.Grade.FLAWLESS).get()));
    //public static final ItemGroup POTTERY = new TFCItemGroup("pottery", "tfc:ceramics/fired/mold/ingot");
    //public static final ItemGroup FOOD = new TFCItemGroup("food", "tfc:food/green_apple");
    //public static final ItemGroup MISC = new TFCItemGroup("misc", "tfc:wand");
    //public static final ItemGroup FLORA = new TFCItemGroup("flora", "tfc:plants/goldenrod");

    private final Lazy<ItemStack> iconStack;

    private TFCItemGroup(String label, Supplier<ItemStack> iconSupplier)
    {
        super(label);
        this.iconStack = Lazy.of(iconSupplier);
    }

    @Override
    public ItemStack createIcon()
    {
        return iconStack.get();
    }
}
