/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.tileentity;

import java.util.Arrays;
import java.util.Collection;
import java.util.function.Supplier;

import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import net.dries007.tfc.common.blocks.TFCBlocks;
import net.dries007.tfc.common.blocks.soil.SoilBlockType;

import static net.dries007.tfc.TerraFirmaCraft.MOD_ID;

@SuppressWarnings("unused")
public class TFCTileEntities
{
    public static final DeferredRegister<TileEntityType<?>> TILE_ENTITIES = DeferredRegister.create(ForgeRegistries.TILE_ENTITIES, MOD_ID);

    public static final RegistryObject<TileEntityType<FarmlandTileEntity>> FARMLAND = register("farmland", FarmlandTileEntity::new, TFCBlocks.SOIL.get(SoilBlockType.FARMLAND).values());
    public static final RegistryObject<TileEntityType<SnowPileTileEntity>> SNOW_PILE = register("snow_pile", SnowPileTileEntity::new, TFCBlocks.SNOW_PILE);
    public static final RegistryObject<TileEntityType<FirepitTileEntity>> FIREPIT = register("firepit", FirepitTileEntity::new, TFCBlocks.FIREPIT);
    public static final RegistryObject<TileEntityType<GrillTileEntity>> GRILL = register("grill", GrillTileEntity::new, TFCBlocks.GRILL);
    public static final RegistryObject<TileEntityType<PotTileEntity>> POT = register("pot", PotTileEntity::new, TFCBlocks.POT);
    public static final RegistryObject<TileEntityType<TickCounterTileEntity>> TICK_COUNTER = register("tick_counter", TickCounterTileEntity::new, Arrays.asList(TFCBlocks.TORCH, TFCBlocks.WALL_TORCH));
    public static final RegistryObject<TileEntityType<LogPileTileEntity>> LOG_PILE = register("log_pile", LogPileTileEntity::new, TFCBlocks.LOG_PILE);
    public static final RegistryObject<TileEntityType<BurningLogPileTileEntity>> BURNING_LOG_PILE = register("burning_log_pile", BurningLogPileTileEntity::new, TFCBlocks.BURNING_LOG_PILE);
    public static final RegistryObject<TileEntityType<PlacedItemTileEntity>> PLACED_ITEM = register("placed_item", PlacedItemTileEntity::new, TFCBlocks.PLACED_ITEM);
    public static final RegistryObject<TileEntityType<PitKilnTileEntity>> PIT_KILN = register("pit_kiln", PitKilnTileEntity::new, TFCBlocks.PIT_KILN);

    @SuppressWarnings("ConstantConditions")
    private static <T extends TileEntity> RegistryObject<TileEntityType<T>> register(String name, Supplier<T> factory, Supplier<? extends Block> block)
    {
        return TILE_ENTITIES.register(name, () -> TileEntityType.Builder.of(factory, block.get()).build(null));
    }

    @SuppressWarnings("ConstantConditions")
    private static <T extends TileEntity> RegistryObject<TileEntityType<T>> register(String name, Supplier<T> factory, Collection<? extends Supplier<? extends Block>> blocks)
    {
        return TILE_ENTITIES.register(name, () -> TileEntityType.Builder.of(factory, blocks.stream().map(Supplier::get).toArray(Block[]::new)).build(null));
    }
}
