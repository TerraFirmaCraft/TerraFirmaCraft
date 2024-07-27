/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.capabilities;

import java.util.stream.Stream;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.ItemCapability;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.common.component.TFCComponents;
import net.dries007.tfc.common.component.fluid.FluidContainerHandler;
import net.dries007.tfc.common.component.heat.HeatCapability;
import net.dries007.tfc.common.component.heat.HeatComponent;
import net.dries007.tfc.common.component.heat.HeatDefinition;
import net.dries007.tfc.common.component.heat.IHeat;
import net.dries007.tfc.common.component.mold.IMold;
import net.dries007.tfc.common.component.mold.Mold;
import net.dries007.tfc.common.component.mold.Vessel;
import net.dries007.tfc.common.items.FluidContainerItem;
import net.dries007.tfc.common.items.MoldItem;
import net.dries007.tfc.common.items.TFCItems;
import net.dries007.tfc.common.items.VesselItem;
import net.dries007.tfc.util.Helpers;

public final class TFCCapabilities
{
    /**
     * This is a capability provided by items that have custom heating behavior. It is not necessary to provide for the majority
     * of items that have a capability attached via {@link HeatDefinition}, and users <strong>should not query</strong> this capability
     * directly, rather, use {@link HeatCapability#get(ItemStack)} which handles fallback providers.
     * <p>
     * Any item providing this capability <strong>must</strong> also expose a {@link HeatComponent} via {@link TFCComponents#HEAT}, typically
     * via a default item value.
     *
     * @see HeatCapability#get(ItemStack)
     */
    public static final ItemCapability<IHeat, @Nullable Void> HEAT = ItemCapability.createVoid(Helpers.identifier("heat"), IHeat.class);

    /**
     * This is a capability provided by molds and mold-like containers that wish to expose a joint heat and fluid handler capability. It
     * is mostly provided for convenience for the implementation of mold and vessel interoperability.
     */
    public static final ItemCapability<IMold, @Nullable Void> MOLD = ItemCapability.createVoid(Helpers.identifier("mold"), IMold.class);


    public static void onRegisterCapabilities(RegisterCapabilitiesEvent event)
    {
        final ItemLike[] molds = Stream.concat(
            TFCItems.MOLDS.values().stream(),
            Stream.of(
                TFCItems.BELL_MOLD,
                TFCItems.FIRE_INGOT_MOLD
            )
        ).toArray(ItemLike[]::new);

        event.registerItem(MOLD, TFCCapabilities::forMold, molds);
        event.registerItem(HEAT, TFCCapabilities::forMold, molds);
        event.registerItem(Capabilities.FluidHandler.ITEM, TFCCapabilities::forMold, molds);

        final ItemLike[] vessels = Stream.concat(
            TFCItems.GLAZED_VESSELS.values().stream(),
            Stream.of(TFCItems.VESSEL)
        ).toArray(ItemLike[]::new);

        event.registerItem(MOLD, TFCCapabilities::forVessel, vessels);
        event.registerItem(HEAT, TFCCapabilities::forVessel, vessels);
        event.registerItem(Capabilities.FluidHandler.ITEM, TFCCapabilities::forVessel, vessels);
        event.registerItem(Capabilities.ItemHandler.ITEM, TFCCapabilities::forVessel, vessels);

        event.registerItem(Capabilities.FluidHandler.ITEM, TFCCapabilities::forBucket,
            TFCItems.JUG,
            TFCItems.WOODEN_BUCKET,
            TFCItems.RED_STEEL_BUCKET,
            TFCItems.BLUE_STEEL_BUCKET,
            TFCItems.HEMATITIC_GLASS_BOTTLE,
            TFCItems.OLIVINE_GLASS_BOTTLE,
            TFCItems.SILICA_GLASS_BOTTLE,
            TFCItems.VOLCANIC_GLASS_BOTTLE);
    }

    public static @Nullable IMold forMold(ItemStack stack, @Nullable Void context)
    {
        return stack.getItem() instanceof MoldItem item ? new Mold(stack, item.containerInfo()) : null;
    }

    public static @Nullable Vessel forVessel(ItemStack stack, @Nullable Void context)
    {
        return stack.getItem() instanceof VesselItem item ? new Vessel(stack, item.containerInfo()) : null;
    }

    public static @Nullable FluidContainerHandler forBucket(ItemStack stack, @Nullable Void context)
    {
        return stack.getItem() instanceof FluidContainerItem item ? new FluidContainerHandler(stack, item.containerInfo()) : null;
    }
}
