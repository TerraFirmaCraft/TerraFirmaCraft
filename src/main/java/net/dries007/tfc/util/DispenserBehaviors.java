/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.util;

import java.util.stream.Stream;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.core.dispenser.DispenseItemBehavior;
import net.minecraft.core.dispenser.OptionalDispenseItemBehavior;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.animal.horse.AbstractChestedHorse;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseRailBlock;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.block.LevelEvent;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.RailShape;
import net.minecraft.world.phys.AABB;
import org.apache.commons.lang3.mutable.Mutable;
import org.apache.commons.lang3.mutable.MutableObject;

import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.blockentities.QuernBlockEntity;
import net.dries007.tfc.common.blocks.TFCBlocks;
import net.dries007.tfc.common.blocks.devices.QuernBlock;
import net.dries007.tfc.common.blocks.wood.Wood;
import net.dries007.tfc.common.fluids.FluidHelpers;
import net.dries007.tfc.common.items.FluidContainerItem;
import net.dries007.tfc.common.items.TFCItems;
import net.dries007.tfc.common.items.TFCMinecartItem;
import net.dries007.tfc.config.TFCConfig;
import net.dries007.tfc.util.events.StartFireEvent;

// todo 1.21, dispensers have changed, we may need to reevaluate here
public final class DispenserBehaviors
{
    public static final DispenseItemBehavior DEFAULT = new DefaultDispenseItemBehavior();

    public static final DispenseItemBehavior VANILLA_BUCKET_BEHAVIOR = new DefaultDispenseItemBehavior()
    {
        @Override
        public ItemStack execute(BlockSource source, ItemStack stack)
        {
            BucketItem bucket = (BucketItem) stack.getItem();
            BlockPos pos = source.pos().relative(source.state().getValue(DispenserBlock.FACING));
            Level level = source.level();
            if (bucket.emptyContents(null, level, pos, null, stack))
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
            final Level level = source.level();
            final BlockPos pos = source.pos().relative(source.state().getValue(DispenserBlock.FACING));
            if (stack.getItem() instanceof FluidContainerItem item)
            {
                final Mutable<ItemStack> result = new MutableObject<>();
                result.setValue(stack);
                FluidHelpers.transferBetweenWorldAndItem(stack, level, pos, null, (newOriginalStack, newContainerStack) -> {
                    result.setValue(newOriginalStack);
                    if (!newContainerStack.isEmpty())
                    {
                        final ItemStack leftover = source.blockEntity().insertItem(newContainerStack);
                        if (!leftover.isEmpty())
                        {
                            DEFAULT.dispense(source, newContainerStack);
                        }
                    }
                }, item.canPlaceLiquidsInWorld(), item.canPlaceSourceBlocks(), false);
                return result.getValue();
            }
            return stack;
        }
    };

    public static final DispenseItemBehavior CHEST_BEHAVIOR = new OptionalDispenseItemBehavior()
    {
        public ItemStack execute(BlockSource level, ItemStack stack)
        {
            final BlockPos blockpos = level.pos().relative(level.state().getValue(DispenserBlock.FACING));
            for (AbstractChestedHorse horse : level.level().getEntitiesOfClass(AbstractChestedHorse.class, new AABB(blockpos), horse -> horse.isAlive() && !horse.hasChest()))
            {
                if (horse.isTamed() && horse.getSlot(499).set(stack))
                {
                    stack.shrink(1);
                    this.setSuccess(true);
                    return stack;
                }
            }
            return super.execute(level, stack);
        }
    };

    public static DispenseItemBehavior MINECART_BEHAVIOR = new DefaultDispenseItemBehavior()
    {
        private final DefaultDispenseItemBehavior defaultBehavior = new DefaultDispenseItemBehavior();

        @Override
        public ItemStack execute(BlockSource source, ItemStack stack)
        {
            if (stack.getItem() instanceof TFCMinecartItem cartItem)
            {
                final Direction direction = source.state().getValue(DispenserBlock.FACING);
                final Level level = source.level();

                final double x = source.center().x + (double) direction.getStepX() * 1.125D;
                final double y = Math.floor(source.center().y) + (double) direction.getStepY();
                final double z = source.center().z + (double) direction.getStepZ() * 1.125D;

                final BlockPos offsetPos = source.pos().relative(direction);
                final BlockState state = level.getBlockState(offsetPos);
                final RailShape railshape = state.getBlock() instanceof BaseRailBlock ? ((BaseRailBlock) state.getBlock()).getRailDirection(state, level, offsetPos, null) : RailShape.NORTH_SOUTH;

                double offset;
                if (Helpers.isBlock(state, BlockTags.RAILS))
                {
                    offset = railshape.isAscending() ? 0.6 : 0.1;
                }
                else
                {
                    if (!state.isAir() || !Helpers.isBlock(level.getBlockState(offsetPos.below()), BlockTags.RAILS))
                    {
                        return this.defaultBehavior.dispense(source, stack);
                    }

                    BlockState offsetState = level.getBlockState(offsetPos.below());
                    @SuppressWarnings("deprecation") final RailShape offsetShape = offsetState.getBlock() instanceof BaseRailBlock ? offsetState.getValue(((BaseRailBlock) offsetState.getBlock()).getShapeProperty()) : RailShape.NORTH_SOUTH;
                    offset = direction != Direction.DOWN && offsetShape.isAscending() ? -0.4 : -0.9;
                }
                cartItem.createMinecartEntity(level, stack, x, y + offset, z);
                return stack;
            }
            return ItemStack.EMPTY;
        }

        @Override
        protected void playSound(BlockSource source)
        {
            source.level().levelEvent(LevelEvent.SOUND_DISPENSER_DISPENSE, source.pos(), 0);
        }
    };

