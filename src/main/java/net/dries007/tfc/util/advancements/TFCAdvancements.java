/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.util.advancements;

import net.minecraft.advancements.CriteriaTriggers;

import net.dries007.tfc.util.Helpers;

public class TFCAdvancements
{
    public static void registerTriggers() { }

    public static final BlockActionTrigger CHISELED = registerBlock("chiseled");
    public static final BlockActionTrigger LIT = registerBlock("lit");
    public static final BlockActionTrigger ROCK_ANVIL = registerBlock("rock_anvil");
    public static final BlockActionTrigger FIREPIT_CREATED = registerBlock("firepit_created");

    public static final GenericTrigger FULL_POWDERKEG = registerGeneric("full_powderkeg");
    public static final GenericTrigger FULL_FERTILIZER = registerGeneric("full_fertilizer");
    public static final GenericTrigger LAVA_LAMP = registerGeneric("lava_lamp");
    public static final GenericTrigger ROTTEN_COMPOST_KILL = registerGeneric("rotten_compost_kill");
    public static final GenericTrigger PRESENT_DAY = registerGeneric("present_day");

    public static final EntityActionTrigger HOOKED_ENTITY = registerEntity("hooked_entity");
    public static final EntityActionTrigger FED_ANIMAL = registerEntity("fed_animal");
    public static final EntityActionTrigger STAB_ENTITY = registerEntity("stab_entity");

    public static BlockActionTrigger registerBlock(String name)
    {
        return CriteriaTriggers.register(new BlockActionTrigger(Helpers.identifier(name)));
    }

    public static GenericTrigger registerGeneric(String name)
    {
        return CriteriaTriggers.register(new GenericTrigger(Helpers.identifier(name)));
    }

    public static EntityActionTrigger registerEntity(String name)
    {
        return CriteriaTriggers.register(new EntityActionTrigger(Helpers.identifier(name)));
    }
}
