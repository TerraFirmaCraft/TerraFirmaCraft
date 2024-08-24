/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.util.tooltip;

import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.Mth;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.FireBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.common.blockentities.AbstractFirepitBlockEntity;
import net.dries007.tfc.common.blockentities.BarrelBlockEntity;
import net.dries007.tfc.common.blockentities.BellowsBlockEntity;
import net.dries007.tfc.common.blockentities.BlastFurnaceBlockEntity;
import net.dries007.tfc.common.blockentities.BloomBlockEntity;
import net.dries007.tfc.common.blockentities.BloomeryBlockEntity;
import net.dries007.tfc.common.blockentities.CharcoalForgeBlockEntity;
import net.dries007.tfc.common.blockentities.ComposterBlockEntity;
import net.dries007.tfc.common.blockentities.CropBlockEntity;
import net.dries007.tfc.common.blockentities.CrucibleBlockEntity;
import net.dries007.tfc.common.blockentities.DecayingBlockEntity;
import net.dries007.tfc.common.blockentities.IngotPileBlockEntity;
import net.dries007.tfc.common.blockentities.LampBlockEntity;
import net.dries007.tfc.common.blockentities.LoomBlockEntity;
import net.dries007.tfc.common.blockentities.NestBoxBlockEntity;
import net.dries007.tfc.common.blockentities.PitKilnBlockEntity;
import net.dries007.tfc.common.blockentities.PotBlockEntity;
import net.dries007.tfc.common.blockentities.PowderkegBlockEntity;
import net.dries007.tfc.common.blockentities.SheetPileBlockEntity;
import net.dries007.tfc.common.blockentities.TickCounterBlockEntity;
import net.dries007.tfc.common.blockentities.rotation.RotatingBlockEntity;
import net.dries007.tfc.common.blocks.BloomBlock;
import net.dries007.tfc.common.blocks.HotPouredGlassBlock;
import net.dries007.tfc.common.blocks.TFCCandleBlock;
import net.dries007.tfc.common.blocks.TFCCandleCakeBlock;
import net.dries007.tfc.common.blocks.TFCTorchBlock;
import net.dries007.tfc.common.blocks.TFCWallTorchBlock;
import net.dries007.tfc.common.blocks.crop.CropBlock;
import net.dries007.tfc.common.blocks.crop.DecayingBlock;
import net.dries007.tfc.common.blocks.devices.BarrelBlock;
import net.dries007.tfc.common.blocks.devices.BellowsBlock;
import net.dries007.tfc.common.blocks.devices.BlastFurnaceBlock;
import net.dries007.tfc.common.blocks.devices.BloomeryBlock;
import net.dries007.tfc.common.blocks.devices.CharcoalForgeBlock;
import net.dries007.tfc.common.blocks.devices.CrucibleBlock;
import net.dries007.tfc.common.blocks.devices.DryingBricksBlock;
import net.dries007.tfc.common.blocks.devices.FirepitBlock;
import net.dries007.tfc.common.blocks.devices.IngotPileBlock;
import net.dries007.tfc.common.blocks.devices.JackOLanternBlock;
import net.dries007.tfc.common.blocks.devices.LampBlock;
import net.dries007.tfc.common.blocks.devices.NestBoxBlock;
import net.dries007.tfc.common.blocks.devices.PitKilnBlock;
import net.dries007.tfc.common.blocks.devices.PowderkegBlock;
import net.dries007.tfc.common.blocks.devices.QuernBlock;
import net.dries007.tfc.common.blocks.devices.SheetPileBlock;
import net.dries007.tfc.common.blocks.devices.TFCComposterBlock;
import net.dries007.tfc.common.blocks.plant.fruit.FruitTreeSaplingBlock;
import net.dries007.tfc.common.blocks.rotation.AbstractShaftAxleBlock;
import net.dries007.tfc.common.blocks.rotation.ClutchBlock;
import net.dries007.tfc.common.blocks.rotation.CrankshaftBlock;
import net.dries007.tfc.common.blocks.rotation.EncasedAxleBlock;
import net.dries007.tfc.common.blocks.rotation.GearBoxBlock;
import net.dries007.tfc.common.blocks.rotation.WaterWheelBlock;
import net.dries007.tfc.common.blocks.rotation.WindmillBlock;
import net.dries007.tfc.common.blocks.soil.HoeOverlayBlock;
import net.dries007.tfc.common.blocks.wood.TFCLoomBlock;
import net.dries007.tfc.common.blocks.wood.TFCSaplingBlock;
import net.dries007.tfc.common.component.EggComponent;
import net.dries007.tfc.common.component.TFCComponents;
import net.dries007.tfc.common.component.food.FoodCapability;
import net.dries007.tfc.common.component.heat.HeatCapability;
import net.dries007.tfc.common.component.heat.IHeatView;
import net.dries007.tfc.common.items.Powder;
import net.dries007.tfc.common.items.TFCItems;
import net.dries007.tfc.common.recipes.BarrelRecipe;
import net.dries007.tfc.common.recipes.BloomeryRecipe;
import net.dries007.tfc.common.recipes.LoomRecipe;
import net.dries007.tfc.config.TFCConfig;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.calendar.Calendars;
import net.dries007.tfc.util.calendar.ICalendar;
import net.dries007.tfc.util.data.LampFuel;
import net.dries007.tfc.util.rotation.Rotation;

