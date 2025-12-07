package furhatos.app.furhatlab.roast

import furhatos.flow.kotlin.State
import furhatos.flow.kotlin.furhat
import furhatos.flow.kotlin.state
import furhatos.gestures.Gestures

val Transition: State = state {
    onEntry {
        furhat.say("Okay, I like playing Fortnight and hanging out with my Alpha squad.")
        delay(400)
        furhat.say("Where do you see yourself in 10 years?")
        delay(4000)
        furhat.gesture(Gestures.GazeAway)
        furhat.say("Wow, that's literally so interesting...")
        delay(400)
        furhat.say("Now, let's begin the roast battle. I'll go first.")
        goto(Roast)
    }

}