package com.rfz.appflotal.data

import android.app.Activity
import com.google.android.ump.ConsentDebugSettings
import com.google.android.ump.ConsentRequestParameters
import com.google.android.ump.UserMessagingPlatform
import javax.inject.Singleton

class ConsentManager(
    private val activity: Activity
) {

    fun requestConsent(onFinished: () -> Unit) {
        val consentInformation =
            UserMessagingPlatform.getConsentInformation(activity)

        val params = ConsentRequestParameters.Builder()
            .setTagForUnderAgeOfConsent(false)
            .build()

        consentInformation.requestConsentInfoUpdate(
            activity,
            params,
            {
                if (consentInformation.isConsentFormAvailable) {
                    UserMessagingPlatform.loadConsentForm(
                        activity,
                        { form ->
                            form.show(activity) { onFinished() }
                        },
                        { onFinished() }
                    )
                } else {
                    onFinished()
                }
            },
            { onFinished() }
        )
    }
}