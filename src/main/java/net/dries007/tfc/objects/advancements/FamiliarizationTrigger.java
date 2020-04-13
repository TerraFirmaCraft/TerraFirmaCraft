/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.advancements;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.advancements.ICriterionTrigger;
import net.minecraft.advancements.PlayerAdvancements;
import net.minecraft.advancements.critereon.AbstractCriterionInstance;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.EntityRegistry;

import mcp.MethodsReturnNonnullByDefault;
import net.dries007.tfc.api.types.IAnimalTFC;

import static net.dries007.tfc.TerraFirmaCraft.MOD_ID;

/**
 * Advancement criterion trigger for when an animal is familiarized
 */
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class FamiliarizationTrigger implements ICriterionTrigger<FamiliarizationTrigger.Instance>
{
    private static final ResourceLocation ID = new ResourceLocation(MOD_ID, "familiarity_changed");
    private final Map<PlayerAdvancements, FamiliarizationTrigger.Listeners> listeners = new HashMap<>();

    @Override
    public ResourceLocation getId()
    {
        return ID;
    }

    @Override
    public void addListener(PlayerAdvancements playerAdvancementsIn, Listener<FamiliarizationTrigger.Instance> listener)
    {
        FamiliarizationTrigger.Listeners listeners = this.listeners.get(playerAdvancementsIn);
        if (listeners == null)
        {
            listeners = new FamiliarizationTrigger.Listeners(playerAdvancementsIn);
            this.listeners.put(playerAdvancementsIn, listeners);
        }

        listeners.add(listener);
    }

    @Override
    public void removeListener(PlayerAdvancements playerAdvancementsIn, Listener<FamiliarizationTrigger.Instance> listener)
    {
        FamiliarizationTrigger.Listeners listeners = this.listeners.get(playerAdvancementsIn);
        if (listeners != null)
        {
            listeners.remove(listener);
            if (listeners.isEmpty())
            {
                this.listeners.remove(playerAdvancementsIn);
            }
        }

    }

    @Override
    public void removeAllListeners(PlayerAdvancements playerAdvancementsIn)
    {
        this.listeners.remove(playerAdvancementsIn);
    }

    @Override
    public FamiliarizationTrigger.Instance deserializeInstance(JsonObject json, JsonDeserializationContext context)
    {
        ResourceLocation resourceLocation = null;
        float familiarity = 0;
        JsonElement animalElement = json.get("animal");
        JsonElement familiarityElement = json.get("familiarity");
        if (animalElement != null && !animalElement.isJsonNull())
        {
            resourceLocation = new ResourceLocation(JsonUtils.getString(animalElement, "animal"));
        }
        if (familiarityElement != null && !familiarityElement.isJsonNull())
        {
            familiarity = JsonUtils.getFloat(familiarityElement, "familiarity");
        }
        return new FamiliarizationTrigger.Instance(resourceLocation, familiarity);
    }

    public <T extends EntityAnimal & IAnimalTFC> void trigger(EntityPlayerMP player, T animal)
    {
        FamiliarizationTrigger.Listeners listeners = this.listeners.get(player.getAdvancements());
        if (listeners != null)
        {
            listeners.trigger(animal);
        }

    }

    public static class Instance extends AbstractCriterionInstance
    {
        private final ResourceLocation animalPredicate;
        private final float minFamiliarity;

        public Instance(@Nullable ResourceLocation animalPredicate, float minFamiliarity)
        {
            super(FamiliarizationTrigger.ID);
            this.animalPredicate = animalPredicate;
            this.minFamiliarity = minFamiliarity;
        }

        public <T extends EntityAnimal & IAnimalTFC> boolean test(@Nonnull T animal)
        {
            //noinspection ConstantConditions
            if (animalPredicate != null && !animalPredicate.equals(EntityRegistry.getEntry(animal.getClass()).getRegistryName()))
            {
                return false;
            }
            return animal.getFamiliarity() >= minFamiliarity;
        }
    }

    private static class Listeners
    {
        private final PlayerAdvancements playerAdvancements;
        private final Set<Listener<FamiliarizationTrigger.Instance>> listeners = new HashSet<>();

        private Listeners(PlayerAdvancements playerAdvancementsIn)
        {
            this.playerAdvancements = playerAdvancementsIn;
        }

        private boolean isEmpty()
        {
            return this.listeners.isEmpty();
        }

        private void add(Listener<FamiliarizationTrigger.Instance> listener)
        {
            this.listeners.add(listener);
        }

        private void remove(Listener<FamiliarizationTrigger.Instance> listener)
        {
            this.listeners.remove(listener);
        }

        private <T extends EntityAnimal & IAnimalTFC> void trigger(T animal)
        {
            this.listeners.stream()
                .filter(listener -> listener.getCriterionInstance().test(animal))
                .collect(Collectors.toList()) // This line is needed to avoid ConcurrentModificationException
                .forEach(listener -> listener.grantCriterion(this.playerAdvancements));
        }
    }
}
