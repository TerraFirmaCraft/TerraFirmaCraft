/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;

import net.dries007.tfc.util.Helpers;

public class MoltenBlock extends ExtendedBlock
{
    public static final IntegerProperty LAYERS = TFCBlockStateProperties.LAYERS_4;
    public static final BooleanProperty LIT = BlockStateProperties.LIT;

    public static void removeMoltenBlockTower(Level level, BlockPos pos, int maxHeight)
    {
        manageMoltenBlockTower(level, pos, false, maxHeight, 0, 1);
    }

    /**
     * Manages a molten block tower.
     *
     * @param level         The world.
     * @param pos           The starting position of the tower, or the first molten block.
     * @param lit           The lit state of the tower
     * @param maxHeight     The maximum height of the tower, in a number of blocks.
     * @param itemCount     The count of items that this tower is representing.
     * @param itemsPerLayer The number of items per layer that is contained within the tower.
     */
    public static void manageMoltenBlockTower(Level level, BlockPos pos, boolean lit, int maxHeight, int itemCount, int itemsPerLayer)
    {
        final BlockState state = TFCBlocks.MOLTEN.get().defaultBlockState().setValue(MoltenBlock.LIT, lit);

        int layers = 0;
        if (itemCount > 0)
        {
            layers = Math.max(1, (4 * itemCount) / itemsPerLayer);
        }

        for (int y = 0; y < maxHeight; y++)
        {
            final BlockPos checkPos = pos.above(y);
            if (layers >= 4)
            {
                // Full block (4 layers)
                level.setBlockAndUpdate(checkPos, state.setValue(MoltenBlock.LAYERS, 4));
                layers -= 4;
            }
            else if (layers > 0)
            {
                // Partial block (0-3 layers)
                level.setBlockAndUpdate(checkPos, state.setValue(MoltenBlock.LAYERS, layers));
                layers = 0;
            }
            else if (Helpers.isBlock(level.getBlockState(checkPos), TFCBlocks.MOLTEN.get()))
            {
                // Remove any existing molten blocks
                level.setBlockAndUpdate(checkPos, Blocks.AIR.defaultBlockState());
            }
        }
    }

    public MoltenBlock(ExtendedProperties properties)
    {
        super(properties);
        registerDefaultState(getStateDefinition().any().setValue(LAYERS, 1).setValue(LIT, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        builder.add(LAYERS, LIT);
    }

    @Override
    public void stepOn(Level level, BlockPos pos, BlockState state, Entity entity)
    {
        if (entity instanceof LivingEntity && level.getBlockState(pos).getValue(LIT))
        {
            entity.hurt(entity.damageSources().hotFloor(), 1f);
        }
        super.stepOn(level, pos, state, entity);
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random)
    {
        if (state.getValue(LIT) && level.isEmptyBlock(pos.above()))
        {
            final double x = pos.getX() + 0.5D;
            final double y = pos.getY() + 1.1D;
            final double z = pos.getZ() + 0.5D;
            level.addParticle(ParticleTypes.CAMPFIRE_COSY_SMOKE, x, y, z, Helpers.triangle(random) * 0.1D, 0.2D, Helpers.triangle(random) * 0.1D);
            if (random.nextInt(10) == 0)
            {
                level.addParticle(ParticleTypes.LAVA, x, y, z, Helpers.triangle(random) * 0.1D, 0.5D, Helpers.triangle(random) * 0.1D);
            }
        }
    }

    @Override
    protected void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource rand)
    {
        if (level.getBlockState(pos.above()).isAir() && state.getValue(LIT))
        {
            Helpers.fireSpreaderTick(level, pos, rand, 2);
        }
    }
}
