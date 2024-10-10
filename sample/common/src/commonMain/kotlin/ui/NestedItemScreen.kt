package ui

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.mohamedrejeb.compose.dnd.DragAndDropContainer
import com.mohamedrejeb.compose.dnd.DragAndDropState
import com.mohamedrejeb.compose.dnd.drag.DraggableItem
import com.mohamedrejeb.compose.dnd.drag.DropStrategy
import com.mohamedrejeb.compose.dnd.drop.dropTarget
import com.mohamedrejeb.compose.dnd.rememberDragAndDropState
import components.RedBox

object NestedItemScreen : Screen {

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow

        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = "Item in Item (nested)",
                        )
                    },
                    navigationIcon = {
                        IconButton(
                            onClick = {
                                navigator.pop()
                            },
                        ) {
                            Icon(
                                Icons.AutoMirrored.Rounded.ArrowBack,
                                contentDescription = "Back",
                            )
                        }
                    },
                )
            },
        ) { paddingValues ->
            NestedContent(
                Modifier
                    .fillMaxSize()
                    .safeDrawingPadding()
                    .padding(paddingValues)
                    .padding(20.dp),
            )
        }
    }
}

data class Nestable(val label: String, var next: Nestable? = null) {
    override fun toString(): String = "$label (${next ?: ""})"

    fun append(value: Nestable): Nestable =
        if (next == null) {
            this.copy(next = value)
        } else {
            this.copy(next = next!!.append(value))
        }

    fun split(label: String): Pair<Nestable?, Nestable?> =
        if (this.label == label) {
            Pair(null, this)
        } else if (this.next != null) {
            val res = this.next!!.split(label)
            Pair(Nestable(this.label, res.first), res.second)
        } else {
            Pair(this, null)
        }

}

@Composable
fun ColumnScope.Parent(
    dragAndDropState: DragAndDropState<Nestable>,
    value: Nestable?,
    key: Int,
    enabled: Boolean = true,
    onDrop: (Nestable) -> Unit = {},
) {
    if (value == null) {
        var dragSubject: Nestable? by remember { mutableStateOf(null) }
        var accepting by remember { mutableStateOf(false) }
        val borderColor by animateColorAsState(
            with(MaterialTheme.colorScheme) {
                if (dragSubject != null) primary else onSurface
            },
        )
        if (enabled)
            Box(
                Modifier.weight(1f).padding(1.dp).fillMaxWidth().dropTarget(
                    key = key,
                    state = dragAndDropState,
                    onDragEnter = { state ->
                        println("Enter ($key)");dragSubject = state.data; accepting = true
                    },
                    onDragExit = { _ ->
                        println("Exit ($key)");dragSubject = null; accepting = false
                    },
                    onDrop = { println("Drop ($key)"); onDrop(it.data) },
                ).border(2.dp, borderColor, RoundedCornerShape(20.dp))
                    .background(if (enabled)Color(0x30eeb6c6) else Color(154,58,58), RoundedCornerShape(20.dp)),
            ) {
                if (dragSubject != null) {
                    Column {
                        Parent(dragAndDropState, dragSubject, key + 1, false) { }
                    }
                }
            }
    } else {
        DraggableItem(
            state = dragAndDropState,
            key = key,
            data = value,
            nested = false,
            enabled = enabled,
            modifier = Modifier.weight(1f),
            dropStrategy = DropStrategy.CenterDistance,
            draggableContent = {
                Column(Modifier.heightIn(40.dp, 180.dp).widthIn(80.dp, 240.dp)) {
                    RedBox(
                        false,
                        Modifier.fillMaxWidth(),
                    )
                    Parent(dragAndDropState, value.next, key + 1, enabled)
                }
            },
        ) {
            Column(
                with(
                    Modifier.fillMaxSize()
                        .scale(0.9f)
                        .alpha(if (dragAndDropState.draggedItem?.data == value) 0f else 1f),
                ) {
                    if (enabled)
                        this.border(
                            2.dp,
                            MaterialTheme.colorScheme.surfaceContainerHighest,
                            RoundedCornerShape(20.dp),
                        )
                    else
                        this
                },
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                RedBox(
                    false,
                    with(
                        Modifier.graphicsLayer {
                            alpha = if (isDragging) 0f else 1f
                        }.fillMaxWidth().height(40.dp),
                    ) {
                        if(enabled)
                        this
                        else
                            this.background(Color.Red)
                    },
                )
                Parent(dragAndDropState, value.next, key + 1, !isDragging && enabled)
            }
        }
    }
}

@Composable
fun NestedContent(modifier: Modifier) {
    val dragAndDropState = rememberDragAndDropState<Nestable>()
    var nestables by remember {
        mutableStateOf(
            arrayOf(
                Nestable("A"),
                Nestable("B", Nestable("C")),
                null,
            ),
        )
    }

    fun onDrop(value: Nestable, index: Int) {
        println("Drop at $index, $value")
        nestables = nestables.map {
            it?.split(value.label)?.first
        }.toTypedArray()

        nestables[index] = nestables[index]?.append(value) ?: value
        nestables.map {
            print("${it ?: "()"};")
            println()
        }
    }
    DragAndDropContainer(
        state = dragAndDropState,
        modifier = modifier,
    ) {
        Column(Modifier.fillMaxSize()) {
            Parent(dragAndDropState, nestables[0], 0, true) { onDrop(it, 0) }
            Parent(dragAndDropState, nestables[1], 3, true) { onDrop(it, 1) }
            Parent(dragAndDropState, nestables[2], 6,true) { onDrop(it, 2) }
            nestables.map {
                Text("${it ?: "()"}")
            }
        }
    }
}
