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
import android.app.FragmentManager
import android.app.ProgressDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import org.robovm.store.FragmentSwitch
import org.robovm.store.R
import org.robovm.store.api.RoboVMWebService
import org.robovm.store.api.ValidationError
import org.robovm.store.model.Country
import org.robovm.store.model.User
import org.robovm.store.util.Countries

import java.util.ArrayList

class ShippingDetailsFragment @JvmOverloads constructor(private val user: User = User()) : Fragment() {
    private var firstNameField: EditText? = null
    private var lastNameField: EditText? = null
    private var address1Field: EditText? = null
    private var address2Field: EditText? = null
    private var zipCodeField: EditText? = null
    private var cityField: EditText? = null
    private var stateField: AutoCompleteTextView? = null
    private var phoneNumberField: EditText? = null
    private var countryField: AutoCompleteTextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val shippingDetailsView = inflater.inflate(R.layout.shipping_details, container, false)

        val placeOrder = shippingDetailsView.findViewById(R.id.placeOrder) as Button

        phoneNumberField = shippingDetailsView.findViewById(R.id.phone) as EditText
        phoneNumberField!!.setText(user.phone)

        firstNameField = shippingDetailsView.findViewById(R.id.firstName) as EditText
        firstNameField!!.setText(user.firstName)

        lastNameField = shippingDetailsView.findViewById(R.id.lastName) as EditText
        lastNameField!!.setText(user.lastName)

        address1Field = shippingDetailsView.findViewById(R.id.streetAddress1) as EditText
        address1Field!!.setText(user.address1)

        address2Field = shippingDetailsView.findViewById(R.id.streetAddress2) as EditText
        address2Field!!.setText(user.address2)

        cityField = shippingDetailsView.findViewById(R.id.city) as EditText
        cityField!!.setText(user.city)

        stateField = shippingDetailsView.findViewById(R.id.state) as AutoCompleteTextView
        stateField!!.setText(user.state)

        zipCodeField = shippingDetailsView.findViewById(R.id.postalCode) as EditText
        zipCodeField!!.setText(user.zipCode)

        countryField = shippingDetailsView.findViewById(R.id.country) as AutoCompleteTextView
        user.country = if (user.country != null) user.country else "United States"
        countryField!!.setText(user.country)
        countryField!!.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                loadStates()
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
            }
        }

        placeOrder.setOnClickListener { b -> placeOrder() }

        loadCountries()
        loadStates()
        return shippingDetailsView
    }

    private fun loadCountries() {
        val countries = Countries.getCountries()
        val items = ArrayList<String>()
        for (country in countries) {
            items.add(country.name!!)
        }
        countryField!!.setAdapter(ArrayAdapter(activity, android.R.layout.simple_dropdown_item_1line, items))
    }

    private fun loadStates() {
        val country = Countries.getCountryForName(countryField!!.text.toString())
        if (country != null) {
            val states = country.states
            stateField!!.setAdapter(ArrayAdapter(activity, android.R.layout.simple_dropdown_item_1line, states))
        }
    }

    private fun placeOrder() {
        val entries = arrayOf<EditText>(phoneNumberField!!, address1Field!!, address2Field!!, cityField!!, stateField!!, zipCodeField!!, countryField!!)
        for (entry in entries) {
            entry.isEnabled = false
        }

        user.firstName = firstNameField!!.text.toString()
        user.lastName = lastNameField!!.text.toString()
        user.phone = phoneNumberField!!.text.toString()
        user.address1 = address1Field!!.text.toString()
        user.address2 = address2Field!!.text.toString()
        user.city = cityField!!.text.toString()
        user.state = stateField!!.text.toString()
        user.zipCode = zipCodeField!!.text.toString()
        val selectedCountry = Countries.getCountryForName(countryField!!.text.toString())
        if (selectedCountry != null) {
            user.country = selectedCountry.code
        }

        val progressDialog = ProgressDialog.show(activity, "Please wait...", "Placing Order", true)

        RoboVMWebService.instance.placeOrder(user) { response ->
            progressDialog.hide()
            progressDialog.dismiss()
            for (entry in entries) {
                entry.isEnabled = true
            }

            if (response!!.success) {
                RoboVMWebService.instance.basket.clear()

                Toast.makeText(activity, "Your order has been placed!", Toast.LENGTH_LONG).show()

//                TODO
//                fragmentManager.popBackStack(baseFragment, FragmentManager.POP_BACK_STACK_INCLUSIVE)
//                actionBar!!.setDisplayHomeAsUpEnabled(showUp)
                FragmentSwitch().switchScreens(fragmentManager, BragFragment())
            } else {
                val errors = response!!.errors
                var alertMessage = "An unexpected error occurred! Please try again later!"

                if (errors != null) {
                    // We handle only the first error.
                    val error = errors[0]

                    val message = error.message
                    val field = error.field
                    if (field == null) {
                        alertMessage = message
                    } else {
                        when (field) {
                            "firstName" -> alertMessage = "First name is required"
                            "lastName" -> alertMessage = "Last name is required"
                            "address1" -> alertMessage = "Address is required"
                            "city" -> alertMessage = "City is required"
                            "zipCode" -> alertMessage = "ZIP code is required"
                            "phone" -> alertMessage = "Phone number is required"
                            "country" -> alertMessage = "Country is required"
                            else -> alertMessage = message
                        }
                    }
                }
                Toast.makeText(activity, alertMessage, Toast.LENGTH_LONG).show()
            }
        }
    }
}
