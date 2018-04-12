package net.dries007.tfc.world.classic.worldgen;

import net.dries007.tfc.TerraFirmaCraft;
import net.dries007.tfc.objects.Wood;
import net.dries007.tfc.objects.blocks.wood.BlockLeavesTFC;
import net.dries007.tfc.objects.blocks.wood.BlockLogTFC;
import net.dries007.tfc.objects.blocks.wood.BlockSaplingTFC;
import net.dries007.tfc.objects.trees.TreeRegistry;
import net.dries007.tfc.objects.trees.TreeSchematicManager;
import net.dries007.tfc.util.Schematic;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenAbstractTree;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import static net.dries007.tfc.objects.blocks.BlocksTFC.isSoil;

/**
 * Ported and modified version of TFC2 code by Bioxx
 */
public class WorldGenTree extends WorldGenAbstractTree {

    protected IBlockState saplingBlock;
    protected int generateFlag;
    protected ArrayList<IBlockState> validGroundBlocks;
    protected Wood wood;

    //protected config things
    private boolean allowBarkCoveredLogs;

    public WorldGenTree() { this(Wood.ASH,false); }

    public WorldGenTree(Wood wood, boolean notify) {

        super(notify);

        this.wood = wood;
        this.saplingBlock = BlockSaplingTFC.get(wood).getDefaultState();
        this.generateFlag = 2;

        // Each tree sub-class is responsible for using (or not using) this list as part of its generation logic.
        this.validGroundBlocks = new ArrayList<>(Arrays.asList());
        this.allowBarkCoveredLogs = true;
    }

    @Override
    public boolean generate(World world, Random rand, BlockPos pos) {

        // use grow here
        return false;
    }

    @Override
    public boolean isReplaceable(World world, BlockPos pos) {
        IBlockState blockState = world.getBlockState(pos);
        return blockState.getMaterial().isReplaceable() || super.canGrowInto(blockState.getBlock());
    }

    public void grow(World world, Random random, BlockPos blockPos, IBlockState blockState)
    {
        TerraFirmaCraft.getLog().info("Sapling.grow called");
        TreeSchematicManager treeSchematicManager = TreeRegistry.instance.managerFromString(wood.name());

        int size = random.nextInt(100 ) < 20 ? 2 : random.nextInt(100 ) < 50 ? 1: 0;

        int rotation = random.nextInt(4);

        for(int i = size; i >= 0; i--)
        {
            TreeSchematicManager.TreeSchematic randomSchematic = treeSchematicManager.getRandomSchematic(random);
            int invalidCount = 0;
            int baseValidCount = 0;
            BlockPos scanPos;

            //validate the tree area
            for(Schematic.SchematicBlock schematicBlock : randomSchematic.getBlockMap())
            {
                scanPos = rotatePos(blockPos, schematicBlock.blockPos, rotation);
                if(schematicBlock.state.getBlock().getMaterial(schematicBlock.state) == Material.WOOD)
                {
                    if(!world.getBlockState(scanPos).getBlock().isReplaceable(world, scanPos))
                        invalidCount++;

                    if(schematicBlock.blockPos.getY() == 0)
                        if(isSoil(world.getBlockState(scanPos.down())))
                            baseValidCount++;

                }
            }

            if(invalidCount > randomSchematic.getLogCount() / 10 || baseValidCount < randomSchematic.getBaseCount()*0.75)
                continue;


            for(Schematic.SchematicBlock schematicBlock : randomSchematic.getBlockMap())
            {
                Process(world, wood, rotatePos(blockPos, schematicBlock.blockPos, rotation), schematicBlock.state);
            }
            break;
        }
    }

    private BlockPos rotatePos(BlockPos treePos, BlockPos localPos, int rot)
    {
        int localX = treePos.getX() + (localPos.getX() * -1) - 2;
        int localZ = treePos.getZ() + (localPos.getZ() * -1) - 2;
        int localY = treePos.getY() + localPos.getY();

        if(rot == 0)
        {
            localX = treePos.getX() + localPos.getX() + 1;
            localZ = treePos.getZ() + localPos.getZ() + 1;
        }
        else if(rot == 1)
        {
            localX = treePos.getX() + localPos.getZ();
            localZ = treePos.getZ() + (localPos.getX() * -1) - 2;
        }
        else if(rot == 2)
        {
            localX = treePos.getX()  + (localPos.getZ() * -1) -2;
            localZ = treePos.getZ() + localPos.getX();
        }

        return new BlockPos(localX, localY, localZ);
    }

    private void Process(World world, Wood wood, BlockPos blockPos, IBlockState state)
    {
        BlockLogTFC block = BlockLogTFC.get(wood);
        BlockLeavesTFC leaves = BlockLeavesTFC.get(wood);

        if(state.getBlock().getMaterial(state) == Material.WOOD)
        {
            world.setBlockState(blockPos, block.getDefaultState(), 2);
        }
        else if(state.getBlock().getMaterial(state) == Material.LEAVES)
        {
            if(world.getBlockState(blockPos).getBlock().isReplaceable(world, blockPos))
            {
                world.setBlockState(blockPos, leaves.getDefaultState(), 2);
            }
        }
        else
        {
            world.setBlockState(blockPos, state);
        }
    }
}