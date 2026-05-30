package com.example.data

data class ProductFormulation(
    val brandName: String,
    val genericName: String,
    val category: String,
    val isKeyMolecule: Boolean = false,
    val efficacyDetails: String = ""
)

object ProductPortfolio {
    val products = listOf(
        ProductFormulation(
            "Ulshield 20/40",
            "Esomeprazole (20mg/40mg)",
            "Gastro (Tablets)",
            isKeyMolecule = true,
            efficacyDetails = "Ulshield delivers 92% acid suppression vs 76% with conventional omeprazole. Features superior mucosal healing rates in GERD and rapid relief of nocturnal heartburn."
        ),
        ProductFormulation(
            "Ulshield Fast 40",
            "Esomeprazole + Sodium Bicarbonate",
            "Gastro (Tablets)"
        ),
        ProductFormulation(
            "Lactusol",
            "Lactulose Solution",
            "Gastro (Syrup)"
        ),
        ProductFormulation(
            "Alerz / Alerz Syrup",
            "Levocetirizine",
            "Anti-allergic"
        ),
        ProductFormulation(
            "Montrin L",
            "Montelukast + Levocetirizine",
            "Anti-allergic"
        ),
        ProductFormulation(
            "Pbin (25/50/75/150)",
            "Pregabalin",
            "Neuropathic Pain",
            isKeyMolecule = true,
            efficacyDetails = "Pbin provides rapid-onset neuropathic pain relief in diabetic neuropathy and post-herpetic neuralgia. Significantly improves sleep quality index scores (PSQI) vs placebo."
        ),
        ProductFormulation(
            "Pbin M",
            "Pregabalin + Methylcobalamin",
            "Neuropathic Pain"
        ),
        ProductFormulation(
            "Zanstat 40/80",
            "Febuxostat",
            "Antigout (Tablets)",
            isKeyMolecule = true,
            efficacyDetails = "Zanstat maintains serum uric acid < 6.0 mg/dL in 82% of gout patients compared to only 44% with standard allopurinol. Safe to use in mild-to-moderate kidney impairment."
        ),
        ProductFormulation(
            "Cefmex (200/400/Forte)",
            "Cefixime",
            "Antibiotic"
        ),
        ProductFormulation(
            "Myzith (100/200/500)",
            "Azithromycin",
            "Antibiotic"
        ),
        ProductFormulation(
            "Clincare 300",
            "Clindamycin (300mg)",
            "Antibiotic"
        ),
        ProductFormulation(
            "Zanilex 2/4",
            "Tizanidine (2mg/4mg)",
            "Muscle Relaxant"
        ),
        ProductFormulation(
            "Calcipan (CCM/CT)",
            "Calcium + Vitamin D3 / Calcitriol",
            "Supplement"
        ),
        ProductFormulation(
            "Vitex (C/M/L)",
            "Multivitamins / B-Complex",
            "Supplement"
        ),
        ProductFormulation(
            "Hemotone",
            "Iron + Folic Acid + B-Complex",
            "Supplement (Syrup)"
        ),
        ProductFormulation(
            "Diclofast Gel",
            "Diclofenac + Linseed Oil + Menthol",
            "Topical (Gel)"
        ),
        ProductFormulation(
            "Chlorosol",
            "Chlorhexidine Gluconate 0.2%",
            "Mouthwash"
        ),
        ProductFormulation(
            "Impiroc",
            "Mupirocin 2%",
            "Topical (Ointment)"
        ),
        ProductFormulation(
            "Trancid 250/500",
            "Tranexamic Acid",
            "Hemostatic"
        ),
        ProductFormulation(
            "Chymosol / Forte",
            "Trypsin-Chymotrypsin",
            "Anti-inflammatory"
        ),
        ProductFormulation(
            "Alphasil 4/8",
            "Silodosin",
            "Urology (Capsules)"
        )
    )
}
