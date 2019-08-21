/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.api.capability.metal;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;
import javax.annotation.Nullable;

import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.oredict.OreDictionary;

import net.dries007.tfc.api.capability.DumbStorage;
import net.dries007.tfc.api.registries.TFCRegistries;
import net.dries007.tfc.api.types.Metal;
import net.dries007.tfc.api.util.TFCConstants;
import net.dries007.tfc.objects.inventory.ingredient.IIngredient;
import net.dries007.tfc.util.Helpers;

public final class CapabilityMetalItem
{
    @CapabilityInject(IMetalItem.class)
    public static final Capability<IMetalItem> METAL_OBJECT_CAPABILITY = Helpers.getNull();
    public static final ResourceLocation KEY = new ResourceLocation(TFCConstants.MOD_ID, "metal_object");

    public static final Map<IIngredient<ItemStack>, Supplier<ICapabilityProvider>> CUSTOM_ITEMS = new HashMap<>(); //Used inside CT, set custom IItemSize for items outside TFC

    public static void preInit()
    {
        CapabilityManager.INSTANCE.register(IMetalItem.class, new DumbStorage<>(), MetalItemHandler::new);
    }

    /**
     * Gets the IMetalItem instance from an itemstack, either via capability or via interface
     *
     * @param stack The stack
     * @return The IMetalItem if it exists, or null if it doesn't
     */
    @Nullable
    public static IMetalItem getMetalItem(ItemStack stack)
    {
        if (!stack.isEmpty())
        {
            if (stack.getItem() instanceof IMetalItem)
            {
                return (IMetalItem) stack.getItem();
            }
            else if (stack.getItem() instanceof ItemBlock && ((ItemBlock) stack.getItem()).getBlock() instanceof IMetalItem)
            {
                return (IMetalItem) ((ItemBlock) stack.getItem()).getBlock();
            }
            return stack.getCapability(METAL_OBJECT_CAPABILITY, null);
        }
        return null;
    }

    @Nullable
    public static ICapabilityProvider getCustomMetalItem(ItemStack stack)
    {
        if (!stack.isEmpty())
        {
            Set<IIngredient<ItemStack>> itemItemSet = CUSTOM_ITEMS.keySet();
            for (IIngredient<ItemStack> ingredient : itemItemSet)
            {
                if (ingredient.testIgnoreCount(stack))
                {
                    return CUSTOM_ITEMS.get(ingredient).get();
                }
            }
            //Try using ore dict prefix-suffix common values (ie: ingotCopper)
            int[] ids = OreDictionary.getOreIDs(stack);
            for (int id : ids)
            {
                ICapabilityProvider handler = getMetalItemFromOreDict(OreDictionary.getOreName(id));
                if (handler != null)
                {
                    return handler;
                }
            }
        }
        return null;
    }

    @Nullable
    private static ICapabilityProvider getMetalItemFromOreDict(String oreDict)
    {
        Metal.ItemType type;
        String remaining;
        int amount;
        if (oreDict.startsWith("ingot"))
        {
            type = Metal.ItemType.INGOT;
            remaining = oreDict.substring(5);
            amount = 100;
        }
        else if (oreDict.startsWith("nugget"))
        {
            type = Metal.ItemType.NUGGET;
            remaining = oreDict.substring(6);
            amount = 10;
        }
        else
        {
            return null;
        }
        //noinspection ConstantConditions
        Metal output = TFCRegistries.METALS.getValuesCollection().stream().filter(metal -> metal.getRegistryName().getPath().equalsIgnoreCase(remaining)).findFirst().orElse(null);
        if (output != null)
        {
            return new MetalItemHandler(output, amount, true);
        }
        return null;
    }
}
