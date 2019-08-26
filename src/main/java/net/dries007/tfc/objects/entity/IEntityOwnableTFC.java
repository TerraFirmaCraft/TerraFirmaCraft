/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.entity;

import java.util.UUID;
import javax.annotation.Nullable;

import net.minecraft.entity.Entity;

public interface IEntityOwnableTFC
{
    @Nullable
    UUID getOwnerId();

    @Nullable
    Entity getOwner();
}
