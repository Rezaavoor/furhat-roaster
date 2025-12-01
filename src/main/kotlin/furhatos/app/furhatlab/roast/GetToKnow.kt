package furhatos.app.furhatlab.roast

import furhatos.flow.kotlin.State
import furhatos.flow.kotlin.state

object GetToKnowState {
    var round = 1
}

val GetToKnow: State = state {
    onEntry {
        goto(Transition)
    }
}