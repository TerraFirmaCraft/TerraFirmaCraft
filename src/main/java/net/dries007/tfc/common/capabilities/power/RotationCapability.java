/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.capabilities.power;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityToken;

import net.dries007.tfc.util.Helpers;

public final class RotationCapability
{
    public static final Capability<IRotator> ROTATION = Helpers.capability(new CapabilityToken<>() {});
    public static final ResourceLocation KEY = Helpers.identifier("rotation");
}
