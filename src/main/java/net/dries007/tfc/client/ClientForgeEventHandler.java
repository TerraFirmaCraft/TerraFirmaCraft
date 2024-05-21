/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.client;

import java.lang.reflect.Field;
import java.util.List;
import java.util.stream.Collectors;
import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.toasts.TutorialToast;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.FogRenderer;
import net.minecraft.client.resources.sounds.AmbientSoundHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.material.FogType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.client.event.CustomizeGuiOverlayEvent;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.client.event.RenderHandEvent;
import net.minecraftforge.client.event.RenderHighlightEvent;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.client.event.ToastAddEvent;
import net.minecraftforge.client.event.ViewportEvent;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.server.ServerLifecycleHooks;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.TerraFirmaCraft;
import net.dries007.tfc.client.particle.TFCParticles;
import net.dries007.tfc.client.screen.button.PlayerInventoryTabButton;
import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.blockentities.SluiceBlockEntity;
import net.dries007.tfc.common.blocks.devices.SluiceBlock;
import net.dries007.tfc.common.blocks.rock.RockCategory;
import net.dries007.tfc.common.capabilities.egg.EggCapability;
import net.dries007.tfc.common.capabilities.egg.IEgg;
import net.dries007.tfc.common.capabilities.food.FoodCapability;
import net.dries007.tfc.common.capabilities.forge.ForgingBonus;
import net.dries007.tfc.common.capabilities.forge.ForgingCapability;
import net.dries007.tfc.common.capabilities.glass.GlassWorkData;
import net.dries007.tfc.common.capabilities.heat.HeatCapability;
import net.dries007.tfc.common.capabilities.heat.IHeat;
import net.dries007.tfc.common.capabilities.size.ItemSizeManager;
import net.dries007.tfc.common.items.EmptyPanItem;
import net.dries007.tfc.common.items.PanItem;
import net.dries007.tfc.common.recipes.ChiselRecipe;
import net.dries007.tfc.common.recipes.HeatingRecipe;
import net.dries007.tfc.common.recipes.inventory.ItemStackInventory;
import net.dries007.tfc.compat.patchouli.PatchouliIntegration;
import net.dries007.tfc.config.TFCConfig;
import net.dries007.tfc.mixin.client.accessor.LocalPlayerAccessor;
import net.dries007.tfc.network.CycleChiselModePacket;
import net.dries007.tfc.network.PacketHandler;
import net.dries007.tfc.network.PlaceBlockSpecialPacket;
import net.dries007.tfc.network.RequestClimateModelPacket;
import net.dries007.tfc.network.StackFoodPacket;
import net.dries007.tfc.network.SwitchInventoryTabPacket;
import net.dries007.tfc.util.Fertilizer;
import net.dries007.tfc.util.Fuel;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.Metal;
import net.dries007.tfc.util.Pannable;
import net.dries007.tfc.util.PhysicalDamageType;
import net.dries007.tfc.util.Sluiceable;
import net.dries007.tfc.util.calendar.Calendars;
import net.dries007.tfc.util.climate.Climate;
import net.dries007.tfc.util.tracker.WorldTracker;
import net.dries007.tfc.world.ChunkGeneratorExtension;
import net.dries007.tfc.world.chunkdata.ChunkData;

import static net.minecraft.ChatFormatting.*;

public class ClientForgeEventHandler
{
    private static final Field CAP_NBT_FIELD = Helpers.uncheck(() -> {
        final Field field = ItemStack.class.getDeclaredField("capNBT");
        field.setAccessible(true);
        return field;
    });

    private static float waterFogLevel = 1f;

