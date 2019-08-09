/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects;

import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.LootPool;
import net.minecraft.world.storage.loot.LootTableList;
import net.minecraftforge.event.LootTableLoadEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import net.dries007.tfc.ConfigTFC;

import static net.dries007.tfc.api.util.TFCConstants.MOD_ID;

public class LootTablesTFC
{
    public static final ResourceLocation ANIMALS_BEAR = register("animals/bear");
    public static final ResourceLocation ANIMALS_CHICKEN = register("animals/chicken");
    public static final ResourceLocation ANIMALS_COW = register("animals/cow");
    public static final ResourceLocation ANIMALS_DEER = register("animals/deer");
    public static final ResourceLocation ANIMALS_PHEASANT = register("animals/pheasant");
    public static final ResourceLocation ANIMALS_PIG = register("animals/pig");
    public static final ResourceLocation ANIMALS_SHEEP = register("animals/sheep");
    public static final ResourceLocation ANIMALS_RABBIT = register("animals/rabbit");
    public static final ResourceLocation ANIMALS_WOLF = register("animals/wolf");

    private static ResourceLocation register(String id)
    {
        return LootTableList.register(new ResourceLocation(MOD_ID, id));
    }

    private static final LootTablesTFC INSTANCE = new LootTablesTFC();

    public static LootTablesTFC getInstance() { return INSTANCE; }

    @SubscribeEvent
    public void onLootTableLoad(LootTableLoadEvent event)
    {
        LootPool pool = event.getTable().getPool("main");
        //noinspection ConstantConditions - it can be null on non-vanilla pools
        if (ConfigTFC.GENERAL.removeVanillaLoots && pool != null)
        {
            pool.removeEntry("minecraft:potato");
            pool.removeEntry("minecraft:carrot");
            pool.removeEntry("minecraft:wheat");
            pool.removeEntry("minecraft:gold_nugget");
            pool.removeEntry("minecraft:gold_ingot");
            pool.removeEntry("minecraft:iron_ingot");
            pool.removeEntry("minecraft:iron_nugget");
            pool.removeEntry("minecraft:leather");
            pool.removeEntry("minecraft:coal");
            pool.removeEntry("minecraft:diamond");
        }
    }
}
