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

package pl.marcinmoskala.store.presentation.login

import android.app.Fragment
import android.app.ProgressDialog
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.TypedValue
import android.util.TypedValue.COMPLEX_UNIT_DIP
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import butterknife.bindView
import pl.marcinmoskala.store.R
import pl.marcinmoskala.store.model.User
import pl.marcinmoskala.store.presentation.shipping.ShippingDetailsFragment
import pl.marcinmoskala.store.util.Gravatar
import pl.marcinmoskala.store.util.switchScreen
import pl.marcinmoskala.store.views.CircleDrawable

class LoginFragment : Fragment() {
    private val password: EditText by bindView(R.id.password)
    private val login: Button by bindView(R.id.signInBtn)
    private val imageView: ImageView by bindView(R.id.imageView1)
    private val email: EditText by bindView(R.id.email)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.login_screen, null)

    override fun onStart() {
        super.onStart()
        login.setOnClickListener { b -> login(email.text.toString(), password.text.toString()) }
    }

    private fun loadUserImage() {
        val px = TypedValue.applyDimension(COMPLEX_UNIT_DIP, 85f, activity.resources.displayMetrics).toInt()
        Gravatar.instance.getImageBytes("marcinmoskala@gmail.com", px, Gravatar.Rating.PG) { bytes ->
            val image = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
            imageView.setImageDrawable(CircleDrawable(image))
        }
    }

    private fun login(username: String, password: String) {
        val progressDialog = ProgressDialog.show(activity, "Please wait...", "Logging in", true)
        this.login.isEnabled = false
        this.password.isEnabled = false
        switchScreen(ShippingDetailsFragment())
        this.password.isEnabled = true
        progressDialog.hide()
        progressDialog.dismiss()
    }
}
