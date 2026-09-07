package com.example.data.model

import androidx.annotation.DrawableRes
import com.example.R

data class SelfDefenseTechnique(
    val title: String,
    val targetArea: String,
    val description: String,
    val actionSteps: List<String>,
    val tips: String
)

data class SafetyChecklist(
    val category: String,
    val title: String,
    val points: List<String>
)

data class LegalRight(
    val title: String,
    val description: String,
    val actOrLaw: String,
    @DrawableRes val imageResId: Int? = null
)

object SafetyGuideProvider {
    val selfDefenseTechniques = listOf(
        SelfDefenseTechnique(
            title = "Palm Heel Strike to Nose",
            targetArea = "Nose / Facial Bridge",
            description = "A powerful upward thrust with the heel of your palm forcing attacker back and causing immediate eye tearing.",
            actionSteps = listOf(
                "Keep fingers bent slightly backward to protect your fingernails",
                "Drive palm heel upwards hard under offender's nose",
                "Follow through with immediate backwards sprint to open space"
            ),
            tips = "Effective regardless of attacker size or weight."
        ),
        SelfDefenseTechnique(
            title = "Eye Gouge / Thumb Drive",
            targetArea = "Eyes",
            description = "Direct target to attacker's vision to disorient and force instantaneous release.",
            actionSteps = listOf(
                "Grasp attacker's cheek or head firmly",
                "Drive thumbs directly into corner of eyes",
                "Yell 'HELP! POLICE!' continuously to attract attention"
            ),
            tips = "Forces automatic pain reaction allowing immediate escape."
        ),
        SelfDefenseTechnique(
            title = "Groin Kick & Knee Strike",
            targetArea = "Groin",
            description = "Upward knee drive or swift instep kick to disable attacker instantly.",
            actionSteps = listOf(
                "Grab attacker's shoulders or arms for leverage",
                "Drive your knee directly upward into the groin area with maximum speed",
                "Turn immediately and run towards well-lit public area"
            ),
            tips = "Never turn your back until you kick or break grip."
        ),
        SelfDefenseTechnique(
            title = "Solar Plexus Elbow Strike",
            targetArea = "Upper Abdomen / Solar Plexus",
            description = "Used when grabbed from behind or side. Elbow is your hardest bone target.",
            actionSteps = listOf(
                "Shift weight slightly to lower your center of gravity",
                "Drive elbow horizontally backwards into attacker's chest or stomach",
                "Follow with a foot stamp directly onto attacker's instep"
            ),
            tips = "Elbow strikes carry tremendous momentum in close quarters."
        )
    )

    val safetyChecklists = listOf(
        SafetyChecklist(
            category = "Cab & Auto Travel",
            title = "Night Taxi & Auto Safety Checklist",
            points = listOf(
                "Verify vehicle registration plate matches app booking before stepping in",
                "Share trip status link & live WhatsApp location with family or Primary Guardian",
                "Check child lock on auto/cab rear doors (ensure handle works from inside)",
                "Avoid sitting directly behind driver or sharing rides with unknown co-passengers at night",
                "Keep phone loaded with emergency quick-dial and Sentinel AI app open",
                "If driver deviates from route, call 1091 loudly and announce vehicle number"
            )
        ),
        SafetyChecklist(
            category = "Walking Alone",
            title = "Street & Public Transit Vigilance",
            points = listOf(
                "Walk facing incoming traffic so vehicles cannot pull up behind you unnoticed",
                "Keep both hands free (do not keep hands buried in pockets or wear noise-cancelling headphones)",
                "If followed, cross the street immediately or head directly into open shop / petrol bunk / tea stall",
                "Hold keys firmly between knuckles or hold phone with thumb on Sentinel AI voice recorder button",
                "Trust your instinct: if an area feels wrong, immediately turn into a bright public zone",
                "Keep phone screen unblocked with Sentinel AI Voice Assistant active"
            )
        ),
        SafetyChecklist(
            category = "Workplace & Office",
            title = "Workplace & Late-Shift Commute Safety",
            points = listOf(
                "Know your Internal Complaints Committee (ICC) members under POSH Act 2013",
                "Request company verified security escort for drop-offs past 8:00 PM",
                "Document all unwanted messages, comments, or physical boundary violations",
                "Save company security control desk & AWPS numbers on speed dial"
            )
        ),
        SafetyChecklist(
            category = "Cyber & Digital Safety",
            title = "Online Protection & Stalking Safeguards",
            points = listOf(
                "Enable 2-Factor Authentication (2FA) on all messaging and social media accounts",
                "Avoid sharing real-time location check-ins publicly until you depart the venue",
                "Report morphing, blackmail or fake profile threats immediately on CyberCrime portal (cybercrime.gov.in / 1930)",
                "Do not click unknown links offering suspicious downloads or location tracking"
            )
        ),
        SafetyChecklist(
            category = "Elevator & Home Entry",
            title = "Home Entry & Elevator Vigilance",
            points = listOf(
                "In elevators with suspicious strangers, stand near the button panel; exit immediately if uncomfortable",
                "Keep emergency numbers (1091, 112, AWPS) saved on speed dial",
                "Ensure home entrance and balcony are well-lit with functioning locks",
                "Enable secondary lock / latch when inside hotel or rental rooms"
            )
        )
    )

