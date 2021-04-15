/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.container;

import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.util.text.TranslationTextComponent;

import net.dries007.tfc.util.Helpers;

/**
 * {@link INamedContainerProvider} for static screen / container pairs that are not attached to a TE or other object which makes sense to implement this on.
 */
public class TFCContainerProviders
{
    public static final INamedContainerProvider CALENDAR = Helpers.createNamedContainerProvider(new TranslationTextComponent("tfc.screen.calendar"), (windowId, inv, player) -> new SimpleContainer(TFCContainerTypes.CALENDAR.get(), windowId, player.inventory));
    public static final INamedContainerProvider NUTRITION = Helpers.createNamedContainerProvider(new TranslationTextComponent("tfc.screen.nutrition"), (windowId, inv, player) -> new SimpleContainer(TFCContainerTypes.NUTRITION.get(), windowId, player.inventory));
    public static final INamedContainerProvider CLIMATE = Helpers.createNamedContainerProvider(new TranslationTextComponent("tfc.screen.climate"), (windowId, inv, player) -> new SimpleContainer(TFCContainerTypes.CLIMATE.get(), windowId, player.inventory));
}