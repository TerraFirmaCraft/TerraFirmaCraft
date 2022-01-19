/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.util;

import java.util.*;

import com.google.common.collect.Lists;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ClassInheritanceMultiMap;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.ChunkProviderServer;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.event.world.ChunkDataEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

import net.dries007.tfc.ConfigTFC;
import net.dries007.tfc.api.registries.TFCRegistries;
import net.dries007.tfc.api.types.*;
import net.dries007.tfc.objects.blocks.agriculture.BlockCropDead;
import net.dries007.tfc.objects.blocks.plants.BlockMushroomTFC;
import net.dries007.tfc.objects.blocks.stone.BlockRockVariant;
import net.dries007.tfc.objects.items.ItemSeedsTFC;
import net.dries007.tfc.objects.te.TECropBase;
import net.dries007.tfc.objects.te.TEPlacedItemFlat;
import net.dries007.tfc.types.DefaultPlants;
import net.dries007.tfc.util.calendar.CalendarTFC;
import net.dries007.tfc.util.calendar.Month;
import net.dries007.tfc.util.climate.ClimateTFC;
import net.dries007.tfc.world.classic.chunkdata.ChunkDataTFC;
import net.dries007.tfc.world.classic.worldgen.WorldGenBerryBushes;
import net.dries007.tfc.world.classic.worldgen.WorldGenPlantTFC;
import net.dries007.tfc.world.classic.worldgen.WorldGenTrees;

import static net.dries007.tfc.TerraFirmaCraft.MOD_ID;
import static net.dries007.tfc.objects.blocks.agriculture.BlockCropTFC.WILD;

/**
 * Seasonally regenerates rocks, sticks, snow, plants, crops and bushes.
 */

//@SuppressWarnings({"unused", "WeakerAccess"})
@Mod.EventBusSubscriber(modid = MOD_ID)
public class WorldRegenHandler
{

    private static final RegenRocksSticks ROCKS_GEN = new RegenRocksSticks(true);
    private static final RegenWildCrops CROPS_GEN = new RegenWildCrops();
    private static final WorldGenBerryBushes BUSH_GEN = new WorldGenBerryBushes();
    public static final WorldGenPlantTFC PLANT_GEN = new WorldGenPlantTFC();
    private static final Random RANDOM = new Random();
    private static final List<ChunkPos> POSITIONS = new LinkedList<>();


    @SubscribeEvent
    public static void onChunkLoad(ChunkDataEvent.Load event)
    {
        ChunkDataTFC chunkDataTFC = ChunkDataTFC.get(event.getChunk());
        if (event.getWorld().provider.getDimension() == 0 && chunkDataTFC.isInitialized() && POSITIONS.size() < 1000)
        {
            //Only run this in the early months of each year
            if (CalendarTFC.CALENDAR_TIME.getMonthOfYear().isWithin(Month.APRIL, Month.JULY) && !chunkDataTFC.isSpawnProtected() && CalendarTFC.CALENDAR_TIME.getTotalYears() > chunkDataTFC.getLastUpdateYear())
            {
                POSITIONS.add(event.getChunk().getPos());
            }
        }
    }

