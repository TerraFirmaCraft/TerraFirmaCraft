/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blocks.devices;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
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
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;

import net.dries007.tfc.client.IHighlightHandler;
import net.dries007.tfc.client.TFCSounds;
import net.dries007.tfc.common.blockentities.QuernBlockEntity;
import net.dries007.tfc.common.blockentities.TFCBlockEntities;
import net.dries007.tfc.common.blocks.ExtendedProperties;
import net.dries007.tfc.common.blocks.TFCBlockStateProperties;
import net.dries007.tfc.common.capabilities.Capabilities;
import net.dries007.tfc.util.Helpers;

import static net.dries007.tfc.common.blockentities.QuernBlockEntity.*;

public class QuernBlock extends DeviceBlock implements IHighlightHandler
{
    private static final VoxelShape BASE_SHAPE = box(0.0D, 0.0D, 0.0D, 16.0D, 10.0D, 16.0D);
    private static final AABB BASE_AABB = BASE_SHAPE.bounds().inflate(0.01D);

    private static final VoxelShape HANDSTONE_SHAPE = box(3.0D, 10.0D, 3.0D, 13.0D, 13.76D, 13.0D);
    private static final AABB HANDSTONE_AABB = HANDSTONE_SHAPE.bounds().inflate(0.01D);

    private static final VoxelShape HANDLE_SHAPE = box(4.34D, 13.76D, 4.34D, 5.36D, 16.24D, 5.36D);
    private static final AABB HANDLE_AABB = HANDLE_SHAPE.bounds().inflate(0.01D);

    private static final VoxelShape INPUT_SLOT_SHAPE = box(6.0D, 13.76D, 6.0D, 10.0D, 16.24D, 10.0D);
    private static final AABB INPUT_SLOT_AABB = INPUT_SLOT_SHAPE.bounds().inflate(0.01D);

    private static final VoxelShape FULL_SHAPE = Shapes.join(Shapes.or(BASE_SHAPE, HANDSTONE_SHAPE, HANDLE_SHAPE), INPUT_SLOT_SHAPE, BooleanOp.ONLY_FIRST);
    private static final VoxelShape COLLISION_FULL_SHAPE = Shapes.or(BASE_SHAPE, HANDSTONE_SHAPE);

    private static SelectionPlace getPlayerSelection(BlockGetter level, BlockPos pos, Player player, BlockHitResult result)
    {
        return level.getBlockEntity(pos, TFCBlockEntities.QUERN.get())
            .flatMap(quern -> quern.getCapability(Capabilities.ITEM)
                .map(inventory -> {
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
                    return SelectionPlace.BASE;
                }))
            .orElse(SelectionPlace.BASE);
    }

    private static InteractionResult insertOrExtract(Level level, QuernBlockEntity quern, IItemHandler inventory, Player player, ItemStack stack, int slot)
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
        return InteractionResult.sidedSuccess(level.isClientSide);
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
    public void stepOn(Level level, BlockPos pos, BlockState state, Entity entity)
    {
        level.getBlockEntity(pos, TFCBlockEntities.QUERN.get()).ifPresent(quern -> {
            if (quern.isGrinding() && HANDSTONE_AABB.move(pos).contains(entity.position()) && !BASE_AABB.contains(entity.position()))
            {
                entity.setYRot((entity.getYRot() + 4f) % 360f);
            }
        });
    }

    @Override
    @SuppressWarnings("deprecation")
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit)
    {
        final QuernBlockEntity quern = level.getBlockEntity(pos, TFCBlockEntities.QUERN.get()).orElse(null);
        if (quern != null && !quern.isGrinding())
        {
            final ItemStack heldStack = player.getItemInHand(hand);
            final SelectionPlace selection = getPlayerSelection(level, pos, player, hit);
            return quern.getCapability(Capabilities.ITEM).map(inventory -> switch (selection)
                    {
                        case HANDLE ->
                        {
                            if (quern.startGrinding())
                            {
                                level.playSound(null, pos, TFCSounds.QUERN_DRAG.get(), SoundSource.BLOCKS, 1, 1 + ((level.random.nextFloat() - level.random.nextFloat()) / 16));
                                yield InteractionResult.sidedSuccess(level.isClientSide);
                            }
                            yield InteractionResult.FAIL;
                        }
                        case INPUT_SLOT -> insertOrExtract(level, quern, inventory, player, heldStack, SLOT_INPUT);
                        case HANDSTONE -> insertOrExtract(level, quern, inventory, player, heldStack, SLOT_HANDSTONE);
                        case BASE -> insertOrExtract(level, quern, inventory, player, ItemStack.EMPTY, SLOT_OUTPUT);
                    })
                .orElse(InteractionResult.PASS);
        }
        return InteractionResult.PASS;
    }

    @Override
    @SuppressWarnings("deprecation")
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context)
    {
        return state.getValue(HAS_HANDSTONE) ? FULL_SHAPE : BASE_SHAPE;
    }

    @Override
    @SuppressWarnings("deprecation")
    public VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context)
    {
        return state.getValue(HAS_HANDSTONE) ? COLLISION_FULL_SHAPE : BASE_SHAPE;
    }

    @Override
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

        final VoxelShape shape;

        SelectionPlace(VoxelShape shape)
        {
            this.shape = shape;
        }
    }
}
