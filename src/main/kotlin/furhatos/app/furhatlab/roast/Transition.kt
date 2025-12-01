package furhatos.app.furhatlab.roast

import furhatos.flow.kotlin.State
import furhatos.flow.kotlin.state

val Transition: State = state {
    onEntry {
            goto(Roast)
    }

}