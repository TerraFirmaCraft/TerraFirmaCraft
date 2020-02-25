/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.api.capability.damage;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import org.apache.commons.io.FileUtils;

import net.dries007.tfc.TerraFirmaCraft;

import static net.dries007.tfc.Constants.GSON;
import static net.dries007.tfc.TerraFirmaCraft.MOD_ID;

public enum DamageResistanceRegistry
{
    INSTANCE;

    private static final String DEFAULT_LOCATION = "assets/tfc/config/entity_resistance_data.json";

    private File damageConfigDir;

    /**
     * Creates the TFC ddamage resistance config directory, and populates it if empty
     *
     * @param dir the mod config directory root
     */
    public void preInit(File dir)
    {
        // Init base config dir
        TerraFirmaCraft.getLog().info("Loading or creating TFC damage resistance config directory");
        damageConfigDir = new File(dir, MOD_ID + "/damage_resistance");
        if (!damageConfigDir.exists() && !damageConfigDir.mkdir())
        {
            throw new Error("Problem creating TFC extra config directory.");
        }

        // If the directory is empty, then create a new one
        try
        {
            if (!Files.list(damageConfigDir.toPath()).findAny().isPresent())
            {
                // Create default entity resistance data file
                File defaultFile = new File(damageConfigDir, "entity_resistance_data.json");
                try
                {
                    if (defaultFile.createNewFile())
                    {
                        FileUtils.copyInputStreamToFile(Objects.requireNonNull(DamageResistanceRegistry.class.getClassLoader().getResourceAsStream(DEFAULT_LOCATION)), defaultFile);
                    }
                }
                catch (IOException e)
                {
                    throw new Error("Problem creating default entity resistance config file.", e);
                }
            }
        }
        catch (IOException e)
        {
            TerraFirmaCraft.getLog().error("Problem creating default entity resistance config file.");
            TerraFirmaCraft.getLog().error("Exception: ", e);
        }
    }

    /**
     * Recursively scan through config dir, finding all entity resistance data files
     */
    public void postInit()
    {
        File[] allFiles;
        try
        {
            allFiles = damageConfigDir.listFiles();
            Objects.requireNonNull(allFiles);
        }
        catch (NullPointerException e)
        {
            TerraFirmaCraft.getLog().error("Unable to read files in the damage resistance directory! TFC will not set entities resistance data!");
            TerraFirmaCraft.getLog().error("Error: ", e);
            return;
        }

        int loadedEntities = 0;

        for (File resistanceFile : allFiles)
        {
            if (resistanceFile.isDirectory())
            {
                continue; // skip
            }
            try
            {
                // Read each file, then json parse each file individually into a map, so each entity resistance data can be parsed by GSON independently
                String fileContents = new String(Files.readAllBytes(resistanceFile.toPath()), Charset.defaultCharset());
                Set<Map.Entry<String, JsonElement>> allResistanceJson = new JsonParser().parse(fileContents).getAsJsonObject().entrySet();
                for (Map.Entry<String, JsonElement> entry : allResistanceJson)
                {
                    try
                    {
                        String entityName = entry.getKey();
                        DamageResistance resistance = GSON.fromJson(entry.getValue(), DamageResistance.class);

                        CapabilityDamageResistance.ENTITY_RESISTANCE.put(entityName, () -> resistance);
                        loadedEntities++;
                    }
                    catch (JsonParseException e)
                    {
                        TerraFirmaCraft.getLog().error("An entity resistance is specified incorrectly! Skipping.");
                        TerraFirmaCraft.getLog().error("Error: ", e);
                    }
                }
            }
            catch (IOException e)
            {
                // Don't crash the game if one of the files error-ed, just show it in log
                TerraFirmaCraft.getLog().error("There was an error reading an entity resistance data file at: " + resistanceFile.toPath());
                TerraFirmaCraft.getLog().error("Error: ", e);
            }
        }

        TerraFirmaCraft.getLog().info("Entity resistance data initialized, loaded a total of {} resistance configurations", loadedEntities);
    }
}