    @SubscribeEvent
    public static void onWorldTick(TickEvent.WorldTickEvent event)
    {
        if (!event.world.isRemote && event.phase == TickEvent.Phase.END)
        {
            if (!POSITIONS.isEmpty())
            {
                double tps = Helpers.getTPS(event.world, 0);
                ChunkPos pos = POSITIONS.remove(0);
                if (tps > ConfigTFC.General.WORLD_REGEN.minRegenTps)
                {
                    Chunk chunk = event.world.getChunk(pos.x, pos.z);
                    BlockPos blockPos = pos.getBlock(0, 0, 0);
                    ChunkDataTFC chunkDataTFC = ChunkDataTFC.get(event.world, pos.getBlock(0, 0, 0));
                    IChunkProvider chunkProvider = event.world.getChunkProvider();
                    IChunkGenerator chunkGenerator = ((ChunkProviderServer) chunkProvider).chunkGenerator;

                    if (CalendarTFC.CALENDAR_TIME.getMonthOfYear().isWithin(Month.APRIL, Month.JULY) && !chunkDataTFC.isSpawnProtected() && CalendarTFC.CALENDAR_TIME.getTotalYears() > chunkDataTFC.getLastUpdateYear())
                    {
                        if (ConfigTFC.General.WORLD_REGEN.sticksRocksModifier > 0)
                        {
                            //Nuke any rocks and sticks in chunk.
                            removeAllPlacedItems(event.world, pos);
                            double rockModifier = ConfigTFC.General.WORLD_REGEN.sticksRocksModifier;
                            ROCKS_GEN.generate(RANDOM, pos.x, pos.z, event.world, chunkGenerator, chunkProvider);

                            final float density = chunkDataTFC.getFloraDensity();
                            List<Tree> trees = chunkDataTFC.getValidTrees();
                            int stickDensity = 3 + (int) (4f * density + 1.5f * trees.size() * rockModifier);
                            if (trees.isEmpty())
                            {
                                stickDensity = 1 + (int) (1.5f * density * rockModifier);
                            }
                            WorldGenTrees.generateLooseSticks(RANDOM, pos.x, pos.z, event.world, stickDensity);
                        }

                        //Nuke crops/mushrooms/dead crops (not sure the latter is working.
                        removeCropsAndMushrooms(event.world, pos);
                        removeSeedBags(event.world, pos);

                        float floraDensity = chunkDataTFC.getFloraDensity(); // Use for various plant based decoration (tall grass, those vanilla jungle shrub things, etc.)
                        float floraDiversity = chunkDataTFC.getFloraDiversity();
                        Plant mushroom = TFCRegistries.PLANTS.getValue(DefaultPlants.PORCINI);
                        if (mushroom != null) PLANT_GEN.setGeneratedPlant(mushroom);
                        for (float i = RANDOM.nextInt(Math.round(3 / floraDiversity)); i < (1 + floraDensity) * 5; i++)
                        {
                            BlockPos blockMushroomPos = event.world.getHeight(blockPos.add(RANDOM.nextInt(16) + 8, 0, RANDOM.nextInt(16) + 8));
                            PLANT_GEN.generate(event.world, RANDOM, blockMushroomPos);
                        }
                        CROPS_GEN.generate(RANDOM, pos.x, pos.z, event.world, chunkGenerator, chunkProvider);
                        int worldX = pos.x << 4;
                        int worldZ = pos.z << 4;
                        BlockPos blockpos = new BlockPos(worldX, 0, worldZ);
                        Biome biome = event.world.getBiome(blockpos.add(16, 0, 16));
                        removePredators(event.world, pos);
                        regenPredators(event.world, biome, worldX + 8, worldZ + 8, 16, 16, RANDOM);

                        // notably missing: berry bushes
                        chunkDataTFC.resetLastUpdateYear();
                    }
                    chunk.markDirty();
                    ((ChunkProviderServer) chunkProvider).queueUnload(chunk);
                }
            }
        }
    }

    private static void removePredators(World world, ChunkPos pos)
    {
        for (ClassInheritanceMultiMap<Entity> target : world.getChunk(pos.x, pos.z).getEntityLists())
            target.forEach(entity -> {
                if (entity instanceof IPredator || entity instanceof IHuntable && !entity.hasCustomName())
                {
                    entity.setDropItemsWhenDead(false);
                    entity.setDead();
                }
            });
    }

    private static void removeCropsAndMushrooms(World world, ChunkPos pos)
    {
        for (int xX = 0; xX < 16; ++xX)
        {
            for (int zZ = 0; zZ < 16; ++zZ)
            {
                BlockPos topPos = world.getTopSolidOrLiquidBlock(pos.getBlock(xX, 0, zZ));
                //If I'm not completely missing the point, then we have the top block for each in a chunk. Which is apparently not the top solid block ffs.
                IBlockState topState = world.getBlockState(topPos);
                Block topBlock = topState.getBlock();
                if (!topState.getMaterial().isLiquid() && (topBlock instanceof BlockCropDead || topBlock instanceof BlockMushroomTFC))
                {
                    IBlockState soil = world.getBlockState(topPos.down());
                    if (soil.getBlock() instanceof BlockRockVariant)
                    {
                        BlockRockVariant soilRock = (BlockRockVariant) soil.getBlock();
                        //Stop removing dead crops from farmland please!
                        if (soilRock.getType() != Rock.Type.FARMLAND)
                        {
                            world.removeTileEntity(topPos);
                            world.setBlockToAir(topPos);
                        }
                    }
                }
            }
        }
        //Remove all the crops
        Map<BlockPos, TileEntity> teTargets = world.getChunk(pos.x, pos.z).getTileEntityMap();
        List<BlockPos> removals = new ArrayList<>();
        teTargets.forEach((tePos, te) -> {
            IBlockState state = world.getBlockState(tePos);
            if (te instanceof TECropBase && state.getProperties().containsKey(WILD) && state.getValue(WILD))
            {
                removals.add(tePos);
            }
        });
        for (BlockPos tePos : removals)
        {
            world.removeTileEntity(tePos);
            world.setBlockToAir(tePos);
        }
    }


