/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.api.capability.damage;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;
import javax.annotation.Nullable;

import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import net.dries007.tfc.TerraFirmaCraft;
import net.dries007.tfc.api.capability.DumbStorage;
import net.dries007.tfc.objects.inventory.ingredient.IIngredient;

import static net.dries007.tfc.Constants.GSON;
import static net.dries007.tfc.TerraFirmaCraft.MOD_ID;

public final class CapabilityDamageResistance
{
    public static final ResourceLocation KEY = new ResourceLocation(MOD_ID, "damage_resistance");
    public static final Map<IIngredient<ItemStack>, Supplier<ICapabilityProvider>> CUSTOM_ARMOR = new HashMap<>(); //Used inside CT, set custom IDamageResistance for armor items outside TFC
    public static final Map<String, Supplier<ICapabilityProvider>> ENTITY_RESISTANCE = new HashMap<>(); // Map entities -> Capability to damage resistance
    @CapabilityInject(IDamageResistance.class)
    public static Capability<IDamageResistance> CAPABILITY;

    public static void preInit()
    {
        CapabilityManager.INSTANCE.register(IDamageResistance.class, new DumbStorage<>(), () -> new IDamageResistance() {});
    }

    /**
     * Output to log
     */
    public static void postInit()
    {
        TerraFirmaCraft.getLog().info("Entity resistance data initialized, loaded a total of {} resistance configurations", ENTITY_RESISTANCE.size());
    }

    /**
     * Read json data and load entities damage resistances from it
     *
     * @param jsonElements the json elements to read
     */
    public static void readFile(Set<Map.Entry<String, JsonElement>> jsonElements)
    {
        for (Map.Entry<String, JsonElement> entry : jsonElements)
        {
            try
            {
                String entityName = entry.getKey();
                if ("#loader".equals(entityName)) continue; // Skip loader
                DamageResistance resistance = GSON.fromJson(entry.getValue(), DamageResistance.class);

                ENTITY_RESISTANCE.put(entityName, () -> resistance);
            }
            catch (JsonParseException e)
            {
                TerraFirmaCraft.getLog().error("An entity resistance is specified incorrectly! Skipping.");
                TerraFirmaCraft.getLog().error("Error: ", e);
            }
        }
    }

    @Nullable
    public static ICapabilityProvider getCustomDamageResistance(ItemStack stack)
    {
        Set<IIngredient<ItemStack>> itemArmorSet = CUSTOM_ARMOR.keySet();
        for (IIngredient<ItemStack> ingredient : itemArmorSet)
        {
            if (ingredient.testIgnoreCount(stack))
            {
                return CUSTOM_ARMOR.get(ingredient).get();
            }
        }
        return null;
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
                if (ENTITY_RESISTANCE.containsKey(entityTypeName))
                {
                    event.addCapability(KEY, ENTITY_RESISTANCE.get(entityTypeName).get());
                }
            }
        }
    }
}
