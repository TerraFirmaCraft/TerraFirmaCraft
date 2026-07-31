/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.client.model;

import java.util.List;
import java.util.function.Function;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.BlockModel;
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
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.model.IDynamicBakedModel;
import net.neoforged.neoforge.client.model.data.ModelData;
import net.neoforged.neoforge.client.model.data.ModelProperty;
import net.neoforged.neoforge.client.model.geometry.IGeometryBakingContext;
import net.neoforged.neoforge.client.model.geometry.IGeometryLoader;
import net.neoforged.neoforge.client.model.geometry.IUnbakedGeometry;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.client.ClientHelpers;
import net.dries007.tfc.client.ClimateRenderCache;
import net.dries007.tfc.client.RenderHelpers;
import net.dries007.tfc.client.TFCColors;
import net.dries007.tfc.client.overworld.SolarCalculator;
import net.dries007.tfc.common.blocks.wood.TFCLeavesBlock;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.calendar.Calendars;
import net.dries007.tfc.util.climate.Climate;

import static net.dries007.tfc.world.TFCChunkGenerator.*;

public class LeavesBlockModel implements IDynamicBakedModel, IUnbakedGeometry<LeavesBlockModel>
{
    private final BlockModel denseLeaves;
    private final BlockModel sparseLeaves;
    private final BlockModel bare;
    private final BlockModel blooming;

    @Nullable private BakedModel denseLeavesBakedModel;
    @Nullable private BakedModel sparseLeavesBakedModel;
    @Nullable private BakedModel bareBakedModel;
    @Nullable private BakedModel bloomingBakedModel;

    public LeavesBlockModel(BlockModel denseLeaves, BlockModel sparseLeaves, BlockModel bare, BlockModel snowyBare, BlockModel snowyLeaves, BlockModel blooming)
    {
        this.denseLeaves = denseLeaves;
        this.sparseLeaves = sparseLeaves;
        this.bare = bare;
        this.blooming = blooming;
    }

    @Override
    public ModelData getModelData(BlockAndTintGetter level, BlockPos pos, BlockState state, ModelData data)
    {
        return data.derive().with(BakedModelData.PROPERTY, new BakedModelData(getModelFromBlockState(state, pos), pos)).build();
    }