    private static void removeSeedBags(World world, ChunkPos pos)
    {
        List<Entity> removals = new ArrayList<>();
        for (ClassInheritanceMultiMap<Entity> target : world.getChunk(pos.x, pos.z).getEntityLists())
        {
            target.forEach(entity -> {
                if (entity instanceof EntityItem && ((EntityItem) entity).getItem().getItem() instanceof ItemSeedsTFC)
                {
                    removals.add(entity);
                }
            });
        }
        for (Entity e : removals)
        {
            world.removeEntity(e);
        }
    }

    private static void removeAllPlacedItems(World world, ChunkPos pos)
    {
        List<BlockPos> removals = new ArrayList<>();
        world.getChunk(pos.x, pos.z).getTileEntityMap().forEach((tePos, te) -> {
            if (te instanceof TEPlacedItemFlat)
            {
                removals.add(tePos);
            }
        });
        for (BlockPos tePos : removals)
        {
            world.removeTileEntity(tePos);
            world.setBlockToAir(tePos);
        }
    }

    public static void regenPredators(World worldIn, Biome biomeIn, int centerX, int centerZ, int diameterX, int diameterZ, Random randomIn)
    {
        final BlockPos chunkBlockPos = new BlockPos(centerX, 0, centerZ);
        final float temperature = ClimateTFC.getAvgTemp(worldIn, chunkBlockPos);
        final float rainfall = ChunkDataTFC.getRainfall(worldIn, chunkBlockPos);
        final float floraDensity = ChunkDataTFC.getFloraDensity(worldIn, chunkBlockPos);
        final float floraDiversity = ChunkDataTFC.getFloraDiversity(worldIn, chunkBlockPos);
        ForgeRegistries.ENTITIES.getValuesCollection().stream().filter((x) -> {
            if (ICreatureTFC.class.isAssignableFrom(x.getEntityClass()))
            {
                Entity ent = x.newInstance(worldIn);
                if (ent instanceof IPredator || ent instanceof IHuntable)
                {
                    int weight = ((ICreatureTFC) ent).getSpawnWeight(biomeIn, temperature, rainfall, floraDensity, floraDiversity);
                    return weight > 0 && randomIn.nextInt(weight) == 0;
                }
            }
            return false;
        }).findAny().ifPresent((entityEntry) -> doGroupSpawning(entityEntry, worldIn, centerX, centerZ, diameterX, diameterZ, randomIn));
    }

    private static void doGroupSpawning(EntityEntry entityEntry, World worldIn, int centerX, int centerZ, int diameterX, int diameterZ, Random rand)
    {
        List<EntityLiving> group = Lists.newArrayList();
        EntityLiving creature = (EntityLiving) entityEntry.newInstance(worldIn);
        if (creature instanceof ICreatureTFC)
        {
            ICreatureTFC creatureTFC = (ICreatureTFC) creature;
            int fallback = 5;
            int individuals = Math.max(1, creatureTFC.getMinGroupSize()) + rand.nextInt(creatureTFC.getMaxGroupSize() - Math.max(0, creatureTFC.getMinGroupSize() - 1));

            while (individuals > 0)
            {
                int i = centerX + rand.nextInt(diameterX);
                int k = centerZ + rand.nextInt(diameterZ);
                BlockPos blockpos = worldIn.getTopSolidOrLiquidBlock(new BlockPos(i, 0, k));
                creature.setLocationAndAngles((float) i + 0.5F, blockpos.getY(), (float) k + 0.5F, rand.nextFloat() * 360.0F, 0.0F);
                if (creature.getCanSpawnHere())
                {
                    if (ForgeEventFactory.canEntitySpawn(creature, worldIn, (float) i + 0.5F, (float) blockpos.getY(), (float) k + 0.5F, null) == Event.Result.DENY)
                    {
                        --fallback;
                        if (fallback > 0)
                        {
                            continue;
                        }
                        break;
                    }
                    else
                    {
                        fallback = 5;
                        worldIn.spawnEntity(creature);
                        group.add(creature);
                        creature.onInitialSpawn(worldIn.getDifficultyForLocation(new BlockPos(creature)), null);
                        --individuals;
                        if (individuals > 0)
                        {
                            creature = (EntityLiving) entityEntry.newInstance(worldIn);
                            creatureTFC = (ICreatureTFC) creature;
                        }
                    }
                }
                else
                {
                    --fallback;
                    if (fallback <= 0)
                    {
                        break;
                    }
                }
            }

            creatureTFC.getGroupingRules().accept(group, rand);
        }
    }
}

