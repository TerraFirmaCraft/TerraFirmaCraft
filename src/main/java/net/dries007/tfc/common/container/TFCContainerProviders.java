/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.container;

import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.network.chat.TranslatableComponent;

import net.dries007.tfc.client.TFCSounds;
import net.dries007.tfc.common.recipes.TFCRecipeTypes;

/**
 * {@link INamedContainerProvider} for static screen / container pairs that are not attached to a TE or other object which makes sense to implement this on.
 */
public class TFCContainerProviders
{
    public static final MenuProvider CALENDAR = new SimpleMenuProvider((windowId, inv, player) -> new SimpleContainer(TFCContainerTypes.CALENDAR.get(), windowId, player.inventory), new TranslatableComponent("tfc.screen.calendar"));
    public static final MenuProvider NUTRITION = new SimpleMenuProvider((windowId, inv, player) -> new SimpleContainer(TFCContainerTypes.NUTRITION.get(), windowId, player.inventory), new TranslatableComponent("tfc.screen.nutrition"));
    public static final MenuProvider CLIMATE = new SimpleMenuProvider((windowId, inv, player) -> new SimpleContainer(TFCContainerTypes.CLIMATE.get(), windowId, player.inventory), new TranslatableComponent("tfc.screen.climate"));
    public static final MenuProvider CLAY_KNAPPING = new SimpleMenuProvider((windowId, inv, player) -> new KnappingContainer(TFCContainerTypes.CLAY_KNAPPING.get(), TFCRecipeTypes.CLAY_KNAPPING, windowId, player.inventory, 5, true, true, TFCSounds.KNAP_CLAY.get()), new TranslatableComponent("tfc.screen.clay_knapping"));
    public static final MenuProvider FIRE_CLAY_KNAPPING = new SimpleMenuProvider((windowId, inv, player) -> new KnappingContainer(TFCContainerTypes.FIRE_CLAY_KNAPPING.get(), TFCRecipeTypes.FIRE_CLAY_KNAPPING, windowId, player.inventory,5, true, true, TFCSounds.KNAP_CLAY.get()), new TranslatableComponent("tfc.screen.fire_clay_knapping"));
    public static final MenuProvider LEATHER_KNAPPING = new SimpleMenuProvider((windowId, inv, player) -> new LeatherKnappingContainer(TFCContainerTypes.LEATHER_KNAPPING.get(), TFCRecipeTypes.LEATHER_KNAPPING, windowId, player.inventory, 1, false, false, TFCSounds.KNAP_LEATHER.get()), new TranslatableComponent("tfc.screen.leather_knapping"));
    public static final MenuProvider ROCK_KNAPPING = new SimpleMenuProvider((windowId, inv, player) -> new KnappingContainer(TFCContainerTypes.ROCK_KNAPPING.get(), TFCRecipeTypes.ROCK_KNAPPING, windowId, player.inventory, 1, false, false, TFCSounds.KNAP_STONE.get()), new TranslatableComponent("tfc.screen.rock_knapping"));
}