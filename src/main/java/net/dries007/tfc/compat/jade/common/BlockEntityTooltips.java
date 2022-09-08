/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.compat.jade.common;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import net.dries007.tfc.common.blockentities.*;
import net.dries007.tfc.common.blocks.crop.CropBlock;
import net.dries007.tfc.common.blocks.devices.*;
import net.dries007.tfc.common.blocks.plant.fruit.FruitTreeSaplingBlock;
import net.dries007.tfc.common.blocks.soil.HoeOverlayBlock;
import net.dries007.tfc.common.blocks.wood.TFCSaplingBlock;
import net.dries007.tfc.common.capabilities.Capabilities;
import net.dries007.tfc.common.capabilities.egg.EggCapability;
import net.dries007.tfc.common.capabilities.egg.IEgg;
import net.dries007.tfc.common.capabilities.food.FoodCapability;
import net.dries007.tfc.common.capabilities.heat.HeatCapability;
import net.dries007.tfc.common.recipes.BarrelRecipe;
import net.dries007.tfc.common.recipes.BloomeryRecipe;
import net.dries007.tfc.common.recipes.SoupPotRecipe;
import net.dries007.tfc.config.TFCConfig;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.LampFuel;
import net.dries007.tfc.util.calendar.Calendars;
import net.dries007.tfc.util.calendar.ICalendar;

/**
 * Common tooltips that can be displayed for various block entities via external sources.
 */
public final class BlockEntityTooltips
{
    public static final BlockEntityTooltip BARREL = (level, state, entity, tooltip) -> {
        if (state.getBlock() instanceof BarrelBlock && entity instanceof BarrelBlockEntity barrel)
        {
            if (state.getValue(BarrelBlock.SEALED))
            {
                BarrelRecipe recipe = barrel.getRecipe();
                if (recipe != null)
                {
                    tooltip.accept(recipe.getTranslationComponent());
                    // this is the translation key used in the barrel class, if that changes we should change it in barrel screen too.
                    tooltip.accept(Helpers.translatable("tfc.jade.sealed_date", ICalendar.getTimeAndDate(Calendars.get(level).ticksToCalendarTicks(barrel.getSealedTick()), Calendars.get(level).getCalendarDaysInMonth())));
                }
            }
        }
    };

    public static final BlockEntityTooltip BELLOWS = (level, state, entity, tooltip) -> {
        if (entity instanceof BellowsBlockEntity bellows)
        {
            int pushTicks = bellows.getTicksSincePushed();
            if (pushTicks < 20 && pushTicks > 0)
            {
                if (pushTicks > 10)
                {
                    pushTicks = 20 - pushTicks;
                }

                tooltip.accept(Helpers.translatable("tfc.jade.bellows_" + pushTicks));
            }
        }
    };

    public static final BlockEntityTooltip BLAST_FURNACE = (level, state, entity, tooltip) -> {
        if (entity instanceof BlastFurnaceBlockEntity furnace)
        {
            furnace.getCapability(HeatCapability.BLOCK_CAPABILITY).ifPresent(cap -> heat(tooltip, cap.getTemperature()));

            tooltip.accept(Helpers.translatable("tfc.jade.input_stacks", furnace.getInputCount()));
            tooltip.accept(Helpers.translatable("tfc.jade.catalyst_stacks", furnace.getCatalystCount()));
            tooltip.accept(Helpers.translatable("tfc.jade.fuel_stacks", furnace.getFuelCount()));
        }
    };

    public static final BlockEntityTooltip BLOOMERY = (level, state, entity, tooltip) -> {
        if (entity instanceof BloomeryBlockEntity bloomery && state.getBlock() instanceof BloomeryBlock)
        {
            tooltip.accept(Helpers.translatable("tfc.jade.input_stacks", bloomery.getInputStacks().size()));
            tooltip.accept(Helpers.translatable("tfc.jade.catalyst_stacks", bloomery.getCatalystStacks().size()));
            if (state.getValue(BloomeryBlock.LIT))
            {
                final long ticksLeft = bloomery.getRemainingTicks();
                if (ticksLeft > 0)
                {
                    final BloomeryRecipe recipe = bloomery.getCachedRecipe();
                    if (recipe != null)
                    {
                        tooltip.accept(Helpers.translatable("tfc.jade.time_left", Calendars.get(level).getTimeDelta(ticksLeft)));
                        tooltip.accept(Helpers.translatable("tfc.jade.creating", recipe.getResultItem().getHoverName()));
                    }
                }
            }
        }
    };

