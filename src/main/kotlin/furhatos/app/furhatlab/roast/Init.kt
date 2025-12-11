package furhatos.app.furhatlab.roast

import furhatos.app.furhatlab.llm.OpenAIChatCompletionModel
import furhatos.app.furhatlab.llm.ResponseGenerator
import furhatos.flow.kotlin.State
import furhatos.flow.kotlin.furhat
import furhatos.flow.kotlin.state
import furhatos.flow.kotlin.users
import furhatos.gestures.Gestures


val apiKey: String = System.getenv("OPENAI_API_KEY")
    ?: error("Missing OPENAI_API_KEY")

val model = OpenAIChatCompletionModel(serviceKey = apiKey)

object userInfo {
    var name:String? = null
    var age:String? = null
    var study:String? = null
    var job:String? = null
    var interests:String? = null
    var appearance:String? = null

    override fun toString(): String {
        return "userInfo(name=$name, age=$age, study=$study, job=$job, interests=$interests, appearance=$appearance)"
    }
}

fun resetUserInfo() {
    userInfo.name = null
    userInfo.age = null
    userInfo.study = null
    userInfo.job = null
    userInfo.interests = null
    userInfo.appearance = null

    println("User info reset")
}

val InitRoast: State = state {
    onEntry {
        /** Set our default interaction parameters */
        resetUserInfo()
        resetRoastStateData()
        resetCounter()
        furhat.gesture(Gestures.OpenEyes)
        users.setSimpleEngagementPolicy(distance = 1.5, maxUsers = 1)
        // furhat.character = "Marty"
        // furhat.voice = ""
        when {
            users.hasAny() -> {
                furhat.attend(users.random)
                goto(Start)
            }
            else -> goto(Start)
        }
    }

}
