package net.dries007.tfc.util.json;

import java.util.Map;
import javax.annotation.ParametersAreNonnullByDefault;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.block.Block;
import net.minecraft.util.JSONUtils;
import net.minecraftforge.registries.ForgeRegistries;

import net.dries007.tfc.api.types.Rock;
import net.dries007.tfc.world.gen.rock.RockCategory;

@ParametersAreNonnullByDefault
public class RockDeserializer extends TFCTypeDeserializer<Rock>
{
    @Override
    protected Rock create(JsonObject obj)
    {
        // Rock category
        String rockCategoryName = JSONUtils.getString(obj, "category");
        RockCategory category;
        try
        {
            category = RockCategory.valueOf(rockCategoryName.toUpperCase());
        }
        catch (IllegalArgumentException e)
        {
            throw new JsonParseException("Unknown rock category " + rockCategoryName);
        }

        // Rock blocks
        Map<Rock.BlockType, Block> blockVariants = findRegistryObjects(obj, "blocks", ForgeRegistries.BLOCKS, Rock.BlockType.values(), type -> type.name().toLowerCase());
        return new Rock(category, blockVariants);
    }
}
