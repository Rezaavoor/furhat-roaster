package furhatos.app.furhatlab.roast


import furhatos.flow.kotlin.*
import furhatos.flow.kotlin.furhat
import furhatos.gestures.Gestures
import furhatos.gestures.Gesture
import furhatos.gestures.ARKitParams // if we create our own gestures
import furhatos.gestures.CharParams
import furhatos.gestures.defineGesture

class CustomGestures {
    val SadReaction = defineGesture("SadReaction") {
        frame(0.32, 0.72) {
            ARKitParams.BROW_INNER_UP to 1.0
            ARKitParams.EYE_WIDE_LEFT to 1.0
            ARKitParams.EYE_WIDE_RIGHT to 1.0
            ARKitParams.MOUTH_FROWN_LEFT to 1.0
            ARKitParams.MOUTH_FROWN_RIGHT to 1.0
            ARKitParams.MOUTH_ROLL_LOWER to 0.5
        }
        reset(2.0)
    }

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
            ARKitParams.EYE_LOOK_OUT_LEFT to 1.0
            ARKitParams.EYE_LOOK_IN_RIGHT to 1.0
        }
        reset(2.0)
    }

    val Test = defineGesture("Test") {
        frame(0.32, 0.72) {
            CharParams.LIP_BOTTOM_THINNER to 2.0
        }
        frame(0.72,1.0) {
            CharParams.LIP_BOTTOM_THINNER to 1.0
        }
    }

}


fun stringToGesture(name: String): Gesture? {
    return try {
        // Access property of Gestures by name
        val property = CustomGestures::class.members.firstOrNull { it.name.equals(name, ignoreCase = true) }
        property?.call(Gestures) as? Gesture

    } catch (e: Exception) {
        null
    }
}

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

fun splitGesturesAndText(input: String): Sequence<String> {
    val regex = Regex("""\[\w+]""")
    return sequence {
        var lastIndex = 0
        for (match in regex.findAll(input)) {
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
        if (part.matches(Regex("\\[[^]]+]"))) {
            val gestureName = part.removeSurrounding("[", "]")
            val gesture = stringToGesture(gestureName)
            if (gesture != null){

                furhat.gesture(gesture)
            }
        }
        else {
            furhat.say(part)
        }
    }
}