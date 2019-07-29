/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.world.classic.fluids;

import static net.dries007.tfc.Constants.GSON;
import static net.dries007.tfc.api.util.TFCConstants.MOD_ID;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.commons.io.FileUtils;

import com.google.common.base.Strings;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;

import net.dries007.tfc.TerraFirmaCraft;
import net.dries007.tfc.world.classic.worldgen.vein.VeinRegistry;

public enum FluidThirstRegistry
{
	INSTANCE;
	
	private static final String DEFAULT_FLUID_THIRST_LOCATION = "assets/tfc/config/fluid_thirst_data.json";
    private File fluidThirstFile;
    private Map<String, FluidThirstConfig> fluidThirstRegistry;

    @Nonnull
    public Collection<FluidThirstConfig> getFluidThirstConfigs()
    {
    	return fluidThirstRegistry.values();
    }

    @Nonnull
    public Set<String> keySet()
    {
        return fluidThirstRegistry.keySet();
    }

    @Nullable
    public FluidThirstConfig getThirstConfig(String name)
    {
        return fluidThirstRegistry.get(name);
    }
    
    public void preInit(File dir)
    {
    	File tfcDir = new File(dir, MOD_ID);
    	if (!tfcDir.exists() && !tfcDir.mkdir())
        {
            throw new Error("Problem creating TFC extra config directory.");
        }
    	fluidThirstFile = new File(tfcDir, "fluid_thirst_data.json");
    	try
        {
            if (fluidThirstFile.createNewFile())
            {
                FileUtils.copyInputStreamToFile(Objects.requireNonNull(VeinRegistry.class.getClassLoader().getResourceAsStream(DEFAULT_FLUID_THIRST_LOCATION)), fluidThirstFile);
            }
        }
        catch (IOException e)
        {
            throw new Error("Problem creating default fluid thirst config file.", e);
        }
    }
    
    public void reloadFluidThirst()
    {
    	String fluidThirstData;
    	try
        {
            fluidThirstData = FileUtils.readFileToString(fluidThirstFile, Charset.defaultCharset());
        }
        catch (IOException e)
        {
            throw new Error("Error reading fluid thirst config file.", e);
        }

        if (Strings.isNullOrEmpty(fluidThirstData))
        {
            TerraFirmaCraft.getLog().warn("The fluid thirst config file is empty! All water will produce thirst value of 0!");
        }
        else
        {
            try
            {
                fluidThirstRegistry = GSON.fromJson(fluidThirstData, new TypeToken<Map<String, FluidThirstConfig>>() {}.getType());
            }
            catch (JsonParseException e)
            {
                TerraFirmaCraft.getLog().warn("There was a serious issue parsing the fluid thirst file!! All water will produce thirst value of 0!", e);
            }
        }
        
//        GSON
    }
    
}
