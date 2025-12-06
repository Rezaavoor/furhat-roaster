package furhatos.app.furhatlab.roast

import furhatos.app.furhatlab.roast.utils.getUserAppearance
import furhatos.flow.kotlin.State
import furhatos.flow.kotlin.state
import furhatos.flow.kotlin.users

val Start: State = state {
    onEntry {
        getUserAppearance(this, users.current)
        goto(GetToKnow)
    }
}