/**
 * Common tooltips that can be displayed for various block entities via external sources.
 */
public final class BlockEntityTooltips
{
    public static void register(RegisterCallback<BlockEntityTooltip, Block> callback)
    {
        callback.register("barrel", BARREL, BarrelBlock.class);
        callback.register("bellows", BELLOWS, BellowsBlock.class);
        callback.register("sapling", SAPLING, TFCSaplingBlock.class);
        callback.register("blast_furnace", BLAST_FURNACE, BlastFurnaceBlock.class);
        callback.register("bloomery", BLOOMERY, BloomeryBlock.class);
        callback.register("bloom", BLOOM, BloomBlock.class);
        callback.register("charcoal_forge", CHARCOAL_FORGE, CharcoalForgeBlock.class);
        callback.register("composter", COMPOSTER, TFCComposterBlock.class);
        callback.register("crop", CROP, CropBlock.class);
        callback.register("crucible", CRUCIBLE, CrucibleBlock.class);
        callback.register("firepit", FIREPIT, FirepitBlock.class);
        callback.register("fruit_tree_sapling", FRUIT_TREE_SAPLING, FruitTreeSaplingBlock.class);
        callback.register("hoe_overlay", HOE_OVERLAY, Block.class);
        callback.register("lamp", LAMP, LampBlock.class);
        callback.register("nest_box", NEST_BOX, NestBoxBlock.class);
        callback.register("pit_kiln_internal", PIT_KILN_INTERNAL, PitKilnBlock.class);
        callback.register("pit_kiln_above", PIT_KILN_ABOVE, FireBlock.class);
        callback.register("powder_keg", POWDER_KEG, PowderkegBlock.class);
        callback.register("torch", TORCH, TFCTorchBlock.class);
        callback.register("wall_torch", TORCH, TFCWallTorchBlock.class);
        callback.register("candle", CANDLE, TFCCandleBlock.class);
        callback.register("candle_cake", CANDLE, TFCCandleCakeBlock.class);
        callback.register("jack_o_lantern", JACK_O_LANTERN, JackOLanternBlock.class);
        callback.register("mud_bricks", MUD_BRICKS, DryingBricksBlock.class);
        callback.register("decaying", DECAYING, DecayingBlock.class);
        callback.register("loom", LOOM, TFCLoomBlock.class);
        callback.register("sheet_pile", SHEET_PILE, SheetPileBlock.class);
        callback.register("ingot_pile", INGOT_PILE, IngotPileBlock.class);
        callback.register("axle", ROTATING, AbstractShaftAxleBlock.class);
        callback.register("encased_axle", ROTATING, EncasedAxleBlock.class);
        callback.register("clutch", ROTATING, ClutchBlock.class);
        callback.register("gearbox", ROTATING, GearBoxBlock.class);
        callback.register("crankshaft", ROTATING, CrankshaftBlock.class);
        callback.register("quern", ROTATING, QuernBlock.class);
        callback.register("water_wheel", ROTATING, WaterWheelBlock.class);
        callback.register("windmill", ROTATING, WindmillBlock.class);
        callback.register("hot_poured_glass", HOT_POURED_GLASS, HotPouredGlassBlock.class);
    }

