/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.compat.emi.recipe;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.render.EmiTexture;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.TextWidget;
import dev.emi.emi.api.widget.WidgetHolder;

import net.dries007.tfc.util.Helpers;

import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.compat.emi.EmiHelpers;
import net.dries007.tfc.compat.emi.EmiIntegration;
import net.dries007.tfc.config.TFCConfig;
import net.dries007.tfc.util.data.LampFuel;

public class EmiLampFuelRecipe implements EmiRecipe, ComparableRecipe
{

    private static final ResourceLocation ICONS = Helpers.identifier("textures/gui/jei/icons.png");

    private static final EmiTexture LAMP_BACKGROUND = new EmiTexture(ICONS, 0, 48, 20, 20);
    private static final EmiTexture LAMP_FOREGROUND = new EmiTexture(ICONS, 20, 48, 20, 20);

    private final LampFuel fuel;
    private final ResourceLocation id;
    private final EmiIngredient fluidInput;
    private final EmiIngredient lampInput;

    private static final int WIDTH = 140;
    private static final int HEIGHT = 30;

    public EmiLampFuelRecipe(ResourceLocation id, LampFuel fuel)
    {
        this.fuel = fuel;
        this.id = id;
        this.fluidInput = EmiIngredient.of(Arrays.stream(fuel.fluid().getStacks()).map(s -> EmiStack.of(s.getFluid())).toList());
        this.lampInput = EmiHelpers.toIngredient(fuel.lamps());
    }

    @Override
    public EmiRecipeCategory getCategory()
    {
        return EmiIntegration.LAMP_FUEL;
    }

    @Override
    public @Nullable ResourceLocation getId()
    {
        return id;
    }

    @Override
    public List<EmiIngredient> getInputs()
    {
        return List.of(fluidInput, lampInput);
    }

    @Override
    public List<EmiStack> getOutputs()
    {
        return List.of();
    }

    @Override
    public int getDisplayWidth()
    {
        return WIDTH;
    }

    @Override
    public int getDisplayHeight()
    {
        return HEIGHT;
    }

    @Override
    public void addWidgets(WidgetHolder widgets)
    {
        int x = 2;
        int y = 5;

        widgets.addSlot(fluidInput, x, y);
        x += 20;

        widgets.addTexture(LAMP_BACKGROUND, x, y);

        if (fuel.burnRate() <= 0)
        {
            // Negative rate shows the foreground image permanently.
            widgets.addTexture(LAMP_FOREGROUND, x, y);
        }
        else
        {
            // Animated Foreground Lamp.
            widgets.addAnimatedTexture(LAMP_FOREGROUND, x, y, fuel.burnRate() * 5, false, true, true);
        }
        x += 22;

        widgets.addSlot(lampInput, x, y);
        x += 22;

        int lampCapacity = TFCConfig.SERVER.lampCapacity.get();
        int secondsPerMb = fuel.burnRate() / 20;
        int daysPerLamp = fuel.burnRate() * lampCapacity / 24000;
        // If burn rate is negative, show "∞" for both burn time and burn days.
        Object burnTime = secondsPerMb <= 0 ? "∞" : secondsPerMb;
        Object burnDays = daysPerLamp <= 0 ? "∞" : daysPerLamp;

        widgets.add(new TextWidget(Component.translatable("tfc.jei.lamp_fuel.burn_rate", burnTime).getVisualOrderText(), x, y * 2, 0xFFFFFF, true)
        {
            @Override
            public List<ClientTooltipComponent> getTooltip(int mouseX, int mouseY)
            {
                return List.of(ClientTooltipComponent.create(Component.translatable("tfc.jei.lamp_fuel.days", burnDays, lampCapacity).getVisualOrderText()));
            }
        });
    }

    @Override
    public int compareTo(EmiRecipe other)
    {
        if (other instanceof EmiLampFuelRecipe otherFuel)
        {
            return fuel.burnRate() - otherFuel.fuel.burnRate();
        }
        return id.compareTo(Objects.requireNonNull(other.getId()));
    }
}
