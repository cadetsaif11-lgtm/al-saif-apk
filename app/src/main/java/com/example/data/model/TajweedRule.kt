package com.example.data.model

import androidx.compose.ui.graphics.Color
import com.example.ui.theme.*

enum class TajweedRuleType(
    val title: String,
    val arabicTitle: String,
    val description: String,
    val color: Color,
    val letters: String
) {
    GHUNNAH(
        title = "Ghunnah (Nasalization)",
        arabicTitle = "الغُنَّة",
        description = "A nasal sound held for 2 counts on Noon or Meem with Shaddah (نّ / مّ).",
        color = TajweedGhunnah,
        letters = "نّ ، مّ"
    ),
    QALQALAH(
        title = "Qalqalah (Echoing Bounce)",
        arabicTitle = "القَلْقَلَة",
        description = "Vibrant echoing bounce when one of the 5 letters has a Sukoon or stop.",
        color = TajweedQalqalah,
        letters = "ق ، ط ، ب ، ج ، د (قُطْبُ جَدٍّ)"
    ),
    IKHFA(
        title = "Ikhfa (Concealment)",
        arabicTitle = "الإِخْفَاء",
        description = "Concealing the Noon Saakin or Tanween with a light nasal sound before 15 letters.",
        color = TajweedIkhfa,
        letters = "ت ث ج د ذ ز س ش ص ض ط ظ ف ق ك"
    ),
    IDGHAM(
        title = "Idgham (Merging)",
        arabicTitle = "الإِدْغَام",
        description = "Merging Noon Saakin or Tanween completely into the following letter.",
        color = TajweedIdgham,
        letters = "ي ، ر ، م ، ل ، و ، ن (يَرْمَلُون)"
    ),
    IQLAB(
        title = "Iqlab (Conversion)",
        arabicTitle = "الإِقْلاب",
        description = "Converting Noon Saakin or Tanween into a concealed Meem when followed by Baa (ب).",
        color = TajweedIqlab,
        letters = "ب (تُقْلَبُ مِيماً خَفِيّة)"
    ),
    MADD(
        title = "Madd (Prolongation)",
        arabicTitle = "المَدّ",
        description = "Prolonging the sound of the Madd letters (Alif, Waw, Yaa) from 2 up to 6 harakat.",
        color = TajweedMadd,
        letters = "ا ، و ، ي (المَدّ الطَّبِيعِي وَالفَرْعِي)"
    ),
    TAFKHEEM(
        title = "Tafkheem (Heavy Letters)",
        arabicTitle = "التَّفْخِيم",
        description = "Pronouncing with a thick, heavy mouth articulation (Istila' letters).",
        color = TajweedHeavy,
        letters = "خ ، ص ، ض ، غ ، ط ، ق ، ظ (خُصَّ ضَغْطٍ قِظْ)"
    )
}

data class TajweedSpan(
    val rule: TajweedRuleType,
    val arabicSubstring: String,
    val explanation: String,
    val phoneticTip: String
)
