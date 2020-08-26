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
    public static ResourceLocation ANIMALS_BLACK_BEAR;
    public static ResourceLocation ANIMALS_GRIZZLY_BEAR;
    public static ResourceLocation ANIMALS_POLAR_BEAR;
    public static ResourceLocation ANIMALS_CHICKEN;
    public static ResourceLocation ANIMALS_COW;
    public static ResourceLocation ANIMALS_DEER;
    public static ResourceLocation ANIMALS_PHEASANT;
    public static ResourceLocation ANIMALS_PIG;
    public static ResourceLocation ANIMALS_SHEEP;
    public static ResourceLocation ANIMALS_RABBIT;
    public static ResourceLocation ANIMALS_WOLF;
    public static ResourceLocation ANIMALS_HORSE;
    public static ResourceLocation ANIMALS_ALPACA;
    public static ResourceLocation ANIMALS_DUCK;
    public static ResourceLocation ANIMALS_GOAT;
    public static ResourceLocation ANIMALS_CAMEL;
    public static ResourceLocation ANIMALS_COUGAR;
    public static ResourceLocation ANIMALS_LLAMA;
    public static ResourceLocation ANIMALS_OCELOT;
    public static ResourceLocation ANIMALS_SQUID;
    public static ResourceLocation ANIMALS_PARROT;
    public static ResourceLocation ANIMALS_HYENA;
    public static ResourceLocation ANIMALS_MUSKOX;
    public static ResourceLocation ANIMALS_BOAR;
    public static ResourceLocation ANIMALS_COYOTE;
    public static ResourceLocation ANIMALS_DIREWOLF;
    public static ResourceLocation ANIMALS_DONKEY;
    public static ResourceLocation ANIMALS_GAZELLE;
    public static ResourceLocation ANIMALS_GROUSE;
    public static ResourceLocation ANIMALS_HARE;
    public static ResourceLocation ANIMALS_JACKAL;
    public static ResourceLocation ANIMALS_LION;
    public static ResourceLocation ANIMALS_MONGOOSE;
    public static ResourceLocation ANIMALS_MULE;
    public static ResourceLocation ANIMALS_PANTHER;
    public static ResourceLocation ANIMALS_QUAIL;
    public static ResourceLocation ANIMALS_SABERTOOTH;
    public static ResourceLocation ANIMALS_TURKEY;
    public static ResourceLocation ANIMALS_WILDEBEEST;
    public static ResourceLocation ANIMALS_YAK;
    public static ResourceLocation ANIMALS_ZEBU;

    public static void init()
    {
        ANIMALS_BLACK_BEAR = register("animals/black_bear");
        ANIMALS_GRIZZLY_BEAR = register("animals/grizzly_bear");
        ANIMALS_POLAR_BEAR = register("animals/polar_bear");
        ANIMALS_CHICKEN = register("animals/chicken");
        ANIMALS_COW = register("animals/cow");
        ANIMALS_DEER = register("animals/deer");
        ANIMALS_PHEASANT = register("animals/pheasant");
        ANIMALS_PIG = register("animals/pig");
        ANIMALS_SHEEP = register("animals/sheep");
        ANIMALS_RABBIT = register("animals/rabbit");
        ANIMALS_WOLF = register("animals/wolf");
        ANIMALS_HORSE = register("animals/horse");
        ANIMALS_ALPACA = register("animals/alpaca");
        ANIMALS_DUCK = register("animals/duck");
        ANIMALS_GOAT = register("animals/goat");
        ANIMALS_CAMEL = register("animals/camel");
        ANIMALS_COUGAR = register("animals/cougar");
        ANIMALS_LLAMA = register("animals/llama");
        ANIMALS_OCELOT = register("animals/ocelot");
        ANIMALS_SQUID = register("animals/squid");
        ANIMALS_PARROT = register("animals/parrot");
        ANIMALS_HYENA = register("animals/hyena");
        ANIMALS_MUSKOX = register("animals/muskox");
        ANIMALS_BOAR = register("animals/boar");
        ANIMALS_COYOTE = register("animals/coyote");
        ANIMALS_DIREWOLF = register("animals/direwolf");
        ANIMALS_DONKEY = register("animals/donkey");
        ANIMALS_GAZELLE = register("animals/gazelle");
        ANIMALS_GROUSE = register("animals/grouse");
        ANIMALS_HARE = register("animals/hare");
        ANIMALS_JACKAL = register("animals/jackal");
        ANIMALS_LION = register("animals/lion");
        ANIMALS_MONGOOSE = register("animals/mongoose");
        ANIMALS_MULE = register("animals/mule");
        ANIMALS_PANTHER = register("animals/panther");
        ANIMALS_QUAIL = register("animals/quail");
        ANIMALS_SABERTOOTH = register("animals/sabertooth");
        ANIMALS_TURKEY = register("animals/turkey");
        ANIMALS_WILDEBEEST = register("animals/wildebeest");
        ANIMALS_YAK = register("animals/yak");
        ANIMALS_ZEBU = register("animals/zebu");

        // Loot function for skill drop multiplier
        LootFunctionManager.registerFunction(new ApplySimpleSkill.Serializer(new ResourceLocation(MOD_ID, "apply_skill")));
    }

    @SubscribeEvent
    public static void onLootTableLoad(LootTableLoadEvent event)
    {
        if (ConfigTFC.General.OVERRIDES.removeVanillaLoots)
        {
            // The pool with carrots, potatoes, and iron ingots
            remove(event, "minecraft:entities/zombie_villager", "pool1");
            remove(event, "minecraft:entities/zombie", "pool1");
            remove(event, "minecraft:entities/husk", "pool1");
        }

        // Add calamari to squid's loot table
        if ("minecraft:entities/squid".equals(event.getName().toString()))
        {
            event.getTable().addPool(event.getLootTableManager().getLootTableFromLocation(ANIMALS_SQUID).getPool("roll1"));
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
