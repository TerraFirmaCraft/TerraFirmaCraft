/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.items;

import net.dries007.tfc.api.Gem;
import net.dries007.tfc.api.Metal;
import net.dries007.tfc.api.Rock;
import net.dries007.tfc.api.RockCategory;
import net.dries007.tfc.objects.TFCItemGroup;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.util.Util;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.EnumMap;
import java.util.Map;

import static net.dries007.tfc.TerraFirmaCraft.MOD_ID;

public final class TFCItems
{
    public static final DeferredRegister<Item> ITEMS = new DeferredRegister<>(ForgeRegistries.ITEMS, MOD_ID);

    public static final Map<Gem.Default, Map<Gem.Grade, RegistryObject<Item>>> GEMS = Util.make(new EnumMap<>(Gem.Default.class), map -> {
        for (Gem.Default gem : Gem.Default.values())
        {
            Map<Gem.Grade, RegistryObject<Item>> inner = new EnumMap<>(Gem.Grade.class);
            for(Gem.Grade grade : Gem.Grade.values()) {
                String name = ("gem/" + grade.name().toLowerCase() +"/"+ gem.name()).toLowerCase();
                RegistryObject<Item> item = ITEMS.register(name, () -> new Item(new Item.Properties().group(TFCItemGroup.GEM)));
                inner.put(grade, item);
            }
            map.put(gem, inner);
        }
    });
}
