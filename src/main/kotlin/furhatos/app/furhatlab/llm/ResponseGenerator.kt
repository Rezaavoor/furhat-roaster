package furhatos.app.furhatlab.llm

import furhatos.flow.kotlin.FlowControlRunner
import kotlinx.coroutines.runBlocking

/**
 * A simple stateless response generator.
 * It sends only the system prompt to the LLM, optionally with a user message.
 * This prevents the model from drifting, adding emojis,
 * or reacting to earlier exchanges.
 */
class ResponseGenerator(
    private val systemPrompt: String,
    private val model: ChatCompletionModel,
    private val userMessage: String? = null
) {

    fun generate(runner: FlowControlRunner): String {
        val response = runner.call {
            getChatCompletion()
        } as? String
        return response ?: "An error has occurred"
    }

    /**
     * Sends a prompt to the model:
     * system: <systemPrompt>
     * optionally user: <userMessage>
     *
     * No history, no assistant messages.
     */
    private fun getChatCompletion(): String? {
        val messages = mutableListOf<Message>(
            SystemMessage(systemPrompt)
        )
        
        if (userMessage != null) {
            messages.add(UserMessage(userMessage))
        }

        return try {
            val completion = runBlocking {
                model.request(Prompt(messages))
            }
            completion.text
        } catch (e: Exception) {
            println("Error getting LLM completion: ${e.message}")
            null
        }
    }
}
