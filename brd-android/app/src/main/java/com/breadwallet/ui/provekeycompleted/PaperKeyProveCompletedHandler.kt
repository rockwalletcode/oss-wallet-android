package com.breadwallet.ui.provekeycompleted

import com.breadwallet.tools.util.EventUtils
import com.breadwallet.ui.provekeycompleted.PaperKeyProveCompleted.E
import com.breadwallet.ui.provekeycompleted.PaperKeyProveCompleted.F
import drewcarlson.mobius.flow.subtypeEffectHandler

fun createProveKeyCompletedScreenHandler() = subtypeEffectHandler<F, E> {
    addConsumer<F.TrackEvent> { (event) ->
        EventUtils.pushEvent(event)
    }
}