    public static void init()
    {
        final IEventBus bus = MinecraftForge.EVENT_BUS;

        bus.addListener(ClientForgeEventHandler::onRenderGameOverlayText);
        bus.addListener(ClientForgeEventHandler::onRenderGameOverlayPost);
        bus.addListener(ClientForgeEventHandler::onItemTooltip);
        bus.addListener(ClientForgeEventHandler::onInitGuiPost);
        bus.addListener(ClientForgeEventHandler::onClientPlayerLoggedIn);
        bus.addListener(ClientForgeEventHandler::onClientPlayerLoggedOut);
        bus.addListener(ClientForgeEventHandler::onClientTick);
        bus.addListener(ClientForgeEventHandler::onKeyEvent);
        bus.addListener(ClientForgeEventHandler::onScreenKey);
        bus.addListener(ClientForgeEventHandler::onHighlightBlockEvent);
        bus.addListener(ClientForgeEventHandler::onFogRender);
        bus.addListener(ClientForgeEventHandler::onHandRender);
        bus.addListener(ClientForgeEventHandler::onToast);
        bus.addListener(ClientForgeEventHandler::onEffectRender);
        bus.addListener(IngameOverlays::checkGuiOverlays);
    }

    public static void onRenderGameOverlayText(CustomizeGuiOverlayEvent.DebugText event)
    {
        final Minecraft mc = Minecraft.getInstance();
        if (mc.level != null && mc.options.renderDebug && TFCConfig.CLIENT.enableDebug.get())
        {
            final Entity camera = mc.getCameraEntity();
            assert camera != null;
            final BlockPos pos = BlockPos.containing(camera.getX(), camera.getBoundingBox().minY, camera.getZ());
            if (mc.level.hasChunk(pos.getX() >> 4, pos.getZ() >> 4))
            {
                final List<String> tooltip = event.getLeft();

                tooltip.add("");
                tooltip.add(AQUA + TerraFirmaCraft.MOD_NAME);
                tooltip.add(Component.translatable("tfc.tooltip.calendar_date", Calendars.CLIENT.getCalendarTimeAndDate()).getString());
                tooltip.add("Avg: %.1f Actual: %.1f Rain: %.1f".formatted(
                    ClimateRenderCache.INSTANCE.getAverageTemperature(),
                    ClimateRenderCache.INSTANCE.getTemperature(),
                    ClimateRenderCache.INSTANCE.getRainfall()
                ));
                final Vec2 wind = ClimateRenderCache.INSTANCE.getWind();
                tooltip.add(Component.translatable("tfc.tooltip.wind_speed",
                    Mth.floor(320 * wind.length()),
                    String.format("%.0f", Mth.abs(wind.x * 100)),
                    Helpers.translateEnum(wind.x > 0 ? Direction.EAST : Direction.WEST),
                    String.format("%.0f", Mth.abs(wind.y * 100)),
                    Helpers.translateEnum(wind.y > 0 ? Direction.SOUTH : Direction.NORTH))
                    .getString());
                tooltip.add("Tick: %d Calendar: %d Day: %d".formatted(Calendars.CLIENT.getTicks(), Calendars.CLIENT.getCalendarTicks(), camera.level().getDayTime()));

                final ChunkData data = ChunkData.get(mc.level, pos);
                if (data.status() == ChunkData.Status.CLIENT)
                {
                    tooltip.add("F: %s Density: %.1f Weird: %.1f".formatted(data.getForestType().getSerializedName(), data.getForestDensity(), data.getForestWeirdness()));
                }
                else
                {
                    tooltip.add("[Waiting for chunk data]");
                }

                WorldTracker.get(mc.level).addDebugTooltip(tooltip);

                final MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
                if (server != null && server.overworld().getChunkSource().getGenerator() instanceof ChunkGeneratorExtension ex)
                {
                    final int approxSurfaceY = mc.level.getHeight(Heightmap.Types.MOTION_BLOCKING, pos.getX(), pos.getZ());
                    ex.chunkDataProvider().generator().displayDebugInfo(tooltip, pos, approxSurfaceY);
                }
            }
        }
    }

