/**
 * BreadWallet
 *
 * Created by Drew Carlson <drew.carlson@breadwallet.com> on 10/21/20.
 * Copyright (c) 2020 breadwallet LLC
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package brd

private val ciTag = (System.getenv("CI_COMMIT_TAG") ?: System.getenv("CIRCLE_TAG"))
    ?.split("-") // <target>-x.x.x.x
    ?.lastOrNull()
    ?.split(".")
    ?.map(String::toInt)

object BrdRelease {
    /** Major version. Usually affected by marketing. Maximum value: 99 */
    private val marketing = ciTag?.firstOrNull() ?: 5

    /** Minor version. Usually affected by product. Maximum value: 99 */
    private val product = ciTag?.get(1) ?: 1

    /** Hot fix version. Usually affected by engineering. Maximum value: 9 */
    private val engineering = ciTag?.get(2) ?: 1

    /** Build version. Increase for each new build. Maximum value: 999 */
    private val build = ciTag?.lastOrNull() ?: 24

    init {
        check(marketing in 0..99)
        check(product in 0..99)
        check(engineering in 0..9)
        check(build in 0..999)
    }

    // The version code must be monotonically increasing. It is used by Android to maintain upgrade/downgrade
    // relationship between builds with a max value of 2 100 000 000.
    val versionCode = (marketing * 1000000) + (product * 10000) + (engineering * 1000) + build
    val versionName = "$marketing.$product.$engineering"
    val buildVersion = build
    val internalVersionName = "$marketing.$product.$engineering.$build"

    const val ANDROID_TARGET_SDK = 31
    const val ANDROID_COMPILE_SDK = 31
    const val ANDROID_MINIMUM_SDK = 24
    const val ANDROID_BUILD_TOOLS = "30.0.2"
}
