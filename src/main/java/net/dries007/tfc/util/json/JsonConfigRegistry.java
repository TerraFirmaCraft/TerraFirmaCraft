/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.util.json;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.commons.io.FileUtils;

import net.dries007.tfc.ConfigTFC;
import net.dries007.tfc.TerraFirmaCraft;
import net.dries007.tfc.api.capability.damage.CapabilityDamageResistance;
import net.dries007.tfc.objects.entity.animal.AnimalFood;
import net.dries007.tfc.world.classic.worldgen.vein.VeinRegistry;

import static net.dries007.tfc.TerraFirmaCraft.MOD_ID;

public enum JsonConfigRegistry
{
    INSTANCE;
    private static final String DEFAULT_ANIMAL_FOOD = "assets/tfc/config/animal_food_data.json";
    private static final String DEFAULT_DAMAGE_RESISTANCE = "assets/tfc/config/entity_resistance_data.json";
    private static final String DEFAULT_ORE_SPAWN = "assets/tfc/config/ore_spawn_data.json";

    private File tfcConfigDir;

    /**
     * Creates the TFC config directory, and populates it if empty
     *
     * @param dir the mod config directory root
     */
    public void preInit(File dir)
    {
        // Init base config dir
        TerraFirmaCraft.getLog().info("Loading or creating TFC config directory");
        tfcConfigDir = new File(dir, MOD_ID);
        if (!tfcConfigDir.exists() && !tfcConfigDir.mkdir())
        {
            throw new Error("Problem creating TFC extra config directory.");
        }

        // Create or overwrite our default ore gen file
        if (ConfigTFC.General.OVERRIDES.forceDefaultOreGenFile)
        {
            // Create default vein file
            try
            {
                File defaultFile = new File(tfcConfigDir, "ore_spawn_data.json");
                if (defaultFile.exists())
                {
                    // Back up the file, in case of illiteracy
                    FileUtils.copyFile(defaultFile, new File(defaultFile.getPath() + ".old"));
                    // And replace the contents
                    FileUtils.copyInputStreamToFile(Objects.requireNonNull(JsonConfigRegistry.class.getClassLoader().getResourceAsStream(DEFAULT_ORE_SPAWN)), defaultFile);
                }
                else if (defaultFile.createNewFile())
                {
                    FileUtils.copyInputStreamToFile(Objects.requireNonNull(JsonConfigRegistry.class.getClassLoader().getResourceAsStream(DEFAULT_ORE_SPAWN)), defaultFile);
                }
            }
            catch (IOException e)
            {
                throw new Error("Problem creating default ore vein config file.", e);
            }
        }
        File defaultFile = new File(tfcConfigDir, "entity_resistance_data.json");
        try
        {
            if (defaultFile.createNewFile())
            {
                FileUtils.copyInputStreamToFile(Objects.requireNonNull(JsonConfigRegistry.class.getClassLoader().getResourceAsStream(DEFAULT_DAMAGE_RESISTANCE)), defaultFile);
            }
        }
        catch (IOException e)
        {
            throw new Error("Problem creating default entity resistance config file.", e);
        }
        defaultFile = new File(tfcConfigDir, "animal_food_data.json");
        try
        {
            if (defaultFile.createNewFile())
            {
                FileUtils.copyInputStreamToFile(Objects.requireNonNull(JsonConfigRegistry.class.getClassLoader().getResourceAsStream(DEFAULT_ANIMAL_FOOD)), defaultFile);
            }
        }
        catch (IOException e)
        {
            throw new Error("Problem creating default animal food config file.", e);
        }
    }

    public void postInit()
    {
        Path[] recursivePathList;
        try
        {
            recursivePathList = Files.walk(tfcConfigDir.toPath()).filter(f -> Files.isRegularFile(f) && f.toString().endsWith(".json")).toArray(Path[]::new);
        }
        catch (IOException e)
        {
            TerraFirmaCraft.getLog().error("Unable to read files in the config directory! TFC will not generate any ore veins or load any damage resitances!");
            TerraFirmaCraft.getLog().error("Error: ", e);
            return;
        }
        for (Path path : recursivePathList)
        {
            try
            {
                // Read each file, then json parse each file individually into a map, so each vein can be parsed by GSON independently
                String fileContents = new String(Files.readAllBytes(path), Charset.defaultCharset());
                JsonObject jsonObject = new JsonParser().parse(fileContents).getAsJsonObject();
                JsonElement loader = jsonObject.get("#loader");
                if (loader != null && "tfc:damage_resistance".equals(loader.getAsString()))
                {
                    CapabilityDamageResistance.readFile(jsonObject.entrySet());
                }
                else if (loader != null && "tfc:animal_food".equals(loader.getAsString()))
                {
                    AnimalFood.readFile(jsonObject.entrySet());
                }
                else
                {
                    // Defaults to the vein loader, this will be thrown out at 1.15 anyway
                    String veinPath = tfcConfigDir.toPath().relativize(path.getParent()).toString();
                    VeinRegistry.INSTANCE.readFile(jsonObject.entrySet(), veinPath);
                }

            }
            catch (IOException e)
            {
                // Don't crash the game if one of the files error-ed, just show it in log
                TerraFirmaCraft.getLog().error("There was an error reading a json file at: " + path);
                TerraFirmaCraft.getLog().error("Error: ", e);
            }
        }
        VeinRegistry.INSTANCE.postInit();
        CapabilityDamageResistance.postInit();
    }

}
