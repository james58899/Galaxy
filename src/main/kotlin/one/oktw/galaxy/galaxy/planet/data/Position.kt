/*
 * OKTW Galaxy Project
 * Copyright (C) 2018-2018
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package one.oktw.galaxy.galaxy.planet.data

import com.flowpowered.math.vector.Vector3d
import java.util.*

data class Position(
    var x: Double = 0.0,
    var y: Double = 0.0,
    var z: Double = 0.0,
    var planet: UUID? = null
) {
    constructor(vector3d: Vector3d, planet: UUID? = null) : this(vector3d.x, vector3d.y, vector3d.z, planet)
}
