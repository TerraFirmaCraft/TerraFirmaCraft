/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.util;

import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockSource;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.core.dispenser.DispenseItemBehavior;
import net.minecraft.core.dispenser.OptionalDispenseItemBehavior;
import net.minecraft.world.entity.animal.horse.AbstractChestedHorse;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.phys.AABB;

import net.dries007.tfc.common.blocks.TFCBlocks;
import net.dries007.tfc.common.blocks.wood.Wood;
import net.dries007.tfc.common.items.TFCItems;

public class DispenserBehaviors
{
    private static final DispenseItemBehavior DEFAULT = new DefaultDispenseItemBehavior();

    private static final DispenseItemBehavior BUCKET_BEHAVIOR = new DefaultDispenseItemBehavior()
    {
        @Override
        public ItemStack execute(BlockSource source, ItemStack stack)
        {
            BucketItem bucket = (BucketItem) stack.getItem();
            BlockPos pos = source.getPos().relative(source.getBlockState().getValue(DispenserBlock.FACING));
            Level world = source.getLevel();
            if (bucket.emptyContents(null, world, pos, null))
            {
                bucket.checkExtraContent(null, world, stack, pos);
                return new ItemStack(Items.BUCKET);
            }
            else
            {
                return DEFAULT.dispense(source, stack);
            }
        }
    };

    private static final DispenseItemBehavior CHEST_BEHAVIOR = new OptionalDispenseItemBehavior()
    {
        public ItemStack execute(BlockSource level, ItemStack stack)
        {
            final BlockPos blockpos = level.getPos().relative(level.getBlockState().getValue(DispenserBlock.FACING));
            for (AbstractChestedHorse abstractchestedhorse : level.getLevel().getEntitiesOfClass(AbstractChestedHorse.class, new AABB(blockpos), horse -> horse.isAlive() && !horse.hasChest()))
            {
                if (abstractchestedhorse.isTamed() && abstractchestedhorse.getSlot(499).set(stack))
                {
                    stack.shrink(1);
                    this.setSuccess(true);
                    return stack;
                }
            }
            return super.execute(level, stack);
        }
    };

    /**
     * {@link DispenserBlock#registerBehavior(ItemLike, DispenseItemBehavior)} is not thread safe
     */
    public static void registerAll()
    {
        // Bucket emptying
        DispenserBlock.registerBehavior(TFCItems.SALT_WATER_BUCKET.get(), BUCKET_BEHAVIOR);
        DispenserBlock.registerBehavior(TFCItems.SPRING_WATER_BUCKET.get(), BUCKET_BEHAVIOR);
        DispenserBlock.registerBehavior(TFCItems.BLUEGILL_BUCKET.get(), BUCKET_BEHAVIOR);
        DispenserBlock.registerBehavior(TFCItems.COD_BUCKET.get(), BUCKET_BEHAVIOR);
        DispenserBlock.registerBehavior(TFCItems.JELLYFISH_BUCKET.get(), BUCKET_BEHAVIOR);
        DispenserBlock.registerBehavior(TFCItems.SALMON_BUCKET.get(), BUCKET_BEHAVIOR);
        DispenserBlock.registerBehavior(TFCItems.TROPICAL_FISH_BUCKET.get(), BUCKET_BEHAVIOR);
        DispenserBlock.registerBehavior(TFCItems.PUFFERFISH_BUCKET.get(), BUCKET_BEHAVIOR);

        TFCBlocks.WOODS.values().stream().map(map -> map.get(Wood.BlockType.CHEST).get()).forEach(chest -> DispenserBlock.registerBehavior(chest, CHEST_BEHAVIOR));
        TFCItems.METAL_FLUID_BUCKETS.values().forEach(reg -> DispenserBlock.registerBehavior(reg.get(), BUCKET_BEHAVIOR));
    }
}
