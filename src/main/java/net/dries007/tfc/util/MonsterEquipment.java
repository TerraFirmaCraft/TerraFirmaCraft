/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import javax.annotation.Nullable;

import com.google.common.collect.ImmutableMap;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
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
    private static final Map<String, MonsterEquipment> ENTRIES = new HashMap<>();

    static
    {
        WeightedCollection<ItemStack> weapons = new WeightedCollection<>();
        weapons.add(0.4, ItemStack.EMPTY);
        weapons.add(0.2, new ItemStack(ItemMetal.get(Metal.WROUGHT_IRON, Metal.ItemType.MACE)));
        weapons.add(0.2, new ItemStack(ItemMetalSword.get(Metal.WROUGHT_IRON)));
        weapons.add(0.2, new ItemStack(ItemMetal.get(Metal.WROUGHT_IRON, Metal.ItemType.KNIFE)));

        WeightedCollection<ItemStack> rangedWeapons = new WeightedCollection<>();
        rangedWeapons.add(0.7, new ItemStack(Items.BOW));
        rangedWeapons.add(0.1, new ItemStack(ItemMetal.get(Metal.WROUGHT_IRON, Metal.ItemType.MACE)));
        rangedWeapons.add(0.1, new ItemStack(ItemMetalSword.get(Metal.WROUGHT_IRON)));
        rangedWeapons.add(0.2, new ItemStack(ItemMetal.get(Metal.WROUGHT_IRON, Metal.ItemType.KNIFE)));

        WeightedCollection<ItemStack> helmets = new WeightedCollection<>();
        helmets.add(0.8, ItemStack.EMPTY);
        helmets.add(0.2, new ItemStack(ItemMetalArmor.get(Metal.WROUGHT_IRON, Metal.ItemType.HELMET)));

        WeightedCollection<ItemStack> chestplates = new WeightedCollection<>();
        chestplates.add(0.8, ItemStack.EMPTY);
        chestplates.add(0.2, new ItemStack(ItemMetalArmor.get(Metal.WROUGHT_IRON, Metal.ItemType.CHESTPLATE)));

        WeightedCollection<ItemStack> leggings = new WeightedCollection<>();
        leggings.add(0.8, ItemStack.EMPTY);
        leggings.add(0.2, new ItemStack(ItemMetalArmor.get(Metal.WROUGHT_IRON, Metal.ItemType.GREAVES)));

        WeightedCollection<ItemStack> boots = new WeightedCollection<>();
        boots.add(0.8, ItemStack.EMPTY);
        boots.add(0.2, new ItemStack(ItemMetalArmor.get(Metal.WROUGHT_IRON, Metal.ItemType.BOOTS)));

        MonsterEquipment equipment = new MonsterEquipment(weapons, helmets, chestplates, leggings, boots);
        MonsterEquipment rangedEquipment = new MonsterEquipment(rangedWeapons, helmets, chestplates, leggings, boots);

        // Register to some vanilla mobs
        // Do some of these even spawn? I think not...
        ENTRIES.put("minecraft:skeleton", rangedEquipment);
        ENTRIES.put("minecraft:stray", rangedEquipment);
        ENTRIES.put("minecraft:zombie", equipment);
        ENTRIES.put("minecraft:husk", equipment);
        ENTRIES.put("minecraft:zombie_villager", equipment);
    }

    @Nullable
    public static MonsterEquipment get(Entity entity)
    {
        ResourceLocation entityType = EntityList.getKey(entity);
        if (entityType != null)
        {
            String entityTypeName = entityType.toString();
            return ENTRIES.get(entityTypeName);
        }
        return null;
    }

    @Nullable
    public static MonsterEquipment get(String entityId)
    {
        return ENTRIES.get(entityId);
    }

    public static void put(String entityId, MonsterEquipment equipment)
    {
        ENTRIES.put(entityId, equipment);
    }

    private final Map<EntityEquipmentSlot, WeightedCollection<ItemStack>> equipment;

    public MonsterEquipment(WeightedCollection<ItemStack> weapons, WeightedCollection<ItemStack> helmets, WeightedCollection<ItemStack> chestplates, WeightedCollection<ItemStack> leggings, WeightedCollection<ItemStack> boots)
    {
        equipment = new ImmutableMap.Builder<EntityEquipmentSlot, WeightedCollection<ItemStack>>()
            .put(EntityEquipmentSlot.MAINHAND, weapons)
            .put(EntityEquipmentSlot.HEAD, helmets)
            .put(EntityEquipmentSlot.CHEST, chestplates)
            .put(EntityEquipmentSlot.LEGS, leggings)
            .put(EntityEquipmentSlot.FEET, boots)
            .build();
    }

    public Optional<ItemStack> getEquipment(EntityEquipmentSlot slot, Random random)
    {
        if (equipment.containsKey(slot))
        {
            return Optional.of(equipment.get(slot).getRandomEntry(random));
        }
        return Optional.empty();
    }
}