    /**
     * Render overlays for looking at particular block / item combinations
     */
    public static void onRenderGameOverlayPost(RenderGuiOverlayEvent.Post event)
    {
        // todo this should probably be a forge ingame gui
        final GuiGraphics stack = event.getGuiGraphics();
        final Minecraft minecraft = Minecraft.getInstance();
        final Player player = minecraft.player;
        if (player != null)
        {
            final boolean holdingHoe = Helpers.isItem(player.getMainHandItem().getItem(), TFCTags.Items.HOES) || Helpers.isItem(player.getOffhandItem().getItem(), TFCTags.Items.HOES);
            if (event.getOverlay() == VanillaGuiOverlay.CROSSHAIR.type() && minecraft.screen == null && holdingHoe && (!TFCConfig.CLIENT.showHoeOverlaysOnlyWhenShifting.get() || player.isShiftKeyDown()))
            {
                HoeOverlays.render(minecraft, event.getWindow(), stack);
            }
        }
    }

    @SuppressWarnings("ConstantConditions")
    public static void onItemTooltip(ItemTooltipEvent event)
    {
        final ItemStack stack = event.getItemStack();
        final List<Component> text = event.getToolTip();
        if (!stack.isEmpty())
        {
            // These are ordered in a predictable fashion
            // 1. Common information, that is important to know about the item stack itself (such as size, food, heat, etc.). Static (unchanging) information is ordered before dynamic (changing) information.
            // 2. Extra information, that is useful QoL info, but not necessary (such as possible recipes, melting into, etc.)
            // 3. Debug information, that is only available in debug mode.

            if (Helpers.isItem(stack, TFCTags.Items.SILICA_ITEMS))
            {
                text.add(Component.translatable("tfc.tooltip.glass.silica").withStyle(AQUA, ITALIC));
            }
            else if (Helpers.isItem(stack, TFCTags.Items.HEMATITIC_ITEMS))
            {
                text.add(Component.translatable("tfc.tooltip.glass.hematitic").withStyle(RED, ITALIC));
            }
            else if (Helpers.isItem(stack, TFCTags.Items.OLIVINE_ITEMS))
            {
                text.add(Component.translatable("tfc.tooltip.glass.olivine").withStyle(GREEN, ITALIC));
            }
            else if (Helpers.isItem(stack, TFCTags.Items.VOLCANIC_ITEMS))
            {
                text.add(Component.translatable("tfc.tooltip.glass.volcanic").withStyle(DARK_PURPLE, ITALIC));
            }
            else if (Helpers.isItem(stack, TFCTags.Items.SEDIMENTARY_ITEMS))
            {
                text.add(Helpers.translateEnum(RockCategory.SEDIMENTARY).withStyle(GRAY, ITALIC));
            }
            else if (Helpers.isItem(stack, TFCTags.Items.METAMORPHIC_ITEMS))
            {
                text.add(Helpers.translateEnum(RockCategory.METAMORPHIC).withStyle(GRAY, ITALIC));
            }
            else if (Helpers.isItem(stack, TFCTags.Items.IGNEOUS_EXTRUSIVE_ITEMS))
            {
                text.add(Helpers.translateEnum(RockCategory.IGNEOUS_EXTRUSIVE).withStyle(GRAY, ITALIC));
            }
            else if (Helpers.isItem(stack, TFCTags.Items.IGNEOUS_INTRUSIVE_ITEMS))
            {
                text.add(Helpers.translateEnum(RockCategory.IGNEOUS_INTRUSIVE).withStyle(GRAY, ITALIC));
            }
            else if (Helpers.isItem(stack, TFCTags.Items.UNSEALED_JARS))
            {
                text.add(Component.translatable("tfc.tooltip.unsealed").withStyle(GRAY, ITALIC));
            }
            else if (Helpers.isItem(stack, TFCTags.Items.SEALED_JARS))
            {
                text.add(Component.translatable("tfc.tooltip.sealed").withStyle(GRAY, ITALIC));
            }

            ItemSizeManager.addTooltipInfo(stack, text);
            PhysicalDamageType.addTooltipInfo(stack, text);
            ForgingBonus.addTooltipInfo(stack, text);
            ForgingCapability.addTooltipInfo(stack, text);
            GlassWorkData.addTooltipInfo(stack, text);
            FoodCapability.addTooltipInfo(stack, text);

            final @Nullable IHeat heat = HeatCapability.get(stack);
            if (heat != null)
            {
                heat.addTooltipInfo(stack, text);
            }

            final @Nullable IEgg egg = EggCapability.get(stack);
            if (egg != null)
            {
                egg.addTooltipInfo(text);
            }

            // Fuel information
            final Fuel fuel = Fuel.get(stack);
            if (fuel != null)
            {
                final MutableComponent heatTooltip = TFCConfig.CLIENT.heatTooltipStyle.get().formatColored(fuel.getTemperature());
                if (heatTooltip != null)
                {
                    // burns at %s for %s
                    text.add(Component.translatable("tfc.tooltip.fuel_burns_at", heatTooltip, Calendars.CLIENT.getTimeDelta(fuel.getDuration())));
                }
            }

            final Fertilizer fertilizer = Fertilizer.get(stack);
            if (fertilizer != null)
            {
                final float n = fertilizer.getNitrogen(), p = fertilizer.getPhosphorus(), k = fertilizer.getPotassium();
                if (n != 0)
                    text.add(Component.translatable("tfc.tooltip.fertilizer.nitrogen", String.format("%.1f", n * 100)));
                if (p != 0)
                    text.add(Component.translatable("tfc.tooltip.fertilizer.phosphorus", String.format("%.1f", p * 100)));
                if (k != 0)
                    text.add(Component.translatable("tfc.tooltip.fertilizer.potassium", String.format("%.1f", k * 100)));
            }

            // Metal content, inferred from a matching heat recipe.
            final ItemStackInventory inventory = new ItemStackInventory(stack);
            final HeatingRecipe recipe = HeatingRecipe.getRecipe(inventory);
            if (recipe != null)
            {
                // Check what we would get if melted
                final FluidStack fluid = recipe.assembleFluid(inventory);
                if (!fluid.isEmpty())
                {
                    final Metal metal = Metal.get(fluid.getFluid());
                    if (metal != null)
                    {
                        final MutableComponent heatTooltip = TFCConfig.CLIENT.heatTooltipStyle.get().formatColored(recipe.getTemperature());
                        if (heatTooltip != null)
                        {
                            // %s mB of %s (at %s)
                            text.add(Component.translatable("tfc.tooltip.item_melts_into", fluid.getAmount() * stack.getCount(), Component.translatable(metal.getTranslationKey()), heatTooltip));
                        }
                    }
                }
            }

            final boolean sluice = Sluiceable.get(stack) != null;
            final boolean pan = stack.getItem() instanceof BlockItem bi && Pannable.get(bi.getBlock().defaultBlockState()) != null;
            if (sluice && !pan)
            {
                text.add(Component.translatable("tfc.tooltip.usable_in_sluice").withStyle(GRAY));
            }
            else if (pan && !sluice)
            {
                text.add(Component.translatable("tfc.tooltip.usable_in_pan").withStyle(GRAY));
            }
            else if (pan && sluice)
            {
                text.add(Component.translatable("tfc.tooltip.usable_in_sluice_and_pan").withStyle(GRAY));
            }

            if (TFCConfig.CLIENT.enableDebug.get() && event.getFlags().isAdvanced())
            {
                final CompoundTag stackTag = stack.getTag();
                if (stackTag != null)
                {
                    text.add(Component.literal(DARK_GRAY + "[Debug] NBT: " + stackTag));
                }

                final CompoundTag capTag = Helpers.uncheck(() -> CAP_NBT_FIELD.get(stack));
                if (capTag != null && !capTag.isEmpty())
                {
                    text.add(Component.literal(DARK_GRAY + "[Debug] Cap NBT: " + capTag));
                }

                text.add(Component.literal(DARK_GRAY + "[Debug] Item Tags: " + Helpers.getHolder(ForgeRegistries.ITEMS, stack.getItem()).tags().map(t1 -> "#" + t1.location()).collect(Collectors.joining(", "))));

                if (stack.getItem() instanceof BlockItem blockItem)
                {
                    final Block block = blockItem.getBlock();
                    text.add(Component.literal(DARK_GRAY + "[Debug] Block Tags: " + Helpers.getHolder(ForgeRegistries.BLOCKS, block).tags().map(t -> "#" + t.location()).collect(Collectors.joining(", "))));
                }
            }
        }
    }

