/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.recipes;

import java.util.ArrayList;
import java.util.List;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.common.blockentities.PotBlockEntity;
import net.dries007.tfc.common.recipes.ingredients.FluidStackIngredient;
import net.dries007.tfc.compat.jade.common.BlockEntityTooltip;
import net.dries007.tfc.util.Helpers;

/**
 * Recipe type for all cooking pot recipes
 */
public abstract class PotRecipe implements ISimpleRecipe<PotBlockEntity.PotInventory>
{
    private static final BiMap<ResourceLocation, OutputType> OUTPUT_TYPES = HashBiMap.create();

    private static final ResourceLocation EMPTY_ID = Helpers.identifier("empty");
    private static final Output EMPTY_INSTANCE = new Output() {};
    private static final OutputType EMPTY = register(EMPTY_ID, nbt -> EMPTY_INSTANCE);

    /**
     * Register a pot output type.
     * If a pot recipe uses a custom output, that must persist (and thus be serialized), it needs to be registered here.
     * This method is safe to call during parallel mod loading.
     */
    public static synchronized OutputType register(ResourceLocation id, OutputType outputType)
    {
        if (OUTPUT_TYPES.containsKey(id))
        {
            throw new IllegalArgumentException("Duplicate key: " + id);
        }
        OUTPUT_TYPES.put(id, outputType);
        return outputType;
    }

    protected final ResourceLocation id;
    protected final List<Ingredient> itemIngredients;
    protected final FluidStackIngredient fluidIngredient;
    protected final int duration;
    protected final float minTemp;

    protected PotRecipe(ResourceLocation id, List<Ingredient> itemIngredients, FluidStackIngredient fluidIngredient, int duration, float minTemp)
    {
        this.id = id;
        this.itemIngredients = itemIngredients;
        this.fluidIngredient = fluidIngredient;
        this.duration = duration;
        this.minTemp = minTemp;
    }

    @Override
    public boolean matches(PotBlockEntity.PotInventory inventory, Level worldIn)
    {
        if (!fluidIngredient.test(inventory.getFluidInTank(0)))
        {
            return false;
        }
        final List<ItemStack> stacks = new ArrayList<>();
        for (int i = PotBlockEntity.SLOT_EXTRA_INPUT_START; i <= PotBlockEntity.SLOT_EXTRA_INPUT_END; i++)
        {
            ItemStack stack = inventory.getStackInSlot(i);
            if (!stack.isEmpty())
            {
                stacks.add(stack);
            }
        }
        return (stacks.isEmpty() && itemIngredients.isEmpty()) || Helpers.perfectMatchExists(stacks, itemIngredients);
    }

    @Override
    public ItemStack getResultItem(RegistryAccess access)
    {
        return ItemStack.EMPTY;
    }

    @Override
    public ResourceLocation getId()
    {
        return id;
    }

    @Override
    public RecipeType<?> getType()
    {
        return TFCRecipeTypes.POT.get();
    }

    public FluidStackIngredient getFluidIngredient()
    {
        return fluidIngredient;
    }

    public List<Ingredient> getItemIngredients()
    {
        return itemIngredients;
    }

    /**
     * @return true if the temperature is hot enough to boil
     */
    public boolean isHotEnough(float tempIn)
    {
        return tempIn > minTemp;
    }

    /**
     * @return The number of ticks needed to boil for.
     */
    public int getDuration()
    {
        return duration;
    }

    /**
     * @return The output of the pot recipe.
     */
    public abstract PotRecipe.Output getOutput(PotBlockEntity.PotInventory inventory);

    /**
     * The output of a pot recipe. This output can be fairly complex, but follows a specific contract:
     * <ol>
     *     <li>The output is created, with access to the inventory, populated with the ingredient items (in {@link PotRecipe#getOutput(PotBlockEntity.PotInventory)})</li>
     *     <li>{@link Output#onFinish(PotBlockEntity.PotInventory)} is called, with a completely empty inventory. The output can then add fluids or items back into the pot as necessary</li>
     *     <li>THEN, if {@link Output#isEmpty()} returns true, the output is discarded. Otherwise...</li>
     *     <li>The output is saved to the tile entity. On a right click, {@link Output#onInteract(PotBlockEntity, Player, ItemStack)} is called, and after each call, {@link Output#isEmpty()} will be queried to see if the output is empty. The pot will not resume functionality until the output is empty</li>
     * </ol>
     *
     * @see PotBlockEntity#handleCooking()
     */
    public interface Output
    {
        /**
         * Read an output from an NBT tag.
         */
        static Output read(CompoundTag nbt)
        {
            final OutputType type = OUTPUT_TYPES.getOrDefault(Helpers.resourceLocation(nbt.getString("type")), EMPTY);
            return type.read(nbt);
        }

