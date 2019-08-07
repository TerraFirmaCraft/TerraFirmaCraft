package net.dries007.tfc.compat.jei.categories;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;

import mezz.jei.api.IGuiHelper;
import mezz.jei.api.gui.*;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.ingredients.VanillaTypes;
import net.dries007.tfc.api.util.TFCConstants;
import net.dries007.tfc.compat.jei.BaseRecipeCategory;
import net.dries007.tfc.compat.jei.wrappers.SimpleRecipeWrapper;

@ParametersAreNonnullByDefault
public class HeatCategory extends BaseRecipeCategory<SimpleRecipeWrapper>
{
    private static final ResourceLocation ICONS = new ResourceLocation(TFCConstants.MOD_ID, "textures/gui/jei/icons.png");

    private final IDrawableStatic slot;
    private final IDrawableStatic fire, icon;
    private final IDrawableAnimated fireAnimated;

    public HeatCategory(IGuiHelper helper, String Uid)
    {
        super(helper.createBlankDrawable(120, 38), Uid);
        icon = helper.createDrawable(ICONS, 0, 30, 18, 18);
        fire = helper.createDrawable(ICONS, 0, 0, 14, 14);
        IDrawableStatic arrowAnimated = helper.createDrawable(ICONS, 14, 0, 14, 14);
        this.fireAnimated = helper.createAnimatedDrawable(arrowAnimated, 160, IDrawableAnimated.StartDirection.TOP, true);
        this.slot = helper.getSlotDrawable();
    }

    @Nullable
    @Override
    public IDrawable getIcon()
    {
        return icon;
    }

    @Override
    public void drawExtras(Minecraft minecraft)
    {
        fire.draw(minecraft, 54, 16);
        fireAnimated.draw(minecraft, 54, 16);
        slot.draw(minecraft, 20, 16);
        slot.draw(minecraft, 84, 16);
    }

    @Override
    public void setRecipe(IRecipeLayout recipeLayout, SimpleRecipeWrapper recipeWrapper, IIngredients ingredients)
    {
        IGuiItemStackGroup itemStackGroup = recipeLayout.getItemStacks();
        itemStackGroup.init(0, true, 20, 16);
        itemStackGroup.init(1, false, 84, 16);

        itemStackGroup.set(0, ingredients.getInputs(VanillaTypes.ITEM).get(0));
        itemStackGroup.set(1, ingredients.getOutputs(VanillaTypes.ITEM).get(0));
    }
}
