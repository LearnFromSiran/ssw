package com.example.model

data class Vocabulary(
    val id: Int,
    val word: String,          // Kanji
    val pronunciation: String,   // Hiragana/Katakana
    val translation: String,     // English translation
    val romaji: String,          // Romaji
    val category: String,        // Body Parts, Body Positions, Symptoms, Care Verbs
    val explanation: String      // Short situational explanation
)

data class StudyChapter(
    val id: Int,
    val part: String,            // Part 1, Part 2, Part 3, Part 4
    val chapterNumber: Int,
    val title: String,
    val contentEnglish: String,
    val contentJapanese: String,
    val bulletPoints: List<String>
)

data class QuizQuestion(
    val id: Int,
    val questionType: QuizType,  // GENERAL, CONVERSATIONAL
    val questionJa: String,      // Japanese question / scenario context
    val questionEn: String,      // English context / translation
    val options: List<String>,   // 4 options
    val correctAnswerIndex: Int, // 0 to 3
    val explanation: String,     // Why it's correct and other points
    val dialogs: List<DialogLine>? = null // For conversational context
)

enum class QuizType {
    GENERAL, CONVERSATIONAL
}

data class DialogLine(
    val speaker: String,       // "Care Worker", "Satou-san", etc.
    val textJa: String,
    val textEn: String
)

