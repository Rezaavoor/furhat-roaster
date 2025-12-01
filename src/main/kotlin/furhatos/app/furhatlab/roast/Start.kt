package furhatos.app.furhatlab.roast

import furhatos.flow.kotlin.State
import furhatos.flow.kotlin.state

val Start: State = state {
    onEntry {
        goto(GetToKnow)
    }
}