    public static void onInitGuiPost(ScreenEvent.Init.Post event)
    {
        Player player = Minecraft.getInstance().player;
        if (event.getScreen() instanceof InventoryScreen screen && player != null && !player.isCreative())
        {
            int guiLeft = screen.getGuiLeft();
            int guiTop = screen.getGuiTop();

            event.addListener(new PlayerInventoryTabButton(guiLeft, guiTop, 176 - 3, 4, 20 + 3, 22, 128 + 20, 0, 1, 3, 0, 0, button -> {}).setRecipeBookCallback(screen));
            event.addListener(new PlayerInventoryTabButton(guiLeft, guiTop, 176, 27, 20, 22, 128, 0, 1, 3, 32, 0, SwitchInventoryTabPacket.Type.CALENDAR).setRecipeBookCallback(screen));
            event.addListener(new PlayerInventoryTabButton(guiLeft, guiTop, 176, 50, 20, 22, 128, 0, 1, 3, 64, 0, SwitchInventoryTabPacket.Type.NUTRITION).setRecipeBookCallback(screen));
            event.addListener(new PlayerInventoryTabButton(guiLeft, guiTop, 176, 73, 20, 22, 128, 0, 1, 3, 96, 0, SwitchInventoryTabPacket.Type.CLIMATE).setRecipeBookCallback(screen));
            PatchouliIntegration.ifEnabled(() -> event.addListener(new PlayerInventoryTabButton(guiLeft, guiTop, 176, 96, 20, 22, 128, 0, 1, 3, 0, 32, SwitchInventoryTabPacket.Type.BOOK).setRecipeBookCallback(screen)));
        }
    }

