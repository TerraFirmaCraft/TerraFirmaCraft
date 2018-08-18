/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 *
 */

package net.dries007.tfc.objects;

import java.util.function.BiFunction;

import net.minecraft.block.material.Material;

import net.dries007.tfc.api.types.Rock;
import net.dries007.tfc.objects.blocks.stone.*;

import static net.dries007.tfc.objects.RockType.FallingBlockType.*;

public enum RockType
{
    RAW(Material.ROCK, NO_FALL, false, BlockRockRaw::new), // Todo: add collapsing when broken
    SMOOTH(Material.ROCK, NO_FALL, false),
    COBBLE(Material.ROCK, FALL_HORIZONTAL, false),
    BRICKS(Material.ROCK, NO_FALL, false),
    SAND(Material.SAND, FALL_HORIZONTAL, false),
    GRAVEL(Material.SAND, FALL_HORIZONTAL, false),
    DIRT(Material.GROUND, FALL_HORIZONTAL, false),
    GRASS(Material.GRASS, FALL_HORIZONTAL, true),
    DRY_GRASS(Material.GRASS, FALL_HORIZONTAL, true),
    CLAY(Material.GRASS, FALL_VERTICAL, false),
    CLAY_GRASS(Material.GRASS, FALL_VERTICAL, true),
    FARMLAND(Material.GROUND, FALL_VERTICAL, false, BlockFarmlandTFC::new),
    PATH(Material.GROUND, FALL_VERTICAL, false, BlockPathTFC::new);

    public final Material material;
    public final boolean isGrass;

    private final FallingBlockType gravType;
    private final BiFunction<RockType, Rock, BlockRockVariant> supplier;

    RockType(Material material, FallingBlockType gravType, boolean isGrass)
    {
        // If no fall + no grass, then normal. If it can fall, then eiether fallable or fallable + connected (since grass always falls)
        this(material, gravType, isGrass, (gravType == NO_FALL && !isGrass) ? BlockRockVariant::new :
            (isGrass ? BlockRockVariantConnected::new : BlockRockVariantFallable::new));
    }

    RockType(Material material, FallingBlockType gravType, boolean isGrass, BiFunction<RockType, Rock, BlockRockVariant> supplier)
    {
        this.material = material;
        this.gravType = gravType;
        this.isGrass = isGrass;
        this.supplier = supplier;
    }

    public boolean canFall()
    {
        return gravType != NO_FALL;
    }

    public boolean canFallHorizontal()
    {
        return gravType == FALL_HORIZONTAL;
    }

    public BlockRockVariant create(Rock rock)
    {
        return supplier.apply(this, rock);
    }

    public RockType getNonGrassVersion()
    {
        if (!isGrass) return this;
        switch (this)
        {
            case GRASS:
                return DIRT;
            case DRY_GRASS:
                return DIRT;
            case CLAY_GRASS:
                return CLAY;
        }
        throw new IllegalStateException("Someone forgot to add enum constants to this switch case...");
    }

    public RockType getGrassVersion(RockType spreader)
    {
        if (!spreader.isGrass) throw new IllegalArgumentException("Non-grass can't spread.");
        switch (this)
        {
            case DIRT:
                return spreader == DRY_GRASS ? DRY_GRASS : GRASS;
            case CLAY:
                return CLAY_GRASS;
        }
        throw new IllegalArgumentException("You cannot get grass from rock types.");
    }

    protected enum FallingBlockType
    {
        NO_FALL,
        FALL_VERTICAL,
        FALL_HORIZONTAL
    }
}
