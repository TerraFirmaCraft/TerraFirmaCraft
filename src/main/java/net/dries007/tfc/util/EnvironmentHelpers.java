package net.dries007.tfc.util;

import java.util.Random;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.SnowBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.server.ServerWorld;

import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.blocks.SnowPileBlock;
import net.dries007.tfc.common.blocks.TFCBlocks;

/**
 * This is a stupid helper class until https://github.com/MinecraftForge/MinecraftForge/pull/7235 is merged and this can be moved to {@link net.dries007.tfc.ForgeEventHandler}
 */
public final class EnvironmentHelpers
{
    /**
     * When snowing, perform two additional changes:
     * - Snow or snow piles should stack up to 7 high
     * - Convert possible blocks to snow piles
     */
    public static void onEnvironmentTick(ServerWorld world, Chunk chunkIn, Random random)
    {
        ChunkPos chunkPos = chunkIn.getPos();
        if (random.nextInt(16) == 0)
        {
            BlockPos pos = world.getHeightmapPos(Heightmap.Type.MOTION_BLOCKING, world.getBlockRandomPos(chunkPos.getMinBlockX(), 0, chunkPos.getMinBlockZ(), 15));
            if (world.isAreaLoaded(pos, 1) && world.isRaining() && Climate.getTemperature(world, pos) < Climate.SNOW_STACKING_TEMPERATURE)
            {
                if (!tryStackSnow(world, pos, world.getBlockState(pos)))
                {
                    pos = pos.below();
                    tryStackSnow(world, pos, world.getBlockState(pos));
                }
            }
        }
    }

    private static boolean tryStackSnow(IWorld world, BlockPos pos, BlockState state)
    {
        if ((state.is(Blocks.SNOW) || state.is(TFCBlocks.SNOW_PILE.get())) && state.getValue(SnowBlock.LAYERS) < 7)
        {
            // Vanilla snow block stacking
            world.setBlock(pos, state.setValue(SnowBlock.LAYERS, state.getValue(SnowBlock.LAYERS) + 1), 3);
        }
        else if (TFCTags.Blocks.CAN_BE_SNOW_PILED.contains(state.getBlock()))
        {
            // Other snow block stacking
            SnowPileBlock.convertToPile(world, pos, state);
        }
        else
        {
            return false;
        }
        return true;
    }
}