    public static void onClientPlayerLoggedIn(ClientPlayerNetworkEvent.LoggingIn event)
    {
        // We can't send this on client world load, it's too early, as the connection is not setup yet
        // This is the closest point after that which will work
        PacketHandler.send(PacketDistributor.SERVER.noArg(), new RequestClimateModelPacket());

        LocalPlayer player = event.getPlayer();
        List<AmbientSoundHandler> handlers = ((LocalPlayerAccessor) player).accessor$getAmbientSoundHandlers();
        if (handlers.stream().noneMatch(handler -> handler instanceof TFCBubbleColumnAmbientSoundHandler))
        {
            handlers.add(new TFCBubbleColumnAmbientSoundHandler(player));
        }
    }

    public static void onClientPlayerLoggedOut(ClientPlayerNetworkEvent.LoggingOut event)
    {
        Calendars.CLIENT.resetToDefault();
    }

    public static void onClientTick(TickEvent.ClientTickEvent event)
    {
        Level world = Minecraft.getInstance().level;
        if (event.phase == TickEvent.Phase.END && world != null && !Minecraft.getInstance().isPaused())
        {
            Calendars.CLIENT.onClientTick();
            ClimateRenderCache.INSTANCE.onClientTick();
            tickWind();
        }
    }

    private static void tickWind()
    {
        if (!TFCConfig.CLIENT.enableWindParticles.get())
            return;
        final Level level = ClientHelpers.getLevel();
        final Player player = ClientHelpers.getPlayer();
        if (player != null && level != null && level.getGameTime() % 2 == 0)
        {
            final BlockPos pos = player.blockPosition();
            final Vec2 wind = ClimateRenderCache.INSTANCE.getWind();
            final float windStrength = wind.length();
            int count = 0;
            if (windStrength > 0.3f)
            {
                count = (int) (windStrength * 8);
            }
            else if (player.getVehicle() instanceof Boat)
            {
                count = 2; // always show if in a boat
            }
            if (count == 0)
                return;
            final double xBias = wind.x > 0 ? 6 : -6;
            final double zBias = wind.y > 0 ? 6 : -6;
            final ParticleOptions particle = ClimateRenderCache.INSTANCE.getTemperature() < 0f && level.getRainLevel(0) > 0 ? TFCParticles.SNOWFLAKE.get() : TFCParticles.WIND.get();
            for (int i = 0; i < count; i++)
            {
                final double x = pos.getX() + Mth.nextDouble(level.random, -12 - xBias, 12 - xBias);
                final double y = pos.getY() + Mth.nextDouble(level.random, -1, 6);
                final double z = pos.getZ() + Mth.nextDouble(level.random, -12 - zBias, 12 - zBias);
                if (level.canSeeSky(BlockPos.containing(x, y, z)))
                {
                    level.addParticle(particle, x, y, z, 0D, 0D, 0D);
                }
            }
        }
    }

