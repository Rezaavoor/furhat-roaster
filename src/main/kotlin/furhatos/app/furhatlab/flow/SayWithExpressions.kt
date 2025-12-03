package furhatos.app.furhatlab.roast


import furhatos.flow.kotlin.*
import furhatos.flow.kotlin.furhat
import furhatos.gestures.Gestures
import furhatos.gestures.Gesture
import furhatos.gestures.ARKitParams // if we create our own gestures

fun stringToGesture(name: String): Gesture? {
    return try {
        // Access property of Gestures by name
        val property = Gestures::class.members.firstOrNull { it.name.equals(name, ignoreCase = true) }
        property?.call(Gestures) as? Gesture

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