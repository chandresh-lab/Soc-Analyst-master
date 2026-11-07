package com.example.data.repository

import com.example.data.model.MockTestCertificate
import com.example.data.model.Question
import com.example.data.model.QuestionCategory
import com.example.data.model.ResumeTemplate
import com.example.data.model.ResumeTemplateData
import com.example.data.model.SubscriptionPlan
import com.example.data.model.TwoFactorMethod
import com.example.data.model.UserProfile
import com.example.data.model.UserRole
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class PaymentTransaction(
    val id: String,
    val userEmail: String,
    val userName: String,
    val plan: SubscriptionPlan,
    val amountRupees: Int,
    val paymentMethod: String,
    val razorpayPaymentId: String,
    val timestamp: String,
    val status: String = "SUCCESS"
)

data class ResumeReviewSubmission(
    val id: String,
    val userEmail: String,
    val userName: String,
    val targetRole: String,
    val resumeSummary: String,
    val timestamp: String,
    var status: String = "Pending Review",
    var adminFeedback: String = ""
)

object SocRepository {

    // Active User State
    private val _currentUser = MutableStateFlow<UserProfile?>(null)
    val currentUser: StateFlow<UserProfile?> = _currentUser.asStateFlow()

    // Registered Users List (Synchronized with Cloud Firestore)
    private val _registeredUsers = MutableStateFlow<List<UserProfile>>(
        listOf(
            UserProfile(
                id = "usr_super_admin",
                fullName = "SOC Master Super Admin",
                email = "76076ck@gmail.com",
                mobileNumber = "7607640067",
                fullAddress = "Aarav IT Solutions Cyber Tower, Mathura, UP",
                dob = "1992-05-14",
                gender = "Male",
                qualification = "M.Tech / Lead CISO & Threat Architect",
                role = UserRole.SUPER_ADMIN,
                plan = SubscriptionPlan.PREMIUM,
                is2faEnabled = true,
                twoFactorMethod = TwoFactorMethod.EMAIL,
                registeredDate = "2026-11-01",
                deviceName = "Google Pixel 9 Pro Fold",
                androidVersion = "Android 15 (API 35)",
                appVersion = "1.0",
                installDate = "2026-11-01",
                lastLoginDate = "2026-11-06 18:30",
                cityLocation = "Mathura",
                mockTestScore = 20,
                atsScore = 98
            )
        )
    )
    val registeredUsers: StateFlow<List<UserProfile>> = _registeredUsers.asStateFlow()

    // Questions State (500 Questions)
    private val _allQuestions = MutableStateFlow<List<Question>>(emptyList())
    val allQuestions: StateFlow<List<Question>> = _allQuestions.asStateFlow()

    // Resume Templates State
    private val _templates = MutableStateFlow<List<ResumeTemplate>>(ResumeTemplateData.templates)
    val templates: StateFlow<List<ResumeTemplate>> = _templates.asStateFlow()

    // Transactions History
    private val _transactions = MutableStateFlow<List<PaymentTransaction>>(emptyList())
    val transactions: StateFlow<List<PaymentTransaction>> = _transactions.asStateFlow()

    // Submitted Resumes for Admin Review
    private val _resumeReviews = MutableStateFlow<List<ResumeReviewSubmission>>(emptyList())
    val resumeReviews: StateFlow<List<ResumeReviewSubmission>> = _resumeReviews.asStateFlow()

    // Saved Certificates
    private val _certificates = MutableStateFlow<List<MockTestCertificate>>(emptyList())
    val certificates: StateFlow<List<MockTestCertificate>> = _certificates.asStateFlow()

    init {
        _allQuestions.value = QuestionBank.getAllQuestions()
    }

    fun mergeCloudUsers(cloudList: List<UserProfile>) {
        val current = _registeredUsers.value.toMutableList()
        cloudList.forEach { cu ->
            val idx = current.indexOfFirst { it.email.equals(cu.email, ignoreCase = true) }
            if (idx >= 0) {
                current[idx] = cu
            } else {
                current.add(cu)
            }
        }
        _registeredUsers.value = current
    }