    public static final OptionalDispenseItemBehavior TFC_FLINT_AND_STEEL_BEHAVIOR = new OptionalDispenseItemBehavior()
    {
        @Override
        protected ItemStack execute(BlockSource source, ItemStack stack)
        {
            final ServerLevel level = source.level();
            final Direction facing = source.state().getValue(DispenserBlock.FACING);
            final BlockPos pos = source.pos().relative(facing);
            final BlockState state = level.getBlockState(pos);
            if (TFCConfig.SERVER.dispenserEnableLighting.get() && StartFireEvent.startFire(level, pos, state, facing.getOpposite(), null, stack, StartFireEvent.FireStrength.STRONG))
            {
                stack.hurtAndBreak(1, level, null, i -> {});
                return stack;
            }
            setSuccess(false);
            return stack;
        }
    };

    public static final DispenseItemBehavior HANDSTONE_BEHAVIOR = new DefaultDispenseItemBehavior()
    {
        @Override
        public ItemStack execute(BlockSource source, ItemStack stack)
        {
            final Level level = source.level();
            final BlockPos pos = source.pos().relative(source.state().getValue(DispenserBlock.FACING));
            if (Helpers.isItem(stack, TFCTags.Items.QUERN_HANDSTONES) && level.getBlockState(pos).getBlock() instanceof QuernBlock)
            {
                if (level.getBlockEntity(pos) instanceof QuernBlockEntity quern && !quern.hasHandstone())
                {
                    return quern.getInventory().insertItem(QuernBlockEntity.SLOT_HANDSTONE, stack, false);
                }
            }
            return stack;
        }
    };

    /**
     * {@link DispenserBlock#registerBehavior(ItemLike, DispenseItemBehavior)} is not thread safe
     */
    public static void registerDispenserBehaviors()
    {
        // Bucket emptying
        Stream.of(TFCItems.BLUE_STEEL_BUCKET, TFCItems.RED_STEEL_BUCKET, TFCItems.JUG, TFCItems.WOODEN_BUCKET, TFCItems.SILICA_GLASS_BOTTLE, TFCItems.HEMATITIC_GLASS_BOTTLE, TFCItems.OLIVINE_GLASS_BOTTLE, TFCItems.VOLCANIC_GLASS_BOTTLE)
            .forEach(reg -> DispenserBlock.registerBehavior(reg.get(), TFC_BUCKET_BEHAVIOR));

        Stream.of(TFCItems.COD_BUCKET, TFCItems.JELLYFISH_BUCKET, TFCItems.TROPICAL_FISH_BUCKET, TFCItems.PUFFERFISH_BUCKET)
            .forEach(reg -> DispenserBlock.registerBehavior(reg.get(), VANILLA_BUCKET_BEHAVIOR));
        TFCItems.FLUID_BUCKETS.values().forEach(reg -> DispenserBlock.registerBehavior(reg.get(), VANILLA_BUCKET_BEHAVIOR));
        TFCItems.FRESHWATER_FISH_BUCKETS.values().forEach(reg -> DispenserBlock.registerBehavior(reg.get(), VANILLA_BUCKET_BEHAVIOR));

        // chest
        TFCBlocks.WOODS.values().stream().map(map -> map.get(Wood.BlockType.CHEST).get()).forEach(chest -> DispenserBlock.registerBehavior(chest, CHEST_BEHAVIOR));

        // minecart chest
        TFCItems.CHEST_MINECARTS.values().forEach(reg -> DispenserBlock.registerBehavior(reg.get(), MINECART_BEHAVIOR));

        DispenserBlock.registerBehavior(Items.EGG, new DefaultDispenseItemBehavior());
        DispenserBlock.registerBehavior(Items.FLINT_AND_STEEL, new MultipleItemBehavior(TFC_FLINT_AND_STEEL_BEHAVIOR, DispenserBlock.DISPENSER_REGISTRY.get(Items.FLINT_AND_STEEL)));
        DispenserBlock.registerBehavior(TFCItems.HANDSTONE.get(), HANDSTONE_BEHAVIOR);
    }

    public static class MultipleItemBehavior implements DispenseItemBehavior
    {
        private final OptionalDispenseItemBehavior primary;
        private final DispenseItemBehavior defaultBehavior;

        public MultipleItemBehavior(OptionalDispenseItemBehavior first, DispenseItemBehavior second)
        {
            primary = first;
            defaultBehavior = second;
        }

        @Override
        public ItemStack dispense(BlockSource source, ItemStack stack)
        {
            ItemStack result = primary.dispense(source, stack);
            if (primary.isSuccess())
            {
                return result;
            }
            return defaultBehavior.dispense(source, stack);
        }
    }
}
