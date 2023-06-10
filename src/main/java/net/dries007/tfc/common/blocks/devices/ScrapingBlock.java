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
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.blockentities.ScrapingBlockEntity;
import net.dries007.tfc.common.blocks.ExtendedProperties;
import net.dries007.tfc.common.blocks.TFCBlockStateProperties;
import net.dries007.tfc.common.capabilities.Capabilities;
import net.dries007.tfc.util.Helpers;

public class ScrapingBlock extends DeviceBlock
{
    private static final VoxelShape SHAPE = box(0.0D, 0.0D, 0.0D, 16.0D, 1.0D, 16.0D);
    private static final Vec3 PLANE_NORMAL = new Vec3(0.0, 1.0, 0.0);
    public static final BooleanProperty WAXED = TFCBlockStateProperties.WAXED;

    private static Vec3 calculatePoint(Vec3 rayVector, Vec3 rayPoint)
    {
        return rayPoint.subtract(rayVector.scale(rayPoint.dot(PLANE_NORMAL) / rayVector.dot(PLANE_NORMAL)));
    }

    public ScrapingBlock(ExtendedProperties properties)
    {
        super(properties, InventoryRemoveBehavior.DROP);
        registerDefaultState(getStateDefinition().any().setValue(WAXED, false));
    }

    @Override
    @SuppressWarnings("deprecation")
    public BlockState updateShape(BlockState stateIn, Direction facing, BlockState facingState, LevelAccessor worldIn, BlockPos currentPos, BlockPos facingPos)
    {
        if (facing == Direction.DOWN && !Helpers.isBlock(facingState, TFCTags.Blocks.SCRAPING_SURFACE))
        {
            return Blocks.AIR.defaultBlockState();
        }
        return stateIn;
    }

    @Override
    @SuppressWarnings("deprecation")
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit)
    {
        final ItemStack stack = player.getItemInHand(hand);
        if (Helpers.isItem(stack, TFCTags.Items.WAXES_SCRAPING_SURFACE) && !state.getValue(WAXED))
        {
            if (!player.isCreative()) stack.shrink(1);
            level.setBlockAndUpdate(pos, state.setValue(WAXED, true));
            return InteractionResult.sidedSuccess(level.isClientSide);
        }
        if (level.getBlockEntity(pos) instanceof ScrapingBlockEntity scraping)
        {
            final Vec3 point = calculatePoint(player.getLookAngle(), hit.getLocation().subtract(new Vec3(pos.getX(), pos.getY(), pos.getZ())));
            if (state.getValue(WAXED))
            {
                final DyeColor color = DyeColor.getColor(stack);
                if (color != null && scraping.dye(color))
                {
                    doParticles(level, pos, scraping, point);
                    if (!player.isCreative()) stack.shrink(1);
                    return InteractionResult.sidedSuccess(level.isClientSide);
                }
            }
            else if (Helpers.isItem(stack.getItem(), TFCTags.Items.KNIVES))
            {
                scraping.onClicked((float) point.x, (float) point.z);
                stack.hurtAndBreak(1, player, p -> p.broadcastBreakEvent(hand));
                doParticles(level, pos, scraping, point);
                return InteractionResult.sidedSuccess(level.isClientSide);
            }
        }
        return InteractionResult.PASS;
    }

    private static void doParticles(Level level, BlockPos pos, ScrapingBlockEntity scraping, Vec3 point)
    {
        if (level instanceof ServerLevel server)
        {
            scraping.getCapability(Capabilities.ITEM).ifPresent(cap -> {
                server.sendParticles(new ItemParticleOption(ParticleTypes.ITEM, cap.getStackInSlot(0)), pos.getX() + point.x, pos.getY() + 0.0625, pos.getZ() + point.z, 2, Helpers.triangle(level.random) / 2.0D, level.random.nextDouble() / 4.0D, Helpers.triangle(level.random) / 2.0D, 0.15f);
            });
        }
    }

    @Override
    @SuppressWarnings("deprecation")
    public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context)
    {
        return SHAPE;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        super.createBlockStateDefinition(builder.add(WAXED));
    }
}
