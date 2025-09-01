/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.client.screen;

import net.minecraft.client.Minecraft;
import net.minecraft.client.OptionInstance;
import net.minecraft.client.Options;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.layouts.FrameLayout;
import net.minecraft.client.gui.layouts.GridLayout;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.worldselection.CreateWorldScreen;
import net.minecraft.client.gui.screens.worldselection.WorldCreationContext;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.chunk.ChunkGenerator;

import net.dries007.tfc.world.ChunkGeneratorExtension;
import net.dries007.tfc.world.settings.Settings;

@SuppressWarnings("NotNullFieldNotInitialized")
public class CreateTFCWorldScreen extends Screen
{
    private static OptionInstance<Double> constOption(String caption, double defaultValue)
    {
        return new OptionInstance<>(caption, OptionInstance.noTooltip(),
            (text, value) -> (value > 0.49 && value < 0.51) ?
                Options.genericValueLabel(text, CommonComponents.OPTION_OFF) :
                Component.translatable("options.percent_value", text, (int)((value - 0.5) * 200.0)),
            OptionInstance.UnitDouble.INSTANCE, (1.0 + defaultValue) * 0.5, value -> {});
    }

    private static OptionInstance<Double> pctOption(String caption, double defaultValue)
    {
        return new OptionInstance<>(caption, OptionInstance.noTooltip(),
            (text, value) -> Component.translatable("options.percent_value", text, (int)((value - 0.5) * 200.0)),
            OptionInstance.UnitDouble.INSTANCE, defaultValue, value -> {});
    }

    private static OptionInstance<Integer> kmOption(String caption, int min, int max, int defaultValue)
    {
        return new OptionInstance<>(caption, OptionInstance.cachedConstantTooltip(Component.translatable(caption + ".tooltip")), (text, value) -> Options.genericValueLabel(text, Component.translatable("tfc.settings.km", String.format("%.1f", value / 1000.0))), new OptionInstance.IntRange(min, max), defaultValue, value -> {});
    }

    private final CreateWorldScreen parent;
    private final WorldCreationContext context;

    private OptionInstance<Boolean> flatBedrock, finiteContinents;
    private OptionInstance<Integer> spawnDistance, spawnCenterX, spawnCenterZ, temperatureScale, rainfallScale;
    private OptionInstance<Double> temperatureConstant, rainfallConstant, continentalness, grassDensity;

    public CreateTFCWorldScreen(CreateWorldScreen parent, WorldCreationContext context)
    {
        super(Component.translatable("tfc.tooltip.create_world.title"));

        this.parent = parent;
        this.context = context;
    }

    @Override
    public void onClose()
    {
        assert minecraft != null;
        minecraft.setScreen(parent);
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        renderBackground(graphics, mouseX, mouseY, partialTick);
        super.render(graphics, mouseX, mouseY, partialTick);
        graphics.drawCenteredString(font, title, width / 2, 4, 16777215);
    }

    @Override
    protected void init()
    {
        assert minecraft != null;

        final ChunkGenerator generator = context.selectedDimensions().overworld();
        final Settings settings = ((ChunkGeneratorExtension) generator).settings();

        final GridLayout grid = new GridLayout();
        grid.defaultCellSetting().padding(4, 4, 4, 0);
        final GridLayout.RowHelper builder = grid.createRowHelper(2);

        flatBedrock = OptionInstance.createBoolean("tfc.create_world.flat_bedrock", settings.flatBedrock(), value -> {});
        spawnDistance = kmOption("tfc.create_world.spawn_distance", 100, 20_000, settings.spawnDistance());
        spawnCenterX = kmOption("tfc.create_world.spawn_center_x", -20_000, 20_000, settings.spawnCenterX());
        spawnCenterZ = kmOption("tfc.create_world.spawn_center_z", -20_000, 20_000, settings.spawnCenterZ());
        temperatureScale = kmOption("tfc.create_world.temperature_scale", 0, 40_000, settings.temperatureScale());
        rainfallScale = kmOption("tfc.create_world.rainfall_scale", 0, 40_000, settings.rainfallScale());
        temperatureConstant = constOption("tfc.create_world.temperature_constant", settings.temperatureConstant());
        rainfallConstant = constOption("tfc.create_world.rainfall_constant", settings.rainfallConstant());
        continentalness = pctOption("tfc.create_world.continentalness", settings.continentalness());
        grassDensity = pctOption("tfc.create_world.grass_density", settings.continentalness());
        finiteContinents = OptionInstance.createBoolean("tfc.create_world.finite_continents", settings.finiteContinents(), value -> {});
        builder.addChild(smallButton(flatBedrock));
        builder.addChild(smallButton(spawnDistance));
        builder.addChild(smallButton(spawnCenterX));
        builder.addChild(smallButton(spawnCenterZ));
        builder.addChild(smallButton(temperatureScale));
        builder.addChild(smallButton(rainfallScale));
        builder.addChild(smallButton(temperatureConstant));
        builder.addChild(smallButton(rainfallConstant));
        builder.addChild(smallButton(continentalness));
        builder.addChild(smallButton(grassDensity));
        builder.addChild(smallButton(finiteContinents));

        builder.addChild(Button.builder(CommonComponents.GUI_DONE, button -> {
            applySettings();
            minecraft.setScreen(parent);
        }).width(400).build(), 2);
        builder.addChild(Button.builder(CommonComponents.GUI_CANCEL, button -> {
            minecraft.setScreen(parent);
        }).width(400).build(), 2);

        grid.arrangeElements();
        FrameLayout.alignInRectangle(grid, 0, 0, width, height, 0.5f, 0.25f);
        grid.visitWidgets(this::addRenderableWidget);
    }

    private AbstractWidget smallButton(OptionInstance<?> option)
    {
        final AbstractWidget widget = option.createButton(Minecraft.getInstance().options);
        widget.setWidth(200);
        return widget;
    }

    private void applySettings()
    {
        final ChunkGenerator generator = context.selectedDimensions().overworld();
        if (generator instanceof ChunkGeneratorExtension extension)
        {
            extension.applySettings(old -> new Settings(
                flatBedrock.get(),
                spawnDistance.get(),
                spawnCenterX.get(),
                spawnCenterZ.get(),
                0.49 < temperatureConstant.get() && temperatureConstant.get() < 0.51 ? temperatureScale.get() : 0,
                (float) (temperatureConstant.get() * 2.0 - 1.0),
                0.49 < rainfallConstant.get() && rainfallConstant.get() < 0.51 ? rainfallScale.get() : 0,
                (float) (rainfallConstant.get() * 2.0 - 1.0),
                old.rockLayerSettings(),
                continentalness.get().floatValue(),
                grassDensity.get().floatValue(),
                finiteContinents.get()
            ));
        }
    }
}
