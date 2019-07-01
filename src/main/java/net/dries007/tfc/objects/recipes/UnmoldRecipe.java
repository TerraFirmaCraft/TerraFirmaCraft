/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.recipes;

import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import com.google.common.collect.Lists;
import com.google.gson.JsonObject;
import net.minecraft.client.util.RecipeItemHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.crafting.IRecipeFactory;
import net.minecraftforge.common.crafting.JsonContext;
import net.minecraftforge.oredict.ShapelessOreRecipe;

import net.dries007.tfc.Constants;
import net.dries007.tfc.api.capability.IMoldHandler;
import net.dries007.tfc.api.capability.heat.IItemHeat;
import net.dries007.tfc.api.types.Metal;
import net.dries007.tfc.objects.items.metal.ItemMetal;

import static net.dries007.tfc.api.capability.heat.CapabilityItemHeat.ITEM_HEAT_CAPABILITY;
import static net.minecraftforge.fluids.capability.CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY;

@ParametersAreNonnullByDefault
public class UnmoldRecipe extends ShapelessOreRecipe
{
    int moldSlotID;
    IMoldHandler moldHandler;
    Metal.ItemType type;
    float chance;

    public UnmoldRecipe(ResourceLocation group, NonNullList<Ingredient> input, @Nonnull Metal.ItemType type, float chance)
    {
        super(group, input, ItemStack.EMPTY);
        this.type = type;
        this.chance = chance;
    }

    @Override
    @Nonnull
    public NonNullList<ItemStack> getRemainingItems(final InventoryCrafting inventoryCrafting)
    {
        if (Constants.RNG.nextFloat() <= chance)
        {
            EntityPlayer player = ForgeHooks.getCraftingPlayer();
            if (player != null)
            {
                player.addItemStackToInventory(new ItemStack(inventoryCrafting.getStackInSlot(moldSlotID).getItem()));
            }
        }

        return super.getRemainingItems(inventoryCrafting);
    }

    @Override
    @Nonnull
    public String getGroup()
    {
        return group == null ? "" : group.toString();
    }

    @Override
    @Nonnull
    public ItemStack getRecipeOutput() { return ItemStack.EMPTY; }

    @Override
    public boolean matches(@Nonnull InventoryCrafting inv, @Nonnull World world)
    {
        int ingredientCount = 0;
        RecipeItemHelper recipeItemHelper = new RecipeItemHelper();
        List<ItemStack> items = Lists.newArrayList();

        for (int i = 0; i < inv.getSizeInventory(); ++i)
        {
            ItemStack itemstack = inv.getStackInSlot(i);
            if (!itemstack.isEmpty())
            {
                ++ingredientCount;
                if (this.isSimple)
                    recipeItemHelper.accountStack(itemstack, 1);
                else
                    items.add(itemstack);

                moldSlotID = i;
            }
        }

        if (ingredientCount != this.input.size())
            return false;

        boolean canCraft = recipeItemHelper.canCraft(this, null);

        if (canCraft)
        {
            ItemStack itemstack = inv.getStackInSlot(moldSlotID);
            IMoldHandler moldHandler = ((IMoldHandler) itemstack.getCapability(FLUID_HANDLER_CAPABILITY, null));
            this.moldHandler = moldHandler;
            if (moldHandler.getMetal() == null || moldHandler.getAmount() != 100 || moldHandler.isMolten())
                return false;
        }

        return canCraft;
    }

    @Override
    @Nonnull
    public ItemStack getCraftingResult(InventoryCrafting inv)
    {
        return getOutputItem(moldHandler, type);
    }

    @Override
    public boolean canFit(int width, int height)
    {
        return true;
    }

    @Override
    public boolean isDynamic()
    {
        return true;
    }

    public ItemStack getOutputItem(final IMoldHandler moldHandler, final Metal.ItemType type)
    {
        Metal m = moldHandler.getMetal();
        if (m != null)
        {
            ItemStack output = new ItemStack(ItemMetal.get(m, type));
            IItemHeat heat = output.getCapability(ITEM_HEAT_CAPABILITY, null);
            if (heat != null)
            {
                heat.setTemperature(moldHandler.getTemperature());
            }
            return output;
        }
        return ItemStack.EMPTY;
    }

    @SuppressWarnings("unused")
    public static class Factory implements IRecipeFactory
    {
        @Override
        public IRecipe parse(final JsonContext context, final JsonObject json)
        {
            final NonNullList<Ingredient> ingredients = RecipeUtils.parseShapeless(context, json);
            final String result = JsonUtils.getString(json, "result");
            final Metal.ItemType type = Metal.ItemType.valueOf(result.toUpperCase());
            final String group = JsonUtils.getString(json, "group", "");

            //Chance of getting the mold back
            float chance = 0;
            if (JsonUtils.hasField(json, "chance"))
            {
                chance = JsonUtils.getFloat(json, "chance");
            }


            return new UnmoldRecipe(group.isEmpty() ? new ResourceLocation(result) : new ResourceLocation(group), ingredients, type, chance);
        }
    }
}
