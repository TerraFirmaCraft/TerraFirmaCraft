/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.api.capabilities.forge;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;

import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.NoopStorage;

import static net.dries007.tfc.TerraFirmaCraft.MOD_ID;

public class CapabilityForging
{
    public static final ResourceLocation KEY = new ResourceLocation(MOD_ID, "item_forge");
    @CapabilityInject(IForging.class)
    public static Capability<IForging> CAPABILITY = Helpers.notNull();

    public static void setup()
    {
        CapabilityManager.INSTANCE.register(IForging.class, new NoopStorage<>(), ForgingHandler::new);
    }
}