object StudyData {
    val vocabularies = listOf(
        // === CATEGORY 1: BODY PARTS (1-30) ===
        Vocabulary(1, "頭", "あたま", "Head", "atama", "Body Parts", "Must be protected during transfers. Always align carefully."),
        Vocabulary(2, "額", "ひたい", "Forehead", "hitai", "Body Parts", "Used when observing facial expressions or taking temperatures with thermometers."),
        Vocabulary(3, "目", "め", "Eye", "me", "Body Parts", "Explanations of direction use the 'Clock Position' method for visually impaired users."),
        Vocabulary(4, "耳", "みみ", "Ear", "mimi", "Body Parts", "If the user has a hearing disorder, speak slowly and face them directly."),
        Vocabulary(5, "鼻", "はな", "Nose", "hana", "Body Parts", "Observe if there is discharge, breathing difficulty, or changes in color."),
        Vocabulary(6, "口", "くち", "Mouth", "kuchi", "Body Parts", "Observe the inside of the mouth before eating. Moisten it to help swallowing."),
        Vocabulary(7, "首", "くび", "Neck", "kubi", "Body Parts", "Do not pull or force the neck upward during meals; it increases aspiration risk."),
        Vocabulary(8, "脇", "わき", "Armpit", "waki", "Body Parts", "Default place to measure body temperature. If paralyzed, use non-paralyzed armpit."),
        Vocabulary(9, "肩", "かた", "Shoulder", "kata", "Body Parts", "Hold gently when helping a user roll over. Never pull on a paralyzed shoulder."),
        Vocabulary(10, "胸", "むね", "Chest", "mune", "Body Parts", "Observe respiration movement here. If chest pain occurs, report immediately."),
        Vocabulary(11, "背中", "せなか", "Back", "senaka", "Body Parts", "Prone to pressures ulcers. Do 'Senuki' to relieve bed pressure."),
        Vocabulary(12, "腰", "こし", "Lower Back", "koshi", "Body Parts", "To prevent back pain, utilize body mechanics principles (wide base, bend knees)."),
        Vocabulary(13, "臀部 / 尻", "でんぶ / しり", "Buttocks / Hips", "denbu / shiri", "Body Parts", "A frequent site for pressure ulcers as it takes the weight when lying down."),
        Vocabulary(14, "膝", "ひざ", "Knee", "hiza", "Body Parts", "Knee buckling must be prevented during standing/walking."),
        Vocabulary(15, "足の裏", "あしのうら", "Sole", "ashinoura", "Body Parts", "Ensure both soles are flat on the floor when sitting on the edge of the bed."),
        Vocabulary(16, "爪", "つめ", "Nail", "tsume", "Body Parts", "Keep nails neatly trimmed. Long nails can scratch skin or harbor bacteria."),
        Vocabulary(17, "皮膚", "ひふ", "Skin", "hifu", "Body Parts", "Observe for redness, dryness, or peeling, which are early signs of bedsores."),
        Vocabulary(18, "骨", "ほね", "Bone", "hone", "Body Parts", "Elderly bones are fragile and prone to osteoporosis, leading to easy fractures."),
        Vocabulary(19, "関節", "かんせつ", "Joint", "kansetsu", "Body Parts", "Support joints directly when performing range-of-motion or changing clothes."),
        Vocabulary(20, "筋肉", "きんにく", "Muscle", "kinniku", "Body Parts", "Promote daily movement to avoid muscle atrophy (disuse syndrome)."),
        Vocabulary(21, "手首", "てくび", "Wrist", "tekubi", "Body Parts", "Do not pull hard on the wrist during standing support."),
        Vocabulary(22, "足首", "あしくび", "Ankle", "ashikubi", "Body Parts", "Position at a 90-degree angle when seated in a wheelchair for posture stability."),
        Vocabulary(23, "肘", "ひじ", "Elbow", "hiji", "Body Parts", "A common skeletal protrusion that needs turning attention to avoid ulcers."),
        Vocabulary(24, "顎", "あご", "Chin", "ago", "Body Parts", "Always tuck the chin down during swallowing directly to prevent aspiration."),
        Vocabulary(25, "喉", "のど", "Throat", "nodo", "Body Parts", "Listen carefully for wet throat sounds or gargling cough blocks after drinking."),
        Vocabulary(26, "腹", "おなか / はら", "Abdomen", "onaka", "Body Parts", "Massage clockwise to help stimulate bowel movements for constipation."),
        Vocabulary(27, "歯", "は", "Tooth / Teeth", "ha", "Body Parts", "Ensure proper brushing to prevent oral bacteria and secondary pneumonia."),
        Vocabulary(28, "舌", "した", "Tongue", "shita", "Body Parts", "Keep clean using tongue brushes for optimal taste and hygiene."),
        Vocabulary(29, "指", "ゆび", "Finger", "yubi", "Body Parts", "Check fingernail capillary refill for instant circulation checks."),
        Vocabulary(30, "踵", "かかと", "Heel", "kakato", "Body Parts", "Heels are extremely susceptible to pressure ulcers when flat in bed."),

        // === CATEGORY 2: BODY POSITIONS (31-60) ===
        Vocabulary(31, "仰臥位", "ぎょうがい", "Supine Position", "gyougai", "Body Positions", "Lying flat on the back. It is the most common resting position."),
        Vocabulary(32, "側臥位", "そくがい", "Lateral Position", "sokugai", "Body Positions", "Lying flat on one side. Helpful for alleviating pressure ulcers."),
        Vocabulary(33, "腹臥位", "ふくがい", "Prone Position", "fukugai", "Body Positions", "Lying flat on the stomach. Used to relieve pressure on the back."),
        Vocabulary(34, "端座位", "たんざい", "End-sitting Position", "tanzai", "Body Positions", "Sitting on the edge of the bed with feet down. Keep feet flat."),
        Vocabulary(35, "椅座位", "いざい", "Chair-sitting Position", "izai", "Body Positions", "Sitting deep in a chair with hips back and ankles at a 90-degree angle."),
        Vocabulary(36, "立位", "りつい", "Standing Position", "ritsui", "Body Positions", "Standing up. Stand on affected side to guarantee safety."),
        Vocabulary(37, "半坐位", "はんざい", "Fowler's Position", "hanzai", "Body Positions", "Sitting partially reclined (30-40 degrees). Helpful for feeding on bed."),
        Vocabulary(38, "屈曲", "くっきょく", "Flexion", "kukkyoku", "Body Positions", "Bending joint action. Essential during dressing of affected limbs."),
        Vocabulary(39, "伸展", "しんてん", "Extension", "shinten", "Body Positions", "Straightening joint action. Essential during undressing of healthy limbs."),
        Vocabulary(40, "外転", "がいてん", "Abduction", "gaiten", "Body Positions", "Moving limbs outward from midline. Promoted slowly during warm-ups."),
        Vocabulary(41, "内転", "ないてん", "Adduction", "naiten", "Body Positions", "Moving limbs inward toward midline."),
        Vocabulary(42, "右片麻痺", "みぎかたまひ", "Right Hemiplegia", "migi katamahi", "Body Positions", "Paralysis on right side. Approach patient from left side."),
        Vocabulary(43, "左片麻痺", "ひだりかたまひ", "Left Hemiplegia", "hidari katamahi", "Body Positions", "Paralysis on left side. Place wheelchair on their right side."),
        Vocabulary(44, "患側", "かんそく", "Affected Side", "kansoku", "Body Positions", "The paralyzed or injured side. Must be supported closely."),
        Vocabulary(45, "健側", "けんそく", "Unaffected Side", "kensoku", "Body Positions", "The healthy/strong side. Encourage using it for self-support."),
        Vocabulary(46, "移乗", "いじょう", "Transfer", "ijou", "Body Positions", "Transferring from bed to wheelchair. Stand on affected side."),
        Vocabulary(47, "移動", "いどう", "Locomotion", "idou", "Body Positions", "General movement from one location to another safely."),
        Vocabulary(48, "起床", "きしょう", "Getting Out of Bed", "kishou", "Body Positions", "Waking up and sitting up. Do slowly to avoid dizziness."),
        Vocabulary(49, "就寝", "しゅうしん", "Going to Bed", "shuushin", "Body Positions", "Preparing for sleep. Check sheet creases to prevent friction."),
        Vocabulary(50, "寝返り", "ねがえり", "Rolling Over", "negaeri", "Body Positions", "Shifting left/right in bed. Induce every 2 hours to avoid bedsores."),
        Vocabulary(51, "離床", "りしょう", "Out of Bed Activity", "rishou", "Body Positions", "Leaving the bed. Promotes mental alertness and prevents disuse syndrome."),
        Vocabulary(52, "立位保持", "りついほじ", "Standing Retention", "ritsui hoji", "Body Positions", "Maintaining balance while standing. Use handrail on healthy side."),
        Vocabulary(53, "歩行", "ほこう", "Gait / Walking", "hokou", "Body Positions", "Walking under guidance. Matching rhythm brings security."),
        Vocabulary(54, "両足", "りょうあし", "Both Feet", "ryouashi", "Body Positions", "Always confirm both soles contact the subfloor fully before transfers."),
        Vocabulary(55, "体位変換", "たいいへんかん", "Postural Care Change", "taii henkan", "Body Positions", "Scheduled rotation of patient positions (usually every 2 hours in bed)."),
        Vocabulary(56, "良肢位", "りょうしい", "Position of Function", "ryoushii", "Body Positions", "Optimal joint posture that prevents contractures and deformities."),
        Vocabulary(57, "拘縮", "こうしゅく", "Contracture", "koushuku", "Body Positions", "Joint stiffening caused by non-movement. Prevent with daily exercises."),
        Vocabulary(58, "健側向き", "けんそくむき", "Facing Healthy Side", "kensokumuki", "Body Positions", "Roll the patient towards their non-paralyzed side for easier transitions."),
        Vocabulary(59, "麻痺側向き", "まひそくむき", "Facing Affected Side", "mahisokumuki", "Body Positions", "Roll onto affected side only briefly, with careful shoulder support."),
        Vocabulary(60, "座位安定", "ざいあんてい", "Sitting Stability", "zai antei", "Body Positions", "Maintaining seated posture without leaning or falling left/right."),

        // === CATEGORY 3: SYMPTOMS & DISEASES (61-90) ===
        Vocabulary(61, "脱水", "だっすい", "Dehydration", "dassui", "Symptoms", "Serious water loss. Signs include dry mouth, dark urine, skin tenting."),
        Vocabulary(62, "誤嚥", "ごえん", "Aspiration", "goen", "Symptoms", "Food/fluids entering trachea. Major risk factor for pneumonia."),
        Vocabulary(63, "褥瘡", "じょくそう", "Pressure Bedsore", "jokusou", "Symptoms", "Skin breakdown caused by pressure. Frequently occurs on hips/heels."),
        Vocabulary(64, "片麻痺", "かたまひ", "Hemiplegia", "katamahi", "Symptoms", "Paralysis on one side of the body. Always respect 'Dakken Chakkan'."),
        Vocabulary(65, "認知症", "にんちしょう", "Dementia", "ninchishou", "Symptoms", "Decline in brain cognitive function. Support with calm, clear empathy."),
        Vocabulary(66, "便秘", "べんぴ", "Constipation", "benpi", "Symptoms", "Infrequent bowel movements. Alleviate with fiber, fluids, abdominal massage."),
        Vocabulary(67, "失禁", "しっきん", "Incontinence", "shikkin", "Symptoms", "Unintentional leakage of urine/stools. Clean immediately with dignity."),
        Vocabulary(68, "低体温", "ていたいおん", "Hypothermia", "teitaion", "Symptoms", "Core temperature drops below 35°C. Check room temperature closely."),
        Vocabulary(69, "発熱", "ハツネツ", "Fever", "hatsunetsu", "Symptoms", "Elevated body temperature indicating systemic infection or inflammation."),
        Vocabulary(70, "嘔吐", "おうと", "Vomiting", "outo", "Symptoms", "Forceful expelling of stomach content. Keep sideways to prevent choking."),
        Vocabulary(71, "下痢", "げり", "Diarrhea", "geri", "Symptoms", "Loose, watery stool. Watch for dehydration and skin breakdown."),
        Vocabulary(72, "骨折", "こっせつ", "Fracture", "kossetsu", "Symptoms", "Broken bone. Frequently caused by falls (tentou) under low visibility."),
        Vocabulary(73, "擦り傷", "すりきず", "Abrasion / Scratch", "surikizu", "Symptoms", "Superficial wound. Clean immediately and protect with dressing."),
        Vocabulary(74, "目眩", "めまい", "Dizziness", "memai", "Symptoms", "Sensation of spinning. Prompt the user to sit down immediately."),
        Vocabulary(75, "咳", "せき", "Cough", "seki", "Symptoms", "Reflex to clear airways. Keep mouth covered and monitor intensity."),
        Vocabulary(76, "痰", "たん", "Sputum", "tan", "Symptoms", "Mucus from the lungs. Sticky tan can block breathing passages."),
        Vocabulary(77, "呼吸困難", "こきゅうkonnan", "Dyspnea / Shortness of Breath", "kokyuu konnan", "Symptoms", "Difficulty breathing. Raise head of bed to Fowler's pos."),
        Vocabulary(78, "腹痛", "ふくつう", "Abdominal Pain", "fukutsuu", "Symptoms", "Stomach pain. Note the exact sector and duration for reports."),
        Vocabulary(79, "頭痛", "ずつう", "Headache", "zutsuu", "Symptoms", "Pain in the head. Observe for associated signs like vomiting."),
        Vocabulary(80, "チアノーゼ", "ちあのーぜ", "Cyanosis", "chianooze", "Symptoms", "Blue skin coloration due to lack of oxygen. Report emergency immediately."),
        Vocabulary(81, "心不全", "しんふぜん", "Heart Failure", "shinfuzen", "Symptoms", "Heart is unable to pump blood adequately, leading to leg edema."),
        Vocabulary(82, "脳卒中", "のうそっちゅう", "Stroke", "nousochuu", "Symptoms", "Sudden interruption of brain blood flow. Main source of hemiplegia."),
        Vocabulary(83, "誤飲", "ごいん", "Accidental Ingestion", "goin", "Symptoms", "Swallowing non-food items. Keep detergent/meds locked safely."),
        Vocabulary(84, "転倒", "てんとう", "Fall", "tentou", "Symptoms", "Slipping/tripping on floor. Ensure clutter-free corridors."),
        Vocabulary(85, "転落", "てんらく", "Falling Off / Drop", "tenraku", "Symptoms", "Falling off high surfaces like bed/wheelchair. Ensure side rails."),
        Vocabulary(86, "むせ", "むせ", "Choking / Cough", "muse", "Symptoms", "Tracheal reflex when eating. Cut feed and offer thick liquids."),
        Vocabulary(87, "痒み", "かゆみ", "Itchiness", "kayumi", "Symptoms", "Check for skin dryness or contagious scabies. Avoid scratching."),
        Vocabulary(88, "浮腫", "ふしゅ", "Edema / Swelling", "fushu", "Symptoms", "Fluid retention in tissues. Commonly observed on shins/ankles."),
        Vocabulary(89, "高血圧", "こうけつあつ", "Hypertension", "kouketsuatsu", "Symptoms", "Chronically high blood pressure. Monitor salt intake in dietary care."),
        Vocabulary(90, "糖尿病", "とうにょうびょう", "Diabetes", "tounyoubyou", "Symptoms", "Impaired blood-sugar control. Careful monitoring of meal quantities."),

        // === CATEGORY 4: CARE VERBS & EQUIPMENT (91-120) ===
        Vocabulary(91, "声かけ", "こえかけ", "Addressing / Voice Call", "koekake", "Care Verbs", "Crucial practice of explaining actions in advance to reassure patient."),
        Vocabulary(92, "車いす", "くるまいす", "Wheelchair", "kurumaisu", "Care Verbs", "Ensure brakes are locked and feet on subfloor before transfers."),
        Vocabulary(93, "手すり", "てすり", "Handrail", "tesuri", "Care Verbs", "Fixed supporting device for safety in toileting and hallways."),
        Vocabulary(94, "杖", "つえ", "Medical Cane", "tsue", "Care Verbs", "Walking walking support. Grip with strong hand, move first."),
        Vocabulary(95, "歩行器", "ほこうき", "Walker Frame", "hokouki", "Care Verbs", "Provides four-point stability for users who can walk but sway."),
        Vocabulary(96, "おむつ", "おむつ", "Absorbent Diaper", "omutsu", "Care Verbs", "Worn only if toilet independence is impossible. Change promptly."),
        Vocabulary(97, "洗面", "せんめん", "Face Wash Assist", "senmen", "Care Verbs", "Helps user brush teeth and wash face to stimulate awakening."),
        Vocabulary(98, "着脱", "ちゃくだつ", "Dressing Care", "chakudatsu", "Care Verbs", "Putting on/taking off shirts. Adhere to undress-healthy/dress-affected."),
        Vocabulary(99, "水分補給", "すいぶんほきゅう", "Daily Hydration", "suibun-hokyuu", "Care Verbs", "Proactive fluid administration for elderly who have weak thirst."),
        Vocabulary(100, "ポータブルトイレ", "ぽーたぶるといれ", "Portable Toilet", "portable-toilet", "Care Verbs", "Bedside toilet. Place 30-45 degrees near their healthy hand side."),
        Vocabulary(101, "介護ベッド", "かいごべっど", "Care Bed", "kaigobeddo", "Care Verbs", "Adjustable bed with backrest elevation and height functions."),
        Vocabulary(102, "スライディングシート", "すらいでぃんぐしーと", "Sliding Sheet", "sliding-sheet", "Care Verbs", "Low-friction sheet to help shift horizontal weight without friction."),
        Vocabulary(103, "食事介助", "しょくじかいじょ", "Feeding Assistance", "shokuji kaijo", "Care Verbs", "Helping users eat. Sit at eye level and check their swallowing."),
        Vocabulary(104, "入浴介助", "にゅうよくかいじょ", "Bathing Assist", "nyuyoku kaijo", "Care Verbs", "Washing skin and hair. Keep warm and prevent slips in tub."),
        Vocabulary(105, "排泄介助", "はいせつかいじょ", "Toileting Assist", "haisetsu kaijo", "Care Verbs", "Assisting urination/bowels while keeping maximum patient privacy."),
        Vocabulary(106, "清拭", "せいしき", "Bed Bath / Wiping", "seishiki", "Care Verbs", "Wiping body with warm towels for hygiene if a tub bath isn't possible."),
        Vocabulary(107, "自立支援", "じりつしえん", "Autonomy Support", "jiritsu shien", "Care Verbs", "Helping users do what they can themselves to retain function."),
        Vocabulary(108, "尊厳保持", "そんげんほじ", "Dignity Protection", "songen hoji", "Care Verbs", "Treating the user with deep respect as a valuable human individual."),
        Vocabulary(109, "守秘義務", "しゅひぎむ", "Confidentiality Duty", "shuhi gimu", "Care Verbs", "Never leaking health records or private information outside staff."),
        Vocabulary(110, "共感", "きょうかん", "Empathy", "kyoukan", "Care Verbs", "Understanding and sharing user's emotional standpoint with warmth."),
        Vocabulary(111, "傾聴", "けいちょう", "Active Listening", "keichou", "Care Verbs", "Listening with concentration, nodding, and avoiding quick judgment."),
        Vocabulary(112, "報告", "ほうこく", "Reporting", "houkoku", "Care Verbs", "Relaying symptoms and outcomes promptly to supervisors/nurses."),
        Vocabulary(113, "連絡", "れんらく", "Contacting", "renraku", "Care Verbs", "Sharing schedule changes and logistical updates across the care team."),
        Vocabulary(114, "相談", "そうだん", "Consultation", "soudan", "Care Verbs", "Asking for professional advice when unsure of handling (Ho-Ren-So)."),
        Vocabulary(115, "口腔ケア", "こうくうケア", "Oral Care Hygiene", "koukuu kea", "Care Verbs", "Cleaning mouth/dentures to prevent bacteria and aspiration pneumonia."),
        Vocabulary(116, "ナースコール", "なーすこーる", "Nurse Call Button", "naasukooru", "Care Verbs", "Emergency call button. Ensure it is always within user's reach."),
        Vocabulary(117, "自助具", "じょじょぐ", "Self-Help Devices", "jijogu", "Care Verbs", "Modified utensils (spoon/grips) helping disabled eat by themselves."),
        Vocabulary(118, "福祉用具", "ふくしようぐ", "Welfare Equipment", "fukushi yougu", "Care Verbs", "Assistive technologies like lifters, walkers, and posture pads."),
        Vocabulary(119, "着替え", "きがえ", "Changing Apparel", "kigae", "Care Verbs", "Routine dressing change to maintain cleanliness and mental refresh."),
        Vocabulary(120, "見守り", "みまもり", "Observation Watch", "mimamori", "Care Verbs", "Watching a capability without interfering, to allow independent attempts.")
    )

