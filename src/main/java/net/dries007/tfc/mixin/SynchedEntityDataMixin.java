/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.mixin;

import java.util.concurrent.locks.ReadWriteLock;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.dries007.tfc.util.collections.NoopReadWriteLock;

/**
 * By inspection, you may notice that {@link SynchedEntityData} has a read/write lock.
 * But why? Getting/setting is done on the server thread, and the entity sends packets itself, which can then use the network thread.
 *
 * The answer is that entity deserialization used to occur on the network thread, and this is no longer the case.
 * We found that accesses (Which are necessary for things like ticking animal behavior and rendering!) were taking up to 4% of the server tick time.
 */
@Mixin(SynchedEntityData.class)
public abstract class SynchedEntityDataMixin
{
    @Final
    @Mutable
    @Shadow
    private ReadWriteLock lock;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void inject$init(Entity entity, CallbackInfo ci)
    {
        lock = NoopReadWriteLock.INSTANCE;
    }
}
