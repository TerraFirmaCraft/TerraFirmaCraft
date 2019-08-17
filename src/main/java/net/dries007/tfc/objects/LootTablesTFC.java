/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects;

import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.LootPool;
import net.minecraft.world.storage.loot.LootTableList;
import net.minecraftforge.event.LootTableLoadEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import net.dries007.tfc.ConfigTFC;

import static net.dries007.tfc.api.util.TFCConstants.MOD_ID;

@Mod.EventBusSubscriber(modid = MOD_ID)
public class LootTablesTFC
{
    public static ResourceLocation ANIMALS_BEAR;
    public static ResourceLocation ANIMALS_CHICKEN;
    public static ResourceLocation ANIMALS_COW;
    public static ResourceLocation ANIMALS_DEER;
    public static ResourceLocation ANIMALS_PHEASANT;
    public static ResourceLocation ANIMALS_PIG;
    public static ResourceLocation ANIMALS_SHEEP;
    public static ResourceLocation ANIMALS_RABBIT;
    public static ResourceLocation ANIMALS_WOLF;
    public static ResourceLocation ANIMALS_HORSE;

    public static void init()
    {
        ANIMALS_BEAR = register("animals/bear");
        ANIMALS_CHICKEN = register("animals/chicken");
        ANIMALS_COW = register("animals/cow");
        ANIMALS_DEER = register("animals/deer");
        ANIMALS_PHEASANT = register("animals/pheasant");
        ANIMALS_PIG = register("animals/pig");
        ANIMALS_SHEEP = register("animals/sheep");
        ANIMALS_RABBIT = register("animals/rabbit");
        ANIMALS_WOLF = register("animals/wolf");
        ANIMALS_HORSE = register("animals/horse");
    }

    @SubscribeEvent
    public static void onLootTableLoad(LootTableLoadEvent event)
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

    private static ResourceLocation register(String id)
    {
        return LootTableList.register(new ResourceLocation(MOD_ID, id));
    }
}
