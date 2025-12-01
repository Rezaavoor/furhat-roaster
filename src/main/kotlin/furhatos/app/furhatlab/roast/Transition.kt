package furhatos.app.furhatlab.roast

import furhatos.app.furhatlab.flow.Idle
import furhatos.app.furhatlab.flow.chat.ChatState
import furhatos.flow.kotlin.State
import furhatos.flow.kotlin.furhat
import furhatos.flow.kotlin.state

val Transition: State = state {
    onEntry {
        furhat.say(" Where do you see yourself in 10 years?")
        furhat.say("Okay, I got enough. Let's see what we have")
        goto(Roast)
    }

}