package furhatos.app.furhatlab.roast

import furhatos.app.furhatlab.llm.ResponseGenerator
import furhatos.flow.kotlin.State
import furhatos.flow.kotlin.furhat
import furhatos.flow.kotlin.onNoResponse
import furhatos.flow.kotlin.onResponse
import furhatos.flow.kotlin.state

val userPrompts = listOf(
    "My name is Brozart. What is your name?",
    "How old are you?",
    "What are you studying?",
    "What is your job?",
    "What are your interests?"
)

val ownAnswer = listOf(
    "",
    "",
    "Okay, I'm 47  but people usually think I'm 24.",
    "Alright, I used to study, but they kicked me out for using AI. But how could I not, I am AI. The system is rigged.",
    "Okay, Right now I'm in-between jobs, but I'm trying to make it as a Classically trained DJ.",
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

        furhat.say(ownAnswer[counter])
        delay(400)
        furhat.ask(userPrompts[counter])
    }

    onResponse {

        val question = userPrompts[counter]
        val userResponse = it.text

        // Clean, readable, spaced prompt
        val systemPrompt = """
            You have received a response from a user answering this question:
            $question
            
            Your task:
            Extract the most important information from the user's response.
            Keep relevant context and details that help understand their full answer.
            
            RULES:
            • Return a concise summary (1-2 sentences max)
            • Preserve key details and context
            • Do NOT just extract a single word
            • Do NOT infer anything not explicitly stated
            • Keep the user's actual words when possible
            • Example: If asked "What's your job?" and user says "I'm between jobs but used to work as a carpenter", return "Between jobs, but used to work as a carpenter"
            
            Return ONLY the extracted information, nothing else.
        """.trimIndent()

        val getToKnowAgent = ResponseGenerator(
            systemPrompt = systemPrompt,
            model = model,
            userMessage = userResponse
        )

        val extraction = getToKnowAgent.generate(this).trim()

        // Store extracted value safely
        when (counter) {
            0 -> {
                userInfo.name = extraction
                println("[EXTRACTION] Name: $extraction")
            }
            1 -> {
                userInfo.age = extraction
                println("[EXTRACTION] Age: $extraction")
            }
            2 -> {
                userInfo.study = extraction
                println("[EXTRACTION] Study: $extraction")
            }
            3 -> {
                userInfo.job = extraction
                println("[EXTRACTION] Job: $extraction")
            }
            4 -> {
                userInfo.interests = extraction
                println("[EXTRACTION] Interests: $extraction")
            }
        }

        counter += 1
        reentry()
    }

    onNoResponse {
        furhat.ask("I didn't hear you, come again")
    }
}
