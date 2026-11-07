package com.example.data.model

data class SocLevel(
    val id: String,
    val levelCode: String, // "L1", "L2", "L3", "SOC Manager"
    val title: String,
    val subtitle: String,
    val salaryRange: String,
    val experienceReq: String,
    val coreMission: String,
    val keyResponsibilities: List<String>,
    val mandatoryTools: List<String>,
    val certifications: List<String>,
    val interviewFocus: List<String>,
    val roadmapChecklist: List<String>
)

object SocCareerData {
    val levels = listOf(
        SocLevel(
            id = "soc_l1",
            levelCode = "L1",
            title = "Tier 1 SOC Analyst",
            subtitle = "Triage, Alert Monitoring & Initial Dispatch",
            salaryRange = "₹4.5 - ₹8.5 LPA ($65k - $85k)",
            experienceReq = "0 - 2 Years (Freshers to Junior)",
            coreMission = "24/7 SIEM monitoring, true vs false positive triage, initial alert containment, and escalation adhering strictly to SLA timelines.",
            keyResponsibilities = listOf(
                "Monitor SIEM consoles (Splunk, Microsoft Sentinel, QRadar) for triggered alerts and rule violations.",
                "Perform initial alert triage, validate IOCs (IPs, hashes, URLs) using VirusTotal, AbuseIPDB, and AlienVault OTX.",
                "Verify email phishing alerts, parse RFC 822 headers, extract malicious attachments and URLs in sandbox.",
                "Execute L1 containment SOPs: isolate infected endpoints via EDR (CrowdStrike / SentinelOne), block malicious IPs at firewall.",
                "Document incident ticket details and escalate confirmed security events to Tier 2 within strict 15-minute SLA."
            ),
            mandatoryTools = listOf("Splunk / Sentinel", "CrowdStrike Falcon", "Wireshark", "VirusTotal / AbuseIPDB", "Jira / ServiceNow ITSM", "AnyRun / Joe Sandbox"),
            certifications = listOf("CompTIA Security+ (SY0-701)", "Blue Team Level 1 (BTL1)", "CompTIA CySA+", "SC-200 (Microsoft Security Ops)"),
            interviewFocus = listOf("3-Way TCP Handshake & Flag analysis", "Phishing header analysis & SPF/DKIM/DMARC", "False vs True Positive triage logic", "CIA Triad and basic MITRE ATT&CK tactics"),
            roadmapChecklist = listOf(
                "Master TCP/IP, OSI model, ports (53, 80, 443, 3389, 445, 88)",
                "Hands-on Splunk search queries (stats, eval, table, chart)",
                "Analyze 20+ phishing email headers (.eml)",
                "Learn Windows Security Event IDs (4624, 4625, 4688, 4720, 7045)",
                "Practice Sysinternals tools (Sysmon, Procmon, Autoruns)"
            )
        ),
        SocLevel(
            id = "soc_l2",
            levelCode = "L2",
            title = "Tier 2 SOC Analyst / Incident Responder",
            subtitle = "Deep Investigation, Remediation & Incident Handling",
            salaryRange = "₹9.0 - ₹16.0 LPA ($95k - $125k)",
            experienceReq = "2 - 5 Years",
            coreMission = "Lead in-depth investigation of escalated incidents, determine root cause, perform live endpoint forensics, and author remediation playbooks.",
            keyResponsibilities = listOf(
                "Lead end-to-end incident response following NIST SP 800-61r2 and SANS 6-step PICERL framework.",
                "Conduct live host forensics, inspect memory artifacts, investigate persistence mechanisms (Registry Run keys, WMI subscriptions, Scheduled tasks).",
                "Deep PCAP analysis in Wireshark: identify DNS tunneling, C2 beaconing jitter, SMB lateral movement, TLS metadata anomalies.",
                "Map threat actor actions to MITRE ATT&CK enterprise matrix (Initial Access -> Exfiltration).",
                "Author and tune SIEM correlation rules to suppress persistent false positives and enhance detection fidelity."
            ),
            mandatoryTools = listOf("Wireshark & Zeek / Suricata", "Volatility 3 & FTK Imager", "Splunk SPL / Sentinel KQL", "KAPE / Velociraptor", "YARA & Sigma Rules", "TheHive / Cortex"),
            certifications = listOf("GIAC Certified Incident Handler (GCIH)", "Certified Threat Intelligence Analyst (CTIA)", "BTL2", "Certified Ethical Hacker (CEH Practical)"),
            interviewFocus = listOf("NIST / SANS Incident Response lifecycle", "Windows forensic artifacts (MFT, Prefetch, Amcache, Shimcache)", "Ransomware outbreak containment strategy", "Memory forensics: carving processes with Volatility"),
            roadmapChecklist = listOf(
                "Execute full DFIR lab on infected Windows VM",
                "Analyze memory dump for Meterpreter & Cobalt Strike beacons",
                "Write 10 custom Sigma rules for detecting lateral movement",
                "Correlate EDR telemetry with network firewall logs",
                "Build automated SOAR playbook for credential abuse alert"
            )
        ),
        SocLevel(
            id = "soc_l3",
            levelCode = "L3",
            title = "Tier 3 Senior Threat Hunter & Forensics Lead",
            subtitle = "Proactive Threat Hunting, Reverse Eng & Detection Engineering",
            salaryRange = "₹18.0 - ₹30.0 LPA ($140k - $185k)",
            experienceReq = "5 - 8 Years",
            coreMission = "Formulate hypothesis-driven threat hunts, discover stealthy APTs bypassing existing defenses, reverse-engineer malware payloads, and engineer resilient detections.",
            keyResponsibilities = listOf(
                "Execute proactive hypothesis-driven threat hunting campaigns without relying on active alerts.",
                "Perform static and dynamic malware triage: unpack obfuscated payloads, decode PowerShell/VBS scripts, extract C2 configs.",
                "Design and deploy detection-as-code pipelines using Sigma, YARA, and CI/CD for SIEM ingestion.",
                "Perform adversary emulation using Atomic Red Team to validate detection coverage across MITRE techniques.",
                "Conduct complex DFIR on enterprise Active Directory compromises (Kerberoasting, DCSync, Golden Ticket)."
            ),
            mandatoryTools = listOf("Ghidra / x64dbg / IDA Free", "Atomic Red Team", "Sigma / YARA", "MISP / OpenCTI", "Cuckoo / CAPEv2", "BloodHound / SharpHound"),
            certifications = listOf("GIAC Certified Forensic Analyst (GCFA)", "Offensive Security Certified Professional (OSCP)", "GIAC Cyber Threat Intelligence (GCTI)", "CISSP"),
            interviewFocus = listOf("Hypothesis generation using Threat Intel feeds", "De-obfuscating PowerShell & macro downloaders", "Active Directory attack pathways & Kerberos attacks", "Detection Engineering architecture & MITRE mapping"),
            roadmapChecklist = listOf(
                "Conduct 5 end-to-end hypothesis hunts in enterprise datasets",
                "Reverse-engineer real-world dropper to extract C2 endpoints",
                "Build continuous purple teaming pipeline with Atomic Red Team",
                "Audit Active Directory attack paths with BloodHound",
                "Mentor L1 and L2 analysts on triage methodology"
            )
        ),
        SocLevel(
            id = "soc_mgr",
            levelCode = "SOC Manager",
            title = "SOC Operations Lead / Security Operations Manager",
            subtitle = "Governance, Team Leadership, Budgeting & SLA Metrics",
            salaryRange = "₹28.0 - ₹48.0 LPA ($180k - $240k+)",
            experienceReq = "8+ Years",
            coreMission = "Direct 24/7 SOC operations, govern compliance standards (ISO 27001, SOC 2, HIPAA), optimize MTTA/MTTR, manage SIEM/EDR licensing, and lead C-level crisis communications.",
            keyResponsibilities = listOf(
                "Supervise round-the-clock shift staffing, escalation rosters, training programs, and performance KPIs.",
                "Track, optimize, and present core SOC metrics: Mean Time to Acknowledge (MTTA), Mean Time to Detect (MTTD), Mean Time to Respond (MTTR).",
                "Manage security tooling procurement, vendor contracts, log ingestion budgeting, and license optimization.",
                "Lead incident command post during high-impact cyber disasters and coordinate with legal, HR, PR, and executive leadership.",
                "Conduct tabletop exercises (TTX) for executive stakeholders and continuously align SOC ops with NIST CSF."
            ),
            mandatoryTools = listOf("SOC Metric Dashboards & PowerBI", "Risk Register & GRC Suites", "Vendor Management Portals", "Tabletop Exercise Frameworks", "C-Suite Briefing Templates"),
            certifications = listOf("CISSP (Certified Information Systems Security Professional)", "CISM (Certified Information Security Manager)", "CRISC", "GIAC Security Leadership (GSLC)"),
            interviewFocus = listOf("Crisis communication under regulatory scrutiny", "Balancing log ingestion cost vs detection visibility", "Staff retention and burn-out mitigation in 24/7 shifts", "Audit readiness for SOC 2 and ISO 27001"),
            roadmapChecklist = listOf(
                "Establish SOC SLA/SLO contract with business units",
                "Implement tiered triage model with automated SOAR enrichment",
                "Design tabletop scenario for ransomware targeting backup servers",
                "Deliver monthly executive risk presentation to CISO",
                "Audit continuous training pipeline for SOC staff"
            )
        )
    )
}
