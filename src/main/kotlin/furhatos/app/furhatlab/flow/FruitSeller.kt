package furhatos.app.furhatlab.flow.fruitseller

import furhatos.app.furhatlab.flow.Idle
import furhatos.flow.kotlin.*
import furhatos.nlu.EnumEntity
import furhatos.nlu.Intent
import furhatos.nlu.common.No
import furhatos.nlu.common.RequestRepeat
import furhatos.nlu.common.Yes
import furhatos.records.User
import furhatos.util.Language

/**
 * This is the top parent state which all other states inherit
 */
val Interaction: State = state {

    onUserLeave(instant = true) {
        goto(Idle)
    }

    onResponse<RequestRepeat> {
        reentry()
    }

    onResponse {
        furhat.say("I am not sure I understand that")
        reentry()
    }

    onNoResponse {
        furhat.say("I didn't hear anything")
        reentry()
    }

}

/**
 * This is the entry state to the fruit seller
 */
val FruitSellerGreeting: State = state(Interaction) {

    onEntry {
        furhat.say("Hi there!")
        goto(TakingOrder)
    }

    onUserEnter(instant = true) {
        furhat.glance(it)
    }

}

val TakingOrder: State = state(Interaction) {

    onEntry {
        furhat.ask("Would you like to buy some fruit?")
    }

    onResponse<BuyFruit> {
        val fruit = it.intent.fruit!!.value!!
        furhat.say("Alright, $fruit it is.")
        users.current.order.fruits.add(fruit)
        furhat.say("Your current order is ${users.current.order.summarize()}")
        goto(AnythingElse)
    }

    onResponse<Yes> {
        furhat.ask("So, what fruit do you want?")
    }

    onResponse<No> {
        goto(Goodbye)
    }

}

/**
 * Note: we are inheriting all triggers from TakingOrder
 */
val AnythingElse: State = state(TakingOrder) {

    onEntry {
        furhat.ask("Anything else?")
    }

}


val Goodbye: State = state(Interaction) {

    onEntry {
        furhat.say("Alright, it was nice talking to you")
        goto(Idle)
    }

}


/*** NLU: INTENTS AND ENTITIES **/

class BuyFruit(val fruit : Fruit? = null) : Intent() {

    override fun getExamples(lang: Language) = listOf("I would like to buy an orange", "can I have some a banana", "banana")

}

class Fruit : EnumEntity() {
    override fun getEnum(lang: Language) = listOf("banana", "orange", "apple", "pineapple", "pear")

}

/** KEEPING TRACK OF CURRENT ORDER **/

class Order {

    /**
     * Summarize the fruits into a string like "banana, apple and orange"
     */
    fun summarize() = fruits.toList().let { items ->
        when (items.size) {
            0 -> ""
            1 -> items[0]
            2 -> items.joinToString(" and ")
            else -> items.dropLast(1).joinToString(", ") + ", and " + items.last()
        }
    }

    val fruits = mutableSetOf<String>()
}

var User.order by NullSafeUserDataDelegate { Order() }
