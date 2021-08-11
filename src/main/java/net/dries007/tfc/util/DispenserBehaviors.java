/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.util;

import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.core.BlockSource;
import net.minecraft.core.dispenser.DispenseItemBehavior;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.util.IItemProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

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
            if (bucket.emptyBucket(null, world, pos, null))
            {
                bucket.checkExtraContent(world, stack, pos);
                return new ItemStack(Items.BUCKET);
            }
            else
            {
                return DEFAULT.dispense(source, stack);
            }
        }
    };

    /**
     * {@link DispenserBlock#registerBehavior(IItemProvider, IDispenseItemBehavior)} is not thread safe
     */
    public static void syncSetup()
    {
        // Bucket emptying
        DispenserBlock.registerBehavior(TFCItems.SALT_WATER_BUCKET.get(), BUCKET_BEHAVIOR);
        DispenserBlock.registerBehavior(TFCItems.SPRING_WATER_BUCKET.get(), BUCKET_BEHAVIOR);

        TFCItems.METAL_FLUID_BUCKETS.values().forEach(reg -> DispenserBlock.registerBehavior(reg.get(), BUCKET_BEHAVIOR));
    }
}
