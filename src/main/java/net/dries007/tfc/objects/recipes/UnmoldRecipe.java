/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.recipes;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import com.google.gson.JsonObject;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.crafting.IRecipeFactory;
import net.minecraftforge.common.crafting.JsonContext;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.registries.IForgeRegistryEntry;

import net.dries007.tfc.Constants;
import net.dries007.tfc.api.capability.IMoldHandler;
import net.dries007.tfc.api.capability.heat.IItemHeat;
import net.dries007.tfc.api.types.Metal;
import net.dries007.tfc.client.TFCSounds;
import net.dries007.tfc.objects.items.ceramics.ItemMold;
import net.dries007.tfc.objects.items.metal.ItemMetal;

import static net.dries007.tfc.api.capability.heat.CapabilityItemHeat.ITEM_HEAT_CAPABILITY;
import static net.minecraftforge.fluids.capability.CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY;

@SuppressWarnings("unused")
@ParametersAreNonnullByDefault
public class UnmoldRecipe extends IForgeRegistryEntry.Impl<IRecipe> implements IRecipe
{
    private final NonNullList<Ingredient> input;
    private final ResourceLocation group;
    private final Metal.ItemType type;
    private final float chance; // Return chance

    private UnmoldRecipe(@Nullable ResourceLocation group, NonNullList<Ingredient> input, @Nonnull Metal.ItemType type, float chance)
    {
        this.group = group;
        this.input = input;
        this.type = type;
        this.chance = chance;
    }

    @Override
    public boolean matches(@Nonnull InventoryCrafting inv, @Nonnull World world)
    {
        boolean foundMold = false;
        for (int slot = 0; slot < inv.getSizeInventory(); slot++)
        {
            ItemStack stack = inv.getStackInSlot(slot);
            if (!stack.isEmpty())
            {
                if (stack.getItem() instanceof ItemMold)
                {
                    ItemMold moldItem = ((ItemMold) stack.getItem());
                    IFluidHandler cap = stack.getCapability(FLUID_HANDLER_CAPABILITY, null);

                    if (cap instanceof IMoldHandler)
                    {
                        IMoldHandler moldHandler = (IMoldHandler) cap;
                        if (!moldHandler.isMolten())
                        {
                            Metal metal = moldHandler.getMetal();
                            if (metal != null && moldItem.getType().equals(this.type) && !foundMold)
                            {
                                foundMold = true;
                            }
                            else
                            {
                                return false;
                            }
                        }
                        else
                        {
                            return false;
                        }
                    }
                    else
                    {
                        return false;
                    }
                }
                else
                {
                    return false;
                }
            }
        }
        return foundMold;
    }

    @Override
    @Nonnull
    public ItemStack getCraftingResult(InventoryCrafting inv)
    {
        ItemStack moldStack = null;
        for (int slot = 0; slot < inv.getSizeInventory(); slot++)
        {
            ItemStack stack = inv.getStackInSlot(slot);
            if (!stack.isEmpty())
            {
                if (stack.getItem() instanceof ItemMold)
                {
                    ItemMold tmp = ((ItemMold) stack.getItem());
                    if (tmp.getType().equals(this.type) && moldStack == null)
                    {
                        moldStack = stack;
                    }
                    else
                    {
                        return ItemStack.EMPTY;
                    }
                }
                else
                {
                    return ItemStack.EMPTY;
                }
            }
        }
        if (moldStack != null)
        {
            IFluidHandler moldCap = moldStack.getCapability(FLUID_HANDLER_CAPABILITY, null);
            if (moldCap instanceof IMoldHandler)
            {
                IMoldHandler moldHandler = (IMoldHandler) moldCap;
                if (!moldHandler.isMolten() && moldHandler.getAmount() == 100)
                {
                    return getOutputItem(moldHandler);
                }
            }
        }
        return ItemStack.EMPTY;
    }

    @Override
    public boolean canFit(int width, int height)
    {
        return true;
    }

    @Override
    @Nonnull
    public ItemStack getRecipeOutput()
    {
        return ItemStack.EMPTY;
    }

    @Override
    @Nonnull
    public NonNullList<ItemStack> getRemainingItems(final InventoryCrafting inv)
    {
        // Return empty molds
        for (int slot = 0; slot < inv.getSizeInventory(); slot++)
        {
            ItemStack stack = inv.getStackInSlot(slot);
            if (!stack.isEmpty())
            {
                if (stack.getItem() instanceof ItemMold)
                {
                    // No need to check for the mold, as it has already been checked earlier
                    EntityPlayer player = ForgeHooks.getCraftingPlayer();
                    if (!player.world.isRemote)
                    {
                        stack = getMoldResult(stack);
                        if (!stack.isEmpty())
                        {
                            // This can't use the remaining items, because vanilla doesn't sync them on crafting, thus it gives a desync error
                            // To fix: ContainerWorkbench#onCraftMatrixChanged needs to call Container#detectAndSendChanges
                            ItemHandlerHelper.giveItemToPlayer(player, stack);
                        }
                        else
                        {
                            player.world.playSound(null, player.getPosition(), TFCSounds.CERAMIC_BREAK, SoundCategory.PLAYERS, 1.0f, 1.0f);
                        }
                    }
                }
            }
        }
        return ForgeHooks.defaultRecipeGetRemainingItems(inv);
    }

    @Override
    @Nonnull
    public NonNullList<Ingredient> getIngredients()
    {
        return input;
    }

    @Override
    public boolean isDynamic()
    {
        return true;
    }

    @Override
    @Nonnull
    public String getGroup()
    {
        return group == null ? "" : group.toString();
    }

    public Metal.ItemType getType()
    {
        return type;
    }

    public float getChance()
    {
        return chance;
    }

    /**
     * Performs breaking check
     *
     * @param moldIn the mold to do a breaking check
     * @return ItemStack.EMPTY on break, the mold (empty) if pass
     */
    public ItemStack getMoldResult(ItemStack moldIn)
    {
        if (Constants.RNG.nextFloat() <= chance)
        {
            return new ItemStack(moldIn.getItem());
        }
        else
        {
            return ItemStack.EMPTY;
        }
    }

    public ItemStack getOutputItem(final IMoldHandler moldHandler)
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
