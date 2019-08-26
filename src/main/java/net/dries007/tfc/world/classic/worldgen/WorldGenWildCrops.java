/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.world.classic.worldgen;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;

import net.dries007.tfc.TerraFirmaCraft;
import net.dries007.tfc.api.types.ICrop;
import net.dries007.tfc.objects.blocks.BlocksTFC;
import net.dries007.tfc.objects.blocks.agriculture.BlockCropTFC;
import net.dries007.tfc.util.climate.ClimateTFC;
import net.dries007.tfc.world.classic.chunkdata.ChunkDataTFC;

import static net.dries007.tfc.objects.blocks.agriculture.BlockCropTFC.WILD;

@ParametersAreNonnullByDefault
public class WorldGenWildCrops extends WorldGenerator
{
    private final List<ICrop> crops;

    public WorldGenWildCrops()
    {
        this.crops = new ArrayList<>(BlockCropTFC.getCrops());
        if (crops.size() == 0)
        {
            TerraFirmaCraft.getLog().warn("There are no wild crops registered to world gen!");
        }
    }

    @Override
    public boolean generate(World world, Random rng, BlockPos start)
    {
        if (crops.size() <= 0)
        {
            return false;
        }
        ICrop crop = crops.get(rng.nextInt(crops.size()));
        BlockCropTFC cropBlock = BlockCropTFC.get(crop);

        float temperature = ClimateTFC.getAvgTemp(world, start);
        float rainfall = ChunkDataTFC.getRainfall(world, start);
        if (crop.isValidConditions(temperature, rainfall))
        {
            for (int i = 0; i < 14 + rng.nextInt(5); ++i)
            {
                BlockPos pos = start.add(rng.nextInt(8) - rng.nextInt(8), rng.nextInt(4) - rng.nextInt(4), rng.nextInt(8) - rng.nextInt(8));
                if (world.isAirBlock(pos) && cropBlock.canPlaceBlockAt(world, pos))
                {
                    if (BlocksTFC.isSoil(world.getBlockState(pos.add(0, -1, 0))))
                    {
                        int growth = 2 + rng.nextInt(crop.getMaxStage() - 2);
                        world.setBlockState(pos, cropBlock.getDefaultState().withProperty(cropBlock.getStageProperty(), growth).withProperty(WILD, true), 2);
                    }

                }
            }
        }

        return true;
    }
}
