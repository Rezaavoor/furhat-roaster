package furhatos.app.furhatlab.roast

import furhatos.app.furhatlab.flow.chat.ChatState
import furhatos.flow.kotlin.State
import furhatos.flow.kotlin.state

object roastState {
    var round = 1
}

val Roast: State = state {
    onEntry {
        goto(ChatState)
    }
}