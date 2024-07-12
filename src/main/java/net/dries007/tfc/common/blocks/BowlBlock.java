/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.items.ItemHandlerHelper;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.blockentities.BowlBlockEntity;
import net.dries007.tfc.common.blocks.devices.DeviceBlock;
import net.dries007.tfc.common.capabilities.Capabilities;
import net.dries007.tfc.common.capabilities.food.FoodCapability;
import net.dries007.tfc.common.capabilities.food.FoodTraits;
import net.dries007.tfc.common.capabilities.food.IFood;
import net.dries007.tfc.common.component.glass.GlassOperation;
import net.dries007.tfc.common.component.glass.GlassOperations;
import net.dries007.tfc.common.component.glass.GlassWorking;
import net.dries007.tfc.common.items.Powder;
import net.dries007.tfc.common.items.TFCItems;
import net.dries007.tfc.util.Helpers;

public class BowlBlock extends DeviceBlock
{
    public static final VoxelShape SHAPE = box(2, 0, 2, 14, 3, 14);

    public BowlBlock(ExtendedProperties properties)
    {
        super(properties, InventoryRemoveBehavior.DROP);
    }

    @Override
    @SuppressWarnings("deprecation")
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context)
    {
        return SHAPE;
    }

    @Override
    @SuppressWarnings("deprecation")
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult)
    {
        if (level.getBlockEntity(pos) instanceof BowlBlockEntity bowl)
        {
            final var inv = Helpers.getCapability(bowl, Capabilities.ITEM);
            if (inv != null)
            {
                final ItemStack held = player.getItemInHand(hand);
                final ItemStack current = inv.getStackInSlot(0);
                final GlassOperations data = GlassWorking.get(held);
                if (!data.isEmpty())
                {
                    final GlassOperation op = GlassOperation.getByPowder(current);
                    if (op != null)
                    {
                        GlassWorking.apply(held, op);
                        inv.getStackInSlot(0).shrink(1);
                        Helpers.playSound(level, pos, SoundEvents.SAND_PLACE);
                        player.getCooldowns().addCooldown(held.getItem(), 10);
                        return InteractionResult.sidedSuccess(level.isClientSide);
                    }
                    return InteractionResult.PASS;
                }
                if (held.isEmpty() && hand == InteractionHand.MAIN_HAND)
                {
                    ItemHandlerHelper.giveItemToPlayer(player, inv.extractItem(0, player.isShiftKeyDown() ? 16 : 1, false));
                    Helpers.playSound(level, pos, SoundEvents.SAND_PLACE);
                    return InteractionResult.sidedSuccess(level.isClientSide);
                }
                if (Helpers.isItem(held, TFCTags.Items.POWDERS))
                {
                    player.setItemInHand(hand, Helpers.insertAllSlots(inv, held));
                    Helpers.playSound(level, pos, SoundEvents.SAND_PLACE);
                    return InteractionResult.sidedSuccess(level.isClientSide);
                }
                if (Helpers.isItem(held, TFCTags.Items.CAN_BE_SALTED) && Helpers.isItem(current, TFCItems.POWDERS.get(Powder.SALT).get()))
                {
                    final @Nullable IFood food = FoodCapability.get(held);
                    if (food != null && !food.hasTrait(FoodTraits.SALTED))
                    {
                        final int toSalt = Math.min(held.getCount(), current.getCount());
                        final ItemStack salted = held.split(toSalt);
                        FoodCapability.applyTrait(salted, FoodTraits.SALTED);
                        ItemHandlerHelper.giveItemToPlayer(player, salted);
                        inv.getStackInSlot(0).shrink(toSalt);
                        Helpers.playSound(level, pos, SoundEvents.SAND_PLACE);
                        return InteractionResult.sidedSuccess(level.isClientSide);
                    }
                }
            }
        }
        return InteractionResult.PASS;
    }
}