    // --- Authentication & Users with Cloud Sync & Restore ---
    fun login(email: String, role: UserRole): Boolean {
        val existing = _registeredUsers.value.firstOrNull { it.email.equals(email.trim(), ignoreCase = true) }
        val loggedInUser = if (existing != null) {
            existing.copy(
                role = role,
                deviceName = FirebaseSyncManager.getDeviceName(),
                androidVersion = FirebaseSyncManager.getAndroidVersion(),
                lastLoginDate = FirebaseSyncManager.getCurrentTimestamp()
            )
        } else {
            UserProfile(
                fullName = if (email.contains("@")) email.substringBefore("@").replace(".", " ").capitalize() else "Candidate",
                email = email.trim(),
                mobileNumber = "+91 99887 76655",
                role = role,
                plan = if (role == UserRole.SUPER_ADMIN) SubscriptionPlan.PREMIUM else SubscriptionPlan.FREE,
                deviceName = FirebaseSyncManager.getDeviceName(),
                androidVersion = FirebaseSyncManager.getAndroidVersion(),
                lastLoginDate = FirebaseSyncManager.getCurrentTimestamp()
            )
        }

        updateUserProfile(loggedInUser)

        // Try asynchronous Cloud Restore in case user previously saved on another phone
        CoroutineScope(Dispatchers.IO).launch {
            val restored = FirebaseSyncManager.restoreUserFromCloud(email.trim())
            if (restored != null) {
                val merged = loggedInUser.copy(
                    plan = restored.plan,
                    mockTestScore = if (restored.mockTestScore > 0) restored.mockTestScore else loggedInUser.mockTestScore,
                    atsScore = if (restored.atsScore > 0) restored.atsScore else loggedInUser.atsScore,
                    bookmarkedQuestionIds = restored.bookmarkedQuestionIds.ifEmpty { loggedInUser.bookmarkedQuestionIds },
                    resumeRole = restored.resumeRole.ifBlank { loggedInUser.resumeRole },
                    resumeSummary = restored.resumeSummary.ifBlank { loggedInUser.resumeSummary },
                    resumeBullets = restored.resumeBullets.ifEmpty { loggedInUser.resumeBullets }
                )
                _currentUser.value = merged
                // Re-sync with latest device info
                FirebaseSyncManager.syncUserToCloud(merged)
            } else {
                // First time sync to cloud
                FirebaseSyncManager.syncUserToCloud(loggedInUser)
            }
        }

        return true
    }


    fun registerUser(
        fullName: String,
        email: String,
        mobileNumber: String,
        fullAddress: String,
        dob: String,
        gender: String,
        qualification: String,
        profilePhotoUri: String,
        role: UserRole
    ): UserProfile {
        val user = UserProfile(
            fullName = fullName.trim(),
            email = email.trim(),
            mobileNumber = mobileNumber.trim(),
            fullAddress = fullAddress.trim(),
            dob = dob,
            gender = gender,
            qualification = qualification.trim(),
            profilePhotoUri = profilePhotoUri,
            role = role,
            plan = SubscriptionPlan.FREE,
            is2faEnabled = true,
            twoFactorMethod = TwoFactorMethod.EMAIL,
            deviceName = FirebaseSyncManager.getDeviceName(),
            androidVersion = FirebaseSyncManager.getAndroidVersion(),
            appVersion = "1.0",
            installDate = FirebaseSyncManager.getCurrentTimestamp().substringBefore(" "),
            lastLoginDate = FirebaseSyncManager.getCurrentTimestamp(),
            cityLocation = if (fullAddress.contains(",")) fullAddress.split(",").last().trim() else "Noida"
        )
        _registeredUsers.value = listOf(user) + _registeredUsers.value
        _currentUser.value = user

        // Auto-Sync Registration to Cloud Database
        FirebaseSyncManager.syncUserToCloud(user)
        return user
    }

    fun logout() {
        _currentUser.value = null
    }

    fun updateUserProfile(updated: UserProfile) {
        _currentUser.value = updated
        _registeredUsers.value = _registeredUsers.value.map {
            if (it.id == updated.id || it.email.equals(updated.email, ignoreCase = true)) updated else it
        }
        FirebaseSyncManager.syncUserToCloud(updated)
    }

