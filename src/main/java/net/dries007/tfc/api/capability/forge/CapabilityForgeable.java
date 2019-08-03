/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.api.capability.forge;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;
import javax.annotation.Nullable;

import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.ICapabilityProvider;

import net.dries007.tfc.api.capability.DumbStorage;
import net.dries007.tfc.objects.inventory.ingredient.IIngredient;
import net.dries007.tfc.util.Helpers;

import static net.dries007.tfc.api.util.TFCConstants.MOD_ID;

public final class CapabilityForgeable
{
    @CapabilityInject(IForgeable.class)
    public static final Capability<IForgeable> FORGEABLE_CAPABILITY = Helpers.getNull();
    public static final ResourceLocation KEY = new ResourceLocation(MOD_ID, "item_forge");

    public static final Map<IIngredient<ItemStack>, Supplier<ICapabilityProvider>> CUSTOM_ITEMS = new HashMap<>(); //Used inside CT, set custom IForgeable for items outside TFC

    public static void preInit()
    {
        CapabilityManager.INSTANCE.register(IForgeable.class, new DumbStorage<>(), ForgeableHandler::new);
    }

    @Nullable
    public static ICapabilityProvider getCustomForgeable(ItemStack stack)
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
