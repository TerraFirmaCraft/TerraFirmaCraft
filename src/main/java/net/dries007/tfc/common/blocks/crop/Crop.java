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
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Material;

import net.dries007.tfc.common.blockentities.CropBlockEntity;
import net.dries007.tfc.common.blockentities.FarmlandBlockEntity;
import net.dries007.tfc.common.blockentities.TFCBlockEntities;
import net.dries007.tfc.common.blocks.ExtendedProperties;

public enum Crop implements StringRepresentable
{
    // Grains
    BARLEY(FarmlandBlockEntity.NutrientType.NITROGEN, self -> DefaultCropBlock.create(crop(), 8, self), self -> new DeadCropBlock(dead(), self)), // Default, 8
    OAT(FarmlandBlockEntity.NutrientType.PHOSPHOROUS, self -> DefaultCropBlock.create(crop(), 8, self), self -> new DeadCropBlock(dead(), self)), // Default, 8
    RYE(FarmlandBlockEntity.NutrientType.PHOSPHOROUS, self -> DefaultCropBlock.create(crop(), 8, self), self -> new DeadCropBlock(dead(), self)), // Default, 8
    MAIZE(FarmlandBlockEntity.NutrientType.PHOSPHOROUS, self -> DoubleCropBlock.create(crop().serverTicks(CropBlockEntity::serverTickBottomPartOnly), 3, 3, self), self -> new DeadDoubleCropBlock(dead(), self), true), // Double, 3 -> 3
    WHEAT(FarmlandBlockEntity.NutrientType.PHOSPHOROUS, self -> DefaultCropBlock.create(crop(), 8, self), self -> new DeadCropBlock(dead(), self)), // Default, 8
    RICE(FarmlandBlockEntity.NutrientType.PHOSPHOROUS, self -> FloodedCropBlock.create(crop(), 8, self), self -> new FloodedDeadCropBlock(dead(), self)), // Default, Waterlogged, 8
    // Vegetables
    BEET(FarmlandBlockEntity.NutrientType.POTASSIUM, self -> DefaultCropBlock.create(crop(), 6, self), self -> new DeadCropBlock(dead(), self)), // Default, 6
    CABBAGE(FarmlandBlockEntity.NutrientType.NITROGEN, self -> DefaultCropBlock.create(crop(), 6, self), self -> new DeadCropBlock(dead(), self)), // Default, 6
    CARROT(FarmlandBlockEntity.NutrientType.POTASSIUM, self -> DefaultCropBlock.create(crop(), 5, self), self -> new DeadCropBlock(dead(), self)), // Default, 5
    GARLIC(FarmlandBlockEntity.NutrientType.NITROGEN, self -> DefaultCropBlock.create(crop(), 5, self), self -> new DeadCropBlock(dead(), self)), // Default, 5
    GREEN_BEAN(FarmlandBlockEntity.NutrientType.NITROGEN, self -> ClimbingCropBlock.create(crop().serverTicks(CropBlockEntity::serverTickBottomPartOnly), 4, 4, self), self -> new DeadClimbingCropBlock(dead(), self), true), // Double, Stick, 4 -> 4
    POTATO(FarmlandBlockEntity.NutrientType.POTASSIUM, self -> DefaultCropBlock.create(crop(), 7, self), self -> new DeadCropBlock(dead(), self)), // Default, 7
    ONION(FarmlandBlockEntity.NutrientType.NITROGEN, self -> DefaultCropBlock.create(crop(), 7, self), self -> new DeadCropBlock(dead(), self)), // Default, 7
    SOYBEAN(FarmlandBlockEntity.NutrientType.NITROGEN, self -> DefaultCropBlock.create(crop(), 7, self), self -> new DeadCropBlock(dead(), self)), // Default, 7
    SQUASH(FarmlandBlockEntity.NutrientType.POTASSIUM, self -> DefaultCropBlock.create(crop(), 8, self), self -> new DeadCropBlock(dead(), self)), // Default , 8
    SUGARCANE(FarmlandBlockEntity.NutrientType.POTASSIUM, self -> DoubleCropBlock.create(crop().serverTicks(CropBlockEntity::serverTickBottomPartOnly), 4, 4, self), self -> new DeadDoubleCropBlock(dead(), self), true), // Double, 4 -> 4
    TOMATO(FarmlandBlockEntity.NutrientType.POTASSIUM, self -> ClimbingCropBlock.create(crop().serverTicks(CropBlockEntity::serverTickBottomPartOnly), 4, 4, self), self -> new DeadClimbingCropBlock(dead(), self), true), // Double, Stick, 4 -> 4
    JUTE(FarmlandBlockEntity.NutrientType.POTASSIUM, self -> DoubleCropBlock.create(crop().serverTicks(CropBlockEntity::serverTickBottomPartOnly), 2, 4, self), self -> new DeadDoubleCropBlock(dead(), self), true); // Double, 2 -> 4
    // todo: pickable crops + spreading crops

    private static ExtendedProperties crop()
    {
        return dead().blockEntity(TFCBlockEntities.CROP).serverTicks(CropBlockEntity::serverTick);
    }

    private static ExtendedProperties dead()
    {
        return ExtendedProperties.of(BlockBehaviour.Properties.of(Material.PLANT).noCollission().randomTicks().strength(0.4F).sound(SoundType.CROP)).flammable(60, 30);
    }

    private final String serializedName;
    private final FarmlandBlockEntity.NutrientType primaryNutrient;
    private final Supplier<Block> factory;
    private final Supplier<Block> deadFactory;
    private final boolean tall;

    Crop(FarmlandBlockEntity.NutrientType primaryNutrient, Function<Crop, Block> factory, Function<Crop, Block> deadFactory)
    {
        this(primaryNutrient, factory, deadFactory, false);
    }

    Crop(FarmlandBlockEntity.NutrientType primaryNutrient, Function<Crop, Block> factory, Function<Crop, Block> deadFactory, boolean tall)
    {
        this.serializedName = name().toLowerCase(Locale.ROOT);
        this.primaryNutrient = primaryNutrient;
        this.factory = () -> factory.apply(this);
        this.deadFactory = () -> deadFactory.apply(this);
        this.tall = tall;
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
        return tall ? new WildDoubleCropBlock(dead()) : new WildCropBlock(dead());
    }

    public FarmlandBlockEntity.NutrientType getPrimaryNutrient()
    {
        return primaryNutrient;
    }
}