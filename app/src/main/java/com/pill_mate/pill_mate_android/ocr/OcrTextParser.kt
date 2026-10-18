package com.pill_mate.pill_mate_android.ocr

object OcrTextParser {

    // 용량 표기 (500mg, 0.5g, 10ml 등)
    private val strengthRegex =
        Regex("\\d+(\\.\\d+)?\\s*(mg|g|ml|mcg|μg|IU|㎎|㎖|㎍)", RegexOption.IGNORE_CASE)

    // 괄호 안 내용 제거 (성분명, 규격 등)
    private val bracketRegex = Regex("[(\\[{（][^)\\]}）]*[)\\]}）]")

    // 약 이름으로 끝나는 토큰 (예: 타이레놀정, 아목시실린캡슐)
    private val drugTokenRegex =
        Regex("^[가-힣A-Za-z0-9]{2,}(정|캡슐|캅셀|시럽|액|과립|겔|크림|연고|패치|환)$")

    private val stopWords = setOf("복용정", "처방정", "조제정")

    fun extractCandidates(raw: String, max: Int = 6): List<String> {
        return raw.lines()
            .map { clean(it) }
            .flatMap { it.split(" ") }
            .map { it.trim() }
            .filter { it.length in 3..25 }
            .filter { drugTokenRegex.matches(it) }
            .filter { it !in stopWords }
            .distinct()
            .take(max)
    }

    private fun clean(line: String): String {
        return line
            .replace(bracketRegex, " ")
            .replace(strengthRegex, " ")
            .replace(Regex("[^가-힣A-Za-z0-9\\s]"), " ")
            .replace(Regex("\\s+"), " ")
            .trim()
    }
}