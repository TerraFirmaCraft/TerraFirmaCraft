/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.util;


import java.util.ArrayList;

import net.minecraft.nbt.NBTTagList;

/**
 * todo: change to proper vanilla schematics
 */
public interface ISchematic
{
    boolean Load();

    void PostProcess();

    /**
     * @return Schematic "Height"
     */
    int getSizeY();

    void setSizeY(int y);

    /**
     * @return Schematic "Width"
     */
    int getSizeX();

    void setSizeX(int x);

    /**
     * @return Schematic "Length"
     */
    int getSizeZ();

    void setSizeZ(int z);

    /**
     * @return Schematic "TileEntities"
     */
    NBTTagList getTileEntities();

    void setTileEntities(NBTTagList te);

    /**
     * @return Schematic "Entities"
     */
    NBTTagList getEntities();

    void setEntities(NBTTagList e);

    /**
     * Gets the file path.
     */
    String getPath();

    /**
     * Sets the file path for future reference.
     *
     * @param path The path to the schematic file
     */
    void setPath(String path);

    String getFileName();

    /**
     * @return Center of the schematic X Coordinate
     */
    int getCenterX();

    /**
     * @return Center of the schematic Z Coordinate
     */
    int getCenterZ();

    /**
     * @return Returns an Arraylist containing every block in this schematic for iteration
     */
    ArrayList<Schematic.SchematicBlock> getBlockMap();
}
