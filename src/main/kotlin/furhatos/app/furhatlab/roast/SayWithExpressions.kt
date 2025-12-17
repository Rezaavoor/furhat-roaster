package furhatos.app.furhatlab.roast


import furhatos.flow.kotlin.*
import furhatos.flow.kotlin.furhat
import furhatos.gestures.Gestures
import furhatos.gestures.Gesture
import furhatos.gestures.ARKitParams // if we create our own gestures
import furhatos.gestures.BasicParams
import furhatos.gestures.CharParams
import furhatos.gestures.defineGesture

class CustomGestures {
    val Cheeky = defineGesture("Cheeky") {
        frame(0.32, 0.72) {
            ARKitParams.BROW_OUTER_UP_LEFT to 1.0
            ARKitParams.MOUTH_SMILE_RIGHT to 0.8
            ARKitParams.MOUTH_SMILE_LEFT to 0.5
            ARKitParams.EYE_SQUINT_LEFT to 0.2
            ARKitParams.EYE_SQUINT_RIGHT to 0.2
            ARKitParams.MOUTH_PUCKER to 1.0
            ARKitParams.BROW_INNER_UP to 0.2
        }
        frame(0.32, 0.6) {
            ARKitParams.EYE_LOOK_OUT_LEFT to 0.6
            ARKitParams.EYE_LOOK_IN_RIGHT to 0.6
        }
        reset(15.0)
    }

    // Hurtlevel 0
    val EvenLessConcerned = defineGesture("Concerned") {
        frame(0.6, persist = true) {
            ARKitParams.BROW_INNER_UP to 0.8
            ARKitParams.EYE_WIDE_LEFT to 0.3
            ARKitParams.EYE_WIDE_RIGHT to 0.3
        }
    }

    // Hurtlevel 1
    val MildlyConcerned = defineGesture("Concerned") {
        frame(0.6, persist = true) {
            ARKitParams.BROW_INNER_UP to 0.8
            ARKitParams.EYE_WIDE_LEFT to 0.3
            ARKitParams.EYE_WIDE_RIGHT to 0.3
            ARKitParams.MOUTH_FROWN_LEFT to 0.4
            ARKitParams.MOUTH_FROWN_RIGHT to 0.4
        }

    }

    // Hurtlevel 2
    val Concerned2 = defineGesture("Concerned") {
        frame(0.6, persist = true) {
            ARKitParams.BROW_INNER_UP to 0.8
            ARKitParams.EYE_WIDE_LEFT to 0.5
            ARKitParams.EYE_WIDE_RIGHT to 0.5
            ARKitParams.MOUTH_FROWN_LEFT to 0.6
            ARKitParams.MOUTH_FROWN_RIGHT to 0.6
            ARKitParams.MOUTH_ROLL_LOWER to 0.5
        }
    }

    // Hurtlevel 3
    val Concerned3 = defineGesture("Concerned") {
        frame(0.6, persist = true) {
            ARKitParams.BROW_INNER_UP to 1.0
            ARKitParams.EYE_WIDE_LEFT to 0.7
            ARKitParams.EYE_WIDE_RIGHT to 0.7
            ARKitParams.MOUTH_FROWN_LEFT to 1.0
            ARKitParams.MOUTH_FROWN_RIGHT to 1.0
            ARKitParams.MOUTH_ROLL_LOWER to 0.5
        }
    }

    // Hurtlevel 4
    val ReallySadReaction = defineGesture("SadReaction") {
        frame(0.6, persist = true) {
            ARKitParams.BROW_INNER_UP to 1.0
            ARKitParams.EYE_WIDE_LEFT to 1.0
            ARKitParams.EYE_WIDE_RIGHT to 1.0
            ARKitParams.MOUTH_FROWN_LEFT to 1.0
            ARKitParams.MOUTH_FROWN_RIGHT to 1.0
            ARKitParams.MOUTH_ROLL_LOWER to 0.8
        }
    }

