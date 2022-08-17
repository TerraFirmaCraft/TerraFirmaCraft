/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.items;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.items.IItemHandlerModifiable;

import net.dries007.tfc.client.TFCSounds;
import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.blockentities.AbstractFirepitBlockEntity;
import net.dries007.tfc.common.blockentities.TFCBlockEntities;
import net.dries007.tfc.common.blocks.TFCBlocks;
import net.dries007.tfc.common.blocks.devices.FirepitBlock;
import net.dries007.tfc.config.TFCConfig;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.events.StartFireEvent;

public class FirestarterItem extends Item
{
    public FirestarterItem(Item.Properties properties)
    {
        super(properties);
    }

    @Override
    @SuppressWarnings("deprecation")
    public void onUseTick(Level world, LivingEntity livingEntityIn, ItemStack stack, int countLeft)
    {
        if (livingEntityIn instanceof final Player player)
        {
            final BlockHitResult result = getPlayerPOVHitResult(world, player, ClipContext.Fluid.NONE);

            final BlockPos pos = result.getBlockPos();
            final BlockPos abovePos = pos.above();
            double chance = TFCConfig.SERVER.fireStarterChance.get() * (world.isRainingAt(abovePos) ? 0.3 : 1);
            if (world.isClientSide())
            {
                Vec3 location = result.getLocation();
                makeEffects(world, player, location.x(), location.y(), location.z(), countLeft, getUseDuration(stack), world.random);
            }
            else if (countLeft == 1)
            {
                if (!player.isCreative())
                {
                    stack.hurtAndBreak(1, player, p -> p.broadcastBreakEvent(InteractionHand.MAIN_HAND));
                }
                if (FirepitBlock.canSurvive(world, pos)) // firepit
                {
                    final List<ItemEntity> items = world.getEntitiesOfClass(ItemEntity.class, new AABB(abovePos, abovePos.offset(1, 2, 1)));
                    final List<ItemEntity> usableItems = new ArrayList<>();

                    int sticks = 0, kindling = 0;
                    ItemEntity logEntity = null;

                    for (ItemEntity entity : items)
                    {
                        ItemStack foundStack = entity.getItem();
                        Item foundItem = foundStack.getItem();
                        int itemCount = foundStack.getCount();
                        if (Helpers.isItem(foundItem, TFCTags.Items.FIREPIT_STICKS))
                        {
                            sticks += itemCount;
                            usableItems.add(entity);
                        }
                        else if (Helpers.isItem(foundItem, TFCTags.Items.FIREPIT_KINDLING))
                        {
                            kindling += itemCount;
                            usableItems.add(entity);
                        }
                        else if (logEntity == null && Helpers.isItem(foundItem, TFCTags.Items.FIREPIT_LOGS))
                        {
                            logEntity = entity;
                        }
                    }
                    if (sticks >= 3 && logEntity != null)
                    {
                        final float kindlingModifier = Math.min(0.1F * (float) kindling, 0.5F);
                        if (world.random.nextFloat() < chance + kindlingModifier)
                        {
                            usableItems.forEach(Entity::kill);
                            logEntity.kill();

                            ItemStack initialLog = logEntity.getItem().copy();
                            initialLog.setCount(1);

                            final BlockState state = TFCBlocks.FIREPIT.get().defaultBlockState();
                            world.setBlock(abovePos, state, 3);
                            world.getBlockEntity(abovePos, TFCBlockEntities.FIREPIT.get()).ifPresent(firepit -> firepit.getCapability(Capabilities.ITEM).ifPresent(cap -> {
                                if (cap instanceof IItemHandlerModifiable modifiableInventory)
                                {
                                    modifiableInventory.setStackInSlot(AbstractFirepitBlockEntity.SLOT_FUEL_CONSUME, initialLog);
                                }
                                firepit.light(state);
                            }));
                        }
                        return;
                    }
                }
                // if can't make a firepit, try to light the block
                StartFireEvent.startFire(world, pos, world.getBlockState(pos), result.getDirection(), player, stack);
            }
        }
    }

    @Override
    public InteractionResult useOn(UseOnContext context)
    {
        Level world = context.getLevel();
        if (context.getHand() != InteractionHand.MAIN_HAND || world.isClientSide())
            return InteractionResult.PASS;
        Player player = context.getPlayer();
        if (player == null)
            return InteractionResult.FAIL;
        player.startUsingItem(InteractionHand.MAIN_HAND);
        return InteractionResult.SUCCESS;
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack)
    {
        return UseAnim.BOW;
    }

    @Override
    public int getUseDuration(ItemStack stack)
    {
        return 72;
    }

    private void makeEffects(Level world, Player player, double x, double y, double z, int countLeft, int total, Random random)
    {
        int count = total - countLeft;
        if (random.nextFloat() + 0.3 < count / (double) total)
        {
            world.addParticle(ParticleTypes.SMOKE, x, y, z, 0.0F, 0.1F, 0.0F);
        }
        if (countLeft < 10 && random.nextFloat() + 0.3 < count / (double) total)
        {
            world.addParticle(ParticleTypes.FLAME, x, y, z, 0.0F, 0.1F, 0.0F);
        }
        if (count % 3 == 1)
        {
            player.playSound(TFCSounds.FIRESTARTER.get(), 0.5F, 0.05F);
        }
    }
}
