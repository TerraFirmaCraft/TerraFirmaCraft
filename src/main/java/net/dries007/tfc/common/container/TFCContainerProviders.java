/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.container;

import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleMenuProvider;

import net.dries007.tfc.util.Helpers;

/**
 * {@link MenuProvider} for static screen / container pairs that are not attached to a block entity or other object which makes sense to implement this on.
 */
public class TFCContainerProviders
{
    public static final MenuProvider CALENDAR = new SimpleMenuProvider((windowId, inv, player) -> Container.create(TFCContainerTypes.CALENDAR.get(), windowId, player.getInventory()), Helpers.translatable("tfc.screen.calendar"));
    public static final MenuProvider NUTRITION = new SimpleMenuProvider((windowId, inv, player) -> Container.create(TFCContainerTypes.NUTRITION.get(), windowId, player.getInventory()), Helpers.translatable("tfc.screen.nutrition"));
    public static final MenuProvider CLIMATE = new SimpleMenuProvider((windowId, inv, player) -> Container.create(TFCContainerTypes.CLIMATE.get(), windowId, player.getInventory()), Helpers.translatable("tfc.screen.climate"));
    public static final MenuProvider SALAD = new SimpleMenuProvider((windowId, inv, player) -> SaladContainer.create(windowId, inv), Helpers.translatable("tfc.tooltip.salad"));

    public static final ItemStackContainerProvider CLAY_KNAPPING = new ItemStackContainerProvider(KnappingContainer::createClay, Helpers.translatable("tfc.screen.clay_knapping"));
    public static final ItemStackContainerProvider FIRE_CLAY_KNAPPING = new ItemStackContainerProvider(KnappingContainer::createFireClay, Helpers.translatable("tfc.screen.fire_clay_knapping"));
    public static final ItemStackContainerProvider LEATHER_KNAPPING = new ItemStackContainerProvider(KnappingContainer::createLeather, Helpers.translatable("tfc.screen.leather_knapping"));
    public static final ItemStackContainerProvider ROCK_KNAPPING = new ItemStackContainerProvider(KnappingContainer::createRock, Helpers.translatable("tfc.screen.rock_knapping"));

    public static final ItemStackContainerProvider SMALL_VESSEL = new ItemStackContainerProvider(SmallVesselInventoryContainer::create);
    public static final ItemStackContainerProvider MOLD_LIKE_ALLOY = new ItemStackContainerProvider(MoldLikeAlloyContainer::create);
}