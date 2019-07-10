/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.recipes;

import javax.annotation.Nonnull;

import com.google.gson.JsonObject;
import net.minecraft.entity.player.EntityPlayer;
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

public class ShapelessDamageAddItemRecipe extends ShapelessOreRecipe
{
    private ItemStack out;
    private int damage;

    public ShapelessDamageAddItemRecipe(ResourceLocation group, NonNullList<Ingredient> input, @Nonnull ItemStack result, @Nonnull ItemStack output, int damage)
    {
        super(group, input, result);
        this.isSimple = false;
        this.out = output;
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

        EntityPlayer player = ForgeHooks.getCraftingPlayer();
        if (player != null)
        {
            player.addItemStackToInventory(out.copy());
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

    public static class Factory implements IRecipeFactory
    {
        @Override
        public IRecipe parse(final JsonContext context, final JsonObject json)
        {
            final String group = JsonUtils.getString(json, "group", "");
            final NonNullList<Ingredient> ingredients = RecipeUtils.parseShapeless(context, json);
            final ItemStack result = CraftingHelper.getItemStack(JsonUtils.getJsonObject(json, "result"), context);
            final ItemStack output = CraftingHelper.getItemStack(JsonUtils.getJsonObject(json, "output"), context);
            final int damage = JsonUtils.getInt(json, "damage");

            return new ShapelessDamageAddItemRecipe(group.isEmpty() ? null : new ResourceLocation(group), ingredients, result, output, damage);
        }
    }
}
