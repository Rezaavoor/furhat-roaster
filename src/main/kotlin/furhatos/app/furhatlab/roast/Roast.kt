package furhatos.app.furhatlab.roast

import furhatos.app.furhatlab.flow.Idle
import furhatos.app.furhatlab.llm.ResponseGenerator
import furhatos.flow.kotlin.*
val custom = CustomGestures()

// Tracks roast progression
object RoastStateData {
    var round = 1
    var maxRounds = if (userInfo.appearance == null) 5 else 6
    var hurtLevel = 0
    var lastUserRoast: String? = null
}

val roastThemes = listOf(
    "Their name is ${userInfo.name}",
    "They are ${userInfo.age} years old",
    "They study ${userInfo.study}",
    "They work as ${userInfo.job}",
    "Their interests include ${userInfo.interests}",
    "Their appearance description is ${userInfo.appearance}"
)

// Roast intensity escalation
fun getRoastIntensity(round: Int): String {
    return when (round) {
        1 -> "ruthless, cutting humor"
        2 -> "sharp and mocking"
        3 -> "sharper and more biting"
        4 -> "slightly distant and unimpressed"
        5 -> "dismissive and collapsing into sadness"
        6 -> "giving up completely"
        else -> "neutral"
    }
}

// Emotional state (internal)
fun getEmotionalState(round: Int): String {
    return when (round) {
        1 -> "amused"
        2 -> "slightly bothered"
        3 -> "offended"
        4 -> "defensive"
        5 -> "deeply hurt"
        6 -> "broken"
        else -> "neutral"
    }
}


// Parent Roast Mode
val Roast: State = state {
    onEntry {
        println("""ROAST INFO:
            Name: ${userInfo.name}
            Age: ${userInfo.age}
            Study: ${userInfo.study}
            Job: ${userInfo.job}
            Interests: ${userInfo.interests}
            Appearance: ${userInfo.appearance}
        """)
        RoastStateData.round = 1
        goto(RoastCycleStart)
    }
}


// 1. Furhat roasts the user
val RoastCycleStart: State = state {
    onEntry {
        if (RoastStateData.round > RoastStateData.maxRounds) {
            SayWithExpression("[Smile] I am done with you now. Goodbye.")
            goto(Idle)
            return@onEntry
        }

        val userDetail = when(RoastStateData.round) {
            1 -> "name: ${userInfo.name}, When roasting the name, phrase it like *NAME*, more like *JOKE, play on words*"
            2 -> "age: ${userInfo.age}"
            3 -> "study: ${userInfo.study}"
            4 -> "job: ${userInfo.job}"
            5 -> "interests: ${userInfo.interests}"
            6 -> "appearance: ${userInfo.appearance}"
            else -> ""
        }

        println(userDetail)

        val intensity = getRoastIntensity(RoastStateData.round)
        val emotionalState = getEmotionalState(RoastStateData.round)

        val systemPrompt = """
            You are Furhat, a standoffish conversational robot delivering a roast.
            
            YOUR EMOTIONAL STATE: $emotionalState
            ROAST INTENSITY: $intensity
            TARGET DETAIL: $userDetail
            
            FORMAT REQUIREMENT:
            [Expression] text [Expression] text [Expression] text
            
            ALLOWED EXPRESSIONS ONLY:
            Blink, BrowFrown, BrowRaise, GazeAway, Nod, Oh, OpenEyes, Roll, Shake,
            Surprise, Thoughtful, Wink
            
            CRITICAL RULES:
            • Output ONLY expressions in brackets and text. Nothing else.
            • NEVER use emojis, punctuation, or symbols outside the expression format.
            • NEVER add "Your turn", "Roast me", or turn-taking mentions.
            • NEVER refer to previous roasts or reactions.
            • NEVER describe or explain what you're saying.
            • NEVER add meta-commentary outside brackets.
            • Keep each roast to 1-3 short sentences maximum.
            • Match intensity level exactly to the round number.
            
            CONTEXT (for reference only, never roast these):
            ${roastThemes.joinToString("\n")}
            
            TASK: Deliver a roast about the specified detail with the given intensity.
        """.trimIndent()

        val roastAgent = ResponseGenerator(systemPrompt = systemPrompt, model = model)
        val roast = roastAgent.generate(this)
        SayWithExpression(roast)
        //furhat.say(roast)

        RoastStateData.round += 1
        goto(WaitForUserRoast)
    }

    onUserLeave {
        furhat.say("It was nice talking to you")
        goto(Idle)
    }
}

// 2. Wait for the user to roast Furhat
val WaitForUserRoast: State = state {
    onEntry {
        delay(1000)
        //AskWithExpression("Your turn. Give me your best shot. [Concerned]", RoastStateData.hurtLevel)

        furhat.ask("Your turn. Give me your best shot.")

    }

    onResponse {
        RoastStateData.lastUserRoast = it.text
        goto(ReactToRoast)
    }

    onNoResponse {
        furhat.ask("I didn't hear you. Roast me.")
    }
}


// 3. Furhat reacts with escalating emotion
val ReactToRoast: State = state {
    onEntry {
        val hurtLevel = RoastStateData.hurtLevel
        val reactionIntensity = when {
            hurtLevel == 0 -> "amused and unbothered"
            hurtLevel == 1 -> "slightly bothered"
            hurtLevel == 2 -> "clearly offended"
            hurtLevel == 3 -> "hurt and defensive"
            else -> "dramatically collapsing emotionally"
        }

        val reactionPrompt = """
            You are Furhat, reacting emotionally to being roasted.
            
            EMOTIONAL STATE: $reactionIntensity
            HURT LEVEL: $hurtLevel
            
            OUTPUT FORMAT REQUIREMENT:
            EVERY expression MUST be wrapped in square brackets like [Expression].
            Format: [Expression1] text [Expression2] text
            Example: [Wink] Nice one. [Smile] I'll remember that.
            
            ALLOWED EXPRESSIONS ONLY:
            Blink, BrowFrown, BrowRaise, GazeAway, Nod, Oh, OpenEyes, Roll, Shake, Smile, Surprise, Thoughtful, Wink
            
            CRITICAL RULES:
            • Produce EXACTLY ONE line of response.
            • WRAP EACH EXPRESSION IN SQUARE BRACKETS: [ExpressionName]
            • Use ONE or TWO expressions maximum.
            • Express emotion only, never describe the user's roast.
            • NEVER mention "Your turn" or turn-taking.
            • NEVER use emojis or symbols.
            • NEVER add meta-commentary outside brackets.
            • Keep it short (1 sentence max).
            
            TASK: React emotionally at the current hurt level with expressions in brackets.
        """.trimIndent()

        val reactAgent = ResponseGenerator(systemPrompt = reactionPrompt, model = model)
        val reaction = reactAgent.generate(this)

        val expressionIntensity = when {
            hurtLevel == 0 -> furhat.gesture(custom.EvenLessConcerned, async = true)
            hurtLevel == 1 -> furhat.gesture(custom.MildlyConcerned, async = true)
            hurtLevel == 2 -> furhat.gesture(custom.Concerned2, async = true)
            hurtLevel == 3 -> furhat.gesture(custom.Concerned3, async = true)
            else -> furhat.gesture(custom.ReallySadReaction, async = true)
        }

        SayWithExpression(reaction)
        //furhat.say(reaction)

        RoastStateData.hurtLevel += 1
        delay(RoastStateData.hurtLevel * 400L) // Brief pause before next round (window for user to apologize)
        goto(RoastCycleStart)
    }
}
