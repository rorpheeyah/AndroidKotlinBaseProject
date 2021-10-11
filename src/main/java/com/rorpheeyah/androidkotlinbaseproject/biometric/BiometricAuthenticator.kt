package com.rorpheeyah.androidkotlinbaseproject.biometric

import android.content.Context
import android.os.Build
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import com.rorp.rorpdevlibs.biometric.*
import com.rorp.rorpdevlibs.biometric.crypto.CryptographyManager
import com.rorp.rorpdevlibs.biometric.crypto.EncryptedData
import com.rorp.rorpdevlibs.biometric.crypto.EncryptionMode
import com.rorpheeyah.androidkotlinbaseproject.R

abstract class BiometricAuthenticator(
    activity: FragmentActivity,
    protected val listener: Listener,
) {

    var showNegativeButton = false
    var isDeviceCredentialAuthenticationEnabled = !showNegativeButton
    var isStrongAuthenticationEnabled = true
    var isWeakAuthenticationEnabled = false
    var showAuthenticationConfirmation = true
    var biometricListener : BiometricListener? = null

    /** Handle using biometrics + cryptography to encrypt/decrypt data securely */
    protected val cryptographyManager = CryptographyManager.instance()
    protected lateinit var encryptionMode: EncryptionMode
    protected lateinit var encryptedData: EncryptedData

    /** Receives callbacks from an authentication operation */
    private val authenticationCallback = object : BiometricPrompt.AuthenticationCallback() {
        override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
            listener.onNewMessage("Authentication succeeded")
            biometricListener?.onAuthenticationSucceeded(result)

            val type = result.authenticationType
            val cryptoObject = result.cryptoObject
            listener.onNewMessage("Type: ${getAuthenticationType(type)} - Crypto: $cryptoObject")

            val cipher = cryptoObject?.cipher ?: return
            when (encryptionMode) {
                EncryptionMode.ENCRYPT -> {
                    encryptedData = cryptographyManager.encrypt(PAYLOAD, cipher)
                    listener.onNewMessage("Encrypted text: ${encryptedData.encrypted}")
                }
                EncryptionMode.DECRYPT -> {
                    val plainData = cryptographyManager.decrypt(encryptedData.encrypted, cipher)
                    listener.onNewMessage("Decrypted text: $plainData")
                }
            }
        }

        override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
            listener.onNewMessage("Authentication error[${getBiometricError(errorCode)}] - $errString")
            biometricListener?.onAuthenticationError(errorCode, errString)
        }

        override fun onAuthenticationFailed() {
            listener.onNewMessage("Authentication failed - Biometric is valid but not recognized")
            biometricListener?.onAuthenticationFailed()
        }
    }

    /** Manages a biometric prompt, and allows to perform an authentication operation */
    protected val biometricPrompt =
        BiometricPrompt(activity, ContextCompat.getMainExecutor(activity), authenticationCallback)

    abstract fun canAuthenticate(context: Context)

    fun authenticateWithoutCrypto(context: Context) {
        val promptInfo = buildPromptInfo(context) ?: return
        biometricPrompt.authenticate(promptInfo)
    }

    abstract fun authenticateAndEncrypt(context: Context)

    abstract fun authenticateAndDecrypt(context: Context)

    abstract fun setAllowedAuthenticators(builder: BiometricPrompt.PromptInfo.Builder)

    fun cancelAuthentication() {
        biometricPrompt.cancelAuthentication()
    }

    /** Build a [PromptInfo] that defines the properties of the biometric prompt dialog. */
    protected fun buildPromptInfo(context: Context): BiometricPrompt.PromptInfo? {
        val builder = BiometricPrompt.PromptInfo.Builder()
            .setTitle(context.getString(R.string.prompt_title))
            .setSubtitle(context.getString(R.string.prompt_subtitle))
            .setDescription(context.getString(R.string.prompt_description))

        // Show a confirmation button after authentication succeeds
        builder.setConfirmationRequired(showAuthenticationConfirmation)

        // Allow authentication with a password, pin or pattern
        setAllowedAuthenticators(builder)

        // Set a negative button. It would typically display "Cancel"
        if (showNegativeButton) {
            builder.setNegativeButtonText(context.getString(R.string.prompt_negative_text))
        }

        return try {
            builder.build()
        } catch (exception: IllegalArgumentException) {
            listener.onNewMessage("Building prompt info error - ${exception.message}")
            null
        }
    }

    interface Listener {
        fun onNewMessage(message: String)
    }

    interface BiometricListener {
        fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult)
        fun onAuthenticationError(errorCode: Int, errString: CharSequence)
        fun onAuthenticationFailed()
    }

    companion object {
        private const val PAYLOAD = "Biometrics sample"

        fun instance(activity: FragmentActivity, listener: Listener): BiometricAuthenticator {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                return BiometricAuthenticatorLegacy(activity, listener)
            }
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
                return BiometricAuthenticatorApi23(activity, listener)
            }
            return BiometricAuthenticatorApi30(activity, listener)
        }
    }
}