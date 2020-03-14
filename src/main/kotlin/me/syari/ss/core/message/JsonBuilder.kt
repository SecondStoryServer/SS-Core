package me.syari.ss.core.message

import me.syari.ss.core.code.StringEditor.toColor
import net.md_5.bungee.api.chat.ClickEvent
import net.md_5.bungee.api.chat.HoverEvent
import net.md_5.bungee.api.chat.TextComponent

class JsonBuilder {
    private val message = mutableListOf<JsonMessage>()

    fun append(text: String, hover: String? = null, click: Click? = null): JsonBuilder {
        message.add(JsonMessage.Text(text, hover, click))
        return this
    }

    fun appendln(): JsonBuilder {
        message.add(JsonMessage.NewLine)
        return this
    }

    fun appendln(text: String, hover: String? = null, click: Click? = null): JsonBuilder {
        return append(text, hover, click).appendln()
    }

    val toTextComponent
        get() = TextComponent().apply {
            message.forEach { eachMessage ->
                when (eachMessage) {
                    is JsonMessage.Text -> addExtra(
                        TextComponent(eachMessage.text.toColor).apply {
                            eachMessage.hover?.let {
                                hoverEvent = HoverEvent(HoverEvent.Action.SHOW_TEXT, arrayOf(TextComponent(it.toColor)))
                            }
                            eachMessage.click?.let {
                                clickEvent = ClickEvent(it.event, it.content.toColor)
                            }
                        }
                    )
                    is JsonMessage.NewLine -> addExtra("\n")
                }
            }
        }

    sealed class JsonMessage {
        class Text(val text: String, val hover: String?, val click: Click?) : JsonMessage()
        object NewLine : JsonMessage()
    }

    sealed class Click(val event: ClickEvent.Action, val content: String) {
        class RunCommand(content: String) : Click(ClickEvent.Action.RUN_COMMAND, content)
        class TypeText(content: String) : Click(ClickEvent.Action.SUGGEST_COMMAND, content)
        class OpenURL(content: String) : Click(ClickEvent.Action.OPEN_URL, content)
        class Clipboard(content: String) : Click(ClickEvent.Action.COPY_TO_CLIPBOARD, content)
    }
}