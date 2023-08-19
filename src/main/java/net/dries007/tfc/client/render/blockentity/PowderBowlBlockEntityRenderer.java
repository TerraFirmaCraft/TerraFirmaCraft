package net.dries007.tfc.client.render.blockentity;

import java.util.HashMap;
import java.util.Map;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;

import net.dries007.tfc.client.RenderHelpers;
import net.dries007.tfc.common.blockentities.PowderBowlBlockEntity;
import net.dries007.tfc.common.capabilities.Capabilities;
import net.dries007.tfc.util.Helpers;

public class PowderBowlBlockEntityRenderer implements BlockEntityRenderer<PowderBowlBlockEntity>
{
    private static final Map<Item, ResourceLocation> TEXTURES = new HashMap<>();

    @Override
    public void render(PowderBowlBlockEntity bowl, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int combinedLight, int combinedOverlay)
    {
        final var inv = Helpers.getCapability(bowl, Capabilities.ITEM);
        if (inv == null)
            return;
        final ItemStack item = inv.getStackInSlot(0);
        if (item.isEmpty())
            return;

        final ResourceLocation texture = TEXTURES.computeIfAbsent(item.getItem(), i -> {
            final ResourceLocation key = ForgeRegistries.ITEMS.getKey(item.getItem());
            assert key != null;
            String path = key.getPath();
            if (!path.contains("powder/"))
                path = "powder/" + path;
            return new ResourceLocation(key.getNamespace(), "block/" + path);
        });

        final float y = Mth.map(item.getCount(), 0, PowderBowlBlockEntity.MAX_POWDER, 0.5f, 2f);

        poseStack.pushPose();
        RenderHelpers.renderTexturedFace(poseStack, buffer, 255, 2f / 16, 2f / 16, 14f / 16, 14f / 16, y, combinedOverlay, combinedLight, texture);
        poseStack.popPose();
    }
}