    public static final BlockEntityTooltip HOT_POURED_GLASS = (level, state, pos, entity, tooltip) -> {
        if (state.getBlock() instanceof HotPouredGlassBlock && !state.getValue(HotPouredGlassBlock.FLAT))
        {
            tooltip.accept(Component.translatable("tfc.tooltip.glass.flatten_me"));
        }
    };

    public static final BlockEntityTooltip ROTATING = (level, state, pos, entity, tooltip) -> {
        if (entity instanceof RotatingBlockEntity rotating)
        {
            final Rotation rotation = rotating.getRotationNode().rotation();
            if (rotation != null && rotation.speed() != 0)
            {
                tooltip.accept(Component.translatable("tfc.tooltip.rotation.angular_velocity", String.format("%.2f", Math.abs(rotation.positiveSpeed()) * 20f)));
            }
        }
    };

    public static final BlockEntityTooltip INGOT_PILE = (level, state, pos, entity, tooltip) -> {
        if (entity instanceof IngotPileBlockEntity pile)
        {
            pile.fillTooltip(tooltip);
        }
    };

    public static final BlockEntityTooltip SHEET_PILE = (level, state, pos, entity, tooltip) -> {
        if (entity instanceof SheetPileBlockEntity pile)
        {
            pile.fillTooltip(tooltip);
        }
    };

    public static final BlockEntityTooltip BARREL = (level, state, pos, entity, tooltip) -> {
        if (state.getBlock() instanceof BarrelBlock && entity instanceof BarrelBlockEntity barrel)
        {
            if (state.getValue(BarrelBlock.SEALED))
            {
                final long tickLeft = barrel.getRemainingTicks();
                if (tickLeft > 0)
                {
                    final Component recipe = barrel.getRecipeTooltip();
                    if (recipe != null)
                    {
                        tooltip.accept(recipe);
                        tooltip.accept(Component.translatable("tfc.jade.sealed_date", ICalendar.getTimeAndDate(Calendars.get(level).ticksToCalendarTicks(barrel.getSealedTick()), Calendars.get(level).getCalendarDaysInMonth())));
                        timeLeft(level, tooltip, tickLeft);
                    }
                }
            }
        }
    };

    public static final BlockEntityTooltip BELLOWS = (level, state, pos, entity, tooltip) -> {
        if (entity instanceof BellowsBlockEntity bellows)
        {
            final int step = Mth.clamp((int) Mth.map(
                bellows.getExtensionLength(0f),
                BellowsBlockEntity.MIN_EXTENSION, BellowsBlockEntity.MAX_EXTENSION,
                0, 10), 0, 10);
            if (step > 0)
            {
                tooltip.accept(Component.translatable("tfc.jade.bellows_" + step));
            }
        }
    };

    public static final BlockEntityTooltip BLAST_FURNACE = (level, state, pos, entity, tooltip) -> {
        if (entity instanceof BlastFurnaceBlockEntity furnace)
        {
            heat(tooltip, furnace.getTemperature());
            tooltip.accept(Component.translatable("tfc.jade.input_stacks", furnace.getInputCount()));
            tooltip.accept(Component.translatable("tfc.jade.catalyst_stacks", furnace.getCatalystCount()));
            tooltip.accept(Component.translatable("tfc.jade.fuel_stacks", furnace.getFuelCount()));
        }
    };

