package com.example.data.model

import com.google.firebase.firestore.IgnoreExtraProperties

enum class UserRole(val displayName: String) {
    CANDIDATE("Candidate"),
    ADMIN("Admin"),
    SUPER_ADMIN("Super Admin")
}

enum class SubscriptionPlan(val title: String, val priceDisplay: String, val badge: String) {
    FREE("FREE", "₹0", "Starter"),
    PRO("PRO", "₹199/mo or ₹999/yr", "Pro Hunter"),
    PREMIUM("PREMIUM", "₹1,999 One-Time", "Lifetime Elite")
}

enum class TwoFactorMethod {
    EMAIL,
    SMS
}

@IgnoreExtraProperties
data class UserProfile(
    val id: String = "usr_${System.currentTimeMillis()}",
    val fullName: String = "",
    val email: String = "",
    val mobileNumber: String = "",
    val fullAddress: String = "",
    val dob: String = "",
    val gender: String = "Male",
    val qualification: String = "B.Tech / Cybersecurity",
    val profilePhotoUri: String = "",
    val role: UserRole = UserRole.CANDIDATE,
    val plan: SubscriptionPlan = SubscriptionPlan.FREE,
    val is2faEnabled: Boolean = true,
    val twoFactorMethod: TwoFactorMethod = TwoFactorMethod.EMAIL,
    val registeredDate: String = "2026-11-05",
    // Device & Sync Telemetry fields
    val deviceName: String = "",
    val androidVersion: String = "",
    val appVersion: String = "1.0",
    val installDate: String = "2026-11-05",
    val lastLoginDate: String = "2026-11-06 18:30",
    val cityLocation: String = "Noida",
    val mockTestScore: Int = 0,
    val atsScore: Int = 0,
    val bookmarkedQuestionIds: List<String> = emptyList(),
    val resumeSummary: String = "",
    val resumeRole: String = "",
    val resumeBullets: List<String> = emptyList()
)

data class ExportAuditLog(
    val id: String = "EXP_${System.currentTimeMillis()}",
    val exportType: String = "CSV", // "Google Sheet", "CSV", "XLS"
    val exportedBy: String = "76076ck@gmail.com",
    val timestamp: String = "",
    val totalRecords: Int = 0,
    val fileOrSheetUrl: String = ""
)
