package net.dries007.tfc.util;


import java.util.HashSet;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraftforge.fml.common.IWorldGenerator;

import net.dries007.tfc.ConfigTFC;
import net.dries007.tfc.api.types.Rock;
import net.dries007.tfc.objects.blocks.BlocksTFC;
import net.dries007.tfc.objects.blocks.plants.BlockShortGrassTFC;
import net.dries007.tfc.objects.blocks.plants.BlockTallGrassTFC;
import net.dries007.tfc.objects.items.rock.ItemRock;
import net.dries007.tfc.objects.te.TEPlacedItemFlat;
import net.dries007.tfc.world.classic.ChunkGenTFC;
import net.dries007.tfc.world.classic.chunkdata.ChunkDataTFC;
import net.dries007.tfc.world.classic.worldgen.vein.Vein;

public class RegenRocksSticks implements IWorldGenerator
{
    private final boolean generateOres;
    private double factor;

    public RegenRocksSticks(boolean generateOres)
    {
        this.generateOres = generateOres;
        factor = 1;
    }

    public void setFactor(double factor)
    {
        if (factor < 0) factor = 0;
        if (factor > 1) factor = 1;
        this.factor = factor;
    }

    @Override
    public void generate(Random random, int chunkX, int chunkZ, World world, IChunkGenerator chunkGenerator, IChunkProvider chunkProvider)
    {
        if (chunkGenerator instanceof ChunkGenTFC && world.provider.getDimension() == 0)
        {
            final BlockPos chunkBlockPos = new BlockPos(chunkX << 4, 0, chunkZ << 4);
            final ChunkDataTFC baseChunkData = ChunkDataTFC.get(world, chunkBlockPos);

            // Get the proper list of veins
            Set<Vein> veins = new HashSet();
            int xoff = chunkX * 16 + 8;
            int zoff = chunkZ * 16 + 8;

            if (generateOres)
            {
                // Grab 2x2 area
                ChunkDataTFC[] chunkData = {
                    baseChunkData, // This chunk
                    ChunkDataTFC.get(world, chunkBlockPos.add(16, 0, 0)),
                    ChunkDataTFC.get(world, chunkBlockPos.add(0, 0, 16)),
                    ChunkDataTFC.get(world, chunkBlockPos.add(16, 0, 16))
                };
                if (!chunkData[0].isInitialized())
                {
                    return;
                }

                // Default to 35 below the surface, like classic
                int lowestYScan = Math.max(10, world.getTopSolidOrLiquidBlock(chunkBlockPos).getY() - ConfigTFC.General.WORLD.looseRockScan);

                for (ChunkDataTFC data : chunkData)
                {
                    veins.addAll(data.getGeneratedVeins());
                }



                if (!veins.isEmpty())
                {
                    veins.removeIf(v -> {
                        if (v.getType() == null || !v.getType().hasLooseRocks() || v.getHighestY() < lowestYScan)
                        {
                            return true;
                        }
                        return false;
                    });
                }
            }

            for (int i = 0; i < ConfigTFC.General.WORLD.looseRocksFrequency * factor; i++)
            {
                BlockPos pos = new BlockPos(
                    xoff + random.nextInt(16),
                    0,
                    zoff + random.nextInt(16)
                );
                Rock rock = baseChunkData.getRock1(pos);
                generateRock(world, pos.up(world.getTopSolidOrLiquidBlock(pos).getY()), getRandomVein(veins, pos, random), rock);
            }
        }
    }

    private void generateRock(World world, BlockPos pos, @Nullable Vein vein, Rock rock)
    {

        if (isReplaceable(world, pos) && world.getBlockState(pos.down()).isSideSolid(world, pos.down(), EnumFacing.UP) && BlocksTFC.isSoil(world.getBlockState(pos.down())))
        {
            world.setBlockState(pos, BlocksTFC.PLACED_ITEM_FLAT.getDefaultState(), 2);
            TEPlacedItemFlat tile = Helpers.getTE(world, pos, TEPlacedItemFlat.class);
            if (tile != null)
            {
                ItemStack stack = ItemStack.EMPTY;
                if (vein != null && vein.getType() != null)
                {
                    if (ConfigTFC.General.WORLD.enableLooseOres)
                    {
                        stack = vein.getType().getLooseRockItem();
                    }
                }
                if (stack.isEmpty())
                {
                    if (ConfigTFC.General.WORLD.enableLooseRocks)
                    {
                        stack = ItemRock.get(rock, 1);
                    }
                }
                if (!stack.isEmpty())
                {
                    tile.setStack(stack);
                }
            }
        }
    }

    @Nullable
    private Vein getRandomVein(Set<Vein> veins, BlockPos pos, Random rand)
    {
        if (!veins.isEmpty() && rand.nextDouble() < 0.4)
        {
            Optional<Vein> vein = veins.stream().findAny();
            if (!veins.isEmpty())
            {
                Vein veintarget = vein.get();
                if (veintarget.inRange(pos.getX(), pos.getZ(), 8))
                {
                    return veintarget;
                }
            }
        }
        return null;
    }

    private static Boolean isReplaceable(World world, BlockPos pos)
    {
        //Modified to allow replacement of grass during spring regen
        Block test = world.getBlockState(pos).getBlock();
        if (test instanceof BlockShortGrassTFC || test instanceof BlockTallGrassTFC || test.isAir(world.getBlockState(pos), world, pos))
        {
            return true;
        }
        return false;
    }

    public static void generateLooseSticks(Random rand, int chunkX, int chunkZ, World world, int amount)
    {
        if (ConfigTFC.General.WORLD.enableLooseSticks)
        {
            for (int i = 0; i < amount; i++)
            {
                final int x = chunkX * 16 + rand.nextInt(16) + 8;
                final int z = chunkZ * 16 + rand.nextInt(16) + 8;
                final BlockPos pos = world.getTopSolidOrLiquidBlock(new BlockPos(x, 0, z));

                // Use air, so it doesn't replace other replaceable world gen
                // This matches the check in BlockPlacedItemFlat for if the block can stay
                // Also, only add on soil, since this is called by the world regen handler later
                IBlockState stateDown = world.getBlockState(pos.down());
                if (isReplaceable(world, pos) && stateDown.isSideSolid(world, pos.down(), EnumFacing.UP) && BlocksTFC.isGround(stateDown))
                {
                    world.setBlockState(pos, BlocksTFC.PLACED_ITEM_FLAT.getDefaultState());
                    TEPlacedItemFlat tile = (TEPlacedItemFlat) world.getTileEntity(pos);
                    if (tile != null)
                    {
                        tile.setStack(new ItemStack(Items.STICK));
                    }
                }
            }
        }
    }

}
