package com.example.data.model

data class ResumeTemplate(
    val id: String,
    val title: String,
    val levelCategory: String, // "L1", "L2", "L3", "Specialist", "Leadership"
    val targetRole: String,
    val summary: String,
    val coreSkills: List<String>,
    val sampleBullets: List<String>,
    val recommendedCerts: List<String>,
    val atsKeywords: List<String>,
    val colorTag: String
)

data class ExperienceBulletGroup(
    val experienceLevel: String, // "Fresher / 0-1 Yr", "1-2 Years", "3-5 Years", "Manager / Lead"
    val category: String,
    val bullets: List<String>
)

object ResumeTemplateData {
    val templates = listOf(
        ResumeTemplate(
            id = "tmpl_soc_l1",
            title = "SOC Analyst Tier 1 (Entry)",
            levelCategory = "L1",
            targetRole = "SOC Analyst L1 / Junior Cyber Analyst",
            summary = "Vigilant and detail-oriented Security Operations Center (SOC) Analyst L1 with hands-on proficiency in SIEM monitoring (Splunk, Microsoft Sentinel), alert triage, and phishing investigation. Demonstrated capability in analyzing 100+ daily security events, classifying true vs. false positives, and executing endpoint isolation playbooks within strict 15-minute SLA windows.",
            coreSkills = listOf("Splunk SIEM (SPL)", "Alert Triage", "Phishing Email Analysis", "CrowdStrike Falcon EDR", "Wireshark PCAP", "VirusTotal / AbuseIPDB", "NIST Incident Lifecycle", "Windows Event Logs"),
            sampleBullets = listOf(
                "Monitored enterprise SIEM (Splunk Enterprise) processing 12,000+ EPS, triaging 70+ security alerts daily across endpoints, firewalls, and proxy logs.",
                "Investigated 150+ user-reported phishing emails; analyzed RFC 822 email headers, parsed suspicious URLs and SPF/DKIM/DMARC configurations, preventing 8 credential harvesting campaigns.",
                "Executed initial containment procedures on 12 compromised endpoints via CrowdStrike Falcon EDR, adhering strictly to 15-minute internal incident response SLA.",
                "Authored 25+ detailed incident tickets in ServiceNow ITSM with documented IOCs, timeline reconstructs, and severity classifications.",
                "Performed proactive daily vulnerability sweeps using Nessus scanner and coordinated with sysadmins to patch critical CVEs across 400+ servers."
            ),
            recommendedCerts = listOf("CompTIA Security+", "Blue Team Level 1 (BTL1)", "CompTIA CySA+"),
            atsKeywords = listOf("Splunk", "SIEM", "Triage", "Phishing", "EDR", "CrowdStrike", "Wireshark", "IOC", "SLA", "Incident Response", "Windows Event IDs", "True Positive", "MITRE ATT&CK"),
            colorTag = "#FFD700"
        ),
        ResumeTemplate(
            id = "tmpl_soc_l2",
            title = "SOC Analyst Tier 2 (Incident Response)",
            levelCategory = "L2",
            targetRole = "Tier 2 SOC Analyst / Incident Responder",
            summary = "Proactive and analytical Tier 2 SOC Analyst with 3+ years of experience in deep incident handling, root cause forensics, and threat hunting. Adept at orchestrating incident response following NIST SP 800-61r2, authoring high-fidelity correlation rules, and executing memory/host forensics across enterprise hybrid-cloud environments.",
            coreSkills = listOf("Incident Response (PICERL)", "Splunk SPL / Sentinel KQL", "Memory Forensics (Volatility)", "Host Forensics (KAPE, FTK)", "YARA / Sigma Rules", "Suricata / Zeek", "MITRE ATT&CK Mapping", "Malware Sandbox Analysis"),
            sampleBullets = listOf(
                "Led end-to-end incident handling for 35+ high-severity security incidents, mitigating ransomware, Cobalt Strike beacons, and business email compromise (BEC).",
                "Conducted host and memory forensics utilizing FTK Imager and Volatility 3, extracting injected DLLs, malicious thread IDs, and persistence registry artifacts.",
                "Engineered and deployed 18 custom Splunk SPL correlation alerts mapped to MITRE ATT&CK, reducing false positive fatigue by 42% across SOC Tier 1.",
                "Performed deep packet inspection (DPI) in Wireshark and Zeek to uncover beaconing intervals, DNS tunneling, and SMB lateral movement.",
                "Automated alert enrichment playbooks using Python and SOAR, slashing mean time to acknowledge (MTTA) from 18 minutes to under 3 minutes."
            ),
            recommendedCerts = listOf("GIAC Certified Incident Handler (GCIH)", "CompTIA CySA+", "BTL2", "SC-200"),
            atsKeywords = listOf("Incident Response", "Forensics", "Volatility", "KQL", "Splunk SPL", "Sigma", "YARA", "Ransomware", "C2 Beaconing", "Zeek", "Root Cause Analysis", "SOAR"),
            colorTag = "#38BDF8"
        ),
        ResumeTemplate(
            id = "tmpl_soc_l3",
            title = "Senior Threat Hunter & L3 Lead",
            levelCategory = "L3",
            targetRole = "Senior SOC Analyst L3 / Threat Hunting Lead",
            summary = "Distinguished Senior Cybersecurity Analyst and Threat Hunter with 6+ years spearheading adversary hunting, detection engineering, and advanced DFIR. Proven expertise in uncovering stealthy APT intrusions, engineering detection-as-code, and emulating adversary tradecraft using Atomic Red Team and MITRE ATT&CK.",
            coreSkills = listOf("Hypothesis Threat Hunting", "Detection Engineering (Sigma)", "Adversary Emulation", "Active Directory Security", "Reverse Engineering", "BloodHound / SharpHound", "Python & PowerShell Scripting", "Atomic Red Team"),
            sampleBullets = listOf(
                "Formulated and executed 45+ proactive hypothesis-driven threat hunts across 25,000 enterprise endpoints, discovering 3 previously undetected persistent APT footholds.",
                "Built and maintained detection-as-code pipeline with 80+ Sigma rules integrated into CI/CD, auto-transpiling into Splunk SPL and Microsoft Sentinel KQL.",
                "Uncovered and dismantled complex Active Directory lateral movement attack chains involving Kerberoasting, DCSync, and unauthorized shadow admin creation.",
                "Reverse-engineered obfuscated PowerShell droppers and .NET loaders using Ghidra and CyberChef to extract hardcoded C2 infrastructure domains and encryption keys.",
                "Mentored a team of 12 Tier 1 & 2 analysts, conducting weekly case study debriefs and authoring 15 comprehensive incident response playbooks."
            ),
            recommendedCerts = listOf("GIAC Certified Forensic Analyst (GCFA)", "OSCP", "GCTI", "CISSP"),
            atsKeywords = listOf("Threat Hunting", "APT", "Adversary Emulation", "Kerberoasting", "BloodHound", "Detection as Code", "Sigma Rules", "Ghidra", "Memory Analysis", "Active Directory"),
            colorTag = "#A855F7"
        ),
        ResumeTemplate(
            id = "tmpl_ir_dfir",
            title = "Digital Forensics & Incident Response (DFIR)",
            levelCategory = "Specialist",
            targetRole = "DFIR Specialist / CSIRT Investigator",
            summary = "Dedicated DFIR Specialist specialized in forensic evidence acquisition, memory dump analysis, and enterprise breach containment. Seasoned in dead-box forensics, timeline generation with plaso/log2timeline, and courtroom-defensible chain of custody management.",
            coreSkills = listOf("EnCase / FTK Imager", "Volatility 3", "X-Ways Forensics", "Timeline Analysis (Plaso)", "Chain of Custody", "NTFS Artifacts (\$MFT, \$LogFile)", "Prefetch / Shimcache", "Linux Forensics"),
            sampleBullets = listOf(
                "Acquired forensic bit-stream disk images and volatile RAM dumps across 60+ critical production servers adhering to ISO/IEC 27037 standards.",
                "Reconstructed granular incident timelines parsing MFT records, Windows Prefetch, Amcache, and Event Logs to determine exact breach entry points.",
                "Authored 30+ comprehensive forensic breach reports submitted to general counsel, insurance underwriters, and cyber law enforcement.",
                "Engineered customized Python and PowerShell scripts to automate remote evidence gathering across 5,000 distributed endpoints during live breach containment."
            ),
            recommendedCerts = listOf("GIAC GCFA", "GIAC GCFE", "CHFI"),
            atsKeywords = listOf("DFIR", "Forensics", "Chain of Custody", "FTK Imager", "Volatility", "MFT", "Shimcache", "Evidence Acquisition", "Breach Investigation"),
            colorTag = "#EC4899"
        ),
        ResumeTemplate(
            id = "tmpl_threat_intel",
            title = "Cyber Threat Intelligence (CTI) Analyst",
            levelCategory = "Specialist",
            targetRole = "CTI Analyst / Threat Intelligence Specialist",
            summary = "Strategic and technical Cyber Threat Intelligence Analyst experienced in tracking threat actor TTPs, curating threat feeds in MISP/OpenCTI, and operationalizing intelligence into defensive detection signatures.",
            coreSkills = listOf("MISP & OpenCTI", "Diamond Model", "MITRE ATT&CK Matrix", "STIX / TAXII Protocols", "Dark Web Monitoring", "YARA Rule Creation", "Threat Actor Profiling", "OSINT"),
            sampleBullets = listOf(
                "Curated high-confidence intelligence feeds in OpenCTI and MISP, converting adversary indicators into automated blocking policies across perimeter firewalls.",
                "Published bi-weekly threat briefs detailing emerging ransomware variants, zero-day CVEs, and adversary campaigns relevant to the financial sector.",
                "Mapped adversary campaigns using the Diamond Model and MITRE ATT&CK, pinpointing specific defensive gaps in enterprise detection telemetry.",
                "Conducted OSINT and dark web reconnaissance to detect leaked corporate credentials, compromised API tokens, and brand impersonation attacks."
            ),
            recommendedCerts = listOf("GIAC GCTI", "CTIA", "CompTIA CySA+"),
            atsKeywords = listOf("Threat Intelligence", "MISP", "OpenCTI", "STIX/TAXII", "Diamond Model", "OSINT", "Dark Web", "IOC", "TTPs", "MITRE ATT&CK"),
            colorTag = "#14B8A6"
        ),
        ResumeTemplate(
            id = "tmpl_siem_eng",
            title = "SIEM & Detection Engineer (Splunk/Sentinel)",
            levelCategory = "Specialist",
            targetRole = "SIEM Engineer / Detection Architect",
            summary = "Technical SIEM Architect specializing in large-scale log ingestion architecture, data normalization, parser authoring, and query performance tuning in Splunk, Microsoft Sentinel, and IBM QRadar.",
            coreSkills = listOf("Splunk Enterprise & Cloud", "Microsoft Sentinel (KQL)", "IBM QRadar (AQL)", "Logstash / Cribl Log Stream", "Syslog-ng & Fluentd", "Data Normalization (CIM)", "Splunk Heavy Forwarders", "Regex Parsing"),
            sampleBullets = listOf(
                "Architected enterprise Splunk deployment scaling to 45,000 EPS across multi-site clustering, achieving 99.98% ingestion uptime and optimal indexing efficiency.",
                "Authored 120+ optimized KQL analytical queries in Microsoft Sentinel, achieving 60% faster query response and saving $40,000 annually in Log Analytics compute.",
                "Engineered Cribl LogStream routing pipelines to sanitize, filter, and mask PII data prior to SIEM ingestion, reducing license spend by 32%.",
                "Built and maintained 60+ custom log parsers mapping legacy server syslog formats into the Splunk Common Information Model (CIM)."
            ),
            recommendedCerts = listOf("Splunk Certified Enterprise Admin", "Microsoft SC-200", "IBM QRadar SIEM Specialist"),
            atsKeywords = listOf("SIEM", "Splunk", "KQL", "AQL", "Log Ingestion", "CIM", "Cribl", "Syslog", "Detection Engineering", "Parsing", "EPS"),
            colorTag = "#F97316"
        ),
        ResumeTemplate(
            id = "tmpl_soc_mgr",
            title = "SOC Operations Lead / Manager",
            levelCategory = "Leadership",
            targetRole = "SOC Manager / Cyber Operations Lead",
            summary = "Results-driven Security Operations Center Manager with 8+ years guiding 24/7/365 security monitoring teams, driving SOC maturity from reactive triage to automated threat hunting, and managing multi-million dollar security tooling budgets.",
            coreSkills = listOf("SOC Governance & SLAs", "NIST CSF / ISO 27001", "MTTA / MTTD / MTTR Optimization", "Budgeting & Tool Procurement", "Staff Mentorship & Roster", "Tabletop Simulations", "Crisis Communication", "Executive Reporting"),
            sampleBullets = listOf(
                "Directed 24/7 SOC operations comprising 18 security analysts, reducing average MTTR from 75 minutes to 22 minutes through automated playbook deployment.",
                "Managed $2.4M security tooling budget covering SIEM, EDR, SOAR, and Threat Intel subscriptions; renegotiated enterprise contracts yielding 18% cost savings.",
                "Presented quarterly cybersecurity risk posture and threat metrics to Board of Directors, CISO, and external compliance auditors.",
                "Designed and facilitated bi-annual ransomware tabletop exercises (TTX) for executive leadership, enhancing cross-departmental incident response readiness.",
                "Maintained 100% compliance with SOC 2 Type II, ISO/IEC 27001:2022, and PCI-DSS audit requirements with zero major non-conformities."
            ),
            recommendedCerts = listOf("CISSP", "CISM", "CRISC", "GSLC"),
            atsKeywords = listOf("SOC Manager", "SLA", "MTTR", "MTTA", "Leadership", "Budgeting", "SOC 2", "ISO 27001", "Governance", "Tabletop Exercise", "Crisis Management"),
            colorTag = "#EAB308"
        ),
        ResumeTemplate(
            id = "tmpl_cloud_soc",
            title = "Cloud Security SOC Analyst (AWS & Azure)",
            levelCategory = "Specialist",
            targetRole = "Cloud SOC Analyst / Cloud Security Engineer",
            summary = "Cybersecurity engineer specialized in securing AWS and Microsoft Azure cloud environments, analyzing CloudTrail/GuardDuty logs, identifying IAM privilege escalation, and safeguarding Kubernetes workloads.",
            coreSkills = listOf("AWS CloudTrail & GuardDuty", "Azure Monitor & Sentinel", "AWS IAM & SCPs", "Cloud Custodian", "Kubernetes Security", "Wiz / Orca Security", "Terraform / CloudFormation", "S3 Bucket Auditing"),
            sampleBullets = listOf(
                "Monitored multi-account AWS environment with AWS Security Hub and GuardDuty, triaging 50+ monthly alerts related to anomalous IAM role assumption and public S3 exposures.",
                "Investigated Azure Entra ID anomalous sign-ins, conditional access bypass attempts, and OAuth app consent abuses.",
                "Implemented CSPM compliance scans across 300+ cloud accounts using Wiz, reducing high-severity cloud security misconfigurations by 65%.",
                "Authored automated Lambda functions to revoke compromised AWS access keys upon detection of unauthorized API calls within 60 seconds."
            ),
            recommendedCerts = listOf("AWS Certified Security - Specialty", "Microsoft Certified: Azure Security Engineer (AZ-500)", "CCSP"),
            atsKeywords = listOf("Cloud Security", "AWS", "Azure", "CloudTrail", "GuardDuty", "IAM", "CSPM", "Kubernetes", "Lambda", "Sentinel", "Security Hub"),
            colorTag = "#06B6D4"
        ),
        ResumeTemplate(
            id = "tmpl_vapt_blue",
            title = "VAPT & Purple Teaming Analyst",
            levelCategory = "Specialist",
            targetRole = "Vulnerability Analyst / Purple Team Specialist",
            summary = "Hands-on security professional bridging offensive testing and defensive detection engineering. Experienced in vulnerability scanning, penetration testing validations, and collaborating with SOC to ensure robust detection coverage.",
            coreSkills = listOf("Nessus & Qualys", "Burp Suite Pro", "Nmap & Metasploit", "Atomic Red Team", "MITRE ATT&CK Mapping", "CVE Prioritization", "Web Application Testing", "Purple Teaming"),
            sampleBullets = listOf(
                "Administered enterprise vulnerability management across 15,000 assets using Tenable Nessus, reducing unpatched critical CVEs by 78% within 90 days.",
                "Emulated adversary techniques (Atomic Red Team) to benchmark SOC detection rules and identified 14 blind spots in endpoint telemetry.",
                "Executed web application penetration tests using Burp Suite to identify OWASP Top 10 vulnerabilities (SQLi, IDOR, SSRF) before production releases.",
                "Collaborated with dev teams to implement CVSS-based remediation roadmaps, achieving 100% adherence to patch management SLAs."
            ),
            recommendedCerts = listOf("OSCP", "eJPT", "CompTIA PenTest+", "CEH"),
            atsKeywords = listOf("VAPT", "Vulnerability Management", "Nessus", "Burp Suite", "CVE", "CVSS", "Purple Team", "OWASP", "Penetration Testing"),
            colorTag = "#F43F5E"
        ),
        ResumeTemplate(
            id = "tmpl_malware_rev",
            title = "Malware Analyst & Reverse Engineer",
            levelCategory = "Specialist",
            targetRole = "Malware Analyst / Threat Researcher",
            summary = "Technical Malware Analyst experienced in static and dynamic analysis of sophisticated malware samples, unpacking executable binaries, decrypting payloads, and generating actionable threat intelligence.",
            coreSkills = listOf("Ghidra & IDA Pro", "x64dbg / OllyDbg", "Cuckoo Sandbox / CAPEv2", "PEStudio & Process Hacker", "YARA Rule Authoring", "Assembly (x86/x64)", "Python De-obfuscation", "CyberChef"),
            sampleBullets = listOf(
                "Conducted static and dynamic reverse engineering on 90+ malicious binaries, extracting command-and-control IP addresses and domain generation algorithms (DGA).",
                "Dissected multi-stage malware droppers, reversing complex string encryption and process hollowing techniques inside Ghidra.",
                "Authored 50+ custom YARA signatures deployed across corporate mail gateways and EDR agents to intercept novel malware variants.",
                "Automated sandbox detonations using CAPEv2 API, generating automated threat intelligence reports detailing behavioral indicators."
            ),
            recommendedCerts = listOf("GIAC Reverse Engineering Malware (GREM)", "CREST CMAR"),
            atsKeywords = listOf("Malware Analysis", "Reverse Engineering", "Ghidra", "IDA Pro", "x64dbg", "YARA", "Process Hollowing", "PEStudio", "Assembly"),
            colorTag = "#6366F1"
        ),
        ResumeTemplate(
            id = "tmpl_net_soc",
            title = "Network Security Operations Specialist",
            levelCategory = "Specialist",
            targetRole = "Network Security Specialist / Firewall Admin",
            summary = "Network Security Engineer proficient in perimeter security, IDS/IPS tuning, deep packet inspection, and securing enterprise next-generation firewalls (Palo Alto, Fortinet, Cisco ASA).",
            coreSkills = listOf("Palo Alto Networks", "Fortinet FortiGate", "Cisco ASA / Firepower", "Suricata & Snort IDS", "Wireshark Packet Analysis", "BGP & OSPF Security", "VPN & Zero Trust Access", "SSL/TLS Decryption"),
            sampleBullets = listOf(
                "Managed 24 enterprise Palo Alto Next-Gen Firewalls protecting corporate perimeter and data center zones with zero unauthorized breaches.",
                "Configured SSL/TLS decryption policies enabling SOC inspection of encrypted outbound traffic, identifying 12 data exfiltration attempts.",
                "Tuned Snort and Suricata IDS/IPS signatures, eliminating 8,000+ noisy alerts per day while maintaining zero detection escapes.",
                "Audited firewall rule bases using Tufin, deprecating 300+ obsolete rules and enforcing least-privilege egress filtering."
            ),
            recommendedCerts = listOf("PCNSA / PCNSE", "CCNP Security", "Fortinet NSE 4/5"),
            atsKeywords = listOf("Network Security", "Palo Alto", "Fortinet", "Firewall", "Wireshark", "IDS/IPS", "Suricata", "Snort", "VPN", "SSL Decryption"),
            colorTag = "#10B981"
        ),
        ResumeTemplate(
            id = "tmpl_iam_soc",
            title = "Identity & Access (IAM) SOC Specialist",
            levelCategory = "Specialist",
            targetRole = "IAM Security Analyst / Identity Threat Specialist",
            summary = "Identity security analyst specializing in Active Directory defense, Entra ID (Azure AD), Privileged Access Management (PAM), and detecting identity-based attacks (Pass-the-Hash, Golden Ticket, MFA Fatigue).",
            coreSkills = listOf("Active Directory & Kerberos", "Microsoft Entra ID", "CyberArk PAM", "Okta Identity Cloud", "MFA & Conditional Access", "BloodHound / PingCastle", "Identity Threat Detection (ITDR)", "OAuth Security"),
            sampleBullets = listOf(
                "Secured Active Directory enterprise forest for 12,000 users, eliminating insecure NTLMv1 protocols and enforcing Kerberos AES-256.",
                "Deployed and monitored CyberArk PAM for all administrative credentials, enforcing automated password rotation and session recording.",
                "Detected and neutralized 4 MFA fatigue / push-bombing attacks targeting executive accounts through automated conditional access policies.",
                "Performed quarterly Active Directory health audits using PingCastle and BloodHound, resolving 40+ risky ACL delegation paths."
            ),
            recommendedCerts = listOf("Microsoft SC-300 (Identity & Access)", "CyberArk Certified Defender"),
            atsKeywords = listOf("IAM", "Active Directory", "Entra ID", "CyberArk", "PAM", "Kerberos", "BloodHound", "PingCastle", "MFA", "Identity Threat"),
            colorTag = "#8B5CF6"
        ),
        ResumeTemplate(
            id = "tmpl_edr_analyst",
            title = "Endpoint Detection & Response (EDR) Analyst",
            levelCategory = "Specialist",
            targetRole = "EDR Specialist / Host Defense Analyst",
            summary = "Specialized host-defense analyst expert in deploying, configuring, and monitoring enterprise EDR solutions (CrowdStrike Falcon, Microsoft Defender for Endpoint, SentinelOne) across 20,000+ endpoints.",
            coreSkills = listOf("CrowdStrike Falcon", "Microsoft Defender for Endpoint", "SentinelOne Singularity", "Sysinternals Suite", "Process Tree Analysis", "PowerShell & WMI Auditing", "Live Terminal Response", "Ransomware Remediation"),
            sampleBullets = listOf(
                "Supervised EDR agent deployment and sensor hygiene across 20,000 Windows, macOS, and Linux servers with 99.8% sensor coverage.",
                "Conducted live host investigations using CrowdStrike Real Time Response (RTR) to remediate malicious scheduled tasks and terminate rogue processes.",
                "Crafted custom IOC prevention rules and hash blacklists across Defender for Endpoint, neutralizing targeted phishing malware campaigns.",
                "Analyzed parent-child process relationships (e.g., winword.exe spawning powershell.exe) to isolate and terminate living-off-the-land (LotL) attacks."
            ),
            recommendedCerts = listOf("CrowdStrike Certified Falcon Administrator", "Microsoft SC-200"),
            atsKeywords = listOf("EDR", "CrowdStrike", "Defender for Endpoint", "SentinelOne", "Process Analysis", "Live Response", "Sysinternals", "Host Defense"),
            colorTag = "#3B82F6"
        ),
        ResumeTemplate(
            id = "tmpl_compliance_soc",
            title = "SOC & Compliance Auditor",
            levelCategory = "Specialist",
            targetRole = "Cybersecurity Compliance Analyst / SOC Auditor",
            summary = "Security auditor specializing in validating SOC technical controls against regulatory frameworks (ISO/IEC 27001, SOC 2 Type II, PCI-DSS, HIPAA, and NIST CSF).",
            coreSkills = listOf("ISO 27001:2022", "SOC 2 Type II Audits", "PCI-DSS v4.0", "NIST CSF 2.0", "Evidence Collection", "Risk Assessment", "Vendor Security Audits", "GRC Platforms (ServiceNow GRC)"),
            sampleBullets = listOf(
                "Led internal audit preparation for annual SOC 2 Type II and ISO 27001 audits, collecting evidence across 150+ controls with zero major findings.",
                "Conducted third-party vendor risk assessments on 85 SaaS vendors, evaluating SOC reports and security architecture to mitigate supply-chain risk.",
                "Authored security policy updates aligning internal incident handling and data retention procedures with PCI-DSS 4.0 requirements.",
                "Coordinated with SOC and infrastructure teams to remediate audit non-conformities within designated 30-day corrective action windows."
            ),
            recommendedCerts = listOf("CISA (Certified Information Systems Auditor)", "ISO 27001 Lead Auditor"),
            atsKeywords = listOf("Compliance", "Audit", "ISO 27001", "SOC 2", "PCI-DSS", "NIST CSF", "GRC", "Risk Assessment", "Evidence Collection"),
            colorTag = "#14B8A6"
        ),
        ResumeTemplate(
            id = "tmpl_devsecops",
            title = "DevSecOps & Security Automation Engineer",
            levelCategory = "Specialist",
            targetRole = "DevSecOps Engineer / Security Automation Lead",
            summary = "Engineer bridging software development and SOC operations. Expert in embedding SAST/DAST into CI/CD pipelines, automating incident response through SOAR, and writing custom security tooling.",
            coreSkills = listOf("CI/CD Security (GitHub Actions, GitLab)", "SAST / DAST (SonarQube, Snyk)", "SOAR (Tines, Splunk SOAR)", "Python & Bash Scripting", "Docker & Container Security", "Trivy / Aqua Security", "REST API Integration", "Infrastructure as Code"),
            sampleBullets = listOf(
                "Integrated automated SAST and container vulnerability scanning into GitHub Actions pipelines, preventing 200+ vulnerable code packages from reaching prod.",
                "Engineered automated SOAR workflows connecting SIEM alerts to Jira and Slack, reducing Tier 1 alert triage time by 75%.",
                "Authored custom Python tools to query threat intelligence APIs (VirusTotal, Shodan, Censys) and auto-enrich incoming SOC incident tickets.",
                "Enforced container image signing with Cosign and Trivy vulnerability gates across production Kubernetes clusters."
            ),
            recommendedCerts = listOf("Certified DevSecOps Professional (CDP)", "AWS Certified DevOps Engineer"),
            atsKeywords = listOf("DevSecOps", "CI/CD", "SOAR", "SAST", "DAST", "Python", "Docker", "Kubernetes", "Automation", "Snyk", "SonarQube"),
            colorTag = "#D946EF"
        )
    )

