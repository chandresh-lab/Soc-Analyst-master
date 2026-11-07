package com.example.data.repository

import android.content.Context
import android.os.Build
import android.util.Log
import com.example.R
import com.example.data.model.ExportAuditLog
import com.example.data.model.SubscriptionPlan
import com.example.data.model.UserProfile
import com.example.data.model.UserRole
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object FirebaseSyncManager {
    private const val TAG = "FirebaseSync"

    private var firestoreDb: FirebaseFirestore? = null

    private val _cloudUsers = MutableStateFlow<List<UserProfile>>(emptyList())
    val cloudUsers: StateFlow<List<UserProfile>> = _cloudUsers.asStateFlow()

    private val _exportLogs = MutableStateFlow<List<ExportAuditLog>>(emptyList())
    val exportLogs: StateFlow<List<ExportAuditLog>> = _exportLogs.asStateFlow()

    private val _isCloudConnected = MutableStateFlow(false)
    val isCloudConnected: StateFlow<Boolean> = _isCloudConnected.asStateFlow()

    fun initialize(context: Context) {
        if (firestoreDb != null) return

        try {
            val dbId = context.getString(R.string.firestore_database_id)
            firestoreDb = FirebaseFirestore.getInstance(dbId)
            _isCloudConnected.value = true
            Log.d(TAG, "Initialized Firestore with DB: $dbId")

            // Real-time listener for All Users & Devices (For Super Admin Table)
            firestoreDb?.collection("users")?.addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.w(TAG, "Listen failed on users collection: ${error.message}")
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    val list = mutableListOf<UserProfile>()
                    for (doc in snapshot.documents) {
                        try {
                            val user = doc.toObject(UserProfile::class.java)
                            if (user != null) {
                                list.add(user)
                            }
                        } catch (e: Exception) {
                            Log.w(TAG, "Error mapping user document: ${e.message}")
                        }
                    }
                    if (list.isNotEmpty()) {
                        _cloudUsers.value = list
                        // Merge with SocRepository registered users
                        SocRepository.mergeCloudUsers(list)
                    }
                }
            }

            // Real-time listener for Export Logs
            firestoreDb?.collection("export_logs")?.addSnapshotListener { snapshot, error ->
                if (error == null && snapshot != null) {
                    val logs = snapshot.toObjects(ExportAuditLog::class.java)
                    _exportLogs.value = logs.sortedByDescending { it.timestamp }
                }
            }

        } catch (e: Exception) {
            Log.e(TAG, "Error initializing FirebaseFirestore: ${e.message}", e)
            _isCloudConnected.value = false
        }
    }

    fun getDeviceName(): String {
        val manufacturer = Build.MANUFACTURER.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.ROOT) else it.toString() }
        val model = Build.MODEL
        return if (model.startsWith(manufacturer)) model else "$manufacturer $model"
    }

    fun getAndroidVersion(): String {
        return "Android ${Build.VERSION.RELEASE} (API ${Build.VERSION.SDK_INT})"
    }

    fun getCurrentTimestamp(): String {
        return SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
    }

    // Auto-Sync User data to Cloud Firestore
    fun syncUserToCloud(user: UserProfile) {
        val db = firestoreDb ?: return
        val docId = if (user.email.isNotBlank()) user.email.replace(".", "_") else user.id

        val enrichedUser = user.copy(
            deviceName = if (user.deviceName.isBlank()) getDeviceName() else user.deviceName,
            androidVersion = if (user.androidVersion.isBlank()) getAndroidVersion() else user.androidVersion,
            appVersion = "1.0",
            lastLoginDate = getCurrentTimestamp(),
            cityLocation = if (user.cityLocation.isBlank()) "Noida, UP" else user.cityLocation
        )

        CoroutineScope(Dispatchers.IO).launch {
            try {
                db.collection("users").document(docId).set(enrichedUser, SetOptions.merge()).await()
                Log.d(TAG, "User ${user.email} synced to Cloud Firestore successfully")
            } catch (e: Exception) {
                Log.w(TAG, "Offline / Error syncing user to cloud: ${e.message}")
            }
        }
    }

    // Auto-Restore old user data if installed on a new device
    suspend fun restoreUserFromCloud(email: String): UserProfile? {
        val db = firestoreDb ?: return null
        val docId = email.trim().replace(".", "_")

        return try {
            val doc = db.collection("users").document(docId).get().await()
            if (doc.exists()) {
                val restored = doc.toObject(UserProfile::class.java)
                if (restored != null) {
                    Log.d(TAG, "Restored user ${restored.email} with plan ${restored.plan} from Cloud!")
                    return restored
                }
            }
            // Check by query if docId differed
            val querySnapshot = db.collection("users").whereEqualTo("email", email.trim()).get().await()
            if (!querySnapshot.isEmpty) {
                querySnapshot.documents.firstOrNull()?.toObject(UserProfile::class.java)
            } else {
                null
            }
        } catch (e: Exception) {
            Log.w(TAG, "Failed to restore user from cloud: ${e.message}")
            null
        }
    }

    // Record Security Export Audit Log
    fun logExport(exportType: String, totalRecords: Int, fileOrSheetUrl: String = "") {
        val db = firestoreDb ?: return
        val log = ExportAuditLog(
            id = "EXP_${System.currentTimeMillis()}",
            exportType = exportType,
            exportedBy = "76076ck@gmail.com",
            timestamp = getCurrentTimestamp(),
            totalRecords = totalRecords,
            fileOrSheetUrl = fileOrSheetUrl
        )

        CoroutineScope(Dispatchers.IO).launch {
            try {
                db.collection("export_logs").document(log.id).set(log).await()
                Log.d(TAG, "Security audit log recorded for $exportType export")
            } catch (e: Exception) {
                Log.w(TAG, "Error recording export log: ${e.message}")
            }
        }
    }
}
