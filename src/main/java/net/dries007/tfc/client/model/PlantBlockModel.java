package net.dries007.tfc.client.model;

import java.util.List;
import java.util.function.Function;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverride;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.model.data.ModelData;
import net.neoforged.neoforge.client.model.data.ModelProperty;
import net.neoforged.neoforge.client.model.geometry.IGeometryBakingContext;
import net.neoforged.neoforge.client.model.geometry.IGeometryLoader;
import org.jetbrains.annotations.Nullable;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.neoforged.neoforge.client.model.IDynamicBakedModel;
import net.neoforged.neoforge.client.model.geometry.IUnbakedGeometry;

import net.dries007.tfc.TerraFirmaCraft;
import net.dries007.tfc.client.ClientHelpers;
import net.dries007.tfc.client.ClimateRenderCache;
import net.dries007.tfc.client.RenderHelpers;
import net.dries007.tfc.common.blocks.plant.Plant;
import net.dries007.tfc.common.blocks.plant.PlantBlock;
import net.dries007.tfc.util.calendar.Calendars;
import net.dries007.tfc.util.registry.RegistryPlant;

public class PlantBlockModel implements IDynamicBakedModel, IUnbakedGeometry<PlantBlockModel>
{
    private final BlockModel dormant;
    private final BlockModel sprouting;
    private final BlockModel budding;
    private final BlockModel blooming;
    private final BlockModel seeding;
    private final BlockModel dying;

    @Nullable private BakedModel dormantBakedModel;
    @Nullable private BakedModel sproutingBakedModel;
    @Nullable private BakedModel buddingBakedModel;
    @Nullable private BakedModel bloomingBakedModel;
    @Nullable private BakedModel seedingBakedModel;
    @Nullable private BakedModel dyingBakedModel;

    public PlantBlockModel(BlockModel dormant, BlockModel sprouting, BlockModel budding, BlockModel blooming, BlockModel seeding, BlockModel dying)
    {
        this.dormant = dormant;
        this.sprouting = sprouting;
        this.budding = budding;
        this.blooming = blooming;
        this.seeding = seeding;
        this.dying = dying;
    }

    public ModelData getModelData(BlockAndTintGetter level, BlockPos pos, BlockState state, ModelData data)
    {
        return data.derive().with(BakedModelData.PROPERTY, new BakedModelData(getModelFromBlockState(state))).build();
    }

    private BakedModel getModelFromBlockState(@Nullable BlockState state)
    {
        if (state == null)
        {
            return getModelFromCalendar();
        }
        final Block block = state.getBlock();
        if (!(block instanceof PlantBlock))
        {
            return getModelFromCalendar();
        }
        else
        {
            final RegistryPlant plant = ((PlantBlock) block).getPlant();
            final float start = plant.getBloomOffset() + (plant.isWetSeasonBlooming() ? ClimateRenderCache.INSTANCE.getRainVariance() < 0f ? 0f : 0.5f : 0.5f) % 1f;
            return getModelFromCalendar(start, start + plant.getBloomingEnd(), start + plant.getSeedingEnd(), start + plant.getDyingEnd(),
                start + plant.getDormantEnd(), start + plant.getSproutingEnd(), plant.getStartTime(), plant.getEndTime());
        }
    }

    private BakedModel getModelFromCalendar()
    {
        return getModelFromCalendar(0.4f, 0.6f, 0.75f, 0.9f, 1.1f, 1.25f, 0, 0);
    }

