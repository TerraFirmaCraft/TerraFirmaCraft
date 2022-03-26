/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blocks.devices;

import java.util.List;
import org.jetbrains.annotations.Nullable;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.network.NetworkHooks;
import net.minecraftforge.items.ItemStackHandler;

import net.dries007.tfc.common.blockentities.TFCBlockEntities;
import net.dries007.tfc.common.blocks.EntityBlockExtension;
import net.dries007.tfc.common.blocks.ExtendedProperties;
import net.dries007.tfc.common.capabilities.size.IItemSize;
import net.dries007.tfc.common.capabilities.size.Size;
import net.dries007.tfc.common.capabilities.size.Weight;
import net.dries007.tfc.util.Alloy;
import net.dries007.tfc.util.Helpers;

public class CrucibleBlock extends DeviceBlock implements EntityBlockExtension, IItemSize
{
    private static final VoxelShape SHAPE = Shapes.or(
        box(3, 0, 3, 13, 2, 13), // base
        box(1, 1, 1, 15, 16, 3), // north
        box(1, 1, 13, 15, 16, 15), // south
        box(13, 1, 1, 15, 16, 15), // east
        box(1, 1, 1, 3, 16, 15) // west
    );

    public CrucibleBlock(ExtendedProperties properties)
    {
        super(properties, InventoryRemoveBehavior.SAVE);
    }

    @Override
    @SuppressWarnings("deprecation")
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit)
    {
        if (!player.isShiftKeyDown())
        {
            if (player instanceof ServerPlayer serverPlayer)
            {
                level.getBlockEntity(pos, TFCBlockEntities.CRUCIBLE.get()).ifPresent(crucible -> NetworkHooks.openGui(serverPlayer, crucible, crucible.getBlockPos()));
            }
        }
        return InteractionResult.PASS;
    }

    @Override
    @SuppressWarnings("deprecation")
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context)
    {
        return SHAPE;
    }

    @Override
    public Size getSize(ItemStack stack)
    {
        final CompoundTag tag = stack.getTagElement("BlockEntityTag" /* BlockItem.BLOCK_ENTITY_TAG */);
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
    public void appendHoverText(ItemStack stack, @Nullable BlockGetter level, List<Component> tooltip, TooltipFlag flag)
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

            tooltip.add(new TranslatableComponent("tfc.tooltip.small_vessel.contents").withStyle(ChatFormatting.DARK_GREEN));
            Helpers.addInventoryTooltipInfo(inventory, tooltip);
            Helpers.addFluidStackTooltipInfo(alloy.getResultAsFluidStack(), tooltip);
        }
    }
}
