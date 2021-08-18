/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.client;

import java.lang.reflect.Field;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.color.block.BlockTintCache;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ColorResolver;
import net.minecraft.world.level.Level;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fmllegacy.network.PacketDistributor;

import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import net.dries007.tfc.TerraFirmaCraft;
import net.dries007.tfc.client.screen.button.PlayerInventoryTabButton;
import net.dries007.tfc.common.capabilities.food.FoodCapability;
import net.dries007.tfc.common.capabilities.heat.HeatCapability;
import net.dries007.tfc.common.capabilities.size.ItemSizeManager;
import net.dries007.tfc.common.types.FuelManager;
import net.dries007.tfc.common.types.MetalItemManager;
import net.dries007.tfc.config.TFCConfig;
import net.dries007.tfc.network.PacketHandler;
import net.dries007.tfc.network.PlaceBlockSpecialPacket;
import net.dries007.tfc.network.SwitchInventoryTabPacket;
import net.dries007.tfc.util.Climate;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.calendar.Calendars;
import net.dries007.tfc.util.calendar.ICalendar;
import net.dries007.tfc.world.chunkdata.ChunkData;

import static net.minecraft.ChatFormatting.*;

public class ClientForgeEventHandler
{
    private static final ResourceLocation ICONS = Helpers.identifier("textures/gui/icons/overlay.png");
    private static final Field CAP_NBT_FIELD = Helpers.findUnobfField(ItemStack.class, "capNBT");

