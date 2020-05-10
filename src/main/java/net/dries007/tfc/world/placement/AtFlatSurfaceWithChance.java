package net.dries007.tfc.world.placement;

import java.util.Objects;
import java.util.Random;
import java.util.function.Function;
import java.util.stream.Stream;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.GenerationSettings;
import net.minecraft.world.gen.placement.AtSurfaceWithChance;
import net.minecraft.world.gen.placement.ChanceConfig;

import com.mojang.datafixers.Dynamic;

public class AtFlatSurfaceWithChance extends AtSurfaceWithChance
{
    @SuppressWarnings("unused")
    public AtFlatSurfaceWithChance(Function<Dynamic<?>, ? extends ChanceConfig> configFactory)
    {
        super(configFactory);
    }

    public AtFlatSurfaceWithChance()
    {
        super(ChanceConfig::deserialize);
    }

    @Override
    public Stream<BlockPos> getPositions(IWorld worldIn, ChunkGenerator<? extends GenerationSettings> generatorIn, Random random, ChanceConfig configIn, BlockPos pos)
    {
        return super.getPositions(worldIn, generatorIn, random, configIn, pos).map(targetPos -> {
            for (int y = 0; y >= -2; y--)
            {
                if (isFlatEnough(worldIn, targetPos, y))
                {
                    return targetPos.up(y);
                }
            }
            return null;
        }).filter(Objects::nonNull);
    }

    private boolean isFlatEnough(IWorld world, BlockPos pos, int y)
    {
        int flatAmount = 0;
        for (int x = -4; x <= 4; x++)
        {
            for (int z = -4; z <= 4; z++)
            {
                BlockPos posAt = pos.add(x, y, z);
                BlockPos posDown = posAt.down();
                BlockState stateAt = world.getBlockState(posAt);
                BlockState stateDown = world.getBlockState(posDown);
                if (stateDown.isSolid() && stateAt.isAir(world, posAt))
                {
                    flatAmount++;
                }
            }
        }
        return flatAmount > 48; // 60% flatness
    }
}
