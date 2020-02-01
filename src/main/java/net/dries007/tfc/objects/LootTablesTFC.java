/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects;

import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.LootPool;
import net.minecraft.world.storage.loot.LootTableList;
import net.minecraft.world.storage.loot.functions.LootFunctionManager;
import net.minecraftforge.event.LootTableLoadEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import net.dries007.tfc.ConfigTFC;
import net.dries007.tfc.util.loot.ApplySimpleSkill;

import static net.dries007.tfc.TerraFirmaCraft.MOD_ID;

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

        // Loot function for skill drop multiplier
        LootFunctionManager.registerFunction(new ApplySimpleSkill.Serializer(new ResourceLocation(MOD_ID, "apply_skill")));
    }

    @SubscribeEvent
    public static void onLootTableLoad(LootTableLoadEvent event)
    {
        if (ConfigTFC.GENERAL.removeVanillaLoots)
        {
            // The pool with carrots, potatoes, and iron ingots
            remove(event, "minecraft:entities/zombie_villager", "pool1");
            remove(event, "minecraft:entities/zombie", "pool1");
            remove(event, "minecraft:entities/husk", "pool1");
        }
    }

    private static ResourceLocation register(String id)
    {
        return LootTableList.register(new ResourceLocation(MOD_ID, id));
    }

    private static void remove(LootTableLoadEvent event, String tableName, String pool)
    {
        if (tableName.equals(event.getName().toString()))
        {
            event.getTable().removePool(pool);
        }
    }

    private static void remove(LootTableLoadEvent event, String tableName, String poolName, String entry)
    {
        if (tableName.equals(event.getName().toString()))
        {
            LootPool pool = event.getTable().getPool(poolName);
            //noinspection ConstantConditions
            if (pool != null)
            {
                pool.removeEntry(entry);
            }
        }
    }
}
