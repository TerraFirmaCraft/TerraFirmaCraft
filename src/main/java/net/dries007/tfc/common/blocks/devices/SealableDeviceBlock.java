/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blocks.devices;

import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import net.dries007.tfc.common.blockentities.InventoryBlockEntity;
import net.dries007.tfc.common.blocks.ExtendedProperties;
import net.dries007.tfc.common.blocks.TFCBlockStateProperties;
import net.dries007.tfc.common.blocks.TooltipBlock;
import net.dries007.tfc.common.component.TFCComponents;
import net.dries007.tfc.common.component.item.ItemListComponent;
import net.dries007.tfc.common.component.size.IItemSize;
import net.dries007.tfc.common.component.size.Size;
import net.dries007.tfc.common.component.size.Weight;
import net.dries007.tfc.config.TFCConfig;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.tooltip.Tooltips;

public class SealableDeviceBlock extends DeviceBlock implements IItemSize, TooltipBlock
{
    public static final BooleanProperty SEALED = TFCBlockStateProperties.SEALED;
    public static final BooleanProperty POWERED = TFCBlockStateProperties.POWERED;
    private static final VoxelShape SHAPE = box(2, 0, 2, 14, 16, 14);
    private static final VoxelShape SHAPE_UNSEALED = Shapes.join(SHAPE, box(3, 1, 3, 13, 16, 13), BooleanOp.ONLY_FIRST);

    public SealableDeviceBlock(ExtendedProperties properties)
    {
        super(properties, InventoryRemoveBehavior.DROP);
        registerDefaultState(getStateDefinition().any().setValue(SEALED, false).setValue(POWERED, false));
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context)
    {
        return state.getValue(SEALED) ? SHAPE : SHAPE_UNSEALED;
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context)
    {
        BlockState state = getStateForPlacement(context.getLevel(), context.getClickedPos());
        if (isStackSealed(context.getItemInHand()))
        {
            state = state.setValue(SEALED, true);
        }
        return state;
    }

    public BlockState getStateForPlacement(LevelAccessor level, BlockPos pos)
    {
        return defaultBlockState().setValue(POWERED, level.hasNeighborSignal(pos));
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flag)
    {
        if (!TFCConfig.CLIENT.displayItemContentsAsImages.get())
        {
            final List<ItemStack> contents = stack.getOrDefault(TFCComponents.CONTENTS, ItemListComponent.EMPTY).contents();
            if (!Helpers.isEmpty(contents))
            {
                tooltip.add(Tooltips.contents());
                Helpers.addInventoryTooltipInfo(contents, tooltip);
            }
        }
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        builder.add(SEALED, POWERED);
    }

    @Override
    public Size getSize(ItemStack stack)
    {
        return Size.HUGE;
    }

    @Override
    public Weight getWeight(ItemStack stack)
    {
        return isStackSealed(stack) ? Weight.VERY_HEAVY : Weight.HEAVY;
    }

    @Override
    public int getDefaultStackSize(ItemStack stack)
    {
        return 1; // Stacks to 1, regardless of weight
    }

    @Override
    public ItemStack getCloneItemStack(BlockState state, HitResult target, LevelReader level, BlockPos pos, Player player)
    {
        final ItemStack stack = super.getCloneItemStack(state, target, level, pos, player);
        if (state.getValue(SEALED))
        {
            final BlockEntity entity = level.getBlockEntity(pos);
            if (entity instanceof InventoryBlockEntity<?> inv)
            {
                inv.saveToItem(stack, level.registryAccess());
            }
        }
        return stack;
    }

    @Override
    protected void beforeRemove(InventoryBlockEntity<?> entity)
    {
        if (!entity.getBlockState().getValue(SEALED))
        {
            entity.ejectInventory();
        }
    }

    /* Handles block states for redstone changes from neighbors and adjusts the block entities to match */
    public void handleNeighborChanged(BlockState state, Level level, BlockPos pos, Runnable onSeal, Runnable onUnseal)
    {
        final boolean signal = level.hasNeighborSignal(pos);
        if (signal != state.getValue(POWERED))
        {
            if (signal != state.getValue(SEALED))
            {
                level.setBlockAndUpdate(pos, state.setValue(POWERED, signal).setValue(SEALED, signal));

                if (signal) onSeal.run();
                else onUnseal.run();
            }
            else
            {
                level.setBlockAndUpdate(pos, state.setValue(POWERED, signal));
            }
        }
    }

    protected boolean isStackSealed(ItemStack stack)
    {
        return stack.has(TFCComponents.CONTENTS);
    }
}
