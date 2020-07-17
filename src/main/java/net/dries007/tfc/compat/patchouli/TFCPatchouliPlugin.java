/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.compat.patchouli;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.oredict.OreIngredient;

import net.dries007.tfc.TerraFirmaCraft;
import net.dries007.tfc.objects.inventory.ingredient.IIngredient;
import net.dries007.tfc.objects.inventory.ingredient.IngredientItemStack;
import net.dries007.tfc.objects.inventory.ingredient.IngredientOreDict;
import vazkii.patchouli.api.PatchouliAPI;

import static net.dries007.tfc.TerraFirmaCraft.MOD_ID;

public class TFCPatchouliPlugin
{
    public static final ResourceLocation BOOK_UTIL_TEXTURES = new ResourceLocation(MOD_ID, "textures/gui/book.png");

    private static PatchouliAPI.IPatchouliAPI API = null;

    public static Ingredient getIngredient(IIngredient<ItemStack> ingredient)
    {
        if (ingredient instanceof IngredientItemStack)
        {
            return Ingredient.fromStacks(ingredient.getValidIngredients().get(0));
        }
        else if (ingredient instanceof IngredientOreDict)
        {
            return new OreIngredient(((IngredientOreDict) ingredient).getOreName());
        }
        else
        {
            return Ingredient.EMPTY;
        }
    }

    public static void giveBookToPlayer(EntityPlayer player)
    {
        ItemStack bookStack = getAPI().getBookStack(new ResourceLocation(MOD_ID, "book").toString());
        ItemHandlerHelper.giveItemToPlayer(player, bookStack);
    }

    private static PatchouliAPI.IPatchouliAPI getAPI()
    {
        if (API == null)
        {
            API = PatchouliAPI.instance;
            if (API.isStub())
            {
                TerraFirmaCraft.getLog().warn("Failed to intercept Patchouli API. Problems may occur");
            }
        }
        return API;
    }
}
