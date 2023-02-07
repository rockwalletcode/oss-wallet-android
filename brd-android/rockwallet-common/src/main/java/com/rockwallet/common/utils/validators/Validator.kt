package com.rockwallet.common.utils.validators

interface Validator {
    operator fun invoke(input: String): Boolean
}