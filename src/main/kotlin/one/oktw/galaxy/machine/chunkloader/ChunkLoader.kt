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

package one.oktw.galaxy.machine.chunkloader

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import one.oktw.galaxy.Main.Companion.chunkLoaderManager
import one.oktw.galaxy.Main.Companion.serverThread
import one.oktw.galaxy.data.DataUUID
import one.oktw.galaxy.gui.GUIHelper
import one.oktw.galaxy.gui.machine.ChunkLoader
import org.spongepowered.api.block.BlockTypes
import org.spongepowered.api.block.tileentity.Piston
import org.spongepowered.api.entity.EnderCrystal
import org.spongepowered.api.entity.living.player.Player
import org.spongepowered.api.entity.projectile.Projectile
import org.spongepowered.api.event.Listener
import org.spongepowered.api.event.entity.AttackEntityEvent
import org.spongepowered.api.event.entity.CollideEntityEvent
import org.spongepowered.api.event.entity.InteractEntityEvent
import org.spongepowered.api.event.entity.SpawnEntityEvent
import org.spongepowered.api.event.filter.Getter
import org.spongepowered.api.event.filter.cause.First
import org.spongepowered.api.event.world.ExplosionEvent

class ChunkLoader {
    @Listener
    fun onSpawnEntity(event: SpawnEntityEvent, @First player: Player) {
        val enderCrystal = event.entities.firstOrNull { it is EnderCrystal } as? EnderCrystal ?: return

        if (enderCrystal.location.add(0.0, -1.0, 0.0).blockType == BlockTypes.OBSIDIAN) {
            GlobalScope.launch {
                chunkLoaderManager.add(enderCrystal.location).uuid.let {
                    withContext(serverThread) { enderCrystal.offer(DataUUID(it)) }
                }
            }
        }
    }

    @Listener
    @Suppress("UNUSED_PARAMETER")
    fun onInteractEntity(event: InteractEntityEvent.Secondary.MainHand, @First player: Player, @Getter("getTargetEntity") enderCrystal: EnderCrystal) {
        if (enderCrystal[DataUUID.key].isPresent) GUIHelper.open(player) { ChunkLoader(enderCrystal) }
    }

    @Listener
    fun onAttackEntity(event: AttackEntityEvent, @Getter("getTargetEntity") enderCrystal: EnderCrystal) {
        if (enderCrystal[DataUUID.key].isPresent) event.isCancelled = true
    }

    @Listener
    fun onCollideEntity(event: CollideEntityEvent) {
        if (event.source is Projectile || event.source is Piston) event.filterEntities { !(it is EnderCrystal && it[DataUUID.key].isPresent) }
    }

    @Listener
    fun onExplosion(event: ExplosionEvent.Detonate) {
        event.filterEntities { !(it is EnderCrystal && it[DataUUID.key].isPresent) }
    }
}
