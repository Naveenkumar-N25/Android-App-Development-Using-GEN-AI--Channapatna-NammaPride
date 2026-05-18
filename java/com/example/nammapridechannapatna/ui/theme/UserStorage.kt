package com.example.nammapridechannapatna

import android.content.Context
import android.content.SharedPreferences

class UserStorage(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("ChannapatnaUsers", Context.MODE_PRIVATE)

    fun registerUser(
        name: String,
        email: String,
        password: String,
        userType: String
    ): Boolean {
        val emailLower = email.lowercase().trim()
        if (isUserExists(emailLower)) return false

        val allUsers = prefs.getString("all_users", "") ?: ""
        val newUser = "$emailLower|$name|$password|$userType;"
        prefs.edit().putString("all_users", allUsers + newUser).apply()
        return true
    }

    fun loginUser(email: String, password: String): String? {
        val emailLower = email.lowercase().trim()
        val allUsers = prefs.getString("all_users", "") ?: ""
        if (allUsers.isEmpty()) return null

        val users = allUsers.split(";")
        for (user in users) {
            if (user.isEmpty()) continue
            val parts = user.split("|")
            if (parts.size >= 4) {
                if (parts[0] == emailLower && parts[2] == password) {
                    saveCurrentUser(parts[1], parts[0], parts[3])
                    return parts[3]
                }
            }
        }
        return null
    }

    fun isUserExists(email: String): Boolean {
        val emailLower = email.lowercase().trim()
        val allUsers = prefs.getString("all_users", "") ?: ""
        if (allUsers.isEmpty()) return false
        return allUsers.split(";").any {
            it.isNotEmpty() && it.split("|").firstOrNull() == emailLower
        }
    }

    private fun saveCurrentUser(name: String, email: String, userType: String) {
        prefs.edit().apply {
            putString("current_name", name)
            putString("current_email", email)
            putString("current_userType", userType)
            putBoolean("is_logged_in", true)
            apply()
        }
    }

    fun getCurrentUserName(): String =
        prefs.getString("current_name", "Guest") ?: "Guest"

    fun getCurrentUserEmail(): String =
        prefs.getString("current_email", "") ?: ""

    fun getCurrentUserType(): String =
        prefs.getString("current_userType", "") ?: ""

    fun isLoggedIn(): Boolean =
        prefs.getBoolean("is_logged_in", false)

    fun logout() {
        prefs.edit().apply {
            remove("current_name")
            remove("current_email")
            remove("current_userType")
            putBoolean("is_logged_in", false)
            apply()
        }
    }

    fun resetPassword(email: String, newPassword: String): Boolean {
        val emailLower = email.lowercase().trim()
        val allUsers = prefs.getString("all_users", "") ?: ""
        if (allUsers.isEmpty()) return false

        val users = allUsers.split(";").filter { it.isNotEmpty() }.toMutableList()
        var updated = false

        for (i in users.indices) {
            val parts = users[i].split("|")
            if (parts.size >= 4 && parts[0] == emailLower) {
                users[i] = "$emailLower|${parts[1]}|$newPassword|${parts[3]}"
                updated = true
                break
            }
        }

        if (updated) {
            prefs.edit().putString("all_users", users.joinToString(";") + ";").apply()
            return true
        }
        return false
    }

    fun validatePassword(password: String): List<String> {
        val errors = mutableListOf<String>()
        if (password.length != 8) errors.add("• Password must be exactly 8 characters")
        if (!password.any { it.isLetter() }) errors.add("• Must contain at least 1 letter")
        if (!password.any { it.isDigit() }) errors.add("• Must contain at least 1 number")
        val specialChars = "!@#\$%^&*()_+-=[]{}|;:,.<>?/~`"
        if (!password.any { it in specialChars }) errors.add("• Must contain at least 1 special character")
        return errors
    }

    // ================================
    // ✅ PROFILE SETTINGS
    // ================================
    fun saveProfile(dob: String, address: String, phone: String) {
        prefs.edit().apply {
            putString("profile_dob", dob)
            putString("profile_address", address)
            putString("profile_phone", phone)
            apply()
        }
    }

    fun getProfileDob(): String = prefs.getString("profile_dob", "") ?: ""
    fun getProfileAddress(): String = prefs.getString("profile_address", "") ?: ""
    fun getProfilePhone(): String = prefs.getString("profile_phone", "") ?: ""

    fun updateUserName(newName: String) {
        prefs.edit().putString("current_name", newName).apply()
    }

    fun getTotalUsers(): Int {
        val allUsers = prefs.getString("all_users", "") ?: ""
        if (allUsers.isEmpty()) return 0
        return allUsers.split(";").filter { it.isNotEmpty() }.size
    }
}