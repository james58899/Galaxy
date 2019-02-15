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

package one.oktw.galaxy.player.event

import net.minecraft.entity.player.EntityPlayerMP
import net.minecraft.util.math.BlockPos
import org.spongepowered.api.block.BlockType
import org.spongepowered.api.block.BlockTypes.*
import org.spongepowered.api.data.key.Keys
import org.spongepowered.api.entity.living.player.Player
import org.spongepowered.api.event.Listener
import org.spongepowered.api.event.block.InteractBlockEvent
import org.spongepowered.api.event.filter.cause.First
import org.spongepowered.api.world.Location
import org.spongepowered.api.world.World
import java.util.Arrays.asList

class Harvest {
    private fun isNextTo(location: Location<World>, type: BlockType): Boolean {
        return location.add(1.0, 0.0, 0.0).blockType == type ||
                location.add(0.0, 0.0, 1.0).blockType == type ||
                location.add(-1.0, 0.0, 0.0).blockType == type ||
                location.add(0.0, 0.0, -1.0).blockType == type
    }

    @Listener
    fun onClick(event: InteractBlockEvent.Secondary.MainHand, @First player: Player) {
        val block = event.targetBlock

        when (block.state.type) {
            PUMPKIN -> {
                val location = block.location.get()

                if (location.add(0.0, -1.0, 0.0).blockType !in asList(GRASS, DIRT, FARMLAND)) return
                if (!isNextTo(location, PUMPKIN_STEM)) return

                (player as EntityPlayerMP).interactionManager.tryHarvestBlock(block.position.run { BlockPos(x, y, z) })
            }
            MELON_BLOCK -> {
                val location = block.location.get()

                if (location.add(0.0, -1.0, 0.0).blockType !in asList(GRASS, DIRT, FARMLAND)) return
                if (!isNextTo(location, MELON_STEM)) return

                (player as EntityPlayerMP).interactionManager.tryHarvestBlock(block.position.run { BlockPos(x, y, z) })
            }
        }

        val age = block[Keys.GROWTH_STAGE].orElse(null) ?: return
        val max = when (block.state.type) {
            COCOA -> 2
            BEETROOTS -> 3
            NETHER_WART -> 3
            PUMPKIN_STEM -> 8 // don't harvest it, we harvest block instead
            MELON_STEM -> 8 // don't harvest it, we harvest block instead
            else -> 7
        }

        if (age != max) return

        if ((player as EntityPlayerMP).interactionManager.tryHarvestBlock(block.position.run { BlockPos(x, y, z) })) {
            block.location.get().block = block.state.with(Keys.GROWTH_STAGE, 0).get()
        }
    }
}
