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
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.items.ItemStackHandler;

import net.dries007.tfc.common.blockentities.CrucibleBlockEntity;
import net.dries007.tfc.common.blockentities.TFCBlockEntities;
import net.dries007.tfc.common.blocks.EntityBlockExtension;
import net.dries007.tfc.common.blocks.ExtendedProperties;
import net.dries007.tfc.common.blocks.TooltipBlock;
import net.dries007.tfc.common.capabilities.size.IItemSize;
import net.dries007.tfc.common.capabilities.size.Size;
import net.dries007.tfc.common.capabilities.size.Weight;
import net.dries007.tfc.config.TFCConfig;
import net.dries007.tfc.util.Alloy;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.Tooltips;

public class CrucibleBlock extends DeviceBlock implements EntityBlockExtension, IItemSize, TooltipBlock
{
    private static final VoxelShape SHAPE = Shapes.or(
        box(3, 0, 3, 13, 2, 13), // base
        box(1, 1, 1, 15, 16, 3), // north
        box(1, 1, 13, 15, 16, 15), // south
        box(13, 1, 1, 15, 16, 15), // east
        box(1, 1, 1, 3, 16, 15) // west
    );

    /**
     * Full interaction shape when placing blocks against the composter, as otherwise targeting the top is quite difficult
     */
    private static final VoxelShape INTERACTION_SHAPE = Shapes.or(
        box(3, 0, 3, 13, 2, 13), // base
        box(1, 1, 1, 15, 16, 15) // interior
    );

    public CrucibleBlock(ExtendedProperties properties)
    {
        super(properties, InventoryRemoveBehavior.SAVE);
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult)
    {
        if (!player.isShiftKeyDown())
        {
            if (player instanceof ServerPlayer serverPlayer)
            {
                level.getBlockEntity(pos, TFCBlockEntities.CRUCIBLE.get()).ifPresent(crucible -> Helpers.openScreen(serverPlayer, crucible, crucible.getBlockPos()));
            }
            return ItemInteractionResult.SUCCESS;
        }
        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context)
    {
        return SHAPE;
    }

    @Override
    protected VoxelShape getInteractionShape(BlockState state, BlockGetter level, BlockPos pos)
    {
        return INTERACTION_SHAPE;
    }

    @Override
    public Size getSize(ItemStack stack)
    {
        final CompoundTag tag = stack.getTagElement(Helpers.BLOCK_ENTITY_TAG);
        if (tag != null && tag.contains("empty") && !tag.getBoolean("empty"))
        {
            return Size.HUGE;
        }
        return Size.LARGE;
    }

    @Override
    public Weight getWeight(ItemStack stack)
    {
        return Weight.VERY_HEAVY;
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flag)
    {
        final CompoundTag tag = stack.getTagElement(Helpers.BLOCK_ENTITY_TAG);
        if (tag != null && tag.contains("empty") && !tag.getBoolean("empty"))
        {
            // Decode the contents of the crucible
            final CompoundTag inventoryTag = tag.getCompound("inventory");
            final Alloy alloy = new Alloy();
            final ItemStackHandler inventory = new ItemStackHandler();

            alloy.deserializeNBT(inventoryTag.getCompound("alloy"));
            inventory.deserializeNBT(inventoryTag.getCompound("inventory"));

            if (!TFCConfig.CLIENT.displayItemContentsAsImages.get())
            {
                tooltip.add(Component.translatable("tfc.tooltip.small_vessel.contents").withStyle(ChatFormatting.DARK_GREEN));
                Helpers.addInventoryTooltipInfo(inventory, tooltip);
            }

            final FluidStack fluid = alloy.getResultAsFluidStack();
            if (!fluid.isEmpty())
            {
                tooltip.add(Tooltips.fluidUnitsOf(fluid));
            }
        }
    }

    @Override
    public Optional<TooltipComponent> getTooltipImage(ItemStack stack)
    {
        if (TFCConfig.CLIENT.displayItemContentsAsImages.get())
        {
            final CompoundTag tag = stack.getTagElement(Helpers.BLOCK_ENTITY_TAG);
            if (tag != null && tag.contains("empty") && !tag.getBoolean("empty"))
            {
                final CompoundTag inventoryTag = tag.getCompound("inventory");
                final ItemStackHandler inventory = new ItemStackHandler();
                inventory.deserializeNBT(inventoryTag.getCompound("inventory"));

                return Helpers.getTooltipImage(inventory, 3, 3, CrucibleBlockEntity.SLOT_INPUT_START, CrucibleBlockEntity.SLOT_INPUT_END);
            }
        }
        return Optional.empty();
    }
}
