/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.items;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.item.StandingAndWallBlockItem;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.InteractionResult;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.blocks.TFCBlocks;
import net.dries007.tfc.common.fluids.FluidHelpers;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.events.StartFireEvent;
import net.dries007.tfc.util.loot.TFCLoot;

public class TorchItem extends StandingAndWallBlockItem
{
    public TorchItem(Block floorBlock, Block wallBlockIn, Properties propertiesIn)
    {
        super(floorBlock, wallBlockIn, propertiesIn, Direction.DOWN);
    }

    @Override
    public InteractionResult useOn(UseOnContext context)
    {
        final Level level = context.getLevel();
        final BlockPos pos = context.getClickedPos();
        if (StartFireEvent.startFire(level, pos, level.getBlockState(pos), context.getClickedFace(), context.getPlayer(), context.getItemInHand(), StartFireEvent.FireStrength.WEAK))
        {
            return InteractionResult.sidedSuccess(level.isClientSide);
        }
        return super.useOn(context);
    }

    @Override
    public boolean onEntityItemUpdate(ItemStack stack, ItemEntity itemEntity)
    {
        final Level level = itemEntity.level();
        final BlockPos pos = itemEntity.blockPosition();
        final BlockState stateAt = level.getBlockState(pos);

        if (FluidHelpers.canFluidExtinguishFire(stateAt.getFluidState().getType()))
        {
            final int amount = stack.getCount() > 5 ? 1 + level.random.nextInt(5) : stack.getCount();
            if (level instanceof ServerLevel serverLevel)
            {
                for (int i = 0; i < amount; i++)
                {
                    Helpers.dropWithContext(serverLevel, TFCBlocks.TORCH.get().defaultBlockState(), pos, builder -> builder.withParameter(TFCLoot.BURNT_OUT, true), true);
                }
            }

            Helpers.playSound(level, pos, SoundEvents.FIRE_EXTINGUISH);

            stack.shrink(amount);
            if (stack.isEmpty())
            {
                itemEntity.discard();
                return true;
            }
            return false;
        }

        final BlockPos downPos = itemEntity.blockPosition().below();
        final boolean isNotInBlock = level.isEmptyBlock(pos);
        final BlockState checkState = isNotInBlock ? level.getBlockState(downPos) : stateAt;
        final int ageRequirement = isNotInBlock ? 20 : 160;

        if (Helpers.isBlock(checkState, TFCTags.Blocks.LIT_BY_DROPPED_TORCH))
        {
            if (itemEntity.getAge() > ageRequirement && level.random.nextFloat() < 0.01f && !level.isClientSide())
            {
                StartFireEvent.startFire(level, isNotInBlock ? downPos : pos, checkState, Direction.UP, null, ItemStack.EMPTY, StartFireEvent.FireStrength.STRONG);
                itemEntity.kill();
                return true;
            }
            else
            {
                final RandomSource rand = level.getRandom();
                if (rand.nextDouble() <= 0.1)
                {
                    level.addParticle(ParticleTypes.LAVA, itemEntity.getX(), itemEntity.getY(), itemEntity.getZ(), -0.5F + rand.nextDouble(), -0.5F + rand.nextDouble(), -0.5F + rand.nextDouble());
                }
            }
        }
        return false;
    }
}
