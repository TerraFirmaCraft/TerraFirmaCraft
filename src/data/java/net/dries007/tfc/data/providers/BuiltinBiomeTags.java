/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.data.providers;

import java.util.concurrent.CompletableFuture;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.tags.BiomeTagsProvider;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import net.dries007.tfc.TerraFirmaCraft;
import net.dries007.tfc.data.Accessors;
import net.dries007.tfc.world.biome.TFCBiomes;

import static net.dries007.tfc.common.TFCTags.Biomes.*;

public class BuiltinBiomeTags extends BiomeTagsProvider implements Accessors
{
    public BuiltinBiomeTags(
        GatherDataEvent event,
        CompletableFuture<HolderLookup.Provider> lookup
    )
    {
        super(event.getGenerator().getPackOutput(), lookup, TerraFirmaCraft.MOD_ID, event.getExistingFileHelper());
    }

    @Override
    protected void addTags(HolderLookup.Provider provider)
    {

        //======Common Tags======//

        tag(Tags.Biomes.IS_AQUATIC)
            .addTag(commonTagOf(Registries.BIOME, "is_lake"));

        tag(Tags.Biomes.IS_AQUATIC_ICY).add(
            TFCBiomes.MELTWATER_LAKE.key(), TFCBiomes.SUBGLACIAL_LAKE.key()
        );

        tag(Tags.Biomes.IS_BEACH).add(
            TFCBiomes.COASTAL_DUNES.key(), TFCBiomes.EMBAYMENTS.key(),
            TFCBiomes.ICE_SHEET_SHORE.key(), TFCBiomes.OLD_SHIELD_VOLCANO_SHORE.key(),
            TFCBiomes.ROCKY_SHORES.key(), TFCBiomes.SEA_STACKS.key(),
            TFCBiomes.SETBACK_CLIFFS.key(), TFCBiomes.SHIELD_VOLCANO_SHORE.key(),
            TFCBiomes.SHORE.key(), TFCBiomes.TERRACE_LOWER.key(),
            TFCBiomes.TERRACE_UPPER.key(), TFCBiomes.TIDAL_FLATS.key()
        );

        tag(Tags.Biomes.IS_BADLANDS).add(
            TFCBiomes.BADLANDS.key(), TFCBiomes.BURREN_BADLANDS.key(),
            TFCBiomes.BURREN_BADLANDS_TALL.key(), TFCBiomes.BUTTES.key(),
            TFCBiomes.HOODOOS.key(), TFCBiomes.MESAS.key(),
            TFCBiomes.STAIR_STEP_CANYONS.key(), TFCBiomes.WHORLED_CANYONS.key()
        );

        tag(Tags.Biomes.IS_COLD_OVERWORLD).add(
            TFCBiomes.DRUMLINS.key(), TFCBiomes.GLACIALLY_CARVED_MOUNTAINS.key(),
            TFCBiomes.GLACIALLY_CARVED_OCEANIC_MOUNTAINS.key(), TFCBiomes.GLACIATED_MOUNTAINS.key(),
            TFCBiomes.GLACIATED_OCEANIC_MOUNTAINS.key(), TFCBiomes.GLACIATED_SHIELD_VOLCANO.key(),
            TFCBiomes.GLACIATED_VOLCANIC_MOUNTAINS.key(), TFCBiomes.GLACIATED_VOLCANIC_OCEANIC_MOUNTAINS.key(),
            TFCBiomes.ICE_SHEET.key(), TFCBiomes.ICE_SHEET_EDGE.key(),
            TFCBiomes.ICE_SHEET_MOUNTAINS.key(), TFCBiomes.ICE_SHEET_MOUNTAINS_EDGE.key(),
            TFCBiomes.ICE_SHEET_OCEANIC.key(), TFCBiomes.ICE_SHEET_OCEANIC_MOUNTAINS.key(),
            TFCBiomes.ICE_SHEET_OCEANIC_MOUNTAINS_EDGE.key(), TFCBiomes.ICE_SHEET_SHIELD_VOLCANO.key(),
            TFCBiomes.ICE_SHEET_SHORE.key(), TFCBiomes.ICE_SHEET_TUYAS.key(),
            TFCBiomes.ICE_SHEET_TUYAS_EDGE.key(), TFCBiomes.ICE_SHEET_VOLCANIC_MOUNTAINS.key(),
            TFCBiomes.ICE_SHEET_VOLCANIC_OCEANIC_MOUNTAINS.key(), TFCBiomes.INVERTED_PATTERNED_GROUND.key(),
            TFCBiomes.KNOB_AND_KETTLE.key(), TFCBiomes.MELTWATER_LAKE.key(),
            TFCBiomes.PATTERNED_GROUND.key(), TFCBiomes.STONE_CIRCLES.key(),
            TFCBiomes.SUBGLACIAL_LAKE.key(), TFCBiomes.TUYAS.key()
        );

        tag(Tags.Biomes.IS_DEEP_OCEAN).add(
            TFCBiomes.DEEP_OCEAN.key(), TFCBiomes.DEEP_OCEAN_ATOLLS.key(),
            TFCBiomes.DEEP_OCEAN_TRENCH.key(), TFCBiomes.OCEAN_RIDGE.key()
        );

        tag(Tags.Biomes.IS_DRY).add(
            TFCBiomes.BUTTES.key(), TFCBiomes.DUNE_SEA.key(),
            TFCBiomes.GRASSY_DUNES.key(), TFCBiomes.HOODOOS.key(),
            TFCBiomes.MESAS.key(), TFCBiomes.ROCKY_PLATEAU.key(),
            TFCBiomes.SALT_FLATS.key(), TFCBiomes.STAIR_STEP_CANYONS.key(),
            TFCBiomes.WHORLED_CANYONS.key()
        );

        tag(Tags.Biomes.IS_HILL).add(
            TFCBiomes.CANYONS.key(), TFCBiomes.CENOTE_HIGHLANDS.key(),
            TFCBiomes.CENOTE_HILLS.key(), TFCBiomes.CENOTE_ROLLING_HILLS.key(),
            TFCBiomes.DOLINE_HIGHLANDS.key(), TFCBiomes.DOLINE_HILLS.key(),
            TFCBiomes.DOLINE_ROLLING_HILLS.key(), TFCBiomes.DRUMLINS.key(),
            TFCBiomes.HIGHLANDS.key(), TFCBiomes.HILLS.key(),
            TFCBiomes.KNOB_AND_KETTLE.key(), TFCBiomes.LOW_CANYONS.key(),
            TFCBiomes.ROLLING_HILLS.key(), TFCBiomes.SHILIN_CANYONS.key(),
            TFCBiomes.SHILIN_HIGHLANDS.key(), TFCBiomes.SHILIN_HILLS.key(),
            TFCBiomes.TOWER_KARST_HIGHLANDS.key(), TFCBiomes.TOWER_KARST_HILLS.key()
        );

        tag(Tags.Biomes.IS_HOT_OVERWORLD).add(
            TFCBiomes.BUTTES.key(), TFCBiomes.HOODOOS.key(),
            TFCBiomes.MESAS.key(), TFCBiomes.STAIR_STEP_CANYONS.key(),
            TFCBiomes.WHORLED_CANYONS.key()
        );

        tag(Tags.Biomes.IS_ICY).add(
            TFCBiomes.ICE_SHEET.key(), TFCBiomes.ICE_SHEET_EDGE.key(),
            TFCBiomes.ICE_SHEET_MOUNTAINS.key(), TFCBiomes.ICE_SHEET_MOUNTAINS_EDGE.key(),
            TFCBiomes.ICE_SHEET_OCEANIC.key(), TFCBiomes.ICE_SHEET_OCEANIC_MOUNTAINS.key(),
            TFCBiomes.ICE_SHEET_OCEANIC_MOUNTAINS_EDGE.key(), TFCBiomes.ICE_SHEET_SHIELD_VOLCANO.key(),
            TFCBiomes.ICE_SHEET_SHORE.key(), TFCBiomes.ICE_SHEET_TUYAS.key(),
            TFCBiomes.ICE_SHEET_TUYAS_EDGE.key()
        );

        tag(commonTagOf(Registries.BIOME, "is_lake")).add(
            TFCBiomes.LAKE.key(), TFCBiomes.MELTWATER_LAKE.key(),
            TFCBiomes.MOUNTAIN_LAKE.key(), TFCBiomes.OCEANIC_MOUNTAIN_LAKE.key(),
            TFCBiomes.OLD_MOUNTAIN_LAKE.key(), TFCBiomes.PLATEAU_LAKE.key(),
            TFCBiomes.RIFT_LAKE.key(), TFCBiomes.SUBGLACIAL_LAKE.key(),
            TFCBiomes.TOWER_KARST_LAKE.key(), TFCBiomes.VOLCANIC_MOUNTAIN_LAKE.key(),
            TFCBiomes.VOLCANIC_OCEANIC_MOUNTAIN_LAKE.key()
        );

        tag(Tags.Biomes.IS_MOUNTAIN).add(
            TFCBiomes.COLLISIONAL_MOUNTAINS.key(), TFCBiomes.EXTREME_DOLINE_MOUNTAINS.key(),
            TFCBiomes.GLACIALLY_CARVED_MOUNTAINS.key(), TFCBiomes.GLACIALLY_CARVED_OCEANIC_MOUNTAINS.key(),
            TFCBiomes.GLACIATED_MOUNTAINS.key(), TFCBiomes.GLACIATED_OCEANIC_MOUNTAINS.key(),
            TFCBiomes.GLACIATED_VOLCANIC_MOUNTAINS.key(), TFCBiomes.GLACIATED_VOLCANIC_OCEANIC_MOUNTAINS.key(),
            TFCBiomes.ICE_SHEET_MOUNTAINS.key(), TFCBiomes.ICE_SHEET_MOUNTAINS_EDGE.key(),
            TFCBiomes.ICE_SHEET_OCEANIC_MOUNTAINS.key(), TFCBiomes.ICE_SHEET_OCEANIC_MOUNTAINS_EDGE.key(),
            TFCBiomes.ICE_SHEET_VOLCANIC_MOUNTAINS.key(), TFCBiomes.ICE_SHEET_VOLCANIC_OCEANIC_MOUNTAINS.key(),
            TFCBiomes.MOUNTAINS.key(), TFCBiomes.OCEANIC_MOUNTAINS.key(),
            TFCBiomes.OLD_MOUNTAINS.key(), TFCBiomes.VOLCANIC_MOUNTAIN_ISLANDS.key()
        );

        tag(Tags.Biomes.IS_OCEAN).add(
            TFCBiomes.DEEP_OCEAN.key(), TFCBiomes.DEEP_OCEAN_ATOLLS.key(),
            TFCBiomes.DEEP_OCEAN_TRENCH.key(), TFCBiomes.OCEAN.key(),
            TFCBiomes.OCEAN_ATOLLS.key(), TFCBiomes.OCEAN_REEF.key(),
            TFCBiomes.OCEAN_RIDGE.key()
        );

        tag(Tags.Biomes.IS_PLAINS).add(
            TFCBiomes.BURREN_PLAINS.key(), TFCBiomes.CENOTE_PLAINS.key(),
            TFCBiomes.DOLINE_PLAINS.key(), TFCBiomes.MUD_FLATS.key(),
            TFCBiomes.PLAINS.key(), TFCBiomes.SALT_FLATS.key(),
            TFCBiomes.SHILIN_PLAINS.key(), TFCBiomes.TOWER_KARST_PLAINS.key()
        );

        tag(Tags.Biomes.IS_PLATEAU).add(
            TFCBiomes.BURREN_PLATEAU.key(), TFCBiomes.CENOTE_PLATEAU.key(),
            TFCBiomes.DOLINE_PLATEAU.key(), TFCBiomes.EXTREME_DOLINE_PLATEAU.key(),
            TFCBiomes.PLATEAU.key(), TFCBiomes.PLATEAU_LAKE.key(),
            TFCBiomes.PLATEAU_WIDE.key(), TFCBiomes.ROCKY_PLATEAU.key(),
            TFCBiomes.SHILIN_PLATEAU.key()
        );

        tag(Tags.Biomes.IS_RIVER).addTag(IS_RIVER);

        tag(Tags.Biomes.IS_SANDY).add(
            TFCBiomes.BADLANDS.key(), TFCBiomes.BURREN_BADLANDS.key(),
            TFCBiomes.BURREN_BADLANDS_TALL.key(), TFCBiomes.BUTTES.key(),
            TFCBiomes.COASTAL_DUNES.key(), TFCBiomes.DUNE_SEA.key(),
            TFCBiomes.EMBAYMENTS.key(), TFCBiomes.GRASSY_DUNES.key(),
            TFCBiomes.ICE_SHEET_SHORE.key(), TFCBiomes.OLD_SHIELD_VOLCANO_SHORE.key(),
            TFCBiomes.SALT_FLATS.key(), TFCBiomes.SEA_STACKS.key(),
            TFCBiomes.SETBACK_CLIFFS.key(), TFCBiomes.SHIELD_VOLCANO_SHORE.key(),
            TFCBiomes.SHORE.key(), TFCBiomes.TERRACE_LOWER.key(),
            TFCBiomes.TERRACE_UPPER.key(), TFCBiomes.TIDAL_FLATS.key()
        );

        tag(Tags.Biomes.IS_SHALLOW_OCEAN).add(
            TFCBiomes.OCEAN.key(), TFCBiomes.OCEAN_ATOLLS.key(),
            TFCBiomes.OCEAN_REEF.key()
        );

        tag(Tags.Biomes.IS_SNOWY).add(
            TFCBiomes.DRUMLINS.key(), TFCBiomes.GLACIALLY_CARVED_MOUNTAINS.key(),
            TFCBiomes.GLACIALLY_CARVED_OCEANIC_MOUNTAINS.key(), TFCBiomes.GLACIATED_MOUNTAINS.key(),
            TFCBiomes.GLACIATED_OCEANIC_MOUNTAINS.key(), TFCBiomes.GLACIATED_SHIELD_VOLCANO.key(),
            TFCBiomes.GLACIATED_VOLCANIC_MOUNTAINS.key(), TFCBiomes.GLACIATED_VOLCANIC_OCEANIC_MOUNTAINS.key(),
            TFCBiomes.ICE_SHEET.key(), TFCBiomes.ICE_SHEET_EDGE.key(),
            TFCBiomes.ICE_SHEET_MOUNTAINS.key(), TFCBiomes.ICE_SHEET_MOUNTAINS_EDGE.key(),
            TFCBiomes.ICE_SHEET_OCEANIC.key(), TFCBiomes.ICE_SHEET_OCEANIC_MOUNTAINS.key(),
            TFCBiomes.ICE_SHEET_OCEANIC_MOUNTAINS_EDGE.key(), TFCBiomes.ICE_SHEET_SHIELD_VOLCANO.key(),
            TFCBiomes.ICE_SHEET_SHORE.key(), TFCBiomes.ICE_SHEET_TUYAS.key(),
            TFCBiomes.ICE_SHEET_TUYAS_EDGE.key(), TFCBiomes.ICE_SHEET_VOLCANIC_MOUNTAINS.key(),
            TFCBiomes.ICE_SHEET_VOLCANIC_OCEANIC_MOUNTAINS.key(), TFCBiomes.INVERTED_PATTERNED_GROUND.key(),
            TFCBiomes.KNOB_AND_KETTLE.key(), TFCBiomes.MELTWATER_LAKE.key(),
            TFCBiomes.PATTERNED_GROUND.key(), TFCBiomes.STONE_CIRCLES.key(),
            TFCBiomes.SUBGLACIAL_LAKE.key(), TFCBiomes.TUYAS.key()
        );

        tag(Tags.Biomes.IS_SNOWY_PLAINS).add(
            TFCBiomes.ICE_SHEET.key(), TFCBiomes.ICE_SHEET_EDGE.key(),
            TFCBiomes.INVERTED_PATTERNED_GROUND.key(), TFCBiomes.PATTERNED_GROUND.key(),
            TFCBiomes.STONE_CIRCLES.key()
        );

        tag(Tags.Biomes.IS_SPARSE_VEGETATION_OVERWORLD)
            .addTag(IS_BURREN)
            .add(
                TFCBiomes.DRUMLINS.key(), TFCBiomes.GLACIALLY_CARVED_MOUNTAINS.key(),
                TFCBiomes.GLACIALLY_CARVED_OCEANIC_MOUNTAINS.key(), TFCBiomes.GLACIALLY_CARVED_VOLCANIC_MOUNTAINS.key(),
                TFCBiomes.GLACIALLY_CARVED_VOLCANIC_OCEANIC_MOUNTAINS.key(), TFCBiomes.GLACIATED_MOUNTAINS.key(),
                TFCBiomes.GLACIATED_OCEANIC_MOUNTAINS.key(), TFCBiomes.GLACIATED_SHIELD_VOLCANO.key(),
                TFCBiomes.GLACIATED_VOLCANIC_MOUNTAINS.key(), TFCBiomes.GLACIATED_VOLCANIC_OCEANIC_MOUNTAINS.key(),
                TFCBiomes.ICE_SHEET.key(), TFCBiomes.ICE_SHEET_EDGE.key(),
                TFCBiomes.ICE_SHEET_MOUNTAINS.key(), TFCBiomes.ICE_SHEET_MOUNTAINS_EDGE.key(),
                TFCBiomes.ICE_SHEET_OCEANIC.key(), TFCBiomes.ICE_SHEET_OCEANIC_MOUNTAINS.key(),
                TFCBiomes.ICE_SHEET_OCEANIC_MOUNTAINS_EDGE.key(), TFCBiomes.ICE_SHEET_SHIELD_VOLCANO.key(),
                TFCBiomes.ICE_SHEET_SHORE.key(), TFCBiomes.ICE_SHEET_TUYAS.key(),
                TFCBiomes.ICE_SHEET_TUYAS_EDGE.key(), TFCBiomes.ICE_SHEET_VOLCANIC_MOUNTAINS.key(),
                TFCBiomes.ICE_SHEET_VOLCANIC_OCEANIC_MOUNTAINS.key(), TFCBiomes.INVERTED_PATTERNED_GROUND.key(),
                TFCBiomes.KNOB_AND_KETTLE.key(), TFCBiomes.PATTERNED_GROUND.key(),
                TFCBiomes.SALT_FLATS.key(), TFCBiomes.STONE_CIRCLES.key(),
                TFCBiomes.TUYAS.key(), TFCBiomes.WHORLED_CANYONS.key()
            );

        tag(Tags.Biomes.IS_STONY_SHORES)
            .add(TFCBiomes.ROCKY_SHORES.key());

        tag(Tags.Biomes.IS_SWAMP).add(
            TFCBiomes.INVERTED_PATTERNED_GROUND.key(), TFCBiomes.LOWLANDS.key(),
            TFCBiomes.PATTERNED_GROUND.key(), TFCBiomes.SALT_MARSH.key(),
            TFCBiomes.TIDAL_FLATS.key()
        );

        //tag(Tags.Biomes.IS_TEMPERATE_OVERWORLD) Should anything be here??

        tag(Tags.Biomes.IS_WET_OVERWORLD).add(
                TFCBiomes.LOWLANDS.key(), TFCBiomes.SALT_MARSH.key(),
                TFCBiomes.TIDAL_FLATS.key()
            )
            .addTag(IS_KARST);

        tag(Tags.Biomes.IS_WINDSWEPT).add(
            TFCBiomes.COASTAL_DUNES.key(), TFCBiomes.DUNE_SEA.key(),
            TFCBiomes.GRASSY_DUNES.key(), TFCBiomes.SEA_STACKS.key(),
            TFCBiomes.SETBACK_CLIFFS.key(), TFCBiomes.WHORLED_CANYONS.key()
        );

        tag(commonTagOf(Registries.BIOME, "is_volcanic"))
            .addTag(HAS_CINDER_CONES)
            .addTag(HAS_STRATOVOLCANOES)
            .addTag(HAS_TUYAS)
            .addTag(HAS_TUFF_CONES)
            .addTag(IS_SHIELD_VOLCANO);

        //=====TFC Tags=====//

        tag(HAS_ATOLLS).add(
            TFCBiomes.DEEP_OCEAN_ATOLLS.key(), TFCBiomes.OCEAN_ATOLLS.key()
        );

        tag(HAS_CINDER_CONES).add(
            TFCBiomes.ACTIVE_SHIELD_VOLCANO.key(), TFCBiomes.CANYONS.key(),
            TFCBiomes.DOLINE_CANYONS.key(), TFCBiomes.RIFT_VALLEY.key(),
            TFCBiomes.VOLCANIC_MOUNTAIN_ISLANDS.key()
        );

        tag(HAS_PREDICTABLE_WINDS)
            .addTag(IS_OCEAN);

        tag(HAS_STRATOVOLCANOES).add(
            TFCBiomes.GLACIALLY_CARVED_VOLCANIC_MOUNTAINS.key(), TFCBiomes.GLACIALLY_CARVED_VOLCANIC_OCEANIC_MOUNTAINS.key(),
            TFCBiomes.GLACIATED_VOLCANIC_MOUNTAINS.key(), TFCBiomes.GLACIATED_VOLCANIC_OCEANIC_MOUNTAINS.key(),
            TFCBiomes.ICE_SHEET_VOLCANIC_MOUNTAINS.key(), TFCBiomes.ICE_SHEET_VOLCANIC_OCEANIC_MOUNTAINS.key(),
            TFCBiomes.OCEANIC_VOLCANIC_ARC.key(), TFCBiomes.VOLCANIC_ISLAND.key(),
            TFCBiomes.VOLCANIC_MOUNTAINS.key(), TFCBiomes.VOLCANIC_MOUNTAIN_LAKE.key(),
            TFCBiomes.VOLCANIC_OCEANIC_MOUNTAINS.key(), TFCBiomes.VOLCANIC_OCEANIC_MOUNTAIN_LAKE.key()
        );

        tag(HAS_TUFF_CONES).add(
            TFCBiomes.ANCIENT_SHIELD_VOLCANO.key(), TFCBiomes.DORMANT_SHIELD_VOLCANO.key(),
            TFCBiomes.EXTINCT_SHIELD_VOLCANO.key(), TFCBiomes.GLACIATED_SHIELD_VOLCANO.key(),
            TFCBiomes.ICE_SHEET_SHIELD_VOLCANO.key(), TFCBiomes.OLD_SHIELD_VOLCANO_SHORE.key(),
            TFCBiomes.SHIELD_VOLCANO_SHORE.key(), TFCBiomes.SUNKEN_SHIELD_VOLCANO.key()
        );

        tag(HAS_TUYAS).add(
            TFCBiomes.ICE_SHEET_TUYAS.key(), TFCBiomes.ICE_SHEET_TUYAS_EDGE.key(),
            TFCBiomes.TUYAS.key()
        );

        tag(IS_BURREN).add(
            TFCBiomes.BURREN_BADLANDS.key(), TFCBiomes.BURREN_BADLANDS_TALL.key(),
            TFCBiomes.BURREN_PLAINS.key(), TFCBiomes.BURREN_PLATEAU.key(),
            TFCBiomes.BURREN_ROCHE_MOUTONEE.key()
        );

        tag(IS_CENOTE).add(
            TFCBiomes.CENOTE_CANYONS.key(), TFCBiomes.CENOTE_HIGHLANDS.key(),
            TFCBiomes.CENOTE_HILLS.key(), TFCBiomes.CENOTE_PLAINS.key(),
            TFCBiomes.CENOTE_PLATEAU.key(), TFCBiomes.CENOTE_ROLLING_HILLS.key()
        );

        tag(IS_DOLINES).add(
            TFCBiomes.DOLINE_CANYONS.key(), TFCBiomes.DOLINE_HIGHLANDS.key(),
            TFCBiomes.DOLINE_HILLS.key(), TFCBiomes.DOLINE_PLAINS.key(),
            TFCBiomes.DOLINE_PLATEAU.key(), TFCBiomes.DOLINE_ROLLING_HILLS.key()
        );

        tag(IS_EXTREME_DOLINES).add(
            TFCBiomes.EXTREME_DOLINE_MOUNTAINS.key(), TFCBiomes.EXTREME_DOLINE_PLATEAU.key()
        );

        tag(IS_GLACIATED).add(
            TFCBiomes.GLACIATED_MOUNTAINS.key(), TFCBiomes.GLACIATED_OCEANIC_MOUNTAINS.key(),
            TFCBiomes.GLACIATED_SHIELD_VOLCANO.key(), TFCBiomes.GLACIATED_VOLCANIC_MOUNTAINS.key(),
            TFCBiomes.GLACIATED_VOLCANIC_OCEANIC_MOUNTAINS.key()
        );

        tag(IS_ICE_SHEET).add(
            TFCBiomes.ICE_SHEET.key(), TFCBiomes.ICE_SHEET_EDGE.key(),
            TFCBiomes.ICE_SHEET_MOUNTAINS.key(), TFCBiomes.ICE_SHEET_MOUNTAINS_EDGE.key(),
            TFCBiomes.ICE_SHEET_OCEANIC.key(), TFCBiomes.ICE_SHEET_OCEANIC_MOUNTAINS.key(),
            TFCBiomes.ICE_SHEET_OCEANIC_MOUNTAINS_EDGE.key(), TFCBiomes.ICE_SHEET_SHIELD_VOLCANO.key(),
            TFCBiomes.ICE_SHEET_SHORE.key(), TFCBiomes.ICE_SHEET_TUYAS.key(),
            TFCBiomes.ICE_SHEET_TUYAS_EDGE.key(), TFCBiomes.ICE_SHEET_VOLCANIC_MOUNTAINS.key(),
            TFCBiomes.ICE_SHEET_VOLCANIC_OCEANIC_MOUNTAINS.key(), TFCBiomes.SUBGLACIAL_LAKE.key()
        );

        tag(IS_KARST)
            .addTag(IS_BURREN)
            .addTag(IS_CENOTE)
            .addTag(IS_DOLINES)
            .addTag(IS_SHILIN)
            .addTag(IS_TOWER_KARST);

        tag(IS_OCEAN).add(
            TFCBiomes.COASTAL_DUNES.key(), TFCBiomes.DEEP_OCEAN.key(),
            TFCBiomes.DEEP_OCEAN_ATOLLS.key(), TFCBiomes.DEEP_OCEAN_TRENCH.key(),
            TFCBiomes.EMBAYMENTS.key(), TFCBiomes.ICE_SHEET_OCEANIC.key(),
            TFCBiomes.ICE_SHEET_SHORE.key(), TFCBiomes.OCEAN.key(),
            TFCBiomes.OCEANIC_VOLCANIC_ARC.key(), TFCBiomes.OCEAN_ATOLLS.key(),
            TFCBiomes.OCEAN_REEF.key(), TFCBiomes.OCEAN_RIDGE.key(),
            TFCBiomes.OLD_SHIELD_VOLCANO_SHORE.key(), TFCBiomes.ROCKY_SHORES.key(),
            TFCBiomes.SEA_STACKS.key(), TFCBiomes.SETBACK_CLIFFS.key(),
            TFCBiomes.SHIELD_VOLCANO_SHORE.key(), TFCBiomes.SHORE.key(),
            TFCBiomes.TERRACE_LOWER.key(), TFCBiomes.TERRACE_UPPER.key(),
            TFCBiomes.TIDAL_FLATS.key()
        );

        tag(IS_RIFT).add(
            TFCBiomes.RIFT_LAKE.key(), TFCBiomes.RIFT_VALLEY.key()
        );

        tag(IS_RIVER).add(
            TFCBiomes.RIVER.key(), TFCBiomes.RIVER_VALLEY.key()
        );

        tag(IS_SHIELD_VOLCANO).add(
            TFCBiomes.ACTIVE_SHIELD_VOLCANO.key(), TFCBiomes.ANCIENT_SHIELD_VOLCANO.key(),
            TFCBiomes.DORMANT_SHIELD_VOLCANO.key(), TFCBiomes.EXTINCT_SHIELD_VOLCANO.key(),
            TFCBiomes.GLACIATED_SHIELD_VOLCANO.key(), TFCBiomes.ICE_SHEET_SHIELD_VOLCANO.key(),
            TFCBiomes.OLD_SHIELD_VOLCANO_SHORE.key(), TFCBiomes.SHIELD_VOLCANO_SHORE.key(),
            TFCBiomes.SUNKEN_SHIELD_VOLCANO.key()
        );

        tag(IS_SHILIN).add(
            TFCBiomes.SHILIN_CANYONS.key(), TFCBiomes.SHILIN_HIGHLANDS.key(),
            TFCBiomes.SHILIN_HILLS.key(), TFCBiomes.SHILIN_PLAINS.key(),
            TFCBiomes.SHILIN_PLATEAU.key()
        );

        tag(IS_TOWER_KARST).add(
            TFCBiomes.TOWER_KARST_BAY.key(), TFCBiomes.TOWER_KARST_CANYONS.key(),
            TFCBiomes.TOWER_KARST_HIGHLANDS.key(), TFCBiomes.TOWER_KARST_HILLS.key(),
            TFCBiomes.TOWER_KARST_LAKE.key(), TFCBiomes.TOWER_KARST_PLAINS.key()
        );


    }
}
