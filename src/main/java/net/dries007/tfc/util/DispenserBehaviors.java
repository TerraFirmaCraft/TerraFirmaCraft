/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.util;

import java.util.stream.Stream;

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
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.phys.AABB;

import net.minecraftforge.fluids.capability.CapabilityFluidHandler;

import net.dries007.tfc.common.blocks.TFCBlocks;
import net.dries007.tfc.common.blocks.wood.Wood;
import net.dries007.tfc.common.items.TFCBucketItem;
import net.dries007.tfc.common.items.TFCItems;

public class DispenserBehaviors
{
    public static final DispenseItemBehavior DEFAULT = new DefaultDispenseItemBehavior();

    public static final DispenseItemBehavior VANILLA_BUCKET_BEHAVIOR = new DefaultDispenseItemBehavior()
    {
        @Override
        public ItemStack execute(BlockSource source, ItemStack stack)
        {
            BucketItem bucket = (BucketItem) stack.getItem();
            BlockPos pos = source.getPos().relative(source.getBlockState().getValue(DispenserBlock.FACING));
            Level level = source.getLevel();
            if (bucket.emptyContents(null, level, pos, null))
            {
                bucket.checkExtraContent(null, level, stack, pos);
                return new ItemStack(Items.BUCKET);
            }
            else
            {
                return DEFAULT.dispense(source, stack);
            }
        }
    };

    public static final DispenseItemBehavior TFC_BUCKET_BEHAVIOR = new DefaultDispenseItemBehavior()
    {
        @Override
        public ItemStack execute(BlockSource source, ItemStack stack)
        {
            TFCBucketItem bucket = (TFCBucketItem) stack.getItem();
            BlockPos dropPos = source.getPos().relative(source.getBlockState().getValue(DispenserBlock.FACING));
            Level level = source.getLevel();
            return stack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY).map(handler -> {
                if (bucket.emptyContents(handler, null, level, dropPos, level.getBlockState(dropPos), null))
                {
                    // if we wanted to check extra content, we would do that here.
                    return stack.getContainerItem();
                }
                return DEFAULT.dispense(source, stack);
            }).orElse(DEFAULT.dispense(source, stack));

        }
    };

    public static final DispenseItemBehavior CHEST_BEHAVIOR = new OptionalDispenseItemBehavior()
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
    public static void registerDispenserBehaviors()
    {
        // Bucket emptying
        Stream.of(TFCItems.BLUE_STEEL_BUCKET, TFCItems.RED_STEEL_BUCKET, TFCItems.JUG, TFCItems.WOODEN_BUCKET)
            .forEach(reg -> DispenserBlock.registerBehavior(reg.get(), TFC_BUCKET_BEHAVIOR));

        Stream.of(TFCItems.SALT_WATER_BUCKET, TFCItems.SPRING_WATER_BUCKET, TFCItems.BLUEGILL_BUCKET, TFCItems.COD_BUCKET, TFCItems.JELLYFISH_BUCKET,TFCItems.SALMON_BUCKET, TFCItems.TROPICAL_FISH_BUCKET, TFCItems.PUFFERFISH_BUCKET)
            .forEach(reg -> DispenserBlock.registerBehavior(reg.get(), VANILLA_BUCKET_BEHAVIOR));
        TFCItems.METAL_FLUID_BUCKETS.values().forEach(reg -> DispenserBlock.registerBehavior(reg.get(), VANILLA_BUCKET_BEHAVIOR));
        TFCItems.ALCOHOL_BUCKETS.values().forEach(reg -> DispenserBlock.registerBehavior(reg.get(), VANILLA_BUCKET_BEHAVIOR));
        TFCItems.SIMPLE_FLUID_BUCKETS.values().forEach(reg -> DispenserBlock.registerBehavior(reg.get(), VANILLA_BUCKET_BEHAVIOR));
        TFCItems.COLORED_FLUID_BUCKETS.values().forEach(reg -> DispenserBlock.registerBehavior(reg.get(), VANILLA_BUCKET_BEHAVIOR));

        // chest
        TFCBlocks.WOODS.values().stream().map(map -> map.get(Wood.BlockType.CHEST).get()).forEach(chest -> DispenserBlock.registerBehavior(chest, CHEST_BEHAVIOR));

    }
}
