package pl.marcinmoskala.store

import android.graphics.Color.WHITE
import android.view.Gravity.CENTER
import android.view.View
import android.widget.Button
import android.widget.TextView
import org.jetbrains.anko.backgroundResource
import org.jetbrains.anko.textColor
import pl.marcinmoskala.store.model.Basket

val basket = Basket()

val API_URL = "https://store-app.robovm.com/test/"
val UNKNOWN_IMAGE_URL = "https://upload.wikimedia.org/wikipedia/en/e/ee/Unknown-person.gif"

val appStyle = { v: View ->
    when (v) {
        is Button -> {
            v.backgroundResource = R.drawable.btn_b
            v.textColor = WHITE
        }
    }
}

val titleStyle = { v: View -> if(v is TextView) {
    v.textColor = 4276545
    v.gravity = CENTER
    v.textSize = 26.toFloat()
}}

val subtitleStyle = { v: View -> if(v is TextView) v.apply {
    textColor = 5329233
    gravity = CENTER
    textSize = 14.toFloat()
}}