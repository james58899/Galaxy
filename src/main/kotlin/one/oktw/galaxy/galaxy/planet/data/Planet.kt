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

import one.oktw.galaxy.Main.Companion.dummyUUID
import one.oktw.galaxy.galaxy.planet.enums.PlanetType
import one.oktw.galaxy.galaxy.planet.enums.PlanetType.NORMAL
import org.spongepowered.api.effect.potion.PotionEffect
import java.util.*
import kotlin.collections.ArrayList

data class Planet(
    val uuid: UUID = UUID.randomUUID(),
    var world: UUID = dummyUUID,
    var name: String = "",
    val type: PlanetType = NORMAL,
    var size: Int = 32,
    var level: Short = 1,
    var effect: List<PotionEffect> = ArrayList(),
    var visitable: Boolean = true,
    var lastTime: Date = Date()
)