    val chapters = listOf(
        // === PART 1: BASICS OF NURSING CARE (1-5) ===
        StudyChapter(
            1, "Part 1: Basics of Care", 1, "Human Dignity & QOL",
            "Nursing care is a specialized profession that respects human rights, encourages Quality of Life (QOL), and supports self-independence.",
            "介護の基本は、利用者の尊厳（Dignity）を保持し、QOL（生活の質）を高め、自己決定と自立支援を行うことです。",
            listOf(
                "Human Dignity: Every user must be respected as an individual.",
                "QOL: Includes physical, mental, and social fulfillment, recognizing what the user values.",
                "Self-Independence: Support self-determination. Let users choose their clothing or meals to keep mental vitality.",
                "Activities of Daily Living (ADL): Walking, eating, bathing, toileting, and grooming."
            )
        ),
        StudyChapter(
            2, "Part 1: Basics of Care", 2, "Professional Ethics",
            "Care workers observe high ethical standards, protecting user privacy, maintaining confidentiality, and preventing physical restraint.",
            "介護職の倫理には、プライバシーの保護、守秘義務の厳守、身体拘束の禁止、虐待の防止が含まれます。",
            listOf(
                "Privacy: Respect users during bathing and toileting. Cover private areas so they are not exposed.",
                "Confidentiality: Never share personal details of the user online or outside work without explicit consent.",
                "Abuse Prevention: Understand the 5 types (Physical, Psychological, Neglect, Economic, Sexual).",
                "Physical Restraint: Restricting movement is forbidden unless there are emergency, unavoidable circumstances."
            )
        ),
        StudyChapter(
            3, "Part 1: Basics of Care", 3, "Safety & Body Mechanics",
            "Ensuring safety involves regular observation, predicting dangers, and using the physics of Body Mechanics to protect both user and caregiver.",
            "安全管理とリスクマネジメントには、利用者の観察、ヒヤリハットの記録、ボディメカニクスによる腰痛予防が含まれます。",
            listOf(
                "Observation: Check for anomalies in facial expressions, respiration, gait, and vital signs.",
                "Risk Management: Analyze incidents ('Hiyari-Hatto') to compile preventive strategies (e.g. locking wheelchair brakes).",
                "Infectious Disease Control: Break the chain of infection. Standard Precautions: Wash hands and treat all fluids as infectious.",
                "Body Mechanics: Lower center of gravity, widen base of support, keep user close, bend knees. Use bigger leg muscles."
            )
        ),
        StudyChapter(
            4, "Part 1: Basics of Care", 4, "Infection Prevention (Standard Precautions)",
            "Standard Precautions protect both healthcare team and patient by handling all bodily fluids as potentially infectious.",
            "標準予防策（Standard Precautions）は、すべての湿性生体物質を感染の危険性があるものとして取り扱う原則です。",
            listOf(
                "Hand Hygiene: Wash hands before and after touching a user, even if disposable gloves were worn.",
                "Personal Protective Equipment: Wear gloves, aprons, and masks when contacting blood, bodily fluids, or waste.",
                "Environment Care: Disinfect frequently touched surfaces and handle contaminated sheets carefully without shaking.",
                "Disposal Rules: Throw diapers and wipes in designated containment bins to stop virus propagation."
            )
        ),
        StudyChapter(
            5, "Part 1: Basics of Care", 5, "Disuse Syndrome & Physical Care",
            "Disuse Syndrome refers to the physical and mental decline that occurs when a user is kept in bed for prolonged periods.",
            "生活不活発病（廃用症候群 / Disuse Syndrome）は、長期間の安静により身体機能や精神能力が低下する病態です。",
            listOf(
                "Muscle Atrophy: Muscle mass declines rapidly when kept non-weight bearing. Joint contractures lock up moving ranges.",
                "Cardiovascular & Skin: Orthostatic hypotension causes dizziness when rising. Prolonged pressure provokes bedsores.",
                "Mental Decline: Lack of external stimulus triggers depression, disorientation, and faster memory decline.",
                "Proactive Support: Encourage out-of-bed activities (Rishou), standing, or wheelchair transfers daily."
            )
        ),

        // === PART 2: MECHANISMS OF MIND AND BODY (6-10) ===
        StudyChapter(
            6, "Part 2: Mind & Body", 1, "Mechanism of Body & Mind",
            "This covers homeostasis (maintenance of stable internals) and observation of Vital Signs: Temperature, Blood Pressure, Pulse, and Respiration.",
            "人間の心身のメカニズムには、恒常性（ホメオスタシス）があり、バイタルサイン（体温・血圧・脈拍・呼吸）の観察が基本となります。",
            listOf(
                "Homeostasis: The body maintains safe constant heat and fluids. Dehydration is a major risk.",
                "Body Temperature: Higher in daytime, lower at night. Non-paralyzed armpit is used.",
                "Respiration: Breathing takes in oxygen and clears CO2. Bluish nails/lips indicate oxygen lack, called cyanosis.",
                "Pulse & Blood Pressure: Hypertension increases risk of stroke. Sympathetic is active; Parasympathetic relaxes."
            )
        ),
        StudyChapter(
            7, "Part 2: Mind & Body", 2, "Vital Signs & Temperature",
            "Vital signs signify dynamic life status. Proper parameters evaluation saves lives directly when incidents trigger.",
            "バイタルサインは生命の活動レベルを示します。正確な測定手順と基準値の把握が必須です。",
            listOf(
                "Normal Body Temperature: Average 36.0 to 37.0 degrees. Keep armpit closed tight for measurements.",
                "Normal Pulse: 60 to 90 beats per minute. Tachycardia (fast) indicates fever, anxiety, or bleeding.",
                "Normal Blood Pressure: Systolic under 130 mmHg, Diastolic under 85 mmHg. Avoid measurements right after exercise.",
                "Abnormal Indicators: If respiratory rate is irregular, or blood pressure drops abruptly, call the nurse instantly."
            )
        ),
        StudyChapter(
            8, "Part 2: Mind & Body", 3, "Understanding Dementia (Ninchishou)",
            "Dementia is a brain disease leading to decline in cognitive functions. Understand core symptoms vs BPSD behavioral symptoms.",
            "認知症は、脳の病気により認知機能が低下する状態です。中核症状と行動・心理症状（BPSD）の違いを学びます。",
            listOf(
                "Core Symptoms: Amnesia (memory loss), Disorientation (lost track of date, place, person), Impaired judgment.",
                "BPSD: Anxiety, depression, wandering, hallucinations, agitation. BPSD is triggered by stress or unmet physical desires.",
                "Standard Response: Avoid scolding or correcting their distorted reality. Validate, listen, and assure safety.",
                "Major Types: Alzheimer's (gradual decline, memory), Vascular (stepwise decline, paralysis risks), Lewy Body (hallucinations)."
            )
        ),
        StudyChapter(
            9, "Part 2: Mind & Body", 4, "Aging Process & Sensory Changes",
            "With advanced age, sensory organs and homeostatic controls degrade gradually, requiring protective environmental safety.",
            "加齢に伴う感覚機能の変化では、聴覚・視覚の低下、温熱感覚の鈍化に対する配慮が必要です。",
            listOf(
                "Vision Declines: Narrowing of field, cloudy lens (Cataracts). Yellow tones are easiest to see, blue/black tones hard.",
                "Hearing Losses: Difficulty hearing high-pitched voices. Speak in a low pitch, clearly, and face them directly.",
                "Temperature Sense: Elderly feel heat/cold less easily. Do not rely on their feedback; monitor room thermometer directly.",
                "Taste & Thirst: Thirst reflex declines, inviting silent dehydration. Prompt fluid intake on schedule."
            )
        ),
        StudyChapter(
            10, "Part 2: Mind & Body", 5, "Common Geriatric Diseases",
            "This covers vascular accidents, fractures, and organic disorders frequently encountered in nursing facilities.",
            "高齢者に多い疾病には、脳血管障害（脳卒中）、糖尿病、骨粗鬆症による骨折があります。",
            listOf(
                "Brain Vascular Accidents: Stroke or cerebral hemorrhage. Causes sudden hemiplegia on one side of the body.",
                "Osteoporosis: Brittle bones. A simple trip can lead to a femoral neck fracture, causing prolonged bedridden status.",
                "Diabetes: Poor blood sugar regulation. Symptoms include high thirst and poor wound healing. Check feet for sores daily.",
                "Pneumonia: High risk for users with chewing failures due to silent food particles slipping into windpipes."
            )
        ),

        // === PART 3: COMMUNICATION SKILLS (11-15) ===
        StudyChapter(
            11, "Part 3: Communication", 1, "Basics of Communication",
            "Communication builds mutual trust. Care workers must master both verbal and non-verbal skills to support users effectively.",
            "コミュニケーション技術は、信頼関係を築く基礎です。言語表現と言語以外の手段を効果的に組み合わせます。",
            listOf(
                "Verbal Communication: Using spoken words, writing, or communication boards explicitly.",
                "Non-Verbal Communication: Facial expressions, posture, eye contact, touch, and tone of voice.",
                "Physical Proximity: Sit at the user's eye level (horizontal alignment). Avoid standing over them, which can feel threatening.",
                "Open Questions: Ask open-ended questions for deep expression, and use closed 'Yes/No' questions for users with limited speech."
            )
        ),
        StudyChapter(
            12, "Part 3: Communication", 2, "Active Listening & Empathy",
            "Listening deeply (Keichou) promotes emotional peace, helping users feel secure and integrated.",
            "傾聴（Keichou）と共感（Empathy）は利用者の心の不安を和らげ、信頼関係を強固にする核心です。",
            listOf(
                "Keichou: Listen with total attention, nodding periodically. Avoid interrupting, correcting, or judging their stories.",
                "Empathy: Mirror and voice back their emotions (e.g., 'You must have felt very anxious when that happened').",
                "Non-judgmental: Maintain absolute respect even if their memories are confused or historical timeline is inaccurate.",
                "Silence: Do not rush to fill quiet moments. Give the user time to gather their thoughts and form sentences."
            )
        ),
        StudyChapter(
            13, "Part 3: Communication", 3, "Team Dynamics & Ho-Ren-So",
            "Nursing care is teamwork. High speed relaying of indicators prevents accidents and coordinates optimal services.",
            "チームケアと報告・連絡・相談（ホウレンソウ / Ho-Ren-So）は、事故を防ぎ、質の高い介護を提供する鍵です。",
            listOf(
                "Houkoku (Report): Report actions and physical changes to your supervisor or nurses immediately. State facts first, opinions second.",
                "Renraku (Contact): Share shift updates, daily schedules, and logistical changes across the whole care team.",
                "Soudan (Consult): Seek expert feedback from specialists when facing a difficult care challenge or ethical choice.",
                "Objective Records: Write what you observed, not what you guessed. (e.g. 'Slept 3 hours' rather than 'Looked tired')."
            )
        ),
        StudyChapter(
            14, "Part 3: Communication", 4, "Aphasia & Hearing Impairments Support",
            "Specialized communication methods are required for users who have lost language abilities due to strokes or aging ears.",
            "失語症や聴覚障害を持つ利用者との意思疎通では、代替手段や物理的工夫を凝らします。",
            listOf(
                "Aphasia Support: Speak in simple sentences, use gestures, pointing boards, and give them ample time to reply without pressure.",
                "Hearing Impairment: Face the user directly, speak in a lower pitch, move away from background noise, and write down key words.",
                "Visual Impairment: Address them by name before touching, and describe positions using the clock system (e.g. 'The tea is at 2 o\\'clock').",
                "Non-verbal matching: Maintain a gentle face to ensure peace even if they cannot understand the spoken words."
            )
        ),
        StudyChapter(
            15, "Part 3: Communication", 5, "Records & Information Sharing",
            "Care logs serve as legal evidence, scientific reference, and coordinate cohesive clinical goals across multi-discipline shifts.",
            "介護記録の作成と共有は、情報の連続性を維持し、スタッフ間の連携を高め、過誤を排除します。",
            listOf(
                "Timely Logging: Record events as soon as possible after they occur to maintain accuracy of numbers/times.",
                "Legal Evidence: Keep logs objective, clear, and neat. Avoid subjective descriptions or personal complaints.",
                "Information Privacy: Keep screens locked, and never read medical charts outside your designated duty hours.",
                "Coordinated Shift Change: Review logs thoroughly during handovers to guarantee seamless safety across morning/night cycles."
            )
        ),

        // === PART 4: SKILLS FOR NURSING CARE ASSISTANCE (16-21) ===
        StudyChapter(
            16, "Part 4: Skills for Care", 1, "Meal & Hydration Assistance",
            "Eating orally stimulates the brain and provides life satisfaction. Assist with accurate posture and food thickness.",
            "食事介助では、食事前に口を湿らせ、あごを引いた安定姿勢を取り、一口ごとに飲み込みを慎重に確認します。",
            listOf(
                "Choking Prevention (Goen): Keep bed elevated to 30-60 degrees. Maintain neck in slight flexion with chin tucked.",
                "Hydration Balance: Proactively administer fluids on schedule. Dehydration causes confusion, dizziness, and urinary tract infections.",
                "Feeding Assistance: Sit at their eye level, explain the menu beforehand, and feed only from the unaffected side.",
                "Oral Hygiene: Clean teeth, gums, and dentures after meals to suppress respiratory bacteria that trigger pneumonia."
            )
        ),
        StudyChapter(
            17, "Part 4: Skills for Care", 2, "Daily Assistance: Excretion Care",
            "Excretion is a highly private function. Maintaining dignity, ensuring safety, and detecting abnormalities early are core parts.",
            "排泄介助はプライバシーの塊です。尊厳を保持し、転倒を防ぎ、排泄物の色・硬さから異常を早期に検出します。",
            listOf(
                "Dignity Protocols: Keep the door/curtains fully closed, use drapes to cover legs, and clean up spills immediately.",
                "Portable Toilets: Stand wheelchair or portable toilet at 30-45 degrees near their healthy hand. Feet must rest flat.",
                "Diaper Changes: Choose the correct size. Pulling diaper tabs too tightly constricts breath and damages skin.",
                "Observation of Output: Check stool for color (black/red indicates bleeding) and urine for turbidity (turbid indicates UTI)."
            )
        ),
        StudyChapter(
            18, "Part 4: Skills for Care", 3, "Transfer & Mobility Support",
            "Mobility support increases physical stimulation, maintains muscle mass, and expands the user's social world.",
            "車いす移乗では、ブレーキのダブルロック確認、フットサポート（足置き台）の跳ね上げ、自立的な前傾姿勢への誘導が要です。",
            listOf(
                "Brake Locking: Always double-lock wheelchair brakes and fold up footrests before the user stands or sits.",
                "Wheelchair Angle: Place wheelchair on their unaffected side at an angle of 30 to 45 degrees relative to the bed.",
                "Center of Gravity: Prompt the user to bend forward, bringing their head over their toes, to easily shift weight during standing.",
                "Movement Assist: Stand on their affected side, hold their torso gently, and never pull on a paralyzed arm or shoulder."
            )
        ),
        StudyChapter(
            19, "Part 4: Skills for Care", 4, "Daily Assistance: Bathing Care (Nyuyoku)",
            "Bathing cleanses the skin, promotes blood circulation, and provides mental refreshment. Safety in wet environments is paramount.",
            "入浴介助では、脱衣所と浴室の温度差によるヒートショック（Heat Shock）の予防、滑り防止、安全な浴槽の出入が基本です。",
            listOf(
                "Heat Shock Prevention: Keep temperatures balanced (not too cold in dressing room, not too hot in tub: keep below 41°C).",
                "Assisting Sequence: Wash from extremities first, working toward center. Always check water temperature with your hand first.",
                "Bathroom Slipping: Keep floors dry and clear of soapy water. Use non-slip mats and prompt users to hold onto secure handrails.",
                "Physical Watch: Monitor face color during bath. Restrict tub soaking time to 5 minutes to prevent dehydration or fainting."
            )
        ),
        StudyChapter(
            20, "Part 4: Skills for Care", 5, "Daily Assistance: Dressing Care (Dressing)",
            "Adhering to correct dressing techniques preserves joint mobility and respects their self-determination.",
            "着脱介助では、「脱健着患（だっけんちゃっかん）」の原則を守り、利用者が着たい服を選ぶ決定権を尊重します。",
            listOf(
                "Dakken Chakkan Principle: Undress the unaffected (healthy) side first, dress the affected (paralyzed) side first.",
                "Joint Safety: Protect contractured joints by supporting the limb directly and avoiding forced stretching.",
                "Drapery Protocol: Maintain body warmth and dignity during dressing by covering exposed areas with a bath towel.",
                "Self-care Encouragement: Let the user fasten buttons or pull sleeves with their healthy hand as much as they can."
            )
        ),
        StudyChapter(
            21, "Part 4: Skills for Care", 6, "Emergency Actions & First Aid",
            "First aid response saves life when accidents happen. Caregivers must stay calm, call for help, and follow clinical guides.",
            "緊急時の対応では、意識障害・誤嚥窒息・急激な発熱・転倒骨折に対する迅速な呼吸確認と看護師への報告が命です。",
            listOf(
                "Choking Emergency: Do back blows (Heimlich) immediately to lodge out blocked boluses while calling secondary emergency.",
                "Fall Incidents: Do not move the patient immediately. Check consciousness, inspect head/limbs, and alert the nurse on duty.",
                "Heart Attack: If breathing/pulse ceases, initiate CPR and get the AED immediately. Do not leave the user alone.",
                "Seizures: Prevent head injuries with soft pillows, loosen tight clothing around neck, turn head sideways, do NOT stick anything in mouth."
            )
        )
    )

