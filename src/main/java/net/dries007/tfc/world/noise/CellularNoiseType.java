/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 *
 * This was originally part of FastNoise (https://github.com/Auburn/FastNoise) and has been included as per the MIT license:
 *
 * MIT License
 *
 * Copyright(c) 2020 Jordan Peck (jordan.me2@gmail.com)
 * Copyright(c) 2020 Contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files(the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and / or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions :
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package net.dries007.tfc.world.noise;

public enum CellularNoiseType
{
    VALUE,
    DISTANCE,
    DISTANCE_2,
    DISTANCE_SUM,
    DISTANCE_DIFFERENCE,
    DISTANCE_PRODUCT,
    DISTANCE_QUOTIENT,
    OTHER;

    public float calculate(float distance0, float distance1, int closestHash)
    {
        switch (this)
        {
            case VALUE:
                return closestHash * (1 / 2147483648.0f);
            case DISTANCE:
                return distance0;
            case DISTANCE_2:
                return distance1;
            case DISTANCE_SUM:
                return (distance1 + distance0) * 0.5f - 1;
            case DISTANCE_DIFFERENCE:
                return distance1 - distance0 - 1;
            case DISTANCE_PRODUCT:
                return distance1 * distance0 * 0.5f - 1;
            case DISTANCE_QUOTIENT:
                return distance0 / distance1 - 1;
            default:
                return 0;
        }
    }
}
