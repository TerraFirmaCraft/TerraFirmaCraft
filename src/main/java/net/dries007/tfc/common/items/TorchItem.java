/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.items;

import java.util.Random;

import net.minecraft.world.InteractionHand;
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

public class TorchItem extends StandingAndWallBlockItem
{
    public TorchItem(Block floorBlock, Block wallBlockIn, Properties propertiesIn)
    {
        super(floorBlock, wallBlockIn, propertiesIn);
    }

    @Override
    public InteractionResult useOn(UseOnContext context)
    {
        final Level level = context.getLevel();
        final BlockPos pos = context.getClickedPos();
        if (context.getHand() == InteractionHand.MAIN_HAND && StartFireEvent.startFire(level, pos, level.getBlockState(pos), context.getClickedFace(), context.getPlayer(), context.getItemInHand(), StartFireEvent.FireResult.NEVER))
        {
            return InteractionResult.SUCCESS;
        }
        return super.useOn(context);
    }

    @Override
    public boolean onEntityItemUpdate(ItemStack stack, ItemEntity itemEntity)
    {
        final Level level = itemEntity.level;
        final BlockPos pos = itemEntity.blockPosition();
        final BlockState stateAt = level.getBlockState(pos);
        if (Helpers.isFluid(stateAt.getFluidState(), FluidTags.WATER))
        {
            // Chances of dropping are independent of the size of stack dropped in water
            // This puts a 25% chance for each product (independent) per item.
            int sticks = 0, ash = 0;
            for (int i = 0; i < stack.getCount(); i++)
            {
                if (level.random.nextInt(4) == 0)
                {
                    sticks++;
                }
                if (level.random.nextInt(4) == 0)
                {
                    ash++;
                }
            }
            if (sticks > stack.getCount() / 2)
            {
                sticks = stack.getCount() / 2; // Don't duplicate sticks, since 1 stick = 2 torches
            }
            if (sticks > 0)
            {
                Helpers.spawnItem(level, itemEntity.position(), new ItemStack(Items.STICK, sticks));
            }
            if (ash > 0)
            {
                Helpers.spawnItem(level, itemEntity.position(), new ItemStack(TFCItems.POWDERS.get(Powder.WOOD_ASH).get(), ash));
            }
            Helpers.playSound(level, pos, SoundEvents.FIRE_EXTINGUISH);
            itemEntity.discard();
            return true;
        }

        final BlockPos downPos = itemEntity.blockPosition().below();
        final boolean isNotInBlock = level.isEmptyBlock(pos);
        final BlockState checkState = isNotInBlock ? level.getBlockState(downPos) : stateAt;
        final int ageRequirement = isNotInBlock ? 20 : 160;

        if (Helpers.isBlock(checkState, TFCTags.Blocks.LIT_BY_DROPPED_TORCH))
        {
            if (itemEntity.getAge() > ageRequirement && level.random.nextFloat() < 0.01f)
            {
                StartFireEvent.startFire(level, isNotInBlock ? downPos : pos, checkState, Direction.UP, null, ItemStack.EMPTY, StartFireEvent.FireResult.NEVER);
                itemEntity.kill();
            }
            else
            {
                Random rand = level.getRandom();
                if (rand.nextDouble() <= 0.1)
                {
                    level.addParticle(ParticleTypes.LAVA, itemEntity.getX(), itemEntity.getY(), itemEntity.getZ(), -0.5F + rand.nextDouble(), -0.5F + rand.nextDouble(), -0.5F + rand.nextDouble());
                }
            }
        }
        return super.onEntityItemUpdate(stack, itemEntity);
    }
}
