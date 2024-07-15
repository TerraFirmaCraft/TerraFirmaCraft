/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.client;

import java.util.Objects;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.RegistryBuilder;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.TerraFirmaCraft;
import net.dries007.tfc.common.capabilities.heat.Heat;
import net.dries007.tfc.common.capabilities.heat.HeatCapability;
import net.dries007.tfc.common.capabilities.heat.IHeat;
import net.dries007.tfc.common.component.EggComponent;
import net.dries007.tfc.common.component.TFCComponents;
import net.dries007.tfc.common.recipes.HeatingRecipe;
import net.dries007.tfc.config.TFCConfig;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.calendar.Calendars;


/**
 * A registry for generic progress bars applied to items based on components. If the type of bar is specific to an item,
 * it should be implemented on the item, not via this.
 */
@SuppressWarnings("unused")
public final class BarSystem
{
    public static final ResourceKey<Registry<Bar>> KEY = ResourceKey.createRegistryKey(Helpers.identifier("bars"));
    public static final Registry<Bar> REGISTRY = new RegistryBuilder<>(KEY).sync(true).create();
    public static final DeferredRegister<Bar> BARS = DeferredRegister.create(KEY, TerraFirmaCraft.MOD_ID);

    public static final DeferredHolder<Bar, Bar> HEAT = register("heat", new Bar() {
        @Override
        public int getBarColor(ItemStack stack)
        {
            final @Nullable IHeat cap = HeatCapability.get(stack);
            if (cap != null && cap.getTemperature() > 0)
            {
                final Heat heat = Heat.getHeat(cap.getTemperature());
                if (heat != null)
                {
                    return Objects.requireNonNull(heat.getColor().getColor());
                }
            }
            return 0;
        }

        @Override
        public boolean isBarVisible(ItemStack stack)
        {
            return TFCConfig.CLIENT.displayItemHeatBars.get() && HeatCapability.isHot(stack);
        }

        @Override
        public int getBarWidth(ItemStack stack)
        {
            final @Nullable IHeat cap = HeatCapability.get(stack);
            if (cap != null)
            {
                final HeatingRecipe recipe = HeatingRecipe.getRecipe(stack);
                if (recipe != null)
                {
                    return Mth.clamp(Math.round(13f * cap.getTemperature() / recipe.getTemperature()), 1, 13);
                }
                return Mth.clamp(Math.round(13f * cap.getTemperature() / Heat.maxVisibleTemperature()), 1, 13);
            }
            return 0;
        }

        @Override
        public ItemStack createDefaultItem(ItemStack stack)
        {
            HeatCapability.setTemperature(stack, 0);
            return stack;
        }
    });

    public static final DeferredHolder<Bar, Bar> EGG = register("egg", new Bar() {
        @Override
        public int getBarColor(ItemStack stack)
        {
            return Objects.requireNonNull(ChatFormatting.GOLD.getColor());
        }

        @Override
        public boolean isBarVisible(ItemStack stack)
        {
            final @Nullable EggComponent egg = stack.get(TFCComponents.EGG);
            return egg != null && egg.fertilized();
        }

        @Override
        public int getBarWidth(ItemStack stack)
        {
            final int maxDays = 8;
            final @Nullable EggComponent egg = stack.get(TFCComponents.EGG);
            if (egg != null)
            {
                final int incubationDays = maxDays - Mth.clamp((int) (egg.hatchDay() - Calendars.CLIENT.getTotalDays()), 0, maxDays);
                return Math.round(13f * incubationDays / maxDays);
            }
            return 0;
        }

        @Override
        public ItemStack createDefaultItem(ItemStack stack)
        {
            stack.set(TFCComponents.EGG, EggComponent.DEFAULT);
            return stack;
        }
    });

    private static DeferredHolder<Bar, Bar> register(String name, Bar bar)
    {
        return BARS.register(name, () -> bar);
    }

    @Nullable
    public static Bar getCustomBar(ItemStack stack)
    {
        if (stack.isEmpty())
        {
            return null;
        }
        for (Bar bar : REGISTRY)
        {
            if (bar.isBarVisible(stack) && (bar.overridesOtherBars() || !bar.createDefaultItem(stack.copy()).isBarVisible()))
            {
                return bar;
            }
        }
        return null;
    }

    public interface Bar
    {
        int WIDTH = 13;

        /**
         * @return an {@code int} color. This is compatible with {@linkplain net.minecraft.ChatFormatting}
         */
        int getBarColor(ItemStack stack);

        /**
         * @return {@code true if the bar should render}
         */
        boolean isBarVisible(ItemStack stack);

        /**
         * @return {@code int} in range [0, {@link #WIDTH}]
         */
        int getBarWidth(ItemStack stack);

        /**
         * On a copy of the item stack, reverses changes that would have caused this bar to show up.
         * Example: for the 'heat' bar, set the item's heat to zero.
         */
        ItemStack createDefaultItem(ItemStack stack);

        /**
         * @return {@code true} if the bar should ignore bars added by vanilla/other mods
         */
        default boolean overridesOtherBars()
        {
            return false;
        }
    }
}
