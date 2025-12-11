package furhatos.app.furhatlab.roast

import furhatos.flow.kotlin.State
import furhatos.flow.kotlin.furhat
import furhatos.flow.kotlin.onUserEnter
import furhatos.flow.kotlin.state



val IdleRoast: State = state {
    onEntry {
        furhat.attendNobody()
    }

    onUserEnter {
        furhat.attend(it)
        goto(InitRoast)
    }
}
