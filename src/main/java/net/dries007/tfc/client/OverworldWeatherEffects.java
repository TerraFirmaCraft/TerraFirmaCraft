/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.ParticleStatus;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.DimensionSpecialEffects;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.LightTexture;
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
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import net.dries007.tfc.common.blocks.IBlockRain;
import net.dries007.tfc.util.calendar.Calendars;
import net.dries007.tfc.util.climate.Climate;
import net.dries007.tfc.util.climate.ClimateModel;
import net.dries007.tfc.util.tracker.WeatherHelpers;

/**
 * Overrides {@link DimensionSpecialEffects.OverworldEffects} in order to provide additional features and modifications of the weather rendering.
 */
public class OverworldWeatherEffects extends DimensionSpecialEffects.OverworldEffects
{
    // Copied from the level renderer
    private final float[] rainSizeX = new float[1024];
    private final float[] rainSizeZ = new float[1024];
    private int rainSoundTime;

    {
        for (int i = 0; i < 32; i++)
            for (int j = 0; j < 32; j++)
            {
                float f = j - 16;
                float f1 = i - 16;
                float f2 = Mth.sqrt(f * f + f1 * f1);
                this.rainSizeX[i << 5 | j] = -f1 / f2;
                this.rainSizeZ[i << 5 | j] = f / f2;
            }
    }

    private static final ResourceLocation RAIN_LOCATION = ResourceLocation.withDefaultNamespace("textures/environment/rain.png");
    private static final ResourceLocation SNOW_LOCATION = ResourceLocation.withDefaultNamespace("textures/environment/snow.png");

    /**
     * See {@link LevelRenderer#renderSnowAndRain} for reference. This has a few modifications
     * <ul>
     *     <li>Uses a modified function to query for current precipitation including climate</li>
     *     <li>Renders a different amount of rain and snow based on the current intensity of the weather event</li>
     * </ul>
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
                                RenderSystem.setShaderTexture(0, RAIN_LOCATION);
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
                                * rainLevel
                                * Mth.clampedMap(rainIntensity, 0f, 0.4f, 0, 1);

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
                                RenderSystem.setShaderTexture(0, SNOW_LOCATION);
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
            final ClimateModel model = Climate.get(level);
            final long calendarTick = Calendars.get(level).getCalendarTicks();
            final float climateRain = model.getRain(calendarTick);
            final float climateRainfall = model.getRainfall(level, cameraPos);
            final float rainIntensity = WeatherHelpers.calculateRealRainIntensity(climateRain, climateRainfall);

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
}
