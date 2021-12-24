package dev.sebastiano.bundel.onboarding

import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.UiDevice
import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement

internal class PrepareTestStorageRule : TestRule {

    override fun apply(base: Statement, description: Description?): Statement =
        object : Statement() {
            @Throws(Throwable::class)
            override fun evaluate() {
                val uiDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())

                uiDevice.executeShellCommand("log -p i -t TestStorage Enabling test storage permissions...")
                uiDevice.executeShellCommand("appops set androidx.test.services MANAGE_EXTERNAL_STORAGE allow")

                base.evaluate()
            }
        }
}
