package furhatos.app.furhatlab

import furhatos.app.furhatlab.roast.InitRoast
import furhatos.flow.kotlin.Flow
import furhatos.skills.Skill

class FurhatlabSkill : Skill() {
    override fun start() {
        Flow().run(InitRoast)
    }
}

fun main(args: Array<String>) {
    Skill.main(args)
}
