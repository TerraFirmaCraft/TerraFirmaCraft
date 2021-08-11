/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.items;

import java.util.Random;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.StandingAndWallBlockItem;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.InteractionResult;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;

import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.events.StartFireEvent;

import net.minecraft.world.item.Item.Properties;

public class TorchItem extends StandingAndWallBlockItem
{
    public TorchItem(Block floorBlock, Block wallBlockIn, Properties propertiesIn)
    {
        super(floorBlock, wallBlockIn, propertiesIn);
    }

    @Override
    public InteractionResult useOn(UseOnContext context)
    {
        final Level world = context.getLevel();
        final BlockPos pos = context.getClickedPos();
        if (StartFireEvent.startFire(world, pos, world.getBlockState(pos), context.getClickedFace(), context.getPlayer(), context.getItemInHand(), false))
        {
            return InteractionResult.SUCCESS;
        }
        return super.useOn(context);
    }

    @Override
    public boolean onEntityItemUpdate(ItemStack stack, ItemEntity itemEntity)
    {
        final Level world = itemEntity.level;
        final BlockPos pos = itemEntity.blockPosition();
        final BlockState stateAt = world.getBlockState(pos);
        if (stateAt.getFluidState().is(FluidTags.WATER))
        {
            itemEntity.setItem(new ItemStack(Items.STICK, stack.getCount()));
            int ash = (int) Mth.clamp(stack.getCount() * 0.5 - 4, 0, 8);
            if (ash > 0)
            {
                Helpers.spawnItem(world, pos, new ItemStack(TFCItems.POWDERS.get(Powder.WOOD_ASH).get(), ash));
            }
            Helpers.playSound(world, pos, SoundEvents.FIRE_EXTINGUISH);
            return true;
        }

        final BlockPos downPos = itemEntity.blockPosition().below();
        final boolean isNotInBlock = world.isEmptyBlock(pos);
        final BlockState checkState = isNotInBlock ? world.getBlockState(downPos) : stateAt;
        final int ageRequirement = isNotInBlock ? 20 : 160;

        if (checkState.is(TFCTags.Blocks.LIT_BY_DROPPED_TORCH))
        {
            if (itemEntity.getAge() > ageRequirement && world.random.nextFloat() < 0.01f)
            {
                StartFireEvent.startFire(world, isNotInBlock ? downPos : pos, checkState, Direction.UP, null, null, false);
                itemEntity.kill();
            }
            else
            {
                Random rand = world.getRandom();
                if (rand.nextDouble() <= 0.1)
                {
                    world.addParticle(ParticleTypes.LAVA, itemEntity.getX(), itemEntity.getY(), itemEntity.getZ(), -0.5F + rand.nextDouble(), -0.5F + rand.nextDouble(), -0.5F + rand.nextDouble());
                }
            }
        }
        return super.onEntityItemUpdate(stack, itemEntity);
    }
}
