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

import net.dries007.tfc.api.capability.DumbStorage;
import net.dries007.tfc.api.util.TFCConstants;
import net.dries007.tfc.objects.inventory.ingredient.IIngredient;
import net.dries007.tfc.util.Helpers;

public final class CapabilityMetalObject
{
    @CapabilityInject(IMetalObject.class)
    public static final Capability<IMetalObject> METAL_OBJECT_CAPABILITY = Helpers.getNull();
    public static final ResourceLocation KEY = new ResourceLocation(TFCConstants.MOD_ID, "metal_object");

    public static final Map<IIngredient<ItemStack>, Supplier<ICapabilityProvider>> CUSTOM_ITEMS = new HashMap<>(); //Used inside CT, set custom IItemSize for items outside TFC

    public static void preInit()
    {
        CapabilityManager.INSTANCE.register(IMetalObject.class, new DumbStorage<>(), MetalObjectHandler::new);
    }

    /**
     * Gets the IMetalObject instance from an itemstack, either via capability or via interface
     *
     * @param stack The stack
     * @return The IMetalObject if it exists, or null if it doesn't
     */
    @Nullable
    public static IMetalObject getMetalObject(ItemStack stack)
    {
        if (!stack.isEmpty())
        {
            if (stack.getItem() instanceof IMetalObject)
            {
                return (IMetalObject) stack.getItem();
            }
            else if (stack.getItem() instanceof ItemBlock && ((ItemBlock) stack.getItem()).getBlock() instanceof IMetalObject)
            {
                return (IMetalObject) ((ItemBlock) stack.getItem()).getBlock();
            }
            return stack.getCapability(METAL_OBJECT_CAPABILITY, null);
        }
        return null;
    }

    @Nullable
    public static ICapabilityProvider getCustomMetalObject(ItemStack stack)
    {
        Set<IIngredient<ItemStack>> itemItemSet = CUSTOM_ITEMS.keySet();
        for (IIngredient<ItemStack> ingredient : itemItemSet)
        {
            if (ingredient.testIgnoreCount(stack))
            {
                return CUSTOM_ITEMS.get(ingredient).get();
            }
        }
        return null;
    }
}
