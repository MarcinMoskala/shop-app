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

package pl.marcinmoskala.store.presentation.brag

import android.app.Fragment
import android.content.Intent
import android.content.Intent.ACTION_SEND
import android.content.Intent.EXTRA_TEXT
import android.os.Bundle
import android.view.Gravity.CENTER
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import org.jetbrains.anko.*
import pl.marcinmoskala.store.R
import pl.marcinmoskala.store.appStyle
import pl.marcinmoskala.store.subtitleStyle
import pl.marcinmoskala.store.titleStyle

class BragFragment : Fragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        UI {
            verticalLayout {
                padding = dip(16)

                imageView(R.drawable.complete)
                        .lparams {gravity = CENTER}
                textView(R.string.brag_big)
                        .style ( titleStyle )
                textView(R.string.brag_long)
                        .style ( subtitleStyle )
                button(R.string.brag_to_friends)
                        .setOnClickListener { v -> brag() }
            }.style(appStyle)
        }.view

    private fun brag() {
        val message = ""

        try {
            val intent = Intent(ACTION_SEND)
            intent.setType("text/plain")
            intent.putExtra(EXTRA_TEXT, message)
            startActivity(Intent.createChooser(intent, resources.getString(R.string.brag_on)))
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }
}
