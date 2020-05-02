/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.compat.waila;

import java.util.List;
import javax.annotation.Nonnull;

import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextComponentTranslation;

import mcp.mobius.waila.api.*;
import net.dries007.tfc.api.types.Metal;
import net.dries007.tfc.api.types.Ore;
import net.dries007.tfc.api.types.Rock;
import net.dries007.tfc.objects.blocks.BlockPlacedItemFlat;
import net.dries007.tfc.objects.items.metal.ItemSmallOre;
import net.dries007.tfc.objects.items.rock.ItemRock;

@WailaPlugin
public class PlacedItemProvider implements IWailaDataProvider, IWailaPlugin
{
    @Nonnull
    @Override
    public List<String> getWailaBody(ItemStack stack, List<String> currentTooltip, IWailaDataAccessor accessor, IWailaConfigHandler config)
    {
        if (stack.getItem() instanceof ItemSmallOre)
        {
            ItemSmallOre nugget = (ItemSmallOre) stack.getItem();
            Ore ore = nugget.getOre();
            Metal metal = ore.getMetal();
            if (metal != null && config.getConfig("tfc.newtotfc"))
            {
                currentTooltip.add(new TextComponentTranslation("waila.tfc.ore_drop", new TextComponentTranslation(metal.getTranslationKey()).getFormattedText()).getFormattedText());
            }
        }
        if (stack.getItem() instanceof ItemRock)
        {
            ItemRock pebble = (ItemRock) stack.getItem();
            Rock rock = pebble.getRock(stack);
            if (rock.isFluxStone() && config.getConfig("tfc.newtotfc"))
            {
                currentTooltip.add(new TextComponentTranslation("waila.tfc.flux_stone").getFormattedText());
            }

        }
        return currentTooltip;
    }

    @Override
    public void register(IWailaRegistrar registrar)
    {
        registrar.registerBodyProvider(this, BlockPlacedItemFlat.class);
    }
}
