package net.dries007.tfc.common.items;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.item.Items;
import net.minecraft.item.WallOrFloorItem;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.events.StartFireEvent;

public class TorchItem extends WallOrFloorItem
{
    public TorchItem(Block floorBlock, Block wallBlockIn, Properties propertiesIn)
    {
        super(floorBlock, wallBlockIn, propertiesIn);
    }

    @Override
    public ActionResultType useOn(ItemUseContext context)
    {
        World world = context.getLevel();
        BlockPos pos = context.getClickedPos();
        // we cancel the event every time
        StartFireEvent.startFire(world, pos, world.getBlockState(pos), context.getClickedFace(), context.getPlayer(), context.getItemInHand());
        return super.useOn(context);
    }

    @Override
    public boolean onEntityItemUpdate(ItemStack stack, ItemEntity itemEntity)
    {
        final World world = itemEntity.level;
        final BlockPos pos = itemEntity.blockPosition();
        final BlockState stateAt = world.getBlockState(pos);
        if (stateAt.getFluidState().is(FluidTags.WATER))
        {
            itemEntity.setItem(new ItemStack(Items.STICK, stack.getCount()));
            int ash = (int) MathHelper.clamp(stack.getCount() * 0.5 - 4, 0, 8);
            if (ash > 0)
                Helpers.spawnItem(world, pos, new ItemStack(TFCItems.POWDERS.get(Powder.WOOD_ASH).get(), ash));
            Helpers.playSound(world, pos, SoundEvents.FIRE_EXTINGUISH);
            return true;
        }

        final BlockPos downPos = itemEntity.blockPosition().below();
        final boolean isNotInBlock = world.isEmptyBlock(pos);
        final BlockState checkState = isNotInBlock ? world.getBlockState(downPos) : stateAt;
        final int ageRequirement = isNotInBlock ? 20 : 160;

        if (checkState.is(TFCTags.Blocks.LIT_BY_DROPPED_TORCH))
        {
            if (itemEntity.getAge() > ageRequirement && random.nextFloat() < 0.01f)
            {
                StartFireEvent.startFire(world, isNotInBlock ? downPos : pos, checkState, Direction.UP, null, null);
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