    public static final BlockEntityTooltip BLOOMERY = (level, state, pos, entity, tooltip) -> {
        if (entity instanceof BloomeryBlockEntity bloomery && state.getBlock() instanceof BloomeryBlock)
        {
            tooltip.accept(Component.translatable("tfc.jade.input_stacks", bloomery.getInputCount()));
            if (state.getValue(BloomeryBlock.LIT))
            {
                final long ticksLeft = bloomery.getRemainingTicks();
                if (ticksLeft > 0)
                {
                    final BloomeryRecipe recipe = bloomery.getCachedRecipe();
                    if (recipe != null)
                    {
                        timeLeft(level, tooltip, ticksLeft);
                        tooltip.accept(Component.translatable("tfc.jade.creating", recipe.getResultItem(level.registryAccess()).getHoverName()));
                    }
                }
            }
            if (TFCConfig.CLIENT.enableDebug.get())
            {
                final Object2IntMap<Item> items = new Object2IntOpenHashMap<>();
                for (ItemStack stack : bloomery.getInputStacks())
                {
                    items.mergeInt(stack.getItem(), 1, Integer::sum);
                }
                for (Map.Entry<Item, Integer> entry : items.object2IntEntrySet())
                {
                    itemWithCount(tooltip, new ItemStack(entry.getKey(), entry.getValue()));
                }
            }
        }
    };

    public static final BlockEntityTooltip BLOOM = (level, state, pos, entity, tooltip) -> {
        if (entity instanceof BloomBlockEntity bloom)
        {
            final ItemStack item = bloom.getItem();

            ItemStack displayItem = item.copy();
            displayItem.setCount(bloom.getCount());
            itemWithCount(tooltip, displayItem);

            final @Nullable IHeatView heat = HeatCapability.view(item);
            if (heat != null)
            {
                heat.addTooltipInfo(item, tooltip);
            }
        }
    };

    public static final BlockEntityTooltip CHARCOAL_FORGE = (level, state, pos, entity, tooltip) -> {
        if (entity instanceof CharcoalForgeBlockEntity forge)
        {
            heat(tooltip, forge.getTemperature());
        }
    };

    public static final BlockEntityTooltip COMPOSTER = (level, state, pos, entity, tooltip) -> {
        if (state.getBlock() instanceof TFCComposterBlock block && entity instanceof ComposterBlockEntity composter)
        {
            if (composter.isRotten())
            {
                tooltip.accept(Component.translatable("tfc.composter.rotten"));
            }
            else
            {
                if (!composter.isReady() && state.getValue(TFCComposterBlock.STAGE) == 8)
                {
                    timeLeft(level, tooltip, composter.getReadyTicks() - composter.getTicksSinceUpdate());
                }
            }
        }
    };

    public static final BlockEntityTooltip CROP = (level, state, pos, entity, tooltip) -> {
        if (entity instanceof CropBlockEntity crop && state.getBlock() instanceof CropBlock block)
        {
            tooltip.accept(Component.translatable("tfc.jade.yield", String.format("%.0f", crop.getYield() * 100)));
        }
    };

    public static final BlockEntityTooltip CRUCIBLE = (level, state, pos, entity, tooltip) -> {
        if (entity instanceof CrucibleBlockEntity crucible)
        {
            heat(tooltip, crucible.getTemperature());
        }
    };

    public static final BlockEntityTooltip FIREPIT = (level, state, pos, entity, tooltip) -> {
        if (entity instanceof AbstractFirepitBlockEntity<?> firepit)
        {
            heat(tooltip, firepit.getTemperature());

            if (state.hasProperty(FirepitBlock.SMOKE_LEVEL))
            {
                tooltip.accept(Component.translatable("tfc.jade.smoke_level", state.getValue(FirepitBlock.SMOKE_LEVEL)));
            }
            if (firepit.getAsh() > 0)
            {
                itemWithCount(tooltip, new ItemStack(TFCItems.POWDERS.get(Powder.WOOD_ASH).get(), firepit.getAsh()));
            }
            if (firepit instanceof PotBlockEntity pot)
            {
                if (pot.shouldRenderAsBoiling())
                {
                    tooltip.accept(Component.translatable("tfc.tooltip.pot_boiling"));
                }
                else if (pot.getOutput() != null && !pot.getOutput().isEmpty())
                {
                    tooltip.accept(Component.translatable("tfc.tooltip.pot_finished"));

                    final BlockEntityTooltip tt = pot.getOutput().getTooltip();
                    if (tt != null)
                    {
                        tt.display(level, state, pos, entity, tooltip);
                    }
                }
            }
        }
    };

