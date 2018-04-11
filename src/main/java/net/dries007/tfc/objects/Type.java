package net.dries007.tfc.objects;

import net.minecraft.block.material.Material;

public enum Type
{
    RAW(Material.ROCK, false, false),
    SMOOTH(Material.ROCK, false, false),
    COBBLE(Material.ROCK, true, false),
    BRICKS(Material.ROCK, false, false),
    SAND(Material.SAND, true, false),
    GRAVEL(Material.SAND, true, false),
    DIRT(Material.GROUND, false, false),
    GRASS(Material.GRASS, false, true),
    DRY_GRASS(Material.GRASS, false, true),
    CLAY(Material.GRASS, false, false),
    CLAY_GRASS(Material.GRASS, false, true);

    public final Material material;
    public final boolean isAffectedByGravity;
    public final boolean isGrass;

    Type(Material material, boolean isAffectedByGravity, boolean isGrass)
    {
        this.material = material;
        this.isAffectedByGravity = isAffectedByGravity;
        this.isGrass = isGrass;
    }

    public Type getNonGrassVersion()
    {
        if (!isGrass) return this;
        switch (this)
        {
            case GRASS: return DIRT;
            case DRY_GRASS: return DIRT;
            case CLAY_GRASS: return CLAY;
        }
        throw new IllegalStateException("Someone forgot to add enum constants to this switch case...");
    }

    public Type getGrassVersion(Type spreader)
    {
        if (!spreader.isGrass) throw new IllegalArgumentException("Non-grass can't spread.");
        switch (this)
        {
            case DIRT: return spreader == DRY_GRASS ? DRY_GRASS : GRASS;
            case CLAY: return CLAY_GRASS;
        }
        throw new IllegalArgumentException("You cannot get grass from rock types.");
    }
}
