/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blocks.crop;

import java.util.Locale;
import java.util.function.Function;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;

import net.minecraft.world.level.material.MapColor;

import net.dries007.tfc.common.blockentities.CropBlockEntity;
import net.dries007.tfc.common.blockentities.FarmlandBlockEntity.NutrientType;
import net.dries007.tfc.common.blockentities.TFCBlockEntities;
import net.dries007.tfc.common.blocks.ExtendedProperties;
import net.dries007.tfc.common.blocks.TFCBlocks;
import net.dries007.tfc.common.items.Food;
import net.dries007.tfc.common.items.TFCItems;
import net.dries007.tfc.util.climate.ClimateRange;
import net.dries007.tfc.util.climate.ClimateRanges;


public enum Crop implements StringRepresentable
{
    // Grains
    BARLEY(0.75f, -0.15f, -0.15f, 8), // Default, 8
    OAT(1f, -.25f, -.25f, 8), // Default, 8
    RYE(1f, -.25f, -.25f, 8), // Default, 8
    MAIZE(1f, -.25f, -.25f, 3, 3, false), // Double, 3 -> 3
    WHEAT(1f, -.25f, -.25f, 8), // Default, 8
    RICE(.40f, .30f, .3f, 8, true), // Default, Waterlogged, 8

    // Legumes
    SOYBEAN(-.60f, .60f, .40f, 7), // Default, 7
    GREEN_BEAN(-.60f, .50f, .40f, 4, 4, true), // Double, Stick, 4 -> 4

    // Vegetables
    BEET(.40f, .40f, .40f, 6), // Default, 6
    CABBAGE(.40f, .40f, .40f, 6), // Default, 6
    CARROT(.40f, .40f, .40f, 5), // Default, 5
    GARLIC(.40f, .40f, .40f, 5), // Default, 5
    POTATO(.40f, .40f, .40f, 7), // Default, 7
    ONION(.40f, .40f, .40f, 7), // Default, 7
    SQUASH(.40f, .40f, .40f, 8), // Default , 8
    TOMATO(.40f, .40f, .40f, 4, 4, true, null, () -> TFCItems.FOOD.get(Food.TOMATO)), // Double, Stick, 4 -> 4
    PUMPKIN(.40f, .40f, .40f, 8, () -> TFCBlocks.PUMPKIN), // Spreading, 8
    MELON(.40f, .40f, .40f, 8, () -> TFCBlocks.MELON), // Spreading, 8
    RED_BELL_PEPPER(.40f, .40f, .40f, 7, () -> TFCItems.FOOD.get(Food.GREEN_BELL_PEPPER), () -> TFCItems.FOOD.get(Food.RED_BELL_PEPPER)), // Pickable, 7
    YELLOW_BELL_PEPPER(.40f, .40f, .40f, 7, () -> TFCItems.FOOD.get(Food.GREEN_BELL_PEPPER), () -> TFCItems.FOOD.get(Food.YELLOW_BELL_PEPPER)), // Pickable, 7

    // Miscellaneous
    JUTE(.80f, -.40f, .10f, 3, 3, false), // Double, 3 -> 3
    SUGARCANE(.60f, .60f, .60f, 4, 4, false), // Double, 4 -> 4
    PAPYRUS(.40f, .40f, .40f, 3, 3, false);

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
        return ExtendedProperties.of(MapColor.PLANT).noCollission().randomTicks().strength(0.4F).sound(SoundType.CROP).flammable(60, 30);
    }

    private final String serializedName;
    private final float nitrogen;
    private final float phosphorous;
    private final float potassium;
    private final Supplier<Block> factory;
    private final Supplier<Block> deadFactory;
    private final Supplier<Block> wildFactory;

    Crop(float nitrogen, float phosporous, float potassium, int singleBlockStages)
    {
        this(nitrogen, phosporous, potassium, self -> DefaultCropBlock.create(crop(), singleBlockStages, self), self -> new DeadCropBlock(dead(), self.getClimateRange()), self -> new WildCropBlock(dead().randomTicks()));
    }

    Crop(float nitrogen, float phosporous, float potassium, int spreadingSingleBlockStages, Supplier<Supplier<? extends Block>> fruit)
    {
        this(nitrogen, phosporous, potassium, self -> SpreadingCropBlock.create(crop(), spreadingSingleBlockStages, self, fruit), self -> new DeadCropBlock(dead(), self.getClimateRange()), self -> new WildSpreadingCropBlock(dead().randomTicks(), fruit));
    }

    Crop(float nitrogen, float phosporous, float potassium, int spreadingSingleBlockStages, @Nullable Supplier<Supplier<? extends Item>> fruit1, Supplier<Supplier<? extends Item>> fruit2)
    {
        this(nitrogen, phosporous, potassium, self -> PickableCropBlock.create(crop(), spreadingSingleBlockStages, self, fruit1, fruit2), self -> new DeadCropBlock(dead(), self.getClimateRange()), self -> new WildCropBlock(dead().randomTicks()));
    }

    Crop(float nitrogen, float phosporous, float potassium, int floodedSingleBlockStages, boolean flooded)
    {
        this(nitrogen, phosporous, potassium, self -> FloodedCropBlock.create(crop(), floodedSingleBlockStages, self), self -> new FloodedDeadCropBlock(dead(), self.getClimateRange()), self -> new FloodedWildCropBlock(dead().randomTicks()));
        assert flooded;
    }

    Crop(float nitrogen, float phosporous, float potassium, int doubleBlockBottomStages, int doubleBlockTopStages, boolean requiresStick, @Nullable Supplier<Supplier<? extends Item>> fruit1, Supplier<Supplier<? extends Item>> fruit2)
    {
        this(nitrogen, phosporous, potassium, requiresStick ?
                self -> PickableClimbingCropBlock.create(doubleCrop(), doubleBlockBottomStages, doubleBlockTopStages, self, fruit1, fruit2) :
                self -> DoubleCropBlock.create(doubleCrop(), doubleBlockBottomStages, doubleBlockTopStages, self),
            self -> new DeadClimbingCropBlock(dead(), self.getClimateRange()), self -> new WildDoubleCropBlock(dead().randomTicks())
        );
    }

    Crop(float nitrogen, float phosporous, float potassium, int doubleBlockBottomStages, int doubleBlockTopStages, boolean requiresStick)
    {
        this(nitrogen, phosporous, potassium, requiresStick ?
                self -> ClimbingCropBlock.create(doubleCrop(), doubleBlockBottomStages, doubleBlockTopStages, self) :
                self -> DoubleCropBlock.create(doubleCrop(), doubleBlockBottomStages, doubleBlockTopStages, self),
            self -> new DeadClimbingCropBlock(dead(), self.getClimateRange()), self -> new WildDoubleCropBlock(dead().randomTicks())
        );
    }

    Crop(float nitrogen, float phosporous, float potassium, Function<Crop, Block> factory, Function<Crop, Block> deadFactory, Function<Crop, Block> wildFactory)
    {
        this.serializedName = name().toLowerCase(Locale.ROOT);
        this.nitrogen = nitrogen;
        this.phosphorous = phosporous;
        this.potassium = potassium;
        this.factory = () -> factory.apply(this);
        this.deadFactory = () -> deadFactory.apply(this);
        this.wildFactory = () -> wildFactory.apply(this);
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

    public float getNitrogen()
    {
        return this.nitrogen;
    }

    public float getPhosphorous()
    {
        return this.phosphorous;
    }

    public float getPotassium()
    {
        return this.potassium;
    }

    public Supplier<ClimateRange> getClimateRange()
    {
        return ClimateRanges.CROPS.get(this);
    }

}