    // Less like a frown and more like a worried?
    val Frown = defineGesture("Frown") {
        frame(0.5, 0.8) {
            ARKitParams.BROW_DOWN_RIGHT to 0.8
            ARKitParams.BROW_DOWN_LEFT to 0.8
            ARKitParams.EYE_SQUINT_LEFT to 0.3
            ARKitParams.EYE_SQUINT_RIGHT to 0.3
            ARKitParams.MOUTH_FROWN_LEFT to 0.3
            ARKitParams.MOUTH_FROWN_RIGHT to 0.3
        }
        reset(4.0)

    }

    val Smile = defineGesture("Smile") {
        frame(0.32, 0.72) {
            ARKitParams.MOUTH_SMILE_LEFT to 1.0
            ARKitParams.MOUTH_SMILE_RIGHT to 1.0
            ARKitParams.EYE_SQUINT_LEFT to 0.3
            ARKitParams.EYE_SQUINT_RIGHT to 0.3
        }
        reset(2.0)

    }

    val Nod = defineGesture("Nod") {
        frame(0.32, 0.72) {
            BasicParams.NECK_TILT to 1.0
        }
        reset(2.0)

    }

    val Roll = defineGesture("Roll") {
        frame(0.32, 2.0) {
            BasicParams.NECK_ROLL to 1.0
        }
        reset(2.0)

    }
    companion object

}

/* Original Gesture class*/
fun stringToGesture(name: String): Gesture? {
    return try {
        // Access property of Gestures by name
        val property = Gestures::class.members.firstOrNull { it.name.equals(name, ignoreCase = true) }
        property?.call(Gestures) as? Gesture

    } catch (e: Exception) {
        null
    }
}

/* Using the Custom Gesture class*/
fun stringToCustomGesture(name: String): Gesture? {
    return try {
        val customGestures = CustomGestures()
        val property = CustomGestures::class.members
            .firstOrNull { it.name.equals(name, ignoreCase = true) }
        property?.call(customGestures) as? Gesture
    } catch (e: Exception) {
        null
    }
}

val gestureRegex = Regex("""\[[^]]+]""") // Checks for []

fun splitGesturesAndText(input: String): Sequence<String> {

    return sequence {
        var lastIndex = 0
        for (match in gestureRegex.findAll(input)) {
            //Text between gestures (.say())
            if (match.range.first > lastIndex) {
                val text = input.substring(lastIndex, match.range.first).trim()
                if (text.isNotEmpty()) yield(text)
            }

            yield(match.value)
            lastIndex = match.range.last +1
        }
        // Yield remaining text after the last gesture
        if (lastIndex < input.length) {
            val text = input.substring(lastIndex).trim()
            if (text.isNotEmpty()) yield(text)
        }
    }
}

fun FlowControlRunner.SayWithExpression(input: String) {
    for(part in splitGesturesAndText(input)){
        println(part)
        if (part.matches(gestureRegex)) {
            val gestureName = part.removeSurrounding("[", "]")
            val gesture = stringToGesture(gestureName)
            if (gesture != null && useExpression == true){
                furhat.gesture(gesture)
            }
        }
        else {
            furhat.say(part)
        }
    }
}

fun FlowControlRunner.AskWithExpression(input: String) {
    val parts = splitGesturesAndText(input).toList()
    var lastTextPartIndex = -1
    
    // Find the index of the last text part (non-gesture)
    for (i in parts.lastIndex downTo 0) {
        if (!parts[i].matches(gestureRegex)) {
            lastTextPartIndex = i
            break
        }
    }
    
    for(i in parts.indices) {
        val part = parts[i]
        println(part)
        if (part.matches(gestureRegex)) {
            val gestureName = part.removeSurrounding("[", "]")
            val gesture = stringToGesture(gestureName)
            if (gesture != null && useExpression == true){
                furhat.gesture(gesture, async = true)
            }
        }
        else {
            if (i == lastTextPartIndex) {
                furhat.ask(part)
            } else {
                furhat.say(part)
            }
        }
    }
}
