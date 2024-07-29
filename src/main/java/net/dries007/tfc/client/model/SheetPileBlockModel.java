/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.client.model;

import java.util.function.Function;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import net.dries007.tfc.client.RenderHelpers;
import net.dries007.tfc.common.blockentities.SheetPileBlockEntity;
import net.dries007.tfc.common.blockentities.TFCBlockEntities;
import net.dries007.tfc.common.blocks.DirectionPropertyBlock;
import net.dries007.tfc.common.blocks.devices.SheetPileBlock;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.MetalItem;

public enum SheetPileBlockModel implements SimpleStaticBlockEntityModel<SheetPileBlockModel, SheetPileBlockEntity>
{
    INSTANCE;

    @Override
    public TextureAtlasSprite render(SheetPileBlockEntity pile, PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay)
    {
        final BlockState state = pile.getBlockState();
        TextureAtlasSprite sprite = null;

        final Function<ResourceLocation, TextureAtlasSprite> textureAtlas = Minecraft.getInstance().getTextureAtlas(RenderHelpers.BLOCKS_ATLAS);

        for (Direction direction : Helpers.DIRECTIONS)
        {
            if (state.getValue(DirectionPropertyBlock.getProperty(direction))) // The properties are authoritative on which sides should be rendered
            {
                final MetalItem metal = pile.getOrCacheMetal(direction);
                sprite = textureAtlas.apply(metal.textureId());

                renderSheet(poseStack, sprite, buffer, direction, packedLight, packedOverlay);
            }
        }

        if (sprite == null)
        {
            // Use whatever sprite we found in the ingot pile towards the top as the particle texture
            sprite = RenderHelpers.missingTexture();
        }

        return sprite;
    }

    @Override
    public BlockEntityType<SheetPileBlockEntity> type()
    {
        return TFCBlockEntities.SHEET_PILE.get();
    }

    @Override
    public int faces(SheetPileBlockEntity blockEntity)
    {
        return 6 * 6;  // room for 6 faces x each sheet
    }

    private void renderSheet(PoseStack poseStack, TextureAtlasSprite sprite, VertexConsumer buffer, Direction direction, int packedLight, int packedOverlay)
    {
        RenderHelpers.renderTexturedCuboid(poseStack, buffer, sprite, packedLight, packedOverlay, SheetPileBlock.getShapeForSingleFace(direction).bounds());
    }
}
