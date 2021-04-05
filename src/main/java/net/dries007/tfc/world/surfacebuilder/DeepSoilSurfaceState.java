package net.dries007.tfc.world.surfacebuilder;

import net.minecraft.block.BlockState;

import net.dries007.tfc.common.blocks.SandstoneBlockType;
import net.dries007.tfc.common.blocks.TFCBlocks;
import net.dries007.tfc.common.blocks.soil.SoilBlockType;
import net.dries007.tfc.common.types.Rock;
import net.dries007.tfc.world.chunkdata.RockData;

public class DeepSoilSurfaceState extends SoilSurfaceState
{
    public DeepSoilSurfaceState()
    {
        super(SoilBlockType.GRASS); // ignored
    }

    @Override
    public BlockState state(RockData rockData, int x, int y, int z, float temperature, float rainfall, boolean salty)
    {
        if (rainfall < RAINFALL_SAND)
        {
            // Sandy
            return sandstone(rockData, x, z);
        }
        else if (rainfall < RAINFALL_SAND_SANDY_MIX)
        {
            // Sandy - Sand Transition Zone
            float noise = patchNoise.noise(x, z);
            return noise > 0.2f * (rainfall - RAINFALL_SAND_SANDY_MEAN) / RAINFALL_SAND_SANDY_RANGE ? sandstone(rockData, x, z) : gravel(rockData, x, z);
        }
        else
        {
            // All others
            return gravel(rockData, x, z);
        }
    }

    private BlockState gravel(RockData rockData, int x, int z)
    {
        return rockData.getTopRock(x, z).getBlock(Rock.BlockType.GRAVEL).defaultBlockState();
    }

    private BlockState sandstone(RockData rockData, int x, int z)
    {
        return TFCBlocks.SANDSTONE.get(rockData.getTopRock(x, z).getDesertSandColor()).get(SandstoneBlockType.RAW).get().defaultBlockState();
    }
}
