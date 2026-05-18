package com.example.nammapridechannapatna

import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.InputType
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity

class LoginActivity : ComponentActivity() {

    private lateinit var tabVendor: TextView
    private lateinit var tabArtisan: TextView
    private lateinit var layoutName: LinearLayout
    private lateinit var editName: EditText
    private lateinit var editEmail: EditText
    private lateinit var editPassword: EditText
    private lateinit var btnEyeToggle: TextView
    private lateinit var btnAction: Button
    private lateinit var txtScreenTitle: TextView
    private lateinit var txtScreenSubtitle: TextView
    private lateinit var txtSwitchMessage: TextView
    private lateinit var txtSwitchMode: TextView
    private lateinit var txtForgotPassword: TextView

    private var isVendorSelected = true
    private var isLoginMode = true
    private var isPasswordVisible = false

    private lateinit var userStorage: UserStorage

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        // ✅ Instant transition
        overridePendingTransition(0, 0)

        userStorage = UserStorage(this)

        // ✅ Auto Login if already logged in
        if (userStorage.isLoggedIn()) {
            val savedUserType = userStorage.getCurrentUserType()
            navigateToDashboard(savedUserType)
            return
        }

        // Connect Views
        tabVendor = findViewById(R.id.tabVendor)
        tabArtisan = findViewById(R.id.tabArtisan)
        layoutName = findViewById(R.id.layoutName)
        editName = findViewById(R.id.editName)
        editEmail = findViewById(R.id.editEmail)
        editPassword = findViewById(R.id.editPassword)
        btnEyeToggle = findViewById(R.id.btnEyeToggle)
        btnAction = findViewById(R.id.btnAction)
        txtScreenTitle = findViewById(R.id.txtScreenTitle)
        txtScreenSubtitle = findViewById(R.id.txtScreenSubtitle)
        txtSwitchMessage = findViewById(R.id.txtSwitchMessage)
        txtSwitchMode = findViewById(R.id.txtSwitchMode)
        txtForgotPassword = findViewById(R.id.txtForgotPassword)

        showLoginMode()
        selectVendor()

        tabVendor.setOnClickListener { selectVendor() }
        tabArtisan.setOnClickListener { selectArtisan() }

        btnEyeToggle.setOnClickListener {
            togglePasswordVisibility(editPassword, btnEyeToggle)
        }

        btnAction.setOnClickListener {
            if (isLoginMode) {
                handleLogin()
            } else {
                handleRegister()
            }
        }

        txtSwitchMode.setOnClickListener {
            if (isLoginMode) {
                showRegisterMode()
            } else {
                showLoginMode()
            }
        }

