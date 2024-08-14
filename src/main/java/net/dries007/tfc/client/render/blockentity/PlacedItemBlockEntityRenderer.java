/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.client.render.blockentity;

import java.util.Map;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.ModelBlockRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.model.data.ModelData;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.client.RenderHelpers;
import net.dries007.tfc.common.blockentities.PlacedItemBlockEntity;
import net.dries007.tfc.common.items.TFCItems;

public class PlacedItemBlockEntityRenderer<T extends PlacedItemBlockEntity> implements BlockEntityRenderer<T>
{
    /**
     * Custom models, on a per-item basis, when rendered in a placed item. If an item is not present here, it will be rendered using
     * a static item renderer.
     */
    public static final Map<Item, Provider> MODELS = RenderHelpers.mapOf(map -> {
        map.accept(TFCItems.WROUGHT_IRON_GRILL, cutout("block/firepit_grill"));
        map.accept(TFCItems.POT, cutout("block/firepit_pot_placed"));
        map.accept(TFCItems.UNFIRED_POT, cutout("block/firepit_pot_placed_unfired"));
        map.accept(TFCItems.JUG, cutout("block/ceramic/jug"));
        map.accept(TFCItems.UNFIRED_JUG, cutout("block/ceramic/unfired_jug"));
        map.accept(TFCItems.VESSEL, cutout("block/ceramic/small_vessel"));
        map.accept(TFCItems.UNFIRED_VESSEL, cutout("block/ceramic/unfired_small_vessel"));
        TFCItems.GLAZED_VESSELS.forEach((color, item) -> map.accept(item, cutout("block/ceramic/" + color.getSerializedName() + "_small_vessel")));
        TFCItems.UNFIRED_GLAZED_VESSELS.forEach((color, item) -> map.accept(item, cutout("block/ceramic/" + color.getSerializedName() + "_small_vessel_unfired")));
        map.accept(TFCItems.EMPTY_JAR, translucent("block/jar/empty"));
        map.accept(TFCItems.EMPTY_JAR_WITH_LID, translucent("block/jar"));
        TFCItems.FRUIT_PRESERVES.forEach((fruit, item) -> map.accept(item, translucent("block/jar/" + fruit.getSerializedName())));
        TFCItems.UNSEALED_FRUIT_PRESERVES.forEach((fruit, item) -> map.accept(item, translucent("block/jar/" + fruit.getSerializedName() + "_unsealed")));
    });

    private static Provider translucent(String model)
    {
        return new Provider(RenderHelpers.modelId(model), RenderType.translucent());
    }

    private static Provider cutout(String model)
    {
        return new Provider(RenderHelpers.modelId(model), RenderType.cutout());
    }


    @Override
    public void render(T placedItem, float partialTicks, PoseStack pose, MultiBufferSource buffers, int packedLight, int packedOverlay)
    {
        if (!placedItem.hasLevel()) return;

        final ItemStackHandler inventory = placedItem.getInventory();
        final RandomSource random = RandomSource.create();

        if (placedItem.holdingLargeItem())
        {
            renderContent(placedItem, pose, buffers, packedLight, packedOverlay, random, PlacedItemBlockEntity.SLOT_LARGE_ITEM);
        }
        else
        {
            for (int slot = 0; slot < inventory.getSlots(); slot++)
            {
                renderContent(placedItem, pose, buffers, packedLight, packedOverlay, random, slot);
            }
        }
    }

    private void renderContent(T entity, PoseStack pose, MultiBufferSource buffers, int packedLight, int packedOverlay, RandomSource random, int slot)
    {
        assert entity.getLevel() != null;

        final ItemStack stack = entity.getInventory().getStackInSlot(slot);
        if (stack.isEmpty())
        {
            return;
        }

        final boolean isLarge = entity.holdingLargeItem();
        final Minecraft mc = Minecraft.getInstance();
        final @Nullable Provider custom = MODELS.get(stack.getItem());
        final int slotX = slot % 2 == 0 ? 1 : 0;
        final int slotZ = slot < 2 ? 1 : 0;

        pose.pushPose();

        if (custom != null)
        {
            final BakedModel baked = mc.getModelManager().getModel(custom.model);
            final VertexConsumer buffer = buffers.getBuffer(custom.renderType);
            final ModelBlockRenderer blockRenderer = mc.getBlockRenderer().getModelRenderer();

            // Rendering block models is relative to the (0, 0) position, so we translate to the corner of where we want to render
            // We don't scale or rotate for block models. We don't lift them (beyond a slight amount to prevent z fighting in pit kilns), because we assume block models are positioned correctly
            if (!isLarge)
            {
                pose.translate(slotX * 0.5, 0, slotZ * 0.5);
            }
            pose.translate(0, -0.05, 0);

            blockRenderer.tesselateWithAO(entity.getLevel(), baked, entity.getBlockState(), entity.getBlockPos(), pose, buffer, true, random, packedLight, packedOverlay, ModelData.EMPTY, RenderType.translucent());
        }
        else
        {
            // Rendering items, we have two separate placements based on if the model for the item is flat or 3d
            // Flat models, we translate slightly up, and rotate to place flat on the surface. 3D models, we place directly
            // on the surface, and don't rotate them into a 'laying down' position, but, we need to translate upwards so the
            // model is properly 'sitting' on the surface
            final BakedModel model = mc.getItemRenderer().getModel(stack, entity.getLevel(), null, 0);

            if (isLarge)
            {
                // Large items, translate to center
                pose.translate(0.5, model.isGui3d() ? 0.25 : 0.03125, 0.5);
            }
            else
            {
                // For small items, translate to the center of the slot, and then scale down by half
                pose.translate(0.25 + slotX * 0.5, model.isGui3d() ? 0.125 : 0.03125, 0.25 + slotZ * 0.5);
                pose.scale(0.5f, 0.5f, 0.5f);
            }

            if (model.isGui3d())
            {
                // Rotate around the center axis
                pose.mulPose(Axis.YP.rotationDegrees(entity.getRotations(slot)));
            }
            else
            {
                // Flip to flat, then rotate according to the slot
                pose.mulPose(Axis.XP.rotationDegrees(90f));
                pose.mulPose(Axis.ZP.rotationDegrees(entity.getRotations(slot)));
            }

            // Then render the model
            // This is copying what renderStatic() would've done
            mc.getItemRenderer().render(stack, ItemDisplayContext.FIXED, false, pose, buffers, packedLight, packedOverlay, model);
        }

        pose.popPose();
    }

    @Override
    public int getViewDistance()
    {
        return 24;
    }

    public record Provider(ModelResourceLocation model, RenderType renderType) {}
}
