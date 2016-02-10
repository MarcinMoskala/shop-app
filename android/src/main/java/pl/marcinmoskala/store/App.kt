package pl.marcinmoskala.store

import android.app.Application
import android.content.SharedPreferences
import android.preference.PreferenceManager

import com.securepreferences.SecurePreferences

import java.security.GeneralSecurityException

class App : Application() {
    private var mSecurePrefs: SecurePreferences? = null

    val sharedPreferences: SecurePreferences
        get() {
            if(mSecurePrefs == null) mSecurePrefs = SecurePreferences(this, "", "my_prefs.xml")
            return mSecurePrefs!!
        }

    fun changeUserPrefPassword(newPassword: String): Boolean {
        try {
            sharedPreferences.handlePasswordChange(newPassword, this)
            return true
        } catch (e: GeneralSecurityException) {
            return false
        }
    }
}