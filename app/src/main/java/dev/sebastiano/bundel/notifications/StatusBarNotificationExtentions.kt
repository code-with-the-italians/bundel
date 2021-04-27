package dev.sebastiano.bundel.notifications

import android.app.Notification
import android.service.notification.StatusBarNotification

val StatusBarNotification.text: String
    get() = (this.notification.extras.get(Notification.EXTRA_TEXT) ?: "").toString()

val StatusBarNotification.title: String
    get() = (this.notification.extras.get(Notification.EXTRA_TITLE) ?: "").toString()

val StatusBarNotification.titleBig: String
    get() = (this.notification.extras.get(Notification.EXTRA_TITLE_BIG) ?: "").toString()

val StatusBarNotification.subText: String
    get() = (this.notification.extras.get(Notification.EXTRA_SUB_TEXT) ?: "").toString()
