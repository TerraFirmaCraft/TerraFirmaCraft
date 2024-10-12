/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.client.overworld;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import com.machinezoo.noexception.throwing.ThrowingSupplier;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.MeshData;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexBuffer;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.math.Axis;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.ParticleStatus;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.DimensionSpecialEffects;
import net.minecraft.client.renderer.FogRenderer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.FogType;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import net.dries007.tfc.TerraFirmaCraft;
import net.dries007.tfc.client.ClimateRenderCache;
import net.dries007.tfc.common.blocks.IBlockRain;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.calendar.Calendars;
import net.dries007.tfc.util.climate.Climate;
import net.dries007.tfc.util.climate.ClimateModel;
import net.dries007.tfc.util.tracker.WeatherHelpers;

/**
 * Overrides {@link DimensionSpecialEffects.OverworldEffects} in order to provide additional features and modifications of the weather
 * and sky/sun/moon rendering.
 */
public class LevelRendererExtension extends DimensionSpecialEffects.OverworldEffects
{
    public static final LevelRendererExtension INSTANCE = new LevelRendererExtension();

    // Most of this is copied from LevelRenderer
    private static final ResourceLocation RAIN_0 = Helpers.identifier("textures/environment/rain_0.png");
    private static final ResourceLocation RAIN_1 = Helpers.identifier("textures/environment/rain_1.png");
    private static final ResourceLocation RAIN_2 = Helpers.identifier("textures/environment/rain_2.png");
    private static final ResourceLocation RAIN_3 = Helpers.identifier("textures/environment/rain_3.png");

    private static final ResourceLocation SNOW_0 = Helpers.identifier("textures/environment/snow_0.png");
    private static final ResourceLocation SNOW_1 = Helpers.identifier("textures/environment/snow_1.png");
    private static final ResourceLocation SNOW_2 = Helpers.identifier("textures/environment/snow_2.png");
    private static final ResourceLocation SNOW_3 = Helpers.identifier("textures/environment/snow_3.png");
    private static final ResourceLocation MOON_LOCATION = ResourceLocation.withDefaultNamespace("textures/environment/moon_phases.png");
    private static final ResourceLocation SUN_LOCATION = ResourceLocation.withDefaultNamespace("textures/environment/sun.png");

    private static VertexBuffer createBuffer(ThrowingSupplier<?> draw)
    {
        final VertexBuffer buffer = new VertexBuffer(VertexBuffer.Usage.STATIC);
        buffer.bind();
        buffer.upload(Helpers.uncheck(draw));
        VertexBuffer.unbind();
        return buffer;
    }

    private final float[] rainSizeX;
    private final float[] rainSizeZ;
    private int rainSoundTime;

    private @Nullable VertexBuffer starBuffer = null;
    private final VertexBuffer skyBuffer;
    private final VertexBuffer darkBuffer;

    private LevelRendererExtension()
    {
        // Reflect into LevelRenderer to avoid copying (both at runtime, and code)
        final Field rainSizeX = Helpers.uncheck(() -> LevelRenderer.class.getDeclaredField("rainSizeX"));
        final Field rainSizeZ = Helpers.uncheck(() -> LevelRenderer.class.getDeclaredField("rainSizeZ"));
        final Method buildSkyDisc = Helpers.uncheck(() -> LevelRenderer.class.getDeclaredMethod("buildSkyDisc", Tesselator.class, float.class));

        rainSizeX.setAccessible(true);
        rainSizeZ.setAccessible(true);
        buildSkyDisc.setAccessible(true);

        final LevelRenderer instance = Minecraft.getInstance().levelRenderer;

        this.rainSizeX = Helpers.uncheck(() -> rainSizeX.get(instance));
        this.rainSizeZ = Helpers.uncheck(() -> rainSizeZ.get(instance));

        this.skyBuffer = createBuffer(() -> buildSkyDisc.invoke(null, Tesselator.getInstance(), 16.0F));
        this.darkBuffer = createBuffer(() -> buildSkyDisc.invoke(null, Tesselator.getInstance(), -16.0F));
    }

    public void updateStars(List<Star> stars)
    {
        if (starBuffer != null) starBuffer.close();
        starBuffer = new VertexBuffer(VertexBuffer.Usage.STATIC);
        starBuffer.bind();
        starBuffer.upload(drawStars(stars));
        VertexBuffer.unbind();
    }

