/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.client.render.blockentity;

import java.util.HashMap;
import java.util.Map;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import net.dries007.tfc.client.RenderHelpers;
import net.dries007.tfc.common.blockentities.BowlBlockEntity;
import net.dries007.tfc.util.Helpers;

public class BowlBlockEntityRenderer implements BlockEntityRenderer<BowlBlockEntity>
{
    private static final Map<Item, ResourceLocation> TEXTURES = new HashMap<>();

    @Override
    public void render(BowlBlockEntity bowl, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int combinedLight, int combinedOverlay)
    {
        final ItemStack item = bowl.getInventory().getStackInSlot(0);
        if (item.isEmpty())
        {
            return;
        }

        final ResourceLocation texture = TEXTURES.computeIfAbsent(item.getItem(), i -> {
            final ResourceLocation key = BuiltInRegistries.ITEM.getKey(item.getItem());
            String path = key.getPath();
            if (!path.contains("powder/"))
                path = "powder/" + path;
            return Helpers.resourceLocation(key.getNamespace(), "block/" + path);
        });

        final float y = Mth.map(item.getCount(), 0, BowlBlockEntity.MAX_POWDER, 0.5f, 2f);

        RenderHelpers.renderTexturedFace(poseStack, buffer, 0xFFFFFF, 2f / 16, 2f / 16, 14f / 16, 14f / 16, y / 16f, combinedOverlay, combinedLight, texture, false);
    }
}
