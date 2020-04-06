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
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.advancements.ICriterionTrigger;
import net.minecraft.advancements.PlayerAdvancements;
import net.minecraft.advancements.critereon.AbstractCriterionInstance;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;

import mcp.MethodsReturnNonnullByDefault;

import static net.dries007.tfc.TerraFirmaCraft.MOD_ID;

/**
 * Advancement criterion trigger for when a device is lit
 */
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class LitTrigger implements ICriterionTrigger<LitTrigger.Instance>
{
    private static final ResourceLocation ID = new ResourceLocation(MOD_ID, "lit_device");
    private final Map<PlayerAdvancements, LitTrigger.Listeners> listeners = new HashMap<>();

    @Override
    public ResourceLocation getId()
    {
        return ID;
    }

    @Override
    public void addListener(PlayerAdvancements playerAdvancementsIn, Listener<LitTrigger.Instance> listener)
    {
        LitTrigger.Listeners listeners = this.listeners.get(playerAdvancementsIn);
        if (listeners == null)
        {
            listeners = new LitTrigger.Listeners(playerAdvancementsIn);
            this.listeners.put(playerAdvancementsIn, listeners);
        }

        listeners.add(listener);
    }

    @Override
    public void removeListener(PlayerAdvancements playerAdvancementsIn, Listener<LitTrigger.Instance> listener)
    {
        LitTrigger.Listeners listeners = this.listeners.get(playerAdvancementsIn);
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
    public LitTrigger.Instance deserializeInstance(JsonObject json, JsonDeserializationContext context)
    {
        ResourceLocation resourceLocation = null;
        JsonElement element = json.get("block");
        if (element != null && !element.isJsonNull())
        {
            resourceLocation = new ResourceLocation(JsonUtils.getString(element, "block"));
        }
        return new LitTrigger.Instance(resourceLocation);
    }

    public void trigger(EntityPlayerMP player, Block block)
    {
        LitTrigger.Listeners listeners = this.listeners.get(player.getAdvancements());
        if (listeners != null)
        {
            listeners.trigger(block);
        }

    }

    public static class Instance extends AbstractCriterionInstance
    {
        private final ResourceLocation blockPredicate;

        public Instance(@Nullable ResourceLocation blockPredicate)
        {
            super(LitTrigger.ID);
            this.blockPredicate = blockPredicate;
        }

        public boolean test(Block block)
        {
            return blockPredicate == null || blockPredicate.equals(block.getRegistryName());
        }
    }

    private static class Listeners
    {
        private final PlayerAdvancements playerAdvancements;
        private final Set<Listener<LitTrigger.Instance>> listeners = new HashSet<>();

        private Listeners(PlayerAdvancements playerAdvancementsIn)
        {
            this.playerAdvancements = playerAdvancementsIn;
        }

        private boolean isEmpty()
        {
            return this.listeners.isEmpty();
        }

        private void add(Listener<LitTrigger.Instance> listener)
        {
            this.listeners.add(listener);
        }

        private void remove(Listener<LitTrigger.Instance> listener)
        {
            this.listeners.remove(listener);
        }

        private void trigger(Block block)
        {
            this.listeners.stream()
                .filter(listener -> listener.getCriterionInstance().test(block))
                .collect(Collectors.toList()) // This line is needed to avoid ConcurrentModificationException
                .forEach(listener -> listener.grantCriterion(this.playerAdvancements));
        }
    }
}
