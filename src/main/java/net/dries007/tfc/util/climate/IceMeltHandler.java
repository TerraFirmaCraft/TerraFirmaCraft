/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.util.climate;

import java.util.Iterator;
import java.util.Random;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldServer;
import net.minecraft.world.WorldType;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import static net.dries007.tfc.TerraFirmaCraft.MOD_ID;

/**
 * Vanilla ice melting is hardcoded to the world. However, we can replicate most of the behavior by watching world ticks, and performing the same simple logic checks
 */
@Mod.EventBusSubscriber(modid = MOD_ID)
public class IceMeltHandler
{
    public static final float ICE_MELT_THRESHOLD = 0f;
    public static final float WATER_FREEZE_THRESHOLD = -4f;
    public static final float SALT_WATER_FREEZE_THRESHOLD = -8f;
    public static final float SALT_WATER_MELT_THRESHOLD = -4f;

    /**
     * Duplicated field from world, idk how this really works or the merits to it, but it should function the same as vanilla
     */
    private static int updateLCG = new Random().nextInt();

    @SubscribeEvent
    public static void onWorldTick(TickEvent.WorldTickEvent event)
    {
        if (event.world instanceof WorldServer && event.phase == TickEvent.Phase.END)
        {
            final WorldServer world = (WorldServer) event.world;
            world.profiler.startSection("tfciceandsnow");
            if (world.getWorldInfo().getTerrainType() != WorldType.DEBUG_ALL_BLOCK_STATES)
            {
                for (Iterator<Chunk> iterator = world.getPersistentChunkIterable(world.getPlayerChunkMap().getChunkIterator()); iterator.hasNext(); )
                {
                    Chunk chunk = iterator.next();
                    int chunkX = chunk.x * 16;
                    int chunkZ = chunk.z * 16;

                    if (world.provider.canDoRainSnowIce(chunk) && world.rand.nextInt(16) == 0)
                    {
                        updateLCG = updateLCG * 3 + 1013904223;
                        int randomSeed = updateLCG >> 2;
                        BlockPos pos = world.getPrecipitationHeight(new BlockPos(chunkX + (randomSeed & 15), 0, chunkZ + (randomSeed >> 8 & 15))).down();

                        if (world.isAreaLoaded(pos, 1))
                        {
                            IBlockState state = world.getBlockState(pos);
                            if (state.getBlock() instanceof ITemperatureBlock)
                            {
                                ((ITemperatureBlock) state.getBlock()).onTemperatureUpdateTick(world, pos, state);
                            }

                            // Also check the above block - snow layers are missed by the before check
                            pos = pos.up();
                            state = world.getBlockState(pos);
                            if (state.getBlock() instanceof ITemperatureBlock)
                            {
                                ((ITemperatureBlock) state.getBlock()).onTemperatureUpdateTick(world, pos, state);
                            }
                        }
                    }
                }
            }
            world.profiler.endSection();
        }
    }
}
