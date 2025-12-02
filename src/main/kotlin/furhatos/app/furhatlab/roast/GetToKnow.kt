package furhatos.app.furhatlab.roast

import furhatos.app.furhatlab.llm.ResponseGenerator
import furhatos.flow.kotlin.State
import furhatos.flow.kotlin.furhat
import furhatos.flow.kotlin.onResponse
import furhatos.flow.kotlin.state

val userPrompts = listOf(
    "What is your name?",
    "How old are you?",
    "What are you studying?",
    "What is your job?",
    "What are your interests?"
)

var counter = 0

val GetToKnow: State = state {

    onEntry {
        if (counter >= userPrompts.size) {
            // Debug
            println( """Their name is ${userInfo.name}",
                "They are ${userInfo.age} years old",
                "They study ${userInfo.study}",
                "They work as ${userInfo.job}",
                "Their interests include ${userInfo.interests}""")
            goto(Transition)
            return@onEntry
        }

        furhat.ask(userPrompts[counter])
    }

    onResponse {

        val question = userPrompts[counter]

        // Clean, readable, spaced prompt
        val systemPrompt = """
            You have received a response from a user.
            
            The response is answering this question:
            $question
            
            Your task:
            Return ONLY the extracted answer as a short string.
            Do not repeat the question.
            Do not add explanation.
            Do not infer anything not said.
            Return a single short answer string.
        """.trimIndent()

        val getToKnowAgent = ResponseGenerator(
            systemPrompt = systemPrompt,
            model = model
        )

        val extraction = getToKnowAgent.generate(this).trim()

        // Store extracted value safely
        when (counter) {
            0 -> userInfo.name = extraction
            1 -> userInfo.age = extraction
            2 -> userInfo.study = extraction
            3 -> userInfo.job = extraction
            4 -> userInfo.interests = extraction
        }

        counter += 1
        reentry()
    }
}
