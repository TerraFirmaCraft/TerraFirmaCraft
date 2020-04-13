/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import net.dries007.tfc.api.types.Metal;
import net.dries007.tfc.objects.items.metal.ItemMetal;
import net.dries007.tfc.objects.items.metal.ItemMetalArmor;
import net.dries007.tfc.objects.items.metal.ItemMetalSword;
import net.dries007.tfc.util.collections.WeightedCollection;

/**
 * Just a helper class to store which equipment we should give to some entities
 * If needed, this functionality can be extended via json
 * and if this is done, may as well merge into entity resistance data
 */
public class MonsterEquipment
{
    public static final Map<String, MonsterEquipment> MONSTER_EQUIPMENT_MAP;

    static
    {
        // Add defaults (if this class functionality is extended via json, please move all this data to json too)
        MONSTER_EQUIPMENT_MAP = new HashMap<>();
        Map<ItemStack, Double> weapons = new HashMap<>();
        weapons.put(ItemStack.EMPTY, 0.4);
        weapons.put(new ItemStack(ItemMetal.get(Metal.WROUGHT_IRON, Metal.ItemType.MACE)), 0.2);
        //noinspection ConstantConditions
        weapons.put(new ItemStack(ItemMetalSword.get(Metal.WROUGHT_IRON)), 0.2);
        weapons.put(new ItemStack(ItemMetal.get(Metal.WROUGHT_IRON, Metal.ItemType.KNIFE)), 0.2);
        Map<ItemStack, Double> helmets = new HashMap<>();
        helmets.put(ItemStack.EMPTY, 0.8);
        helmets.put(new ItemStack(ItemMetalArmor.get(Metal.WROUGHT_IRON, Metal.ItemType.HELMET)), 0.2);
        Map<ItemStack, Double> chestplates = new HashMap<>();
        chestplates.put(ItemStack.EMPTY, 0.8);
        chestplates.put(new ItemStack(ItemMetalArmor.get(Metal.WROUGHT_IRON, Metal.ItemType.CHESTPLATE)), 0.2);
        Map<ItemStack, Double> leggings = new HashMap<>();
        leggings.put(ItemStack.EMPTY, 0.8);
        leggings.put(new ItemStack(ItemMetalArmor.get(Metal.WROUGHT_IRON, Metal.ItemType.GREAVES)), 0.2);
        Map<ItemStack, Double> boots = new HashMap<>();
        boots.put(ItemStack.EMPTY, 0.8);
        boots.put(new ItemStack(ItemMetalArmor.get(Metal.WROUGHT_IRON, Metal.ItemType.BOOTS)), 0.2);
        MonsterEquipment equipment = new MonsterEquipment(weapons, helmets, chestplates, leggings, boots);
        // Register to some vanilla mobs
        MONSTER_EQUIPMENT_MAP.put("minecraft:skeleton", equipment);
        MONSTER_EQUIPMENT_MAP.put("minecraft:wither_skeleton", equipment);
        MONSTER_EQUIPMENT_MAP.put("minecraft:stray", equipment);
        MONSTER_EQUIPMENT_MAP.put("minecraft:zombie", equipment);
        MONSTER_EQUIPMENT_MAP.put("minecraft:husk", equipment);
        MONSTER_EQUIPMENT_MAP.put("minecraft:zombie_villager", equipment);
    }

    @Nullable
    public static MonsterEquipment get(Entity entity)
    {
        ResourceLocation entityType = EntityList.getKey(entity);
        if (entityType != null)
        {
            String entityTypeName = entityType.toString();
            return MONSTER_EQUIPMENT_MAP.get(entityTypeName);
        }
        return null;
    }

    private final WeightedCollection<ItemStack> WEAPONS;
    private final WeightedCollection<ItemStack> HELMETS;
    private final WeightedCollection<ItemStack> CHESTPLATES;
    private final WeightedCollection<ItemStack> LEGGINGS;
    private final WeightedCollection<ItemStack> BOOTS;

    public MonsterEquipment(@Nonnull Map<ItemStack, Double> possibleWeapons, @Nonnull Map<ItemStack, Double> possibleHelmets, @Nonnull Map<ItemStack, Double> possibleChestplates, @Nonnull Map<ItemStack, Double> possibleLeggings, @Nonnull Map<ItemStack, Double> possibleBoots)
    {
        WEAPONS = new WeightedCollection<>(possibleWeapons);
        HELMETS = new WeightedCollection<>(possibleHelmets);
        CHESTPLATES = new WeightedCollection<>(possibleChestplates);
        LEGGINGS = new WeightedCollection<>(possibleLeggings);
        BOOTS = new WeightedCollection<>(possibleBoots);
    }

    public MonsterEquipment()
    {
        WEAPONS = new WeightedCollection<>();
        HELMETS = new WeightedCollection<>();
        CHESTPLATES = new WeightedCollection<>();
        LEGGINGS = new WeightedCollection<>();
        BOOTS = new WeightedCollection<>();
    }

    public ItemStack getWeapon(Random random)
    {
        return WEAPONS.getRandomEntry(random);
    }

    public ItemStack getHelmet(Random random)
    {
        return HELMETS.getRandomEntry(random);
    }

    public ItemStack getChestplate(Random random)
    {
        return CHESTPLATES.getRandomEntry(random);
    }

    public ItemStack getLeggings(Random random)
    {
        return LEGGINGS.getRandomEntry(random);
    }

    public ItemStack getBoots(Random random)
    {
        return BOOTS.getRandomEntry(random);
    }

    public void addWeapon(@Nonnull ItemStack stack, double weight)
    {
        WEAPONS.add(weight, stack);
    }

    public void addHelmet(@Nonnull ItemStack stack, double weight)
    {
        HELMETS.add(weight, stack);
    }

    public void addChestplate(@Nonnull ItemStack stack, double weight)
    {
        CHESTPLATES.add(weight, stack);
    }

    public void addLeggings(@Nonnull ItemStack stack, double weight)
    {
        LEGGINGS.add(weight, stack);
    }

    public void addBoots(@Nonnull ItemStack stack, double weight)
    {
        BOOTS.add(weight, stack);
    }
}
