/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.api.registries;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.registries.IForgeRegistry;

import net.dries007.tfc.TerraFirmaCraft;
import net.dries007.tfc.api.recipes.AlloyRecipe;
import net.dries007.tfc.api.recipes.AnvilRecipe;
import net.dries007.tfc.api.recipes.KnappingRecipe;
import net.dries007.tfc.api.recipes.WeldingRecipe;
import net.dries007.tfc.api.types.*;

/**
 * Get Registry instances for standard TFC objects here.
 */
public class TFCRegistries
{
    public static final IForgeRegistry<Rock> ROCKS = GameRegistry.findRegistry(Rock.class);
    public static final IForgeRegistry<RockCategory> ROCK_CATEGORIES = GameRegistry.findRegistry(RockCategory.class);
    public static final IForgeRegistry<Ore> ORES = GameRegistry.findRegistry(Ore.class);
    public static final IForgeRegistry<Tree> TREES = GameRegistry.findRegistry(Tree.class);
    public static final IForgeRegistry<Metal> METALS = GameRegistry.findRegistry(Metal.class);

    public static final IForgeRegistry<AlloyRecipe> ALLOYS = GameRegistry.findRegistry(AlloyRecipe.class);
    public static final IForgeRegistry<KnappingRecipe> KNAPPING = GameRegistry.findRegistry(KnappingRecipe.class);
    public static final IForgeRegistry<AnvilRecipe> ANVIL = GameRegistry.findRegistry(AnvilRecipe.class);
    public static final IForgeRegistry<WeldingRecipe> WELDING = GameRegistry.findRegistry(WeldingRecipe.class);

    public static final IForgeRegistry<Plant> PLANTS = GameRegistry.findRegistry(Plant.class);

    static
    {
        // Make sure all public static final fields have values, should stop people from prematurely loading this class.
        try
        {
            int publicStaticFinal = Modifier.PUBLIC | Modifier.STATIC | Modifier.FINAL;

            for (Field field : TFCRegistries.class.getFields())
            {
                if (!field.getType().isAssignableFrom(IForgeRegistry.class))
                {
                    TerraFirmaCraft.getLog().warn("[Please inform developers] Weird field? (Not a registry) {}", field);
                    continue;
                }
                if ((field.getModifiers() & publicStaticFinal) != publicStaticFinal)
                {
                    TerraFirmaCraft.getLog().warn("[Please inform developers] Weird field? (not Public Static Final) {}", field);
                    continue;
                }
                if (field.get(null) == null)
                {
                    throw new RuntimeException("Oh nooo! Someone tried to use the registries before they exist. Now everything is broken!");
                }
            }
        }
        catch (Exception e)
        {
            TerraFirmaCraft.getLog().fatal("Fatal error! This is likely a programming mistake.", e);
            throw new RuntimeException(e);
        }
    }
}
