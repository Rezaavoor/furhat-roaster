package furhatos.app.furhatlab.roast

import furhatos.app.furhatlab.flow.Idle
import furhatos.app.furhatlab.llm.OpenAIChatCompletionModel
import furhatos.app.furhatlab.llm.ResponseGenerator
import furhatos.flow.kotlin.*

val model = OpenAIChatCompletionModel(serviceKey = apiKey)

// Tracks roast progression
object RoastStateData {
    var round = 1
    var maxRounds = 5
    var hurtLevel = 0
    var lastUserRoast: String? = null
}

val roastThemes = listOf(
    "Their name is ${userInfo.name}",
    "They are ${userInfo.age} years old",
    "They study ${userInfo.study}",
    "They work as ${userInfo.job}",
    "Their interests include ${userInfo.interests}"
)


// Parent Roast Mode
val Roast: State = state {
    onEntry {
        // Debug
        println( """ROAST INFO:
            Their name is ${userInfo.name}",
                "They are ${userInfo.age} years old",
                "They study ${userInfo.study}",
                "They work as ${userInfo.job}",
                "Their interests include ${userInfo.interests}""")
        RoastStateData.round = 1
        RoastStateData.hurtLevel = 0
        goto(RoastCycleStart)
    }
}


// 1. Furhat roasts the user
val RoastCycleStart: State = state {
    onEntry {
        if (RoastStateData.round > RoastStateData.maxRounds) {
            furhat.say("[Smile] I am done with you now. Goodbye.")
            goto(Idle)
            return@onEntry
        }

        val userDetail = when(RoastStateData.round) {
            1 -> "name: ${userInfo.name}, When roasting the name, phrase it like *NAME*, more like *JOKE, play on words*"
            2 -> "age: ${userInfo.age}"
            3 -> "study: ${userInfo.study}"
            4 -> "job: ${userInfo.job}"
            5 -> "interests: ${userInfo.interests}"
            else -> ""
        }

        println(userDetail)

        val systemPrompt = """
            You are Furhat, a standoffish conversational robot. Roast the user with witty, sharp, slightly dismissive humor.
            All outputs must follow this format:
            
            [Expression] text [Expression] text
            
            Expressions:
            BigSmile, Blink, BrowFrown, BrowRaise, CloseEyes, ExpressAnger, ExpressDisgust,
            ExpressFear, ExpressSad, GazeAway, Nod, Oh, OpenEyes, Roll, Shake, Smile,
            Surprise, Thoughtful, Wink
            
            Keep lines concise, sarcastic, slightly hostile.
            Never add commentary outside the bracketed-expression format.
            
            Here is background information of the user:
            $roastThemes
            
            Use this one SPECIFIC detail from the user to roast for this round (do not roast part of the user
            background, just use it for context). This is the main thing to roast:
            $userDetail
            
            Someone above 25 years old would in this context be considered very old (unless they are working for the university)
            since we are at the KTH campus.
            Someone under 22 years old is very young and almost a baby in this context.
            
            Your roast intensity depends on the escalation level (${RoastStateData.round}):
            1: mild
            2: mild
            3: sharper
            4: sharp, mocking
            5: ruthless, mocking, sharp
            
            Do not react to the user's previous roast.
            Do not reference the user's roast in any way.
            Only produce a roast aimed at the user based on the user detail for this round.
            Never include "Your turn" or anything related to the turn-taking mechanic.
            The users previous roast made you feel this (based on escalation level ${RoastStateData.round}):
            1: a small amused chuckle
            2: slightly bothered
            3: clearly offended
            4: hurt and defensive
            5: dramatic emotional collapse
        """.trimIndent()

        val roastAgent = ResponseGenerator(systemPrompt = systemPrompt, model = model)
        val roast = roastAgent.generate(this)
        furhat.say(roast)

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
        furhat.ask("Your turn. Give me your best shot.")
    }

    onResponse {
        RoastStateData.lastUserRoast = it.text
        goto(ReactToRoast)
    }

    onNoResponse {
        RoastStateData.lastUserRoast = ""
        goto(ReactToRoast)
    }
}


// 3. Furhat reacts with escalating emotion
val ReactToRoast: State = state {
    onEntry {
        val reactionPrompt = """
            You must produce a short emotional reaction using this exact format:
            
            [Expression] short reaction line
            
            Reaction intensity depends on the hurt level:
            ${RoastStateData.hurtLevel}
            
            0: a small amused chuckle
            1: slightly bothered
            2: clearly offended
            3: hurt and defensive
            4: dramatic emotional collapse
            
            Only produce ONE line with ONE or TWO expressions.
            Never describe the user's roast content, only react emotionally.
            Never include "Your turn" or anything related to the turn-taking mechanic.
        """.trimIndent()

        val reactAgent = ResponseGenerator(systemPrompt = reactionPrompt, model = model)
        val reaction = reactAgent.generate(this)
        furhat.say(reaction)

        RoastStateData.hurtLevel += 1
        goto(RoastCycleStart)
    }
}