/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import net.dries007.tfc.TerraFirmaCraft;

import static net.dries007.tfc.api.util.TFCConstants.MOD_ID;

public final class CreativeTabsTFC
{
    public static final CreativeTabs CT_ROCK_BLOCKS = new TFCCreativeTab("rock.blocks", "tfc:smooth/granite");
    public static final CreativeTabs CT_ROCK_ITEMS = new TFCCreativeTab("rock.items", "tfc:ore/tetrahedrite");
    public static final CreativeTabs CT_WOOD = new TFCCreativeTab("wood", "tfc:wood/log/pine");
    public static final CreativeTabs CT_DECORATIONS = new TFCCreativeTab("decorations", "tfc:wall/cobble/granite");
    public static final CreativeTabs CT_METAL = new TFCCreativeTab("metal", "tfc:metal/ingot/bronze");
    public static final CreativeTabs CT_GEMS = new TFCCreativeTab("gems", "tfc:gem/diamond");
    public static final CreativeTabs CT_POTTERY = new TFCCreativeTab("pottery", "tfc:mold/ingot");
    public static final CreativeTabs CT_MISC = new TFCCreativeTab("misc", "tfc:wand");
    public static final CreativeTabs CT_FLORA = new TFCCreativeTab("flora", "tfc:plants/goldenrod");

    private static class TFCCreativeTab extends CreativeTabs
    {
        private final ResourceLocation iconResourceLocation;

        private TFCCreativeTab(String label, String icon)
        {
            super(MOD_ID + "." + label);
            iconResourceLocation = new ResourceLocation(icon);
        }

        @SideOnly(Side.CLIENT)
        @Override
        public ItemStack createIcon()
        {
            //noinspection ConstantConditions
            ItemStack stack = new ItemStack(ForgeRegistries.ITEMS.getValue(iconResourceLocation));
            if (!stack.isEmpty()) return stack;

            TerraFirmaCraft.getLog().error("[Please inform developers] No icon stack for creative tab {}", getTabLabel());
            return new ItemStack(Items.STICK);
        }
    }
}
