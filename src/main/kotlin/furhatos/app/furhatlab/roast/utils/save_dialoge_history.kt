package furhatos.app.furhatlab.roast.utils

import furhatos.app.furhatlab.roast.userInfo
import furhatos.flow.kotlin.DialogHistory
import furhatos.flow.kotlin.Furhat

fun saveDialogHistory() {
    val messages = mutableListOf<String>()
    val fileName = userInfo.name + "_" + System.currentTimeMillis() + ".txt"
    Furhat.dialogHistory.all.forEach {
        when (it) {
            is DialogHistory.ResponseItem -> {
                messages.add("User: " + it.response.text)
            }
            is DialogHistory.UtteranceItem -> {
                messages.add("Furhat: " + it.toText())
            }
        }
    }
    // Persist the captured dialog to a text file named with the generated fileName
    java.io.File("saved_roasts/dialog_history/" + fileName).printWriter().use { writer ->
        messages.forEach { message ->
            writer.println(message)
        }
    }
    println("Dialog history saved to $fileName")
}
