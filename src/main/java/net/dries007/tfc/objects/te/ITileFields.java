/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.te;

public interface ITileFields
{
    int getFieldCount();

    void setField(int index, int value);

    int getField(int index);
}
