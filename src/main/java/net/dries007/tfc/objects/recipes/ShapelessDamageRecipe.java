/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.recipes;

import javax.annotation.Nonnull;

import com.google.gson.JsonObject;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.IRecipeFactory;
import net.minecraftforge.common.crafting.JsonContext;
import net.minecraftforge.oredict.ShapelessOreRecipe;

/**
 * Much of this is borrowed from https://github.com/Choonster-Minecraft-Mods/TestMod3/blob/d064915183a4a3b803d779576f982279268b1ca3/src/main/java/choonster/testmod3/crafting/recipe/ShapelessCuttingRecipe.java
 */
@SuppressWarnings("unused")
public class ShapelessDamageRecipe extends ShapelessOreRecipe
{
    private final int damage;

    public ShapelessDamageRecipe(ResourceLocation group, NonNullList<Ingredient> input, @Nonnull ItemStack result, int damage)
    {
        super(group, input, result);
        this.isSimple = false;
        this.damage = damage;
    }

    public NonNullList<ItemStack> getRemainingItemsDamaged(final InventoryCrafting inv)
    {
        final NonNullList<ItemStack> remainingItems = NonNullList.withSize(inv.getSizeInventory(), ItemStack.EMPTY);
        for (int i = 0; i < remainingItems.size(); ++i)
        {
            final ItemStack itemstack = inv.getStackInSlot(i);

            // If the stack isn't empty and the stack is damageable we can damage it, otherwise delegate to containerItem.
            if (!itemstack.isEmpty() && itemstack.getItem().isDamageable())
            {
                remainingItems.set(i, damageStack(itemstack));
            }
            else
            {
                remainingItems.set(i, ForgeHooks.getContainerItem(itemstack));
            }
        }
        return remainingItems;
    }

    @Override
    @Nonnull
    public NonNullList<ItemStack> getRemainingItems(final InventoryCrafting inventoryCrafting)
    {
        return getRemainingItemsDamaged(inventoryCrafting);
    }

    @Override
    @Nonnull
    public String getGroup()
    {
        return group == null ? "" : group.toString();
    }

    private ItemStack damageStack(ItemStack stack)
    {
        ItemStack damagedStack = stack.copy();
        damagedStack.damageItem(damage, ForgeHooks.getCraftingPlayer());

        return damagedStack;
    }

    @SuppressWarnings("unused")
    public static class Factory implements IRecipeFactory
    {
        @Override
        public IRecipe parse(final JsonContext context, final JsonObject json)
        {
            final String group = JsonUtils.getString(json, "group", "");
            final NonNullList<Ingredient> ingredients = RecipeUtils.parseShapeless(context, json);
            final ItemStack result = CraftingHelper.getItemStack(JsonUtils.getJsonObject(json, "result"), context);
            final int damage;
            if (JsonUtils.hasField(json, "damage"))
                damage = JsonUtils.getInt(json, "damage");
            else damage = 1;

            return new ShapelessDamageRecipe(group.isEmpty() ? null : new ResourceLocation(group), ingredients, result, damage);
        }
    }
}