    val experienceBullets = listOf(
        ExperienceBulletGroup(
            experienceLevel = "Fresher / 0-1 Yr",
            category = "SOC Monitoring & Triage",
            bullets = listOf(
                "Monitored enterprise SIEM console (Splunk) processing 8,000+ EPS, triaging 40+ security alerts daily adhering to 15-minute SLA.",
                "Investigated 60+ user-reported phishing emails; extracted headers, validated sender SPF/DKIM/DMARC, and sandbox-detonated URLs.",
                "Performed initial alert validation using VirusTotal, AbuseIPDB, and AlienVault OTX to distinguish true vs. false positives.",
                "Documented incident findings in Jira/ServiceNow ITSM and escalated confirmed security incidents to Tier 2 analysts.",
                "Executed endpoint isolation via EDR (CrowdStrike / Defender) following established standard operating procedures."
            )
        ),
        ExperienceBulletGroup(
            experienceLevel = "1-2 Years Experience",
            category = "Incident Response & Playbooks",
            bullets = listOf(
                "Handled 50+ tier-2 security incidents including ransomware alerts, unauthorized PowerShell execution, and credential dumping.",
                "Authored 10 custom Splunk SPL correlation rules mapped to MITRE ATT&CK techniques, reducing false positive alert noise by 35%.",
                "Conducted PCAP network traffic analysis in Wireshark to isolate malware command-and-control (C2) beaconing.",
                "Performed live triage on compromised endpoints using Sysinternals tools (Sysmon, Autoruns, Process Hacker).",
                "Automated IP reputation and domain lookup in SOAR, saving 12 minutes of manual enrichment per ticket."
            )
        ),
        ExperienceBulletGroup(
            experienceLevel = "3-5 Years Experience",
            category = "DFIR & Threat Hunting",
            bullets = listOf(
                "Led incident response lifecycle for 20+ enterprise critical breaches following NIST SP 800-61r2 framework.",
                "Executed host and memory forensics using FTK Imager and Volatility 3, extracting malicious DLLs and registry persistence keys.",
                "Formulated hypothesis-driven threat hunting campaigns identifying 5 stealthy lateral movement attempts in Active Directory.",
                "Authored 25+ production-grade Sigma and YARA rules for detecting novel threat actor tradecraft.",
                "Mentored 6 junior L1 analysts on triage methodologies, log analysis, and effective ticket documentation."
            )
        ),
        ExperienceBulletGroup(
            experienceLevel = "Manager / Lead",
            category = "SOC Strategy & Leadership",
            bullets = listOf(
                "Directed 24/7/365 security operations center of 15 analysts, reducing MTTR by 60% through SOAR playbook deployment.",
                "Managed $1.5M annual security budget covering SIEM log ingestion, EDR licenses, and threat intelligence subscriptions.",
                "Established and tracked core SOC performance metrics (MTTA, MTTD, MTTR, false positive ratios) for executive CISO reviews.",
                "Designed and facilitated simulated ransomware tabletop exercises (TTX) for executive stakeholders and crisis management teams.",
                "Maintained 100% compliance across SOC 2 Type II, ISO/IEC 27001, and PCI-DSS external security audits."
            )
        )
    )

