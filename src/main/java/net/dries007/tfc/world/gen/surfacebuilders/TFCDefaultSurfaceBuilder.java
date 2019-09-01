/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.world.gen.surfacebuilders;

import java.util.Random;
import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.gen.surfacebuilders.SurfaceBuilder;

@ParametersAreNonnullByDefault
public class TFCDefaultSurfaceBuilder extends SurfaceBuilder<TFCSurfaceBuilderConfig>
{
    private long seed;

    public TFCDefaultSurfaceBuilder()
    {
        super(TFCSurfaceBuilderConfig::deserialize);

        this.seed = 0;
    }

    @Override
    public void buildSurface(Random random, IChunk chunkIn, Biome biomeIn, int x, int z, int startHeight, double noise, BlockState defaultBlock, BlockState defaultFluid, int seaLevel, long seed, TFCSurfaceBuilderConfig config)
    {
        BlockState stateUnder = config.getUnder();
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();

        int surfaceFlag = -1;
        int chunkX = x & 15;
        int chunkZ = z & 15;

        for (int y = startHeight; y >= 0 && surfaceFlag != 0; y--)
        {
            pos.setPos(chunkX, y, chunkZ);
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
                    if (y >= seaLevel)
                    {
                        // Above water, just hit surface
                        surfaceFlag = config.getSoilLayers();
                        stateUnder = config.getUnder();
                        chunkIn.setBlockState(pos, config.getTop(), false);
                    }
                    else
                    {
                        surfaceFlag = 1;
                        stateUnder = config.getUnderWaterMaterial();
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

    @Override
    public void setSeed(long seed)
    {
        this.seed = seed;
    }
}
