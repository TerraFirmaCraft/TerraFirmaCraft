package net.dries007.tfc.world.feature.cave;

import java.util.Random;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.feature.Feature;

import com.mojang.serialization.Codec;
import net.dries007.tfc.common.blocks.CalciteBlock;
import net.dries007.tfc.common.blocks.TFCBlocks;

public class CalciteFeature extends Feature<CalciteConfig>
{
    public CalciteFeature(Codec<CalciteConfig> codec)
    {
        super(codec);
    }

    @Override
    public boolean place(ISeedReader world, ChunkGenerator generator, Random rand, BlockPos pos, CalciteConfig config)
    {
        final BlockState calcite = TFCBlocks.CALCITE.get().defaultBlockState();
        final BlockPos.Mutable mutablePos = new BlockPos.Mutable();
        boolean placedAny = false;

        for (int attempt = 0; attempt < config.getTries(); attempt++)
        {
            mutablePos.setWithOffset(pos, rand.nextInt(config.getRadius()) - rand.nextInt(config.getRadius()), rand.nextInt(config.getRadius() - rand.nextInt(config.getRadius())), rand.nextInt(config.getRadius()) - rand.nextInt(config.getRadius()));
            // Move upwards to find a suitable spot
            for (int i = 0; i < 7; i++)
            {
                mutablePos.move(0, 1, 0);
                if (!world.isEmptyBlock(mutablePos))
                {
                    mutablePos.move(0, -1, 0);
                    break;
                }
            }
            if (calcite.canSurvive(world, mutablePos) && world.isEmptyBlock(mutablePos))
            {
                placeCalcite(world, mutablePos, calcite, rand, config);
                placedAny = true;
            }
        }
        return placedAny;
    }

    private void placeCalcite(ISeedReader world, BlockPos.Mutable mutablePos, BlockState calcite, Random rand, CalciteConfig config)
    {
        final int height = config.getHeight(rand);
        for (int i = 0; i < height; i++)
        {
            setBlock(world, mutablePos, calcite);
            mutablePos.move(0, -1, 0);
            if (!world.isEmptyBlock(mutablePos))
            {
                // Make the previous state the tip, and exit
                setBlock(world, mutablePos.move(0, 1, 0), calcite.setValue(CalciteBlock.TIP, true));
                return;
            }
        }
        // Add the tip
        setBlock(world, mutablePos, calcite.setValue(CalciteBlock.TIP, true));
    }
}
