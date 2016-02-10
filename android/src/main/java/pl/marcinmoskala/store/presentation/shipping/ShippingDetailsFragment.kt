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

package pl.marcinmoskala.store.presentation.shipping

import android.app.Fragment
import android.app.ProgressDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AutoCompleteTextView
import android.widget.EditText
import android.widget.Toast
import com.jakewharton.rxbinding.widget.RxTextView
import org.jetbrains.anko.*
import pl.marcinmoskala.store.App
import pl.marcinmoskala.store.R
import pl.marcinmoskala.store.api.OrderRequest
import pl.marcinmoskala.store.api.Rest
import pl.marcinmoskala.store.basket
import pl.marcinmoskala.store.model.User
import pl.marcinmoskala.store.presentation.brag.BragFragment
import pl.marcinmoskala.store.util.getSavedString
import pl.marcinmoskala.store.util.pref
import pl.marcinmoskala.store.util.saveString
import pl.marcinmoskala.store.util.switchScreen
import rx.Observable

class ShippingDetailsFragment : Fragment() {
    private val fieldNames = listOf("phone", "firstName", "lastName", "address", "city", "zipCode")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        UI {
            verticalLayout {
                textView("Dane do wysyłki")
                val fieldViews = fieldNames.map { it to editText(getSavedString(it,"")) }
                button(R.id.placeOrder).setOnClickListener { b -> }

                for((key, view) in fieldViews) {
                    RxTextView.textChanges(view)
                            .map {it.toString()}
                            .subscribe { m -> saveString(key, m) }
                }
            }
        }.view
}
