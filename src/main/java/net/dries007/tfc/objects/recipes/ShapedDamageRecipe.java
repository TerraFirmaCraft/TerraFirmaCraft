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
import net.minecraft.util.JsonUtils;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.IRecipeFactory;
import net.minecraftforge.common.crafting.JsonContext;
import net.minecraftforge.oredict.ShapedOreRecipe;

/**
 * Much of this is borrowed from https://github.com/Choonster-Minecraft-Mods/TestMod3/blob/d064915183a4a3b803d779576f982279268b1ca3/src/main/java/choonster/testmod3/crafting/recipe/ShapelessCuttingRecipe.java
 */
@SuppressWarnings("unused")
public class ShapedDamageRecipe extends ShapedOreRecipe
{
    private final int damage;

    public ShapedDamageRecipe(ResourceLocation group, CraftingHelper.ShapedPrimer input, @Nonnull ItemStack result, int damage)
    {
        super(group, result, input);
        this.damage = damage;
    }

    @Override
    @Nonnull
    public NonNullList<ItemStack> getRemainingItems(final InventoryCrafting inventoryCrafting)
    {
        final NonNullList<ItemStack> remainingItems = NonNullList.withSize(inventoryCrafting.getSizeInventory(), ItemStack.EMPTY);

        for (int i = 0; i < remainingItems.size(); ++i)
        {
            final ItemStack itemstack = inventoryCrafting.getStackInSlot(i);

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
    public String getGroup()
    {
        return group == null ? "" : group.toString();
    }

    // We need to damage the stack but damageItem will not return an item.
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
            String group = JsonUtils.getString(json, "group", "");

            CraftingHelper.ShapedPrimer primer = RecipeUtils.parsePhaped(context, json);

            ItemStack result = CraftingHelper.getItemStack(JsonUtils.getJsonObject(json, "result"), context);
            final int damage;
            if (JsonUtils.hasField(json, "damage"))
                damage = JsonUtils.getInt(json, "damage");
            else damage = 1;
            return new ShapedDamageRecipe(group.isEmpty() ? null : new ResourceLocation(group), primer, result, damage);
        }
    }
}
