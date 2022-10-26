/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blocks.crop;

import java.util.Locale;
import java.util.function.Function;
import java.util.function.Supplier;

import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.Material;

import net.dries007.tfc.common.blockentities.CropBlockEntity;
import net.dries007.tfc.common.blockentities.FarmlandBlockEntity;
import net.dries007.tfc.common.blockentities.FarmlandBlockEntity.NutrientType;
import net.dries007.tfc.common.blockentities.TFCBlockEntities;
import net.dries007.tfc.common.blocks.ExtendedProperties;
import net.dries007.tfc.common.blocks.TFCBlocks;
import net.dries007.tfc.common.items.TFCItems;
import net.dries007.tfc.config.TFCConfig;
import net.dries007.tfc.util.calendar.ICalendar;
import net.dries007.tfc.util.climate.ClimateRange;
import net.dries007.tfc.util.climate.ClimateRanges;
import net.dries007.tfc.util.registry.RegistryCrop;


public enum Crop implements StringRepresentable, RegistryCrop
{
    // Grains
    BARLEY(NutrientType.NITROGEN, 8, 24, 12, 1 / 6f), // Default, 8
    OAT(NutrientType.PHOSPHOROUS, 8, 24, 12, 1 / 6f), // Default, 8
    RYE(NutrientType.PHOSPHOROUS, 8, 24, 12, 1 / 6f), // Default, 8
    MAIZE(NutrientType.PHOSPHOROUS, 3, 3, false, 24, 12, 1 / 6f), // Double, 3 -> 3
    WHEAT(NutrientType.PHOSPHOROUS, 8, 24, 12, 1 / 6f), // Default, 8
    RICE(NutrientType.PHOSPHOROUS, 8, true, 24, 12, 1 / 6f), // Default, Waterlogged, 8
    // Vegetables
    BEET(NutrientType.POTASSIUM, 6, 24, 12, 1 / 6f), // Default, 6
    CABBAGE(NutrientType.NITROGEN, 6, 24, 12, 1 / 6f), // Default, 6
    CARROT(NutrientType.POTASSIUM, 5, 24, 12, 1 / 6f), // Default, 5
    GARLIC(NutrientType.NITROGEN, 5, 24, 12, 1 / 6f), // Default, 5
    GREEN_BEAN(NutrientType.NITROGEN, 4, 4, true, 24, 12, 1 / 6f), // Double, Stick, 4 -> 4
    POTATO(NutrientType.POTASSIUM, 7, 24, 12, 1 / 6f), // Default, 7
    ONION(NutrientType.NITROGEN, 7, 24, 12, 1 / 6f), // Default, 7
    SOYBEAN(NutrientType.NITROGEN, 7, 24, 12, 1 / 6f), // Default, 7
    SQUASH(NutrientType.POTASSIUM, 8, 24, 12, 1 / 6f), // Default , 8
    SUGARCANE(NutrientType.POTASSIUM, 4, 4, false, 24, 12, 1 / 6f), // Double, 4 -> 4
    TOMATO(NutrientType.POTASSIUM, 4, 4, true, 24, 12, 1 / 6f), // Double, Stick, 4 -> 4
    JUTE(NutrientType.POTASSIUM, 3, 3, false, 24, 12, 1 / 6f), // Double, 3 -> 3
    PUMPKIN(NutrientType.PHOSPHOROUS, 8, () -> TFCBlocks.PUMPKIN, 24, 12, 1 / 6f),
    MELON(NutrientType.PHOSPHOROUS, 8, () -> TFCBlocks.MELON, 24, 12, 1 / 6f);
    // todo: pickable crops

    private static ExtendedProperties doubleCrop()
    {
        return dead().blockEntity(TFCBlockEntities.CROP).serverTicks(CropBlockEntity::serverTickBottomPartOnly);
    }

    private static ExtendedProperties crop()
    {
        return dead().blockEntity(TFCBlockEntities.CROP).serverTicks(CropBlockEntity::serverTick);
    }

    private static ExtendedProperties dead()
    {
        return ExtendedProperties.of(Material.PLANT).noCollission().randomTicks().strength(0.4F).sound(SoundType.CROP).flammable(60, 30);
    }

    private final String serializedName;
    private final NutrientType primaryNutrient;
    private final Supplier<Block> factory;
    private final Supplier<Block> deadFactory;
    private final Supplier<Block> wildFactory;
    private final int growthTime;
    private final int nutrientTime;
    private final float resupplyFactor;

