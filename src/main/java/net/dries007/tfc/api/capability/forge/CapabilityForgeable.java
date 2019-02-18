/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.api.capability.forge;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;

import net.dries007.tfc.api.capability.DumbStorage;
import net.dries007.tfc.util.Helpers;

public final class CapabilityForgeable
{
    @CapabilityInject(IForgeable.class)
    public static final Capability<IForgeable> FORGEABLE_CAPABILITY = Helpers.getNull();

    public static void preInit()
    {
        CapabilityManager.INSTANCE.register(IForgeable.class, new DumbStorage<>(), ForgeableHandler::new);
    }
}
