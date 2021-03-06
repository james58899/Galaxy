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

package one.oktw.galaxy.gui

import one.oktw.galaxy.Main
import one.oktw.galaxy.Main.Companion.main
import one.oktw.galaxy.data.DataUUID
import one.oktw.galaxy.item.enums.ButtonType.OK
import one.oktw.galaxy.item.enums.ButtonType.X
import one.oktw.galaxy.item.type.Button
import org.spongepowered.api.data.key.Keys
import org.spongepowered.api.event.item.inventory.ClickInventoryEvent
import org.spongepowered.api.event.item.inventory.InteractInventoryEvent
import org.spongepowered.api.item.inventory.Inventory
import org.spongepowered.api.item.inventory.InventoryArchetypes
import org.spongepowered.api.item.inventory.property.InventoryTitle
import org.spongepowered.api.item.inventory.query.QueryOperationTypes
import org.spongepowered.api.item.inventory.type.GridInventory
import org.spongepowered.api.text.Text
import org.spongepowered.api.text.format.TextColors
import java.util.*

class Confirm(content: Text, yesButtonText: Text? = null, noButtonText: Text? = null, private val callback: (Boolean) -> Unit) : GUI() {
    override val token = "Confirm-${UUID.randomUUID()}"
    // Todo get player lang
    private val lang = Main.translationService
    override val inventory: Inventory = Inventory.builder()
        .of(InventoryArchetypes.HOPPER)
        .property(InventoryTitle.of(content))
        .listener(InteractInventoryEvent::class.java, ::eventProcess)
        .build(main)
    private val buttonID = Array(3) { UUID.randomUUID() }
    private val yes = yesButtonText ?: lang.ofPlaceHolder(TextColors.GREEN, lang.of("UI.Button.Yes"))
    private val no = noButtonText ?: lang.ofPlaceHolder(TextColors.RED, lang.of("UI.Button.No"))

    init {
        val inventory = inventory.query<GridInventory>(QueryOperationTypes.INVENTORY_TYPE.of(GridInventory::class.java))

        // button
        Button(OK).createItemStack()
            .apply {
                offer(DataUUID(buttonID[0]))
                offer(Keys.DISPLAY_NAME, yes)
            }
            .let { inventory.set(1, 0, it) }

        Button(X).createItemStack()
            .apply {
                offer(DataUUID(buttonID[1]))
                offer(Keys.DISPLAY_NAME, no)
            }
            .let { inventory.set(3, 0, it) }

        GUIHelper.fillEmptySlot(inventory)

        // register event
        registerEvent(ClickInventoryEvent::class.java, this::clickEvent)
    }

    private fun clickEvent(event: ClickInventoryEvent) {
        event.isCancelled = true

        when (event.cursorTransaction.default[DataUUID.key].orElse(null) ?: return) {
            buttonID[0] -> {
                callback(true)
                GUIHelper.close(token)
            }
            buttonID[1] -> {
                callback(false)
                GUIHelper.close(token)
            }
        }
    }
}
