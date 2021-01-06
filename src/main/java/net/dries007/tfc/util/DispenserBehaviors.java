package net.dries007.tfc.util;

import net.minecraft.block.DispenserBlock;
import net.minecraft.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.dispenser.IDispenseItemBehavior;
import net.minecraft.item.BucketItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import net.dries007.tfc.common.items.TFCItems;

public class DispenserBehaviors
{
    private static final IDispenseItemBehavior DEFAULT = new DefaultDispenseItemBehavior();

    private static final IDispenseItemBehavior BUCKET_BEHAVIOR = new DefaultDispenseItemBehavior()
    {
        @Override
        public ItemStack execute(IBlockSource source, ItemStack stack)
        {
            BucketItem bucket = (BucketItem) stack.getItem();
            BlockPos pos = source.getPos().relative(source.getBlockState().getValue(DispenserBlock.FACING));
            World world = source.getLevel();
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