    Crop(NutrientType primaryNutrient, int singleBlockStages, int growthDays, int nutrientTime, float resupplyFactor)
    {
        this(primaryNutrient, self -> DefaultCropBlock.create(crop(), singleBlockStages, self), self -> new DeadCropBlock(dead(), self.getClimateRange()), self -> new WildCropBlock(dead()), growthDays, nutrientTime, resupplyFactor);
    }

    Crop(NutrientType primaryNutrient, int spreadingSingleBlockStages, Supplier<Supplier<? extends Block>> fruit, int growthDays, int nutrientTime, float resupplyFactor)
    {
        this(primaryNutrient, self -> SpreadingCropBlock.create(crop(), spreadingSingleBlockStages, self, fruit), self -> new DeadCropBlock(dead(), self.getClimateRange()), self -> new WildSpreadingCropBlock(dead(), fruit), growthDays, nutrientTime, resupplyFactor);
    }

    Crop(NutrientType primaryNutrient, int floodedSingleBlockStages, boolean flooded, int growthDays, int nutrientTime, float resupplyFactor)
    {
        this(primaryNutrient, self -> FloodedCropBlock.create(crop(), floodedSingleBlockStages, self), self -> new FloodedDeadCropBlock(dead(), self.getClimateRange()), self -> new FloodedWildCropBlock(dead()), growthDays, nutrientTime, resupplyFactor);
        assert flooded;
    }

    Crop(NutrientType primaryNutrient, int doubleBlockBottomStages, int doubleBlockTopStages, boolean requiresStick, int growthDays, int nutrientTime, float resupplyFactor)
    {
        this(primaryNutrient, requiresStick ?
            self -> ClimbingCropBlock.create(doubleCrop(), doubleBlockBottomStages, doubleBlockTopStages, self) :
            self -> DoubleCropBlock.create(doubleCrop(), doubleBlockBottomStages, doubleBlockTopStages, self),
            self -> new DeadClimbingCropBlock(dead(), self.getClimateRange()), self -> new WildDoubleCropBlock(dead()),
            growthDays, nutrientTime, resupplyFactor);
    }

    Crop(NutrientType primaryNutrient, Function<Crop, Block> factory, Function<Crop, Block> deadFactory, Function<Crop, Block> wildFactory, int growthDays, int nutrientTime, float resupplyFactor)
    {
        this.serializedName = name().toLowerCase(Locale.ROOT);
        this.primaryNutrient = primaryNutrient;
        this.factory = () -> factory.apply(this);
        this.deadFactory = () -> deadFactory.apply(this);
        this.wildFactory = () -> wildFactory.apply(this);
        this.growthTime = ICalendar.TICKS_IN_DAY * growthDays;
        this.nutrientTime = nutrientTime;
        this.resupplyFactor = resupplyFactor;
    }

    @Override
    public String getSerializedName()
    {
        return serializedName;
    }

    public Block create()
    {
        return factory.get();
    }

    public Block createDead()
    {
        return deadFactory.get();
    }

    public Block createWild()
    {
        return wildFactory.get();
    }

    public FarmlandBlockEntity.NutrientType getDefaultPrimaryNutrient()
    {
        return primaryNutrient;
    }

    @Override
    public Supplier<FarmlandBlockEntity.NutrientType> getPrimaryNutrient()
    {
        return TFCConfig.SERVER.cropPrimaryNutrients.get(this);
    }

    @Override
    public Supplier<ClimateRange> getClimateRange()
    {
        return ClimateRanges.CROPS.get(this);
    }

    @Override
    public Supplier<? extends Block> getDeadBlock()
    {
        return TFCBlocks.DEAD_CROPS.get(this);
    }

    @Override
    public Supplier<? extends Item> getSeedItem()
    {
        return TFCItems.CROP_SEEDS.get(this);
    }

    public int getDefaultGrowthTime()
    {
        return growthTime;
    }

    @Override
    public Supplier<Integer> getBaseGrowthTime()
    {
        return TFCConfig.SERVER.cropGrowthTimes.get(this);
    }

    public int getDefaultConsumptionTime()
    {
        return nutrientTime;
    }

    @Override
    public Supplier<Integer> getNutrientConsumptionTime()
    {
        return TFCConfig.SERVER.cropNutrientConsumptionTimes.get(this);
    }

    public float getDefaultResupplyFactor()
    {
        return resupplyFactor;
    }

    @Override
    public Supplier<Double> getNutrientResupplyFactor()
    {
        return TFCConfig.SERVER.cropNutrientResupplyFactors.get(this);
    }
}