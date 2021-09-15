/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blocks.devices;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;

import com.mojang.blaze3d.vertex.PoseStack;
import net.dries007.tfc.client.IHighlightHandler;
import net.dries007.tfc.client.TFCSounds;
import net.dries007.tfc.common.blockentities.QuernBlockEntity;
import net.dries007.tfc.common.blocks.ExtendedProperties;
import net.dries007.tfc.util.Helpers;

import static net.dries007.tfc.common.blockentities.QuernBlockEntity.*;

public class QuernBlock extends DeviceBlock implements IHighlightHandler
{
    private static final VoxelShape BASE_SHAPE = box(0.0D, 0.0D, 0.0D, 16.0D, 10.0D, 16.0D);

    private static final VoxelShape HANDSTONE_SHAPE = box(3.0D, 10.0D, 3.0D, 13.0D, 13.76D, 13.0D);
    private static final AABB HANDSTONE_AABB = HANDSTONE_SHAPE.bounds().inflate(0.01D);

    private static final VoxelShape HANDLE_SHAPE = box(4.34D, 13.76D, 4.34D, 5.36D, 16.24D, 5.36D);
    private static final AABB HANDLE_AABB = HANDLE_SHAPE.bounds().inflate(0.01D);

    private static final VoxelShape INPUT_SLOT_SHAPE = box(6.0D, 13.76D, 6.0D, 10.0D, 16.24D, 10.0D);
    private static final AABB INPUT_SLOT_AABB = INPUT_SLOT_SHAPE.bounds().inflate(0.01D);

    private static final VoxelShape FULL_SHAPE = Shapes.join(Shapes.or(BASE_SHAPE, HANDSTONE_SHAPE, HANDLE_SHAPE), INPUT_SLOT_SHAPE, BooleanOp.ONLY_FIRST);

    private static SelectionPlace getPlayerSelection(BlockGetter world, BlockPos pos, Player player, BlockHitResult result)
    {
        QuernBlockEntity te = Helpers.getBlockEntity(world, pos, QuernBlockEntity.class);
        if (te != null)
        {
            return te.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).map(inventory -> {
                ItemStack held = player.getItemInHand(InteractionHand.MAIN_HAND);
                Vec3 hit = result.getLocation();
                if (te.hasHandstone())
                {
                    if (!te.isGrinding() && HANDLE_AABB.move(pos).contains(hit))
                    {
                        return SelectionPlace.HANDLE;
                    }
                    else if (!te.isGrinding() && !held.isEmpty() || !inventory.getStackInSlot(QuernBlockEntity.SLOT_INPUT).isEmpty() && INPUT_SLOT_AABB.move(pos).contains(hit))
                    {
                        return SelectionPlace.INPUT_SLOT;
                    }
                }
                if ((te.hasHandstone() || te.isItemValid(QuernBlockEntity.SLOT_HANDSTONE, held)) && HANDSTONE_AABB.move(pos).contains(hit))
                {
                    return SelectionPlace.HANDSTONE;
                }
                return SelectionPlace.BASE;
            }).orElse(SelectionPlace.BASE);
        }
        return SelectionPlace.BASE;
    }

    public QuernBlock(ExtendedProperties properties)
    {
        super(properties);
    }

    private static InteractionResult insertOrExtract(Level level, QuernBlockEntity teQuern, IItemHandler inventory, Player player, ItemStack stack, int slot)
    {
        if (!stack.isEmpty())
        {
            player.setItemInHand(InteractionHand.MAIN_HAND, inventory.insertItem(slot, stack, false));
        }
        else
        {
            ItemHandlerHelper.giveItemToPlayer(player, inventory.extractItem(slot, inventory.getStackInSlot(slot).getCount(), false));
        }
        teQuern.setAndUpdateSlots(slot);
        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    @Override
    @SuppressWarnings("deprecation")
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit)
    {
        if (hand == InteractionHand.MAIN_HAND)
        {
            QuernBlockEntity teQuern = Helpers.getBlockEntity(level, pos, QuernBlockEntity.class);
            if (teQuern != null && !teQuern.isGrinding())
            {
                ItemStack heldStack = player.getItemInHand(hand);
                SelectionPlace selection = getPlayerSelection(level, pos, player, hit);
                return teQuern.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).map(inventory -> {
                    if (selection == SelectionPlace.HANDLE)
                    {
                        teQuern.grind();
                        level.playSound(null, pos, TFCSounds.QUERN_DRAG.get(), SoundSource.BLOCKS, 1, 1 + ((level.random.nextFloat() - level.random.nextFloat()) / 16));
                        return InteractionResult.sidedSuccess(level.isClientSide);
                    }
                    else if (selection == SelectionPlace.INPUT_SLOT)
                    {
                        return insertOrExtract(level, teQuern, inventory, player, heldStack, SLOT_INPUT);
                    }
                    else if (selection == SelectionPlace.HANDSTONE && inventory.getStackInSlot(SLOT_HANDSTONE).isEmpty())
                    {
                        return insertOrExtract(level, teQuern, inventory, player, heldStack, SLOT_HANDSTONE);
                    }
                    else if (selection == SelectionPlace.BASE && !inventory.getStackInSlot(SLOT_OUTPUT).isEmpty())
                    {
                        return insertOrExtract(level, teQuern, inventory, player, ItemStack.EMPTY, SLOT_OUTPUT);
                    }
                    return InteractionResult.FAIL;
                }).orElse(InteractionResult.PASS);
            }
        }
        return InteractionResult.PASS;
    }

    @Override
    @SuppressWarnings("deprecation")
    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context)
    {
        QuernBlockEntity te = Helpers.getBlockEntity(world, pos, QuernBlockEntity.class);
        return te != null && te.hasHandstone() ? FULL_SHAPE : BASE_SHAPE;
    }

    // todo
    // @Override
    public boolean drawHighlight(Level level, BlockPos pos, Player player, BlockHitResult rayTrace, PoseStack matrixStack, MultiBufferSource buffers, Vec3 renderPos)
    {
        SelectionPlace selection = getPlayerSelection(level, pos, player, rayTrace);
        if (selection != SelectionPlace.BASE)
        {
            IHighlightHandler.drawBox(matrixStack, selection.shape, buffers, pos, renderPos, 1.0F, 0.0F, 0.0F, 0.4F);
            return true;
        }
        return false;
    }

    /**
     * Just a helper enum to figure out where player is looking at
     * Used to draw selection boxes + handle interaction
     */
    private enum SelectionPlace
    {
        HANDLE(HANDLE_SHAPE),
        HANDSTONE(HANDSTONE_SHAPE),
        INPUT_SLOT(INPUT_SLOT_SHAPE),
        BASE(BASE_SHAPE);

        public final VoxelShape shape;

        SelectionPlace(VoxelShape shape)
        {
            this.shape = shape;
        }
    }
}
