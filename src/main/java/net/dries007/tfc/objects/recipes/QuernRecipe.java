/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.recipes;

import javax.annotation.Nonnull;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.block.Block;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.IRecipeFactory;
import net.minecraftforge.common.crafting.JsonContext;
import net.minecraftforge.registries.IForgeRegistryEntry;

@SuppressWarnings("unused")
public class QuernRecipe extends IForgeRegistryEntry.Impl<IRecipe> implements IRecipe
{
    @Nonnull
    protected ItemStack output;
    protected Ingredient input;
    protected ResourceLocation group;

    public QuernRecipe(ResourceLocation group, Block result, Object... recipe) { this(group, new ItemStack(result), recipe); }

    public QuernRecipe(ResourceLocation group, Item result, Object... recipe) { this(group, new ItemStack(result), recipe); }

    public QuernRecipe(ResourceLocation group, Ingredient input, @Nonnull ItemStack result)
    {
        this.group = group;
        output = result.copy();
        this.input = input;
    }

    @SuppressWarnings("StringConcatenationInLoop")
    public QuernRecipe(ResourceLocation group, @Nonnull ItemStack result, Object... recipe)
    {
        this.group = group;
        output = result.copy();
        for (Object in : recipe)
        {
            Ingredient ing = CraftingHelper.getIngredient(in);
            if (ing != null)
            {
                input = ing;
            }
            else
            {
                String ret = "Invalid quern recipe: ";
                for (Object tmp : recipe)
                {
                    ret += tmp + ", ";
                }
                ret += output;
                throw new RuntimeException(ret);
            }
        }
    }

    @Override
    public boolean matches(@Nonnull InventoryCrafting inv, @Nonnull World world)
    {
        return inv.getClass().getSimpleName().equals("ContainerQuern");
    }

    @Override
    @Nonnull
    public ItemStack getCraftingResult(@Nonnull InventoryCrafting var1) { return output.copy(); }

    @Override
    public boolean canFit(int width, int height)
    {
        return true;
    }

    @Override
    @Nonnull
    public ItemStack getRecipeOutput() { return output; }

    public static class Factory implements IRecipeFactory
    {
        public IRecipe parse(final JsonContext context, final JsonObject json)
        {
            String group = JsonUtils.getString(json, "group", "");

            Ingredient ingredient = (CraftingHelper.getIngredient(JsonUtils.getJsonObject(json, "ingredient"), context));
            if (ingredient == Ingredient.EMPTY)
                throw new JsonParseException("No ingredient for quern recipe");

            ItemStack output = CraftingHelper.getItemStack(JsonUtils.getJsonObject(json, "result"), context);
            if (output == ItemStack.EMPTY)
                throw new JsonParseException("No result for quern recipe");

            float experience = JsonUtils.getFloat(json, "experience", 0.1f);

            return QuernRecipeManager.getInstance().addGrindingRecipe(group, ingredient, output, experience);
        }
    }
}