    /**
     * Uses similar logic to {@link TFCColors#getSeasonalFoliageColor} to display different models for leaf blocks at different times of year
     * As an overview, these models are:
     * Bare - Displayed in winter months in sufficiently cold climates, or during sufficiently extreme dry seasons of sufficiently warm climates
     * Dense Leaves - Displayed year-round in sufficiently warm and wet climates, for evergreen trees, or during the summers/wet seasons of locations that change seasonally
     * Sparse Leaves - Displayed when transitioning from Bare to Dense Leaves models, or vice versa (Spring and Autumn)
     * Blooming - Displayed for roughly a month that begins at the end of winter, or some time offset by the end of winter, see {@link TFCLeavesBlock#getFlowerOffset()}
     */
    private BakedModel getModelFromBlockState(@Nullable BlockState state, @Nullable BlockPos pos)
    {
        // Fast graphics should disable this behavior entirely
        if (!ClientHelpers.useFancyGraphics())
        {
            assert denseLeavesBakedModel != null;
            return denseLeavesBakedModel;
        }

        final float flowerOffset;
        final boolean isConifer;
        // Checks whether the tree species has a flowering stage
        if (pos == null)
        {
            pos = BlockPos.ZERO;
        }
        // Default to using same texture all year round (evergreen behavior)
        if (state == null)
        {
            assert denseLeavesBakedModel != null;
            return denseLeavesBakedModel;
        }
        else
        {
            final Block block = state.getBlock();
            if (block instanceof TFCLeavesBlock)
            {
                isConifer = ((TFCLeavesBlock) block).isConifer();
                flowerOffset = ((TFCLeavesBlock) block).getFlowerOffset();
            }
            else
            {
                assert denseLeavesBakedModel != null;
                return denseLeavesBakedModel;
            }
        }

        final Level level = ClientHelpers.getLevel();
        if (level == null)
        {
            assert denseLeavesBakedModel != null;
            return denseLeavesBakedModel;
        }

        // Calculates the seasons based on average temperature
        // Should match the method used in TFCColors

        // Hash climate values based on block positions. This helps with transitional areas
        final BlockPos seaLevelPos = new BlockPos(pos.getX(), SEA_LEVEL_Y, pos.getZ());
        float temp = Climate.getAverageTemperature(level, seaLevelPos);
        float rainVar = Climate.getRainfallVariance(level, pos);
        if ((temp > 11.7 && temp < 12.8) || (rainVar > 0.38 && rainVar < 0.42))
        {
            final int positionClimateHash = (Helpers.hash(912381187503828153L, pos) & 127);
            temp += (float) (positionClimateHash - 63) / 4_000f;
            rainVar += (float) (positionClimateHash - 63) / 60_000f;
        }
        final float rainVarAbs = Math.abs(rainVar);

        // Since even trees that do not change foliage color may have a flowering phase,
        // we do need to check time of year earlier than we do for foliage colors
        float timeOfYear = Calendars.CLIENT.getCalendarFractionOfYear();

        // See Desmos: https://www.desmos.com/calculator/jw5zkjxtnz
        final float x;
        final boolean inEvergreenClimate;
        final boolean inNorthernHemisphere = SolarCalculator.getInNorthernHemisphere(pos.getZ(), ClimateRenderCache.INSTANCE.getHemisphereScale());
        float seasonOffset = 0;
        if (temp <= 12f)
        {
            // Numbers chosen to create a 2.5-month summer at -20c avg, and a 12-month "summer" at 15c avg
            x = 1.25f * Math.max(temp, -20f) + 7.6f;
            inEvergreenClimate = false;
            if (!inNorthernHemisphere)
            {
                seasonOffset = 0.5f;
            }
        }
        else
        {
            // For dry-season controlled climates, the minimum rain must be below 120
            final float avgRain = Climate.getAverageRainfall(level, seaLevelPos);
            final float minRain = avgRain * (1 - rainVarAbs);

            // Small gap in temperature is so that there are small evergreen bands between dry-season controlled areas and winter-controlled areas
            if (rainVarAbs > 0.4 && temp > 12.5f && minRain <= 120)
            {
                if (rainVar < 0)
                {
                    seasonOffset = 0.5f;
                }
                // Numbers chosen to create a 4-month wet season at max rain var & min rain = 0, and a 12-month "wet season" at minimum rain var & min rain = 120
                // Uses multiple variables to ensure smooth transitions, and that biomes that have green grass year-round do not lose leaves
                x = -.2604f * (0.4f - rainVarAbs) * (120f - minRain) + 18.75f + 5.3f;
                inEvergreenClimate = false;
            }
            // If not in any of the above areas, must be in an evergreen area
            else
            {
                if (!inNorthernHemisphere)
                {
                    seasonOffset = 0.5f;
                }
                x = 24.05f;
                inEvergreenClimate = true;
            }

        }

        final float cubedTerm = x * x * x / 4096; // 1 / 16^3
        final float squaredTerm = x * x / 256; // 1 / 16^2

        // Offset the seasons by six months if in southern hemisphere, or if dry season is in the summer
        // Positional hashing to fuzz the time of year per-block
        final int positionDeltaHash = (Helpers.hash(836494187578334123L, pos) & 127);
        timeOfYear = (1 + timeOfYear + seasonOffset + ((positionDeltaHash - 63) / 4096f)) % 1;

        final float autumnEnd = (cubedTerm - squaredTerm + 10.5f) / 12f;
        final float springStart = 1f - autumnEnd;
        final float warmSeasonLength = autumnEnd - springStart;
        final float bloomStart = springStart + flowerOffset * warmSeasonLength;

        if (timeOfYear > bloomStart)
        {
            final float bloomEnd = bloomStart + Math.min(0.167f * warmSeasonLength, 0.125f);
            if (timeOfYear < bloomEnd)
            {
                assert bloomingBakedModel != null;
                return bloomingBakedModel;
            }
        }

        // Now that we've checked it isn't blooming, skip calcs if in an evergreen climate
        if (inEvergreenClimate || isConifer)
        {
            assert denseLeavesBakedModel != null;
            return denseLeavesBakedModel;
        }

        if (timeOfYear > autumnEnd)
        {
            assert bareBakedModel != null;
            return bareBakedModel;
        }
        final float autumnStart = (cubedTerm - squaredTerm + 8.5f) / 12f;
        final float autumnMid = 0.5f * (autumnEnd + autumnStart);
        if (timeOfYear > autumnMid)
        {
            assert sparseLeavesBakedModel != null;
            return sparseLeavesBakedModel;
        }
        final float springMid = 1f - autumnMid;
        if (timeOfYear > springMid)
        {
            assert denseLeavesBakedModel != null;
            return denseLeavesBakedModel;
        }
        if (timeOfYear > springStart)
        {
            assert sparseLeavesBakedModel != null;
            return sparseLeavesBakedModel;
        }
        assert bareBakedModel != null;
        return bareBakedModel;
    }

