/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.world;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;

import net.dries007.tfc.common.blocks.TFCBlocks;
import net.dries007.tfc.common.blocks.soil.SoilBlockType;
import net.dries007.tfc.common.blocks.soil.TFCGrassBlock;
import net.dries007.tfc.world.chunkdata.RockData;
import net.dries007.tfc.world.noise.INoise2D;
import net.dries007.tfc.world.noise.SimplexNoise2D;

public class SoilBlockReplacer extends SeedBlockReplacer
{
    private final SoilBlockType soil;
    private INoise2D patchNoise;

    public SoilBlockReplacer(SoilBlockType soil)
    {
        this.soil = soil;
    }

    @Override
    public BlockState getReplacement(RockData rockData, int x, int y, int z, float rainfall, float temperature)
    {
        if (rainfall < 225)
        {
            if (rainfall > 150)
            {
                // Sandy
                return soil(SoilBlockType.Variant.SANDY_LOAM);
            }
            else if (rainfall > 100)
            {
                // Sandy - Sand Transition Zone
                float noise = patchNoise.noise(x, z);
                return noise > 0.2f * (rainfall - 125f) / 25f ? sand(rockData, x, z) : soil(SoilBlockType.Variant.SANDY_LOAM);
            }
            else
            {
                // Sand
                return sand(rockData, x, z);
            }
        }
        else if (rainfall > 275)
        {
            if (rainfall < 350)
            {
                // Silty
                return soil(SoilBlockType.Variant.SILTY_LOAM);
            }
            else if (rainfall < 400)
            {
                // Silty / Silt Transition Zone
                float noise = patchNoise.noise(x, z);
                return soil(noise > 0 ? SoilBlockType.Variant.SILTY_LOAM : SoilBlockType.Variant.SILT);
            }
            else
            {
                // Silt
                return soil(SoilBlockType.Variant.SILT);
            }
        }
        else
        {
            // Sandy / Silty Transition Zone
            float noise = patchNoise.noise(x, z);
            return soil(noise > 0 ? SoilBlockType.Variant.SILTY_LOAM : SoilBlockType.Variant.SANDY_LOAM);
        }
    }

    @Override
    public void updatePostPlacement(IWorld world, BlockPos pos, BlockState state)
    {
        super.updatePostPlacement(world, pos, state);
        if (state.getBlock() instanceof TFCGrassBlock)
        {
            // Handle grass update ticks for adjacent blocks
            world.getBlockTicks().scheduleTick(pos, state.getBlock(), 0);
        }
    }

    @Override
    protected void initSeed(long seed)
    {
        patchNoise = new SimplexNoise2D(seed).octaves(2).spread(0.06f);
    }

    private BlockState sand(RockData rockData, int x, int z)
    {
        return TFCBlocks.SAND.get(rockData.getTopRock(x, z).getDesertSandColor()).get().defaultBlockState();
    }

    private BlockState soil(SoilBlockType.Variant variant)
    {
        return TFCBlocks.SOIL.get(soil).get(variant).get().defaultBlockState();
    }
}