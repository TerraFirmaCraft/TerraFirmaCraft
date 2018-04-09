package net.dries007.tfc.util;

import com.google.common.collect.ImmutableList;
import net.dries007.tfc.objects.blocks.BlockRockVariant.Rock;
import net.dries007.tfc.objects.blocks.BlockRockVariant.Rock.*;
import net.dries007.tfc.objects.blocks.BlockTFCOre.Ore;

import static net.dries007.tfc.objects.blocks.BlockRockVariant.Rock.Category.*;
import static net.dries007.tfc.objects.blocks.BlockRockVariant.Rock.*;
import static net.dries007.tfc.objects.blocks.BlockTFCOre.Ore.*;
import static net.dries007.tfc.util.OreSpawnData.SpawnSize.*;
import static net.dries007.tfc.util.OreSpawnData.SpawnType.DEFAULT;
import static net.dries007.tfc.util.OreSpawnData.SpawnType.VEINS;

/**
 * todo: make this configurable
 * todo: have this checked by someone
 */
public final class OreSpawnData
{
    public static final ImmutableList<OreSpawnData> ORE_SPAWN_DATA;
    static
    {
        ImmutableList.Builder<OreSpawnData> b = new ImmutableList.Builder<>();

        // Metals
        b.add(new OreSpawnData(NATIVE_COPPER, VEINS, LARGE, 120, IGNEOUS_EXTRUSIVE, 5, 128, 80, 60));
        b.add(new OreSpawnData(NATIVE_GOLD, VEINS, LARGE, 120, new Category[]{IGNEOUS_EXTRUSIVE, IGNEOUS_INTRUSIVE}, 5, 128, 80, 60));
        b.add(new OreSpawnData(NATIVE_PLATINUM, VEINS, SMALL, 150, SEDIMENTARY, 5, 128, 40, 80));
        b.add(new OreSpawnData(HEMATITE, VEINS, MEDIUM, 125, IGNEOUS_EXTRUSIVE, 5, 128, 80, 60));
        b.add(new OreSpawnData(NATIVE_SILVER, VEINS, MEDIUM, 100, new Rock[]{GRANITE, GNEISS}, 5, 128, 80, 60));
        b.add(new OreSpawnData(CASSITERITE, VEINS, MEDIUM, 100, IGNEOUS_INTRUSIVE, 5, 128, 80, 60));
        b.add(new OreSpawnData(GALENA, VEINS, MEDIUM, 100, new Rock[]{GRANITE, LIMESTONE}, new Category[]{IGNEOUS_EXTRUSIVE, METAMORPHIC}, 5, 128, 80, 60));
        b.add(new OreSpawnData(BISMUTHINITE, VEINS, MEDIUM, 100, new Category[]{IGNEOUS_EXTRUSIVE, SEDIMENTARY}, 5, 128, 80, 60));
        b.add(new OreSpawnData(GARNIERITE, VEINS, MEDIUM, 150, GABBRO, 5, 128, 80, 60));
        b.add(new OreSpawnData(MALACHITE, VEINS, LARGE, 100, new Rock[]{LIMESTONE, MARBLE}, 5, 128, 80, 60));
        b.add(new OreSpawnData(MAGNETITE, VEINS, MEDIUM, 150, SEDIMENTARY, 5, 128, 80, 60));
        b.add(new OreSpawnData(LIMONITE, VEINS, MEDIUM, 150, SEDIMENTARY, 5, 128, 80, 60));
        b.add(new OreSpawnData(SPHALERITE, VEINS, MEDIUM, 100, METAMORPHIC, 5, 128, 80, 60));
        b.add(new OreSpawnData(TETRAHEDRITE, VEINS, MEDIUM, 120, METAMORPHIC, 5, 128, 80, 60));
        b.add(new OreSpawnData(BITUMINOUS_COAL, DEFAULT, LARGE, 100, SEDIMENTARY, 5, 128, 90, 40));
        b.add(new OreSpawnData(LIGNITE, DEFAULT, MEDIUM, 100, SEDIMENTARY, 5, 128, 90, 40));

        // Minerals
        b.add(new OreSpawnData(KAOLINITE, DEFAULT, MEDIUM, 90, SEDIMENTARY, 5, 128, 80, 60));
        b.add(new OreSpawnData(GYPSUM, VEINS, LARGE, 120, SEDIMENTARY, 5, 128, 80, 60));
        //b.add(new OreSpawnData(SATINSPAR, VEINS, SMALL, 150, SEDIMENTARY, 5, 128, 40, 60));
        //b.add(new OreSpawnData(SELENITE, VEINS, MEDIUM, 125, IGNEOUS_EXTRUSIVE, 5, 128, 60, 60));
        b.add(new OreSpawnData(GRAPHITE, VEINS, MEDIUM, 100, new Rock[]{MARBLE, GNEISS, QUARTZITE, SCHIST}, 5, 128, 80, 60));
        b.add(new OreSpawnData(KIMBERLITE, VEINS, MEDIUM, 200, new Rock[]{GABBRO}, 5, 128, 30, 80));
        //b.add(new OreSpawnData(PETRIFIED_WOOD, VEINS, MEDIUM, 100, new Rock[]{GRANITE, LIMESTONE}, new Category[]{IGNEOUS_EXTRUSIVE, METAMORPHIC}, 5, 128, 60, 80));
        //b.add(new OreSpawnData(SULFUR, VEINS, MEDIUM, 100, new Category[]{IGNEOUS_EXTRUSIVE, SEDIMENTARY}, 5, 128, 60, 60));
        b.add(new OreSpawnData(JET, VEINS, LARGE, 110, new Category[]{SEDIMENTARY}, 5, 128, 80, 60));
        //b.add(new OreSpawnData(MICROCLINE, VEINS, LARGE, 100, new Rock[]{LIMESTONE, MARBLE}, 5, 128, 60, 60));
        b.add(new OreSpawnData(PITCHBLENDE, VEINS, SMALL, 150, new Rock[]{GRANITE}, 5, 128, 80, 60));
        b.add(new OreSpawnData(CINNABAR, VEINS, SMALL, 150, new Rock[]{SHALE, QUARTZITE}, new Category[]{IGNEOUS_EXTRUSIVE}, 5, 128, 30, 80));
        b.add(new OreSpawnData(CRYOLITE, VEINS, SMALL, 100, GRANITE, 5, 128, 80, 60));
        b.add(new OreSpawnData(SALTPETER, VEINS, MEDIUM, 120, SEDIMENTARY, 5, 128, 80, 60));
        //b.add(new OreSpawnData(SERPENTINE, VEINS, LARGE, 100, SEDIMENTARY, 5, 128, 90, 40));
        b.add(new OreSpawnData(SYLVITE, VEINS, MEDIUM, 100, ROCKSALT, 5, 128, 90, 40));
        b.add(new OreSpawnData(BORAX, VEINS, LARGE, 120, ROCKSALT, 5, 128, 80, 60));
        b.add(new OreSpawnData(LAPIS_LAZULI, VEINS, LARGE, 120, MARBLE, 5, 128, 80, 60));
        //b.add(new OreSpawnData(OLIVINE, VEINS, SMALL, 150, MARBLE, 5, 128, 40, 80));

        // Surface ores
        b.add(new OreSpawnData(NATIVE_COPPER, VEINS, SMALL, 35, IGNEOUS_EXTRUSIVE, 128, 240, 40, 40));
        b.add(new OreSpawnData(CASSITERITE, VEINS, SMALL, 35, GRANITE, 128, 240, 40, 40));
        b.add(new OreSpawnData(BISMUTHINITE, VEINS, SMALL, 35, new Category[]{IGNEOUS_EXTRUSIVE, SEDIMENTARY}, 128, 240, 40, 40));
        b.add(new OreSpawnData(SPHALERITE, VEINS, SMALL, 35, METAMORPHIC, 128, 240, 40, 40));
        b.add(new OreSpawnData(TETRAHEDRITE, VEINS, SMALL, 35, METAMORPHIC, 128, 240, 40, 40));

        ORE_SPAWN_DATA = b.build();
    }

