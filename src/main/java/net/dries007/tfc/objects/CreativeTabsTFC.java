/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects;

import java.util.Arrays;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

import net.dries007.tfc.TerraFirmaCraft;

import static net.dries007.tfc.Constants.MOD_ID;
import static net.minecraft.creativetab.CreativeTabs.CREATIVE_TAB_ARRAY;

public final class CreativeTabsTFC
{
    public static final CreativeTabs CT_ROCK_BLOCKS = new CT("rock.blocks", "tfc:smooth/granite");
    public static final CreativeTabs CT_ROCK_ITEMS = new CT("rock.items", "tfc:ore/tetrahedrite");
    public static final CreativeTabs CT_WOOD = new CT("wood", "tfc:wood/log/pine");
    public static final CreativeTabs CT_DECORATIONS = new CT("decorations", "tfc:wall/cobble/granite");
    public static final CreativeTabs CT_METAL = new CT("metal", "tfc:metal/ingot/bronze");
    public static final CreativeTabs CT_GEMS = new CT("gems", "tfc:gem/diamond");
    public static final CreativeTabs CT_POTTERY = new CT("pottery", "tfc:mold/axe_head/bronze");
    public static final CreativeTabs CT_PLANTS = new CT("plants", "tfc:crops/seedbag/tomato");
    public static final CreativeTabs CT_FOOD = new CT("food", "tfc:food/greenapple");
    public static final CreativeTabs CT_MISC = new CT("misc", "tfc:wand");

    public static void init()
    {
        Arrays.stream(CREATIVE_TAB_ARRAY).filter(CT.class::isInstance).map(CT.class::cast).forEach(CT::loadIconStack);
    }

    private static class CT extends CreativeTabs
    {
        private final ResourceLocation iconResourceLocation;
        private ItemStack iconStack;

        private CT(String label, String icon)
        {
            super(MOD_ID + "." + label);

            iconResourceLocation = new ResourceLocation(icon);
        }

        @Override
        public ItemStack getTabIconItem()
        {
            return iconStack;
        }

        private void loadIconStack()
        {
            //noinspection ConstantConditions
            iconStack = new ItemStack(ForgeRegistries.ITEMS.getValue(iconResourceLocation));
            if (iconStack.isEmpty())
            {
                TerraFirmaCraft.getLog().error("No icon stack for creative tab {}", getTabLabel());
                iconStack = new ItemStack(Items.STICK);
            }
        }
    }
}
