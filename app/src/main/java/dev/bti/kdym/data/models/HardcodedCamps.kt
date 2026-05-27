package dev.bti.kdym.data.models

import com.google.firebase.Timestamp
import java.util.*

val HARDCODED_CAMPS = listOf(
    Camp(
        id = "camp_2026",
        name = "Heartland Outpour",
        theme = "Outpour",
        year = 2026,
        startDate = Timestamp(Calendar.getInstance().apply { set(2026, 5, 1) }.time),
        isActive = true,
        verse = "I will pour out my spirit upon all flesh.",
        verseReference = "Joel 2:28",
        verseTagline = "THESE ARE THE LAST DAYS",
        accentColor = "#EF4444",
        secondaryColor = "#22D3EE"
    ),
    Camp(
        id = "camp_2025",
        name = "Eternal",
        theme = "Eternal",
        year = 2025,
        isActive = false,
        romanYear = "MMXXV",
        yearText = "TWO THOUSAND TWENTY FIVE",
        subtitle = "Set your eyes on the eternal.",
        verse = "The things which are not seen are eternal.",
        verseReference = "2 Corinthians 4:18",
        verseTagline = "SET YOUR EYES ON THE ETERNAL",
        accentColor = "#EAB308",
        historyPhotos = listOf("mmxxv_1", "mmxxv_2", "mmxxv_3")
    ),
    Camp(
        id = "camp_2024",
        name = "Let's Go",
        theme = "Let's Go",
        year = 2024,
        isActive = false,
        romanYear = "MMXXIV",
        yearText = "TWO THOUSAND TWENTY FOUR",
        subtitle = "Go therefore.",
        verse = "Go ye therefore, and teach all nations.",
        verseReference = "Matthew 28:19",
        verseTagline = "GO WHERE HE SENDS",
        accentColor = "#10B981",
        historyPhotos = listOf("mmxxiv_1", "mmxxiv_2", "mmxxiv_3")
    ),
    Camp(
        id = "camp_2023",
        name = "Fire",
        theme = "Fire",
        year = 2023,
        isActive = false,
        romanYear = "MMXXIII",
        yearText = "TWO THOUSAND TWENTY THREE",
        subtitle = "A generation marked by fire.",
        verse = "Cloven tongues like as of fire.",
        verseReference = "Acts 2:3",
        verseTagline = "LET THE FIRE FALL",
        accentColor = "#F97316",
        historyPhotos = listOf("mmxxiii_1", "mmxxiii_2", "mmxxiii_3")
    ),
    Camp(
        id = "camp_2022",
        name = "One",
        theme = "One",
        year = 2022,
        isActive = false,
        romanYear = "MMXXII",
        yearText = "TWO THOUSAND TWENTY TWO",
        subtitle = "One Lord. One faith.\nOne baptism.",
        verse = "The LORD our God is one LORD.",
        verseReference = "Deuteronomy 6:4",
        verseTagline = "ONE LORD. ONE FAITH.",
        accentColor = "#22D3EE",
        historyPhotos = listOf("mmxxii_1", "mmxxii_2", "mmxxii_3")
    )
)
