/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.api.capability.damage;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;

import net.dries007.tfc.api.capability.DumbStorage;
import net.dries007.tfc.util.Helpers;

public final class CapabilityDamageResistance
{
    @CapabilityInject(IDamageResistance.class)
    public static final Capability<IDamageResistance> CAPABILITY = Helpers.getNull();

    public static void preInit()
    {
        CapabilityManager.INSTANCE.register(IDamageResistance.class, new DumbStorage<>(), () -> new IDamageResistance() {});
    }
}
