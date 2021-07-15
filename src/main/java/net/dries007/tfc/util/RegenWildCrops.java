package net.dries007.tfc.util;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.block.Block;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraftforge.fml.common.IWorldGenerator;

import net.dries007.tfc.ConfigTFC;
import net.dries007.tfc.api.types.ICrop;
import net.dries007.tfc.objects.blocks.BlocksTFC;
import net.dries007.tfc.objects.blocks.agriculture.BlockCropTFC;
import net.dries007.tfc.objects.blocks.plants.BlockShortGrassTFC;
import net.dries007.tfc.objects.blocks.plants.BlockTallGrassTFC;
import net.dries007.tfc.util.agriculture.Crop;
import net.dries007.tfc.util.calendar.CalendarTFC;
import net.dries007.tfc.util.climate.ClimateTFC;
import net.dries007.tfc.world.classic.ChunkGenTFC;
import net.dries007.tfc.world.classic.chunkdata.ChunkDataTFC;

@ParametersAreNonnullByDefault
public class RegenWildCrops implements IWorldGenerator
{
    private static final List<ICrop> CROPS = new ArrayList<>();

    public static void register(ICrop bush)
    {
        CROPS.add(bush);
    }

    public RegenWildCrops(){
        for (ICrop crop : Crop.values())
        {
            RegenWildCrops.register(crop);
        }

    }
    @Override
    public void generate(Random random, int chunkX, int chunkZ, World world, IChunkGenerator chunkGenerator, IChunkProvider chunkProvider)
    {
        if (chunkGenerator instanceof ChunkGenTFC && world.provider.getDimension() == 0 && CROPS.size() > 0 && ConfigTFC.General.FOOD.cropRarity > 0)
        {
            if (random.nextInt(ConfigTFC.General.FOOD.cropRarity) == 0)
            {
                // Guarantees crop generation if possible (easier to balance by config file while also making it random)
                BlockPos chunkBlockPos = new BlockPos(chunkX << 4, 0, chunkZ << 4);

                Collections.shuffle(CROPS);
                float temperature = ClimateTFC.getAvgTemp(world, chunkBlockPos);
                float rainfall = ChunkDataTFC.getRainfall(world, chunkBlockPos);

                ICrop crop = CROPS.stream().filter(x -> x.isValidConditions(temperature, rainfall)).findFirst().orElse(null);
                if (crop != null)
                {
                    BlockCropTFC cropBlock = BlockCropTFC.get(crop);
                    int cropsInChunk = 3 + random.nextInt(5);
                    for (int i = 0; i < cropsInChunk; i++)
                    {
                        final int x = (chunkX << 4) + random.nextInt(16) + 8;
                        final int z = (chunkZ << 4) + random.nextInt(16) + 8;
                        final BlockPos pos = world.getTopSolidOrLiquidBlock(new BlockPos(x, 0, z));
                        if (isReplaceable(world, pos) && BlocksTFC.isSoil(world.getBlockState(pos.down())))
                        {
                            double yearProgress = CalendarTFC.CALENDAR_TIME.getMonthOfYear().ordinal() / 11.0;
                            int maxStage = crop.getMaxStage();
                            int growth = (int) (yearProgress * maxStage) + 3 - random.nextInt(2);
                            if (growth > maxStage)
                                growth = maxStage;
                            world.setBlockState(pos, cropBlock.getDefaultState().withProperty(cropBlock.getStageProperty(), growth).withProperty(BlockCropTFC.WILD, true), 2);

                        }
                    }
                }
            }
        }
    }
    private Boolean isReplaceable(World world, BlockPos pos)
    {

        Block test = world.getBlockState(pos).getBlock();
        if (test instanceof BlockShortGrassTFC || test instanceof BlockTallGrassTFC || test.isAir(world.getBlockState(pos), world, pos))
        {
            return true;
        }
        return false;
    }
}
