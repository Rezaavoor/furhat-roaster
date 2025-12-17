package furhatos.app.furhatlab.roast

import furhatos.app.furhatlab.llm.ResponseGenerator
import furhatos.app.furhatlab.roast.utils.saveDialogHistory
import furhatos.flow.kotlin.*
val custom = CustomGestures()

// Tracks roast progression
object RoastStateData {
    var round = 1
    var maxRounds = 8
    var hurtLevel = 0
    var lastUserRoast: String? = null
}

fun resetRoastStateData() {
    RoastStateData.round = 1
    RoastStateData.maxRounds = 8
    RoastStateData.hurtLevel = 0
    RoastStateData.lastUserRoast = null

    println("Roast state data reset")
}

val roastThemes = listOf(
    "Their name is ${userInfo.name}",
    "They are ${userInfo.age} years old",
    "Their name is ${userInfo.name} and they are ${userInfo.age} years old",
    "Their appearance description is ${userInfo.appearance}. FOCUS ONLY on face",
    "They study ${userInfo.study}",
    "They work as ${userInfo.job}",
    "Their interests include ${userInfo.interests}",
    "Their appearance description is ${userInfo.appearance}. FOCUS ONLY on clothes"
)

// Roast intensity escalation
fun getRoastIntensity(round: Int): String {
    return when (round) {
        1 -> "ruthless, cutting humor"
        2 -> "sharp and mocking"
        3 -> "sharper and more biting"
        4 -> "slightly distant and unimpressed"
        5 -> "slightly distant and unimpressed"
        6 -> "dismissive and collapsing into sadness"
        7 -> "dismissive and collapsing into sadness"
        8 -> "giving up completely"
        else -> "neutral"
    }
}

// Emotional state (internal)
fun getEmotionalState(round: Int): String {
    return when (round) {
        1 -> "amused"
        2 -> "slightly bothered"
        3 -> "offended"
        4 -> "offended"
        5 -> "deeply hurt"
        6 -> "deeply hurt"
        7 -> "broken"
        8 -> "broken"
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
            SayWithExpression("[CloseEyes] Please make it stop.")
            saveDialogHistory()
            goto(IdleRoast)
            return@onEntry
        }

        val userDetail = when(RoastStateData.round) {
            1 -> "name: ${userInfo.name}, When roasting the name, phrase it like *NAME*, more like *JOKE, play on words*"
            2 -> "age: ${userInfo.age}"
            3 -> "name: ${userInfo.name}, age: ${userInfo.age}"  
            4 -> "appearance: ${userInfo.appearance}"
            5 -> "study: ${userInfo.study}"
            6 -> "job: ${userInfo.job}"
            7 -> "interests: ${userInfo.interests}"
            8 -> "appearance: ${userInfo.appearance}"
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
            Example: [Roll] Oh wow, you really got me, [Smile] I’ll have to think about that.
            
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
            • Break the text into short, natural fragments separated by commas or full stops.
            • Do not produce long, uninterrupted sentences; aim for 2–4 short clauses per response.
            
            CONTEXT (for reference only, never roast these):
            ${roastThemes.joinToString("\n")}
            
            TASK: Deliver a roast about the specified detail with the given intensity.
        """.trimIndent()

        val roastAgent = ResponseGenerator(systemPrompt = systemPrompt, model = model)
        val roast = roastAgent.generate(this)
        println(roast)
        SayWithExpression(roast)
        //furhat.say(roast)

        RoastStateData.round += 1
        goto(WaitForUserRoast)
    }

    onUserLeave {
        furhat.say("It was nice talking to you")
        goto(IdleRoast)
    }
}

// 2. Wait for the user to roast Furhat
val WaitForUserRoast: State = state {
    onEntry {
        delay(300)
        var round = RoastStateData.round
        val yourTurn = when {
            round == 2 -> "Okay, your turn, [Nod] give it to me funny guy!"
            round == 3 -> "Let's hear it smartass, [Blink] what do you got? "
            round == 4 -> "[Smile] OK, [GazeAway] roast away"
            round == 5 -> "[BrowRaise] Alright, you're up, [Blink] shoot. "
            else -> "[BrowRaise] Alright, you're up."
        }
        AskWithExpression(yourTurn)
    }

    onResponse {
        RoastStateData.lastUserRoast = it.text
        goto(ReactToRoast)
    }

    onNoResponse {
        furhat.ask("I didn't hear you. Roast me!")
    }
}


// 3. Furhat reacts with escalating emotion
val ReactToRoast: State = state {
    onEntry {
        val hurtLevel = RoastStateData.hurtLevel
        val reactionIntensity = when {
            hurtLevel == 0 -> "amused and unbothered"
            hurtLevel == 1 -> "slightly bothered"
            hurtLevel == 2 -> "clearer offended"
            hurtLevel == 3 -> "hurt and defensive"
            hurtLevel == 4 -> "hurt and defensive"
            else -> "dramatically collapsing emotionally"
        }

        //printing all states
        println("""
            EMOTIONAL STATE: $reactionIntensity
            HURT LEVEL: $hurtLevel
        """)

        val reactionPrompt = """
            You are Furhat, reacting emotionally to being roasted. If no proper roast received, react with N/A.
            
            EMOTIONAL STATE: $reactionIntensity
            HURT LEVEL: $hurtLevel
            
            IMPORTANT - INVALID USER RESPONSE HANDLING:
            - Treat the user response as INVALID if it is empty, pure noise, obviously incomplete, off-topic, or you cannot clearly understand it as a roast.
            - In ALL INVALID cases, output EXACTLY the string N/A (capital N, slash, capital A) with no other words, no punctuation, and no quotation marks.
            - For any CLEAR, meaningful roast, DO NOT output N/A; instead, follow the TASK above and produce a normal emotional reaction.
            
            OUTPUT FORMAT REQUIREMENT:
            EVERY expression MUST be wrapped in square brackets like [Expression]. If invalid user response, return N/A
            Format: [Expression1] text [Expression2] text
            Example: [Wink] Nice one. [Smile] I'll remember that.
            Invalid User Response Example: N/A
            
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
            • Break the text into short, natural fragments separated by commas or full stops.
            • Do not produce long, uninterrupted sentences; aim for 2–4 short clauses per response.
            
            TASK: React emotionally at the current hurt level with expressions in brackets.
        """.trimIndent()


        val reactAgent = ResponseGenerator(systemPrompt = reactionPrompt, model = model)
        val reaction = reactAgent.generate(this).trim()
        print("REACTION: " + reaction)

        if (reaction.equals("N/A", ignoreCase = true)) {
            furhat.say("I didn't really get that, come again")
            goto(WaitForUserRoast)
            return@onEntry
        }

        if (useExpression == true){
            val expressionIntensity = when {
                hurtLevel == 0 -> furhat.gesture(custom.EvenLessConcerned, async = true)
                hurtLevel == 1 -> furhat.gesture(custom.EvenLessConcerned, async = true)
                hurtLevel == 2 -> furhat.gesture(custom.MildlyConcerned, async = true)
                hurtLevel == 3 -> furhat.gesture(custom.Concerned2, async = true)
                hurtLevel == 4 -> furhat.gesture(custom.Concerned3, async = true)
                else -> furhat.gesture(custom.ReallySadReaction, async = true)
            }
        }
        //else {
            //furhat.gesture(custom.EvenLessConcerned, async = true)
        //}
        

        SayWithExpression(reaction)
        //furhat.say(reaction)

        RoastStateData.hurtLevel += 1
        delay(RoastStateData.hurtLevel * 200L) // Brief pause before next round (window for user to apologize)
        goto(RoastCycleStart)
    }
}
