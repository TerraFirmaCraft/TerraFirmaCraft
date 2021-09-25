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
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.climate.ClimateRange;
import net.dries007.tfc.util.climate.ClimateRanges;

public enum Crop implements StringRepresentable
{
    // Grains
    BARLEY(FarmlandBlockEntity.NutrientType.NITROGEN, self -> DefaultCropBlock.create(crop(), 8, self), self -> new DeadCropBlock(dead())), // Default, 8
    OAT(FarmlandBlockEntity.NutrientType.PHOSPHOROUS, self -> DefaultCropBlock.create(crop(), 8, self), self -> new DeadCropBlock(dead())), // Default, 8
    RYE(FarmlandBlockEntity.NutrientType.PHOSPHOROUS, self -> DefaultCropBlock.create(crop(), 8, self), self -> new DeadCropBlock(dead())), // Default, 8
    MAIZE(FarmlandBlockEntity.NutrientType.PHOSPHOROUS, self -> DoubleCropBlock.create(crop().serverTicks(CropBlockEntity::serverTickBottomPartOnly), 3, 3, self), self -> new DoubleDeadCropBlock(dead())), // Double, 3 -> 3
    WHEAT(FarmlandBlockEntity.NutrientType.PHOSPHOROUS, self -> DefaultCropBlock.create(crop(), 8, self), self -> new DeadCropBlock(dead())), // Default, 8
    RICE(FarmlandBlockEntity.NutrientType.PHOSPHOROUS, self -> FloodedCropBlock.create(crop(), 8, self), self -> new FloodedDeadCropBlock(dead())), // Default, Waterlogged, 8
    // Vegetables
    BEET(FarmlandBlockEntity.NutrientType.POTASSIUM, self -> RootCropBlock.create(crop(), 6, self), self -> new DeadCropBlock(dead())), // Default, Root, 6
    CABBAGE(FarmlandBlockEntity.NutrientType.NITROGEN, self -> DefaultCropBlock.create(crop(), 6, self), self -> new DeadCropBlock(dead())), // Default, 6
    CARROT(FarmlandBlockEntity.NutrientType.POTASSIUM, self -> RootCropBlock.create(crop(), 5, self), self -> new DeadCropBlock(dead())), // Default, Root, 5
    GARLIC(FarmlandBlockEntity.NutrientType.NITROGEN, self -> RootCropBlock.create(crop(), 5, self), self -> new DeadCropBlock(dead())), // Default, Root, 5
    GREEN_BEAN(FarmlandBlockEntity.NutrientType.NITROGEN, self -> ClimbingCropBlock.create(crop().serverTicks(CropBlockEntity::serverTickBottomPartOnly), 4, 4, self), self -> new ClimbingDeadCropBlock(dead())), // Double, Pickable, Stick, 4 -> 4
    POTATO(FarmlandBlockEntity.NutrientType.POTASSIUM, self -> RootCropBlock.create(crop(), 7, self), self -> new DeadCropBlock(dead())), // Default, Root, 7
    ONION(FarmlandBlockEntity.NutrientType.NITROGEN, self -> RootCropBlock.create(crop(), 7, self), self -> new DeadCropBlock(dead())), // Default, Root, 7
    SOYBEAN(FarmlandBlockEntity.NutrientType.NITROGEN, self -> DefaultCropBlock.create(crop(), 7, self), self -> new DeadCropBlock(dead())), // Default, 7
    SQUASH(FarmlandBlockEntity.NutrientType.POTASSIUM, self -> DefaultCropBlock.create(crop(), 8, self), self -> new DeadCropBlock(dead())), // Default , 8
    SUGARCANE(FarmlandBlockEntity.NutrientType.POTASSIUM, self -> DoubleCropBlock.create(crop().serverTicks(CropBlockEntity::serverTickBottomPartOnly), 4, 4, self), self -> new DoubleDeadCropBlock(dead())), // Double, 4 -> 4
    TOMATO(FarmlandBlockEntity.NutrientType.POTASSIUM, self -> ClimbingCropBlock.create(crop().serverTicks(CropBlockEntity::serverTickBottomPartOnly), 4, 4, self), self -> new ClimbingDeadCropBlock(dead())), // Double, Stick, Pickable, 4 -> 4
    //BELL_PEPPER(), // Default, Pickable, Multiple Grown Stages, ???
    JUTE(FarmlandBlockEntity.NutrientType.POTASSIUM, self -> DoubleCropBlock.create(crop().serverTicks(CropBlockEntity::serverTickBottomPartOnly), 2, 4, self), self -> new DoubleDeadCropBlock(dead())); // Double, 2 -> 4

    private static ExtendedProperties crop()
    {
        return ExtendedProperties.of(dead()).blockEntity(TFCBlockEntities.CROP).serverTicks(CropBlockEntity::serverTick);
    }

    private static BlockBehaviour.Properties dead()
    {
        return BlockBehaviour.Properties.of(Material.PLANT).noCollission().randomTicks().instabreak().sound(SoundType.CROP);
    }

    private final String serializedName;
    private final FarmlandBlockEntity.NutrientType primaryNutrient;
    private final Supplier<Block> factory;
    private final Supplier<Block> deadFactory;

    Crop(FarmlandBlockEntity.NutrientType primaryNutrient, Function<Crop, Block> factory, Function<Crop, Block> deadFactory)
    {
        this.serializedName = name().toLowerCase(Locale.ROOT);
        this.primaryNutrient = primaryNutrient;
        this.factory = () -> factory.apply(this);
        this.deadFactory = () -> deadFactory.apply(this);
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

    public FarmlandBlockEntity.NutrientType getPrimaryNutrient()
    {
        return primaryNutrient;
    }
}