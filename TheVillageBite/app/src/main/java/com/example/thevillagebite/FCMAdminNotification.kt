package com.example.thevillagebite

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.firestore
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class FCMAdminNotification : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        println("Admin FCM Token: $token")
        saveAdminTokenToFirestore(token)
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        val title = message.notification?.title ?: message.data["title"] ?: "Village Bite Admin"
        val body  = message.notification?.body  ?: message.data["body"]  ?: ""
        showAdminNotification(title, body)
    }

    private fun showAdminNotification(title: String, body: String) {
        val channelId = "admin_village_bite_channel"
        val manager   = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Admin Notifications",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Admin order and payment alerts"
                enableLights(true)
                enableVibration(true)
            }
            manager.createNotificationChannel(channel)
        }

        val intent = Intent(this, MainActivity::class.java).apply {
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

    private fun saveAdminTokenToFirestore(token: String) {
        val uid = Firebase.auth.currentUser?.uid ?: return
        Firebase.firestore.collection("Admin").document(uid)
            .update("fcmToken", token)
            .addOnFailureListener {
                Firebase.firestore.collection("Admin").document(uid)
                    .set(mapOf("fcmToken" to token), com.google.firebase.firestore.SetOptions.merge())
            }
    }
}

object AdminNotificationHelper {

    private val db   = Firebase.firestore
    private val auth = Firebase.auth

    // ✅ FIX: Listener reference aur flag — sirf ek baar register hoga
    private var orderListenerRegistration: ListenerRegistration? = null
    private var isListening = false

    fun sendAdminLoginNotification(context: Context) {
        sendLocalNotification(
            context = context,
            title   = "Admin Login",
            body    = "You have successfully logged in to the admin panel."
        )
        refreshAndSaveAdminFCMToken()
    }

    fun sendNewOrderNotification(context: Context, orderId: String, amount: Double, userName: String) {
        sendLocalNotification(
            context = context,
            title   = "New Order Received!",
            body    = "$userName placed an order of Rs.${"%.2f".format(amount)}. Order #${orderId.takeLast(6).uppercase()}"
        )
    }

    fun sendPaymentReceivedNotification(context: Context, amount: Double, userName: String, orderId: String) {
        sendLocalNotification(
            context = context,
            title   = "Payment Received!",
            body    = "Rs.${"%.2f".format(amount)} received from $userName. Order #${orderId.takeLast(6).uppercase()}"
        )
    }

    fun sendOrderStatusChangedNotification(context: Context, status: String, orderId: String) {
        val shortId = orderId.takeLast(6).uppercase()
        sendLocalNotification(
            context = context,
            title   = "Order Status Updated",
            body    = "Order #$shortId status has been changed to '$status'."
        )
        sendNotificationEntryForUser(orderId, status)
    }

    fun sendAdminProfileUpdateNotification(context: Context) {
        sendLocalNotification(
            context = context,
            title   = "Profile Updated",
            body    = "Admin profile photo updated successfully."
        )
    }

    fun sendLocalNotification(context: Context, title: String, body: String) {
        val channelId = "admin_village_bite_channel"
        val manager   = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId, "Admin Notifications",
                NotificationManager.IMPORTANCE_HIGH
            ).apply { enableVibration(true) }
            manager.createNotificationChannel(channel)
        }

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pending = PendingIntent.getActivity(
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
            .setContentIntent(pending)
            .build()

        manager.notify(System.currentTimeMillis().toInt(), notification)
    }

    private fun sendNotificationEntryForUser(orderId: String, status: String) {
        db.collection("order").document(orderId).get()
            .addOnSuccessListener { doc ->
                val userId  = doc.getString("userId") ?: return@addOnSuccessListener
                val shortId = orderId.takeLast(6).uppercase()
                db.collection("userNotifications").add(
                    mapOf(
                        "userId"    to userId,
                        "title"     to "Order Update",
                        "body"      to "Order #$shortId — Status: $status",
                        "orderId"   to orderId,
                        "timestamp" to System.currentTimeMillis(),
                        "read"      to false
                    )
                )
            }
    }

    fun refreshAndSaveAdminFCMToken() {
        val uid = Firebase.auth.currentUser?.uid ?: return
        FirebaseMessaging.getInstance().token.addOnSuccessListener { token ->
            Firebase.firestore.collection("Admin").document(uid)
                .set(mapOf("fcmToken" to token), com.google.firebase.firestore.SetOptions.merge())
        }
    }

    // ✅ FIX: Sirf ek baar register hoga — baar baar call karne pe duplicate nahi banega
    fun listenForNewOrders(context: Context) {
        if (isListening) return  // Already listen kar raha hai — skip karo
        isListening = true

        val appStartTime = System.currentTimeMillis()
        var isFirstLoad  = true

        orderListenerRegistration = db.collection("order")
            .whereEqualTo("status", "Pending")
            .addSnapshotListener { snapshot, error ->
                if (error != null || snapshot == null) return@addSnapshotListener

                // Pehla load skip — ye saare purane orders hain
                if (isFirstLoad) {
                    isFirstLoad = false
                    return@addSnapshotListener
                }

                snapshot.documentChanges.forEach { change ->
                    if (change.type == com.google.firebase.firestore.DocumentChange.Type.ADDED) {
                        val doc       = change.document
                        val orderId   = doc.id
                        val amount    = doc.getDouble("totalPrice") ?: 0.0
                        val userName  = doc.getString("userName")   ?: "User"
                        val orderTime = doc.getLong("timestamp")    ?: 0L

                        if (orderTime >= appStartTime) {
                            sendNewOrderNotification(context, orderId, amount, userName)
                            sendPaymentReceivedNotification(context, amount, userName, orderId)
                        }
                    }
                }
            }
    }

    // ✅ App close hone pe listener remove karo (optional but good practice)
    fun stopListening() {
        orderListenerRegistration?.remove()
        orderListenerRegistration = null
        isListening = false
    }
}