/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.capabilities.forge;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;

import net.dries007.tfc.util.Helpers;
import org.jetbrains.annotations.Nullable;

public class ForgingCapability
{
    public static final Capability<Forging> CAPABILITY = CapabilityManager.get(new CapabilityToken<>() {});
    public static final ResourceLocation KEY = Helpers.identifier("forging");

    @Nullable
    public static Forging get(ItemStack stack)
    {
        return stack.getCapability(CAPABILITY).resolve().orElse(null);
    }
}