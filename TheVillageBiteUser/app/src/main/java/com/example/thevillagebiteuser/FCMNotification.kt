package com.example.thevillagebiteuser

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.ListenerRegistration  // ✅ ADD THIS
import com.google.firebase.firestore.firestore
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class FCMNotification : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        println("FCM Token: $token")
        saveTokenToFirestore(token)
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        val title =
            message.notification?.title
                ?: message.data["title"]
                ?: "The Village Bite"

        val body =
            message.notification?.body
                ?: message.data["body"]
                ?: ""

        showNotification(title, body)
    }

    private fun showNotification(title: String, body: String) {

        val channelId = "village_bite_channel"
        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Village Bite Notifications",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Food order notifications"
                enableLights(true)
                enableVibration(true)
            }
            manager.createNotificationChannel(channel)
        }

        val intent = Intent(this, DashBoardActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(body)
            .setStyle(NotificationCompat.BigTextStyle().bigText(body))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        manager.notify(System.currentTimeMillis().toInt(), notification)
    }

    private fun saveTokenToFirestore(token: String) {

        val uid = Firebase.auth.currentUser?.uid ?: return

        Firebase.firestore
            .collection("users")
            .document(uid)
            .update("fcmToken", token)
            .addOnFailureListener {
                Firebase.firestore
                    .collection("users")
                    .document(uid)
                    .set(
                        mapOf("fcmToken" to token),
                        com.google.firebase.firestore.SetOptions.merge()
                    )
            }
    }
}

// ───────────────────────────────────────────────────────────────
// Notification Helper
// ───────────────────────────────────────────────────────────────

object NotificationHelper {

    private val db   = Firebase.firestore
    private val auth = Firebase.auth

    // ✅ NEW: Listener reference taaki sirf ek baar register ho
    private var notificationListener: ListenerRegistration? = null
    private var isListening = false

    fun sendLoginNotification(context: Context) {

        val uid = auth.currentUser?.uid ?: return

        db.collection("users")
            .document(uid)
            .get()
            .addOnSuccessListener { doc ->
                val name = doc.getString("name") ?: "User"
                sendLocalNotification(
                    context = context,
                    title   = "Welcome Back! 👋",
                    body    = "Hello $name, you have successfully logged in!"
                )
            }
    }

    fun sendPaymentSuccessNotification(
        context: Context,
        amount: Double,
        orderId: String
    ) {
        val shortId = orderId.takeLast(6).uppercase()

        sendLocalNotification(
            context = context,
            title   = "Payment Successful! ✅",
            body    = "Payment of Rs.${"%.2f".format(amount)} completed. Order #$shortId placed successfully!"
        )

        sendNotificationToAdmin(
            title = "New Order Received!",
            body  = "Order #$shortId — Rs.${"%.2f".format(amount)}"
        )
    }

    fun sendOrderStatusNotification(
        context: Context,
        status: String,
        orderId: String
    ) {
        val shortId = orderId.takeLast(6).uppercase()

        val msg = when (status.lowercase()) {
            "confirmed"        -> "Your order has been confirmed!"
            "preparing"        -> "Your food is being prepared..."
            "out for delivery" -> "Your delivery partner is on the way!"
            "delivered"        -> "Your order has been delivered. Enjoy your meal!"
            "cancelled"        -> "Your order has been cancelled."
            else               -> "Order status updated: $status"
        }

        sendLocalNotification(
            context = context,
            title   = "Order Update 🔔",
            body    = "Order #$shortId — $msg"
        )
    }

    fun sendProfileUpdateNotification(context: Context) {
        sendLocalNotification(
            context = context,
            title   = "Profile Updated!",
            body    = "Your profile photo has been updated successfully."
        )
    }

    fun sendLocalNotification(
        context: Context,
        title: String,
        body: String
    ) {
        val channelId = "village_bite_channel"
        val manager   = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Village Bite Notifications",
                NotificationManager.IMPORTANCE_HIGH
            ).apply { enableVibration(true) }
            manager.createNotificationChannel(channel)
        }

        val intent = Intent(context, DashBoardActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent = PendingIntent.getActivity(
            context, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(body)
            .setStyle(NotificationCompat.BigTextStyle().bigText(body))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        manager.notify(System.currentTimeMillis().toInt(), notification)
    }

    private fun sendNotificationToAdmin(title: String, body: String) {
        db.collection("adminNotifications")
            .add(
                mapOf(
                    "title"     to title,
                    "body"      to body,
                    "timestamp" to System.currentTimeMillis(),
                    "read"      to false
                )
            )
    }

    fun refreshAndSaveFCMToken() {
        val uid = Firebase.auth.currentUser?.uid ?: return
        FirebaseMessaging.getInstance().token
            .addOnSuccessListener { token ->
                Firebase.firestore
                    .collection("users")
                    .document(uid)
                    .set(
                        mapOf("fcmToken" to token),
                        com.google.firebase.firestore.SetOptions.merge()
                    )
            }
    }

    // ✅ NEW: Yeh function user app ke DashBoardActivity ya MainActivity mein call karo
    //         Jab bhi admin status update karega, user ko turant notification aayegi
    fun listenForOrderStatusUpdates(context: Context) {

        val uid = auth.currentUser?.uid ?: return
        if (isListening) return   // Sirf ek baar register hoga — duplicate nahi banega
        isListening = true

        val appStartTime = System.currentTimeMillis()
        var isFirstLoad  = true

        notificationListener = db.collection("userNotifications")
            .whereEqualTo("userId", uid)
            .whereEqualTo("read", false)
            .addSnapshotListener { snapshot, error ->

                if (error != null || snapshot == null) return@addSnapshotListener

                // Pehla load skip — ye saare purane unread notifications hain
                if (isFirstLoad) {
                    isFirstLoad = false
                    return@addSnapshotListener
                }

                snapshot.documentChanges.forEach { change ->
                    if (change.type == com.google.firebase.firestore.DocumentChange.Type.ADDED) {

                        val doc       = change.document
                        val title     = doc.getString("title") ?: "Order Update"
                        val body      = doc.getString("body")  ?: ""
                        val timestamp = doc.getLong("timestamp") ?: 0L

                        // Sirf nayi notifications dikhao (app open hone ke baad aaye)
                        if (timestamp >= appStartTime) {
                            sendLocalNotification(context, title, body)

                            // ✅ Notification ko "read" mark karo taaki dobara na aaye
                            doc.reference.update("read", true)
                        }
                    }
                }
            }
    }

    // ✅ App close hone pe listener remove karo
    fun stopListening() {
        notificationListener?.remove()
        notificationListener = null
        isListening = false
    }
}