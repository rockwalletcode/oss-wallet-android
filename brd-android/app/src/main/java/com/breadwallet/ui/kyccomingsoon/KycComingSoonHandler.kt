package com.breadwallet.ui.kyccomingsoon

import com.breadwallet.ui.kyccomingsoon.KycComingSoonScreen.F
import drewcarlson.mobius.flow.subtypeEffectHandler

fun createKycComingSoonScreenHandler() = subtypeEffectHandler<F, KycComingSoonScreen.E> {}