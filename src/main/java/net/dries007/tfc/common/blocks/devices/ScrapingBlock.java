/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blocks.devices;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.items.CapabilityItemHandler;

import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.blockentities.ScrapingBlockEntity;
import net.dries007.tfc.common.blocks.ForgeBlockProperties;
import net.dries007.tfc.util.Helpers;

public class ScrapingBlock extends DeviceBlock
{
    private static final VoxelShape SHAPE = box(0.0D, 0.0D, 0.0D, 16.0D, 1.0D, 16.0D);

    private static Vec3 calculatePoint(Vec3 rayVector, Vec3 rayPoint)
    {
        Vec3 planeNormal = new Vec3(0.0, 1.0, 0.0);
        return rayPoint.subtract(rayVector.scale(rayPoint.dot(planeNormal) / rayVector.dot(planeNormal)));
    }

    public ScrapingBlock(ForgeBlockProperties properties)
    {
        super(properties);
    }

    @Override
    @SuppressWarnings("deprecation")
    public BlockState updateShape(BlockState stateIn, Direction facing, BlockState facingState, LevelAccessor worldIn, BlockPos currentPos, BlockPos facingPos)
    {
        if (facing == Direction.DOWN && !facingState.is(TFCTags.Blocks.SCRAPING_SURFACE))
        {
            return Blocks.AIR.defaultBlockState();
        }
        return stateIn;
    }

    @Override
    @SuppressWarnings("deprecation")
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit)
    {
        ScrapingBlockEntity te = Helpers.getBlockEntity(level, pos, ScrapingBlockEntity.class);
        if (te != null)
        {
            ItemStack stack = player.getItemInHand(hand);
            if (TFCTags.Items.KNIVES.contains(stack.getItem()))
            {
                Vec3 point = calculatePoint(player.getLookAngle(), hit.getLocation().subtract(new Vec3(pos.getX(), pos.getY(), pos.getZ())));
                te.onClicked((float) point.x, (float) point.z);
                if (!level.isClientSide)
                {
                    stack.hurtAndBreak(1, player, p -> p.broadcastBreakEvent(hand));
                }
                else
                {
                    te.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent(cap -> {
                        level.addParticle(new ItemParticleOption(ParticleTypes.ITEM, cap.getStackInSlot(0)), pos.getX() + point.x, pos.getY() + 0.0625, pos.getZ() + point.z, Helpers.fastGaussian(level.random) / 2.0D, level.random.nextDouble() / 4.0D, Helpers.fastGaussian(level.random) / 2.0D);
                    });
                }
                return InteractionResult.SUCCESS;
            }
        }
        return InteractionResult.PASS;
    }

    @Override
    @SuppressWarnings("deprecation")
    public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context)
    {
        return SHAPE;
    }
}
