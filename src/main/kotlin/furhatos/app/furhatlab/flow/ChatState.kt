package furhatos.app.furhatlab.flow.chat

import furhatos.app.furhatlab.flow.Idle
import furhatos.app.furhatlab.llm.OpenAIChatCompletionModel
import furhatos.app.furhatlab.llm.ResponseGenerator
import furhatos.flow.kotlin.*
import furhatos.nlu.common.Goodbye

val model = OpenAIChatCompletionModel(serviceKey = "MY_OPENAI_KEY")

val responseGenerator = ResponseGenerator(
    systemPrompt = "You are a friendly and helpful social robot. Your name is Furhat. You give very brief answers.",
    model = model
)

val ChatState = state {

    onEntry {
        furhat.say("What do you want to chat about?")
        reentry()
    }

    onReentry {
        furhat.listen()
    }

    onResponse<Goodbye> {
        furhat.say("It was nice talking to you")
        goto(Idle)
    }

    onResponse {
        val furhatResponse = responseGenerator.generate(this)
        furhat.say(furhatResponse)
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
