package net.dries007.tfc.jei.categories;

import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import mcp.MethodsReturnNonnullByDefault;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.gui.IDrawableAnimated;
import mezz.jei.api.gui.IDrawableStatic;
import mezz.jei.api.gui.IGuiItemStackGroup;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.ingredients.VanillaTypes;
import net.dries007.tfc.api.util.TFCConstants;
import net.dries007.tfc.jei.TFCJEIPlugin;
import net.dries007.tfc.jei.wrappers.AnvilWrapper;
import net.dries007.tfc.objects.items.ItemsTFC;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class AnvilCategory extends TFCRecipeCategory<AnvilWrapper>
{
    private final IDrawableStatic slot;
    private final IDrawableStatic arrow;
    private final IDrawableAnimated arrowAnimated;

    public AnvilCategory(IGuiHelper helper)
    {
        super(helper.createBlankDrawable(120, 38), "anvil");
        ResourceLocation resourceLocation = new ResourceLocation(TFCConstants.MOD_ID, "textures/gui/jei/recipes.png");
        arrow = helper.createDrawable(resourceLocation, 0, 14, 22, 16);
        IDrawableStatic arrowAnimated = helper.createDrawable(resourceLocation, 22, 14, 22, 16);
        this.arrowAnimated = helper.createAnimatedDrawable(arrowAnimated, 160, IDrawableAnimated.StartDirection.LEFT, false);
        this.slot = helper.getSlotDrawable();
    }

    @Override
    public String getUid()
    {
        return TFCJEIPlugin.ANVIL_UID;
    }

    @Override
    public void drawExtras(Minecraft minecraft)
    {
        arrow.draw(minecraft, 50, 16);
        arrowAnimated.draw(minecraft, 50, 16);
        slot.draw(minecraft, 0, 16);
        slot.draw(minecraft, 20, 16);
        slot.draw(minecraft, 84, 16);
    }

    @Override
    public void setRecipe(IRecipeLayout recipeLayout, AnvilWrapper recipeWrapper, IIngredients ingredients)
    {
        IGuiItemStackGroup itemStackGroup = recipeLayout.getItemStacks();
        itemStackGroup.init(0, true, 0, 16);
        itemStackGroup.init(1, true, 20, 16);
        itemStackGroup.init(2, false, 84, 16);

        itemStackGroup.set(0, ingredients.getInputs(VanillaTypes.ITEM).get(0));
        itemStackGroup.set(1, new ItemStack(ItemsTFC.HANDSTONE)); //TODO change to hammer
        itemStackGroup.set(2, ingredients.getOutputs(VanillaTypes.ITEM).get(0));
    }
}
