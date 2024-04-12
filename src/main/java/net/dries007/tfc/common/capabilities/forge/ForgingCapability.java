/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.capabilities.forge;

import java.util.List;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.util.Helpers;

public class ForgingCapability
{
    public static final Capability<Forging> CAPABILITY = CapabilityManager.get(new CapabilityToken<>() {});
    public static final ResourceLocation KEY = Helpers.identifier("forging");

    @Nullable
    public static Forging get(ItemStack stack)
    {
        return Helpers.getCapability(stack, CAPABILITY);
    }

    public static void addTooltipInfo(ItemStack stack, List<Component> tooltips)
    {
        final @Nullable Forging forging = get(stack);
        if (forging != null && forging.getSteps().any())
        {
            tooltips.add(Component.translatable("tfc.tooltip.anvil_has_been_worked"));
        }
    }

    public static void clearRecipeIfNotWorked(ItemStack stack)
    {
        final @Nullable Forging forging = get(stack);
        if (forging != null)
        {
            forging.clearRecipeIfNotWorked();
        }
    }
}