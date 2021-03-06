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

package one.oktw.galaxy.armor

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import one.oktw.galaxy.Main.Companion.main
import one.oktw.galaxy.Main.Companion.serverThread
import org.spongepowered.api.Sponge
import org.spongepowered.api.data.key.Keys
import org.spongepowered.api.effect.potion.PotionEffect
import org.spongepowered.api.effect.potion.PotionEffectType
import org.spongepowered.api.entity.living.player.Player
import org.spongepowered.api.scheduler.Task
import java.util.*
import java.util.concurrent.TimeUnit.SECONDS

class ArmorEffect {
    companion object {
        private val server = Sponge.getServer()
        private val effect = HashMap<UUID, HashMap<PotionEffectType, Int>>()

        init {
            Task.builder()
                .name("ArmorEffect")
                .interval(3, SECONDS)
                .execute { _ ->
                    val iterator = effect.iterator()

                    while (iterator.hasNext()) {
                        val (uuid, eff) = iterator.next()
                        val player = server.getPlayer(uuid).orElse(null)

                        if (player == null) {
                            iterator.remove()
                            continue
                        }

                        player.transform(Keys.POTION_EFFECTS) {
                            val effectList = it ?: ArrayList()

                            eff.forEach {
                                effectList += PotionEffect.builder()
                                    .potionType(it.key)
                                    .amplifier(it.value)
                                    .duration(Int.MAX_VALUE)
                                    .particles(false)
                                    .build()
                            }

                            effectList
                        }
                    }
                }
                .submit(main)
        }

        fun offerEffect(player: Player, type: PotionEffectType, level: Int = 0) {
            effect.getOrPut(player.uniqueId) { HashMap() }[type] = level
        }

        fun removeEffect(player: Player, type: PotionEffectType) {
            effect[player.uniqueId]?.remove(type)

            GlobalScope.launch(serverThread) { player.transform(Keys.POTION_EFFECTS) { it?.apply { removeIf { it.type == type } } } }
        }

        fun removeAllEffect(player: Player) {
            effect[player.uniqueId]?.forEach { map ->
                GlobalScope.launch(serverThread) { player.transform(Keys.POTION_EFFECTS) { it?.apply { removeIf { it.type == map.key } } } }
            }
            effect -= player.uniqueId
        }
    }
}
