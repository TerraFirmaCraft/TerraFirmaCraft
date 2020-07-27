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
    public static ResourceLocation ANIMALS_BEAR; // Bear, Grizzly, Black, Polar
    public static ResourceLocation ANIMALS_CHICKEN;
    public static ResourceLocation ANIMALS_COW; // Cow, Yak, Zebu
    public static ResourceLocation ANIMALS_DEER; // Deer, Gazelle, Wildebeest
    public static ResourceLocation ANIMALS_PHEASANT; //Turkey, Pheasant
    public static ResourceLocation ANIMALS_PIG;
    public static ResourceLocation ANIMALS_SHEEP;
    public static ResourceLocation ANIMALS_RABBIT; //Hare, Rabbit
    public static ResourceLocation ANIMALS_WOLF; // Wolf, Dire Wolf, Jackal, Coyote
    public static ResourceLocation ANIMALS_HORSE; //Horse, Donkey
    public static ResourceLocation ANIMALS_ALPACA;
    public static ResourceLocation ANIMALS_DUCK; // Grouse, Duck, Quail
    public static ResourceLocation ANIMALS_GOAT;
    public static ResourceLocation ANIMALS_CAMEL;
    public static ResourceLocation ANIMALS_GRAN_FELINE; // Lion, Panther, Cougar, Sabertooth
    public static ResourceLocation ANIMALS_LLAMA;
    public static ResourceLocation ANIMALS_OCELOT;
    public static ResourceLocation ANIMALS_SQUID;
    public static ResourceLocation ANIMALS_PARROT;
    public static ResourceLocation ANIMALS_HYENA;
    public static ResourceLocation ANIMALS_MUSKOX;

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
        ANIMALS_ALPACA = register("animals/alpaca");
        ANIMALS_DUCK = register("animals/duck");
        ANIMALS_GOAT = register("animals/goat");
        ANIMALS_CAMEL = register("animals/camel");
        ANIMALS_GRAN_FELINE = register("animals/gran_feline");
        ANIMALS_LLAMA = register("animals/llama");
        ANIMALS_OCELOT = register("animals/ocelot");
        ANIMALS_SQUID = register("animals/squid");
        ANIMALS_PARROT = register("animals/parrot");
        ANIMALS_HYENA = register("animals/hyena");
        ANIMALS_MUSKOX = register("animals/muskox");


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
