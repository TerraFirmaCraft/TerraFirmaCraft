/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.compat.waila;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

import mcjty.theoneprobe.api.ITheOneProbe;
import net.dries007.tfc.compat.waila.interfaces.TOPBlockInterface;
import net.dries007.tfc.compat.waila.interfaces.TOPEntityInterface;
import net.dries007.tfc.compat.waila.providers.*;

public class TOPPlugin implements Function<ITheOneProbe, Void>
{
    public static final List<TOPBlockInterface> TOP_BLOCK_INTERFACES = Arrays.asList(
        new TOPBlockInterface(new BarrelProvider()),
        new TOPBlockInterface(new BerryBushProvider()),
        new TOPBlockInterface(new BlastFurnaceProvider()),
        new TOPBlockInterface(new BloomeryProvider()),
        new TOPBlockInterface(new LampProvider()),
        new TOPBlockInterface(new CropProvider()),
        new TOPBlockInterface(new CrucibleProvider()),
        new TOPBlockInterface(new FruitTreeProvider()),
        new TOPBlockInterface(new OreProvider()),
        new TOPBlockInterface(new PitKilnProvider()),
        new TOPBlockInterface(new PlacedItemProvider()),
        new TOPBlockInterface(new InfoProvider()),
        new TOPBlockInterface(new TreeProvider())
    );

    public static final List<TOPEntityInterface> TOP_ENTITY_INTERFACES = Collections.singletonList(
        new TOPEntityInterface(new AnimalProvider())
    );

    @Override
    public Void apply(ITheOneProbe probe)
    {
        for (TOPBlockInterface blockInterface : TOP_BLOCK_INTERFACES)
        {
            probe.registerProvider(blockInterface);
            if (blockInterface.overridesHeadInfo())
            {
                probe.registerBlockDisplayOverride(blockInterface);
            }
        }
        for (TOPEntityInterface entityInterface : TOP_ENTITY_INTERFACES)
        {
            probe.registerEntityProvider(entityInterface);
            if (entityInterface.overridesHeadInfo())
            {
                probe.registerEntityDisplayOverride(entityInterface);
            }
        }
        return null;
    }
}
