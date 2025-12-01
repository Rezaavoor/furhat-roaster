package furhatos.app.furhatlab.roast

import furhatos.app.furhatlab.llm.OpenAIChatCompletionModel
import furhatos.app.furhatlab.llm.ResponseGenerator
import furhatos.flow.kotlin.State
import furhatos.flow.kotlin.state

val Start: State = state {
    onEntry {
        goto(GetToKnow)
    }
}