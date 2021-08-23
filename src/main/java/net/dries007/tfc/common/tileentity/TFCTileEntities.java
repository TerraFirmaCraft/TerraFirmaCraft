/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.tileentity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.fmllegacy.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import net.dries007.tfc.common.blocks.EntityBlockExtension;
import net.dries007.tfc.common.blocks.IForgeBlockExtension;
import net.dries007.tfc.common.blocks.TFCBlocks;
import net.dries007.tfc.common.blocks.soil.SoilBlockType;
import net.dries007.tfc.common.types.Wood;
import net.dries007.tfc.util.Debug;
import net.dries007.tfc.util.Helpers;

import static net.dries007.tfc.TerraFirmaCraft.MOD_ID;

@SuppressWarnings("unused")
public class TFCTileEntities
{
    public static final DeferredRegister<BlockEntityType<?>> TILE_ENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITIES, MOD_ID);

    public static final Collection<RegistryObject<Block>> SAPLING_LIST = TFCBlocks.WOODS.values().stream().map(map -> map.get(Wood.BlockType.SAPLING)).collect(Collectors.toList());

    public static final RegistryObject<BlockEntityType<FarmlandTileEntity>> FARMLAND = register("farmland", FarmlandTileEntity::new, TFCBlocks.SOIL.get(SoilBlockType.FARMLAND).values().stream());
    public static final RegistryObject<BlockEntityType<SnowPileTileEntity>> SNOW_PILE = register("snow_pile", SnowPileTileEntity::new, TFCBlocks.SNOW_PILE);
    public static final RegistryObject<BlockEntityType<FirepitTileEntity>> FIREPIT = register("firepit", FirepitTileEntity::new, TFCBlocks.FIREPIT);
    public static final RegistryObject<BlockEntityType<GrillTileEntity>> GRILL = register("grill", GrillTileEntity::new, TFCBlocks.GRILL);
    public static final RegistryObject<BlockEntityType<PotTileEntity>> POT = register("pot", PotTileEntity::new, TFCBlocks.POT);
    public static final RegistryObject<BlockEntityType<TickCounterTileEntity>> TICK_COUNTER = register("tick_counter", TickCounterTileEntity::new, Stream.of(SAPLING_LIST, TFCBlocks.FRUIT_TREE_SAPLINGS.values(), TFCBlocks.FRUIT_TREE_GROWING_BRANCHES.values(), Arrays.asList(TFCBlocks.TORCH, TFCBlocks.WALL_TORCH, TFCBlocks.DEAD_BERRY_BUSH, TFCBlocks.DEAD_CANE, TFCBlocks.BANANA_SAPLING)).flatMap(Collection::stream));
    public static final RegistryObject<BlockEntityType<LogPileTileEntity>> LOG_PILE = register("log_pile", LogPileTileEntity::new, TFCBlocks.LOG_PILE);
    public static final RegistryObject<BlockEntityType<BurningLogPileTileEntity>> BURNING_LOG_PILE = register("burning_log_pile", BurningLogPileTileEntity::new, TFCBlocks.BURNING_LOG_PILE);
    public static final RegistryObject<BlockEntityType<PlacedItemTileEntity>> PLACED_ITEM = register("placed_item", PlacedItemTileEntity::new, TFCBlocks.PLACED_ITEM);
    public static final RegistryObject<BlockEntityType<PitKilnTileEntity>> PIT_KILN = register("pit_kiln", PitKilnTileEntity::new, TFCBlocks.PIT_KILN);
    public static final RegistryObject<BlockEntityType<CharcoalForgeTileEntity>> CHARCOAL_FORGE = register("charcoal_forge", CharcoalForgeTileEntity::new, TFCBlocks.CHARCOAL_FORGE);
    public static final RegistryObject<BlockEntityType<QuernTileEntity>> QUERN = register("quern", QuernTileEntity::new, TFCBlocks.QUERN);
    public static final RegistryObject<BlockEntityType<ScrapingTileEntity>> SCRAPING = register("scraping", ScrapingTileEntity::new, TFCBlocks.SCRAPING);

    public static final RegistryObject<BlockEntityType<BerryBushTileEntity>> BERRY_BUSH = register("berry_bush", BerryBushTileEntity::new, Stream.of(TFCBlocks.BANANA_PLANT, TFCBlocks.CRANBERRY_BUSH, TFCBlocks.SPREADING_BUSHES.values(), TFCBlocks.SPREADING_CANES.values(), TFCBlocks.STATIONARY_BUSHES.values()).<Supplier<? extends Block>>flatMap(Helpers::flatten));

    public static final RegistryObject<BlockEntityType<FruitTreeLeavesTileEntity>> FRUIT_TREE = register("fruit_tree", FruitTreeLeavesTileEntity::new, TFCBlocks.FRUIT_TREE_LEAVES.values().stream());

    private static final Logger LOGGER = LogManager.getLogger();

    public static void validateBlockEntities()
    {
        if (!Debug.DEBUG) return;

        final List<Block> fbeButNoEbe = new ArrayList<>(),
            ebeButNoFbe = new ArrayList<>(),
            ebButNoEbe = new ArrayList<>();
        ForgeRegistries.BLOCKS.getValues()
            .stream()
            .filter(r -> {
                assert r.getRegistryName() != null;
                return r.getRegistryName().getNamespace().equals("tfc");
            })
            .forEach(b -> {
                if (b instanceof IForgeBlockExtension ex && ex.getForgeProperties().hasBlockEntity() && !(b instanceof EntityBlockExtension))
                {
                    fbeButNoEbe.add(b);
                }
                if (b instanceof EntityBlockExtension && (!(b instanceof IForgeBlockExtension ex) || !ex.getForgeProperties().hasBlockEntity()))
                {
                    ebeButNoFbe.add(b);
                }
                if (b instanceof EntityBlock && !(b instanceof EntityBlockExtension))
                {
                    ebButNoEbe.add(b);
                }
            });

        if (logValidationErrors("Blocks found that declare a block entity in IForgeBlockExtension but do not implement EntityBlockExtension", fbeButNoEbe)
            | logValidationErrors("Blocks found that implement EntityBlockExtension but do not declare a block entity in IForgeBlockExtension", ebeButNoFbe)
            | logValidationErrors("Blocks found that implement EntityBlock but do not implement EntityBlockExtension", ebButNoEbe))
        {
            throw new AssertionError("Errors in declared block entities, fix them!");
        }
    }

    private static boolean logValidationErrors(String error, List<Block> blocks)
    {
        if (!blocks.isEmpty())
        {
            LOGGER.error(error);
            blocks.forEach(b -> LOGGER.error("{} of {}", b.getRegistryName(), b.getClass().getSimpleName()));
            return true;
        }
        return false;
    }

    @SuppressWarnings("ConstantConditions")
    private static <T extends BlockEntity> RegistryObject<BlockEntityType<T>> register(String name, BlockEntityType.BlockEntitySupplier<T> factory, Supplier<? extends Block> block)
    {
        return TILE_ENTITIES.register(name, () -> BlockEntityType.Builder.of(factory, block.get()).build(null));
    }

    @SuppressWarnings("ConstantConditions")
    private static <T extends BlockEntity> RegistryObject<BlockEntityType<T>> register(String name, BlockEntityType.BlockEntitySupplier<T> factory, Stream<? extends Supplier<? extends Block>> blocks)
    {
        return TILE_ENTITIES.register(name, () -> BlockEntityType.Builder.of(factory, blocks.map(Supplier::get).toArray(Block[]::new)).build(null));
    }
}
