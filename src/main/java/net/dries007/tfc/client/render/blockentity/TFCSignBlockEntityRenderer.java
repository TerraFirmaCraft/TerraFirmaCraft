/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.client.render.blockentity;

import java.util.Map;
import java.util.stream.Stream;
import com.google.common.collect.ImmutableMap;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.Font;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.SignRenderer;
import net.minecraft.client.resources.model.Material;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.SignBlockEntity;

import net.dries007.tfc.TerraFirmaCraft;
import net.dries007.tfc.common.blocks.TFCBlocks;
import net.dries007.tfc.common.blocks.wood.Wood;

import static net.minecraft.client.renderer.Sheets.*;

// todo: custom SignEditScreen
public class TFCSignBlockEntityRenderer extends SignRenderer
{
    private static final int OUTLINE_RENDER_DISTANCE = Mth.square(16);

    private static Material createSignMaterial(String domain, String name)
    {
        return new Material(SIGN_SHEET, new ResourceLocation(domain, "entity/signs/" + name));
    }

    private final Font font;
    private final Map<Block, Material> materials;
    private final Map<Block, SignModel> models;

    public TFCSignBlockEntityRenderer(BlockEntityRendererProvider.Context context)
    {
        this(context, TFCBlocks.WOODS.entrySet()
            .stream()
            .map(entry -> new SignModelData(
                TerraFirmaCraft.MOD_ID,
                entry.getKey().getSerializedName(),
                entry.getValue().get(Wood.BlockType.SIGN).get(),
                entry.getValue().get(Wood.BlockType.WALL_SIGN).get()
            )));
    }

    public TFCSignBlockEntityRenderer(BlockEntityRendererProvider.Context context, Stream<SignModelData> blocks)
    {
        super(context);

        this.font = context.getFont();

        ImmutableMap.Builder<Block, Material> materialBuilder = ImmutableMap.builder();
        ImmutableMap.Builder<Block, SignModel> modelBuilder = ImmutableMap.builder();

        blocks.forEach(data -> {
            final Material material = createSignMaterial(data.domain, data.name);
            final SignModel model = new SignModel(context.bakeLayer(new ModelLayerLocation(new ResourceLocation(data.domain, "sign/" + data.name), "main")));

            materialBuilder.put(data.sign, material);
            materialBuilder.put(data.wallSign, material);
            modelBuilder.put(data.sign, model);
            modelBuilder.put(data.wallSign, model);
        });

        this.materials = materialBuilder.build();
        this.models = modelBuilder.build();
    }

    @Override
    public void render(SignBlockEntity sign, float partialTicks, PoseStack poseStack, MultiBufferSource source, int packedLight, int overlay)
    {
        // todo redo
    }

    public record SignModelData(String domain, String name, Block sign, Block wallSign) {}
}