    public static void onKeyEvent(InputEvent.Key event)
    {
        if (TFCKeyBindings.PLACE_BLOCK.isDown())
        {
            PacketHandler.send(PacketDistributor.SERVER.noArg(), new PlaceBlockSpecialPacket());
        }
        else if (TFCKeyBindings.CYCLE_CHISEL_MODE.isDown())
        {
            PacketHandler.send(PacketDistributor.SERVER.noArg(), new CycleChiselModePacket());
        }
    }

    public static void onScreenKey(ScreenEvent.KeyPressed.Pre event)
    {
        if (TFCKeyBindings.STACK_FOOD.isActiveAndMatches(InputConstants.getKey(event.getKeyCode(), event.getScanCode())) && event.getScreen() instanceof InventoryScreen inv)
        {
            Slot slot = inv.getSlotUnderMouse();
            if (slot != null)
            {
                PacketHandler.send(PacketDistributor.SERVER.noArg(), new StackFoodPacket(slot.index));
            }
        }
    }

    /**
     * Handles custom bounding boxes drawing
     * eg: Chisel, Quern handle
     */
    public static void onHighlightBlockEvent(RenderHighlightEvent.Block event)
    {
        final Camera camera = event.getCamera();
        final PoseStack poseStack = event.getPoseStack();
        final Entity entity = camera.getEntity();
        final Level level = entity.level();
        final BlockHitResult hit = event.getTarget();
        final BlockPos pos = hit.getBlockPos();

        if (entity instanceof Player player)
        {
            final BlockState stateAt = level.getBlockState(pos);
            final Block blockAt = stateAt.getBlock();

            ChiselRecipe.computeResult(player, stateAt, hit, false).ifLeft(chiseled -> {
                IHighlightHandler.drawBox(poseStack, chiseled.getShape(level, pos), event.getMultiBufferSource(), pos, camera.getPosition(), 1f, 0f, 0f, 0.4f);
                event.setCanceled(true);
            });

            if (blockAt instanceof IHighlightHandler handler)
            {
                // Pass on to custom implementations
                if (handler.drawHighlight(level, pos, player, hit, poseStack, event.getMultiBufferSource(), camera.getPosition()))
                {
                    // Cancel drawing this block's bounding box
                    event.setCanceled(true);
                }
            }
            else if (blockAt instanceof IGhostBlockHandler handler)
            {
                if (handler.draw(level, player, stateAt, pos, hit.getLocation(), hit.getDirection(), event.getPoseStack(), event.getMultiBufferSource(), player.getMainHandItem()))
                {
                    event.setCanceled(true);
                }
            }
            else if (blockAt instanceof SluiceBlock && level.getBlockEntity(pos) instanceof SluiceBlockEntity sluice)
            {
                BlockPos waterPos = sluice.getWaterOutputPos();
                if (!stateAt.getValue(SluiceBlock.UPPER))
                {
                    waterPos = waterPos.relative(stateAt.getValue(SluiceBlock.FACING).getOpposite());
                }
                if (!level.getBlockState(waterPos).canBeReplaced())
                {
                    IHighlightHandler.drawBox(poseStack, Shapes.block(), event.getMultiBufferSource(), waterPos, camera.getPosition(), 0f, 0f, 1f, 0.4f);
                }

                final BlockPos posAbove = pos.above();
                final BlockState stateAbove = level.getBlockState(posAbove);
                if (!stateAbove.getFluidState().isEmpty())
                {
                    IHighlightHandler.drawBox(poseStack, stateAbove.getFluidState().getShape(level, posAbove), event.getMultiBufferSource(), posAbove, camera.getPosition(), 1f, 0f, 0f, 0.4f);
                }
            }
        }
    }