    public static final BlockEntityTooltip FRUIT_TREE_SAPLING = (level, state, pos, entity, tooltip) -> {
        if (entity instanceof TickCounterBlockEntity counter && state.getBlock() instanceof FruitTreeSaplingBlock sapling)
        {
            timeLeft(level, tooltip, (long) (sapling.getTreeGrowthDays() * ICalendar.TICKS_IN_DAY * TFCConfig.SERVER.globalFruitSaplingGrowthModifier.get()) - counter.getTicksSinceUpdate(), Component.translatable("tfc.jade.ready_to_grow"));
        }
    };

    public static final BlockEntityTooltip HOE_OVERLAY = (level, state, pos, entity, tooltip) -> {
        if (state.getBlock() instanceof HoeOverlayBlock overlay)
        {
            if (TFCConfig.CLIENT.showHoeOverlaysInInfoMods.get() && entity != null)
            {
                final BlockPos pos1 = entity.getBlockPos();
                overlay.addHoeOverlayInfo(level, pos1, level.getBlockState(pos1), tooltip, false);
            }
        }
    };

    public static final BlockEntityTooltip LAMP = (level, state, pos, entity, tooltip) -> {
        if (entity instanceof LampBlockEntity lamp && state.getBlock() instanceof LampBlock)
        {
            final LampFuel fuel = lamp.getFuel();
            if (fuel != null)
            {
                if (fuel.burnRate() != -1)
                {
                    tooltip.accept(Component.translatable("tfc.jade.burn_rate", fuel.burnRate()));
                }
                if (state.getValue(LampBlock.LIT))
                {
                    if (fuel.burnRate() == -1)
                    {
                        tooltip.accept(Component.translatable("tfc.jade.burn_forever"));
                    }
                    else
                    {
                        final IFluidHandler fluidHandler = Helpers.getCapability(Capabilities.FluidHandler.BLOCK, lamp);
                        if (fluidHandler != null)
                        {
                            final int fluid = fluidHandler.getFluidInTank(0).getAmount();
                            if (fluid > 0)
                            {
                                // ticks / mB * mB = ticks
                                timeLeft(level, tooltip, (long) fluid * fuel.burnRate());
                            }
                        }
                    }
                }
            }
        }
    };

    public static final BlockEntityTooltip NEST_BOX = (level, state, pos, entity, tooltip) -> {
        if (entity instanceof NestBoxBlockEntity nest)
        {
            for (int i = 0; i < nest.getInventory().getSlots(); i++)
            {
                final ItemStack stack = nest.getInventory().getStackInSlot(i);
                final @Nullable EggComponent egg = stack.get(TFCComponents.EGG);
                if (egg != null)
                {
                    tooltip.accept(stack.getHoverName());
                    egg.addTooltipInfo(tooltip);
                }
            }
        }
    };

    public static final BlockEntityTooltip PIT_KILN_INTERNAL = (level, state, pos, entity, tooltip) -> pitKiln(level, pos, tooltip);
    public static final BlockEntityTooltip PIT_KILN_ABOVE = (level, state, pos, entity, tooltip) -> pitKiln(level, pos.below(), tooltip);

    public static final BlockEntityTooltip POWDER_KEG = (level, state, pos, entity, tooltip) -> {
        if (entity instanceof PowderkegBlockEntity keg)
        {
            tooltip.accept(Component.translatable("tfc.jade.explosion_strength", PowderkegBlockEntity.getStrength(keg)));
        }
    };

    public static final BlockEntityTooltip SAPLING = (level, state, pos, entity, tooltip) -> {
        if (entity instanceof TickCounterBlockEntity counter && state.getBlock() instanceof TFCSaplingBlock sapling)
        {
            timeLeft(level, tooltip, (long) (sapling.getDaysToGrow() * ICalendar.TICKS_IN_DAY * TFCConfig.SERVER.globalSaplingGrowthModifier.get()) - counter.getTicksSinceUpdate(), Component.translatable("tfc.jade.ready_to_grow"));
        }
    };

    public static final BlockEntityTooltip TORCH = tickCounter(TFCConfig.SERVER.torchTicks);

