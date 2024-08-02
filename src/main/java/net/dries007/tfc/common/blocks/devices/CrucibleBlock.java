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

import net.dries007.tfc.common.blockentities.CrucibleBlockEntity;
import net.dries007.tfc.common.blockentities.TFCBlockEntities;
import net.dries007.tfc.common.blocks.EntityBlockExtension;
import net.dries007.tfc.common.blocks.ExtendedProperties;
import net.dries007.tfc.common.blocks.TooltipBlock;
import net.dries007.tfc.common.component.TFCComponents;
import net.dries007.tfc.common.component.block.CrucibleComponent;
import net.dries007.tfc.common.component.size.IItemSize;
import net.dries007.tfc.common.component.size.Size;
import net.dries007.tfc.common.component.size.Weight;
import net.dries007.tfc.config.TFCConfig;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.tooltip.Tooltips;

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
        return Size.LARGE;
    }

    @Override
    public Weight getWeight(ItemStack stack)
    {
        return stack.getOrDefault(TFCComponents.CRUCIBLE, CrucibleComponent.EMPTY).isEmpty()
            ? Weight.HEAVY
            : Weight.VERY_HEAVY;
    }

    @Override
    public int getDefaultStackSize(ItemStack stack)
    {
        return 1;
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag tooltipFlag)
    {
        final CrucibleComponent component = stack.getOrDefault(TFCComponents.CRUCIBLE, CrucibleComponent.EMPTY);
        if (!component.isEmpty())
        {
            if (!TFCConfig.CLIENT.displayItemContentsAsImages.get())
            {
                tooltip.add(Component.translatable("tfc.tooltip.small_vessel.contents").withStyle(ChatFormatting.DARK_GREEN));
                Helpers.addInventoryTooltipInfo(component.itemContent(), tooltip);
            }

            final FluidStack fluid = component.fluidContent().getResult();
            if (!fluid.isEmpty())
            {
                tooltip.add(Tooltips.fluidUnitsOf(fluid));
            }
        }
    }

    @Override
    public Optional<TooltipComponent> getTooltipImage(ItemStack stack)
    {
        return TFCConfig.CLIENT.displayItemContentsAsImages.get()
            ? TooltipBlock.buildInventoryTooltip(
                stack.getOrDefault(TFCComponents.CRUCIBLE, CrucibleComponent.EMPTY)
                    .itemContent()
                    .subList(CrucibleBlockEntity.SLOT_INPUT_START, 1 + CrucibleBlockEntity.SLOT_INPUT_END),
                3, 3)
            : Optional.empty();
    }
}
