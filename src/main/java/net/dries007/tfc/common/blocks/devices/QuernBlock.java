/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blocks.devices;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Constants;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemHandlerHelper;

import net.dries007.tfc.client.IHighlightHandler;
import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.blockentities.QuernBlockEntity;
import net.dries007.tfc.common.blockentities.TFCBlockEntities;
import net.dries007.tfc.common.blocks.ExtendedProperties;
import net.dries007.tfc.common.blocks.TFCBlockStateProperties;
import net.dries007.tfc.util.Helpers;

import static net.dries007.tfc.common.blockentities.QuernBlockEntity.*;

public class QuernBlock extends DeviceBlock implements IHighlightHandler
{
    private static final VoxelShape BASE_SHAPE = box(0.0D, 0.0D, 0.0D, 16.0D, 10.0D, 16.0D);
    private static final AABB BASE_AABB = BASE_SHAPE.bounds().inflate(0.01D);

    private static final VoxelShape HANDSTONE_SHAPE = box(3.0D, 10.0D, 3.0D, 13.0D, 13.76D, 13.0D);
    private static final AABB HANDSTONE_AABB = HANDSTONE_SHAPE.bounds().inflate(0.01D);
    private static final Vec3 HANDSTONE_CENTER = HANDSTONE_SHAPE.bounds().getCenter();

    private static final VoxelShape HANDLE_SHAPE = box(4.34D, 13.76D, 4.34D, 5.36D, 16.24D, 5.36D);
    private static final AABB HANDLE_AABB = HANDLE_SHAPE.bounds().inflate(0.01D);

    private static final VoxelShape INPUT_SLOT_SHAPE = box(6.0D, 13.76D, 6.0D, 10.0D, 16.24D, 10.0D);
    private static final AABB INPUT_SLOT_AABB = INPUT_SLOT_SHAPE.bounds().inflate(0.01D);

    private static final VoxelShape FULL_SHAPE = Shapes.join(Shapes.or(BASE_SHAPE, HANDSTONE_SHAPE, HANDLE_SHAPE), INPUT_SLOT_SHAPE, BooleanOp.ONLY_FIRST);
    private static final VoxelShape COLLISION_FULL_SHAPE = Shapes.or(BASE_SHAPE, HANDSTONE_SHAPE);

    private static SelectionPlace getPlayerSelection(BlockGetter level, BlockPos pos, Player player, BlockHitResult result)
    {
        final QuernBlockEntity quern = level.getBlockEntity(pos, TFCBlockEntities.QUERN.get()).orElse(null);
        if (quern != null)
        {
            final IItemHandler inventory = quern.getInventory();
            final ItemStack held = player.getItemInHand(InteractionHand.MAIN_HAND);
            final Vec3 hit = result.getLocation();
            if (quern.hasHandstone())
            {
                if (!quern.isGrinding() && HANDLE_AABB.move(pos).contains(hit))
                {
                    return SelectionPlace.HANDLE;
                }
                else if (!quern.isGrinding() && !held.isEmpty() || !inventory.getStackInSlot(SLOT_INPUT).isEmpty() && INPUT_SLOT_AABB.move(pos).contains(hit))
                {
                    return SelectionPlace.INPUT_SLOT;
                }
            }
            if ((quern.hasHandstone() || quern.isItemValid(SLOT_HANDSTONE, held)) && HANDSTONE_AABB.move(pos).contains(hit))
            {
                return SelectionPlace.HANDSTONE;
            }
        }
        return SelectionPlace.BASE;
    }