    private MeshData drawStars(List<Star> stars)
    {
        final Tesselator tesselator = Tesselator.getInstance();
        final BufferBuilder buffer = tesselator.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
        final RandomSource random = RandomSource.create(42L);
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        for (Star star : stars) drawStar(buffer, star, random);
        return buffer.buildOrThrow();
    }

    private void drawStar(BufferBuilder buffer, Star star, RandomSource random)
    {
        final float zenith = star.zenith();
        final float azimuth = star.azimuth();
        final int color = 0xff000000 | star.color();
        final float size = Mth.clampedMap(star.apparentMagnitude(), -1, 6, 0.4f, 0.1f);

        final float sinZenith = Mth.sin(zenith);
        final float x = 100f * sinZenith * Mth.cos(azimuth);
        final float z = 100f * sinZenith * Mth.sin(azimuth);
        final float y = 100f * Mth.cos(zenith);

        final Vector3f pos = new Vector3f(x, y, z);
        final Quaternionf q = new Quaternionf()
            .rotateTo(new Vector3f(0.0F, 0.0F, -1.0F), pos)
            .rotateZ(random.nextFloat() * Mth.TWO_PI);

        buffer.addVertex(pos.add(new Vector3f(size, -size, 0).rotate(q))).setColor(color);
        buffer.addVertex(pos.add(new Vector3f(size, size, 0).rotate(q))).setColor(color);
        buffer.addVertex(pos.add(new Vector3f(-size, size, 0).rotate(q))).setColor(color);
        buffer.addVertex(pos.add(new Vector3f(-size, -size, 0).rotate(q))).setColor(color);
    }

