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

import kotlinx.coroutines.launch
import one.oktw.galaxy.Main
import one.oktw.galaxy.Main.Companion.main
import one.oktw.galaxy.data.DataUUID
import one.oktw.galaxy.galaxy.data.Galaxy
import one.oktw.galaxy.galaxy.data.extensions.delMember
import one.oktw.galaxy.galaxy.data.extensions.setGroup
import one.oktw.galaxy.item.enums.ButtonType.MEMBER_CHANGE
import one.oktw.galaxy.item.enums.ButtonType.MEMBER_REMOVE
import one.oktw.galaxy.item.type.Button
import org.spongepowered.api.Sponge
import org.spongepowered.api.data.key.Keys
import org.spongepowered.api.entity.living.player.Player
import org.spongepowered.api.event.item.inventory.ClickInventoryEvent
import org.spongepowered.api.event.item.inventory.InteractInventoryEvent
import org.spongepowered.api.item.inventory.Inventory
import org.spongepowered.api.item.inventory.InventoryArchetypes
import org.spongepowered.api.item.inventory.property.InventoryTitle
import org.spongepowered.api.item.inventory.query.QueryOperationTypes
import org.spongepowered.api.item.inventory.type.GridInventory
import org.spongepowered.api.service.user.UserStorageService
import org.spongepowered.api.text.Text
import org.spongepowered.api.text.format.TextColors
import java.util.*
import java.util.Arrays.asList

class ManageMember(private val galaxy: Galaxy, private val member: UUID) : GUI() {
    private val lang = Main.translationService
    private val user = Sponge.getServiceManager().provide(UserStorageService::class.java).get().get(member).get()
    override val token = "ManageMember-${galaxy.uuid}-$member"
    override val inventory: Inventory = Inventory.builder()
        .of(InventoryArchetypes.HOPPER)
        .property(InventoryTitle.of(Text.of(user.name)))
        .listener(InteractInventoryEvent::class.java, ::eventProcess)
        .build(main)
    private val buttonID = Array(3) { UUID.randomUUID() }

    init {
        val inventory = inventory.query<GridInventory>(QueryOperationTypes.INVENTORY_TYPE.of(GridInventory::class.java))

        // button
        Button(MEMBER_REMOVE).createItemStack()
            .apply {
                offer(DataUUID(buttonID[0]))
                offer(
                    Keys.DISPLAY_NAME,
                    lang.ofPlaceHolder(TextColors.GREEN, lang.of("UI.Button.RemoveMember"))
                )
                offer(Keys.ITEM_LORE, asList(lang.ofPlaceHolder(TextColors.RED, lang.of("UI.removeMemberNotify"))))
            }
            .let { inventory.set(1, 0, it) }

        Button(MEMBER_CHANGE).createItemStack()
            .apply {
                offer(DataUUID(buttonID[1]))
                offer(
                    Keys.DISPLAY_NAME,
                    lang.ofPlaceHolder(TextColors.GREEN, lang.of("UI.Button.ChangePermissionGroup"))
                )
            }
            .let { inventory.set(3, 0, it) }

        GUIHelper.fillEmptySlot(inventory)

        // register event
        registerEvent(ClickInventoryEvent::class.java, this::clickEvent)
    }

    private fun clickEvent(event: ClickInventoryEvent) {
        event.isCancelled = true

        val player = event.source as Player

        when (event.cursorTransaction.default[DataUUID.key].orElse(null) ?: return) {
            buttonID[0] -> GUIHelper.open(player) {
                Confirm(lang.ofPlaceHolder("UI.Button.ConfirmRemoveMember")) {
                    if (it) {
                        launch { galaxy.delMember(member) }
                        GUIHelper.close(token)
                    }
                }
            }
            buttonID[1] -> GUIHelper.open(player) { GroupSelect { launch { galaxy.setGroup(member, it) } } }
        }
    }
}
