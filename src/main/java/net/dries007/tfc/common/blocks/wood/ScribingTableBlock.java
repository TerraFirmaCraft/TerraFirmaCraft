/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blocks.wood;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import net.dries007.tfc.common.blocks.ExtendedProperties;
import net.dries007.tfc.common.blocks.IForgeBlockExtension;
import net.dries007.tfc.common.container.ScribingTableContainer;

public class ScribingTableBlock extends HorizontalDirectionalBlock implements IForgeBlockExtension
{
    private static final Component CONTAINER_TITLE = Component.translatable("tfc.screen.scribing_table");
    private static final VoxelShape BASE_SHAPE = Shapes.or(
        // Top
        box(0, 12, 0, 16, 16, 16),
        // Legs
        box(1, 0, 1, 4, 12, 4),
        box(12, 0, 1, 15, 12, 4),
        box(1, 0, 12, 4, 12, 15),
        box(12, 0, 12, 15, 12, 15)
    );
    private static final VoxelShape SHAPE_NS = Shapes.or(
        BASE_SHAPE,
        // Sides
        box(12, 5, 4, 14, 12, 12),
        box(2, 5, 4, 4, 12, 12)
    );
    private static final VoxelShape SHAPE_EW = Shapes.or(
        BASE_SHAPE,
        // Sides
        box(4, 5, 12, 12, 12, 14),
        box(4, 5, 2, 12, 12, 4)
    );

    private final ExtendedProperties properties;

    public ScribingTableBlock(ExtendedProperties properties)
    {
        super(properties.properties());
        registerDefaultState(getStateDefinition().any().setValue(FACING, Direction.NORTH));
        this.properties = properties;
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context)
    {
        return switch (state.getValue(FACING))
            {
                case NORTH, SOUTH -> SHAPE_NS;
                case EAST, WEST -> SHAPE_EW;
                default -> BASE_SHAPE;
            };
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult)
    {
        if (!level.isClientSide)
        {
            player.openMenu(state.getMenuProvider(level, pos));
        }
        return ItemInteractionResult.sidedSuccess(level.isClientSide);
    }

    @Override
    protected MenuProvider getMenuProvider(BlockState state, Level level, BlockPos pos)
    {
        return new SimpleMenuProvider((windowId, inv, player) -> new ScribingTableContainer(inv, windowId, ContainerLevelAccess.create(level, pos)), CONTAINER_TITLE);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context)
    {
        return defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    public ExtendedProperties getExtendedProperties()
    {
        return properties;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        builder.add(FACING);
    }

    @Override
    protected MapCodec<? extends HorizontalDirectionalBlock> codec()
    {
        return fakeBlockCodec();
    }
}
