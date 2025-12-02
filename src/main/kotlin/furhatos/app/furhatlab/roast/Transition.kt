package furhatos.app.furhatlab.roast

import furhatos.flow.kotlin.State
import furhatos.flow.kotlin.furhat
import furhatos.flow.kotlin.state
import furhatos.gestures.Gestures

val Transition: State = state {
    onEntry {
        furhat.say(" Where do you see yourself in 10 years?")
        delay(5000)
        furhat.gesture(Gestures.GazeAway)
        furhat.say("That's literally soo interesting...")
        goto(Roast)
    }

}