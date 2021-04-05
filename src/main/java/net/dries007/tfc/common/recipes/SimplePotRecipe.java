package net.dries007.tfc.common.recipes;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import javax.annotation.Nullable;

import com.google.gson.JsonObject;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.ShapedRecipe;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.items.ItemStackHandler;

import com.mojang.serialization.JsonOps;
import net.dries007.tfc.util.Helpers;

//todo: much of the functionality is here, just needs to be finished
public class SimplePotRecipe implements IPotRecipe
{
    protected final ResourceLocation id;
    protected final HashSet<Ingredient> inputItems;
    protected final LinkedList<ItemStack> outputItems;
    protected final int duration;
    protected final float temperature;
    protected final FluidStack inputFluid;
    protected final FluidStack outputFluid;

    public SimplePotRecipe(ResourceLocation id, HashSet<Ingredient> inputItems, LinkedList<ItemStack> outputItems, FluidStack inputFluid, FluidStack outputFluid, float temperature, int duration)
    {
        this.id = id;
        this.inputItems = inputItems;
        this.outputItems = outputItems;
        this.duration = duration;
        this.temperature = temperature;
        this.inputFluid = inputFluid;
        this.outputFluid = outputFluid;
    }

    public LinkedList<ItemStack> getOutputItems()
    {
        return outputItems;
    }

    @Override
    public int getDuration()
    {
        return duration;
    }

    @Override
    public boolean isValidTemperature(float temperatureIn)
    {
        return temperatureIn >= temperature;
    }

    @Override
    public boolean matches(FluidInventoryRecipeWrapper wrapper, World worldIn)
    {
        if (!wrapper.getInputFluid().isFluidEqual(inputFluid)) return false;

        HashSet<Ingredient> notFound = new HashSet<>(inputItems);
        notFound.removeIf(Ingredient::isEmpty); // clear the clutter out

        LinkedList<ItemStack> stacks = new LinkedList<>();
        for (int i = 0; i < wrapper.getContainerSize(); i++)
        {
            stacks.add(wrapper.getItem(i).copy());
        }
        for (ItemStack stack : stacks)
        {
            for (Ingredient i : notFound)
            {
                if (i.test(stack))
                {
                    notFound.remove(i);
                    break; // removeIf not used so we can handle duplicates
                }
            }
        }
        return notFound.isEmpty();
    }

    @Override
    public ResourceLocation getId()
    {
        return id;
    }

    @Override
    public IRecipeSerializer<?> getSerializer()
    {
        return TFCRecipeSerializers.SIMPLE_POT.get();
    }

    /**
     * @return A copy of the output fluid
     */
    @Override
    public FluidStack getOutputFluid()
    {
        return outputFluid.copy();
    }

    //todo: could just add a thing similar to getOutputFluid() that deposits items into the inventory and leave the output handling unused. right now this is good to keep for demonstration purposes
    @Override
    public Output getOutput(ItemStackHandler inv, FluidStack fluid)
    {
        return new Output()
        {
            private final Queue<ItemStack> itemsLeft = outputItems;

            @Override
            public boolean isEmpty()
            {
                return itemsLeft.isEmpty();
            }

            @Override
            public void onExtract(World world, BlockPos pos, ItemStack clickedWith)
            {
                // leave fluid extraction up to the helpful TE logic
                if (!itemsLeft.isEmpty())
                    Helpers.spawnItem(world, pos, itemsLeft.remove(), 0.7D);
            }

            @Override
            public CompoundNBT serializeNBT()
            {
                CompoundNBT nbt = new CompoundNBT();
                ListNBT surplusList = new ListNBT();
                itemsLeft.forEach(stack -> surplusList.add(stack.serializeNBT()));
                nbt.put("leftover", surplusList);
                return nbt;
            }

            @Override
            public void deserializeNBT(CompoundNBT nbt)
            {
                itemsLeft.clear();
                ListNBT items = nbt.getList("leftover", Constants.NBT.TAG_COMPOUND);
                for (int i = 0; i < items.size(); i++)
                {
                    itemsLeft.add(ItemStack.of(items.getCompound(i)));
                }
            }
        };
    }

    public static class Serializer extends RecipeSerializer<SimplePotRecipe>
    {
        private final SimplePotRecipe.Serializer.Factory<SimplePotRecipe> factory;

        public Serializer(SimplePotRecipe.Serializer.Factory<SimplePotRecipe> factory)
        {
            this.factory = factory;
        }

        @Override
        public SimplePotRecipe fromJson(ResourceLocation recipeId, JsonObject json)
        {
            HashSet<Ingredient> ingredients = new HashSet<>(5);
            json.getAsJsonArray("ingredients").forEach(i -> ingredients.add(Ingredient.fromJson(i)));

            LinkedList<ItemStack> outputs = new LinkedList<>();
            json.getAsJsonArray("outputs").forEach(i -> outputs.add(ShapedRecipe.itemFromJson(i.getAsJsonObject())));

            FluidStack input = FluidStack.CODEC.decode(JsonOps.INSTANCE, json.get("fluidInput")).getOrThrow(false, null).getFirst();
            FluidStack output = FluidStack.CODEC.decode(JsonOps.INSTANCE, json.get("fluidOutput")).getOrThrow(false, null).getFirst();
            int duration = json.get("duration").getAsInt();
            float temp = json.get("temperature").getAsFloat();

            return factory.create(recipeId, ingredients, outputs, input, output, temp, duration);
        }

        @Nullable
        @Override
        public SimplePotRecipe fromNetwork(ResourceLocation recipeId, PacketBuffer buffer)
        {
            HashSet<Ingredient> ingredients = new HashSet<>();
            int inputCount = buffer.readInt();
            for (int i = 0; i < inputCount; i++)
                ingredients.add(Ingredient.fromNetwork(buffer));

            LinkedList<ItemStack> outputs = new LinkedList<>();
            int outputCount = buffer.readInt();
            for (int i = 0; i < outputCount; i++)
                outputs.add(buffer.readItem());

            FluidStack inputFluid = buffer.readFluidStack();
            FluidStack outputFluid = buffer.readFluidStack();

            int duration = buffer.readInt();
            float temp = buffer.readFloat();

            return factory.create(recipeId, ingredients, outputs, inputFluid, outputFluid, temp, duration);
        }

        @Override
        public void toNetwork(PacketBuffer buffer, SimplePotRecipe recipe)
        {
            buffer.writeInt(recipe.inputItems.size());
            recipe.inputItems.forEach(i -> i.toNetwork(buffer));

            buffer.writeInt(recipe.outputItems.size());
            recipe.outputItems.forEach(buffer::writeItem);

            buffer.writeFluidStack(recipe.inputFluid);
            buffer.writeFluidStack(recipe.outputFluid);

            buffer.writeInt(recipe.duration);
            buffer.writeFloat(recipe.temperature);
        }

        protected interface Factory<SimplePotRecipe>
        {
            SimplePotRecipe create(ResourceLocation id, HashSet<Ingredient> inputItems, LinkedList<ItemStack> outputItems, FluidStack inputFluid, FluidStack outputFluid, float temperature, int duration);
        }
    }
}
