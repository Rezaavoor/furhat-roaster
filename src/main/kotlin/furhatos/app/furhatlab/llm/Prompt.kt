package furhatos.app.furhatlab.llm

import org.json.JSONArray
import org.json.JSONObject

class Prompt {

    val messages: List<Message>

    constructor(vararg contents: Message) {
        messages = contents.toList()
    }

    constructor(contents: List<Message>) {
        messages = contents.toList()
    }

    fun addAll(other: List<Message>): Prompt {
        return Prompt(this.messages + other)
    }

    fun toJSON() =
    JSONArray(messages.map { message ->
        JSONObject().apply {
            if (message is SystemMessage) {
                put("role", "system")
                put("content", message.content)
            } else if (message is AssistantMessage) {
                put("role", "assistant")
                put("content", message.content)
            } else if (message is UserMessage) {
                put("role", "user")
                put("content", message.content)
            } else if (message is VisionUserMessage) {
                put("role", "user")
                put("content", JSONArray().apply {
                    // text part
                    put(
                        JSONObject().apply {
                            put("type", "text")
                            put("text", message.text)
                        }
                    )
                    // image part
                    put(
                        JSONObject().apply {
                            put("type", "image_url")
                            put(
                                "image_url",
                                JSONObject().apply {
                                    put("url", message.imageUrl)
                                }
                            )
                        }
                    )
                })
            }
        }
    })

}

interface Message

data class AssistantMessage(val content: String): Message

data class UserMessage(val content: String): Message

data class SystemMessage(val content: String): Message

data class VisionUserMessage(
    val text: String,
    val imageUrl: String
) : Message