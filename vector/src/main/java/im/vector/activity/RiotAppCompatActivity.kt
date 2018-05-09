/*
 * Copyright 2015 OpenMarket Ltd
 * Copyright 2018 New Vector Ltd
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package im.vector.activity

import android.content.Context
import android.os.Bundle
import android.support.annotation.CallSuper
import android.support.annotation.LayoutRes
import android.support.v7.app.AppCompatActivity
import android.view.View
import androidx.core.view.isVisible
import butterknife.ButterKnife
import butterknife.Unbinder

import im.vector.VectorApp
import org.matrix.androidsdk.util.Log

/**
 * Parent class for all Activities in Vector application
 */
abstract class RiotAppCompatActivity : AppCompatActivity() {

    /** =========================================================================================
     * DATA
     * ========================================================================================== */

    private var unBinder: Unbinder? = null

    private var savedInstanceState: Bundle? = null

    /** =========================================================================================
     * LIFE CYCLE
     * ========================================================================================== */

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(VectorApp.getLocalisedContext(base))
    }

    final override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        doBeforeSetContentView()

        setContentView(getLayoutRes())

        unBinder = ButterKnife.bind(this)

        this.savedInstanceState = savedInstanceState

        initUiAndData()
    }

    override fun onDestroy() {
        super.onDestroy()

        unBinder?.unbind()
        unBinder = null
    }

    @CallSuper
    override fun onResume() {
        super.onResume()

        if (displayInFullscreen()) {
            setFullScreen()
        }

        Log.event(Log.EventTag.NAVIGATION, "onResume Activity " + this.javaClass.simpleName)
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)

        if (hasFocus && displayInFullscreen()) {
            setFullScreen()
        }
    }

    /** =========================================================================================
     * ABSTRACT METHODS
     * ========================================================================================== */

    @LayoutRes
    abstract fun getLayoutRes(): Int

    /** =========================================================================================
     * OPEN METHODS
     * ========================================================================================== */

    open fun displayInFullscreen() = false

    open fun doBeforeSetContentView() = Unit

    open fun initUiAndData() = Unit

    //==============================================================================================
    // Handle loading view (also called waiting view or spinner view)
    //==============================================================================================

    var waitingView: View? = null

    /**
     * Tells if the waiting view is currently displayed
     *
     * @return true if the waiting view is displayed
     */
    fun isWaitingViewVisible() = waitingView?.isVisible == true

    /**
     * Show the waiting view
     */
    fun showWaitingView() {
        waitingView?.isVisible = true
    }

    /**
     * Hide the waiting view
     */
    fun hideWaitingView() {
        waitingView?.isVisible = false
    }

    /** =========================================================================================
     * PROTECTED METHODS
     * ========================================================================================== */

    /**
     * Get the saved instance state.
     * Ensure {@link isFirstCreation()} returns false before calling this
     *
     * @return
     */
    protected fun getSavedInstanceState(): Bundle {
        return savedInstanceState!!
    }

    /**
     * Is first creation
     *
     * @return true if Activity is created for the first time (and not restored by the system)
     */
    protected fun isFirstCreation() = savedInstanceState == null

    /** =========================================================================================
     * PRIVATE METHODS
     * ========================================================================================== */

    /**
     * Force to render the activity in fullscreen
     */
    private fun setFullScreen() {
        window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_FULLSCREEN
                or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
    }
}
