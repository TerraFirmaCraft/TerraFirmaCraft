/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.world.classic.worldgen.vein;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import net.dries007.tfc.TerraFirmaCraft;
import net.dries007.tfc.util.collections.WeightedCollection;
import net.dries007.tfc.world.classic.worldgen.WorldGenOreVeins;

import static net.dries007.tfc.Constants.GSON;

public enum VeinRegistry
{
    INSTANCE;

    private final WeightedCollection<VeinType> weightedVeinTypes = new WeightedCollection<>();
    private final Map<String, VeinType> veinTypeRegistry = new HashMap<>();

    @Nonnull
    public WeightedCollection<VeinType> getVeins()
    {
        return weightedVeinTypes;
    }

    @Nonnull
    public Set<String> keySet()
    {
        return veinTypeRegistry.keySet();
    }

    @Nullable
    public VeinType getVein(String name)
    {
        return veinTypeRegistry.get(name);
    }

    /**
     * Wraps things up and output to log
     */
    public void postInit()
    {
        // Reset max chunk radius and number of rolls for veins
        int maxRadius = 0;
        for (VeinType type : veinTypeRegistry.values())
        {
            if (type.getWidth() > maxRadius)
            {
                maxRadius = type.getWidth();
            }
        }
        WorldGenOreVeins.CHUNK_RADIUS = 1 + (maxRadius >> 4);
        TerraFirmaCraft.getLog().info("Vein Registry Initialized, with {} veins, {} max radius, {} total weight", veinTypeRegistry.size(), maxRadius, weightedVeinTypes.getTotalWeight());
    }

    /**
     * Read file and load valid veins into registry
     *
     * @param jsonElements the json elements to read
     * @param subfolder    the current subfolder, relative to TFC config directory (used to differentiate veins in case of more than one file registers a vein with the same name)
     */
    public void readFile(Set<Map.Entry<String, JsonElement>> jsonElements, String subfolder)
    {
        for (Map.Entry<String, JsonElement> entry : jsonElements)
        {
            try
            {
                String properVeinName = entry.getKey();
                if ("#loader".equals(properVeinName)) continue; // Skip loader
                if (!subfolder.isEmpty())
                {
                    properVeinName = subfolder + "/" + properVeinName;
                }
                VeinType vein = GSON.fromJson(entry.getValue(), VeinType.class);
                vein.setRegistryName(properVeinName);

                veinTypeRegistry.put(properVeinName, vein);
                weightedVeinTypes.add(vein.getWeight(), vein);

                TerraFirmaCraft.getLog().info("Registered new vein " + vein.toString());
            }
            catch (JsonParseException e)
            {
                TerraFirmaCraft.getLog().error("An ore vein is specified incorrectly! Skipping.");
                TerraFirmaCraft.getLog().error("Error: ", e);
            }
        }
    }
}