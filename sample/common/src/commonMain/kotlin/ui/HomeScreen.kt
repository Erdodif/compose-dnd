/*
 * Copyright 2023, Mohamed Ben Rejeb and the Compose Dnd project contributors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow

object HomeScreen : Screen {

    @Composable
    override fun Content() {
        HomeScreenContent()
    }
}
@Composable
private fun HomeScreenContent() {
    val navigator = LocalNavigator.currentOrThrow

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .safeDrawingPadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {
            HomeListItem(
                text = "From item to item (one direction)",
                onClick = {
                    navigator.push(ItemToItemOneDirectionScreen)
                }
            )

            HomeListItem(
                text = "From item to item (two directions)",
                onClick = {
                    navigator.push(ItemToItemTwoDirectionsScreen)
                }
            )

            HomeListItem(
                text = "Reorderable List",
                onClick = {
                    navigator.push(ReorderListScreen)
                }
            )

            HomeListItem(
                text = "From list to list (without reorder)",
                onClick = {
                    navigator.push(ListToListWithoutReorderScreen)
                }
            )

            HomeListItem(
                text = "From list to list (with reorder)",
                onClick = {
                    navigator.push(ListToListWithReorderScreen)
                }
            )

            HomeListItem(
                text = "Item within an item (nested drag)",
                onClick = {
                    navigator.push(NestedItemScreen)
                }
            )
        }
    }
}

@Composable
private fun HomeListItem(
    text: String,
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .clickable(
                onClick = onClick,
            )
    ) {
        ListItem(
            headlineContent = {
                Text(
                    text = text,
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
        )
    }
}