    public static final BlockEntityTooltip BLOOM = (level, state, entity, tooltip) -> {
        if (entity instanceof BloomBlockEntity bloom)
        {
            final ItemStack item = bloom.getItem();

            ItemStack displayItem = item.copy();
            displayItem.setCount(bloom.getCount());
            itemWithCount(tooltip, displayItem);

            final List<Component> text = new ArrayList<>();
            item.getCapability(HeatCapability.CAPABILITY).ifPresent(cap -> cap.addTooltipInfo(item, text));
            text.forEach(tooltip);
        }
    };

    public static final BlockEntityTooltip CHARCOAL_FORGE = (level, state, entity, tooltip) -> {
        if (entity instanceof CharcoalForgeBlockEntity forge)
        {
            heat(tooltip, forge.getTemperature());
        }
    };

    public static final BlockEntityTooltip COMPOSTER = (level, state, entity, tooltip) -> {
        if (state.getBlock() instanceof TFCComposterBlock block && entity instanceof ComposterBlockEntity composter)
        {
            if (composter.isRotten())
            {
                tooltip.accept(Helpers.translatable("tfc.composter.rotten"));
            }
            else
            {
                hoeOverlay(level, block, entity, tooltip);
                if (!composter.isReady())
                {
                    tooltip.accept(Calendars.get(level).getTimeDelta(composter.getReadyTicks() - composter.getTicksSinceUpdate()));
                }
            }
        }
    };

    public static final BlockEntityTooltip CROP = (level, state, entity, tooltip) -> {
        if (entity instanceof CropBlockEntity crop && state.getBlock() instanceof CropBlock block)
        {
            hoeOverlay(level, block, entity, tooltip);

            tooltip.accept(Helpers.translatable("tfc.jade.yield", String.format("%.2f", crop.getYield())));
        }
    };

    public static final BlockEntityTooltip CRUCIBLE = (level, state, entity, tooltip) -> {
        if (entity instanceof CrucibleBlockEntity crucible)
        {
            crucible.getCapability(HeatCapability.BLOCK_CAPABILITY).ifPresent(cap -> heat(tooltip, cap.getTemperature()));
        }
    };

    public static final BlockEntityTooltip FIREPIT = (level, state, entity, tooltip) -> {
        if (entity instanceof AbstractFirepitBlockEntity<?> firepit)
        {
            heat(tooltip, firepit.getTemperature());

            if (firepit instanceof PotBlockEntity pot)
            {
                if (pot.shouldRenderAsBoiling())
                {
                    tooltip.accept(Helpers.translatable("tfc.tooltip.pot_boiling"));
                }
                else if (pot.getOutput() != null && !pot.getOutput().isEmpty())
                {
                    tooltip.accept(Helpers.translatable("tfc.tooltip.pot_finished"));

                    if (pot.getOutput() instanceof SoupPotRecipe.SoupOutput soup)
                    {
                        final ItemStack stack = soup.stack();
                        itemWithCount(tooltip, stack);

                        final List<Component> text = new ArrayList<>();
                        stack.getCapability(FoodCapability.CAPABILITY).ifPresent(cap -> cap.addTooltipInfo(stack, text));
                        text.forEach(tooltip);
                    }
                }
            }
        }
    };

    public static final BlockEntityTooltip FRUIT_TREE_SAPLING = (level, state, entity, tooltip) -> {
        if (entity instanceof TickCounterBlockEntity counter && state.getBlock() instanceof FruitTreeSaplingBlock sapling)
        {
            tooltip.accept(Helpers.translatable("tfc.jade.growth", Calendars.get(level).getTimeDelta((long) sapling.getTreeGrowthDays() * ICalendar.TICKS_IN_DAY - counter.getTicksSinceUpdate())));
            hoeOverlay(level, sapling, entity, tooltip);
        }
    };

    public static final BlockEntityTooltip HOE_OVERLAY = (level, state, entity, tooltip) -> {
        if (state.getBlock() instanceof HoeOverlayBlock overlay)
        {
            hoeOverlay(level, overlay, entity, tooltip);
        }
    };

