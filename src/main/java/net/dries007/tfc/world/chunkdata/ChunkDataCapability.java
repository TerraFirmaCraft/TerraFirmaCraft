/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.world.chunkdata;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;

import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.NoopStorage;

import static net.dries007.tfc.TerraFirmaCraft.MOD_ID;

public class ChunkDataCapability
{
    @CapabilityInject(ChunkData.class)
    public static final Capability<ChunkData> CAPABILITY = Helpers.getNull();
    public static final ResourceLocation KEY = new ResourceLocation(MOD_ID, "chunk_data");

    public static void setup()
    {
        CapabilityManager.INSTANCE.register(ChunkData.class, new NoopStorage<>(), ChunkData::new);
    }
}