        txtForgotPassword.setOnClickListener { handleForgotPassword() }
    }

    // ================================
    // ✅ Navigate to Dashboard based on User Type
    // ================================
    private fun navigateToDashboard(userType: String) {
        when (userType) {
            "Vendor" -> {
                val intent = Intent(this, VendorDashboardActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or
                        Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
                overridePendingTransition(0, 0)
                finish()
            }
            "Artisan" -> {
                // ✅ Navigate to Artisan Dashboard
                val intent = Intent(this, ArtisanDashboardActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or
                        Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
                overridePendingTransition(0, 0)
                finish()
            }
            else -> {
                Toast.makeText(this,
                    "Unknown user type",
                    Toast.LENGTH_SHORT).show()
            }
        }
    }

    // ================================
    // ✅ Toggle Password Visibility
    // ================================
    private fun togglePasswordVisibility(editText: EditText, eyeButton: TextView) {
        isPasswordVisible = !isPasswordVisible

        if (isPasswordVisible) {
            editText.inputType = InputType.TYPE_CLASS_TEXT or
                    InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
            eyeButton.text = "🙈"
        } else {
            editText.inputType = InputType.TYPE_CLASS_TEXT or
                    InputType.TYPE_TEXT_VARIATION_PASSWORD
            eyeButton.text = "👁"
        }

        editText.setSelection(editText.text.length)
    }

    // ================================
    // ✅ Show LOGIN Mode
    // ================================
    private fun showLoginMode() {
        isLoginMode = true
        layoutName.visibility = View.GONE
        txtScreenTitle.text = "Welcome Back!"
        txtScreenSubtitle.text = "Login to continue"
        btnAction.text = "LOGIN"
        txtSwitchMessage.text = "New user? "
        txtSwitchMode.text = "Register Here"
        txtForgotPassword.visibility = View.VISIBLE

        editEmail.setText("")
        editPassword.setText("")
    }

    // ================================
    // ✅ Show REGISTER Mode
    // ================================
    private fun showRegisterMode() {
        isLoginMode = false
        layoutName.visibility = View.VISIBLE
        txtScreenTitle.text = "Create Account"
        txtScreenSubtitle.text = "Register to get started"
        btnAction.text = "REGISTER"
        txtSwitchMessage.text = "Already registered? "
        txtSwitchMode.text = "Login Here"
        txtForgotPassword.visibility = View.GONE

        editName.setText("")
        editEmail.setText("")
        editPassword.setText("")
    }

    // ================================
    // ✅ Select Vendor Tab
    // ================================
    private fun selectVendor() {
        isVendorSelected = true
        tabVendor.setBackgroundColor(Color.parseColor("#8D4E2A"))
        tabVendor.setTextColor(Color.WHITE)
        tabArtisan.setBackgroundColor(Color.parseColor("#FFE0B2"))
        tabArtisan.setTextColor(Color.parseColor("#5D4037"))
    }

    // ================================
    // ✅ Select Artisan Tab
    // ================================
    private fun selectArtisan() {
        isVendorSelected = false
        tabArtisan.setBackgroundColor(Color.parseColor("#8D4E2A"))
        tabArtisan.setTextColor(Color.WHITE)
        tabVendor.setBackgroundColor(Color.parseColor("#FFE0B2"))
        tabVendor.setTextColor(Color.parseColor("#5D4037"))
    }

    // ================================
    // ✅ Show Validation Errors
    // ================================
    private fun showErrorDialog(title: String, errors: List<String>) {
        val message = errors.joinToString("\n")

        AlertDialog.Builder(this)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()
            }
            .setCancelable(false)
            .show()
    }

    // ================================
    // ✅ Handle REGISTER
    // ================================
    private fun handleRegister() {
        val name = editName.text.toString().trim()
        val email = editEmail.text.toString().trim().lowercase()
        val password = editPassword.text.toString().trim()

        val errors = mutableListOf<String>()

        if (name.isEmpty()) {
            errors.add("• Name cannot be empty")
        }

        if (email.isEmpty()) {
            errors.add("• Email cannot be empty")
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            errors.add("• Please enter a valid email address")
        }

        if (password.isEmpty()) {
            errors.add("• Password cannot be empty")
        } else {
            val passwordErrors = userStorage.validatePassword(password)
            errors.addAll(passwordErrors)
        }

        if (errors.isNotEmpty()) {
            showErrorDialog("Registration Failed", errors)
            return
        }

        val userType = if (isVendorSelected) "Vendor" else "Artisan"
        val success = userStorage.registerUser(name, email, password, userType)

        if (success) {
            val roleEmoji = if (userType == "Vendor") "🛒" else "🎨"
            showToast("✅ $roleEmoji $userType Registered Successfully!\nPlease login now")
            showLoginMode()
            editEmail.setText(email)
        } else {
            showErrorDialog(
                "Registration Failed",
                listOf("• This email is already registered", "• Please use Login instead")
            )
        }
    }

    // ================================
    // ✅ Handle LOGIN - WITH NAVIGATION
    // ================================
    private fun handleLogin() {
        val email = editEmail.text.toString().trim().lowercase()
        val password = editPassword.text.toString().trim()

        val errors = mutableListOf<String>()

        if (email.isEmpty()) {
            errors.add("• Email cannot be empty")
        }

        if (password.isEmpty()) {
            errors.add("• Password cannot be empty")
        }

        if (errors.isNotEmpty()) {
            showErrorDialog("Login Failed", errors)
            return
        }

        val userType = userStorage.loginUser(email, password)

        if (userType != null) {
            val name = userStorage.getCurrentUserName()
            val roleEmoji = if (userType == "Vendor") "🛒" else "🎨"
            showToast("✅ $roleEmoji Welcome back $name!")

            // ✅ Navigate based on user type
            navigateToDashboard(userType)
        } else {
            showErrorDialog(
                "Login Failed",
                listOf("• Invalid email or password", "• Please check and try again")
            )
        }
    }

    // ================================
    // ✅ Handle FORGOT PASSWORD
    // ================================
    private fun handleForgotPassword() {
        val email = editEmail.text.toString().trim().lowercase()

        if (email.isEmpty()) {
            showErrorDialog(
                "Email Required",
                listOf("• Please enter your email first", "• Then click Forgot Password")
            )
            return
        }

        if (!userStorage.isUserExists(email)) {
            showErrorDialog(
                "Email Not Found",
                listOf("• This email is not registered", "• Please register first")
            )
            return
        }

        showResetPasswordDialog(email)
    }

    // ================================
    // ✅ Reset Password Dialog with Eye Button
    // ================================
    private fun showResetPasswordDialog(email: String) {
        val dialogLayout = LinearLayout(this)
        dialogLayout.orientation = LinearLayout.VERTICAL
        dialogLayout.setPadding(50, 30, 50, 10)

        // ===== New Password Row =====
        val newPasswordRow = LinearLayout(this)
        newPasswordRow.orientation = LinearLayout.HORIZONTAL
        newPasswordRow.gravity = android.view.Gravity.CENTER_VERTICAL

        val newPasswordInput = EditText(this)
        newPasswordInput.hint = "Enter new password (8 chars)"
        newPasswordInput.inputType = InputType.TYPE_CLASS_TEXT or
                InputType.TYPE_TEXT_VARIATION_PASSWORD
        newPasswordInput.filters = arrayOf(android.text.InputFilter.LengthFilter(8))
        val newPassParams = LinearLayout.LayoutParams(0,
            LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
        newPasswordInput.layoutParams = newPassParams
        newPasswordRow.addView(newPasswordInput)

        val newPassEye = TextView(this)
        newPassEye.text = "👁"
        newPassEye.textSize = 22f
        newPassEye.setPadding(20, 0, 0, 0)
        var newPassVisible = false
        newPassEye.setOnClickListener {
            newPassVisible = !newPassVisible
            if (newPassVisible) {
                newPasswordInput.inputType = InputType.TYPE_CLASS_TEXT or
                        InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                newPassEye.text = "🙈"
            } else {
                newPasswordInput.inputType = InputType.TYPE_CLASS_TEXT or
                        InputType.TYPE_TEXT_VARIATION_PASSWORD
                newPassEye.text = "👁"
            }
            newPasswordInput.setSelection(newPasswordInput.text.length)
        }
        newPasswordRow.addView(newPassEye)
        dialogLayout.addView(newPasswordRow)

        // ===== Confirm Password Row =====
        val confirmPasswordRow = LinearLayout(this)
        confirmPasswordRow.orientation = LinearLayout.HORIZONTAL
        confirmPasswordRow.gravity = android.view.Gravity.CENTER_VERTICAL
        val rowParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        rowParams.topMargin = 20
        confirmPasswordRow.layoutParams = rowParams

        val confirmPasswordInput = EditText(this)
        confirmPasswordInput.hint = "Confirm new password"
        confirmPasswordInput.inputType = InputType.TYPE_CLASS_TEXT or
                InputType.TYPE_TEXT_VARIATION_PASSWORD
        confirmPasswordInput.filters = arrayOf(android.text.InputFilter.LengthFilter(8))
        val confirmParams = LinearLayout.LayoutParams(0,
            LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
        confirmPasswordInput.layoutParams = confirmParams
        confirmPasswordRow.addView(confirmPasswordInput)

        val confirmEye = TextView(this)
        confirmEye.text = "👁"
        confirmEye.textSize = 22f
        confirmEye.setPadding(20, 0, 0, 0)
        var confirmVisible = false
        confirmEye.setOnClickListener {
            confirmVisible = !confirmVisible
            if (confirmVisible) {
                confirmPasswordInput.inputType = InputType.TYPE_CLASS_TEXT or
                        InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                confirmEye.text = "🙈"
            } else {
                confirmPasswordInput.inputType = InputType.TYPE_CLASS_TEXT or
                        InputType.TYPE_TEXT_VARIATION_PASSWORD
                confirmEye.text = "👁"
            }
            confirmPasswordInput.setSelection(confirmPasswordInput.text.length)
        }
        confirmPasswordRow.addView(confirmEye)
        dialogLayout.addView(confirmPasswordRow)

        // Hint
        val hintText = TextView(this)
        hintText.text = "Must be 8 characters with letters, numbers and special chars"
        hintText.textSize = 11f
        hintText.setTextColor(Color.parseColor("#8D6E63"))
        val hintParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        hintParams.topMargin = 15
        hintText.layoutParams = hintParams
        dialogLayout.addView(hintText)

        // Build Dialog WITHOUT auto dismiss
        val dialog = AlertDialog.Builder(this)
            .setTitle("Reset Password")
            .setMessage("Email: $email")
            .setView(dialogLayout)
            .setPositiveButton("Reset", null)
            .setNegativeButton("Cancel") { d, _ ->
                d.dismiss()
            }
            .setCancelable(false)
            .create()

        dialog.show()

        // Override Reset button
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
            val newPassword = newPasswordInput.text.toString().trim()
            val confirmPassword = confirmPasswordInput.text.toString().trim()

            val success = processPasswordReset(email, newPassword, confirmPassword)

            if (success) {
                dialog.dismiss()
            }
        }
    }

    // ================================
    // ✅ Process Password Reset
    // ================================
    private fun processPasswordReset(
        email: String,
        newPassword: String,
        confirmPassword: String
    ): Boolean {
        val errors = mutableListOf<String>()

        if (newPassword.isEmpty()) {
            errors.add("• New password cannot be empty")
        }

        if (confirmPassword.isEmpty()) {
            errors.add("• Confirm password cannot be empty")
        }

        if (newPassword.isNotEmpty() && confirmPassword.isNotEmpty()) {
            if (newPassword != confirmPassword) {
                errors.add("• Passwords do not match")
            }
        }

        if (newPassword.isNotEmpty()) {
            val passwordErrors = userStorage.validatePassword(newPassword)
            errors.addAll(passwordErrors)
        }

        if (errors.isNotEmpty()) {
            showErrorDialog("Password Reset Failed", errors)
            return false
        }

        val success = userStorage.resetPassword(email, newPassword)

        if (success) {
            showToast("✅ Password reset successful! Please login")
            editEmail.setText(email)
            editPassword.setText("")
            return true
        } else {
            showErrorDialog(
                "Reset Failed",
                listOf("• Could not reset password", "• Please try again")
            )
            return false
        }
    }

    // ================================
    // ✅ Toast Helper
    // ================================
    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    // ================================
    // ✅ Override Back Press for Instant Transition
    // ================================
    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(0, 0)
    }
}