    val legalRights = listOf(
        LegalRight(
            title = "Zero FIR Provision",
            description = "A victim of crime can register a Zero FIR at ANY police station in India regardless of jurisdiction or area location. The police station MUST accept the complaint, assign a Zero FIR number, and transfer it to the concerned jurisdiction without delay.",
            actOrLaw = "Section 154 CrPC / Bharatiya Nyaya Sanhita (BNS)",
            imageResId = R.drawable.img_law_zero_fir_1785561419833
        ),
        LegalRight(
            title = "24/7 Police Protection & AWPS Assistance",
            description = "All Women Police Stations (AWPS) in Tamil Nadu operate 24 hours a day, 7 days a week to provide immediate protection, medical assistance, and lodging of FIR without delay.",
            actOrLaw = "Helpline 1091 & TN Police Special Directives",
            imageResId = R.drawable.img_safety_guide_laws_1785560793482
        ),
        LegalRight(
            title = "Right Against Night Arrest for Women",
            description = "Women cannot be arrested after sunset (6 PM) and before sunrise (6 AM) except under extraordinary emergency circumstances with prior written permission from a Judicial Magistrate.",
            actOrLaw = "Section 46(4) Code of Criminal Procedure",
            imageResId = R.drawable.img_law_night_arrest_1785561432458
        ),
        LegalRight(
            title = "POSH Act 2013 (Workplace Harassment Protection)",
            description = "Every organization with 10 or more employees MUST establish an Internal Complaints Committee (ICC) headed by a senior woman officer. Sexual harassment at workplace includes unwelcome touch, demand for sexual favours, or hostile work environment.",
            actOrLaw = "POSH Act 2013 & BNS Section 75/78",
            imageResId = R.drawable.img_law_posh_act_1785561443165
        ),
        LegalRight(
            title = "Right to Confidentiality & Identity Protection",
            description = "The law strictly prohibits publishing or broadcasting the identity, name, or photo of sexual violence victims in media or public platforms to protect dignity and privacy.",
            actOrLaw = "BNS Section 73 / Section 228A IPC",
            imageResId = R.drawable.img_law_confidentiality_1785561456028
        ),
        LegalRight(
            title = "Protection Against Stalking & Voyeurism",
            description = "Following a woman, monitoring her electronic communications, or capturing private images without consent is a criminal offense punishable with rigorous imprisonment.",
            actOrLaw = "BNS Section 77 & 78 (IPC 354C & 354D)",
            imageResId = R.drawable.img_law_stalking_1785561467323
        ),
        LegalRight(
            title = "Free Legal Aid & Female Police Officer Statement",
            description = "Statement of female victims of sexual violence must be recorded by a female police officer at the victim's residence or place of choice, with free legal aid provided through State Legal Services Authority.",
            actOrLaw = "Section 157 CrPC & Legal Services Authorities Act",
            imageResId = R.drawable.img_legal_rights_banner_1785560774542
        ),
        LegalRight(
            title = "Domestic Violence Act 2005",
            description = "Provides immediate civil remedies including residence orders, protection orders, and monetary relief against physical, verbal, emotional, or economic abuse within home.",
            actOrLaw = "Protection of Women from Domestic Violence Act 2005",
            imageResId = R.drawable.img_law_domestic_violence_1785561476896
        )
    )
}
