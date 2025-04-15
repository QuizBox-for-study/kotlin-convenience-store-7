package store

import java.io.File

class MarkdownReader(val filePath: String) {
    private val lines: List<String> = File(filePath).useLines { it.toList() }

    fun readAll(): String {
        return lines.joinToString("\n")
    }

    fun readLines(): List<String> {
        return lines
    }

    fun filterByKeyword(keyword: String): List<String> {
        return lines.filter { it.contains(keyword, ignoreCase = true) }
    }

    fun filterByRegex(regex: Regex): List<String> {
        return lines.filter { it.contains(regex) }
    }
}

class Receipt(
    val purchase: String?,
    val inventorys: Inventorys) {
    fun createReceipt() {
        println()
        println("==============W 편의점================")
        println("상품명\t\t수량\t금액")

        val items = InputParser.parsePurchaseInput(purchase)
        var total = 0
        var totalQuantity = 0
        items.forEach { (name, quantity) ->
            val item = inventorys.inventoryList.find { it.name == name }
            if (item != null) {
                val price = item.price * quantity
                total += price
                totalQuantity += quantity
                println("$name\t\t$quantity\t${price}원")
            }
        }

        println("=============증\t정===============")


        println("====================================")
        println("총구매액\t\t${totalQuantity}\t${total}원")
    }
}

class Promotions(val promotionList: List<Promotion>) {
    fun printAll() {
        promotionList.forEach {
            println("프로모션명: ${it.name}, 구매 조건: ${it.buy}, 보너스: ${it.get}, 시작 날짜: ${it.startDate}, 종료 날짜: ${it.endDate}")
        }
    }
}

class Promotion(var name: String, var buy: Int, var get: Int, var startDate: String, var endDate: String) {
}

class Inventorys(val inventoryList: MutableList<Inventory>) {
    fun printAllCurrentStocks() {
        println("안녕하세요. W편의점입니다.")
        println("현재 보유하고 있는 상품입니다.")
        println()

        inventoryList.forEach {
            // todo : 돈 출력 시 백원 단위마다 , 추가하는 부분
            if (it.promotion == "null") {
                println("- ${it.name} ${it.price}원 ${it.quantity}개")
            } else {
                println("- ${it.name} ${it.price}원 ${it.quantity}개 ${it.promotion}")
            }
        }
    }
}

class Inventory(var name: String, var price: Int, var quantity: Int, var promotion: String) {

}

class Execute {
    fun printHello() {
        println("hello")
    }
}

object InputParser {
    fun parsePurchaseInput(purchase: String?): List<Pair<String, Int>> {
        if (purchase.isNullOrBlank()) return emptyList()

        return purchase
            .split("],[").map {
                it.replace("[", "").replace("]", "")
            }.map {
                val (name, quantity) = it.split("-")
                name to quantity.toInt()
            }
    }
}

fun main() {
    val stockReader = MarkdownReader("src/main/resources/products.md") // filePath는 레포지토리 루트로 찾음
    val promotionReader = MarkdownReader("src/main/resources/promotions.md")

    val inventoryList = stockReader.readLines()
        .filter { it.isNotBlank() && !it.startsWith("name,") }
        .map { line ->
            val parts = line.split(",")
            Inventory(
                name = parts[0].trim(),
                price = parts[1].trim().toInt(),
                quantity = parts[2].trim().toInt(),
                promotion = parts[3].trim()
            )
        }

    val promotionList = promotionReader.readLines()
        .filter { it.isNotBlank() && !it.startsWith("name,") }
        .map { line ->
            val parts = line.split(",")
            Promotion(
                name = parts[0].trim(),
                buy = parts[1].trim().toInt(),
                get = parts[2].trim().toInt(),
                startDate = parts[3].trim(),
                endDate = parts[4].trim()
            )
        }

    val inventorys = Inventorys(inventoryList.toMutableList())
    val promotions = Promotions(promotionList)

//    println("저장된 재고 목록")
//    inventorys.printAll()
//    println()
//    println("등록된 프로모션 목록")
//    promotions.printAll()

    inventorys.printAllCurrentStocks()
    println("구매하실 상품명과 수량을 입력해 주세요. (예: [사이다-2],[감자칩-1]")

    var purchase = readLine()

    val receipt = Receipt(purchase, inventorys)
    receipt.createReceipt()


}
