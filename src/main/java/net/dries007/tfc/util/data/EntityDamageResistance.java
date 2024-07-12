/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.util.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.PhysicalDamage;

public record EntityDamageResistance(
    TagKey<EntityType<?>> entity,
    PhysicalDamage damages
) {
    public static final Codec<EntityDamageResistance> CODEC = RecordCodecBuilder.create(i -> i.group(
        TagKey.codec(Registries.ENTITY_TYPE).fieldOf("entity").forGetter(c -> c.entity),
        PhysicalDamage.CODEC.forGetter(c -> c.damages)
    ).apply(i, EntityDamageResistance::new));

    public static final DataManager<EntityDamageResistance> MANAGER = new DataManager<>(Helpers.identifier("entity_damage_resistances"), "entity_damage_resistances", CODEC);

    @Nullable
    public static EntityDamageResistance get(Entity entity)
    {
        for (EntityDamageResistance resist : MANAGER.getValues())
        {
            if (Helpers.isEntity(entity, resist.entity))
            {
                return resist;
            }
        }
        return null;
    }
}
