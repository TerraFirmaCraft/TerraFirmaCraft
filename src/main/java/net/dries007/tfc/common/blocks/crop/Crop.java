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
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.Material;

import net.dries007.tfc.common.blockentities.CropBlockEntity;
import net.dries007.tfc.common.blockentities.FarmlandBlockEntity;
import net.dries007.tfc.common.blockentities.IFarmland.NutrientType;
import net.dries007.tfc.common.blockentities.IFarmland;
import net.dries007.tfc.common.blockentities.TFCBlockEntities;
import net.dries007.tfc.common.blocks.ExtendedProperties;
import net.dries007.tfc.common.blocks.TFCBlocks;


public enum Crop implements StringRepresentable
{
    // Grains
    BARLEY(IFarmland.NutrientType.NITROGEN, 8), // Default, 8
    OAT(IFarmland.NutrientType.PHOSPHOROUS, 8), // Default, 8
    RYE(IFarmland.NutrientType.PHOSPHOROUS, 8), // Default, 8
    MAIZE(IFarmland.NutrientType.PHOSPHOROUS, 3, 3, false), // Double, 3 -> 3
    WHEAT(IFarmland.NutrientType.PHOSPHOROUS, 8), // Default, 8
    RICE(IFarmland.NutrientType.PHOSPHOROUS, 8, true), // Default, Waterlogged, 8
    // Vegetables
    BEET(IFarmland.NutrientType.POTASSIUM, 6), // Default, 6
    CABBAGE(IFarmland.NutrientType.NITROGEN, 6), // Default, 6
    CARROT(IFarmland.NutrientType.POTASSIUM, 5), // Default, 5
    GARLIC(IFarmland.NutrientType.NITROGEN, 5), // Default, 5
    GREEN_BEAN(IFarmland.NutrientType.NITROGEN, 4, 4, true), // Double, Stick, 4 -> 4
    POTATO(IFarmland.NutrientType.POTASSIUM, 7), // Default, 7
    ONION(IFarmland.NutrientType.NITROGEN, 7), // Default, 7
    SOYBEAN(IFarmland.NutrientType.NITROGEN, 7), // Default, 7
    SQUASH(IFarmland.NutrientType.POTASSIUM, 8), // Default , 8
    SUGARCANE(IFarmland.NutrientType.POTASSIUM, 4, 4, false), // Double, 4 -> 4
    TOMATO(IFarmland.NutrientType.POTASSIUM, 4, 4, true), // Double, Stick, 4 -> 4
    JUTE(IFarmland.NutrientType.POTASSIUM, 3, 3, false), // Double, 3 -> 3
    PUMPKIN(IFarmland.NutrientType.PHOSPHOROUS, 8, () -> TFCBlocks.PUMPKIN),
    MELON(IFarmland.NutrientType.PHOSPHOROUS, 8, () -> TFCBlocks.MELON);
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

    Crop(NutrientType primaryNutrient, int singleBlockStages)
    {
        this(primaryNutrient, self -> DefaultCropBlock.create(crop(), singleBlockStages, self), self -> new DeadCropBlock(dead(), self), self -> new WildCropBlock(dead()));
    }

    Crop(NutrientType primaryNutrient, int spreadingSingleBlockStages, Supplier<Supplier<? extends Block>> fruit)
    {
        this(primaryNutrient, self -> SpreadingCropBlock.create(crop(), spreadingSingleBlockStages, self, fruit), self -> new DeadCropBlock(dead(), self), self -> new WildSpreadingCropBlock(dead(), fruit));
    }

    Crop(NutrientType primaryNutrient, int floodedSingleBlockStages, boolean flooded)
    {
        this(primaryNutrient, self -> FloodedCropBlock.create(crop(), floodedSingleBlockStages, self), self -> new FloodedDeadCropBlock(dead(), self), self -> new FloodedWildCropBlock(dead()));
        assert flooded;
    }

    Crop(NutrientType primaryNutrient, int doubleBlockBottomStages, int doubleBlockTopStages, boolean requiresStick)
    {
        this(primaryNutrient, requiresStick ?
            self -> ClimbingCropBlock.create(doubleCrop(), doubleBlockBottomStages, doubleBlockTopStages, self) :
            self -> DoubleCropBlock.create(doubleCrop(), doubleBlockBottomStages, doubleBlockTopStages, self),
            self -> new DeadClimbingCropBlock(dead(), self), self -> new WildDoubleCropBlock(dead()));
    }

    Crop(NutrientType primaryNutrient, Function<Crop, Block> factory, Function<Crop, Block> deadFactory, Function<Crop, Block> wildFactory)
    {
        this.serializedName = name().toLowerCase(Locale.ROOT);
        this.primaryNutrient = primaryNutrient;
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

    public FarmlandBlockEntity.NutrientType getPrimaryNutrient()
    {
        return primaryNutrient;
    }
}