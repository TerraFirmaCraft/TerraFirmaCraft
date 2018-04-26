/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.util;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;

/**
 * todo: change to proper vanilla schematics
 */
public class Schematic implements ISchematic
{
    protected int height;
    protected int width;
    protected int centerX;
    protected int length;
    protected int centerZ;
    protected NBTTagList te;
    protected NBTTagList entities;
    protected String path;
    protected String filename;
    protected ArrayList<SchematicBlock> blockMap;
    protected AxisAlignedBB aabb;

    public Schematic(String path, String filename)
    {
        this.path = path;
        this.filename = filename;
    }

    @Override
    public boolean Load()
    {
        NBTTagCompound tree;
        blockMap = new ArrayList<SchematicBlock>();
        try
        {
            InputStream fis = getClass().getResourceAsStream(path);
            if (fis == null)
                return false;
            tree = CompressedStreamTools.readCompressed(fis);
            height = tree.getShort("Height");
            width = tree.getShort("Width");
            centerX = getCenter(width);
            length = tree.getShort("Length");
            centerZ = getCenter(length);
            int[] blockArray = null;
            byte[] dataArray = null;

            if (tree.hasKey("Blocks"))
            {
                byte[] b = tree.getByteArray("Blocks");
                blockArray = new int[b.length];

                for (int i = 0; i < b.length; i++)
                {
                    blockArray[i] = b[i];
                }
            } else if (tree.hasKey("BlocksInt"))
            {
                blockArray = tree.getIntArray("Blocks");
            }

            dataArray = tree.getByteArray("Data");
            te = tree.getTagList("TileEntities", 10);

            for (int y = 0; y < getSizeY(); y++)
            {
                for (int z = 0; z < getSizeZ(); z++)
                {
                    for (int x = 0; x < getSizeX(); x++)
                    {
                        int index = x + getSizeX() * (z + getSizeZ() * y);
                        //TODO: Add support for Tile Entity loading
                        this.blockMap.add(new SchematicBlock(blockArray[index], dataArray[index], new BlockPos(x - centerX, y, z - centerZ)));
                    }
                }
            }

            aabb = new AxisAlignedBB(0, 0, 0, width, height, length);
        } catch (FileNotFoundException e)
        {
            System.out.println("TFC FileNotFound: " + path);
            return false;
        } catch (IOException e)
        {
            System.out.println("TFC IOException: " + path);
            return false;
        }
        return true;
    }

    @Override
    public void PostProcess()
    {

    }

    @Override
    public int getSizeY()
    {
        return height;
    }

    @Override
    public void setSizeY(int y)
    {
        height = y;
    }

    @Override
    public int getSizeX()
    {
        return width;
    }

    @Override
    public void setSizeX(int x)
    {
        width = x;
    }

    @Override
    public int getSizeZ()
    {
        return length;
    }

    @Override
    public void setSizeZ(int z)
    {
        length = z;
    }

    @Override
    public NBTTagList getTileEntities()
    {
        return te;
    }

    @Override
    public void setTileEntities(NBTTagList tag)
    {
        te = tag;
    }

    @Override
    public NBTTagList getEntities()
    {
        return entities;
    }

    @Override
    public void setEntities(NBTTagList e)
    {
        entities = e;
    }

    @Override
    public String getPath()
    {
        return path;
    }

    @Override
    public void setPath(String str)
    {
        path = str;
    }

    @Override
    public String getFileName()
    {
        return filename;
    }

    @Override
    public int getCenterX()
    {
        return centerX;
    }

    @Override
    public int getCenterZ()
    {
        return centerZ;
    }

    @Override
    public ArrayList<SchematicBlock> getBlockMap()
    {
        return blockMap;
    }

    public AxisAlignedBB getBoundingBox(BlockPos pos)
    {
        return aabb.offset(pos);
    }

    //*****************
    // Private methods
    //*****************
    private int getCenter(int v)
    {
        if (v % 2 == 1)
            return (v + 1) / 2;
        return v / 2;
    }


    public class SchematicBlock
    {
        public IBlockState state;
        public BlockPos blockPos;
        public TileEntity tileEntity;

        public SchematicBlock(int id, int meta, BlockPos blockPos)
        {
            state = Block.getBlockById(id).getStateFromMeta(meta);
            this.blockPos = blockPos;
            te = null;
        }

        public SchematicBlock(IBlockState state, BlockPos blockPos)
        {
            this.state = state;
            this.blockPos = blockPos;
            te = null;
        }

        public SchematicBlock(int id, int meta, BlockPos blockPos, TileEntity tileEntity)
        {
            state = Block.getBlockById(id).getStateFromMeta(meta);
            this.blockPos = blockPos;
            this.tileEntity = tileEntity;
        }
    }
}
