/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blocks.crop;

import java.util.function.Supplier;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.neoforge.items.ItemHandlerHelper;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.common.blockentities.CropBlockEntity;
import net.dries007.tfc.common.blockentities.FarmlandBlockEntity;
import net.dries007.tfc.common.blocks.ExtendedProperties;
import net.dries007.tfc.common.blocks.TFCBlockStateProperties;
import net.dries007.tfc.common.blocks.TFCBlocks;
import net.dries007.tfc.common.items.TFCItems;
import net.dries007.tfc.util.climate.ClimateRange;
import net.dries007.tfc.util.climate.ClimateRanges;

public abstract class PickableCropBlock extends DefaultCropBlock
{
    public static PickableCropBlock create(ExtendedProperties properties, int stages, Crop crop, @Nullable Supplier<Supplier<? extends Item>> fruit, Supplier<Supplier<? extends Item>> matureFruit)
    {
        final IntegerProperty property = TFCBlockStateProperties.getAgeProperty(stages - 1);
        return new PickableCropBlock(properties, stages - 1, TFCBlocks.DEAD_CROPS.get(crop), TFCItems.CROP_SEEDS.get(crop), crop.getPrimaryNutrient(), ClimateRanges.CROPS.get(crop), fruit, matureFruit)
        {
            @Override
            public IntegerProperty getAgeProperty()
            {
                return property;
            }
        };
    }

    private final @Nullable Supplier<Supplier<? extends Item>> fruit;
    private final Supplier<Supplier<? extends Item>> matureFruit;

    protected PickableCropBlock(ExtendedProperties properties, int maxAge, Supplier<? extends Block> dead, Supplier<? extends Item> seeds, FarmlandBlockEntity.NutrientType primaryNutrient, Supplier<ClimateRange> climateRange, @Nullable Supplier<Supplier<? extends Item>> fruit, Supplier<Supplier<? extends Item>> matureFruit)
    {
        super(properties, maxAge, dead, seeds, primaryNutrient, climateRange);
        this.fruit = fruit;
        this.matureFruit = matureFruit;
    }

    @Nullable
    public Item getFirstFruit()
    {
        return fruit == null ? null : fruit.get().get();
    }

    public Item getSecondFruit()
    {
        return matureFruit.get().get();
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult result)
    {
        final InteractionResult res = super.use(state, level, pos, player, hand, result);
        if (res.consumesAction())
        {
            return res; // use fertilizer
        }
        if (level.getBlockEntity(pos) instanceof CropBlockEntity crop)
        {
            final float yield = crop.getYield();
            final int age = state.getValue(getAgeProperty());
            final RandomSource random = level.getRandom();
            if (age == maxAge - 1 && getFirstFruit() != null)
            {
                crop.setGrowth(Mth.nextFloat(random, 0.4f, 0.5f));
                crop.setYield(0f);
                postGrowthTick(level, pos, state, crop);
                ItemHandlerHelper.giveItemToPlayer(player, yieldItemStack(getFirstFruit(), yield, random));
                return InteractionResult.sidedSuccess(level.isClientSide);
            }
            else if (age == maxAge)
            {
                crop.setGrowth(Mth.nextFloat(random, 0.5f, 0.6f));
                crop.setYield(0f);
                postGrowthTick(level, pos, state, crop);
                ItemHandlerHelper.giveItemToPlayer(player, yieldItemStack(getSecondFruit(), yield, random));
                return InteractionResult.sidedSuccess(level.isClientSide);
            }
        }
        return InteractionResult.PASS;
    }

    private ItemStack yieldItemStack(Item item, float yield, RandomSource random)
    {
        return new ItemStack(item, Mth.floor(Mth.lerp(yield, 1f, 5f) + random.nextInt(2)));
    }

}
