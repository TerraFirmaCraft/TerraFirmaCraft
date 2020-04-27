/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects;

import java.util.function.Supplier;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.IItemProvider;

import net.dries007.tfc.api.Rock;
import net.dries007.tfc.objects.blocks.TFCBlocks;

public final class TFCItemGroup extends ItemGroup
{
    public static final ItemGroup ROCK_BLOCKS = new TFCItemGroup("rock.blocks", TFCBlocks.ROCKS.get(Rock.Default.GRANITE).get(Rock.BlockType.RAW).get());
    //public static final ItemGroup ROCK_ITEMS = new TFCItemGroup("rock.items", "tfc:ore/tetrahedrite");
    //public static final ItemGroup WOOD = new TFCItemGroup("wood", "tfc:wood/log/pine");
    //public static final ItemGroup DECORATIONS = new TFCItemGroup("decorations", "tfc:wall/cobble/granite");
    //public static final ItemGroup METAL = new TFCItemGroup("metal", "tfc:metal/ingot/bronze");
    //public static final ItemGroup GEMS = new TFCItemGroup("gems", "tfc:gem/diamond");
    //public static final ItemGroup POTTERY = new TFCItemGroup("pottery", "tfc:ceramics/fired/mold/ingot");
    //public static final ItemGroup FOOD = new TFCItemGroup("food", "tfc:food/green_apple");
    //public static final ItemGroup MISC = new TFCItemGroup("misc", "tfc:wand");
    //public static final ItemGroup FLORA = new TFCItemGroup("flora", "tfc:plants/goldenrod");

    private static final Logger LOGGER = LogManager.getLogger();

    private final Supplier<ItemStack> iconSupplier;
    private ItemStack stack;

    private TFCItemGroup(String label, IItemProvider iconProvider)
    {
        this(label, () -> new ItemStack(iconProvider));
    }

    private TFCItemGroup(String label, Supplier<ItemStack> iconSupplier)
    {
        super(label);
        this.iconSupplier = iconSupplier;
    }

    @Override
    public ItemStack createIcon()
    {
        if (stack == null)
        {
            stack = iconSupplier.get();
            if (stack.isEmpty())
            {
                LOGGER.warn("[Please inform developers] No icon stack for creative tab {}", getTabLabel());
                stack = new ItemStack(Items.STICK);
            }
        }
        return stack;
    }
}
