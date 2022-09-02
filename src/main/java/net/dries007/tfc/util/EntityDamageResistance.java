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

public final class EntityDamageResistance implements PhysicalDamageType.Multiplier
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
    private final float piercing, slashing, crushing;

    private EntityDamageResistance(ResourceLocation id, JsonObject json)
    {
        this.entity = JsonHelpers.getTag(json, "entity", Registry.ENTITY_TYPE_REGISTRY);

        this.piercing = JsonHelpers.getAsFloat(json, "piercing", 0);
        this.slashing = JsonHelpers.getAsFloat(json, "slashing", 0);
        this.crushing = JsonHelpers.getAsFloat(json, "crushing", 0);
    }

    public boolean matches(Entity entity)
    {
        return Helpers.isEntity(entity, this.entity);
    }

    @Override
    public float crushing()
    {
        return crushing;
    }

    @Override
    public float piercing()
    {
        return piercing;
    }

    @Override
    public float slashing()
    {
        return slashing;
    }
}