    fun calculateAtsScore(
        jobRole: String,
        resumeText: String,
        skillsCount: Int,
        experienceBulletsCount: Int,
        certificationsCount: Int
    ): Pair<Int, Map<String, String>> {
        var score = 40 // Baseline

        val feedback = mutableMapOf<String, String>()

        // 1. Check Keywords
        val relevantKeywords = listOf(
            "splunk", "siem", "triage", "incident response", "edr", "crowdstrike",
            "wireshark", "pcap", "mitre", "ioc", "sla", "phishing", "forensics",
            "kql", "spl", "sigma", "yara", "nist", "active directory", "c2"
        )
        val textLower = resumeText.lowercase()
        val foundKeywords = relevantKeywords.filter { textLower.contains(it) }
        val keywordScore = (foundKeywords.size * 2).coerceAtMost(25)
        score += keywordScore
        feedback["Keyword Density"] = "${foundKeywords.size}/${relevantKeywords.size} key SOC industry terms detected (+${keywordScore} pts)"

        // 2. Metrics and numbers presence (ATS loves quantified accomplishments)
        val metricRegex = Regex("\\b\\d+[%kK+]?\\b")
        val matches = metricRegex.findAll(resumeText).count()
        if (matches >= 4) {
            score += 15
            feedback["Quantified Impact"] = "Strong metric-driven bullet points found ($matches metrics) (+15 pts)"
        } else {
            score += (matches * 3)
            feedback["Quantified Impact"] = "Add more numerical achievements (e.g., EPS, SLA mins, MTTR %) (+${matches * 3} pts)"
        }

        // 3. Action Verbs
        val actionVerbs = listOf("monitored", "investigated", "executed", "authored", "led", "mitigated", "analyzed", "conducted", "automated", "architected")
        val foundVerbs = actionVerbs.filter { textLower.contains(it) }
        if (foundVerbs.size >= 3) {
            score += 10
            feedback["Action Verbs"] = "Strong security action verbs used (+10 pts)"
        } else {
            score += 4
            feedback["Action Verbs"] = "Include stronger verbs like 'Investigated', 'Mitigated', 'Engineered'"
        }

        // 4. Skills & Certs
        if (skillsCount >= 6) score += 5 else score += 2
        if (certificationsCount >= 1) score += 5 else score += 1
        feedback["Credentials"] = "$skillsCount skills and $certificationsCount certifications cataloged"

        val finalScore = score.coerceIn(15, 98)
        return Pair(finalScore, feedback)
    }

