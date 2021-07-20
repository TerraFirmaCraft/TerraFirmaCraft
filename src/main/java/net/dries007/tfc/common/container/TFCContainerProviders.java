/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.container;

import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.inventory.container.SimpleNamedContainerProvider;
import net.minecraft.util.text.TranslationTextComponent;

import net.dries007.tfc.common.recipes.TFCRecipeTypes;

/**
 * {@link INamedContainerProvider} for static screen / container pairs that are not attached to a TE or other object which makes sense to implement this on.
 */
public class TFCContainerProviders
{
    public static final INamedContainerProvider CALENDAR = new SimpleNamedContainerProvider((windowId, inv, player) -> new SimpleContainer(TFCContainerTypes.CALENDAR.get(), windowId, player.inventory), new TranslationTextComponent("tfc.screen.calendar"));
    public static final INamedContainerProvider NUTRITION = new SimpleNamedContainerProvider((windowId, inv, player) -> new SimpleContainer(TFCContainerTypes.NUTRITION.get(), windowId, player.inventory), new TranslationTextComponent("tfc.screen.nutrition"));
    public static final INamedContainerProvider CLIMATE = new SimpleNamedContainerProvider((windowId, inv, player) -> new SimpleContainer(TFCContainerTypes.CLIMATE.get(), windowId, player.inventory), new TranslationTextComponent("tfc.screen.climate"));
    public static final INamedContainerProvider CLAY_KNAPPING = new SimpleNamedContainerProvider((windowId, inv, player) -> new KnappingContainer(TFCContainerTypes.CLAY_KNAPPING.get(), TFCRecipeTypes.CLAY_KNAPPING, windowId, player.inventory, 5, true, true, false), new TranslationTextComponent("tfc.screen.clay_knapping"));
    public static final INamedContainerProvider FIRE_CLAY_KNAPPING = new SimpleNamedContainerProvider((windowId, inv, player) -> new KnappingContainer(TFCContainerTypes.FIRE_CLAY_KNAPPING.get(), TFCRecipeTypes.FIRE_CLAY_KNAPPING, windowId, player.inventory,5, true, true, false), new TranslationTextComponent("tfc.screen.fire_clay_knapping"));
    public static final INamedContainerProvider LEATHER_KNAPPING = new SimpleNamedContainerProvider((windowId, inv, player) -> new KnappingContainer(TFCContainerTypes.LEATHER_KNAPPING.get(), TFCRecipeTypes.LEATHER_KNAPPING, windowId, player.inventory, 1, false, false, true), new TranslationTextComponent("tfc.screen.leather_knapping"));
    public static final INamedContainerProvider ROCK_KNAPPING = new SimpleNamedContainerProvider((windowId, inv, player) -> new KnappingContainer(TFCContainerTypes.ROCK_KNAPPING.get(), TFCRecipeTypes.ROCK_KNAPPING, windowId, player.inventory, 1, false, false, false), new TranslationTextComponent("tfc.screen.rock_knapping"));
}