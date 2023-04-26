/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blocks.devices;

import java.util.List;
import java.util.Optional;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
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
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.common.blockentities.InventoryBlockEntity;
import net.dries007.tfc.common.blocks.ExtendedProperties;
import net.dries007.tfc.common.blocks.TooltipBlock;
import net.dries007.tfc.common.blocks.TFCBlockStateProperties;
import net.dries007.tfc.common.capabilities.size.IItemSize;
import net.dries007.tfc.common.capabilities.size.Size;
import net.dries007.tfc.common.capabilities.size.Weight;
import net.dries007.tfc.config.TFCConfig;
import net.dries007.tfc.util.Helpers;

public class SealableDeviceBlock extends DeviceBlock implements IItemSize, TooltipBlock
{
    public static final BooleanProperty SEALED = TFCBlockStateProperties.SEALED;
    public static final BooleanProperty POWERED = TFCBlockStateProperties.POWERED;
    private static final VoxelShape SHAPE = box(2, 0, 2, 14, 16, 14);
    private static final VoxelShape SHAPE_UNSEALED = Shapes.join(SHAPE, box(3, 1, 3, 13, 16, 13), BooleanOp.ONLY_FIRST);
    private static final int[] IMAGE_TOOLTIP = {1, 1, 0, 0};

    public SealableDeviceBlock(ExtendedProperties properties)
    {
        super(properties, InventoryRemoveBehavior.DROP);
        registerDefaultState(getStateDefinition().any().setValue(SEALED, false).setValue(POWERED, false));
    }

    @Override
    @SuppressWarnings("deprecation")
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context)
    {
        return state.getValue(SEALED) ? SHAPE : SHAPE_UNSEALED;
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context)
    {
        boolean powered = context.getLevel().hasNeighborSignal(context.getClickedPos());

        return (context.getItemInHand().getTag() != null ? defaultBlockState().setValue(SEALED, true) : defaultBlockState()).setValue(POWERED, powered);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable BlockGetter level, List<Component> tooltip, TooltipFlag flag)
    {
        final CompoundTag tag = stack.getTagElement(Helpers.BLOCK_ENTITY_TAG);
        if (tag != null)
        {
            final CompoundTag inventoryTag = tag.getCompound("inventory");
            final ItemStackHandler inventory = new ItemStackHandler();

            inventory.deserializeNBT(inventoryTag.getCompound("inventory"));

            if (!Helpers.isEmpty(inventory) && !TFCConfig.CLIENT.displayItemContentsAsImages.get())
            {
                tooltip.add(Helpers.translatable("tfc.tooltip.contents").withStyle(ChatFormatting.DARK_GREEN));
                Helpers.addInventoryTooltipInfo(inventory, tooltip);
            }
            addExtraInfo(tooltip, inventoryTag);
        }
    }

    @Override
    public Optional<TooltipComponent> getTooltipImage(ItemStack stack)
    {
        if (TFCConfig.CLIENT.displayItemContentsAsImages.get())
        {
            final CompoundTag tag = stack.getTagElement(Helpers.BLOCK_ENTITY_TAG);
            if (tag != null)
            {
                final CompoundTag inventoryTag = tag.getCompound("inventory");
                final ItemStackHandler inventory = new ItemStackHandler();

                inventory.deserializeNBT(inventoryTag.getCompound("inventory"));

                if (!Helpers.isEmpty(inventory))
                {
                    final int[] params = getImageTooltipParameters();
                    return Helpers.getTooltipImage(inventory, params[0], params[1], params[2], params[3]);
                }
            }
        }
        return Optional.empty();
    }

    /**
     * @return an array of four integers: {width, height, startIndex, endIndex}
     */
    public int[] getImageTooltipParameters()
    {
        return IMAGE_TOOLTIP;
    }

    protected void addExtraInfo(List<Component> tooltip, CompoundTag inventoryTag)
    {

    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        super.createBlockStateDefinition(builder.add(SEALED, POWERED));
    }

    @Override
    public Size getSize(ItemStack stack)
    {
        return Size.HUGE;
    }

    @Override
    public Weight getWeight(ItemStack stack)
    {
        return stack.getTag() == null ? Weight.HEAVY : Weight.VERY_HEAVY;
    }

    @Override
    public int getDefaultStackSize(ItemStack stack)
    {
        return 1; // Stacks to 1, regardless of weight
    }

    @Override
    public ItemStack getCloneItemStack(BlockState state, HitResult target, BlockGetter level, BlockPos pos, Player player)
    {
        final ItemStack stack = super.getCloneItemStack(state, target, level, pos, player);
        if (state.getValue(SEALED))
        {
            final BlockEntity entity = level.getBlockEntity(pos);
            if (entity instanceof InventoryBlockEntity<?> inv)
            {
                inv.saveToItem(stack);
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
        entity.invalidateCapabilities();
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
}
