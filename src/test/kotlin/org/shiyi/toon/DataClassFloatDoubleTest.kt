package org.shiyi.toon

fun main() {
    println("=== 测试 Data Class 包含 Float/Double 字段的往返 ===\n")

    // 测试 1: Double 字段
    data class PointDouble(val x: Double, val y: Double)

    val point1 = PointDouble(10.5, 20.3)
    val encoded1 = Toon.encode(point1)
    val decoded1 = Toon.decodeAs<PointDouble>(encoded1)
    println("测试 1: Double 字段 (带小数)")
    println("  原始: $point1")
    println("  编码: $encoded1")
    println("  解码: $decoded1")
    println("  匹配: ${point1.x == decoded1.x && point1.y == decoded1.y}")
    println()

    // 测试 2: Double 字段（整数值）
    val point2 = PointDouble(5.0, 10.0)
    val encoded2 = Toon.encode(point2)
    val decoded2 = Toon.decodeAs<PointDouble>(encoded2)
    println("测试 2: Double 字段 (整数值)")
    println("  原始: $point2")
    println("  编码: $encoded2")
    println("  解码: $decoded2")
    println("  匹配: ${point2.x == decoded2.x && point2.y == decoded2.y}")
    println()

    // 测试 3: Float 字段
    data class PointFloat(val x: Float, val y: Float)

    val point3 = PointFloat(3.14f, 2.71f)
    val encoded3 = Toon.encode(point3)
    val decoded3 = Toon.decodeAs<PointFloat>(encoded3)
    println("测试 3: Float 字段 (带小数)")
    println("  原始: $point3")
    println("  编码: $encoded3")
    println("  解码: $decoded3")
    println("  匹配: ${Math.abs(point3.x - decoded3.x) < 0.01f && Math.abs(point3.y - decoded3.y) < 0.01f}")
    println()

    // 测试 4: Float 字段（整数值）
    val point4 = PointFloat(5.0f, 10.0f)
    val encoded4 = Toon.encode(point4)
    val decoded4 = Toon.decodeAs<PointFloat>(encoded4)
    println("测试 4: Float 字段 (整数值)")
    println("  原始: $point4")
    println("  编码: $encoded4")
    println("  解码: $decoded4")
    println("  匹配: ${Math.abs(point4.x - decoded4.x) < 0.001f && Math.abs(point4.y - decoded4.y) < 0.001f}")
    println()

    // 测试 5: 混合数字类型
    data class MixedNumbers(
        val intValue: Int,
        val longValue: Long,
        val floatValue: Float,
        val doubleValue: Double
    )

    val mixed = MixedNumbers(
        intValue = 42,
        longValue = 999999999999L,
        floatValue = 3.14f,
        doubleValue = 2.718281828
    )
    val encodedMixed = Toon.encode(mixed)
    val decodedMixed = Toon.decodeAs<MixedNumbers>(encodedMixed)
    println("测试 5: 混合数字类型")
    println("  原始: $mixed")
    println("  编码:\n$encodedMixed")
    println("  解码: $decodedMixed")
    println("  匹配: ${
        mixed.intValue == decodedMixed.intValue && 
        mixed.longValue == decodedMixed.longValue && 
        Math.abs(mixed.floatValue - decodedMixed.floatValue) < 0.001f && 
        Math.abs(mixed.doubleValue - decodedMixed.doubleValue) < 0.000000001
    }")
    println()

    // 测试 6: Product 类（实际场景）
    data class Product(
        val id: Int,
        val name: String,
        val price: Double,
        val weight: Float
    )

    val product = Product(
        id = 1,
        name = "Laptop",
        price = 999.99,
        weight = 2.5f
    )
    val encodedProduct = Toon.encode(product)
    val decodedProduct = Toon.decodeAs<Product>(encodedProduct)
    println("测试 6: Product 类（实际场景）")
    println("  原始: $product")
    println("  编码:\n$encodedProduct")
    println("  解码: $decodedProduct")
    println("  匹配: ${
        product == decodedProduct || (
            product.id == decodedProduct.id && 
            product.name == decodedProduct.name && 
            Math.abs(product.price - decodedProduct.price) < 0.001 && 
            Math.abs(product.weight - decodedProduct.weight) < 0.001f
        )
    }")
    println()
    println("=== 所有测试完成 ===")
}


