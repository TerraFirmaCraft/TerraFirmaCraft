/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.world.feature;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.NoFeatureConfig;

import com.mojang.serialization.Codec;
import net.dries007.tfc.common.blocks.rock.RockSpikeBlock;
import net.dries007.tfc.common.types.Rock;
import net.dries007.tfc.common.types.RockManager;

public class CaveSpikesFeature extends Feature<NoFeatureConfig>
{
    public CaveSpikesFeature(Codec<NoFeatureConfig> codec)
    {
        super(codec);
    }

    @Override
    public boolean place(ISeedReader worldIn, ChunkGenerator generator, Random rand, BlockPos pos, NoFeatureConfig config)
    {
        // Must start at an air state
        BlockState stateAt = worldIn.getBlockState(pos);
        if (stateAt.getBlock() == Blocks.CAVE_AIR)
        {
            // The direction that the spike is pointed
            Direction direction = rand.nextBoolean() ? Direction.UP : Direction.DOWN;
            BlockState wallState = worldIn.getBlockState(pos.relative(direction.getOpposite()));
            Rock wallRock = RockManager.INSTANCE.getRock(wallState.getBlock());
            if (wallRock != null && wallRock.getBlock(Rock.BlockType.RAW) == wallState.getBlock())
            {
                place(worldIn, pos, wallRock.getBlock(Rock.BlockType.SPIKE).defaultBlockState(), wallRock.getBlock(Rock.BlockType.RAW).defaultBlockState(), direction, rand);
            }
            else
            {
                // Switch directions and try again
                direction = direction.getOpposite();
                wallState = worldIn.getBlockState(pos.relative(direction));
                wallRock = RockManager.INSTANCE.getRock(wallState.getBlock());
                if (wallRock != null && wallRock.getBlock(Rock.BlockType.RAW) == wallState.getBlock())
                {
                    place(worldIn, pos, wallRock.getBlock(Rock.BlockType.SPIKE).defaultBlockState(), wallRock.getBlock(Rock.BlockType.RAW).defaultBlockState(), direction, rand);
                }
            }
            return true;
        }
        else
        {
            return false;
        }

    }

    protected void place(ISeedReader worldIn, BlockPos pos, BlockState spike, BlockState raw, Direction direction, Random rand)
    {
        placeSmallSpike(worldIn, pos, spike, raw, direction, rand);
    }

    protected void placeSmallSpike(ISeedReader worldIn, BlockPos pos, BlockState spike, BlockState raw, Direction direction, Random rand)
    {
        placeSmallSpike(worldIn, pos, spike, raw, direction, rand, rand.nextFloat());
    }

    protected void placeSmallSpike(ISeedReader worldIn, BlockPos pos, BlockState spike, BlockState raw, Direction direction, Random rand, float sizeWeight)
    {
        // Build a spike starting downwards from the target block
        if (sizeWeight < 0.2f)
        {
            replaceBlock(worldIn, pos, spike.setValue(RockSpikeBlock.PART, RockSpikeBlock.Part.MIDDLE));
            replaceBlock(worldIn, pos.relative(direction, 1), spike.setValue(RockSpikeBlock.PART, RockSpikeBlock.Part.TIP));
        }
        else if (sizeWeight < 0.7f)
        {
            replaceBlock(worldIn, pos, spike.setValue(RockSpikeBlock.PART, RockSpikeBlock.Part.BASE));
            replaceBlock(worldIn, pos.relative(direction, 1), spike.setValue(RockSpikeBlock.PART, RockSpikeBlock.Part.MIDDLE));
            replaceBlock(worldIn, pos.relative(direction, 2), spike.setValue(RockSpikeBlock.PART, RockSpikeBlock.Part.TIP));
        }
        else
        {
            replaceBlock(worldIn, pos, raw);
            replaceBlock(worldIn, pos.relative(direction, 1), spike.setValue(RockSpikeBlock.PART, RockSpikeBlock.Part.BASE));
            replaceBlock(worldIn, pos.relative(direction, 2), spike.setValue(RockSpikeBlock.PART, RockSpikeBlock.Part.MIDDLE));
            replaceBlock(worldIn, pos.relative(direction, 3), spike.setValue(RockSpikeBlock.PART, RockSpikeBlock.Part.TIP));
        }
    }

    protected void replaceBlock(ISeedReader world, BlockPos pos, BlockState state)
    {
        // We check explicitly for cave air here, because spikes shouldn't generate not in caves
        Block block = world.getBlockState(pos).getBlock();
        if (block == Blocks.CAVE_AIR)
        {
            setBlock(world, pos, state);
        }
    }
}