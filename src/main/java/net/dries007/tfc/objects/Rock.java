/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects;

import net.minecraft.block.material.Material;
import net.minecraft.item.Item;

public enum Rock
{
    GRANITE(Category.IGNEOUS_INTRUSIVE),
    DIORITE(Category.IGNEOUS_INTRUSIVE),
    GABBRO(Category.IGNEOUS_INTRUSIVE),

    SHALE(Category.SEDIMENTARY),
    CLAYSTONE(Category.SEDIMENTARY),
    ROCKSALT(Category.SEDIMENTARY),
    LIMESTONE(Category.SEDIMENTARY),
    CONGLOMERATE(Category.SEDIMENTARY),
    DOLOMITE(Category.SEDIMENTARY),
    CHERT(Category.SEDIMENTARY),
    CHALK(Category.SEDIMENTARY),

    RHYOLITE(Category.IGNEOUS_EXTRUSIVE),
    BASALT(Category.IGNEOUS_EXTRUSIVE),
    ANDESITE(Category.IGNEOUS_EXTRUSIVE),
    DACITE(Category.IGNEOUS_EXTRUSIVE),

    QUARTZITE(Category.METAMORPHIC),
    SLATE(Category.METAMORPHIC),
    PHYLLITE(Category.METAMORPHIC),
    SCHIST(Category.METAMORPHIC),
    GNEISS(Category.METAMORPHIC),
    MARBLE(Category.METAMORPHIC);

    public final Category category;

    Rock(Category category)
    {
        this.category = category;
    }

    public enum Category
    {
        SEDIMENTARY(ToolMaterialsTFC.SED),
        METAMORPHIC(ToolMaterialsTFC.M_M),
        IGNEOUS_INTRUSIVE(ToolMaterialsTFC.IG_IN),
        IGNEOUS_EXTRUSIVE(ToolMaterialsTFC.IG_EX),;
        public final Item.ToolMaterial toolMaterial;

        Category(Item.ToolMaterial toolMaterial)
        {
            this.toolMaterial = toolMaterial;
        }
    }

    public enum Type
    {
        RAW(Material.ROCK, false, false, false),
        SMOOTH(Material.ROCK, false, false, false),
        COBBLE(Material.ROCK, true, false, false),
        BRICKS(Material.ROCK, false, false, false),
        SAND(Material.SAND, true, false, false),
        GRAVEL(Material.SAND, true, false, false),
        DIRT(Material.GROUND, false, false, false),
        GRASS(Material.GRASS, false, true, false),
        DRY_GRASS(Material.GRASS, false, true, false),
        PATH(Material.GROUND, true, false, true),
        CLAY(Material.GRASS, false, false, false),
        CLAY_GRASS(Material.GRASS, false, true, false);

        public final Material material;
        public final boolean isAffectedByGravity;
        public final boolean isGrass;
        public final boolean isPath;

        Type(Material material, boolean isAffectedByGravity, boolean isGrass, boolean isPath)
        {
            this.material = material;
            this.isAffectedByGravity = isAffectedByGravity;
            this.isGrass = isGrass;
            this.isPath = isPath;
        }

        public Type getNonGrassVersion()
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

        public Type getGrassVersion(Type spreader)
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
    }
}
