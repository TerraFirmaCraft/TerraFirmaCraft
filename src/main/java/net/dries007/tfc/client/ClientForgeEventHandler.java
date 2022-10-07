/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.client;

import java.awt.*;
import java.lang.reflect.Field;
import java.util.List;
import java.util.stream.Collectors;

import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.color.block.BlockTintCache;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.FogRenderer;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.client.resources.sounds.AmbientSoundHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ColorResolver;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FogType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.client.event.*;
import net.minecraftforge.client.gui.ForgeIngameGui;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.registries.ForgeRegistries;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.vertex.PoseStack;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import net.dries007.tfc.TerraFirmaCraft;
import net.dries007.tfc.client.screen.button.PlayerInventoryTabButton;
import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.capabilities.egg.EggCapability;
import net.dries007.tfc.common.capabilities.food.FoodCapability;
import net.dries007.tfc.common.capabilities.forge.Forging;
import net.dries007.tfc.common.capabilities.forge.ForgingBonus;
import net.dries007.tfc.common.capabilities.heat.HeatCapability;
import net.dries007.tfc.common.capabilities.size.ItemSizeManager;
import net.dries007.tfc.common.entities.livestock.Mammal;
import net.dries007.tfc.common.entities.livestock.MammalProperties;
import net.dries007.tfc.common.entities.livestock.TFCAnimalProperties;
import net.dries007.tfc.common.items.EmptyPanItem;
import net.dries007.tfc.common.items.PanItem;
import net.dries007.tfc.common.recipes.ChiselRecipe;
import net.dries007.tfc.common.recipes.HeatingRecipe;
import net.dries007.tfc.common.recipes.inventory.ItemStackInventory;
import net.dries007.tfc.compat.patchouli.PatchouliIntegration;
import net.dries007.tfc.config.TFCConfig;
import net.dries007.tfc.mixin.client.accessor.ClientLevelAccessor;
import net.dries007.tfc.mixin.client.accessor.LocalPlayerAccessor;
import net.dries007.tfc.network.*;
import net.dries007.tfc.util.*;
import net.dries007.tfc.util.calendar.Calendars;
import net.dries007.tfc.util.calendar.ICalendar;
import net.dries007.tfc.util.climate.Climate;
import net.dries007.tfc.util.tracker.WorldTrackerCapability;
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
        bus.addListener(ClientForgeEventHandler::onClientWorldLoad);
        bus.addListener(ClientForgeEventHandler::onClientPlayerLoggedIn);
        bus.addListener(ClientForgeEventHandler::onClientTick);
        bus.addListener(ClientForgeEventHandler::onKeyEvent);
        bus.addListener(ClientForgeEventHandler::onScreenKey);
        bus.addListener(ClientForgeEventHandler::onHighlightBlockEvent);
        bus.addListener(ClientForgeEventHandler::onFogRender);
        bus.addListener(ClientForgeEventHandler::onHandRender);
        bus.addListener(ClientForgeEventHandler::onRenderLivingPost);
    }

    public static void onRenderGameOverlayText(RenderGameOverlayEvent.Text event)
    {
        Minecraft mc = Minecraft.getInstance();
        List<String> list = event.getRight();
        if (mc.level != null && mc.options.renderDebug && TFCConfig.CLIENT.enableTFCF3Overlays.get())
        {
            //noinspection ConstantConditions
            BlockPos pos = new BlockPos(mc.getCameraEntity().getX(), mc.getCameraEntity().getBoundingBox().minY, mc.getCameraEntity().getZ());
            if (mc.level.hasChunk(pos.getX() >> 4, pos.getZ() >> 4))
            {
                list.add("");
                list.add(AQUA + TerraFirmaCraft.MOD_NAME);

                // Always add calendar info
                list.add(Helpers.translatable("tfc.tooltip.calendar_date", Calendars.CLIENT.getCalendarTimeAndDate()).getString());

                if (TFCConfig.CLIENT.enableDebug.get())
                {
                    list.add(String.format("[Debug] Ticks = %d, Calendar = %d, Daytime = %d", Calendars.CLIENT.getTicks(), Calendars.CLIENT.getCalendarTicks(), mc.getCameraEntity().level.getDayTime() % ICalendar.TICKS_IN_DAY));
                }

                // Always add climate data
                list.add(GRAY + I18n.get("tfc.tooltip.f3_average_temperature", WHITE + String.format("%.1f", ClimateRenderCache.INSTANCE.getAverageTemperature())));
                list.add(GRAY + I18n.get("tfc.tooltip.f3_temperature", WHITE + String.format("%.1f", ClimateRenderCache.INSTANCE.getTemperature())));
                list.add(GRAY + I18n.get("tfc.tooltip.f3_rainfall", WHITE + String.format("%.1f", ClimateRenderCache.INSTANCE.getRainfall())));

                ChunkData data = ChunkData.get(mc.level, pos);
                if (data.getStatus() == ChunkData.Status.CLIENT)
                {
                    list.add(GRAY + I18n.get("tfc.tooltip.f3_forest_type") + WHITE + I18n.get(Helpers.getEnumTranslationKey(data.getForestType())));
                    list.add(GRAY + I18n.get("tfc.tooltip.f3_forest_properties",
                        WHITE + String.format("%.1f%%", 100 * data.getForestDensity()) + GRAY,
                        WHITE + String.format("%.1f%%", 100 * data.getForestWeirdness()) + GRAY));
                }
                else
                {
                    list.add(GRAY + I18n.get("tfc.tooltip.f3_invalid_chunk_data"));
                }

                mc.level.getCapability(WorldTrackerCapability.CAPABILITY).ifPresent(cap -> cap.addDebugTooltip(list));
            }
        }
    }

    /**
     * Render overlays for looking at particular block / item combinations
     */
    public static void onRenderGameOverlayPost(RenderGameOverlayEvent.Post event)
    {
        final PoseStack stack = event.getMatrixStack();
        final Minecraft minecraft = Minecraft.getInstance();
        final Player player = minecraft.player;
        if (player != null)
        {
            if (event.getType() == RenderGameOverlayEvent.ElementType.ALL && minecraft.screen == null && (Helpers.isItem(player.getMainHandItem().getItem(), TFCTags.Items.HOES)) || Helpers.isItem(player.getOffhandItem().getItem(), TFCTags.Items.HOES) && (!TFCConfig.CLIENT.showHoeOverlaysOnlyWhenShifting.get() && player.isShiftKeyDown()))
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

            ItemSizeManager.addTooltipInfo(stack, text);
            PhysicalDamageType.addTooltipInfo(stack, text);
            ForgingBonus.addTooltipInfo(stack, text);
            Forging.addTooltipInfo(stack, text);

            stack.getCapability(FoodCapability.CAPABILITY).ifPresent(cap -> cap.addTooltipInfo(stack, text));
            stack.getCapability(HeatCapability.CAPABILITY).ifPresent(cap -> cap.addTooltipInfo(stack, text));
            stack.getCapability(EggCapability.CAPABILITY).ifPresent(cap -> cap.addTooltipInfo(text));

            // Fuel information
            final Fuel fuel = Fuel.get(stack);
            if (fuel != null)
            {
                final MutableComponent heat = TFCConfig.CLIENT.heatTooltipStyle.get().formatColored(fuel.getTemperature());
                if (heat != null)
                {
                    text.add(Helpers.translatable(
                        "tfc.tooltip.fuel_burns_at", // burns at %s for %s
                        heat, Calendars.CLIENT.getTimeDelta(fuel.getDuration())));
                }
            }

            final Fertilizer fertilizer = Fertilizer.get(stack);
            if (fertilizer != null)
            {
                final float n = fertilizer.getNitrogen(), p = fertilizer.getPhosphorus(), k = fertilizer.getPotassium();
                if (n != 0)
                    text.add(Helpers.translatable("tfc.tooltip.fertilizer.nitrogen", String.format("%.1f", n * 100)));
                if (p != 0)
                    text.add(Helpers.translatable("tfc.tooltip.fertilizer.phosphorus", String.format("%.1f", p * 100)));
                if (k != 0)
                    text.add(Helpers.translatable("tfc.tooltip.fertilizer.potassium", String.format("%.1f", k * 100)));
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
                        final MutableComponent heat = TFCConfig.CLIENT.heatTooltipStyle.get().formatColored(recipe.getTemperature());
                        if (heat != null)
                        {
                            text.add(Helpers.translatable(
                                "tfc.tooltip.item_melts_into", // %s mB of %s (at %s)
                                fluid.getAmount() * stack.getCount(), Helpers.translatable(metal.getTranslationKey()), heat));
                        }
                    }
                }
            }

            if (TFCConfig.CLIENT.enableDebug.get() && event.getFlags().isAdvanced())
            {
                final CompoundTag stackTag = stack.getTag();
                if (stackTag != null)
                {
                    text.add(Helpers.literal(DARK_GRAY + "[Debug] NBT: " + stackTag));
                }

                final CompoundTag capTag = Helpers.uncheck(() -> CAP_NBT_FIELD.get(stack));
                if (capTag != null && !capTag.isEmpty())
                {
                    text.add(Helpers.literal(DARK_GRAY + "[Debug] Cap NBT: " + capTag));
                }

                text.add(Helpers.literal(DARK_GRAY + "[Debug] Item Tags: " + Helpers.getHolder(ForgeRegistries.ITEMS, stack.getItem()).tags().map(t -> "#" + t.location()).collect(Collectors.joining(", "))));

                if (stack.getItem() instanceof BlockItem blockItem)
                {
                    final Block block = blockItem.getBlock();
                    text.add(Helpers.literal(DARK_GRAY + "[Debug] Block Tags: " + Helpers.getHolder(ForgeRegistries.BLOCKS, block).tags().map(t -> "#" + t.location()).collect(Collectors.joining(", "))));
                }
            }
        }
    }

    public static void onInitGuiPost(ScreenEvent.InitScreenEvent.Post event)
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

    public static void onClientWorldLoad(WorldEvent.Load event)
    {
        if (event.getWorld() instanceof final ClientLevel level)
        {
            // Add our custom tints to the color resolver caches
            final Object2ObjectArrayMap<ColorResolver, BlockTintCache> colorCaches = ((ClientLevelAccessor) level).accessor$getTintCaches();

            colorCaches.putIfAbsent(TFCColors.FRESH_WATER, new BlockTintCache(TFCColors::getWaterColor));
            colorCaches.putIfAbsent(TFCColors.SALT_WATER, new BlockTintCache(TFCColors::getWaterColor));

        }
    }

    public static void onClientPlayerLoggedIn(ClientPlayerNetworkEvent.LoggedInEvent event)
    {
        // We can't send this on client world load, it's too early, as the connection is not setup yet
        // This is the closest point after that which will work
        PacketHandler.send(PacketDistributor.SERVER.noArg(), new RequestClimateModelPacket());

        LocalPlayer player = event.getPlayer();
        if (player != null)
        {
            List<AmbientSoundHandler> handlers = ((LocalPlayerAccessor) player).accessor$getAmbientSoundHandlers();
            if (handlers.stream().noneMatch(handler -> handler instanceof TFCBubbleColumnAmbientSoundHandler))
            {
                handlers.add(new TFCBubbleColumnAmbientSoundHandler(player));
            }
        }
    }

    public static void onClientTick(TickEvent.ClientTickEvent event)
    {
        Level world = Minecraft.getInstance().level;
        if (event.phase == TickEvent.Phase.END && world != null && !Minecraft.getInstance().isPaused())
        {
            Calendars.CLIENT.onClientTick();
            ClimateRenderCache.INSTANCE.onClientTick();
        }
    }

    public static void onKeyEvent(InputEvent.KeyInputEvent event)
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

    public static void onScreenKey(ScreenEvent.KeyboardKeyPressedEvent.Pre event)
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
    public static void onHighlightBlockEvent(DrawSelectionEvent.HighlightBlock event)
    {
        final Camera camera = event.getCamera();
        final PoseStack poseStack = event.getPoseStack();
        final Entity entity = camera.getEntity();
        final Level level = entity.level;
        final BlockHitResult hit = event.getTarget();
        final BlockPos pos = hit.getBlockPos();
        final BlockPos lookingAt = new BlockPos(pos);

        //noinspection ConstantConditions
        if (lookingAt != null && entity instanceof Player player)
        {
            BlockState stateAt = level.getBlockState(lookingAt);
            Block blockAt = stateAt.getBlock();

            ChiselRecipe.computeResult(player, stateAt, hit, false).ifLeft(chiseled -> {
                IHighlightHandler.drawBox(poseStack, chiseled.getShape(level, pos), event.getMultiBufferSource(), pos, camera.getPosition(), 1f, 0f, 0f, 0.4f);
                event.setCanceled(true);
            });
            if (blockAt instanceof IHighlightHandler handler)
            {
                // Pass on to custom implementations
                if (handler.drawHighlight(level, lookingAt, player, hit, poseStack, event.getMultiBufferSource(), camera.getPosition()))
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
        }
    }

    public static void onFogRender(EntityViewRenderEvent.RenderFogEvent event)
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

    public static void onRenderLivingPost(RenderLivingEvent.Post<?, ?> event)
    {
        final Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;
        Player player = mc.player;

        if (player.isShiftKeyDown() && mc.gui instanceof ForgeIngameGui gui && IngameOverlays.setup(gui, mc))
        {
            Entity entity = mc.crosshairPickEntity;
            if (entity instanceof TFCAnimalProperties animal && animal.getAdultFamiliarityCap() > 0 && animal.equals(event.getEntity()))
            {
                if (player.closerThan(entity, 5.0F))
                {
                    PoseStack stack = event.getPoseStack();
                    stack.pushPose();
                    stack.translate(0F, entity.getBbHeight() + 1.2F, 0F); // manipulate this the position of the heart

                    final float scale = 0.0266666688F;
                    stack.scale(-scale, -scale, -scale);
                    stack.translate(0F, 0.25F / scale, 0.0F);
                    stack.scale(0.5F, 0.5F, 0.5F); // manipulate this to change the size of the heart
                    stack.mulPose(mc.getEntityRenderDispatcher().cameraOrientation()); // rotates the heart to face the player

                    float familiarity = Math.max(0.0F, Math.min(1.0F, animal.getFamiliarity()));
                    int u;
                    int fontColor;
                    if (familiarity >= animal.getAdultFamiliarityCap() && animal.getAgeType() != TFCAnimalProperties.Age.CHILD)
                    {
                        u = 132; // Render a red-ish outline for adults that cannot be familiarized more
                        fontColor = Color.RED.getRGB();
                    }
                    else if (familiarity >= 0.3F)
                    {
                        u = 112; // Render a white outline for the when the familiarity stopped decaying
                        fontColor = Color.WHITE.getRGB();
                    }
                    else
                    {
                        u = 92;
                        fontColor = Color.GRAY.getRGB();
                    }

                    if (TFCConfig.CLIENT.displayFamiliarityAsPercent.get())
                    {
                        String string = String.format("%.2f", familiarity * 100);
                        stack.translate(0F, 45F, 0F);
                        mc.font.draw(stack, string, -mc.font.width(string) / 2f, 0, fontColor);
                    }
                    else
                    {
                        gui.blit(stack, -8, 0, u, 40, 16, 16);

                        stack.translate(0F, 0F,-0.001F);
                        gui.blit(stack, -6, 14 - (int) (12 * familiarity), familiarity == 1.0F ? 114 : 94, 74 - (int) (12 * familiarity), 12, (int) (12 * familiarity));
                    }
                    if (animal instanceof MammalProperties mammal && mammal.getPregnantTime() > 0 && mammal.isFertilized())
                    {
                        stack.translate(0, -15F, 0F);
                        String string = Helpers.translatable("tfc.tooltip.animal.pregnant", entity.getName().getString()).getString();
                        mc.font.draw(stack, string, -mc.font.width(string) / 2f, 0, Color.WHITE.getRGB());
                    }

                    stack.popPose();
                }
            }
        }
    }
}