    fun matchJobDescription(jobDescription: String, resumeText: String): Pair<Int, List<String>> {
        val jdLower = jobDescription.lowercase()
        val resumeLower = resumeText.lowercase()

        val socKeywords = listOf(
            "splunk", "sentinel", "qradar", "siem", "crowdstrike", "defender", "sentinelone", "edr",
            "wireshark", "pcap", "tcp/ip", "dns", "phishing", "incident response", "dfir",
            "volatility", "ftk", "mitre att&ck", "cyber kill chain", "nist", "sans", "picerl",
            "sigma", "yara", "kql", "spl", "active directory", "kerberos", "c2", "malware",
            "nessus", "vapt", "cve", "cvss", "soar", "python", "powershell", "regex", "aws", "azure"
        )

        val jdKeywords = socKeywords.filter { jdLower.contains(it) }
        if (jdKeywords.isEmpty()) {
            return Pair(65, listOf("Splunk", "CrowdStrike", "Incident Response", "Wireshark", "MITRE ATT&CK"))
        }

        val matched = jdKeywords.filter { resumeLower.contains(it) }
        val missing = jdKeywords.filter { !resumeLower.contains(it) }.take(6)

        val matchPercent = ((matched.size.toDouble() / jdKeywords.size.toDouble()) * 100).toInt().coerceIn(30, 95)
        return Pair(matchPercent, missing)
    }
}
