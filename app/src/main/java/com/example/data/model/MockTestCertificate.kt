package com.example.data.model

data class MockTestCertificate(
    val certificateId: String, // e.g. "SOC-CERT-98214"
    val candidateName: String,
    val candidateEmail: String,
    val scoreAchieved: Int,
    val totalQuestions: Int = 20,
    val percentage: Int,
    val issueDate: String,
    val verificationUrl: String = "https://aaravitsolutons.com/verify",
    val authorizedBy: String = "Aarav IT Solutions - Cyber Defense Academy"
)
