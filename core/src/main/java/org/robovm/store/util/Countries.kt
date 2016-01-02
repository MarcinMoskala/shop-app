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
package org.robovm.store.util

import java.io.IOException
import java.nio.charset.Charset

import org.apache.commons.io.IOUtils
import org.robovm.store.model.Country

import com.google.gson.Gson
import com.google.gson.JsonSyntaxException

object Countries {
    private var countries: Array<Country>? = null

    fun getCountries(): Array<Country> {
        if (countries == null) {
            readCountries()
        }
        return countries!!
    }

    private fun readCountries() {
        try {
            val gson = Gson()
            countries = gson.fromJson(
                    IOUtils.toString(Countries::class.java.getResourceAsStream("/countries.json"),
                            Charset.defaultCharset()), Array<Country>::class.java)
        } catch (e: JsonSyntaxException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }

    }

    fun getCountryForCode(code: String): Country? {
        if (countries == null) {
            return null
        }
        for (country in countries!!) {
            if (country.code == code) {
                return country
            }
        }
        return null
    }

    fun getCountryForName(name: String): Country? {
        if (countries == null) {
            return null
        }
        for (country in countries!!) {
            if (country.name == name) {
                return country
            }
        }
        return null
    }
}
