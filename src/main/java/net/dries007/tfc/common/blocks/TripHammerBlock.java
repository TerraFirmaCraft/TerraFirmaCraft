/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blocks;

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
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.common.blockentities.rotation.TripHammerBlockEntity;
import net.dries007.tfc.common.capabilities.Capabilities;
import net.dries007.tfc.util.Helpers;


public class TripHammerBlock extends HorizontalDirectionalBlock implements IForgeBlockExtension, EntityBlockExtension
{
    private static final VoxelShape[] BASE_SHAPES = Helpers.computeHorizontalShapes(dir -> Shapes.or(
        Helpers.rotateShape(dir, 11, 0, 0, 13, 16, 10),
        Helpers.rotateShape(dir, 3, 0, 0, 5, 16, 10),
        Helpers.rotateShape(dir, 5, 0, 0, 11, 2, 10)
    ));

    private final ExtendedProperties properties;

    public TripHammerBlock(ExtendedProperties properties)
    {
        super(properties.properties());
        registerDefaultState(getStateDefinition().any().setValue(FACING, Direction.NORTH));
        this.properties = properties;
    }

    @Override
    @SuppressWarnings("deprecation")
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult result)
    {
        if (level.getBlockEntity(pos) instanceof TripHammerBlockEntity hammer)
        {
            final ItemStack held = player.getItemInHand(hand);
            final IItemHandler inv = Helpers.getCapability(hammer, Capabilities.ITEM);
            if (inv != null && inv.getStackInSlot(0).isEmpty() && inv.isItemValid(0, held))
            {
                ItemHandlerHelper.giveItemToPlayer(player, inv.insertItem(0, held.split(1), false));
                hammer.markForSync();
                return InteractionResult.sidedSuccess(level.isClientSide);
            }
        }
        return InteractionResult.PASS;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        super.createBlockStateDefinition(builder.add(FACING));
    }

    @Override
    public ExtendedProperties getExtendedProperties()
    {
        return properties;
    }

    @Override
    @SuppressWarnings("deprecation")
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context)
    {
        return BASE_SHAPES[state.getValue(FACING).get2DDataValue()];
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context)
    {
        return defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }
}
