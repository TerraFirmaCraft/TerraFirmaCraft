/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.jei.wrappers;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.ingredients.VanillaTypes;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.dries007.tfc.api.recipes.KnappingRecipe;
import net.dries007.tfc.api.types.Rock;
import net.dries007.tfc.objects.items.rock.ItemRock;

import static net.dries007.tfc.api.util.TFCConstants.MOD_ID;

public class KnappingWrapper implements IRecipeWrapper
{
    private static final ResourceLocation CLAY_DISABLED_TEXTURE = new ResourceLocation(MOD_ID, "textures/gui/knapping_clay.png");

    private static final ResourceLocation CLAY_TEXTURE = new ResourceLocation(MOD_ID, "textures/gui/knapping/clay_button.png");
    private static final ResourceLocation FIRE_CLAY_TEXTURE = new ResourceLocation(MOD_ID, "textures/gui/knapping/clay_button_fire.png");
    private static final ResourceLocation LEATHER_TEXTURE = new ResourceLocation(MOD_ID, "textures/gui/knapping/leather_button.png");

    private KnappingRecipe recipe;

    public KnappingWrapper(KnappingRecipe recipe)
    {
        this.recipe = recipe;
    }

    @Override
    public void getIngredients(IIngredients ingredients)
    {
        ItemStack output = recipe.getOutput(new ItemStack(ItemRock.get(Rock.GRANITE))); //
        ingredients.setOutput(VanillaTypes.ITEM, output);
    }

    @Override
    public void drawInfo(Minecraft minecraft, int recipeWidth, int recipeHeight, int mouseX, int mouseY)
    {
        if (minecraft.currentScreen != null)
        {
            ResourceLocation high;
            ResourceLocation low;
            switch (recipe.getType())
            {
                case CLAY:
                    high = CLAY_TEXTURE;
                    low = CLAY_DISABLED_TEXTURE;
                    break;
                case FIRE_CLAY:
                    high = FIRE_CLAY_TEXTURE;
                    low = CLAY_DISABLED_TEXTURE;
                    break;
                case LEATHER:
                    high = LEATHER_TEXTURE;
                    low = null;
                    break;
                default:
                    high = Rock.GRANITE.getTexture();
                    low = null;
                    break;
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
                            Gui.drawModalRectWithCustomSizedTexture(1 + x * 16, 1 + y * 16, 12, 12, 16, 16, 256, 256);
                        }
                    }
                }
            }

        }
    }
}
