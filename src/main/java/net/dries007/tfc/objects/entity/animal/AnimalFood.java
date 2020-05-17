/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.entity.animal;

import java.util.*;
import javax.annotation.Nullable;

import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

import net.dries007.tfc.TerraFirmaCraft;
import net.dries007.tfc.api.capability.food.CapabilityFood;
import net.dries007.tfc.api.capability.food.IFood;

import static net.dries007.tfc.Constants.GSON;

public class AnimalFood
{
    private static final HashMap<Class<? extends Entity>, AnimalFood> ANIMAL_FOOD_MAP = new HashMap<>();

    @Nullable
    public static AnimalFood get(Class<? extends Entity> animalClass)
    {
        return ANIMAL_FOOD_MAP.get(animalClass);
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
                ResourceLocation key = new ResourceLocation(entityName);
                EntityEntry entityEntry = ForgeRegistries.ENTITIES.getValue(key);
                if (entityEntry == null)
                {
                    throw new JsonParseException("Could not find an entity with registry name " + entityName);
                }
                else if (get(entityEntry.getEntityClass()) != null)
                {
                    throw new JsonParseException("Another json already registered foods for " + entityName);
                }
                AnimalFood animalFood = GSON.fromJson(entry.getValue(), AnimalFood.class);

                ANIMAL_FOOD_MAP.put(entityEntry.getEntityClass(), animalFood);
                TerraFirmaCraft.getLog().info("Registered animal food data for " + entityName);
            }
            catch (JsonParseException e)
            {
                TerraFirmaCraft.getLog().error("Error while reading an entry! Skipping.");
                TerraFirmaCraft.getLog().error("Error: ", e);
            }
        }
    }

    private final List<Ingredient> acceptedFoods;
    private final boolean eatRotten;

    public AnimalFood(boolean eatRotten)
    {
        this.eatRotten = eatRotten;
        acceptedFoods = new ArrayList<>();
    }

    public void addFood(Ingredient ingredient)
    {
        acceptedFoods.add(ingredient);
    }

    public boolean isFood(ItemStack stack)
    {
        for (Ingredient acceptedFood : acceptedFoods)
        {
            if (acceptedFood.apply(stack))
            {
                if (!eatRotten)
                {
                    IFood cap = stack.getCapability(CapabilityFood.CAPABILITY, null);
                    return cap == null || !cap.isRotten();
                }
                return true;
            }
        }
        return false;
    }
}