    private static ItemInteractionResult insertOrExtract(Level level, QuernBlockEntity quern, IItemHandler inventory, Player player, ItemStack stack, int slot)
    {
        if (!stack.isEmpty())
        {
            player.setItemInHand(InteractionHand.MAIN_HAND, inventory.insertItem(slot, stack, false));
        }
        else
        {
            ItemHandlerHelper.giveItemToPlayer(player, inventory.extractItem(slot, inventory.getStackInSlot(slot).getCount(), false));
            if (slot == SLOT_HANDSTONE)
            {
                insertOrExtract(level, quern, inventory, player, ItemStack.EMPTY, SLOT_INPUT); // extract the grinding item too
            }
        }
        quern.setAndUpdateSlots(slot);
        quern.markForSync();
        return ItemInteractionResult.sidedSuccess(level.isClientSide);
    }

    public static final BooleanProperty HAS_HANDSTONE = TFCBlockStateProperties.HAS_HANDSTONE;

    public QuernBlock(ExtendedProperties properties)
    {
        super(properties, InventoryRemoveBehavior.DROP);
        registerDefaultState(getStateDefinition().any().setValue(HAS_HANDSTONE, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        super.createBlockStateDefinition(builder.add(HAS_HANDSTONE));
    }

    @Override
    protected void entityInside(BlockState state, Level level, BlockPos pos, Entity entity)
    {
        if (level.getBlockEntity(pos) instanceof QuernBlockEntity quern)
        {
            final float rotationSpeed = quern.getRotationSpeed();
            if (rotationSpeed != 0f && HANDSTONE_AABB.move(pos).contains(entity.position()) && !BASE_AABB.contains(entity.position()))
            {
                Helpers.rotateEntity(level, entity, HANDSTONE_CENTER.add(pos.getX(), pos.getY(), pos.getZ()), -rotationSpeed * Constants.RAD_TO_DEG);
            }
        }
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult)
    {
        final QuernBlockEntity quern = level.getBlockEntity(pos, TFCBlockEntities.QUERN.get()).orElse(null);
        if (quern != null && !quern.isGrinding())
        {
            final IItemHandler inventory = quern.getInventory();
            final ItemStack heldStack = player.getItemInHand(hand);
            final SelectionPlace selection = getPlayerSelection(level, pos, player, hitResult);
            return switch (selection)
            {
                case HANDLE -> attemptGrind(level, pos, quern);
                case INPUT_SLOT -> insertOrExtract(level, quern, inventory, player, heldStack, SLOT_INPUT);
                case HANDSTONE -> (player.isShiftKeyDown() || Helpers.isItem(heldStack, TFCTags.Items.QUERN_HANDSTONES))
                    ? insertOrExtract(level, quern, inventory, player, heldStack, SLOT_HANDSTONE)
                    : attemptGrind(level, pos, quern);
                case BASE -> insertOrExtract(level, quern, inventory, player, ItemStack.EMPTY, SLOT_OUTPUT);
            };
        }
        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    private ItemInteractionResult attemptGrind(Level level, BlockPos pos, QuernBlockEntity quern)
    {
        return !quern.isConnectedToNetwork() && quern.startGrinding()
            ? ItemInteractionResult.sidedSuccess(level.isClientSide)
            : ItemInteractionResult.FAIL;
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context)
    {
        return state.getValue(HAS_HANDSTONE) ? FULL_SHAPE : BASE_SHAPE;
    }

    @Override
    protected VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context)
    {
        return state.getValue(HAS_HANDSTONE) ? COLLISION_FULL_SHAPE : BASE_SHAPE;
    }

    @Override
    public boolean drawHighlight(Level level, BlockPos pos, Player player, BlockHitResult rayTrace, PoseStack poseStack, MultiBufferSource buffers, Vec3 renderPos)
    {
        SelectionPlace selection = getPlayerSelection(level, pos, player, rayTrace);
        if (selection != SelectionPlace.BASE)
        {
            IHighlightHandler.drawBox(poseStack, selection.shape, buffers, pos, renderPos, 1.0F, 0.0F, 0.0F, 0.4F);
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

        final VoxelShape shape;

        SelectionPlace(VoxelShape shape)
        {
            this.shape = shape;
        }
    }
}