    // --- Bookmarking & Questions ---
    fun toggleBookmark(questionId: String) {
        _allQuestions.value = _allQuestions.value.map { q ->
            if (q.id == questionId) q.copy(isBookmarked = !q.isBookmarked) else q
        }
        val user = _currentUser.value
        if (user != null) {
            val bookmarked = _allQuestions.value.filter { it.isBookmarked }.map { it.id }
            val updated = user.copy(bookmarkedQuestionIds = bookmarked)
            updateUserProfile(updated)
        }
    }

    fun updateMockScore(score: Int) {
        val user = _currentUser.value ?: return
        val updated = user.copy(mockTestScore = score)
        updateUserProfile(updated)
    }

    fun updateAtsScoreAndResume(atsScore: Int, role: String, summary: String, bullets: List<String>) {
        val user = _currentUser.value ?: return
        val updated = user.copy(
            atsScore = atsScore,
            resumeRole = role,
            resumeSummary = summary,
            resumeBullets = bullets
        )
        updateUserProfile(updated)
    }

    fun addQuestion(
        category: QuestionCategory,
        questionText: String,
        options: List<String>,
        correctIndex: Int,
        explanation: String,
        codeSnippet: String?
    ) {
        val currentMax = _allQuestions.value.filter { it.category == category }.size
        val newQ = Question(
            id = "custom_${System.currentTimeMillis()}",
            category = category,
            questionNumber = currentMax + 1,
            question = questionText,
            options = options,
            correctOptionIndex = correctIndex,
            answerExplanation = explanation,
            codeSnippet = codeSnippet,
            difficulty = "Intermediate"
        )
        _allQuestions.value = listOf(newQ) + _allQuestions.value
    }

    fun deleteQuestion(questionId: String) {
        _allQuestions.value = _allQuestions.value.filter { it.id != questionId }
    }

    fun getDailyMockQuestions(): List<Question> {
        val selected = mutableListOf<Question>()
        QuestionCategory.entries.forEach { cat ->
            val catQuestions = _allQuestions.value.filter { it.category == cat }
            selected.addAll(catQuestions.shuffled().take(4))
        }
        return selected.shuffled()
    }

    fun saveCertificate(certificate: MockTestCertificate) {
        _certificates.value = listOf(certificate) + _certificates.value
    }

    // --- Payment Upgrade with Cloud Auto-Sync ---
    fun upgradeSubscription(plan: SubscriptionPlan, paymentMethod: String, amount: Int): PaymentTransaction {
        val user = _currentUser.value ?: UserProfile(
            fullName = "Candidate",
            email = "user@aaravitsolutons.com",
            mobileNumber = "+91 99999 88888"
        )
        val updatedUser = user.copy(plan = plan)
        updateUserProfile(updatedUser)

        val txn = PaymentTransaction(
            id = "TXN_${System.currentTimeMillis().toString().takeLast(6)}",
            userEmail = user.email,
            userName = user.fullName,
            plan = plan,
            amountRupees = amount,
            paymentMethod = paymentMethod,
            razorpayPaymentId = "pay_rzp_${System.currentTimeMillis().toString().takeLast(8)}",
            timestamp = FirebaseSyncManager.getCurrentTimestamp()
        )
        _transactions.value = listOf(txn) + _transactions.value
        return txn
    }

    fun submitResumeForReview(targetRole: String, summary: String): ResumeReviewSubmission {
        val user = _currentUser.value
        val submission = ResumeReviewSubmission(
            id = "REV_${System.currentTimeMillis().toString().takeLast(4)}",
            userEmail = user?.email ?: "candidate@aaravitsolutons.com",
            userName = user?.fullName ?: "Candidate",
            targetRole = targetRole,
            resumeSummary = summary,
            timestamp = FirebaseSyncManager.getCurrentTimestamp().substringBefore(" ")
        )
        _resumeReviews.value = listOf(submission) + _resumeReviews.value
        return submission
    }

    fun addAdminFeedback(reviewId: String, feedback: String) {
        _resumeReviews.value = _resumeReviews.value.map {
            if (it.id == reviewId) it.copy(adminFeedback = feedback, status = "Reviewed by Admin") else it
        }
    }
}