    /**
     * See {@link LevelRenderer#renderSky} for reference. This implementation has a few modifications:
     * <ul>
     *     <li>We don't implement End-dimension rendering, as this is only used for the overworld</li>
     * </ul>
     *
     * @return {@code true} to prevent vanilla sky rendering
     */
    @Override
    public boolean renderSky(ClientLevel level, int ticks, float partialTick, Matrix4f frustumMatrix, Camera camera, Matrix4f projectionMatrix, boolean isFoggy, Runnable skyFogSetup)
    {
        skyFogSetup.run();
        if (!isFoggy)
        {
            final FogType fogType = camera.getFluidInCamera();
            if (fogType != FogType.POWDER_SNOW && fogType != FogType.LAVA && !this.doesMobEffectBlockSky(camera))
            {
                // This override is only implemented for the overworld, so we skip the possible end dimension rendering path
                //
                // First, we query the sun position. This determines most of the rest of the other rendering as per the sun
                // visibility in the sky. We have to rewrite anything that currently uses daytime internally, as it will be wrong

                final PoseStack stack = new PoseStack();
                final Vec3 skyColor = level.getSkyColor(Minecraft.getInstance().gameRenderer.getMainCamera().getPosition(), partialTick);
                final SkyPos sunPos = ClientSolarCalculatorBridge.getSunPosition(level, camera.getBlockPosition());

                stack.mulPose(frustumMatrix);

                FogRenderer.levelFogColor();
                final Tesselator tesselator = Tesselator.getInstance();
                RenderSystem.depthMask(false);
                RenderSystem.setShaderColor((float) skyColor.x, (float) skyColor.y, (float) skyColor.z, 1.0F);
                final ShaderInstance shader = RenderSystem.getShader();
                assert shader != null;
                skyBuffer.bind();
                skyBuffer.drawWithShader(stack.last().pose(), projectionMatrix, shader);
                VertexBuffer.unbind();
                RenderSystem.enableBlend();

                // Sunrise Colors
                // This is using corrected day time via the underlying call being redirected on client
                // todo: use an adjusted time of day here. The effect means we get day-long sunrises in the north pole in summer, which isn't
                // really realistic. we should compress this - unsure if across all time, or just across compressed areas
                final float[] sunriseColor = level.effects().getSunriseColor(level.getTimeOfDay(partialTick), partialTick);
                if (sunriseColor != null)
                {
                    RenderSystem.setShader(GameRenderer::getPositionColorShader);
                    RenderSystem.setShaderColor(1f, 1f, 1f, 1f);

                    // Position the sunrise colors based on the sun azimuth position, but fixed at the horizon
                    // The offsets + negative here are to not have to deal with the weirdness that is the default rendering code
                    final Matrix4f pose = rotateTo(stack, SkyPos.of(-Mth.HALF_PI, sunPos.azimuth() + Mth.PI));
                    final BufferBuilder buffer = tesselator.begin(VertexFormat.Mode.TRIANGLE_FAN, DefaultVertexFormat.POSITION_COLOR);
                    buffer.addVertex(pose, 0.0F, 100.0F, 0.0F).setColor(sunriseColor[0], sunriseColor[1], sunriseColor[2], sunriseColor[3]);

                    for (int i = 0; i <= 16; i++)
                    {
                        final float angle = i * Mth.TWO_PI / 16.0F;
                        final float sin = Mth.sin(angle);
                        final float cos = Mth.cos(angle);
                        buffer.addVertex(pose, sin * 120.0F, cos * 120.0F, -cos * 40.0F * sunriseColor[3])
                            .setColor(sunriseColor[0], sunriseColor[1], sunriseColor[2], 0.0F);
                    }

                    BufferUploader.drawWithShader(buffer.buildOrThrow());
                }

                RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);

                // Rain darkening, this is redirected on client to use the client-side rain level
                final float rainAlpha = 1.0F - level.getRainLevel(partialTick);
                final float starAlpha = level.getStarBrightness(partialTick);
                final float nightAlpha = starAlpha * rainAlpha;

                // The moon uses a separate shader color, that alpha's out the moon somewhat when it's during the day, just to make it
                // seem a little less prominent than in night
                final float moonAlpha = (0.2f + 0.8f * starAlpha) * rainAlpha;

                // Sun
                final Matrix4f sun = rotateTo(stack, sunPos);

                RenderSystem.setShaderColor(1f, 1f, 1f, rainAlpha);
                RenderSystem.setShader(GameRenderer::getPositionTexShader);
                RenderSystem.setShaderTexture(0, SUN_LOCATION);
                BufferBuilder buffer = tesselator.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
                buffer.addVertex(sun, -30.0F, 100.0F, -30.0F).setUv(0.0F, 0.0F);
                buffer.addVertex(sun, 30.0F, 100.0F, -30.0F).setUv(1.0F, 0.0F);
                buffer.addVertex(sun, 30.0F, 100.0F, 30.0F).setUv(1.0F, 1.0F);
                buffer.addVertex(sun, -30.0F, 100.0F, 30.0F).setUv(0.0F, 1.0F);
                BufferUploader.drawWithShader(buffer.buildOrThrow());

                // Moon
                final SkyPos moonPos = ClientSolarCalculatorBridge.getMoonPosition(level, camera.getBlockPosition());
                final int moonPhase = ClientSolarCalculatorBridge.getMoonPhase();
                final int moonU = moonPhase % 4;
                final int moonV = moonPhase / 4 % 2;
                final Matrix4f moon = rotateTo(stack, moonPos);

                RenderSystem.setShaderColor(1f, 1f, 1f, moonAlpha);
                RenderSystem.setShaderTexture(0, MOON_LOCATION);
                buffer = tesselator.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
                buffer.addVertex(moon, -20.0F, 100.0F, -20.0F).setUv((moonU + 1) / 4.0F, (moonV + 1) / 2.0F);
                buffer.addVertex(moon, 20.0F, 100.0F, -20.0F).setUv(moonU / 4.0F, (moonV + 1) / 2.0F);
                buffer.addVertex(moon, 20.0F, 100.0F, 20.0F).setUv(moonU / 4.0F, moonV / 2.0F);
                buffer.addVertex(moon, -20.0F, 100.0F, 20.0F).setUv((moonU + 1) / 4.0F, moonV / 2.0F);
                BufferUploader.drawWithShader(buffer.buildOrThrow());

                // Stars
                if (nightAlpha > 0.0F && starBuffer != null)
                {
                    final SkyPos starPos = ClientSolarCalculatorBridge.getStarPosition(level, camera.getBlockPosition());

                    RenderSystem.setShaderColor(nightAlpha, nightAlpha, nightAlpha, nightAlpha);
                    FogRenderer.setupNoFog();

                    final Matrix4f stars = rotateSkyTo(stack, starPos);
                    final ShaderInstance starShader = GameRenderer.getPositionColorShader();
                    assert starShader != null;
                    starBuffer.bind();
                    starBuffer.drawWithShader(stars, projectionMatrix, starShader);
                    VertexBuffer.unbind();
                    skyFogSetup.run();
                }

                RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
                RenderSystem.disableBlend();
                RenderSystem.defaultBlendFunc();
                RenderSystem.setShaderColor(0f, 0f, 0f, 0f);

                final double distanceAboveHorizon = camera.getEntity().getEyePosition(partialTick).y - level.getLevelData().getHorizonHeight(level);
                if (distanceAboveHorizon < 0.0)
                {
                    stack.pushPose();
                    stack.translate(0.0F, 12.0F, 0.0F);
                    darkBuffer.bind();
                    darkBuffer.drawWithShader(stack.last().pose(), projectionMatrix, shader);
                    VertexBuffer.unbind();
                    stack.popPose();
                }

                RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
                RenderSystem.depthMask(true);
            }
        }
        return true;
    }

    private Matrix4f rotateTo(PoseStack stack, SkyPos pos)
    {
        stack.pushPose();
        stack.mulPose(Axis.YP.rotation(pos.azimuth()));
        stack.mulPose(Axis.XN.rotation(pos.zenith()));
        final Matrix4f pose = stack.last().pose();
        stack.popPose();
        return pose;
    }

    private Matrix4f rotateSkyTo(PoseStack stack, SkyPos pos)
    {
        stack.pushPose();
        stack.mulPose(Axis.XN.rotation(pos.zenith()));
        stack.mulPose(Axis.YN.rotation(pos.azimuth()));
        final Matrix4f pose = stack.last().pose();
        stack.popPose();
        return pose;
    }

    /**
     * See {@link LevelRenderer#renderSnowAndRain} for reference. This has a few modifications
     * <ul>
     *     <li>Uses a modified function to query for current precipitation including climate</li>
     *     <li>Renders a different amount of rain and snow based on the current intensity of the weather event</li>
     * </ul>
     *
     * @return {@code true} to prevent vanilla rendering
     */
    @Override
    public boolean renderSnowAndRain(ClientLevel level, int ticks, float partialTick, LightTexture lightTexture, double sourceCameraX, double sourceCameraY, double sourceCameraZ)
    {
        final float rainLevel = ClimateRenderCache.INSTANCE.getRainLevel(partialTick);

        if (rainLevel > 0)
        {
            lightTexture.turnOnLightLayer();

            final float camX = (float) sourceCameraX;
            final float camY = (float) sourceCameraY;
            final float camZ = (float) sourceCameraZ;

            final int blockX = Mth.floor(camX);
            final int blockY = Mth.floor(camY);
            final int blockZ = Mth.floor(camZ);
            final Tesselator tesselator = Tesselator.getInstance();
            final BlockPos.MutableBlockPos cursor = new BlockPos.MutableBlockPos();

            cursor.set(blockX, blockY, blockZ);

            final ClimateModel model = Climate.get(level);
            final long calendarTick = Calendars.get(level).getCalendarTicks();
            final float climateRain = model.getRain(calendarTick);
            final float climateRainfall = model.getRainfall(level, cursor);
            final float rainIntensity = WeatherHelpers.calculateRealRainIntensity(climateRain, climateRainfall);
            final float currentTick = ticks + partialTick;

            BufferBuilder buffer = null;

            RenderSystem.disableCull();
            RenderSystem.enableBlend();
            RenderSystem.enableDepthTest();

            // Vanilla switches between 5 and 10 based on if fancy graphics is enabled or not. We want to gradually increase the amount
            // in more intense precipitation, starting at 5 for most storms (but also applying other effects for lighter precipitation)
            //
            // However, we still limit ourselves if the user is not using fancy graphics (boo!)
            final int blockRadius = Minecraft.useFancyGraphics()
                ? (int) Mth.map(rainIntensity, 0f, 1f, 5, 15)
                : 5;

            RenderSystem.depthMask(Minecraft.useShaderTransparency());
            RenderSystem.setShader(GameRenderer::getParticleShader);

            int stateFlag = -1;

            for (int z = blockZ - blockRadius; z <= blockZ + blockRadius; z++)
            {
                for (int x = blockX - blockRadius; x <= blockX + blockRadius; x++)
                {
                    final int rainSizeIndex = (z - blockZ + 16) * 32 + x - blockX + 16;
                    final float rainSizeX = this.rainSizeX[rainSizeIndex] * 0.5f;
                    final float rainSizeZ = this.rainSizeZ[rainSizeIndex] * 0.5f;

                    cursor.set(x, camY, z);

                    // Modified: biomes always have precipitation, we don't need to query the biome for this
                    int yHeight = level.getHeight(Heightmap.Types.MOTION_BLOCKING, x, z);
                    int minY = blockY - blockRadius;
                    int maxY = blockY + blockRadius;
                    if (minY < yHeight)
                    {
                        minY = yHeight;
                    }

                    if (maxY < yHeight)
                    {
                        maxY = yHeight;
                    }

                    final int y = Math.max(yHeight, blockY);
                    if (minY != maxY)
                    {
                        final RandomSource random = RandomSource.create(x * x * 3121L + x * 45238971L ^ z * z * 418711L + z * 13761L);
                        cursor.set(x, minY, z);

                        if (model.getTemperature(level, cursor) > 0) // If positive temperature, then raining
                        {
                            if (stateFlag != 0)
                            {
                                if (stateFlag >= 0)
                                {
                                    BufferUploader.drawWithShader(buffer.buildOrThrow());
                                }

                                stateFlag = 0;


                                // select the texture based on the amount of rain
                                if (rainIntensity >= 0.75f)
                                {
                                    RenderSystem.setShaderTexture(0, RAIN_3);
                                }
                                else if (rainIntensity >= 0.5f)
                                {
                                    RenderSystem.setShaderTexture(0, RAIN_2);
                                }
                                else if (rainIntensity >= 0.25f)
                                {
                                    RenderSystem.setShaderTexture(0, RAIN_1);
                                }
                                else
                                {
                                    RenderSystem.setShaderTexture(0, RAIN_0);
                                }

                                TerraFirmaCraft.LOGGER.debug(rainIntensity + "");

                                buffer = tesselator.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.PARTICLE);
                            }

                            // Mojang magic
                            int i3 = ticks & 131071;
                            int j3 = x * x * 3121 + x * 45238971 + z * z * 418711 + z * 13761 & 0xFF;
                            float f2 = 3.0F + random.nextFloat();
                            float f3 = -((i3 + j3) + partialTick) / 32.0F * f2;
                            float v = f3 % 32.0F;
                            double d2 = x + 0.5 - camX;
                            double d3 = z + 0.5 - camZ;

                            // Vanilla calculates the alpha based on the rain level, which is reasonable as it allows rain to fade in and out.
                            // We additionally use the rain intensity, which causes rain to be milder at low intensity rainfall
                            //
                            // Note we also fix a bug here by clamping, so alpha does not go negative, which causes random non-alpha rainfall on
                            // far-away blocks. This affects us more as we have more distant rainfall (15 vs. 10 max in vanilla)
                            float f6 = (float) Math.sqrt(d2 * d2 + d3 * d3) / blockRadius;
                            final float alpha = Mth.clamp((1.0F - f6 * f6) * 0.5F + 0.5F, 0f, 1f)
                                * rainLevel;

                            cursor.set(x, y, z);

                            final int light = LevelRenderer.getLightColor(level, cursor);

                            buffer.addVertex(x - camX - rainSizeX + 0.5f, maxY - camY, z - camZ - rainSizeZ + 0.5f)
                                .setUv(0.0F, minY * 0.25F + v)
                                .setColor(1.0F, 1.0F, 1.0F, alpha)
                                .setLight(light);
                            buffer.addVertex(x - camX + rainSizeX + 0.5f, maxY - camY, z - camZ + rainSizeZ + 0.5f)
                                .setUv(1.0F, minY * 0.25F + v)
                                .setColor(1.0F, 1.0F, 1.0F, alpha)
                                .setLight(light);
                            buffer.addVertex(x - camX + rainSizeX + 0.5f, minY - camY, z - camZ + rainSizeZ + 0.5f)
                                .setUv(1.0F, maxY * 0.25F + v)
                                .setColor(1.0F, 1.0F, 1.0F, alpha)
                                .setLight(light);
                            buffer.addVertex(x - camX - rainSizeX + 0.5f, minY - camY, z - camZ - rainSizeZ + 0.5f)
                                .setUv(0.0F, maxY * 0.25F + v)
                                .setColor(1.0F, 1.0F, 1.0F, alpha)
                                .setLight(light);
                        }
                        else
                        {
                            // Otherwise, snow
                            if (stateFlag != 1)
                            {
                                if (stateFlag >= 0)
                                {
                                    BufferUploader.drawWithShader(buffer.buildOrThrow());
                                }

                                stateFlag = 1;

                                if (rainIntensity >= 0.75f)
                                {
                                    RenderSystem.setShaderTexture(0, SNOW_3);
                                }
                                else if (rainIntensity >= 0.5f)
                                {
                                    RenderSystem.setShaderTexture(0, SNOW_2);
                                }
                                else if (rainIntensity >= 0.25f)
                                {
                                    RenderSystem.setShaderTexture(0, SNOW_1);
                                }
                                else
                                {
                                    RenderSystem.setShaderTexture(0, SNOW_3);
                                }


                                buffer = tesselator.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.PARTICLE);
                            }

                            // Mojang magic
                            float f8 = -((float) (ticks & 511) + partialTick) / 512.0F;
                            float f9 = (float) (random.nextDouble() + currentTick * 0.01 * random.nextGaussian());
                            float f10 = (float) (random.nextDouble() + (currentTick * random.nextGaussian()) * 0.001);
                            double d4 = (double) x + 0.5 - camX;
                            double d5 = (double) z + 0.5 - camZ;
                            float f11 = (float) Math.sqrt(d4 * d4 + d5 * d5) / (float) blockRadius;
                            final float alpha = ((1.0F - f11 * f11) * 0.3F + 0.5F) * rainLevel;
                            cursor.set(x, y, z);

                            // A hack from Mojang to make snow not turn dark in low-light environments
                            final int light = LevelRenderer.getLightColor(level, cursor);
                            final int lightV = light >> 16 & 65535;
                            final int lightU = light & 65535;
                            final int brightLightV = (lightV * 3 + 240) / 4;
                            final int brightLightU = (lightU * 3 + 240) / 4;

                            buffer.addVertex(x - camX - rainSizeX + 0.5f, maxY - camY, z - camZ - rainSizeZ + 0.5f)
                                .setUv(0.0F + f9, minY * 0.25F + f8 + f10)
                                .setColor(1.0F, 1.0F, 1.0F, alpha)
                                .setUv2(brightLightU, brightLightV);
                            buffer.addVertex(x - camX + rainSizeX + 0.5f, maxY - camY, z - camZ + rainSizeZ + 0.5f)
                                .setUv(1.0F + f9, minY * 0.25F + f8 + f10)
                                .setColor(1.0F, 1.0F, 1.0F, alpha)
                                .setUv2(brightLightU, brightLightV);
                            buffer.addVertex(x - camX + rainSizeX + 0.5f, minY - camY, z - camZ + rainSizeZ + 0.5f)
                                .setUv(1.0F + f9, maxY * 0.25F + f8 + f10)
                                .setColor(1.0F, 1.0F, 1.0F, alpha)
                                .setUv2(brightLightU, brightLightV);
                            buffer.addVertex(x - camX - rainSizeX + 0.5f, minY - camY, z - camZ - rainSizeZ + 0.5f)
                                .setUv(0.0F + f9, maxY * 0.25F + f8 + f10)
                                .setColor(1.0F, 1.0F, 1.0F, alpha)
                                .setUv2(brightLightU, brightLightV);
                        }
                    }
                }
            }

            if (stateFlag >= 0)
            {
                BufferUploader.drawWithShader(buffer.buildOrThrow());
            }

            RenderSystem.enableCull();
            RenderSystem.disableBlend();
            lightTexture.turnOffLightLayer();
        }
        return true;
    }

    /**
     * See {@link LevelRenderer#tickRain(Camera)} for reference. This has a few modifications:
     * <ul>
     *     <li>Uses a modified function to query for current precipitation including climate</li>
     *     <li>Makes rain sounds quieter and reduce particles in less intense rainfall</li>
     * </ul>
     *
     * @return {@code true} to prevent vanilla rain ticking
     */
    @Override
    public boolean tickRain(ClientLevel level, int ticks, Camera camera)
    {
        final Minecraft minecraft = Minecraft.getInstance();
        final float rainLevel = ClimateRenderCache.INSTANCE.getRainLevel(0f);
        if (rainLevel > 0.0F)
        {
            final RandomSource random = RandomSource.create(ticks * 312987231L);
            final BlockPos cameraPos = BlockPos.containing(camera.getPosition());
            final BlockPos.MutableBlockPos cursor = new BlockPos.MutableBlockPos();

            // Calculate rainfall via the climate
            final float rainIntensity = calculateRainIntensity(level, cameraPos);

            // Include all factors that vanilla does (fancy graphics, particle settings, rain level), but also include rain intensity
            final float adjustedRainIntensity = rainLevel
                * Mth.clampedMap(rainIntensity, 0, 0.3f, 0, 1)
                * (Minecraft.useFancyGraphics() ? 1f : 0.5f)
                * (minecraft.options.particles().get() == ParticleStatus.DECREASED ? 0.7f : 1f);
            final int particleAmount = (int) (100f * adjustedRainIntensity * adjustedRainIntensity);

            // Modification from vanilla, since we are using a cursor, we track if we added any particles rather than checking
            // if the position is not null, so we can make sounds for them
            boolean addedAnyParticles = false;

            for (int n = 0; n < particleAmount; n++)
            {
                final int dx = random.nextInt(21) - 10;
                final int dz = random.nextInt(21) - 10;

                cursor.setWithOffset(cameraPos, dx, 0, dz);
                cursor.setY(level.getHeight(Heightmap.Types.MOTION_BLOCKING, cursor.getX(), cursor.getZ()));

                if (cursor.getY() > level.getMinBuildHeight() && cursor.getY() <= cameraPos.getY() + 10 && cursor.getY() >= cameraPos.getY() - 10)
                {
                    cursor.move(Direction.DOWN);
                    addedAnyParticles = true;

                    // Don't check the biome, as all overworld biomes should support rain, and we checked that above

                    // With minimal particle options, we still search for a position (as we use it to determine if we need to play rain sounds), but we
                    // stop at the first one, before spawning any particles for it.
                    if (minecraft.options.particles().get() == ParticleStatus.MINIMAL)
                    {
                        break;
                    }

                    final double offsetX = random.nextDouble();
                    final double offsetZ = random.nextDouble();
                    final BlockState state = level.getBlockState(cursor);
                    final FluidState fluid = level.getFluidState(cursor);

                    // Handle `IBlockRain`, which needs to pretend the block is a solid block.
                    final VoxelShape shape = state.getBlock() instanceof IBlockRain
                        ? Shapes.block()
                        : state.getCollisionShape(level, cursor);
                    final double offsetY = Math.max(
                        shape.max(Direction.Axis.Y, offsetX, offsetZ),
                        fluid.getHeight(level, cursor)
                    );

                    final ParticleOptions options = !fluid.is(FluidTags.LAVA)
                        && !state.is(Blocks.MAGMA_BLOCK)
                        && !CampfireBlock.isLitCampfire(state)
                        ? ParticleTypes.RAIN
                        : ParticleTypes.SMOKE;
                    level.addParticle(options, cursor.getX() + offsetX, cursor.getY() + offsetY, cursor.getZ() + offsetZ, 0.0, 0.0, 0.0);
                }
            }

            if (addedAnyParticles && random.nextInt(3) < rainSoundTime++)
            {
                rainSoundTime = 0;

                // Modification, we use the adjusted rain intensity to control the volume here. Default to the max being the same volume as vanilla, but
                // with the minimum being much quieter.
                final float adjustedRainSoundIntensity = rainLevel * Mth.clampedMap(rainIntensity, 0, 0.4f, 0, 1);

                if (cursor.getY() > cameraPos.getY() + 1 && level.getHeight(Heightmap.Types.MOTION_BLOCKING, cameraPos.getX(), cameraPos.getZ()) > Mth.floor(cameraPos.getY()))
                {
                    level.playLocalSound(cursor, SoundEvents.WEATHER_RAIN_ABOVE, SoundSource.WEATHER, adjustedRainSoundIntensity * 0.1f, 0.5f, false);
                }
                else
                {
                    level.playLocalSound(cursor, SoundEvents.WEATHER_RAIN, SoundSource.WEATHER, adjustedRainSoundIntensity * 0.2f, 1.0f, false);
                }
            }
        }

        return true;
    }

    private float calculateRainIntensity(Level level, BlockPos cameraPos)
    {
        final ClimateModel model = Climate.get(level);
        final long calendarTick = Calendars.get(level).getCalendarTicks();
        final float climateRain = model.getRain(calendarTick);
        final float climateRainfall = model.getRainfall(level, cameraPos);
        return WeatherHelpers.calculateRealRainIntensity(climateRain, climateRainfall);
    }

    private boolean doesMobEffectBlockSky(Camera camera)
    {
        return camera.getEntity() instanceof LivingEntity entity
            && (entity.hasEffect(MobEffects.BLINDNESS) || entity.hasEffect(MobEffects.DARKNESS));
    }
}
