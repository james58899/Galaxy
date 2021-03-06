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

package one.oktw.galaxy.command.admin

import kotlinx.coroutines.future.await
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import one.oktw.galaxy.Main.Companion.galaxyManager
import one.oktw.galaxy.Main.Companion.serverThread
import one.oktw.galaxy.command.CommandBase
import one.oktw.galaxy.galaxy.data.extensions.getPlanet
import one.oktw.galaxy.galaxy.data.extensions.removePlanet
import one.oktw.galaxy.galaxy.planet.PlanetHelper
import one.oktw.galaxy.util.Chat.Companion.confirm
import org.spongepowered.api.command.CommandResult
import org.spongepowered.api.command.CommandSource
import org.spongepowered.api.command.args.CommandContext
import org.spongepowered.api.command.args.GenericArguments
import org.spongepowered.api.command.spec.CommandSpec
import org.spongepowered.api.entity.living.player.Player
import org.spongepowered.api.text.Text
import org.spongepowered.api.text.format.TextColors
import org.spongepowered.api.world.storage.WorldProperties

class DeleteWorld : CommandBase {
    override val spec: CommandSpec = CommandSpec.builder()
        .permission("oktw.command.admin.deleteWorld")
        .arguments(GenericArguments.world(Text.of("World")))
        .executor(this)
        .build()

    override fun execute(src: CommandSource, args: CommandContext): CommandResult {
        if (src !is Player) return CommandResult.empty()

        val world = args.getOne<WorldProperties>("World").get()

        launch {
            if (confirm(src, Text.of(TextColors.AQUA, "Are you sure you want to remove the world ${world.worldName}?")) == true) {
                val galaxy = galaxyManager.get(world)
                if (galaxy != null) {
                    //src.sendMessage(Text.of(TextColors.GREEN, "Planet found!Removing planet instead."))
                    galaxy.getPlanet(world)?.uuid?.let {
                        withContext(serverThread) { galaxy.removePlanet(it) }
                        src.sendMessage(Text.of(TextColors.GREEN, "Planet on ${galaxy.name} (${galaxy.uuid}) deleted!"))
                        return@launch
                    }
                }
                withContext(serverThread) {
                    if (PlanetHelper.removePlanet(world.uniqueId).await()) {
                        src.sendMessage(Text.of(TextColors.RED, "World deleted!"))
                    } else {
                        src.sendMessage(Text.of(TextColors.RED, "Failed!"))
                    }
                }
            }
        }
        return CommandResult.success()
    }
}
