package net.dries007.tfc.world.gen.surfacebuilders;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.chunk.IChunk;

import net.dries007.tfc.api.types.Rock;
import net.dries007.tfc.world.gen.rock.RockData;

import static net.dries007.tfc.world.gen.TFCOverworldChunkGenerator.SEA_LEVEL;

public class DefaultSurfaceBuilder implements ISurfaceBuilder
{
    private final TFCSurfaceBuilderConfig config;
    private final int soilLayers;

    public DefaultSurfaceBuilder(TFCSurfaceBuilderConfig config, int soilLayers)
    {
        this.config = config;
        this.soilLayers = soilLayers;
    }

    @Override
    public void buildSurface(Random random, IChunk chunkIn, RockData data, int x, int z, int startHeight, float temperature, float rainfall, float noise)
    {
        int surfaceFlag = -1;
        int localX = x & 15;
        int localZ = z & 15;

        Block defaultBlock = data.getTopRock(localX, localZ).getBlock(Rock.BlockType.RAW);
        BlockState stateUnder = config.getUnder().get(data, localX, localZ);
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();

        for (int y = startHeight; y >= 0 && surfaceFlag != 0; y--)
        {
            pos.setPos(localX, y, localZ);
            BlockState stateAt = chunkIn.getBlockState(pos);
            if (stateAt.isAir(chunkIn, pos))
            {
                // Air, so continue downwards and wait for surface to appear
                surfaceFlag = -1;
            }
            else if (stateAt.getBlock() == defaultBlock.getBlock())
            {
                if (surfaceFlag == -1)
                {
                    if (y >= SEA_LEVEL)
                    {
                        // Above water, just hit surface
                        surfaceFlag = getSoilLayers(y, random);
                        if (surfaceFlag > 0)
                        {
                            stateUnder = config.getUnder().get(data, localX, localZ);
                            chunkIn.setBlockState(pos, config.getTop().get(data, localX, localZ), false);
                        }
                    }
                    else
                    {
                        surfaceFlag = 1;
                        stateUnder = config.getUnderWater().get(data, localX, localZ);
                        chunkIn.setBlockState(pos, stateUnder, false);
                    }
                }
                else if (surfaceFlag > 0)
                {
                    surfaceFlag--;
                    chunkIn.setBlockState(pos, stateUnder, false);
                }
            }
        }
    }

    protected int getSoilLayers(int y, Random random)
    {
        int maxHeight = 140 + random.nextInt(3) - random.nextInt(3);
        if (y > maxHeight)
        {
            return 0;
        }
        return (int) MathHelper.clamp(0.08f * (maxHeight - y) * soilLayers, 1, soilLayers);
    }
}
