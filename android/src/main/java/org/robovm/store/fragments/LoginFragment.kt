/*
 * Copyright (C) 2013-2015 RoboVM AB
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 *   
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License. 
 */

package org.robovm.store.fragments

import android.app.Fragment
import android.app.ProgressDialog
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.text.Html
import android.text.Spanned
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import org.robovm.store.R
import org.robovm.store.api.RoboVMWebService
import org.robovm.store.util.Gravatar
import org.robovm.store.views.CircleDrawable

class LoginFragment : Fragment() {

    private var loginSuccessListener: Runnable? = null

    private var password: EditText? = null
    private var login: Button? = null
    private var imageView: ImageView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return createLoginView(inflater, container!!, savedInstanceState)
    }

    private fun createLoginView(inflater: LayoutInflater, container: ViewGroup, savedInstanceState: Bundle?): View {
        val view = inflater.inflate(R.layout.login_screen, null)

        imageView = view.findViewById(R.id.imageView1) as ImageView
        loadUserImage()

        val textView = view.findViewById(R.id.email) as EditText
        textView.isEnabled = false
        textView.setText(ROBOVM_ACCOUNT_EMAIL)

        password = view.findViewById(R.id.password) as EditText
        login = view.findViewById(R.id.signInBtn) as Button
        login!!.setOnClickListener { b -> login(ROBOVM_ACCOUNT_EMAIL, password!!.text.toString()) }

        return view
    }

    private fun loadUserImage() {
        val px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 85f, activity.resources.displayMetrics).toInt()
        Gravatar.instance.getImageBytes(ROBOVM_ACCOUNT_EMAIL, px, Gravatar.Rating.PG) { bytes ->
            if (bytes != null) {
                val image = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                imageView!!.setImageDrawable(CircleDrawable(image))
            }
        }
    }

    fun setLoginSuccessListener(loginSuccessListener: Runnable) {
        this.loginSuccessListener = loginSuccessListener
    }

    private fun login(username: String, password: String) {
        val progressDialog = ProgressDialog.show(activity, "Please wait...", "Logging in", true)
        this.login!!.isEnabled = false
        this.password!!.isEnabled = false

        RoboVMWebService.instance.authenticate(username, password) { success ->
            if (success!! && loginSuccessListener != null) {
                loginSuccessListener!!.run()
            } else {
                Toast.makeText(activity, "Please verify your RoboVM account credentials and try again",
                        Toast.LENGTH_LONG).show()
            }

            this.login!!.isEnabled = true
            this.password!!.isEnabled = true
            progressDialog.hide()
            progressDialog.dismiss()
        }
    }

    companion object {
        private val ROBOVM_ACCOUNT_EMAIL = "marcinmoskala@gmail.com"
    }
}
