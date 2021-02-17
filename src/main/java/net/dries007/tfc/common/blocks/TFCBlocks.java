/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blocks;

import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

import net.minecraft.block.AbstractBlock.Properties;
import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.entity.EntityType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import net.dries007.tfc.common.TFCItemGroup;
import net.dries007.tfc.common.blocks.plant.Plant;
import net.dries007.tfc.common.blocks.plant.coral.Coral;
import net.dries007.tfc.common.blocks.plant.coral.TFCSeaPickleBlock;
import net.dries007.tfc.common.blocks.soil.ConnectedGrassBlock;
import net.dries007.tfc.common.blocks.soil.SandBlockType;
import net.dries007.tfc.common.blocks.soil.SoilBlockType;
import net.dries007.tfc.common.fluids.TFCFluids;
import net.dries007.tfc.common.items.TFCItems;
import net.dries007.tfc.common.tileentity.SnowPileTileEntity;
import net.dries007.tfc.common.types.Metal;
import net.dries007.tfc.common.types.Ore;
import net.dries007.tfc.common.types.Rock;
import net.dries007.tfc.common.types.Wood;
import net.dries007.tfc.util.Helpers;

import static net.dries007.tfc.TerraFirmaCraft.MOD_ID;
import static net.dries007.tfc.common.TFCItemGroup.*;


/**
 * Collection of all TFC blocks.
 * Unused is as the registry object fields themselves may be unused but they are required to register each item.
 * Whenever possible, avoid using hardcoded references to these, prefer tags or recipes.
 */
