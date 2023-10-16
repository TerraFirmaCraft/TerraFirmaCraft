/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blocks.wood;

import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.resources.model.Material;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.CeilingHangingSignBlock;
import net.minecraft.world.level.block.WallHangingSignBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.WoodType;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.common.blocks.EntityBlockExtension;
import net.dries007.tfc.common.blocks.ExtendedProperties;
import net.dries007.tfc.common.blocks.IForgeBlockExtension;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.Metal;

public class TFCWallHangingSignBlock extends WallHangingSignBlock implements IForgeBlockExtension, EntityBlockExtension, ITFCHangingSignBlock
{
    private final ExtendedProperties properties;
    private final Material hangingSignMaterial;
    private final ResourceLocation hangingSignGUITexture;

    public TFCWallHangingSignBlock(ExtendedProperties properties, WoodType type, Metal.Default metal)
    {
        this(properties, type, Helpers.identifier(metal.getSerializedName()));
    }
    public TFCWallHangingSignBlock(ExtendedProperties properties, WoodType type, ResourceLocation metal)
    {
        super(properties.properties(), type);
        this.properties = properties;
        ResourceLocation location = new ResourceLocation(type.name());
        this.hangingSignMaterial = new Material(Sheets.SIGN_SHEET, new ResourceLocation(location.getNamespace(), "entity/signs/hanging/" + metal.getPath() + "/" + location.getPath()));
        this.hangingSignGUITexture = new ResourceLocation(type.name() + ".png").withPrefix("textures/gui/hanging_signs/" + metal.getPath() + "/");
    }

    public Material hangingSignMaterial()
    {
        return hangingSignMaterial;
    }
    public ResourceLocation hangingSignGUITexture()
    {
        return hangingSignGUITexture;
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type)
    {
        return EntityBlockExtension.super.getTicker(level, state, type);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state)
    {
        return EntityBlockExtension.super.newBlockEntity(pos, state);
    }

    @Override
    public ExtendedProperties getExtendedProperties()
    {
        return properties;
    }
}