        /**
         * Write an output to a NBT tag.
         */
        static CompoundTag write(Output output)
        {
            final CompoundTag nbt = new CompoundTag();
            nbt.putString("type", OUTPUT_TYPES.inverse().getOrDefault(output.getType(), EMPTY_ID).toString());
            output.write(nbt);
            return nbt;
        }

        /**
         * If there is still something to be extracted from this output. If this returns false at any time the output must be serializable
         */
        default boolean isEmpty()
        {
            return true;
        }

        /**
         * The color of the fluid the pot, while storing this output, should render as inside the pot, despite the pot itself not necessarily being filled with any fluid
         *
         * @return an {@code int} color, or -1 for no fluid to be displayed.
         */
        default int getFluidColor()
        {
            return -1;
        }

        /**
         * An alternative to {@link Output#getFluidColor()} that renders a solid texture.
         *
         * @return A {@linkplain ResourceLocation} matching a texture.
         */
        @Nullable
        default ResourceLocation getRenderTexture()
        {
            return null;
        }

        /**
         * @return The y level [0, 1] that the fluid face renders at. The inside of the pot's model extends from 6 to 11 pixels vertically.
         */
        default float getFluidYLevel()
        {
            return 0.625f;
        }

        /**
         * Called with an empty pot inventory immediately after completion, and after clearing the inventory of the pot, but, before
         * checking {@link #isEmpty()}. Fills the inventory with immediate outputs from the output. Note that any outputs that depend
         * on the inventory must be computed <strong>before</strong> this method, in {@link #getOutput(PotBlockEntity.PotInventory)}
         */
        default void onFinish(PotBlockEntity.PotInventory inventory) {}

        /**
         * Called when a player interacts with the pot inventory, using the specific item stack, to try and extract output.
         */
        default InteractionResult onInteract(PotBlockEntity entity, Player player, ItemStack clickedWith)
        {
            return InteractionResult.PASS;
        }

        /**
         * Gets the output type of this output, used for serializing the output.
         * If the output always returns true to {@link #isEmpty()}, then this can be left as {@link PotRecipe#EMPTY}.
         */
        default OutputType getType()
        {
            return EMPTY;
        }

        /**
         * Writes implementation specific output data to disk.
         */
        default void write(CompoundTag nbt) {}

        @Nullable
        default BlockEntityTooltip getTooltip()
        {
            return null;
        }
    }

    /**
     * The output type of a pot recipe, handles reading the output back from disk.
     */
    public interface OutputType
    {
        /**
         * Read the output from the given tag. The tag should contain the key "type", which will equal the registered ID of this output type.
         */
        Output read(CompoundTag nbt);
    }

    public abstract static class Serializer<R extends PotRecipe> extends RecipeSerializerImpl<R>
    {
        @Override
        public R fromJson(ResourceLocation recipeId, JsonObject json)
        {
            final JsonArray array = GsonHelper.getAsJsonArray(json, "ingredients");
            final List<Ingredient> ingredients = new ArrayList<>();
            for (JsonElement element : array)
            {
                ingredients.add(Ingredient.fromJson(element));
            }

            final FluidStackIngredient fluidIngredient = FluidStackIngredient.fromJson(GsonHelper.getAsJsonObject(json, "fluid_ingredient"));
            final int duration = GsonHelper.getAsInt(json, "duration");
            final float minTemp = GsonHelper.getAsFloat(json, "temperature");
            return fromJson(recipeId, json, ingredients, fluidIngredient, duration, minTemp);
        }

        @Nullable
        @Override
        public R fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer)
        {
            final int count = buffer.readVarInt();
            final List<Ingredient> ingredients = new ArrayList<>();
            for (int i = 0; i < count; i++)
            {
                ingredients.add(Ingredient.fromNetwork(buffer));
            }
            final FluidStackIngredient fluidIngredient = FluidStackIngredient.fromNetwork(buffer);
            final int duration = buffer.readVarInt();
            final float minTemp = buffer.readFloat();
            return fromNetwork(recipeId, buffer, ingredients, fluidIngredient, duration, minTemp);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buffer, R recipe)
        {
            buffer.writeVarInt(recipe.itemIngredients.size());
            for (Ingredient ingredient : recipe.itemIngredients)
            {
                ingredient.toNetwork(buffer);
            }
            recipe.fluidIngredient.toNetwork(buffer);
            buffer.writeVarInt(recipe.duration);
            buffer.writeFloat(recipe.minTemp);
        }

        protected abstract R fromJson(ResourceLocation recipeId, JsonObject json, List<Ingredient> ingredients, FluidStackIngredient fluidIngredient, int duration, float minTemp);

        protected abstract R fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer, List<Ingredient> ingredients, FluidStackIngredient fluidIngredient, int duration, float minTemp);
    }
}
