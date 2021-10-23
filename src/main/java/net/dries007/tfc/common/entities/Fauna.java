/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.entities;

import java.util.Objects;

import com.google.gson.JsonObject;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;

import net.minecraftforge.registries.ForgeRegistries;

import com.mojang.serialization.JsonOps;
import net.dries007.tfc.world.chunkdata.ChunkData;
import net.dries007.tfc.world.decorator.ClimateConfig;

/**
 * A data driven way to make spawning conditions for animals player configurable.
 * //todo: In 1.18, spawn predicates are set to change. We will have to adapt or throw this out.
 */
public class Fauna
{
    private final ResourceLocation id;
    private final EntityType<Mob> entity;
    private final Fluid fluid;
    private final int chance;
    private final int distanceBelowSeaLevel;
    private final ClimateConfig climateConfig;
    private final boolean solidGround;

    @SuppressWarnings("unchecked")
    public Fauna(ResourceLocation id, JsonObject json)
    {
        this.id = id;
        entity = (EntityType<Mob>) ForgeRegistries.ENTITIES.getValue(new ResourceLocation(GsonHelper.getAsString(json, "entity")));
        this.fluid = ForgeRegistries.FLUIDS.getValue(new ResourceLocation(GsonHelper.getAsString(json, "fluid", "minecraft:empty")));
        this.chance = GsonHelper.getAsInt(json, "chance", 1);
        this.distanceBelowSeaLevel = GsonHelper.getAsInt(json, "distance_below_sea_level", -1);
        this.climateConfig = ClimateConfig.CODEC.decode(JsonOps.INSTANCE, json.get("climate")).getOrThrow(false, null).getFirst();
        this.solidGround = GsonHelper.getAsBoolean(json, "solid_ground", false);
    }

    public ResourceLocation getId()
    {
        return id;
    }

    public EntityType<Mob> getEntity()
    {
        return entity;
    }

    public SpawnPlacements.Type getType()
    {
        if (fluid != Fluids.EMPTY)//todo: serialize this...
        {
            return SpawnPlacements.Type.IN_WATER;
        }
        return SpawnPlacements.Type.ON_GROUND;
    }

    public SpawnPlacements.SpawnPredicate<Mob> makeRules()
    {
        return (entity, world, reason, pos, rand) -> {
            if (rand.nextInt(chance) != 0) return false;

            if (fluid != Fluids.EMPTY)
            {
                Fluid foundFluid = world.getFluidState(pos).getType();
                if (foundFluid != fluid)
                {
                    return false;
                }
            }

            final int seaLevel = world.getLevel().getChunkSource().generator.getSeaLevel();
            if (distanceBelowSeaLevel != -1 && pos.getY() > seaLevel - distanceBelowSeaLevel)
            {
                return false;
            }

            final ChunkData data = ChunkData.get(world, pos);
            if (!climateConfig.isValid(data, pos, rand))
            {
                return false;
            }

            final BlockPos below = pos.below();
            if (solidGround && !world.getBlockState(below).is(BlockTags.VALID_SPAWN))
            {
                return false;
            }
            return true;
        };
    }
}