    private BakedModel getModelFromCalendar(float bloomingStart, float bloomingEnd, float seedingEnd, float dyingEnd, float dormantEnd, float sproutingEnd, int startTime, int endTime)
    {
        final float timeOfYear = Calendars.CLIENT.getCalendarFractionOfYear();
        final float adjustedTimeOfYear = timeOfYear < bloomingStart ? timeOfYear + 1f : timeOfYear;

        if (adjustedTimeOfYear < bloomingEnd)
        {
            //TODO: Revisit with variable day lengths
            if (startTime != endTime)
            {
                final int dayTime = (int) Calendars.CLIENT.getCalendarDayTime();
                if ((endTime < dayTime && dayTime < startTime) || (startTime < endTime && (dayTime < startTime || endTime < dayTime)))
                {
                    assert buddingBakedModel != null;
                    return buddingBakedModel;
                }
            }
            assert bloomingBakedModel != null;
            return bloomingBakedModel;
        }
        else if (adjustedTimeOfYear < seedingEnd)
        {
            assert seedingBakedModel != null;
            return seedingBakedModel;
        }
        else if (adjustedTimeOfYear < dyingEnd)
        {
            assert dyingBakedModel != null;
            return dyingBakedModel;
        }
        if (adjustedTimeOfYear < dormantEnd)
        {
            assert dormantBakedModel != null;
            return dormantBakedModel;
        }
        else if (adjustedTimeOfYear < sproutingEnd)
        {
            assert sproutingBakedModel != null;
            return sproutingBakedModel;
        }
        else
        {
            assert buddingBakedModel != null;
            return buddingBakedModel;
        }
    }

    @Override
    public BakedModel bake(IGeometryBakingContext context, ModelBaker baker, Function<Material, TextureAtlasSprite> atlas, ModelState modelState, ItemOverrides overrides)
    {
        dormantBakedModel = dormant.bake(baker, atlas, modelState);
        sproutingBakedModel = sprouting.bake(baker, atlas, modelState);
        buddingBakedModel = budding.bake(baker, atlas, modelState);
        bloomingBakedModel = blooming.bake(baker, atlas, modelState);
        seedingBakedModel = seeding.bake(baker, atlas, modelState);
        dyingBakedModel = dying.bake(baker, atlas, modelState);
        return this;
    }

    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction direction, RandomSource random, ModelData modelData, @Nullable RenderType renderType)
    {
        final BakedModelData bakedData = modelData.get(BakedModelData.PROPERTY);
        if (bakedData != null)
        {
            return bakedData.toRender.getQuads(state, direction, random, modelData, renderType);
        }

        return getModelFromBlockState(state).getQuads(state, direction, random, modelData, renderType);
    }

    @Override
    public void resolveParents(Function<ResourceLocation, UnbakedModel> modelGetter, IGeometryBakingContext context)
    {
        dormant.resolveParents(modelGetter);
        sprouting.resolveParents(modelGetter);
        budding.resolveParents(modelGetter);
        blooming.resolveParents(modelGetter);
        seeding.resolveParents(modelGetter);
        dying.resolveParents(modelGetter);
    }

    @Override
    public boolean useAmbientOcclusion()
    {
        return true;
    }

    @Override
    public boolean isGui3d()
    {
        return false;
    }

    @Override
    public boolean usesBlockLight()
    {
        return true;
    }

    @Override
    public boolean isCustomRenderer()
    {
        return false;
    }

    //TODO: This probably needs to return an actual texture... Could probably be a generic "plant" texture though
    @Override
    public TextureAtlasSprite getParticleIcon()
    {
        return RenderHelpers.missingTexture();
    }

    @Override
    public TextureAtlasSprite getParticleIcon(ModelData data)
    {
        final BakedModelData bakedData = data.get(BakedModelData.PROPERTY);
        return bakedData != null ? bakedData.toRender.getParticleIcon(data) : RenderHelpers.missingTexture();
    }

    @Override
    public ItemOverrides getOverrides()
    {
        return ItemOverrides.EMPTY;
    }

    record BakedModelData(BakedModel toRender)
    {
        public static final ModelProperty<BakedModelData> PROPERTY = new ModelProperty<>();
    }

    public static class Loader implements IGeometryLoader<PlantBlockModel>
    {
        @Override
        public PlantBlockModel read(JsonObject json, JsonDeserializationContext context) throws JsonParseException
        {
            return new PlantBlockModel(
                context.deserialize(json.get("dormant"), BlockModel.class),
                context.deserialize(json.get("sprouting"), BlockModel.class),
                context.deserialize(json.get("budding"), BlockModel.class),
                context.deserialize(json.get("blooming"), BlockModel.class),
                context.deserialize(json.get("seeding"), BlockModel.class),
                context.deserialize(json.get("dying"), BlockModel.class)
            );
        }
    }
}
