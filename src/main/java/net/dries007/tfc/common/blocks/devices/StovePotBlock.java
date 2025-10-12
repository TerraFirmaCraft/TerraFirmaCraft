/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blocks.devices;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.items.ItemHandlerHelper;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.client.TFCSounds;
import net.dries007.tfc.common.TFCDamageTypes;
import net.dries007.tfc.common.blockentities.AbstractFirepitBlockEntity;
import net.dries007.tfc.common.blockentities.PotBlockEntity;
import net.dries007.tfc.common.blockentities.TFCBlockEntities;
import net.dries007.tfc.common.blocks.ExtendedProperties;
import net.dries007.tfc.common.blocks.TFCBlocks;
import net.dries007.tfc.common.items.TFCItems;
import net.dries007.tfc.util.Helpers;

public class StovePotBlock extends PotBlock
{
    public static final EnumProperty<Direction> FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final VoxelShape POT_SHAPE = Shapes.or(StoveBlock.BASE_SHAPE, box(2, 12, 2, 14, 16, 14));

    public StovePotBlock(ExtendedProperties properties)
    {
        super(properties, POT_SHAPE);
        registerDefaultState(getStateDefinition().any().setValue(LIT, false).setValue(SMOKE_LEVEL, 0).setValue(AXIS, Direction.Axis.X).setValue(FACING, Direction.NORTH));
    }

    public BlockState getStateForPlacement(BlockPlaceContext context)
    {
        return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        builder.add(FACING);
        super.createBlockStateDefinition(builder);
    }

    @Override
    protected void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random)
    {
        // doesn't set things on fire :)
    }

    @Nullable
    @Override
    public BlockState getStateToDraw(Level level, Player player, BlockState lookState, Direction direction, BlockPos pos, double x, double y, double z, ItemStack item)
    {
        return null;
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult)
    {
        // intercept pot extraction so the stove isn't replaced with a firepit
        final PotBlockEntity pot = level.getBlockEntity(pos, TFCBlockEntities.POT.get()).orElse(null);
        if (pot != null)
        {
            if (!pot.isBoiling() && stack.isEmpty() && player.isShiftKeyDown())
            {
                if (!(!state.getValue(LIT) && !pot.isBoiling() && pot.getAsh() > 0))
                {
                    ItemHandlerHelper.giveItemToPlayer(player, new ItemStack(TFCItems.POT.get()));
                    AbstractFirepitBlockEntity.convertTo(level, pos, state, pot, TFCBlocks.STOVE.get());
                }
                return ItemInteractionResult.sidedSuccess(level.isClientSide);
            }
            else
            {
                ItemHandlerHelper.giveItemToPlayer(player, new ItemStack(TFCItems.POT.get()));
                AbstractFirepitBlockEntity.convertTo(level, pos, state, pot, TFCBlocks.STOVE.get());
            }
            if (state.getValue(LIT))
            {
                TFCDamageTypes.pot(player, 1f);
                Helpers.playSound(level, pos, TFCSounds.ITEM_COOL.get());
            }
        }
        return super.useItemOn(stack, state, level, pos, player, hand, hitResult);
    }

    @Override
    public BlockState rotate(BlockState state, Rotation rot)
    {
        return state.setValue(FACING, rot.rotate(state.getValue(FACING)));
    }

    @Override
    protected BlockState mirror(BlockState state, Mirror mirror)
    {
        return state.rotate(mirror.getRotation(state.getValue(FACING)));
    }

}
