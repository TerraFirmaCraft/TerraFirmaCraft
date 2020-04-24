package net.dries007.tfc.world.feature;

import java.util.Random;

import net.minecraft.block.BlockState;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.GenerationSettings;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.NoFeatureConfig;

import net.dries007.tfc.api.Rock;
import net.dries007.tfc.objects.blocks.rock.RockSpikeBlock;
import net.dries007.tfc.objects.types.RockManager;

public class CaveSpikesFeature extends Feature<NoFeatureConfig>
{
    public CaveSpikesFeature()
    {
        super(NoFeatureConfig::deserialize);
    }

    @Override
    public boolean place(IWorld worldIn, ChunkGenerator<? extends GenerationSettings> generator, Random rand, BlockPos pos, NoFeatureConfig config)
    {
        // Must start at an air state
        BlockState stateAt = worldIn.getBlockState(pos);
        if (!stateAt.isAir(worldIn, pos))
        {
            return false;
        }

        // The direction that the spike is pointed
        Direction direction = rand.nextBoolean() ? Direction.UP : Direction.DOWN;
        BlockState wallState = worldIn.getBlockState(pos.offset(direction.getOpposite()));
        Rock wallRock = RockManager.INSTANCE.getRock(wallState.getBlock());
        if (wallRock != null && wallRock.getBlock(Rock.BlockType.RAW) == wallState.getBlock())
        {
            place(worldIn, pos, wallRock.getBlock(Rock.BlockType.SPIKE).getDefaultState(), wallRock.getBlock(Rock.BlockType.RAW).getDefaultState(), direction, rand);
        }
        else
        {
            // Switch directions and try again
            direction = direction.getOpposite();
            wallState = worldIn.getBlockState(pos.offset(direction));
            wallRock = RockManager.INSTANCE.getRock(wallState.getBlock());
            if (wallRock != null && wallRock.getBlock(Rock.BlockType.RAW) == wallState.getBlock())
            {
                place(worldIn, pos, wallRock.getBlock(Rock.BlockType.SPIKE).getDefaultState(), wallRock.getBlock(Rock.BlockType.RAW).getDefaultState(), direction, rand);
            }
        }
        return true;
    }

    protected void place(IWorld worldIn, BlockPos pos, BlockState spike, BlockState raw, Direction direction, Random rand)
    {
        // Build a spike starting downwards from the target block
        float sizeWeight = rand.nextFloat();
        if (sizeWeight < 0.2f)
        {
            if (replaceBlock(worldIn, pos, spike.with(RockSpikeBlock.PART, RockSpikeBlock.Part.MIDDLE)))
            {
                return;
            }
            replaceBlock(worldIn, pos.offset(direction, 1), spike.with(RockSpikeBlock.PART, RockSpikeBlock.Part.TIP));
        }
        else if (sizeWeight < 0.7f)
        {
            if (replaceBlock(worldIn, pos, spike.with(RockSpikeBlock.PART, RockSpikeBlock.Part.BASE)))
            {
                return;
            }
            if (replaceBlock(worldIn, pos.offset(direction, 1), spike.with(RockSpikeBlock.PART, RockSpikeBlock.Part.MIDDLE)))
            {
                return;
            }
            replaceBlock(worldIn, pos.offset(direction, 2), spike.with(RockSpikeBlock.PART, RockSpikeBlock.Part.TIP));
        }
        else
        {
            replaceBlock(worldIn, pos, raw);
            if (replaceBlock(worldIn, pos.offset(direction, 1), spike.with(RockSpikeBlock.PART, RockSpikeBlock.Part.BASE)))
            {
                return;
            }
            if (replaceBlock(worldIn, pos.offset(direction, 2), spike.with(RockSpikeBlock.PART, RockSpikeBlock.Part.MIDDLE)))
            {
                return;
            }
            replaceBlock(worldIn, pos.offset(direction, 3), spike.with(RockSpikeBlock.PART, RockSpikeBlock.Part.TIP));
        }
    }

    /**
     * @return true if the replacement was unable to be completed (and as a result the spike will be floating)
     */
    protected boolean replaceBlock(IWorld world, BlockPos pos, BlockState state)
    {
        if (world.getBlockState(pos).isAir(world, pos))
        {
            world.setBlockState(pos, state, 3);
            return false;
        }
        return true;
    }
}
