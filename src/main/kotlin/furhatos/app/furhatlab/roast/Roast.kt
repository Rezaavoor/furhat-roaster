package furhatos.app.furhatlab.roast

import furhatos.app.furhatlab.flow.Idle
import furhatos.app.furhatlab.flow.chat.ChatState
import furhatos.app.furhatlab.flow.chat.responseGenerator
import furhatos.app.furhatlab.flow.fruitseller.FruitSellerGreeting
import furhatos.app.furhatlab.llm.OpenAIChatCompletionModel
import furhatos.app.furhatlab.llm.ResponseGenerator
import furhatos.flow.kotlin.State
import furhatos.flow.kotlin.furhat
import furhatos.flow.kotlin.onNoResponse
import furhatos.flow.kotlin.onResponse
import furhatos.flow.kotlin.onUserLeave
import furhatos.flow.kotlin.state

val model = OpenAIChatCompletionModel(serviceKey = apiKey)



object roastState {
    var round = 1
}

val Roast: State = state {
    onEntry {
        val systemPrompt = """
            You are a pro roaster and need to roast me. You have the following information from me:
        + """ + userInfo + "".trimIndent()
        val roastAgent = ResponseGenerator(
            systemPrompt = systemPrompt,
            model = model
        )
        val resposne = roastAgent.generate(this)
        furhat.say(resposne)
        reentry()
    }
    onReentry {
        goto(Idle)
    }
    onResponse {
        reentry()
    }

    onNoResponse {
        reentry()
    }

    onUserLeave {
        furhat.say("It was nice talking to you")
        goto(Idle)
    }
}