/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blocks.mechanical;

import java.util.function.Supplier;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.items.ItemHandlerHelper;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.common.blockentities.TFCBlockEntities;
import net.dries007.tfc.common.blockentities.WindmillBlockEntity;
import net.dries007.tfc.common.blocks.EntityBlockExtension;
import net.dries007.tfc.common.blocks.ExtendedBlock;
import net.dries007.tfc.common.blocks.ExtendedProperties;
import net.dries007.tfc.common.blocks.TFCBlockStateProperties;
import net.dries007.tfc.common.blocks.TFCBlocks;
import net.dries007.tfc.common.capabilities.Capabilities;
import net.dries007.tfc.common.items.TFCItems;
import net.dries007.tfc.util.Helpers;

public class WindmillBlock extends ExtendedBlock implements EntityBlockExtension
{
    public static final IntegerProperty COUNT = TFCBlockStateProperties.COUNT_1_5;
    public static final EnumProperty<Direction.Axis> AXIS = BlockStateProperties.HORIZONTAL_AXIS;

    public static final VoxelShape SHAPE_X = box(2, 2, 0, 14, 14, 16);
    public static final VoxelShape SHAPE_Z = Helpers.rotateShape(Direction.WEST, 2, 2, 0, 14, 14, 16);

    private final Supplier<? extends AxleBlock> axle;

    public WindmillBlock(ExtendedProperties properties, Supplier<? extends AxleBlock> axle)
    {
        super(properties);

        this.axle = axle;

        registerDefaultState(getStateDefinition().any().setValue(COUNT, 1));
    }

    public AxleBlock getAxle()
    {
        return axle.get();
    }

    @Override
    @SuppressWarnings("deprecation")
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult result)
    {
        final ItemStack stack = player.getItemInHand(hand);
        final int count = state.getValue(COUNT);
        if (stack.isEmpty())
        {
            final BlockState resultState = count == 1
                ? getAxle().defaultBlockState().setValue(AxleBlock.AXIS, state.getValue(WindmillBlock.AXIS))
                : state.setValue(COUNT, count - 1);

            level.setBlockAndUpdate(pos, resultState);
            if (!player.isCreative())
            {
                ItemHandlerHelper.giveItemToPlayer(player, new ItemStack(TFCItems.WINDMILL_BLADE.get()));
            }
            return InteractionResult.SUCCESS;
        }
        else if (count < 5 && stack.is(TFCItems.WINDMILL_BLADE.get()))
        {
            level.setBlockAndUpdate(pos, state.setValue(COUNT, count + 1));
            if (!player.isCreative())
            {
                stack.shrink(1);
            }
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        super.createBlockStateDefinition(builder.add(AXIS, COUNT));
    }

    @Override
    @SuppressWarnings("deprecation")
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context)
    {
        return state.getValue(AXIS) == Direction.Axis.X ? AxleBlock.SHAPE_X : AxleBlock.SHAPE_Z;
    }
}
