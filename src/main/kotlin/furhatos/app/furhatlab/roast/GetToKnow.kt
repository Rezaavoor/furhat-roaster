package furhatos.app.furhatlab.roast

import furhatos.app.furhatlab.llm.ResponseGenerator
import furhatos.flow.kotlin.State
import furhatos.flow.kotlin.furhat
import furhatos.flow.kotlin.onResponse
import furhatos.flow.kotlin.state

val userPrompts = listOf(
    "what is your name?",
    "hold old are you?",
    "what are you studying?",
    "what is your job?",
    "what are your interests?"
)
var counter = 0

val GetToKnow: State = state {
    onEntry {
        if (counter > 4) {
            goto(Transition)
        }
        furhat.ask (userPrompts[counter] )
    }

    onResponse {

        var userPrompt = userPrompts[counter]
        var systemPrompt = """
    You have received a response from a user that answers the following question
""" + userPrompt + """
    You should return a short string that directly answers the question SOLEY based on the user respnose. ONLY RETURN THE ANSWER, A SINGLE ANSWER. DO NOT ANSWER THE QUESTION YOURSLEF. 
  
""".trimIndent()
        val getToKnowAgent = ResponseGenerator(
            systemPrompt = systemPrompt,
            model = model
        )

        val response = getToKnowAgent.generate(this)
        if (counter == 0)
            userInfo.name = response
        else if (counter == 1)
            userInfo.age = response
        else if (counter == 2)
            userInfo.study = response
        else if (counter == 3)
            userInfo.job = response
        else if (counter == 4)
            userInfo.interests = response
        counter++
        reentry()
    }

}