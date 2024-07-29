/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.client.render.blockentity;

import java.util.HashMap;
import java.util.Map;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.Util;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import net.dries007.tfc.client.RenderHelpers;
import net.dries007.tfc.common.blockentities.BowlBlockEntity;
import net.dries007.tfc.common.items.TFCItems;
import net.dries007.tfc.util.Helpers;

public class BowlBlockEntityRenderer implements BlockEntityRenderer<BowlBlockEntity>
{
    private static final ResourceLocation FALLBACK = Helpers.identifier("block/powder/salt");
    private static final Map<Item, ResourceLocation> TEXTURES = Util.make(new HashMap<>(), map -> {
        TFCItems.POWDERS.forEach((type, item) -> map.put(item.asItem(), item.getId().withPrefix("block/")));
        TFCItems.ORE_POWDERS.forEach((type, item) -> map.put(item.asItem(), item.getId().withPrefix("block/")));
        map.put(Items.REDSTONE, Helpers.identifier("block/powder/redstone"));
        map.put(Items.GLOWSTONE_DUST, Helpers.identifier("block/powder/glowstone"));
        map.put(Items.BLAZE_POWDER, Helpers.identifier("block/powder/blaze_powder"));
        map.put(Items.GUNPOWDER, Helpers.identifier("block/powder/gunpowder"));
    });

    @Override
    public void render(BowlBlockEntity bowl, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int combinedLight, int combinedOverlay)
    {
        final ItemStack item = bowl.getInventory().getStackInSlot(0);
        if (item.isEmpty())
        {
            return;
        }

        final ResourceLocation texture = TEXTURES.getOrDefault(item.getItem(), FALLBACK);

        final float y = Mth.map(item.getCount(), 0, BowlBlockEntity.MAX_POWDER, 0.5f, 2f);

        RenderHelpers.renderTexturedFace(poseStack, buffer, 0xFFFFFF, 2f / 16, 2f / 16, 14f / 16, 14f / 16, y / 16f, combinedOverlay, combinedLight, texture);
    }
}