    public static final BlockEntityTooltip LAMP = (level, state, entity, tooltip) -> {
        if (entity instanceof LampBlockEntity lamp && state.getBlock() instanceof LampBlock)
        {
            // todo: this info is not available on the client (?)
            LampFuel fuel = lamp.getFuel();
            if (fuel != null)
            {
                tooltip.accept(Helpers.translatable("tfc.jade.burn_rate", fuel.getBurnRate()));
                if (state.getValue(LampBlock.LIT))
                {
                    if (fuel.getBurnRate() == -1)
                    {
                        tooltip.accept(Helpers.translatable("tfc.jade.burn_forever"));
                    }
                    else
                    {
                        lamp.getCapability(Capabilities.FLUID).ifPresent(cap -> {
                            final int fluid = cap.getFluidInTank(0).getAmount();
                            if (fluid > 0)
                            {
                                // ticks / mB * mB = ticks
                                tooltip.accept(Helpers.translatable("tfc.jade.time_left", Calendars.get(level).getTimeDelta((long) fluid * fuel.getBurnRate())));
                            }
                        });
                    }

                }

            }
        }
    };

    public static final BlockEntityTooltip NEST_BOX = (level, state, entity, tooltip) -> {
        if (entity instanceof NestBoxBlockEntity nest)
        {
            final List<Component> text = new ArrayList<>();
            nest.getCapability(Capabilities.ITEM).ifPresent(inventory -> {
                for (int i = 0; i < inventory.getSlots(); i++)
                {
                    ItemStack stack = inventory.getStackInSlot(i);
                    stack.getCapability(EggCapability.CAPABILITY).filter(IEgg::isFertilized).ifPresent(egg -> {
                        text.add(stack.getHoverName());
                        egg.addTooltipInfo(text);
                    });
                }
            });
            text.forEach(tooltip);
        }
    };

    public static final BlockEntityTooltip PIT_KILN_INTERNAL = (level, state, entity, tooltip) -> pitKiln(level, entity, tooltip, 0);
    public static final BlockEntityTooltip PIT_KILN_ABOVE = (level, state, entity, tooltip) -> pitKiln(level, entity, tooltip, -1);

    public static final BlockEntityTooltip POWDER_KEG = (level, state, entity, tooltip) -> {
        if (entity instanceof PowderkegBlockEntity keg)
        {
            tooltip.accept(Helpers.translatable("tfc.jade.explosion_strength", PowderkegBlockEntity.getStrength(keg)));
        }
    };

    public static final BlockEntityTooltip SAPLING = (level, state, entity, tooltip) -> {
        if (entity instanceof TickCounterBlockEntity counter && state.getBlock() instanceof TFCSaplingBlock sapling)
        {
            tooltip.accept(Helpers.translatable("tfc.jade.growth", Calendars.get(level).getTimeDelta((long) sapling.getDaysToGrow() * ICalendar.TICKS_IN_DAY - counter.getTicksSinceUpdate())));
        }
    };

    private static void pitKiln(Level level, @Nullable BlockEntity entity, Consumer<Component> tooltip, int offset)
    {
        if (entity == null) return;

        final BlockPos pos = entity.getBlockPos().relative(Direction.UP, offset);
        final BlockState state = level.getBlockState(pos);

        if (level.getBlockEntity(pos) instanceof PitKilnBlockEntity kiln && state.getBlock() instanceof PitKilnBlock)
        {
            if (state.getValue(PitKilnBlock.STAGE) == PitKilnBlock.LIT)
            {
                tooltip.accept(Helpers.translatable("tfc.jade.time_left", Calendars.get(level).getTimeDelta(kiln.getTicksLeft())));
            }
            else
            {
                tooltip.accept(Helpers.translatable("tfc.jade.straws", kiln.getStraws().size()));
                tooltip.accept(Helpers.translatable("tfc.jade.logs", kiln.getLogs().size()));
            }
        }
    }

    public static void itemWithCount(Consumer<Component> tooltip, ItemStack stack)
    {
        tooltip.accept(Helpers.literal(String.valueOf(stack.getCount())).append("x ").append(stack.getHoverName()));
    }

    public static void heat(Consumer<Component> tooltip, float temperature)
    {
        final MutableComponent heat = TFCConfig.CLIENT.heatTooltipStyle.get().formatColored(temperature);
        if (heat != null)
        {
            tooltip.accept(heat);
        }
    }

    public static void hoeOverlay(Level level, HoeOverlayBlock block, @Nullable BlockEntity entity, Consumer<Component> tooltip)
    {
        if (TFCConfig.CLIENT.showHoeOverlaysInInfoMods.get() && entity != null)
        {
            final List<Component> text = new ArrayList<>();
            final BlockPos pos = entity.getBlockPos();
            block.addHoeOverlayInfo(level, pos, level.getBlockState(pos), text, false);
            text.forEach(tooltip);
        }
    }
}
