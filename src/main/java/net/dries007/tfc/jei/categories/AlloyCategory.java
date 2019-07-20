/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.jei.categories;

import java.util.List;
import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;

import mcp.MethodsReturnNonnullByDefault;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.gui.IDrawableAnimated;
import mezz.jei.api.gui.IDrawableStatic;
import mezz.jei.api.gui.IGuiItemStackGroup;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.ingredients.VanillaTypes;
import net.dries007.tfc.api.registries.TFCRegistries;
import net.dries007.tfc.api.types.Metal;
import net.dries007.tfc.api.types.Ore;
import net.dries007.tfc.api.util.TFCConstants;
import net.dries007.tfc.jei.wrappers.AlloyWrapper;
import net.dries007.tfc.objects.fluids.FluidMetal;
import net.dries007.tfc.objects.items.metal.ItemIngot;
import net.dries007.tfc.objects.items.metal.ItemOreTFC;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class AlloyCategory extends TFCRecipeCategory<AlloyWrapper>
{
    private static final ResourceLocation ICONS = new ResourceLocation(TFCConstants.MOD_ID, "textures/gui/jei/icons.png");

    private final IDrawableStatic slot;
    private final IDrawableStatic fire;
    private final IDrawableAnimated fireAnimated;

    public AlloyCategory(IGuiHelper helper, String Uid)
    {
        super(helper.createBlankDrawable(156, 38), Uid);
        fire = helper.createDrawable(ICONS, 0, 0, 14, 14);
        IDrawableStatic arrowAnimated = helper.createDrawable(ICONS, 14, 0, 14, 14);
        this.fireAnimated = helper.createAnimatedDrawable(arrowAnimated, 160, IDrawableAnimated.StartDirection.TOP, true);
        this.slot = helper.getSlotDrawable();
    }

    @Override
    public void drawExtras(Minecraft minecraft)
    {
        slot.draw(minecraft, 0, 16);
        slot.draw(minecraft, 30, 16);
        slot.draw(minecraft, 60, 16);
        slot.draw(minecraft, 90, 16);
        fire.draw(minecraft, 118, 16);
        fireAnimated.draw(minecraft, 118, 16);
        slot.draw(minecraft, 138, 16);
    }

    @Override
    public void setRecipe(IRecipeLayout recipeLayout, AlloyWrapper recipeWrapper, IIngredients ingredients)
    {
        IGuiItemStackGroup itemStackGroup = recipeLayout.getItemStacks();
        itemStackGroup.init(0, true, 0, 16);
        itemStackGroup.init(1, true, 30, 16);
        itemStackGroup.init(2, true, 60, 16);
        itemStackGroup.init(3, true, 90, 16);
        itemStackGroup.init(4, false, 138, 16);
        for (int i = 0; i < ingredients.getInputs(VanillaTypes.FLUID).size(); i++)
        {
            List<FluidStack> input = ingredients.getInputs(VanillaTypes.FLUID).get(i);
            Metal metal = ((FluidMetal) input.get(0).getFluid()).getMetal();
            NonNullList<ItemStack> possibleSmeltable = NonNullList.create();
            possibleSmeltable.add(new ItemStack(ItemIngot.get(metal, Metal.ItemType.INGOT)));
            for (Ore ore : TFCRegistries.ORES.getValuesCollection())
            {
                if (ore.getMetal() == metal)
                {
                    possibleSmeltable.add(new ItemStack(ItemOreTFC.get(ore)));
                }
            }
            itemStackGroup.set(i, possibleSmeltable);
        }

        itemStackGroup.set(4, ingredients.getOutputs(VanillaTypes.ITEM).get(0));
    }
}