    public static void init()
    {
        final IEventBus bus = MinecraftForge.EVENT_BUS;

        bus.addListener(ClientForgeEventHandler::onRenderGameOverlayText);
        bus.addListener(ClientForgeEventHandler::onItemTooltip);
        bus.addListener(ClientForgeEventHandler::onInitGuiPost);
        bus.addListener(ClientForgeEventHandler::onClientWorldLoad);
        bus.addListener(ClientForgeEventHandler::onClientTick);
        bus.addListener(ClientForgeEventHandler::onKeyEvent);
        bus.addListener(ClientForgeEventHandler::onRenderOverlay);
        // bus.addListener(ClientForgeEventHandler::onHighlightBlockEvent);
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
                list.add(I18n.get("tfc.tooltip.calendar_date") + Calendars.CLIENT.getCalendarTimeAndDate().getString());

                if (TFCConfig.CLIENT.enableDebug.get())
                {
                    list.add(String.format("Ticks = %d, Calendar = %d, Daytime = %d", Calendars.CLIENT.getTicks(), Calendars.CLIENT.getCalendarTicks(), mc.getCameraEntity().level.getDayTime() % ICalendar.TICKS_IN_DAY));
                }

                ChunkData data = ChunkData.get(mc.level, pos);
                if (data.getStatus() == ChunkData.Status.CLIENT)
                {
                    list.add(GRAY + I18n.get("tfc.tooltip.f3_average_temperature", WHITE + String.format("%.1f", data.getAverageTemp(pos))));
                    list.add(GRAY + I18n.get("tfc.tooltip.f3_temperature", WHITE + String.format("%.1f", Climate.calculateTemperature(pos, data.getAverageTemp(pos), Calendars.CLIENT))));
                    list.add(GRAY + I18n.get("tfc.tooltip.f3_rainfall", WHITE + String.format("%.1f", data.getRainfall(pos))));
                    list.add(GRAY + I18n.get("tfc.tooltip.f3_forest_type") + WHITE + I18n.get(Helpers.getEnumTranslationKey(data.getForestType())));
                    list.add(GRAY + I18n.get("tfc.tooltip.f3_forest_properties",
                        WHITE + String.format("%.1f%%", 100 * data.getForestDensity()) + GRAY,
                        WHITE + String.format("%.1f%%", 100 * data.getForestWeirdness()) + GRAY));
                }
                else
                {
                    list.add(GRAY + I18n.get("tfc.tooltip.f3_invalid_chunk_data"));
                }
            }
        }
    }

    public static void onItemTooltip(ItemTooltipEvent event)
    {
        final ItemStack stack = event.getItemStack();
        final List<Component> text = event.getToolTip();
        if (!stack.isEmpty())
        {
            ItemSizeManager.addTooltipInfo(stack, text);
            stack.getCapability(FoodCapability.CAPABILITY).ifPresent(cap -> cap.addTooltipInfo(stack, text));
            stack.getCapability(HeatCapability.CAPABILITY).ifPresent(cap -> cap.addTooltipInfo(stack, text));

            if (event.getFlags().isAdvanced())
            {
                MetalItemManager.addTooltipInfo(stack, text);
                FuelManager.addTooltipInfo(stack, text);
            }

            if (TFCConfig.CLIENT.enableDebug.get())
            {
                final CompoundTag stackTag = stack.getTag();
                if (stackTag != null)
                {
                    text.add(new TextComponent("[Debug] NBT: " + stackTag));
                }

                final CompoundTag capTag = Helpers.uncheck(() -> CAP_NBT_FIELD.get(stack));
                if (capTag != null)
                {
                    text.add(new TextComponent("[Debug] Capability NBT: " + capTag));
                }
            }
        }
    }

    public static void onInitGuiPost(GuiScreenEvent.InitGuiEvent.Post event)
    {
        Player player = Minecraft.getInstance().player;
        if (event.getGui() instanceof InventoryScreen screen && player != null && !player.isCreative())
        {
            int guiLeft = ((InventoryScreen) event.getGui()).getGuiLeft();
            int guiTop = ((InventoryScreen) event.getGui()).getGuiTop();

            event.addWidget(new PlayerInventoryTabButton(guiLeft, guiTop, 176 - 3, 4, 20 + 3, 22, 128 + 20, 0, 1, 3, 0, 0, button -> {}).setRecipeBookCallback(screen));
            event.addWidget(new PlayerInventoryTabButton(guiLeft, guiTop, 176, 27, 20, 22, 128, 0, 1, 3, 32, 0, SwitchInventoryTabPacket.Type.CALENDAR).setRecipeBookCallback(screen));
            event.addWidget(new PlayerInventoryTabButton(guiLeft, guiTop, 176, 50, 20, 22, 128, 0, 1, 3, 64, 0, SwitchInventoryTabPacket.Type.NUTRITION).setRecipeBookCallback(screen));
            event.addWidget(new PlayerInventoryTabButton(guiLeft, guiTop, 176, 73, 20, 22, 128, 0, 1, 3, 96, 0, SwitchInventoryTabPacket.Type.CLIMATE).setRecipeBookCallback(screen));
        }
    }

    public static void onClientWorldLoad(WorldEvent.Load event)
    {
        if (event.getWorld() instanceof final ClientLevel world)
        {
            // Add our custom tints to the color resolver caches
            // todo: mixin accessor
            final Object2ObjectArrayMap<ColorResolver, BlockTintCache> colorCaches = world.tintCaches;

            colorCaches.putIfAbsent(TFCColors.FRESH_WATER, new BlockTintCache());
            colorCaches.putIfAbsent(TFCColors.SALT_WATER, new BlockTintCache());

            // Update cloud height
            final float cloudHeight = TFCConfig.CLIENT.assumeTFCWorld.get() ? 210 : 160;
            // todo: if we switch the world height to default I don't think this accessor/mixin is needed anymore
            //((DimensionRenderInfoAccessor) DimensionRenderInfoAccessor.accessor$Effects().get(DimensionType.OVERWORLD_EFFECTS)).accessor$setCloudLevel(cloudHeight);

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
    }

    @SuppressWarnings("deprecation")
    public static void onRenderOverlay(RenderGameOverlayEvent.Pre event)
    {
        final Minecraft mc = Minecraft.getInstance();
        if (mc.player == null)
        {
            return;
        }
        // todo: this all needs to be rewritten with the new forge thing
        /*
        final IngameGui gui = mc.gui;
        final PlayerEntity player = mc.player;

        ForgeIngameGui.renderFood = !TFCConfig.CLIENT.enableHungerBar.get();
        ForgeIngameGui.renderHealth = !TFCConfig.CLIENT.enableHealthBar.get();
        ForgeIngameGui.renderExperiance = !TFCConfig.CLIENT.enableHealthBar.get() && !TFCConfig.CLIENT.enableThirstBar.get(); // only allow vanilla exp in this case
        HealthDisplayStyle healthDisplayStyle = TFCConfig.CLIENT.healthDisplayStyle.get();

        if (event.getType() != RenderGameOverlayEvent.ElementType.CROSSHAIRS)
        {
            return;
        }

        if (TFCConfig.CLIENT.enableThirstBar.get() || TFCConfig.CLIENT.enableHungerBar.get())
        {
            ForgeIngameGui.right_height += TFCConfig.CLIENT.enableHungerBar.get() ? 10 : 6;
        }

        MatrixStack matrixStack = event.getMatrixStack();

        float displayModifier = (healthDisplayStyle == HealthDisplayStyle.TFC || healthDisplayStyle == HealthDisplayStyle.TFC_CURRENT) ? 50f : 1f;
        float maxHealth = player.getMaxHealth() * displayModifier; // 20 * 50 = 1000
        float currentThirst = player.getFoodData() instanceof TFCFoodStats ? ((TFCFoodStats) player.getFoodData()).getThirst() : 100f;

        MainWindow window = event.getWindow();
        int guiScaledHeight = window.getGuiScaledHeight();
        int guiScaledWidth = window.getGuiScaledWidth();

        int healthRowHeight = guiScaledHeight - 40;
        int armorRowHeight = healthRowHeight - 10;
        int mid = guiScaledWidth / 2;

        FontRenderer fontRenderer = mc.font;
        RenderSystem.enableBlend();
        RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        TextureManager textureManager = mc.getTextureManager();
        textureManager.bind(ICONS);

        if (!player.abilities.instabuild && !player.isSpectator())
        {
            float foodLevel = player.getFoodData().getFoodLevel();
            float percentFood = foodLevel / 20f;
            float percentThirst = currentThirst / 100f;

            if (TFCConfig.CLIENT.enableHungerBar.get())
            {
                matrixStack.pushPose();
                matrixStack.translate(mid + 1, healthRowHeight, 0);
                gui.blit(matrixStack, 0, 0, 0, 20, 90, 5);
                gui.blit(matrixStack, 0, 0, 0, 25, (int) (90 * percentFood), 5);
                matrixStack.popPose();
            }

            if (TFCConfig.CLIENT.enableThirstBar.get())
            {
                matrixStack.pushPose();
                matrixStack.translate(mid + 1, healthRowHeight + 5, 0);
                gui.blit(matrixStack, 0, 0, 90, 20, 90, 5);
                gui.blit(matrixStack, 0, 0, 90, 25, (int) (90 * percentThirst), 5);
                matrixStack.popPose();
            }

            if (TFCConfig.CLIENT.enableHealthBar.get())
            {
                ForgeIngameGui.left_height += 10;
                matrixStack.pushPose();
                matrixStack.translate(mid - 91, healthRowHeight, 0);
                gui.blit(matrixStack, 0, 0, 0, 0, 90, 10);

                float absorption = player.getAbsorptionAmount();
                float percentHealth = (player.getHealth() + absorption) / 20f;
                float currentHealth = percentHealth * maxHealth;
                int uSurplus = 90;
                if (percentHealth > 1) percentHealth = 1;

                gui.blit(matrixStack, 0, 0, 0, 10, (int) (90 * percentHealth), 10);

                if ((player.getFoodData().getSaturationLevel() > 0.0f && player.isHurt()) || player.hurtTime > 0 || player.hasEffect(Effects.REGENERATION))
                {
                    gui.blit(matrixStack, 0, 1, 0, 30, 90, 8);
                }
                float surplusPercent = MathHelper.clamp(percentHealth + (absorption / 20f) - 1, 0, 1);
                if (surplusPercent > 0)
                {
                    // fill up the yellow bar until you get a second full bar, then just fill it up
                    float percent = Math.min(surplusPercent, 1);
                    gui.blit(matrixStack, 0, 0, uSurplus, 10, (int) (90 * percent), 10);
                }
                matrixStack.popPose();

                String healthString = healthDisplayStyle.format(currentHealth, maxHealth);
                matrixStack.pushPose();
                matrixStack.translate(mid - 45, healthRowHeight + 2.5, 0);
                matrixStack.scale(0.8f, 0.8f, 1.0f);
                fontRenderer.draw(matrixStack, healthString, -1 * fontRenderer.width(healthString) / 2f, 0, surplusPercent < 0.6 ? Color.white.getRGB() : Color.black.getRGB());
                matrixStack.popPose();

                RenderSystem.enableBlend();
                RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
            }

            textureManager.bind(AbstractGui.GUI_ICONS_LOCATION);
            Entity mount = player.getVehicle();
            if (!(mount instanceof LivingEntity) && mc.gameMode != null && mc.gameMode.hasExperience() && !ForgeIngameGui.renderExperiance)
            {
                int left = mid - 91;
                int xpNeeded = player.getXpNeededForNextLevel();
                if (xpNeeded > 0)
                {
                    int filled = (int) (player.experienceProgress * 183.0F);
                    int top = guiScaledHeight - 29;
                    matrixStack.pushPose();
                    matrixStack.translate(left, top, 0);
                    gui.blit(matrixStack, 0, 0, 0, 64, 182, 5);
                    if (filled > 0)
                    {
                        gui.blit(matrixStack, 0, 0, 0, 69, filled, 5);
                    }
                    matrixStack.popPose();
                }

                if (player.experienceLevel > 0)
                {
                    String level = "" + player.experienceLevel;
                    int x = (guiScaledWidth - fontRenderer.width(level)) / 2;
                    int y = guiScaledHeight - 35;
                    fontRenderer.draw(matrixStack, level, x + 1, y, 0);
                    fontRenderer.draw(matrixStack, level, x - 1, y, 0);
                    fontRenderer.draw(matrixStack, level, x, y + 1, 0);
                    fontRenderer.draw(matrixStack, level, x, y - 1, 0);
                    fontRenderer.draw(matrixStack, level, x, y, 8453920);
                }
                RenderSystem.enableBlend();
                RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
            }
            if (mount instanceof LivingEntity && TFCConfig.CLIENT.enableHealthBar.get())
            {
                textureManager.bind(ICONS);
                ForgeIngameGui.renderHealthMount = false;
                LivingEntity mountEntity = (LivingEntity) mount;
                matrixStack.pushPose();
                matrixStack.translate(mid + 1, armorRowHeight, 0);

                float mountMaxHealth = mountEntity.getMaxHealth() * displayModifier;
                float mountCurrentHealth = mountEntity.getHealth() * displayModifier;
                float mountPercentHealth = Math.min(mountCurrentHealth / mountMaxHealth, 1.0f);
                gui.blit(matrixStack, 0, 0, 90, 0, 90, 10);
                gui.blit(matrixStack, 0, 0, 90, 10, (int) (90 * mountPercentHealth), 10);
                if (mountEntity.hurtTime > 0)
                {
                    gui.blit(matrixStack, 0, 1, 0, 30, 90, 8);
                }
                matrixStack.popPose();

                String mountHealthString = healthDisplayStyle.format(mountCurrentHealth, mountMaxHealth);
                matrixStack.pushPose();
                matrixStack.translate(mid + 47, armorRowHeight + 2.5, 0);
                matrixStack.scale(0.8f, 0.8f, 1.0f);
                fontRenderer.draw(matrixStack, mountHealthString, -1 * (fontRenderer.width(mountHealthString) / 2f), 0, mountPercentHealth < 0.6 ? Color.WHITE.getRGB() : Color.BLACK.getRGB());
                matrixStack.popPose();

                RenderSystem.enableBlend();
                RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
            }
        }

         */
    }

    /**
     * Handles custom bounding boxes drawing
     * eg: Chisel, Quern handle
     * todo: where?
     */
    public static void onHighlightBlockEvent(){}/*DrawHighlightEvent.HighlightBlock event)
    {
        final ActiveRenderInfo info = event.getInfo();
        final MatrixStack mStack = event.getMatrix();
        final Entity entity = info.getEntity();
        final World world = entity.level;
        final BlockRayTraceResult traceResult = event.getTarget();
        final BlockPos lookingAt = new BlockPos(traceResult.getLocation());

        //noinspection ConstantConditions
        if (lookingAt != null && entity instanceof PlayerEntity)
        {
            PlayerEntity player = (PlayerEntity) entity;
            Block blockAt = world.getBlockState(lookingAt).getBlock();
            //todo: chisel
            if (blockAt instanceof IHighlightHandler) //todo: java 16
            {
                // Pass on to custom implementations
                IHighlightHandler handler = (IHighlightHandler) blockAt;
                if (handler.drawHighlight(world, lookingAt, player, traceResult, mStack, event.getBuffers(), info.getPosition()))
                {
                    // Cancel drawing this block's bounding box
                    event.setCanceled(true);
                }
            }
        }
    }*/
}