    public final Ore ore;
    public final SpawnType type;
    public final SpawnSize size;
    public final int rarity;
    public final ImmutableList<Rock> baseRocks;
    public final int minY;
    public final int maxY;
    public final float densityVertical;
    public final float densityHorizontal;

    private OreSpawnData(Ore ore, SpawnType type, SpawnSize size, int rarity, Category[] baseRocksCategories, int minY, int maxY, int densityVertical, int densityHorizontal)
    {
        this(ore, type, size, rarity, null, baseRocksCategories, minY, maxY, densityVertical, densityHorizontal);
    }

    private OreSpawnData(Ore ore, SpawnType type, SpawnSize size, int rarity, Rock[] baseRocks, int minY, int maxY, int densityVertical, int densityHorizontal)
    {
        this(ore, type, size, rarity, baseRocks, null, minY, maxY, densityVertical, densityHorizontal);
    }

    private OreSpawnData(Ore ore, SpawnType type, SpawnSize size, int rarity, Category baseRocksCategorie, int minY, int maxY, int densityVertical, int densityHorizontal)
    {
        this(ore, type, size, rarity, null, new Category[]{baseRocksCategorie}, minY, maxY, densityVertical, densityHorizontal);
    }

    private OreSpawnData(Ore ore, SpawnType type, SpawnSize size, int rarity, Rock baseRock, int minY, int maxY, int densityVertical, int densityHorizontal)
    {
        this(ore, type, size, rarity, new Rock[]{baseRock}, null, minY, maxY, densityVertical, densityHorizontal);
    }

    private OreSpawnData(Ore ore, SpawnType type, SpawnSize size, int rarity, Rock[] baseRocks, Category[] baseRocksCategories, int minY, int maxY, int densityVertical, int densityHorizontal)
    {
        this.ore = ore;
        this.type = type;
        this.size = size;
        this.rarity = rarity;

        ImmutableList.Builder<Rock> b = new ImmutableList.Builder<>();
        if (baseRocks != null) b.add(baseRocks);
        if (baseRocksCategories != null)
            for (Category baseRocksCategory : baseRocksCategories)
                for (Rock rock : Rock.values())
                    if (rock.category == baseRocksCategory)
                        b.add(rock);
        this.baseRocks = b.build();

        this.minY = minY;
        this.maxY = maxY;
        this.densityVertical = densityVertical * 0.01f;
        this.densityHorizontal = densityHorizontal * 0.01f;
    }

    public enum SpawnType
    {
        DEFAULT, VEINS
    }

    public enum SpawnSize
    {
        SMALL, MEDIUM, LARGE
    }

    @Override
    public String toString()
    {
        return "OreSpawnData{" +
                "ore=" + ore +
                ", type=" + type +
                ", size=" + size +
                ", rarity=" + rarity +
                ", baseRocks=" + baseRocks +
                ", minY=" + minY +
                ", maxY=" + maxY +
                ", densityVertical=" + densityVertical +
                ", densityHorizontal=" + densityHorizontal +
                '}';
    }
}
