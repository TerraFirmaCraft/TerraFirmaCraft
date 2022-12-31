/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.util;

import com.google.gson.JsonObject;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;

import org.jetbrains.annotations.Nullable;

public final class EntityDamageResistance extends PhysicalDamageTypeData
{
    public static final DataManager<EntityDamageResistance> MANAGER = new DataManager<>(Helpers.identifier("entity_damage_resistances"), "entity_damage_resistances", EntityDamageResistance::new);

    @Nullable
    public static EntityDamageResistance get(Entity entity)
    {
        for (EntityDamageResistance resist : MANAGER.getValues())
        {
            if (resist.matches(entity))
            {
                return resist;
            }
        }
        return null;
    }

    private final TagKey<EntityType<?>> entity;

    private EntityDamageResistance(ResourceLocation id, JsonObject json)
    {
        super(id, json);
        this.entity = JsonHelpers.getTag(json, "entity", Registry.ENTITY_TYPE_REGISTRY);
    }

    public boolean matches(Entity entity)
    {
        return Helpers.isEntity(entity, this.entity);
    }
}
