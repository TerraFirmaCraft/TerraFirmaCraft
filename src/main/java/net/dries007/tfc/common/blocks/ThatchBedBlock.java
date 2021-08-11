/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blocks;

import javax.annotation.Nullable;

import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;

import net.minecraft.world.level.block.state.BlockBehaviour.Properties;

// todo: need to extend to avoid having the EntityBlock in hiearchy?
public class ThatchBedBlock extends BedBlock
{
    private static final VoxelShape BED_SHAPE = Block.box(0.0F, 0.0F, 0.0F, 16.0F, 9.0F, 16.0F);

    public ThatchBedBlock(Properties properties)
    {
        super(DyeColor.YELLOW, properties);
    }

    @Override
    public InteractionResult use(BlockState state, Level worldIn, BlockPos pos, Player player, InteractionHand handIn, BlockHitResult hit)
    {
        if (!worldIn.isClientSide())
        {
            if (canSetSpawn(worldIn))
            {
                if (!worldIn.isThundering())
                {
                    player.displayClientMessage(new TranslatableComponent("tfc.thatch_bed.use"), true);
                }
                else
                {
                    player.displayClientMessage(new TranslatableComponent("tfc.thatch_bed.thundering"), true);
                }
                return InteractionResult.SUCCESS;
            }
            else
            {
                worldIn.explode(null, DamageSource.badRespawnPointExplosion(), null, pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D, 7.0F, true, Explosion.BlockInteraction.DESTROY);
            }
        }
        return InteractionResult.FAIL;
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context)
    {
        return BED_SHAPE;
    }

    @Override
    @SuppressWarnings("deprecation")
    public RenderShape getRenderShape(BlockState state)
    {
        return RenderShape.MODEL;
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state)
    {
        return null; // Need to override as the super class is a EntityBlock
    }

    @SuppressWarnings("deprecation")
    @Override
    public void neighborChanged(BlockState state, Level world, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving)
    {
        Direction facing = state.getValue(FACING);
        if (!(world.getBlockState(pos.relative(facing)).is(TFCBlocks.THATCH_BED.get())) || world.getBlockState(pos.below()).isFaceSturdy(world, pos.below(), Direction.UP))
        {
            world.destroyBlock(pos, true);
        }
    }

    @Override
    public boolean isBed(BlockState state, BlockGetter world, BlockPos pos, @Nullable Entity player)
    {
        return true;
    }
}
