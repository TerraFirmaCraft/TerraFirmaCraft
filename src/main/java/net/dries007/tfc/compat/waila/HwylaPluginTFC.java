/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.compat.waila;

import java.util.Arrays;
import java.util.List;

import mcp.mobius.waila.api.IWailaPlugin;
import mcp.mobius.waila.api.IWailaRegistrar;
import mcp.mobius.waila.api.WailaPlugin;
import net.dries007.tfc.compat.waila.interfaces.HwylaBlockInterface;
import net.dries007.tfc.compat.waila.interfaces.HwylaEntityInterface;
import net.dries007.tfc.compat.waila.providers.*;

@WailaPlugin
public class HwylaPluginTFC implements IWailaPlugin
{
    public static final List<IWailaPlugin> WAILA_PLUGINS = Arrays.asList(
        new HwylaEntityInterface(new AnimalProvider()),
        new HwylaBlockInterface(new BarrelProvider()),
        new HwylaBlockInterface(new BerryBushProvider()),
        new HwylaBlockInterface(new BlastFurnaceProvider()),
        new HwylaBlockInterface(new BloomeryProvider()),
        new HwylaBlockInterface(new LampProvider()),
        new HwylaBlockInterface(new CropProvider()),
        new HwylaBlockInterface(new CrucibleProvider()),
        new HwylaBlockInterface(new FruitTreeProvider()),
        new HwylaBlockInterface(new OreProvider()),
        new HwylaBlockInterface(new PitKilnProvider()),
        new HwylaBlockInterface(new PlacedItemProvider()),
        new HwylaBlockInterface(new InfoProvider()),
        new HwylaBlockInterface(new TreeProvider())
    );

    @Override
    public void register(IWailaRegistrar registrar)
    {
        for (IWailaPlugin plugin : WAILA_PLUGINS)
        {
            plugin.register(registrar);
        }
    }
}
