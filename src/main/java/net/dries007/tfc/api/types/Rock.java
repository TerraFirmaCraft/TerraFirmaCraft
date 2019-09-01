/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.api.types;

import java.util.function.Function;
import javax.annotation.Nonnull;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.registries.ForgeRegistryEntry;
import net.minecraftforge.registries.ObjectHolder;

import net.dries007.tfc.objects.blocks.stone.CollapsibleStoneBlock;
import net.dries007.tfc.objects.blocks.stone.RawStoneBlock;
import net.dries007.tfc.objects.blocks.stone.StoneBlock;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.world.gen.rock.RockCategory;

import static net.dries007.tfc.TerraFirmaCraft.MOD_ID;
import static net.dries007.tfc.api.types.Rock.FallingBlockType.*;

public class Rock extends ForgeRegistryEntry<Rock>
{
    @ObjectHolder("tfc:granite")
    public static final Rock GRANITE = Helpers.getNull();
    @ObjectHolder("tfc:basalt")
    public static final Rock BASALT = Helpers.getNull();

    private final RockCategory rockCategory;
    private final ResourceLocation textureLocation;
    private final boolean isFluxStone;

    public Rock(@Nonnull ResourceLocation name, @Nonnull RockCategory rockCategory, boolean isFluxStone)
    {
        //noinspection ConstantConditions
        if (rockCategory == null)
        {
            throw new IllegalArgumentException("Rock category is not allowed to be null (on rock " + name + ")");
        }

        setRegistryName(name);
        this.rockCategory = rockCategory;
        this.textureLocation = new ResourceLocation(MOD_ID, "textures/blocks/stonetypes/raw/" + name.getPath() + ".png");
        this.isFluxStone = isFluxStone;
    }

    /**
     * Used for knapping GUI
     *
     * @return a texture resource location
     */
    public ResourceLocation getTexture()
    {
        return textureLocation;
    }

    public RockCategory getCategory()
    {
        return rockCategory;
    }

    public boolean isFluxStone()
    {
        return isFluxStone;
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public String toString()
    {
        return getRegistryName().getPath();
    }

    /*
    public enum ToolType
    {
        AXE(ItemRockAxe::new, " X   ", "XXXX ", "XXXXX", "XXXX ", " X   "),
        SHOVEL(ItemRockShovel::new, "XXX", "XXX", "XXX", "XXX", " X "),
        HOE(ItemRockHoe::new, "XXXXX", "   XX"),
        KNIFE(ItemRockKnife::new, "X ", "XX", "XX", "XX", "XX"),
        JAVELIN(ItemRockJavelin::new, "XXX  ", "XXXX ", "XXXXX", " XXX ", "  X  "),
        HAMMER(ItemRockHammer::new, "XXXXX", "XXXXX", "  X  ");

        private final Function<RockCategory, Item> supplier;
        private final String[] pattern;

        ToolType(@Nonnull Function<RockCategory, Item> supplier, String... pattern)
        {
            this.supplier = supplier;
            this.pattern = pattern;
        }

        public Item create(RockCategory category)
        {
            return supplier.apply(category);
        }

        public String[] getPattern()
        {
            return pattern;
        }
    }
    */


    public enum Type
    {
        RAW(FALL_VERTICAL, rock -> new RawStoneBlock(rock, Block.Properties.create(Material.ROCK).sound(SoundType.STONE).hardnessAndResistance(2, 10).harvestLevel(0).harvestTool(ToolType.PICKAXE))),
        SMOOTH(NO_FALL, rock -> new StoneBlock(rock, Block.Properties.create(Material.ROCK).sound(SoundType.STONE).hardnessAndResistance(1.5f, 10).harvestLevel(0).harvestTool(ToolType.PICKAXE))),
        COBBLE(FALL_HORIZONTAL, rock -> new CollapsibleStoneBlock(rock, Block.Properties.create(Material.ROCK).sound(SoundType.STONE).hardnessAndResistance(1.5f, 10).harvestLevel(0).harvestTool(ToolType.PICKAXE))),
        BRICKS(NO_FALL, rock -> new StoneBlock(rock, Block.Properties.create(Material.ROCK).sound(SoundType.STONE).hardnessAndResistance(2.0f, 10).harvestLevel(0).harvestTool(ToolType.PICKAXE))),
        GRAVEL(FALL_HORIZONTAL, rock -> new CollapsibleStoneBlock(rock, Block.Properties.create(Material.SAND, MaterialColor.STONE).sound(SoundType.STONE).hardnessAndResistance(0.8f).harvestLevel(0).harvestTool(ToolType.SHOVEL)));
        //SAND(Material.SAND, FALL_HORIZONTAL, false),
        //DIRT(Material.GROUND, FALL_HORIZONTAL, false),
        //GRASS(Material.GRASS, FALL_HORIZONTAL, true),
        //DRY_GRASS(Material.GRASS, FALL_HORIZONTAL, true),
        //CLAY(Material.CLAY, FALL_VERTICAL, false),
        //CLAY_GRASS(Material.GRASS, FALL_VERTICAL, true),
        //FARMLAND(Material.GROUND, FALL_VERTICAL, false, BlockFarmlandTFC::new),
        //PATH(Material.GROUND, FALL_VERTICAL, false, BlockPathTFC::new);

        //public final Material material;
        //public final boolean isGrass;

        private final FallingBlockType gravityType;
        private final Function<Rock, StoneBlock> supplier;

        Type(FallingBlockType gravityType, Function<Rock, StoneBlock> supplier)
        {
            this.gravityType = gravityType;
            this.supplier = supplier;
        }

        //Type(Material material, FallingBlockType gravityType, boolean isGrass)
        //{
        //    // If no fall + no grass, then normal. If it can fall, then eiether fallable or fallable + connected (since grass always falls)
        //    this(material, gravityType, isGrass, (gravityType == NO_FALL && !isGrass) ? StoneBlock::new :
        //            (isGrass ? BlockRockVariantConnected::new : BlockRockVariantFallable::new));
        //}

        //Type(Material material, FallingBlockType gravityType, boolean isGrass, BiFunction<Type, Rock, StoneBlock> supplier)
        //{
        //    this.material = material;
        //    this.gravityType = gravityType;
        //    this.isGrass = isGrass;
        //    this.supplier = supplier;
        //}

        public boolean canFall()
        {
            return gravityType != NO_FALL;
        }

        public boolean canFallHorizontal()
        {
            return gravityType == FALL_HORIZONTAL;
        }

        public StoneBlock create(Rock rock)
        {
            return supplier.apply(rock);
        }

        //public Type getNonGrassVersion()
        //{
        //    if (!isGrass) return this;
        //    switch (this)
        //    {
        //        case GRASS:
        //            return DIRT;
        //        case DRY_GRASS:
        //            return DIRT;
        //        case CLAY_GRASS:
        //            return CLAY;
        //    }
        //    throw new IllegalStateException("Someone forgot to add enum constants to this switch case...");
        //}
//
        //public Type getGrassVersion(Type spreader)
        //{
        //    if (!spreader.isGrass) throw new IllegalArgumentException("Non-grass can't spread.");
        //    switch (this)
        //    {
        //        case DIRT:
        //            return spreader == DRY_GRASS ? DRY_GRASS : GRASS;
        //        case CLAY:
        //            return CLAY_GRASS;
        //    }
        //    throw new IllegalArgumentException("You cannot get grass from rock types.");
        //}
    }

    public enum FallingBlockType
    {
        NO_FALL,
        FALL_VERTICAL,
        FALL_HORIZONTAL
    }
}