    public static final BlockEntityTooltip CANDLE = tickCounter(TFCConfig.SERVER.candleTicks);

    public static final BlockEntityTooltip JACK_O_LANTERN = tickCounter(TFCConfig.SERVER.jackOLanternTicks);

    public static final BlockEntityTooltip MUD_BRICKS = (level, state, pos, entity, tooltip) -> {
        if (entity instanceof TickCounterBlockEntity counter && state.getBlock() instanceof DryingBricksBlock)
        {
            if (state.getValue(DryingBricksBlock.DRIED))
            {
                tooltip.accept(Component.translatable("tfc.jade.dried_mud_bricks"));
            }
            else
            {
                if (level.isRainingAt(entity.getBlockPos().above()))
                {
                    tooltip.accept(Component.translatable("tfc.jade.raining_mud_bricks").withStyle(ChatFormatting.BLUE));
                }
                else
                {
                    timeLeft(level, tooltip, TFCConfig.SERVER.mudBricksTicks.get() - counter.getTicksSinceUpdate(), Component.translatable("tfc.jade.mud_bricks_nearly_done"));
                }
            }
        }
    };

    public static final BlockEntityTooltip DECAYING = (level, state, pos, entity, tooltip) -> {
        if (entity instanceof DecayingBlockEntity decaying)
        {
            final ItemStack stack = decaying.getStack();
            tooltip.accept(stack.getHoverName());
            FoodCapability.addTooltipInfo(stack, tooltip);
        }
    };

    public static final BlockEntityTooltip LOOM = (level, state, pos, entity, tooltip) -> {
        if (entity instanceof LoomBlockEntity loom)
        {
            final LoomRecipe recipe = loom.getRecipe();
            if (recipe != null)
            {
                tooltip.accept(Component.translatable("tfc.jade.loom_progress", loom.getProgress(), recipe.getStepCount(), recipe.getResultItem(level.registryAccess()).getDisplayName()));
            }
        }
    };

    private static void pitKiln(Level level, BlockPos pos, Consumer<Component> tooltip)
    {
        final BlockState state = level.getBlockState(pos);
        if (level.getBlockEntity(pos) instanceof PitKilnBlockEntity kiln && state.getBlock() instanceof PitKilnBlock)
        {
            if (state.getValue(PitKilnBlock.STAGE) == PitKilnBlock.LIT)
            {
                timeLeft(level, tooltip, kiln.getTicksLeft());
            }
            else
            {
                tooltip.accept(Component.translatable("tfc.jade.straws", kiln.getStraws().stream().filter(s1 -> !s1.isEmpty()).toList().size()));
                tooltip.accept(Component.translatable("tfc.jade.logs", kiln.getLogs().stream().filter(s -> !s.isEmpty()).toList().size()));
            }
        }
    }

    public static void itemWithCount(Consumer<Component> tooltip, ItemStack stack)
    {
        tooltip.accept(Component.literal(String.valueOf(stack.getCount())).append("x ").append(stack.getHoverName()));
    }

    public static void heat(Consumer<Component> tooltip, float temperature)
    {
        final MutableComponent heat = TFCConfig.CLIENT.heatTooltipStyle.get().formatColored(temperature);
        if (heat != null)
        {
            tooltip.accept(heat);
        }
    }

    public static void timeLeft(Level level, Consumer<Component> tooltip, long ticks)
    {
        timeLeft(level, tooltip, ticks, null);
    }

    public static void timeLeft(Level level, Consumer<Component> tooltip, long ticks, @Nullable Component ifNegative)
    {
        if (ticks > 0)
        {
            tooltip.accept(Component.translatable("tfc.jade.time_left", Calendars.get(level).getTimeDelta(ticks)));
        }
        else if (ifNegative != null)
        {
            tooltip.accept(ifNegative);
        }
    }

    public static BlockEntityTooltip tickCounter(Supplier<Integer> totalTicks)
    {
        return (level, state, pos, entity, tooltip) -> {
            if (entity instanceof TickCounterBlockEntity counter)
            {
                timeLeft(level, tooltip, totalTicks.get() - counter.getTicksSinceUpdate());
            }
        };
    }

}
