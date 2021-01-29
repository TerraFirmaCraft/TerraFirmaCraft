/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.capabilities.forge;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;

import net.dries007.tfc.util.Helpers;

import static net.dries007.tfc.TerraFirmaCraft.MOD_ID;

public class ForgingCapability
{
    @CapabilityInject(IForging.class)
    public static final Capability<IForging> CAPABILITY = Helpers.notNull();
    public static final ResourceLocation KEY = new ResourceLocation(MOD_ID, "item_forge");

    public static void setup()
    {
        Helpers.registerSimpleCapability(IForging.class);
    }
}