    val quizQuestions = listOf(
        // === MCQ QUESTIONS (1-15, GENERAL) ===
        QuizQuestion(
            1, QuizType.GENERAL,
            "ボディメカニクス（Body Mechanics）の原則について、介護職員の腰痛予防として【誤っているもの】を選んでください。",
            "Regarding the principles of Body Mechanics for preventing caregiver back pain, choose the item that is 【INCORRECT】.",
            listOf(
                "支持基底面を広く取る (Maintain a wide base of support)",
                "重心を高く設定する (Set the center of gravity high)",
                "利用者の身体を介護者自身の重心に近づける (Keep the user close to your center of gravity)",
                "体幹（トルソー）をひねらずに、足先を移動方向に向ける (Set toes toward the moving direction without twisting)"
            ),
            1,
            "Set the center of gravity low (by bending the knees) to stabilize posture and reduce back strain. Supporting on a wide base and keeping the user close to your torso are correct principles."
        ),
        QuizQuestion(
            2, QuizType.GENERAL,
            "感染予防（Standard Precautions）について最も適切な記述を選んでください。",
            "Regarding infection prevention (Standard Precautions), choose the most appropriate description.",
            listOf(
                "感染症の診断名がついた人のみ、血液や体液を感染源として扱う (Treat blood/fluids as infectious only if diagnosed)",
                "手洗いは、手袋を着用していれば省略できる (Hand washing can be omitted if you wear gloves)",
                "すべての人の血液、排泄物、体液を潜在的に感染性があるものとして扱う (Treat all patients' blood and body fluids as potentially infectious)",
                "使い捨て手袋は、何人かの介助が終わるまで交換しなくてよい (Disposable gloves do not need to be changed until several users are finished)"
            ),
            2,
            "Standard Precautions dictate that we must treat all blood, bodily fluids, secretions, excretions, non-intact skin, and mucous membranes of ALL patients as potentially infectious, regardless of diagnosis."
        ),
        QuizQuestion(
            3, QuizType.GENERAL,
            "左半身に片麻痺（Hemiplegia）がある利用者の着替え（更衣）介助で、正しい法則を選んでください。",
            "Choose the correct principle for assisting a user with left hemiplegia to change clothes.",
            listOf(
                "麻痺のある左側から脱がせ、麻痺のない右側から着せる (Undress from the affected left side, dress from the unaffected right side)",
                "麻痺のない右側から脱がせ、麻痺のある左側から着せる (Undress from the unaffected right side, dress from the affected left side)",
                "麻痺のある左側から脱がせ、左側から着せる (Undress from affected, dress from affected)",
                "麻痺のない右側から脱がせ、右側から着せる (Undress from unaffected, dress from unaffected)"
            ),
            1,
            "The principle is 'Dakken Chakkan' (脱健着患): 'Undress unaffected (healthy) side, Dress affected (paralyzed) side'. Since left is affected, undress right (healthy) first; dress left (affected) first."
        ),
        QuizQuestion(
            4, QuizType.GENERAL,
            "認知症（Dementia）の周辺症状（BPSD）について適切な記述を選択してください。",
            "Choose the correct description regarding Dementia's behavioral and psychological symptoms (BPSD).",
            listOf(
                "記憶力の低下は、BPSDの代表的な一例である (Decline in memory is a representative example of BPSD)",
                "見当識の障害は、加齢自体で起こるものでBPSDではない (Disorientation is caused by aging and is not BPSD)",
                "不安や徘徊、抑うつは周囲の環境や心理状態で変動するBPSDである (Anxiety, wandering, and depression are BPSD that vary with environment and mental state)",
                "BPSDは脳細胞の直接の死滅により、治療やケアに関わらず常に不変である (BPSD is invariant regardless of care)"
            ),
            2,
            "BPSD (wandering, anxiety, aggression, depression) is triggered by psychological distress, direct unmet needs, or sensory stress, and can be mitigated by high-quality personalized care, unlike core irreversible symptoms."
        ),
        QuizQuestion(
            5, QuizType.GENERAL,
            "高齢者の視覚の変化について正しいものを一つ選択してください。",
            "Choose the correct description about visual changes in advanced age.",
            listOf(
                "青色や黒色の区別が容易になる (Blue and black colors become easier to distinguish)",
                "視野が狭まり、薄暗い場所での視力が著しく低下する (The visual field narrows, and visibility in dark surroundings decreases heavily)",
                "レンズが透明になりすぎて光が乱反射する (The lens becomes completely transparent, scattering light)",
                "赤色や黄色が最も判別しにくくなる (Red and yellow colors become the most difficult to distinguish)"
            ),
            1,
            "In elderly eyes (especially with cataracts), the lens clouds up and yellows. This diminishes blue/dark discrimination, narrows visual fields, and increases glare significantly, demanding bright yellow-contrasted environmental markings."
        ),
        QuizQuestion(
            6, QuizType.GENERAL,
            "ポータブルトイレ（Portable Toilet）を片麻痺の利用者に設置する際の、適切な車いすの角度と位置を選んでください。",
            "Choose the correct angle and position for setup of a portable toilet for a hemiplegic user.",
            listOf(
                "利用者の麻痺側（患側）に、ベッドに対して 90度で設置する (Position on the affected side at 90 degrees)",
                "利用者の非麻痺側（健側）に、ベッドに対して 30〜45度で設置する (Position on the unaffected side at 30 to 45 degrees)",
                "利用者の健康な足の後ろ側に、逆向きに設置する (Position behind the user's healthy foot in reverse direction)",
                "ベッドから離して、カーテンの裏に設置する (Position far from the bed, behind the privacy curtain)"
            ),
            1,
            "To maximize safety and maintain autonomy, place the portable toilet at a 30 to 45 degree angle on the user's healthy side. This allows them to hold onto bedside rails with their strong hand."
        ),
        QuizQuestion(
            7, QuizType.GENERAL,
            "排泄介助における「尊厳の保持」とプライバシー保護について、最も不適切な記述を選んでください。",
            "Regarding excretion assistance and 'maintenance of dignity' / privacy, choose the most 【INCORRECT】 description.",
            listOf(
                "利用者が排泄している間、バスタオル等で下半身を覆う (Cover the lower body with a towel while user is excreting)",
                "排便の様子や臭いについて、廊下や他人の前で大きな声で発表しない (Do not announce details about bowel movements or odors loudly)",
                "排泄の失敗を防ぐため、常に事前にオムツをして自分でトイレに行く機会を全て奪う (Enforce diapers to avoid all accidents, preventing any toilet visits)",
                "ポータブルトイレを使用する際は、ドアやカーテンを閉めて個室を作る (Close door/curtains to create a private room)"
            ),
            2,
            "Strictly forcing an active user into diaper usage solely to simplify caregiver labor ruins their self-competency and strips their dignity. Coordinate toilet independence where possible."
        ),
        QuizQuestion(
            8, QuizType.GENERAL,
            "高齢者の「ヒートショック（Heat Shock）」を防ぐ入浴介助として【最も適切なもの】を一つ選んでください。",
            "Regarding bathing assistance to prevent 'Heat Shock' in elderly users, choose the 【MOST APPROPRIATE】 action.",
            listOf(
                "脱衣所をあらかじめ暖房で暖めて、浴室との温度差を少なくしておく (Pre-heat the dressing room to minimize the temperature gap with the bathroom)",
                "一気に熱い44度の湯船に浸からせる (Instantly soak the patient in hot water of 44 degrees C)",
                "入浴直前に水分をたくさん抜いておく (Severely restrict fluid intake inside the patient right before bathing)",
                "脱衣所の窓を全開にして、吹き込む風で体を冷却する (Open dressing room windows fully to cool body with drafts)"
            ),
            0,
            "Heat Shock is triggered by sudden temperature shifts, provoking heavy blood pressure changes and heart failures. Always warm the dressing room before bathing to minimize gaps."
        ),
        QuizQuestion(
            9, QuizType.GENERAL,
            "車いすを押して「スロープを下る際」の、安全な介助手順を選んでください。",
            "Choose the correct safety procedure for pushing a wheelchair DOWN a ramp.",
            listOf(
                "車いすを前向きにしたまま、一気に勢いよく下りきる (Keep the wheelchair facing forward and dash straight down rapidly)",
                "車いすを後ろ向きにし、介助者が後ろに立って自分の体を支えながら、ゆっくり下がる (Turn the wheelchair backward; the caregiver walks backward slowly while supporting the weight)",
                "車いすを斜めに横滑りさせて下ろす (Slide the wheelchair sideways down)",
                "利用者に立ち上がってもらい、車いすを空にして落とす (Ask the user to stand up, then run the empty wheelchair down)"
            ),
            1,
            "When going down a ramp, the wheelchair must be turned backward to prevent the user from falling forward out of the seat or experiencing intense fear. The caregiver must reverse and walk down backward slowly."
        ),
        QuizQuestion(
            10, QuizType.GENERAL,
            "高齢者が「脱水状態（Dehydration）」にあることを疑わせる身体所見として不適切なものを選択してください。",
            "Choose the body inspection finding that is 【INCORRECT】 for suspecting dehydration in older adults.",
            listOf(
                "皮膚の乾燥や、弾力性の低下 (Dry skin and loss of elasticity)",
                "尿量の著しい増加、または無色透明な尿 (Significant increase in urine volume, or crystal clear urine)",
                "腋窩（わきの下）の乾燥 (Completely dry armpits)",
                "意識の混濁、眠気が強くなる (Clouding of consciousness or strong sleepiness)"
            ),
            1,
            "Dehydration leads to a significant decrease in urine output, and the urine becomes concentrated, showing a dark yellow or orange color, not crystal clear."
        ),
        QuizQuestion(
            11, QuizType.GENERAL,
            "介護における食事介助で「誤嚥（Goen）を防ぐため」にとるべき姿勢として正しいものを選んでください。",
            "Choose the correct eating posture to prevent aspiration (Goen) during feeding assistance.",
            listOf(
                "あご（顎）を少し引き、やや前傾の姿勢をとる (Tuck the chin forward/downward, maintaining a slightly forward leaning posture)",
                "頭を後ろに反らせて、喉元を伸ばす (Tilt the head backward, fully stretching the throat out)",
                "完全に水平に仰向けになった（仰臥位）姿勢をとる (Take a completely flat, horizontal supine position)",
                "横向きで枕を高くして、首をひねる (Lie on one side with high pillows, heavily twisting the neck)"
            ),
            0,
            "Tucking the chin slightly down narrows the entry to the trachea and widens the esophagus, ensuring safer swallowing. Tilting the head back stretches the airway open, which invites choking."
        ),
        QuizQuestion(
            12, QuizType.GENERAL,
            "利用者の「自己決定（Self-determination）」を支援する介護スタッフの行動として、理想的なものを選んでください。",
            "Choose the ideal action for supporting a user's 'self-determination' by care staff.",
            listOf(
                "時間短縮のために、介護者が当日の服や靴をすべて選んで強制する (Caregiver chooses and enforces all clothes/shoes to save time)",
                "利用者が自分で選ぶまで、安全な選択肢を提示しながら優しく待ち、言葉を重ねる (Gently guide them with safe alternatives, wait comfortably, and let them choose)",
                "利用者は正しい決定ができないと考えて、意見を反映しない (Assume the user cannot make correct choices and ignore their opinions)",
                "家族の決定のみを100%基準にし、本人の言葉を制止する (Rely purely on family decisions, telling the user to be quiet)"
            ),
            1,
            "Independence starts with small everyday choices (e.g., clothes, food). Assisting with visual choices and waiting patient-first respects their self-determination and boosts cognitive confidence."
        ),
        QuizQuestion(
            13, QuizType.GENERAL,
            "口腔ケア（Oral Hygiene）を行う「主目的」として最も不適切なものを選んでください。",
            "Choose the most 【INCORRECT】 statement regarding the main objectives of oral hygiene.",
            listOf(
                "虫歯や歯周病の予防 (Prevention of tooth decay and periodontal disease)",
                "誤嚥性肺炎（Aspiration Pneumonia）の予防 (Prevention of aspiration pneumonia)",
                "胃や腸を完全に消毒すること (Completely sterilizing the inner stomach and intestines)",
                "唾液分泌の促進や、味覚の維持 (Stimulation of saliva secretion and maintenance of taste)"
            ),
            2,
            "Oral care reduces harmful oral bacteria, pre-emptively cutting down risks of fatal aspiration pneumonia in weak seniors. It cannot disinfect internal gastrointestinal organs."
        ),
        QuizQuestion(
            14, QuizType.GENERAL,
            "高齢者の血圧測定時、測定値が高く出やすい条件として適切なものを一つ選んでください。",
            "Choose the condition that is prone to produce artificially HIGHER blood pressure readouts in elderly.",
            listOf(
                "３０分以上安静にした状態での測定 (Measuring after resting quietly for more than 30 minutes)",
                "排尿をすませた後の、リラックスした状態での測定 (Measuring right after urination in a relaxed state)",
                "コーヒーなどのカフェインを飲んだ直後や、排尿を我慢している状態 (Right after drinking caffeine or while holding back urination)",
                "室温が２４度で、穏やかに静止している状態 (Quietly resting at a comfortable room temperature of 24 degrees C)"
            ),
            2,
            "Caffeine, physical pain, high psychological stress, and having a full bladder trigger the sympathetic nervous system, causing blood pressure readings to spike significantly."
        ),
        QuizQuestion(
            15, QuizType.GENERAL,
            "車いすの「フットサポート（足置き台）」に関する安全管理で正しい記述を選択してください。",
            "Regarding safety control of wheelchair foot supports, choose the correct description.",
            listOf(
                "利用者が車いすから立ち上がる際、足先をフットサポートにのせたまま行う (Let the user stand up while keeping their feet placed on the foot support)",
                "立ち上がる前に必ずフットサポートを跳ね上げて、両足を床（床面）に密着させて行う (Always fold up the foot support and ensure both feet contact the floor flat before they stand up)",
                "フットサポートは、移動時に障害物にぶつかるよう低く設定する (Adjust foot supports low so they crash into floor joints during transport)",
                "車いす走行中は、足を踏み外しやすくするために足元から浮かせて乗せる (Keep feet hovering above foot supports when traveling)"
            ),
            1,
            "Stepping directly on a foot support to stand up shifts the center of gravity forward, causing the wheelchair to tilt and crash. Always fold them up, ensuring soles are flat on the floor before vertical rises."
        ),

        // === CONVERSATIONAL MOCK DIALOGUES QUESTIONS (16-30) ===
        QuizQuestion(
            16, QuizType.CONVERSATIONAL,
            "佐藤さんの回答から、佐藤さんは今日どのように歩く練習をすることに決めましたか。会話の内容として正しいものを選んでください。",
            "According to the conversation with Satou-san, how did they decide to practice walking today? Choose the correct option.",
            listOf(
                "佐藤さんは、体調が良いので歩く練習をスキップすることに決めた (Satou-san decided to skip walking practice because they feel bad)",
                "佐藤さんは、今日は杖を使って歩くことにした (Satou-san decided to walk using a cane today)",
                "佐藤さんは、今日は手すりにつかまって歩いてみることにした (Satou-san decided to walk holding onto the handrail today)",
                "佐藤さんは、一人で立位を維持することにした (Satou-san decided to stand up alone)"
            ),
            2,
            "According to the dialogue, when asked if they use a cane, Satou-san replies 'いいえ。きょうは手すりにつかまって歩いてみます' (No, today I will try walking holding onto the handrail). Therefore, Option 3 is correct.",
            listOf(
                DialogLine("Care Worker", "佐藤さん、体調はどうですか。", "Satou-san, how is your physical condition?"),
                DialogLine("Satou-san", "悪くないです。", "I am doing not bad."),
                DialogLine("Care Worker", "これから歩く練習をしましょうか。", "Shall we practice walking now?"),
                DialogLine("Satou-san", "そうですね。", "Yes, indeed."),
                DialogLine("Care Worker", "杖を使いますか。", "Will you use your cane?"),
                DialogLine("Satou-san", "いいえ。きょうは手すりにつかまって歩いてみます。", "No. Today I will try walking by holding onto the handrail.")
            )
        ),
        QuizQuestion(
            17, QuizType.CONVERSATIONAL,
            "鈴木さんとの会話の内容で、正しいものを選んでください。（排泄ケアと起き上がり）",
            "Based on the conversation with Suzuki-san, choose the correct description of the events.",
            listOf(
                "鈴木さんは、一人で起き上がった (Suzuki-san rose out of bed entirely by themselves)",
                "鈴木さんは、手伝ってもらい、きちんと靴を履くことができた (Suzuki-san successfully put on their shoes with assistance)",
                "鈴木さんは、車いすに乗るのを拒否した (Suzuki-san refused to get into the wheelchair)",
                "鈴木さんは、一人でお風呂に行ってしまった (Suzuki-san went to the bath alone)"
            ),
            1,
            "In the dialogue, when offered assistance to put on shoes, Suzuki-san says 'お願い' (Please), and puts them on carefully. Therefore, they did it with assistance."
            ,
            listOf(
                DialogLine("Suzuki-san", "トイレに行きたいんだけど。", "I want to go to the toilet."),
                DialogLine("Care Worker", "わかりました。いっしょに行きましょう。ベッドから起き上がれますか。", "All right. Let's go together. Can you rise from the bed?"),
                DialogLine("Suzuki-san", "ゆっくりやってみるよ。", "I'll try doing it slowly."),
                DialogLine("Care Worker", "あ、できましたね。靴を履きましょう。お手伝いしましょうか。", "Ah, you did it! Let's put on your shoes. Shall I assist you?"),
                DialogLine("Suzuki-san", "お願い。", "Please.")
            )
        ),
        QuizQuestion(
            18, QuizType.CONVERSATIONAL,
            "佐藤さんとの会話から、介護職員はどうすることにしましたか。適切な行動を選んでください。",
            "Based on the conversation with Satou-san, what did the care worker decide to do? Choose the appropriate action.",
            listOf(
                "佐藤さんが自分で起きるまで一人で放置することにした (Decided to leave Satou-san alone until they wake up naturally)",
                "窓を開けて部屋を涼しくすることに決めた (Decided to open windows to make the room cooler)",
                "暖房をつけて室内温度を確認し、暖かいお茶を用意することにした (Decided to check indoor heat, adjust heating, and prepare warm tea)",
                "直ちに医師を呼び出して救急搬送することにした (Decided to immediately call an emergency doctor for transport)"
            ),
            2,
            "Satou-san declares they are cold and shivering. The care worker immediately shuts the windows, checks the heating system (暖房を少し強めますね), and promises to get them some hot green tea (温かいお茶を持ってきます). Therefore, Option 3 is correct.",
            listOf(
                DialogLine("Care Worker", "佐藤さん、おはようございます。よく眠れましたか。", "Satou-san, good morning. Did you sleep well?"),
                DialogLine("Satou-san", "よく眠れたけど、なんだか寒いです。からだが震えます。", "I slept well, but somehow I feel cold. My body is shivering."),
                DialogLine("Care Worker", "からだが震えていますね。室温を確かめます。あ、風が入っていますね。窓を閉めましょう。", "Your body is shivering. Let me verify the room temperature. Ah! A draft is entering. Let's close the window."),
                DialogLine("Satou-san", "ありがとう。", "Thank you."),
                DialogLine("Care Worker", "暖房を少し強めますね。それから、温かいお茶を持ってきます。上着も羽織りますか。", "I will increase the heating slightly. Then, I will fetch some warm tea. Shall we also wrap a warm jacket on you?"),
                DialogLine("Satou-san", "そうしてください。お願いします。", "Yes, please do that. Thank you.")
            )
        ),
        QuizQuestion(
            19, QuizType.CONVERSATIONAL,
            "田中さんとの食事介助の会話で、利用者の「安全」を確保するための適切な行動について正しい説明を選択してください。",
            "Based on the conversation during meal assistance with Tanaka-san, choose the correct description of the safe clinical intervention.",
            listOf(
                "ベッドを完全に平らにしたままスプーンで急いで食べさせた (Hurriedly fed them with a spoon while keeping the bed completely flat)",
                "ベッドの背もたれを30度（またはそれ以上）起こして、顎（あご）を引いた姿勢にして、飲み込みを確認しながら介助する (Elevate head of bed to 30 degrees or more, tuck chin downward, observe swallowing)",
                "食事を一口で全部噛まずに飲み込むよう促した (Instructed them to swallow everything in one bite without chewing)",
                "お茶にとろみをつけずに、むせている状態のまま一気に飲ませた (Gave tea rapidly without thickening while they were coughing)"
            ),
            1,
            "Elevation of bed backrest reduces aspiration risks. Swallowing must be observed meticulously, feeding only from the unaffected side slowly."
            ,
            listOf(
                DialogLine("Care Worker", "田中さん、お昼ご飯の時間ですよ。ベッドを少し起こしましょうか。", "Tanaka-san, it's lunch time. Shall we raise the head of your bed slightly?"),
                DialogLine("Tanaka-san", "寝たままでいいよ、面倒くさいから。", "No, keeping lying down is fine, it's too much trouble otherwise."),
                DialogLine("Care Worker", "寝たまだと、食べ物が肺のほうに入って、誤嚥（ごえん）してしまいます。30度くらい起こしてみましょう。楽になりますよ。", "If you eat lying flat, meals might enter into your lungs and cause choking (aspiration). Let's raise it to about 30 degrees. You'll feel comfortable!"),
                DialogLine("Tanaka-san", "少しだけなら、いいよ。", "If it's just a little bit, then fine."),
                DialogLine("Care Worker", "ありがとうございます。はい、起こしました。顎を少し引いて、まずはスプーン一口からゆっくり始めましょう。", "Thank you. There! It is elevated. Tuck your chin slightly forward, let's start slowly with a single spoonful first.")
            )
        ),
        QuizQuestion(
            20, QuizType.CONVERSATIONAL,
            "高橋さんとの会話から、車いす移動中に発生した問題と、その安全対応として正しい記述を選んでください。",
            "Based on the conversation with Takahashi-san during wheelchair transport, choose the correct description of the problem and its safety response.",
            listOf(
                "車いすのタイヤがパンクしたが、そのまま走り続けた (The tire went flat, but they continued pushing the wheelchair anyway)",
                "利用者の足がフットレトから落ちていたため、すぐに停止してブレーキをかけ、足を正しい位置に戻した (The user's foot had fallen off the footrest, so the worker stopped immediately, applied the brakes, and positioned the foot safely back)",
                "利用者が眠ったため、シートベルトを外して揺らした (The user slept, so they unfastened the seatbelt and shook them)",
                "目的地を間違えたため、急カーブで引き返した (Took the wrong destination, so they turned back in a very sharp, sudden curve)"
            ),
            1,
            "If a user's foot slips off the footrest during transport, it risks being caught by tires. Always halt instantly, lock brakes, and reposition their soles properly."
            ,
            listOf(
                DialogLine("Care Worker", "高橋さん、これから談話室へ行きましょう。", "Takahashi-san, let's head over to the dayroom now."),
                DialogLine("Takahashi-san", "天気がいいね。", "The weather is lovely!"),
                DialogLine("Care Worker", "そうですね。お、高橋さん、ちょっと待ってください。右足がフットレスト（足のせ台）からすべり落ちていますよ。ストップしますね。", "It is! Oh, Takahashi-san, please wait a moment. Your right foot is slipping down off the footrest. Let's stop."),
                DialogLine("Takahashi-san", "あれ、本当だ。気づかなかった。", "Oh, you are right! I didn't notice it."),
                DialogLine("Care Worker", "はい、車いすを停めて、ブレーキをかけました。足をフットレストの上に置き直しますね。大丈夫ですか。よろしい、では進みます。", "Alright, I've parked the wheelchair and locked the brakes. Let me place your foot back on top of the footrest safely. Are you okay? Excellent, let's proceed.")
            )
        ),
        QuizQuestion(
            21, QuizType.CONVERSATIONAL,
            "加藤さんとの水分補給に関する会話で、安全に関する正しい事実を選んでください。",
            "Based on the conversation with Katou-san regarding hydration, choose the correct safety statement.",
            listOf(
                "加藤さんはお茶が嫌いだが、介護者が無理やり飲ませた (Katou-san hates tea, but the caregiver forced them to drink it)",
                "誤嚥（Aspiration）を防ぐために、お茶にとろみ粉（thickener）を使い、ゆっくり飲んでもらった (To prevent aspiration, thickener was added to the tea, and they drank slowly)",
                "冷たいお茶をごくごく一気に飲ませた (Fed cold tea extremely fast at once)",
                "脱水を防ぐ必要はないと言われ、水分提供を中止した (Told hydration is unnecessary and stopped providing fluids)"
            ),
            1,
            "Katou-san is prone to coughing when swallowing thin liquids. The caregiver suggests using 'とろみ' (Toromi / thickener) to slow the liquid flow down. Katou-san agrees to try it for safe swallowing.",
            listOf(
                DialogLine("Care Worker", "加藤さん、お茶をお持ちしました。水分補給しましょう。", "Katou-san, I brought some green tea. Let's hydrate."),
                DialogLine("Katou-san", "ありがとう。でも、最近お茶を飲むと、少しむせる（せき込む）んだよね。", "Thank you. But recently, when I drink tea, I cough (choke) a bit."),
                DialogLine("Care Worker", "そうなのですね。では、誤嚥を防ぐために、お茶にとろみをつけてみましょうか。飲み込みやすくなりますよ。", "I see. Then, to prevent aspiration, shall we add some thickener (Toromi) to the tea? It will become much easier to swallow, you know."),
                DialogLine("Katou-san", "とろみ？うまい具合に飲み込めるかい？", "Thickener? Will I be able to swallow it properly?"),
                DialogLine("Care Worker", "はい、少しとろみをつけるとゆっくり流れるので、むせずに飲めますよ。ご用意しますね。", "Yes, adjusting it with a bit of thickener slows the flow down, so you can drink without coughing. Let me prepare it.")
            )
        ),
        QuizQuestion(
            22, QuizType.CONVERSATIONAL,
            "小林さんとの入浴介助の会話から、ヒートショック（Heat Shock）や転倒を防ぐ安全対策として記述がふさわしいものを選んでください。",
            "Based on the conversation concerning bathing with Kobayashi-san, choose the correct safety step achieved.",
            listOf(
                "浴槽の温度を45度にして体を一気に温めた (Boiled the bathtub to 45 degrees C to heat the body instantaneously)",
                "脱衣所を暖房であらかじめ温め、浴槽に入る前にかけ湯をして、手すりを持ってゆっくり入ってもらった (Pre-warmed dressing room, used splash-pour water to adapt skin before entry, and guided them using handrails slowly)",
                "床が濡れているが、そのまま早く走るよう指示した (Told them to sprint across wet bathroom floors quickly)",
                "湯船に1時間以上浸かり続けるよう勧めた (Recommended soaking in the tub for over an hour)"
            ),
            1,
            "To prevent sudden pressure spikes (Heat Shock), the caregiver pre-heats the dressing room, checks the water temperature (39 degrees C), uses Kakeyu (splash water) starting from feet, and instructs them to grasp handrails securely.",
            listOf(
                DialogLine("Care Worker", "小林さん、お風呂が沸きましたよ。脱衣所は暖房で暖めておきました。", "Kobayashi-san, the bath is ready. I have warmed the changing room with a heater."),
                DialogLine("Kobayashi-san", "あたたかいねえ、うれしいよ。", "Ah, it's warm here, I am so glad!"),
                DialogLine("Care Worker", "温度の急な変化を防ぐためですよ。お湯の温度は39度に調整しています。浴槽に入る前に、足元からお湯（かけ湯）をかけましょうね。", "It is to prevent sudden temperature shock. The water is set at 39 degrees C. Before dipping in, let's pour some check-water on your feet, okay?"),
                DialogLine("Kobayashi-san", "気持ちいいね。さあ、浴槽に入ろう。", "Feels great! Now, let's step into the tub."),
                DialogLine("Care Worker", "はい、こちらの白い手すりをしっかり掴んでくださいね。足元がすべりやすいので、ゆっくり入りましょう。", "Yes, please hold onto this white handrail firmly. The floor is slippery, so let's step in slowly.")
            )
        ),
        QuizQuestion(
            23, QuizType.CONVERSATIONAL,
            "渡辺さんとの更衣介助（お着替え）の会話から、正しい介助の手順を選んでください。",
            "Based on the conversation during dressing with Watanabe-san, choose the correct assistance procedure.",
            listOf(
                "介護者が一方的に勝手にパジャマを選んで無理やり着せた (Caregiver chose and forced pajamas on Watanabe-san without asking)",
                "「脱健着患」に基づき、健康な右腕から脱ぎ、麻痺のある左腕から上着を着るよう介助した (Assisted undressing healthy right arm first, dressing affected left arm first, following the 'Dakken Chakkan' principle)",
                "服がボタンだらけだが、全部手伝わずに1時間放置した (Left Watanabe-san alone for an hour with complete clothing buttons)",
                "麻痺のある左腕を引っ張って痛がらせてしまった (Assisted by forcefully pulling their paralyzed left arm, causing pain)"
            ),
            1,
            "The caregiver respects Watanabe-san's left hemiplegia. When dressing, they undress the healthy right side first (脱健) and dress the paralyzed left side first (着患) with slow joint support.",
            listOf(
                DialogLine("Care Worker", "渡辺さん、朝ですよ。お洋服に着替えましょう。どちらのパジャマを着ますか。", "Watanabe-san, it's morning. Let's change your clothes. Which pajama shirt do you prefer?"),
                DialogLine("Watanabe-san", "こちらの青いほうがいいな。でも、左腕（麻痺側）がうまく動かないから大変だ。", "This blue one is nice. But my left arm (hempilegic side) doesn't move easily, so it's tough."),
                DialogLine("Care Worker", "大丈夫ですよ。まず、今着ている上着を、よく動く「右側の腕（健側）」から脱ぎましょうね。", "No worries. First, let's undress your current top starting from your active 'right arm' (unaffected side), okay?"),
                DialogLine("Watanabe-san", "よし、右側が脱げたよ。", "Alright, the right side is off!"),
                DialogLine("Care Worker", "では、新しい青いお洋服は、麻痺がある「左側の腕（患側）」からゆっくり通していきましょう。私の手のひらで関節を支えますね。", "Now, let's slide the new blue shirt starting onto your paralyzed 'left arm' (affected side) slowly. Let me support your joint with my hand.")
            )
        ),
        QuizQuestion(
            24, QuizType.CONVERSATIONAL,
            "山本さんとの会話より、ポータブルトイレを使用する際の「プライバシーへの配慮」について正しい記述を選んでください。",
            "Based on the conversation with Yamamoto-san, choose the correct statement showing physical privacy respect.",
            listOf(
                "山本さんが恥ずかしがっているが、トイレ中はずっと顔の目の前で監視し続けた (Stared directly at Yamamoto-san's face close-up during toileting despite their embarrassment)",
                "カーテンやドアを閉めてプライベートエリアを作り、山本さんの膝にタオルをかけて廊下で待つことにした (Closed curtains/doors, covered their knees with a dry towel, and waited in the hallway)",
                "大声で排便が出たかどうか確認した (Shouted loudly to verify if feces were released)",
                "面倒なので、ポータブルトイレのフタを開けたまま部屋を出た (Left the portable toilet lid open and walked out carelessly)"
            ),
            1,
            "The caregiver creates a private zone by pulling bed curtains, covers Yamamoto-san's lower body with a warm towel, and waits in the hallway to preserve dignity while maintaining a nearby presence.",
            listOf(
                DialogLine("Yamamoto-san", "すまないね、ベッドの横のポータブルトイレを使い切りたいが、恥ずかしくてね。", "I'm sorry, I want to use this portable toilet beside my bed, but I feel embarrassed."),
                DialogLine("Care Worker", "そのお気持ち、よく分かります。人目を気にせず排泄できるよう、ベッド周りのカーテンをしっかり閉めますね。", "I completely understand that feeling. Let me pull the curtains around the bed fully so you can proceed without worrying."),
                DialogLine("Yamamoto-san", "ありがたい。でも、君が目の前にいると落ち着かなくて…。", "Thank you. But if you stand right here in front of me, I can't relax..."),
                DialogLine("Care Worker", "では、足元をバスタオルで覆っておきますね。私はカーテンを閉めて廊下（近く）で待っています。終わったらこちらのブザーを鳴らしてください。", "In that case, let me wrap your lower lap with this bath towel. I will slide the curtains closed and wait in the hallway. Please press this call buzzer when finished.")
            )
        ),
        QuizQuestion(
            25, QuizType.CONVERSATIONAL,
            "松本さんとの移乗介助の会話から、ボディメカニクスを活用した安全な介護方法として正しい行動を選択してください。",
            "Based on the conversation on transferring with Matsumoto-san, choose the correct body-mechanics intervention.",
            listOf(
                "松本さんの腕を力任せに引っ張り上げて車いすに叩きつけた (Forcefully yanked Matsumoto-san's arm and threw them down on the wheelchair)",
                "松本さんに前傾姿勢（お辞儀をするポーズ）をとってもらい、介護者も腰を低く落として脇の下と背中から力を入れて誘導した (Asked Matsumoto-san to lean forward, while the caregiver lowered their own hips, supporting with leg muscles and torso)",
                "介護者が腰をピンと立てたまま手を伸ばした (Caregiver stood upright with knees locked and simply stretched arms forward)",
                "松本さんに一人でジャンプして車いすに飛び乗るよう指示した (Instructed Matsumoto-san to jump and hop into the wheelchair alone)"
            ),
            1,
            "For safe transfers, prompting forward lean allows the center of gravity to shift naturally over foot structures. The caregiver keeps their hips low and trunk close to prevent back strain (principles of body mechanics).",
            listOf(
                DialogLine("Care Worker", "松本さん、ベッドの端に座れましたね。これから車いす（非麻痺の右側）へ移りましょう。", "Matsumoto-san, you are sitting nicely on the edge. Let's transfer to the wheelchair positioned closely on your healthy right side."),
                DialogLine("Matsumoto-san", "はい。でも立ち上がる力がでるかなあ。", "Yes, but I wonder if my legs have enough strength to rise."),
                DialogLine("Care Worker", "では、立ち上がる時にお辞儀をするように、頭を少し前に出してください（前傾姿勢）。足首はベッド側に少し引きましょう。", "Then, when rising, please bring your head slightly forward, as if bowing (forward lean). Pull your heels back slightly towards the bed."),
                DialogLine("Matsumoto-san", "こうかい？頭が前に出ているね。", "Like this? My head is bent forward now."),
                DialogLine("Care Worker", "はい、素晴らしいです！私も腰を低く落として体を近づけます。私がせーの！と言ったら、しっかり足の裏を踏ん張って立ち上がりましょう。", "Yes, perfect! Let me lower my center of gravity and bring my body close. When I say 'One, two, three!', press your soles flat on the floor and rise.")
            )
        ),
        QuizQuestion(
            26, QuizType.CONVERSATIONAL,
            "認知症の斉藤さんが「うちに帰る！」と荷物を持って歩いている場面での、適切な対応はどれですか。会話から正しい対応を選択してください。",
            "Regarding Dementia user Saitou-san screaming 'I want to go home!' with luggage, choose the correct emphatic intervention based on the dialogue.",
            listOf(
                "斉藤さんを部屋に閉じ込めて荷物を取り上げた (Locked Saitou-san inside their room and confiscated their bags)",
                "「ここはあなたの家です！」と怒鳴って現実を強要した (Chastised them and loudly shouted 'This is your home now!' forcing reality)",
                "斉藤さんの「家に帰りたい」という不安な気持ちに共感し、お話を聞きながら、温かいお茶に誘って落ち着いてもらった (Empathized with their distress, listened actively to their stories, and guided them to sit over a warm cup of tea)",
                "放置してそのまま外に歩いて行かせた (Left them alone and let them wander straight out into visual traffic)"
            ),
            2,
            "For active wandering or exit demands in dementia, never contradict or lock the user up, as this provokes agitation. Validate their emotion first, join their path, and naturally redirect anxiety into comfortable calming stimuli like tea.",
            listOf(
                DialogLine("Saitou-san", "うちの子供が待っているんだよ！早く帰らないといけないの！カバンはどこ？", "My children are waiting for me at home! I have to return immediately! Where is my bag?"),
                DialogLine("Care Worker", "斉藤さん、ご家族が待っていらっしゃるのですね。心配になりますよね。お気持ちよく分かります。", "Saitou-san, you are worried about your family. You must feel so anxious. I completely understand."),
                DialogLine("Saitou-san", "そうなの！だからドアを開けておくれ！遅れると大変なんだ。", "Yes! So open this door! It'll be terrible if I am late."),
                DialogLine("Care Worker", "急がないといけませんね。大変なことです。もしよろしければ、お帰りの準備をする前に、少し美味しいお茶でも飲んで一息つきませんか。たくさんお話を聞かせてください。", "You have to hurry, that must be tough. If you'd like, before preparing your departure, shall we sit over some nice warm tea first? I would love to hear all about your children.")
            )
        ),
        QuizQuestion(
            27, QuizType.CONVERSATIONAL,
            "高橋さんとの会話から、朝の「洗面介助（頭・顔）」中に介護職が確認した健康異常と対応について正しい記述を選んでください。",
            "Based on the conversation with Takahashi-san during morning grooming, choose the correct health anomaly noticed and the caregiver's response.",
            listOf(
                "高橋さんの額がとても熱く、体が震えていたため、熱を測って直ちにスタッフや看護師に報告した (Noticed Takahashi-san's forehead felt hot and they were shivering; took temperature and logged/reported immediately to the nurse)",
                "口元が汚れていたが、何も拭かずに立ち去った (Noticed a messy mouth but ignored it and moved on)",
                "高橋さんが怒り出したので顔を冷水で濡らした (Splashed ice-cold water on Takahashi-san when they were angry)",
                "何も問題は発見されず、そのまま終了した (No issues were observed, concluded perfectly)"
            ),
            0,
            "During morning wash routines, tactile contact (touching forehead) and visual observation allow caregivers to spot acute anomalies (like dynamic fever rise) and trigger immediate nursing reports.",
            listOf(
                DialogLine("Care Worker", "高橋さん、おはようございます。洗面台で顔をお拭きしましょうか。", "Takahashi-san, good morning. Shall we wipe your face at the washbasin?"),
                DialogLine("Takahashi-san", "ありがとう。でもなんだか、身体がだるくて力が入らないんだよ。", "Thank you. But somehow, my body feels sluggish and weak."),
                DialogLine("Care Worker", "だるいのですね。お顔を拭く際に、額（おでこ）に触れますね。あ、かなり熱い感じがします。身体は寒くないですか。", "Sluggish, I see. Let me touch your forehead as I wipe your face. Oh, it feels quite hot! Are you feeling cold?"),
                DialogLine("Takahashi-san", "うん、少しゾクゾクするね、ガタガタ震えがでるよ。", "Yes, chilling a bit, shivers are starting."),
                DialogLine("Care Worker", "悪寒が走っていますね。お顔を優しく拭き終えたら、すぐベッドで横になりましょう。体温を測って、看護師（ナース）に報告しますね。無理せずお座りください。", "You have chills. Once we gently finish wiping, let's lie back on the bed immediately. I will measure your temperature and report to the nurse.")
            )
        ),
        QuizQuestion(
            28, QuizType.CONVERSATIONAL,
            "鈴木さんとの会話から、聴力損失（Hearing Loss）がある利用者への、適切な話しかけ方法として正しい記述を選んでください。",
            "Based on the conversations with Suzuki-san, choose the correct, supportive way to talk to someone with hearing loss.",
            listOf(
                "鈴木さんの耳元で、耳の穴に向かって怒鳴るような大声で叫んだ (Shouted directly into Suzuki-san's ear canal in a high-pitch scream)",
                "鈴木さんの正面に回り、視線（目）を合わせ、低い声のトーンで口元の動きが見えるようにゆっくり話した (Positioned in front, made eye contact, and spoke in a low-pitched, clear tone with visible mouth movements slowly)",
                "話すのを諦めて、一切コミュニケーションをとるのを止めた (Gave up entirely and ceased any try of speech)",
                "早口言葉で一気に言い切って立ち去った (Spoke fast tongue-twisters and walked off instantly)"
            ),
            1,
            "Shouting into advanced-age ears distorts acoustics and brings anxiety. Face them directly at eye level, use lower voice pitches, and articulate slow lip shapes to allow visual compensation.",
            listOf(
                DialogLine("Care Worker", "鈴木さん、こんにちは！お食事の時間（大声で耳元で叫ぶ）！", "Suzuki-san, hello! Dinner time! (shouting close to their ear in high pitch)"),
                DialogLine("Suzuki-san", "おやまぁ！びっくりした！耳元で大きな高い声で叫ばれると頭が痛いし、よく聞き取れないんだよ。", "Oh dear! You startled me! Screaming directly into my ear with a high voice makes my head ache, and I can't catch the words anyway."),
                DialogLine("Care Worker", "ごめんなさい、驚かせてしまいましたね。これからは正面に立ち、目と目を合わせてお話しします。私の口元の動きが見えますでしょうか。", "I am so sorry, I startled you. From now on, I will stand in front, keeping eye contact. Can you see my lips moving clearly?"),
                DialogLine("Suzuki-san", "うん、よく見えるよ。そのくらいの低い声で、ゆっくり話してくれると、補聴器（ほちょうき）越しでも非常に分かりやすいよ。", "Yes, I can see them well. Speaking in that gentle, low-pitched voice slowly makes it very easy to understand, even with my hearing aid."),
                DialogLine("Care Worker", "良かったです。それでは、本日のメニューは温かいおうどんとカボチャの煮付けですよ。ゆっくり食堂へ行きましょうね。", "Wonderful. Today's menu is warm udon noodles and simmered pumpkin. Let's head over to the dining hall slowly, okay?")
            )
        ),
        QuizQuestion(
            29, QuizType.CONVERSATIONAL,
            "介護の専門用語「ヒヤリハット（Incident Report）」について、田中さんとの会話をもとにした重要性として正しい記述を選んでください。",
            "Based on the conversation with Tanaka-san, choose the correct description regarding 'Hiyari-Hatto' (incident prevention report).",
            listOf(
                "ヒヤリハットは、大事故になってからでなければ記録しなくてよい (Incident reports are only recorded after a major injury occurs)",
                "「ヒヤリ」としたり「ハッ」としたヒヤリハットの段階で、状況を詳しく記録・分析し報告することで、将来の重大な転倒や骨折事故を未然に防止できる (By writing and analyzing close-call events where workers are startled, serious future falls can be actively prevented)",
                "ヒヤリハットは、介護職員の勤務評価を下げて罰則を与えるために存在している (Reports exist only to punish and evaluate workers negatively)",
                "ヒヤリハット報告書は、すぐにシュレッダーにかけて破棄すべきである (Reports should be shredded and hidden instantly)"
            ),
            1,
            "Heinrich's Law states that behind every major accident are 29 minor accidents and 300 close-calls. Recording close-calls (Hiyari-Hatto) promptly allows sharing of preventive wisdom across the team, stopping falls before they happen.",
            listOf(
                DialogLine("Care Worker", "田中さん、先ほどベッドからポータブルトイレへ行かれる際、スリッパが滑りかけてバランスを崩されましたね。お怪我はなかったですか。", "Tanaka-san, just now as you got off bed to the portable toilet, your slipper slipped and you briefly lost your balance. Are you unhurt?"),
                DialogLine("Tanaka-san", "ああ、ヒヤリとした（驚いた）だけだよ。怪我はないから、スタッフを騒がせる必要はないよ、誰にも言わなくていい。", "Ah, I was just startled (Hiyari). I'm not hurt, so no need to make a fuss, don't tell anyone about it."),
                DialogLine("Care Worker", "無事で本当にホッとしました！でも、今回は転倒（てんとう）しなかったものの、廊下に靴をそのまま出すと再度滑る危険性があります。これを『ヒヤリハット』として記録しますね。", "I am so relieved you are safe! However, even though you didn't fall this time, leaving loose slippers is a hazard. I will record this as a 'Hiyari-Hatto' incident."),
                DialogLine("Tanaka-san", "怒るのかい？怒られると嫌だなあ。", "Are you going to scold me? I don't want to get in trouble."),
                DialogLine("Care Worker", "いえ、田中さんを怒ることは絶対にありません！スタッフ全体でこの内容を共有して、より滑りにくい介護用靴へ変更したり、床面のクモの巣やコード整理を呼びかける等の大事故防止策を立てるために大事なのです。", "No, we would never scold you! Sharing this allows the whole team to implement safety steps, like suggesting slip-resistant shoes or taping cords down, to prevent any serious breakdown.")
            )
        ),
        QuizQuestion(
            30, QuizType.CONVERSATIONAL,
            "山下さんとの緊急時の喉詰まり（誤嚥）場面での対応として正しい記述を、会話の内容に基づいて選択してください。",
            "Based on the conversation with Yamasita-san during a choking emergency, choose the correct emergency response described.",
            listOf(
                "山下さんが苦しがっているが、本人が飲むまで放置した (Left Yamashita-san alone despite their visible pain)",
                "咳をするよう促し、すぐ後ろに回って手のひらで肩甲骨の間を何度も強く叩いて異物を吐き出させ、同時に他の職員を呼んだ (Encouraged them to cough, positioned behind to perform forceful back blows between shoulder blades repeatedly to expel the blockage, and called for backup)",
                "口元をテープで塞いで声を殺した (Taped their mouth shut to silence the coughing)",
                "水を一気にコップ一杯無理やり喉に流し込んだ (Forced them to swallow a full glass of water rapidly)"
            ),
            1,
            "In severe choking, do not force drinking, as this completely blocks breathing. Prompt them to cough. If ineffective, execute powerful back blows (Senaka-tataki-hou) repeatedly between shoulder blades using the heel of your hand, and immediately press call alerts.",
            listOf(
                DialogLine("Care Worker", "山下さん、カボチャの煮付けはいかがですか。…あれ、山下さん？顔色（チアノーゼ）が青紫に変わっていますよ。喉が詰まりましたか！", "Yamashita-san, how is the pumpkin? ...Wait, Yamashita-san? Your face is turning bluish-purple (cyanosis). Is something stuck in your throat!"),
                DialogLine("Yamada-san", "（ひどく咳き込みながら、喉をごくごくおさえ、声が出ない様子）", "(Coughing severely, grasping their neck, unable to make vocal sounds)"),
                DialogLine("Care Worker", "山下さん！咳（せき）をしてください！出せますか！出してください！他の職員（スタッフ）の方、急いで来てください！吸引機とナースコールをお願いします！", "Yamashita-san! Attempt to cough! Cough it out! Backup, please come immediately! Bring the suction machine and notify the nurse!"),
                DialogLine("Yamada-san", "（さらに喉元をかきむしる、窒息サインの様子）", "(Grasping neck more desperately, showing the universal choking sign)"),
                DialogLine("Care Worker", "今すぐ背中を叩きますね！山下さんの背後から体を支え、前傾姿勢にします。手のひらの付け根で、肩甲骨の間を強く、何度も叩きます（背部叩打法）！頑張ってください、出してください！", "I will perform back blows right now! Supporting from behind, leaning you forward. I will strike firmly between your shoulder blades with the heel of my hand (back blows)! Hang in there, spit it out!")
            )
        )
    )
}