    @Override
    public BakedModel bake(IGeometryBakingContext context, ModelBaker baker, Function<Material, TextureAtlasSprite> atlas, ModelState modelState, ItemOverrides overrides)
    {
        denseLeavesBakedModel = denseLeaves.bake(baker, atlas, modelState);
        sparseLeavesBakedModel = sparseLeaves.bake(baker, atlas, modelState);
        bareBakedModel = bare.bake(baker, atlas, modelState);
        bloomingBakedModel = blooming.bake(baker, atlas, modelState);
        return this;
    }

    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction direction, RandomSource random, ModelData modelData, @Nullable RenderType renderType)
    {
        final BakedModelData bakedData = modelData.get(BakedModelData.PROPERTY);
        final BlockPos pos;
        if (bakedData != null)
        {
            pos = bakedData.pos;
        }
        else
        {
            pos = null;
        }

        return getModelFromBlockState(state, pos).getQuads(state, direction, random, modelData, renderType);
    }

    @Override
    public void resolveParents(Function<ResourceLocation, UnbakedModel> modelGetter, IGeometryBakingContext context)
    {
        denseLeaves.resolveParents(modelGetter);
        sparseLeaves.resolveParents(modelGetter);
        bare.resolveParents(modelGetter);
        blooming.resolveParents(modelGetter);
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

    @Override
    @SuppressWarnings("deprecation")
    public TextureAtlasSprite getParticleIcon()
    {
        return bloomingBakedModel != null ? bloomingBakedModel.getParticleIcon() : RenderHelpers.missingTexture();
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

    record BakedModelData(BakedModel toRender, BlockPos pos)
    {
        public static final ModelProperty<BakedModelData> PROPERTY = new ModelProperty<>();
    }

    public static class Loader implements IGeometryLoader<LeavesBlockModel>
    {
        public static final Loader INSTANCE = new Loader();

        private Loader() {}

        @Override
        public LeavesBlockModel read(JsonObject json, JsonDeserializationContext context) throws JsonParseException
        {
            return new LeavesBlockModel(
                context.deserialize(json.get("dense_leaves"), BlockModel.class),
                context.deserialize(json.get("sparse_leaves"), BlockModel.class),
                context.deserialize(json.get("bare"), BlockModel.class),
                context.deserialize(json.get("snowy_bare"), BlockModel.class),
                context.deserialize(json.get("snowy_leaves"), BlockModel.class),
                context.deserialize(json.get("blooming"), BlockModel.class)
            );
        }
    }
}