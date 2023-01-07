package net.dries007.tfc.common.capabilities.power;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityToken;

import net.dries007.tfc.util.Helpers;

public final class RotationCapability
{
    public static final Capability<IRotationProvider> ROTATION = Helpers.capability(new CapabilityToken<>() {});
    public static final ResourceLocation KEY = Helpers.identifier("rotation");
}