@SuppressWarnings("unused")
public final class TFCBlocks
{
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, MOD_ID);

    // Earth

    public static final Map<SoilBlockType, Map<SoilBlockType.Variant, RegistryObject<Block>>> SOIL = Helpers.mapOfKeys(SoilBlockType.class, type ->
        Helpers.mapOfKeys(SoilBlockType.Variant.class, variant ->
            register((type.name() + "/" + variant.name()).toLowerCase(), () -> type.create(variant), EARTH)
        )
    );

    public static final RegistryObject<Block> PEAT = register("peat", () -> new Block(Properties.create(Material.EARTH, MaterialColor.BLACK_TERRACOTTA).harvestTool(ToolType.SHOVEL).sound(SoundType.SAND).harvestLevel(0)), EARTH);
    public static final RegistryObject<Block> PEAT_GRASS = register("peat_grass", () -> new ConnectedGrassBlock(Properties.create(Material.PLANTS).tickRandomly().hardnessAndResistance(0.6F).sound(SoundType.PLANT).harvestTool(ToolType.SHOVEL).harvestLevel(0), PEAT, null, null), EARTH);

    public static final Map<SandBlockType, RegistryObject<Block>> SAND = Helpers.mapOfKeys(SandBlockType.class, type ->
        register(("sand/" + type.name()).toLowerCase(), type::create, EARTH)
    );

    public static final Map<GroundcoverBlockType, RegistryObject<Block>> GROUNDCOVER = Helpers.mapOfKeys(GroundcoverBlockType.class, type ->
        register(("groundcover/" + type.name()).toLowerCase(), () -> new GroundcoverBlock(type), block -> new BlockItem(block, new Item.Properties().group(EARTH)), type.shouldCreateBlockItem())
    );

    public static final RegistryObject<Block> SEA_ICE = register("sea_ice", () -> new SeaIceBlock(AbstractBlock.Properties.create(Material.ICE).speedFactor(0.98f).tickRandomly().hardnessAndResistance(0.5f).sound(SoundType.GLASS).notSolid().setAllowsSpawn(TFCBlocks::onlyPolarBears)), EARTH);
    public static final RegistryObject<SnowPileBlock> SNOW_PILE = register("snow_pile", () -> new SnowPileBlock(new ForgeBlockProperties(Properties.from(Blocks.SNOW).harvestTool(ToolType.SHOVEL).harvestLevel(0)).tileEntity(SnowPileTileEntity::new)), EARTH);
    public static final RegistryObject<ThinSpikeBlock> ICICLE = register("icicle", () -> new ThinSpikeBlock(Properties.create(Material.ICE).noDrops().hardnessAndResistance(0.4f).sound(SoundType.GLASS).notSolid()));

    public static final RegistryObject<ThinSpikeBlock> CALCITE = register("calcite", () -> new ThinSpikeBlock(Properties.create(Material.GLASS).noDrops().hardnessAndResistance(0.2f).sound(SoundType.BONE)));

    // Ores

    public static final Map<Rock.Default, Map<Ore.Default, RegistryObject<Block>>> ORES = Helpers.mapOfKeys(Rock.Default.class, rock ->
        Helpers.mapOfKeys(Ore.Default.class, ore -> !ore.isGraded(), ore ->
            register(("ore/" + ore.name() + "/" + rock.name()).toLowerCase(), () -> new Block(Block.Properties.create(Material.ROCK).sound(SoundType.STONE).hardnessAndResistance(3, 10).harvestTool(ToolType.PICKAXE).harvestLevel(0)), TFCItemGroup.ORES)
        )
    );
    public static final Map<Rock.Default, Map<Ore.Default, Map<Ore.Grade, RegistryObject<Block>>>> GRADED_ORES = Helpers.mapOfKeys(Rock.Default.class, rock ->
        Helpers.mapOfKeys(Ore.Default.class, Ore.Default::isGraded, ore ->
            Helpers.mapOfKeys(Ore.Grade.class, grade ->
                register(("ore/" + grade.name() + "_" + ore.name() + "/" + rock.name()).toLowerCase(), () -> new Block(Block.Properties.create(Material.ROCK).sound(SoundType.STONE).hardnessAndResistance(3, 10).harvestTool(ToolType.PICKAXE).harvestLevel(0)), TFCItemGroup.ORES)
            )
        )
    );
    public static final Map<Ore.Default, RegistryObject<Block>> SMALL_ORES = Helpers.mapOfKeys(Ore.Default.class, Ore.Default::isGraded, type ->
        register(("ore/small_" + type.name()).toLowerCase(), () -> GroundcoverBlock.looseOre(Properties.create(Material.PLANTS).hardnessAndResistance(0.05F, 0.0F).sound(SoundType.NETHER_ORE).notSolid()), TFCItemGroup.ORES)
    );

    // Rock Stuff

    public static final Map<Rock.Default, Map<Rock.BlockType, RegistryObject<Block>>> ROCK_BLOCKS = Helpers.mapOfKeys(Rock.Default.class, rock ->
        Helpers.mapOfKeys(Rock.BlockType.class, type ->
            register(("rock/" + type.name() + "/" + rock.name()).toLowerCase(), () -> type.create(rock), ROCK_STUFFS)
        )
    );

    public static final Map<Rock.Default, Map<Rock.BlockType, RegistryObject<SlabBlock>>> ROCK_SLABS = Helpers.mapOfKeys(Rock.Default.class, rock ->
        Helpers.mapOfKeys(Rock.BlockType.class, Rock.BlockType::hasVariants, type ->
            register(("rock/" + type.name() + "/" + rock.name()).toLowerCase() + "_slab", () -> type.createSlab(rock), ROCK_STUFFS)
        )
    );

    public static final Map<Rock.Default, Map<Rock.BlockType, RegistryObject<StairsBlock>>> ROCK_STAIRS = Helpers.mapOfKeys(Rock.Default.class, rock ->
        Helpers.mapOfKeys(Rock.BlockType.class, Rock.BlockType::hasVariants, type ->
            register(("rock/" + type.name() + "/" + rock.name()).toLowerCase() + "_stairs", () -> type.createStairs(rock), ROCK_STUFFS)
        )
    );

    public static final Map<Rock.Default, Map<Rock.BlockType, RegistryObject<Block>>> ROCK_WALLS = Helpers.mapOfKeys(Rock.Default.class, rock ->
        Helpers.mapOfKeys(Rock.BlockType.class, Rock.BlockType::hasVariants, type ->
            register(("rock/" + type.name() + "/" + rock.name()).toLowerCase() + "_wall", () -> type.createWall(rock), ROCK_STUFFS)
        )
    );

    // Metals

    public static final Map<Metal.Default, Map<Metal.BlockType, RegistryObject<Block>>> METALS = Helpers.mapOfKeys(Metal.Default.class, metal ->
        Helpers.mapOfKeys(Metal.BlockType.class, type -> type.hasMetal(metal), type ->
            register(("metal/" + type.name() + "/" + metal.name()).toLowerCase(), type.create(metal), METAL)
        )
    );

    // Wood

    public static final Map<Wood.Default, Map<Wood.BlockType, RegistryObject<Block>>> WOODS = Helpers.mapOfKeys(Wood.Default.class, wood ->
        Helpers.mapOfKeys(Wood.BlockType.class, type ->
            register(type.nameFor(wood), type.create(wood), WOOD)
        )
    );

    // Flora

    public static final Map<Plant, RegistryObject<Block>> PLANTS = Helpers.mapOfKeys(Plant.class, plant ->
        register(("plant/" + plant.name()).toLowerCase(), plant::create, block -> plant.createBlockItem(block, new Item.Properties().group(FLORA)), plant.needsItem())
    );

    public static final Map<Coral.Color, Map<Coral.BlockType, RegistryObject<Block>>> CORAL = Helpers.mapOfKeys(Coral.Color.class, color ->
        Helpers.mapOfKeys(Coral.BlockType.class, type ->
            register("coral/" + color.toString().toLowerCase() + "_" + type.toString().toLowerCase(), type.create(color), block -> type.createBlockItem(block, new Item.Properties().group(FLORA)), type.needsItem())
        )
    );

    public static final RegistryObject<Block> SEA_PICKLE = register("sea_pickle", () -> new TFCSeaPickleBlock(AbstractBlock.Properties.create(Material.OCEAN_PLANT, MaterialColor.GREEN)
        .setLightLevel((state) -> TFCSeaPickleBlock.isDead(state) ? 0 : 3 + 3 * state.get(SeaPickleBlock.PICKLES)).sound(SoundType.SLIME).notSolid()), FLORA);

    // Misc

    public static final RegistryObject<Block> THATCH = register("thatch", () -> new ThatchBlock(new ForgeBlockProperties(Properties.create(Material.PLANTS).hardnessAndResistance(0.6F, 0.4F).notSolid().sound(SoundType.PLANT)).flammable(50, 100)), MISC);
    public static final RegistryObject<Block> THATCH_BED = register("thatch_bed", () -> new ThatchBedBlock(Properties.create(Material.PLANTS).hardnessAndResistance(0.6F, 0.4F)), MISC);

    // Fluids

    public static final Map<Metal.Default, RegistryObject<FlowingFluidBlock>> METAL_FLUIDS = Helpers.mapOfKeys(Metal.Default.class, metal ->
        register("fluid/metal/" + metal.name().toLowerCase(), () -> new FlowingFluidBlock(TFCFluids.METALS.get(metal).getSecond(), Properties.create(TFCMaterials.MOLTEN_METAL).notSolid().hardnessAndResistance(100f).noDrops()))
    );

    public static final RegistryObject<FlowingFluidBlock> SALT_WATER = register("fluid/salt_water", () -> new FlowingFluidBlock(TFCFluids.SALT_WATER.getSecond(), Properties.create(TFCMaterials.SALT_WATER).notSolid().hardnessAndResistance(100f).noDrops()));
    public static final RegistryObject<FlowingFluidBlock> SPRING_WATER = register("fluid/spring_water", () -> new FlowingFluidBlock(TFCFluids.SPRING_WATER.getSecond(), Properties.create(TFCMaterials.SPRING_WATER).notSolid().hardnessAndResistance(100f).noDrops()));

    public static boolean always(BlockState state, IBlockReader world, BlockPos pos)
    {
        return true;
    }

    public static boolean never(BlockState state, IBlockReader world, BlockPos pos)
    {
        return false;
    }

    public static boolean onlyPolarBears(BlockState state, IBlockReader world, BlockPos pos, EntityType<?> type)
    {
        return type == EntityType.POLAR_BEAR; // todo: does this need to be expanded?
    }

    private static <T extends Block> RegistryObject<T> register(String name, Supplier<T> blockSupplier)
    {
        return register(name, blockSupplier, block -> null, false);
    }

    private static <T extends Block> RegistryObject<T> register(String name, Supplier<T> blockSupplier, ItemGroup group)
    {
        return register(name, blockSupplier, block -> new BlockItem(block, new Item.Properties().group(group)), true);
    }

    private static <T extends Block> RegistryObject<T> register(String name, Supplier<T> blockSupplier, Item.Properties blockItemProperties)
    {
        return register(name, blockSupplier, block -> new BlockItem(block, blockItemProperties), true);
    }

    private static <T extends Block> RegistryObject<T> register(String name, Supplier<T> blockSupplier, Function<T, ? extends BlockItem> blockItemFactory, boolean hasItemBlock)
    {
        RegistryObject<T> block = BLOCKS.register(name, blockSupplier);
        if (hasItemBlock)
        {
            TFCItems.ITEMS.register(name, () -> blockItemFactory.apply(block.get()));
        }
        return block;
    }
}