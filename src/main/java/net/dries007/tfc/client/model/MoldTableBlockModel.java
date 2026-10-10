/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.client.model;

import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.model.IDynamicBakedModel;
import net.neoforged.neoforge.client.model.data.ModelData;
import net.neoforged.neoforge.client.model.data.ModelProperty;
import net.neoforged.neoforge.client.model.geometry.IGeometryBakingContext;
import net.neoforged.neoforge.client.model.geometry.IGeometryLoader;
import net.neoforged.neoforge.client.model.geometry.IUnbakedGeometry;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.TerraFirmaCraft;
import net.dries007.tfc.client.RenderHelpers;
import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.blockentities.TFCBlockEntities;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.collections.IndirectHashCollection;

public class MoldTableBlockModel implements IDynamicBakedModel, IUnbakedGeometry<MoldTableBlockModel>
{
    private final BlockModel model;
    private @Nullable BakedModel baked;

    public MoldTableBlockModel(BlockModel model)
    {
        this.model = model;
    }

    @Override
    public ModelData getModelData(BlockAndTintGetter level, BlockPos pos, BlockState state, ModelData modelData)
    {
        final BlockEntity blockEntity = level.getBlockEntity(pos);
        if (blockEntity != null && blockEntity.getType() == TFCBlockEntities.MOLD_TABLE.get())
        {
            return blockEntity.getModelData();
        }
        return modelData;
    }

    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction direction, RandomSource random, ModelData modelData, @Nullable RenderType renderType)
    {
        assert baked != null;
        MoldModelData moldData = modelData.get(MoldModelData.PROPERTY);
        List<BakedQuad> quads = new ArrayList<>(baked.getQuads(state, direction, random, modelData, renderType));
        if (moldData != null && moldData.model() != null)
        {
            quads.addAll(moldData.model().getQuads(state, direction, random, modelData, renderType));
        }
        return quads;
    }

    @Override
    public boolean useAmbientOcclusion()
    {
        return model.hasAmbientOcclusion();
    }

    @Override
    public boolean isGui3d()
    {
        assert baked != null;
        return baked.isGui3d();
    }

    @Override
    public boolean usesBlockLight()
    {
        assert baked != null;
        return baked.usesBlockLight();
    }

    @Override
    public boolean isCustomRenderer()
    {
        assert baked != null;
        return baked.isCustomRenderer();
    }

    @Override
    public TextureAtlasSprite getParticleIcon()
    {
        assert baked != null;
        return baked.getParticleIcon();
    }

    @Override
    public ItemOverrides getOverrides()
    {
        return ItemOverrides.EMPTY;
    }

    @Override
    public BakedModel bake(IGeometryBakingContext context, ModelBaker baker, Function<Material, TextureAtlasSprite> mapper, ModelState state, ItemOverrides overrides)
    {
        baked = model.bake(baker, mapper, state);
        return this;
    }

    public record MoldModelData(@Nullable BakedModel model)
    {
        public static final ModelProperty<MoldModelData> PROPERTY = new ModelProperty<>();
    }

    public static class Loader implements IGeometryLoader<MoldTableBlockModel>
    {
        public static final Loader INSTANCE = new Loader();

        private Loader() {}

        @Override
        public MoldTableBlockModel read(JsonObject json, JsonDeserializationContext context) throws JsonParseException
        {
            // Load the table model as a default model
            json.remove("loader");
            BlockModel model = context.deserialize(json, BlockModel.class);
            return new MoldTableBlockModel(model);
        }
    }

    // Beyond this point could go in MoldTableBlockEntity but that file is already very long
    public static MoldModelData getMoldModelData(ItemStack stack)
    {
        if (!stack.isEmpty())
        {
            return new MoldModelData(MOLD_MODEL_CACHE.values.get(stack.getItem()));
        }
        return new MoldModelData(null);
    }

    private static final MoldModelCache MOLD_MODEL_CACHE = IndirectHashCollection.create(new MoldModelCache(new IdentityHashMap<>()));

    record MoldModelCache(Map<Item, BakedModel> values) implements IndirectHashCollection.Cache
    {
        @Override
        public void clear()
        {
            values.clear();
        }

        @Override
        public void reload(RecipeManager manager)
        {
            BuiltInRegistries.ITEM.getTagOrEmpty(TFCTags.Items.USABLE_IN_MOLD_TABLE).forEach(
                (item) -> {
                    ResourceLocation moldLocation = BuiltInRegistries.ITEM.getKey(item.value());
                    ModelResourceLocation modelLocation = RenderHelpers.modelId(
                        Helpers.resourceLocation(
                            moldLocation.getNamespace(),
                            "block/mold/" + moldLocation.getPath()
                        )
                    );

                    BakedModel model = Minecraft.getInstance().getModelManager().getModel(modelLocation);

                    if (model != Minecraft.getInstance().getModelManager().getMissingModel())
                    {
                        values.put(item.value(), model);
                    }
                    else
                    {
                        TerraFirmaCraft.LOGGER.error("No mold model loaded for mold item {}", moldLocation);
                    }
                }
            );
        }
    }
}
