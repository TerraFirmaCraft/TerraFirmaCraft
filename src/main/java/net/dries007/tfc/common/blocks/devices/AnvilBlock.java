/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blocks.devices;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.items.IItemHandler;

import net.dries007.tfc.client.particle.TFCParticles;
import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.blockentities.AnvilBlockEntity;
import net.dries007.tfc.common.blockentities.TFCBlockEntities;
import net.dries007.tfc.common.blocks.ExtendedProperties;
import net.dries007.tfc.common.capabilities.Capabilities;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.Metal;

public class AnvilBlock extends DeviceBlock implements Tiered
{
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;

    private static final VoxelShape SHAPE_X = box(0, 0, 3, 16, 11, 13);
    private static final VoxelShape SHAPE_Z = box(3, 0, 0, 13, 11, 16);

    public static InteractionResult interactWithAnvil(Level level, BlockPos pos, Player player, InteractionHand hand)
    {
        final AnvilBlockEntity anvil = level.getBlockEntity(pos, TFCBlockEntities.ANVIL.get()).orElse(null);
        if (anvil == null)
        {
            return InteractionResult.PASS;
        }
        final IItemHandler inventory = anvil.getCapability(Capabilities.ITEM).resolve().orElse(null);
        if (inventory == null)
        {
            return InteractionResult.PASS;
        }
        if (player.isShiftKeyDown())
        {
            final ItemStack playerStack = player.getItemInHand(hand);
            if (playerStack.isEmpty()) // Extraction requires held item to be empty
            {
                for (int slot : AnvilBlockEntity.SLOTS_BY_HAND_EXTRACT)
                {
                    final ItemStack anvilStack = inventory.getStackInSlot(slot);
                    if (!anvilStack.isEmpty())
                    {
                        // Give the item to player in the main hand
                        ItemStack result = inventory.extractItem(slot, 1, false);
                        player.setItemInHand(hand, result);
                        return InteractionResult.SUCCESS;
                    }
                }
            }
            else if (Helpers.isItem(playerStack, TFCTags.Items.HAMMERS)) // Attempt welding with a hammer in hand
            {
                final InteractionResult weldResult = anvil.weld(player);
                if (weldResult == InteractionResult.SUCCESS)
                {
                    // Welding occurred
                    if (level instanceof ServerLevel server)
                    {
                        final double x = pos.getX() + Mth.nextDouble(level.random, 0.2, 0.8);
                        final double z = pos.getX() + Mth.nextDouble(level.random, 0.2, 0.8);
                        final double y = pos.getY() + Mth.nextDouble(level.random, 0.8, 1.0);
                        server.sendParticles(TFCParticles.SPARK.get(), x, y, z, 5, Helpers.uniform(level.random, -5f, 5f), 1.5 + level.random.nextFloat(), Helpers.uniform(level.random, -5f, 5f), 5f);
                    }
                    level.playSound(null, pos, SoundEvents.ANVIL_USE, SoundSource.PLAYERS, 0.6f, 1.0f);
                    return InteractionResult.SUCCESS;
                }
                else if (weldResult == InteractionResult.FAIL)
                {
                    // Welding was attempted, but failed for some reason - player was alerted and action was consumed.
                    // Returning fail here causes the off hand to still attempt to be used?
                    return InteractionResult.SUCCESS;
                }
            }
            else
            {
                // Try and insert an item
                final ItemStack insertStack = playerStack.copy();
                for (int slot : AnvilBlockEntity.SLOTS_BY_HAND_INSERT)
                {
                    final ItemStack resultStack = inventory.insertItem(slot, insertStack, false);
                    if (insertStack.getCount() > resultStack.getCount())
                    {
                        // At least one item was inserted (and so remainder < attempt)
                        player.setItemInHand(hand, resultStack);
                        return InteractionResult.SUCCESS;
                    }
                }
            }
        }
        else
        {
            // Not shifting, so attempt to open the anvil gui
            if (player instanceof ServerPlayer serverPlayer)
            {
                Helpers.openScreen(serverPlayer, anvil.anvilProvider(), pos);
            }
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }

    private final int tier;

    public AnvilBlock(ExtendedProperties properties, Metal.Tier tier)
    {
        this(properties, tier.ordinal());
    }

    public AnvilBlock(ExtendedProperties properties, int tier)
    {
        super(properties, InventoryRemoveBehavior.DROP);
        this.tier = tier;
    }

    @Override
    @SuppressWarnings("deprecation")
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit)
    {
        return AnvilBlock.interactWithAnvil(level, pos, player, hand);
    }

    @Override
    @SuppressWarnings("deprecation")
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context)
    {
        return state.getValue(FACING).getAxis() == Direction.Axis.X ? SHAPE_X : SHAPE_Z;
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context)
    {
        return defaultBlockState().setValue(FACING, context.getHorizontalDirection().getClockWise());
    }

    @Override
    public int getTier()
    {
        return tier;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        super.createBlockStateDefinition(builder.add(FACING));
    }

    @Override
    @SuppressWarnings("deprecation")
    public BlockState rotate(BlockState state, Rotation rot)
    {
        return state.setValue(FACING, rot.rotate(state.getValue(FACING)));
    }

    @Override
    @SuppressWarnings("deprecation")
    public BlockState mirror(BlockState state, Mirror mirror)
    {
        return state.rotate(mirror.getRotation(state.getValue(FACING)));
    }
}
