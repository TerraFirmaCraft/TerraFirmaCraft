/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.util.registry;

import java.util.function.Supplier;

import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

import net.dries007.tfc.common.blockentities.FarmlandBlockEntity;
import net.dries007.tfc.common.blocks.crop.CropBlock;
import net.dries007.tfc.util.climate.ClimateRange;

/**
 * Properties to instantiate a {@link CropBlock} subclass
 */
public interface RegistryCrop
{
    Supplier<FarmlandBlockEntity.NutrientType> getPrimaryNutrient();

    Supplier<ClimateRange> getClimateRange();

    Supplier<? extends Block> getDeadBlock();

    Supplier<? extends Item> getSeedItem();

    Supplier<Integer> getBaseGrowthTime();

    Supplier<Integer> getNutrientConsumptionTime();

    Supplier<Double> getNutrientResupplyFactor();
}
