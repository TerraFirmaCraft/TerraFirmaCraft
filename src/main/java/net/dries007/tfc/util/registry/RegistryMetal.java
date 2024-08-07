/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.util.registry;

import java.util.function.Supplier;
import net.minecraft.core.Holder;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.MapColor;

import net.dries007.tfc.common.LevelTier;
import net.dries007.tfc.common.blocks.IClimateWeatheringBlock;
import net.dries007.tfc.common.blocks.TFCBlocks;
import net.dries007.tfc.common.blocks.WeatheringMetalBlock;
import net.dries007.tfc.common.blocks.WeatheringMetalSlab;
import net.dries007.tfc.common.blocks.WeatheringMetalStairs;
import net.dries007.tfc.util.Metal;

/**
 * Interface used in registration to allow {@link Metal.BlockType}, {@link Metal.ItemType} to be used by addons.
 */
public interface RegistryMetal extends StringRepresentable
{
    LevelTier toolTier();

    Holder<ArmorMaterial> armorTier();

    default Supplier<Block> getFullBlock(){
        return getFullBlock(IClimateWeatheringBlock.TFCWeatherState.UNAFFECTED);
    }

    Supplier<Block> getFullBlock(IClimateWeatheringBlock.TFCWeatherState weatherState);

    Supplier<Block> getSlabBlock(IClimateWeatheringBlock.TFCWeatherState weatherState);

    Supplier<Block> getStairBlock(IClimateWeatheringBlock.TFCWeatherState weatherState);

    Metal.WeatheringType weathering();

    MapColor mapColor();

    Rarity rarity();
}
