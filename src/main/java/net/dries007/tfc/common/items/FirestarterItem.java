/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.items;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.item.UseAction;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;

import net.dries007.tfc.client.TFCSounds;
import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.blocks.TFCBlocks;
import net.dries007.tfc.common.blocks.devices.FirepitBlock;
import net.dries007.tfc.common.tileentity.AbstractFirepitTileEntity;
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
    public void onUseTick(World world, LivingEntity livingEntityIn, ItemStack stack, int countLeft)
    {
        if (!(livingEntityIn instanceof PlayerEntity)) return;
        final PlayerEntity player = (PlayerEntity) livingEntityIn;
        final BlockRayTraceResult result = getPlayerPOVHitResult(world, player, RayTraceContext.FluidMode.NONE);

        final BlockPos pos = result.getBlockPos();
        final BlockPos abovePos = pos.above();
        double chance = TFCConfig.SERVER.fireStarterChance.get() * (world.isRainingAt(abovePos) ? 0.3 : 1);
        if (world.isClientSide())
        {
            Vector3d location = result.getLocation();
            makeEffects(world, player, location.x(), location.y(), location.z(), countLeft, getUseDuration(stack), random);
        }
        else if (countLeft == 1)
        {
            if (!player.isCreative())
            {
                stack.hurtAndBreak(1, player, p -> p.broadcastBreakEvent(Hand.MAIN_HAND));
            }
            if (FirepitBlock.canSurvive(world, pos)) // firepit
            {
                final List<ItemEntity> items = world.getEntitiesOfClass(ItemEntity.class, new AxisAlignedBB(abovePos, abovePos.offset(1, 2, 1)));
                final List<ItemEntity> usableItems = new ArrayList<>();

                int sticks = 0, kindling = 0;
                ItemEntity logEntity = null;

                for (ItemEntity entity : items)
                {
                    ItemStack foundStack = entity.getItem();
                    Item foundItem = foundStack.getItem();
                    int itemCount = foundStack.getCount();
                    if (foundItem.is(TFCTags.Items.FIREPIT_STICKS))
                    {
                        sticks += itemCount;
                        usableItems.add(entity);
                    }
                    else if (foundItem.is(TFCTags.Items.FIREPIT_KINDLING))
                    {
                        kindling += itemCount;
                        usableItems.add(entity);
                    }
                    else if (logEntity == null && foundItem.is(TFCTags.Items.FIREPIT_LOGS))
                    {
                        logEntity = entity;
                    }
                }
                if (sticks >= 3 && logEntity != null)
                {
                    final float kindlingModifier = Math.min(0.1F * (float) kindling, 0.5F);
                    if (random.nextFloat() < chance + kindlingModifier)
                    {
                        usableItems.forEach(Entity::kill);
                        logEntity.kill();

                        ItemStack initialLog = logEntity.getItem().copy();
                        initialLog.setCount(1);

                        final BlockState state = TFCBlocks.FIREPIT.get().defaultBlockState();
                        world.setBlock(abovePos, state, 3);
                        AbstractFirepitTileEntity<?> firepit = Helpers.getTileEntity(world, abovePos, AbstractFirepitTileEntity.class);
                        if (firepit != null)
                        {
                            firepit.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
                                .ifPresent(cap -> ((IItemHandlerModifiable) cap).setStackInSlot(AbstractFirepitTileEntity.SLOT_FUEL_CONSUME, initialLog));
                            firepit.light(state);
                        }
                    }
                    return;
                }
            }
            // if can't make a firepit, try to light the block
            StartFireEvent.startFire(world, pos, world.getBlockState(pos), result.getDirection(), player, stack);
        }
    }

    @Override
    public ActionResultType useOn(ItemUseContext context)
    {
        World world = context.getLevel();
        if (context.getHand() != Hand.MAIN_HAND || world.isClientSide())
            return ActionResultType.PASS;
        PlayerEntity player = context.getPlayer();
        if (player == null)
            return ActionResultType.FAIL;
        player.startUsingItem(Hand.MAIN_HAND);
        return ActionResultType.SUCCESS;
    }

    @Override
    public UseAction getUseAnimation(ItemStack stack)
    {
        return UseAction.BOW;
    }

    @Override
    public int getUseDuration(ItemStack stack)
    {
        return 72;
    }

    private void makeEffects(World world, PlayerEntity player, double x, double y, double z, int countLeft, int total, Random random)
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
