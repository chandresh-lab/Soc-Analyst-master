package com.example.data.model

enum class QuestionCategory(val title: String, val shortCode: String, val description: String) {
    SOC_FUNDAMENTALS(
        "SOC Fundamentals",
        "FUNDAMENTALS",
        "Core SOC operations, CIA Triad, MITRE ATT&CK, Cyber Kill Chain & NIST CSF"
    ),
    NETWORKING(
        "Networking & Protocols",
        "NETWORKING",
        "TCP/IP, 3-way handshake, DNS tunneling, TLS/SSL, Wireshark packet analysis"
    ),
    SIEM(
        "SIEM (Splunk, QRadar, Sentinel)",
        "SIEM",
        "SPL queries, KQL queries, AQL queries, correlation rules, normalization & log parsing"
    ),
    INCIDENT_RESPONSE(
        "IR & Threat Hunting",
        "IR & HUNTING",
        "NIST/SANS lifecycle, live memory forensics, ransomware response & C2 hunting"
    ),
    HR_SCENARIO(
        "HR & Scenario Based",
        "SCENARIO & HR",
        "Crisis triage under pressure, shift handovers, escalation matrix & behavioral"
    )
}

data class Question(
    val id: String,
    val category: QuestionCategory,
    val questionNumber: Int,
    val question: String,
    val options: List<String>,
    val correctOptionIndex: Int,
    val answerExplanation: String,
    val codeSnippet: String? = null,
    val difficulty: String = "Intermediate",
    var isBookmarked: Boolean = false
)
