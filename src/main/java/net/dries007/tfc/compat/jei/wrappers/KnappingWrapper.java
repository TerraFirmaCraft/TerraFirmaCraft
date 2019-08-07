/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.compat.jei.wrappers;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;

import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.ingredients.VanillaTypes;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.dries007.tfc.api.recipes.knapping.KnappingRecipe;
import net.dries007.tfc.api.recipes.knapping.KnappingRecipeStone;
import net.dries007.tfc.api.registries.TFCRegistries;
import net.dries007.tfc.api.types.Rock;
import net.dries007.tfc.objects.items.rock.ItemRock;

import static net.dries007.tfc.api.recipes.knapping.KnappingRecipe.Type.*;
import static net.dries007.tfc.api.util.TFCConstants.MOD_ID;

public class KnappingWrapper implements IRecipeWrapper
{
    private static final ResourceLocation CLAY_DISABLED_TEXTURE = new ResourceLocation(MOD_ID, "textures/gui/knapping/clay_button_disabled.png");
    private static final ResourceLocation FIRE_CLAY_DISABLED_TEXTURE = new ResourceLocation(MOD_ID, "textures/gui/knapping/clay_button_fire_disabled.png");

    private static final ResourceLocation CLAY_TEXTURE = new ResourceLocation(MOD_ID, "textures/gui/knapping/clay_button.png");
    private static final ResourceLocation FIRE_CLAY_TEXTURE = new ResourceLocation(MOD_ID, "textures/gui/knapping/clay_button_fire.png");
    private static final ResourceLocation LEATHER_TEXTURE = new ResourceLocation(MOD_ID, "textures/gui/knapping/leather_button.png");

    private int lastTimer = -1;
    private Rock lastRock = Rock.GRANITE;

    private KnappingRecipe recipe;

    public KnappingWrapper(KnappingRecipe recipe)
    {
        this.recipe = recipe;
    }

    @Override
    public void getIngredients(IIngredients ingredients)
    {
        if (recipe instanceof KnappingRecipeStone)
        {
            List<List<ItemStack>> allOutputs = new ArrayList<>();
            NonNullList<ItemStack> possibleOutputs = NonNullList.create();
            for (Rock rock : TFCRegistries.ROCKS.getValuesCollection())
            {
                possibleOutputs.add(recipe.getOutput(new ItemStack(ItemRock.get(rock))));
            }
            allOutputs.add(possibleOutputs);
            ingredients.setOutputLists(VanillaTypes.ITEM, allOutputs);
        }
        else
        {

            ItemStack output = recipe.getOutput(ItemStack.EMPTY);
            ingredients.setOutput(VanillaTypes.ITEM, output);
        }
    }

    @Override
    public void drawInfo(Minecraft minecraft, int recipeWidth, int recipeHeight, int mouseX, int mouseY)
    {
        if (minecraft.currentScreen != null)
        {
            ResourceLocation high = null;
            ResourceLocation low = null;
            if (recipe.getType() == CLAY)
            {
                high = CLAY_TEXTURE;
                low = CLAY_DISABLED_TEXTURE;
            }
            else if (recipe.getType() == FIRE_CLAY)
            {
                high = FIRE_CLAY_TEXTURE;
                low = FIRE_CLAY_DISABLED_TEXTURE;
            }
            else if (recipe.getType() == LEATHER)
            {
                high = LEATHER_TEXTURE;
                low = null;
            }
            else if (recipe.getType() == STONE)
            {
                high = getRock(minecraft.world.getTotalWorldTime()).getTexture();
                low = null;
            }
            for (int y = 0; y < recipe.getMatrix().getHeight(); y++)
            {
                for (int x = 0; x < recipe.getMatrix().getWidth(); x++)
                {
                    if (recipe.getMatrix().get(x, y))
                    {
                        if (high != null)
                        {
                            minecraft.renderEngine.bindTexture(high);
                            Gui.drawModalRectWithCustomSizedTexture(1 + x * 16, 1 + y * 16, 0, 0, 16, 16, 16, 16);
                        }
                    }
                    else
                    {
                        if (low != null)
                        {
                            minecraft.renderEngine.bindTexture(low);
                            Gui.drawModalRectWithCustomSizedTexture(1 + x * 16, 1 + y * 16, 0, 0, 16, 16, 16, 16);
                        }
                    }
                }
            }

        }
    }

    private Rock getRock(long worldTimer)
    {
        int newTimer = (int) (worldTimer / 20);
        if (lastTimer < newTimer)
        {
            lastTimer = newTimer;
            List<Rock> rocks = new ArrayList<>(TFCRegistries.ROCKS.getValuesCollection());
            int index = newTimer % rocks.size();
            lastRock = rocks.get(index);
        }
        return lastRock;
    }
}
