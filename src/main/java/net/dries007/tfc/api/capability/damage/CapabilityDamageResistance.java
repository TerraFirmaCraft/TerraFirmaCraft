/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.api.capability.damage;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import net.dries007.tfc.api.capability.DumbStorage;
import net.dries007.tfc.util.Helpers;

import static net.dries007.tfc.api.util.TFCConstants.MOD_ID;

public final class CapabilityDamageResistance
{
    @CapabilityInject(IDamageResistance.class)
    public static final Capability<IDamageResistance> CAPABILITY = Helpers.getNull();
    private static final ResourceLocation KEY = new ResourceLocation(MOD_ID, "damage_resistance");

    public static void preInit()
    {
        CapabilityManager.INSTANCE.register(IDamageResistance.class, new DumbStorage<>(), () -> new IDamageResistance() {});
    }

    @Mod.EventBusSubscriber(modid = MOD_ID)
    public static final class EventHandler
    {
        @SubscribeEvent
        public static void attachEntityCapabilityEvent(AttachCapabilitiesEvent<Entity> event)
        {
            // Give certain entities damage resistance
            ResourceLocation entityType = EntityList.getKey(event.getObject());
            if (entityType != null)
            {
                String entityTypeName = entityType.toString();
                // todo: make this configurable via json or CT or something
                switch (entityTypeName)
                {
                    case "minecraft:skeleton":
                    case "minecraft:wither_skeleton":
                    case "minecraft:stray":
                        event.addCapability(KEY, new DamageResistance(-20, Float.POSITIVE_INFINITY, 20));
                        break;
                    case "minecraft:creeper":
                        event.addCapability(KEY, new DamageResistance(+20, -20, 0));
                        break;
                    case "minecraft:enderman":
                        event.addCapability(KEY, new DamageResistance(-10, -10, -10));
                        break;
                    case "minecraft:zombie":
                    case "minecraft:husk":
                    case "minecraft:zombie_villager":
                        event.addCapability(KEY, new DamageResistance(+20, 0, -20));
                        break;
                }
            }
        }
    }
}