    public static void onFogRender(ViewportEvent.RenderFog event)
    {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level != null && event.getMode() == FogRenderer.FogMode.FOG_TERRAIN)
        {
            final FogType fluid = event.getCamera().getFluidInCamera();
            final BlockPos pos = event.getCamera().getBlockPosition();
            if (fluid == FogType.NONE)
            {
                final float fog = Climate.getFogginess(mc.level, pos);
                if (fog != 0)
                {
                    final float renderDistance = mc.gameRenderer.getRenderDistance();
                    final float density = renderDistance * (1 - Math.min(0.86f, fog));

                    event.setNearPlaneDistance(density - Mth.clamp(renderDistance / 10.0F, 4.0F, 64.0F));
                    event.setFarPlaneDistance(density);
                    event.setCanceled(true);
                }
            }
            else if (fluid == FogType.WATER)
            {
                final Player player = mc.player;
                final float fog = Climate.getWaterFogginess(mc.level, pos);
                if (fog != 1f)
                {
                    waterFogLevel = player != null && player.hasEffect(MobEffects.NIGHT_VISION) ? 1f : Mth.lerp(0.01f, waterFogLevel, fog);
                    event.scaleFarPlaneDistance(waterFogLevel);
                    event.setCanceled(true);
                }
                else
                {
                    waterFogLevel = 1f;
                }
            }

        }
    }

    /**
     * Vanilla will first make a decision about which hands to render, then optionally render each one. (In {@link net.minecraft.client.renderer.ItemInHandRenderer#evaluateWhichHandsToRender(LocalPlayer)})
     * We have to intercept both hands individually, *after* vanilla's decision has been made, and cooperate with it. As if vanilla decides not to render a given hand, it will not even fire this event to give us the chance.
     */
    public static void onHandRender(RenderHandEvent event)
    {
        final Player player = ClientHelpers.getPlayer();
        if (player == null)
        {
            return;
        }

        final ItemStack mainHand = player.getMainHandItem();
        if (mainHand.getItem() instanceof PanItem || mainHand.getItem() instanceof EmptyPanItem)
        {
            // Like charged crossbows, when present in the main hand, we only render the main hand, and we render it with two hands
            // So, we cancel this event unconditionally, and in the main hand branch, render our own two-handed item
            // Pans held in the offhand render normally, and don't allow panning (unlike vanilla crossbows), as we require it to be the two-handed animation.
            if (event.getHand() == InteractionHand.MAIN_HAND)
            {
                final PoseStack poseStack = event.getPoseStack();
                poseStack.pushPose();
                RenderHelpers.renderTwoHandedItem(poseStack, event.getMultiBufferSource(), event.getPackedLight(), event.getInterpolatedPitch(), event.getEquipProgress(), event.getSwingProgress(), mainHand);
                poseStack.popPose();
            }
            event.setCanceled(true);
        }
    }

    public static void onToast(ToastAddEvent event)
    {
        if (!TFCConfig.CLIENT.enableVanillaTutorialToasts.get() && event.getToast() instanceof TutorialToast)
        {
            event.setCanceled(true);
        }
    }

    public static void onEffectRender(ScreenEvent.RenderInventoryMobEffects event)
    {
        event.addHorizontalOffset(TFCConfig.CLIENT.effectHorizontalAdjustment.get());
    }
}