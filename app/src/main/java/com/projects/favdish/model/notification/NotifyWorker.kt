package com.projects.favdish.model.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.projects.favdish.R
import com.projects.favdish.utils.Constants
import com.projects.favdish.view.activities.MainActivity

class NotifyWorker(context: Context, workerParams : WorkerParameters) : Worker(context, workerParams) {
    override fun doWork(): Result {
        sendNotification()
        return Result.success()
    }

    private fun sendNotification(){
        // TODO Add the notification id.
        val notification_id = 0

        // TODO Create an intent instance that we want to navigate the user when it is clicked.
        val intent = Intent(applicationContext, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        intent.putExtra(Constants.NOTIFICATION_ID, notification_id)

        // TODO Create an instance of Notification Manager.
        /**
         * Class to notify the user of events that happen.  This is how you tell
         * the user that something has happened in the background.
         */
        val notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // TODO Define the Notification Title and SubTitle.
        val titleNotification = applicationContext.getString(R.string.notification_tittle)
        val subtitleNotification = applicationContext.getString(R.string.notification_subtitle)

        // TODO Generate the bitmap from vector icon using the function that we have created.
        val bitmap = applicationContext.vectorToBitmap(R.drawable.ic_vector_logo)

        // TODO Create the style of the Notification. You can create the style as you want here we will create a notification using BigPicture. For Example InboxStyle() which is used for simple Text message.
        val bigPicStyle = NotificationCompat.BigPictureStyle().bigPicture(bitmap).bigLargeIcon(null)

        // TODO Define the pending intent for Notification.
        val pendingIntent = PendingIntent.getActivities(applicationContext, 0, arrayOf(intent), 0)

        // TODO Before building the Notification Builder add the notification icon. You can have look the note file where I have mentioned the step How to generate it.
        // TODO Now as we most of the required params so lets build the Notification Builder.
        // START
        val notification = NotificationCompat.Builder(applicationContext, Constants.NOTIFICATION_CHANNEL).setContentTitle(titleNotification).setContentText(subtitleNotification).setSmallIcon(R.drawable.ic_launcher_foreground)
            .setLargeIcon(bitmap).setDefaults(NotificationCompat.DEFAULT_ALL).setContentIntent(pendingIntent).setStyle(bigPicStyle).setAutoCancel(true)

        notification.priority = NotificationCompat.PRIORITY_MAX

        // TODO Step 16: Set channel ID for Notification if you are using the API level 26 or higher.
        // START
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notification.setChannelId(Constants.NOTIFICATION_CHANNEL)

            // Setup the Ringtone for Notification.
            val ringtoneManager = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            val audioAttributes = AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_NOTIFICATION_RINGTONE).setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION).build()
            val channel = NotificationChannel(Constants.NOTIFICATION_CHANNEL, Constants.NOTIFICATION_NAME, NotificationManager.IMPORTANCE_HIGH)

            channel.enableLights(true)
            channel.lightColor = Color.RED
            channel.enableVibration(true)
            channel.vibrationPattern = longArrayOf(100, 200, 300, 400, 500, 400, 300, 200, 400)
            channel.setSound(ringtoneManager, audioAttributes)
            notificationManager.createNotificationChannel(channel)
        }

        // TODO Notify the user with Notification id and Notification builder using the NotificationManager instance that we have created.
        notificationManager.notify(notification_id, notification.build())
    }

    /**
     * A function that will convert the vector image to bitmap as below.
     */
    private fun Context.vectorToBitmap(drawableId : Int) : Bitmap? {
        //Get the drawable vector image
        val drawable = ContextCompat.getDrawable(this, drawableId) ?: return null
        val bitmap = Bitmap.createBitmap(drawable.intrinsicWidth, drawable.intrinsicHeight, Bitmap.Config.ARGB_8888) ?: return null
        val canvas = Canvas(bitmap)
        drawable.setBounds(0,0, canvas.width, canvas.height)
        drawable.draw(canvas)
        return bitmap
    }

}