package pl.perski.eattogether.utils

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager

class SharedPrefHelper(context: Context) {

    companion object {
        const val TOKEN = "token"
    }

    private val preferences: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)

    var token = preferences.getString(TOKEN, "")
        set(value) = preferences.edit().putString(TOKEN, value).apply()

    fun checkIfExists(name: String): Boolean {
        return preferences.contains(name)
    }

    fun clearAll() {
        preferences.edit().clear().apply()
    }
}