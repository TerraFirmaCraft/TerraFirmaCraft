/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.world.flora;

import com.google.gson.GsonBuilder;

import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.data.TypedDataManager;

public class FloraTypeManager extends TypedDataManager<FloraType>
{
    public static final FloraTypeManager INSTANCE = new FloraTypeManager();

    private FloraTypeManager()
    {
        super(new GsonBuilder().create(), "flora", "flora type");

        register(Helpers.identifier("plant"), PlantFloraType::new);
    }
}