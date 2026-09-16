package com.example.data

object SampleData {
    private val defaultExpiry = System.currentTimeMillis() + (180L * 24 * 60 * 60 * 1000) // 6 months safe future

    val initialProducts = listOf(
        Product(
            name = "Lucky Me! Pancit Canton Kalamansi",
            barcode = "4800016644812",
            category = "Noodles",
            costPrice = 14.50,
            sellingPrice = 18.00,
            stockQuantity = 45,
            unit = "pack",
            lowStockThreshold = 10,
            expiryDate = defaultExpiry
        ),
        Product(
            name = "Lucky Me! Pancit Canton Chili Mansi",
            barcode = "4800016644829",
            category = "Noodles",
            costPrice = 14.50,
            sellingPrice = 18.00,
            stockQuantity = 38,
            unit = "pack",
            lowStockThreshold = 10,
            expiryDate = defaultExpiry
        ),
        Product(
            name = "Great Taste White Coffee 30g",
            barcode = "4800016053300",
            category = "Coffee & Milk",
            costPrice = 10.00,
            sellingPrice = 13.00,
            stockQuantity = 60,
            unit = "sachet",
            lowStockThreshold = 15,
            expiryDate = defaultExpiry
        ),
        Product(
            name = "Kopiko Blanca 3-in-1 Coffee 30g",
            barcode = "8996001414002",
            category = "Coffee & Milk",
            costPrice = 10.50,
            sellingPrice = 14.00,
            stockQuantity = 4, // low stock!
            unit = "sachet",
            lowStockThreshold = 10,
            expiryDate = defaultExpiry
        ),
        Product(
            name = "Coca-Cola Mismo 290ml",
            barcode = "4801981115124",
            category = "Beverages",
            costPrice = 15.00,
            sellingPrice = 20.00,
            stockQuantity = 24,
            unit = "bottle",
            lowStockThreshold = 6,
            expiryDate = defaultExpiry
        ),
        Product(
            name = "Royal Tru-Orange Mismo 290ml",
            barcode = "4801981115131",
            category = "Beverages",
            costPrice = 15.00,
            sellingPrice = 20.00,
            stockQuantity = 18,
            unit = "bottle",
            lowStockThreshold = 6,
            expiryDate = defaultExpiry
        ),
        Product(
            name = "Century Tuna Flakes in Oil 155g",
            barcode = "4807770270014",
            category = "Canned Goods",
            costPrice = 33.00,
            sellingPrice = 40.00,
            stockQuantity = 16,
            unit = "can",
            lowStockThreshold = 5,
            expiryDate = defaultExpiry + (365L * 24 * 60 * 60 * 1000)
        ),
        Product(
            name = "Argentina Corned Beef 150g",
            barcode = "4807770110020",
            category = "Canned Goods",
            costPrice = 35.00,
            sellingPrice = 43.00,
            stockQuantity = 14,
            unit = "can",
            lowStockThreshold = 5,
            expiryDate = defaultExpiry + (365L * 24 * 60 * 60 * 1000)
        ),
        Product(
            name = "Datu Puti Soy Sauce 385ml",
            barcode = "4801042000017",
            category = "Condiments",
            costPrice = 20.00,
            sellingPrice = 25.00,
            stockQuantity = 12,
            unit = "pouch",
            lowStockThreshold = 4,
            expiryDate = defaultExpiry
        ),
        Product(
            name = "Datu Puti White Vinegar 385ml",
            barcode = "4801042000024",
            category = "Condiments",
            costPrice = 18.00,
            sellingPrice = 23.00,
            stockQuantity = 3, // low stock!
            unit = "pouch",
            lowStockThreshold = 5,
            expiryDate = defaultExpiry
        ),
        Product(
            name = "Piattos Cheese Flavor 40g",
            barcode = "4800016035030",
            category = "Snacks",
            costPrice = 16.00,
            sellingPrice = 20.00,
            stockQuantity = 25,
            unit = "pack",
            lowStockThreshold = 8,
            expiryDate = defaultExpiry
        ),
        Product(
            name = "Chippy Barbecue Flavor 110g",
            barcode = "4800016010044",
            category = "Snacks",
            costPrice = 27.00,
            sellingPrice = 34.00,
            stockQuantity = 15,
            unit = "pack",
            lowStockThreshold = 5,
            expiryDate = defaultExpiry
        ),
        Product(
            name = "Safeguard Pure White Soap 130g",
            barcode = "4902430752108",
            category = "Personal Care",
            costPrice = 44.00,
            sellingPrice = 54.00,
            stockQuantity = 10,
            unit = "bar",
            lowStockThreshold = 4,
            expiryDate = defaultExpiry + (180L * 24 * 60 * 60 * 1000)
        ),
        Product(
            name = "Surf Powder Detergent Sachet 65g",
            barcode = "4800888145233",
            category = "Household",
            costPrice = 8.50,
            sellingPrice = 12.00,
            stockQuantity = 50,
            unit = "sachet",
            lowStockThreshold = 12,
            expiryDate = defaultExpiry + (365L * 24 * 60 * 60 * 1000)
        ),
        Product(
            name = "Sinandomeng Well-Milled Rice 1kg",
            barcode = "4809012345678",
            category = "Rice & Grains",
            costPrice = 48.00,
            sellingPrice = 56.00,
            stockQuantity = 80,
            unit = "kg",
            lowStockThreshold = 20,
            expiryDate = defaultExpiry
        ),
        Product(
            name = "Egg (Medium Size)",
            barcode = "000000000001",
            category = "Fresh Goods",
            costPrice = 7.00,
            sellingPrice = 9.00,
            stockQuantity = 60,
            unit = "pc",
            lowStockThreshold = 15,
            expiryDate = defaultExpiry
        )
    )

    val initialCustomers = listOf(
        CustomerDebt(
            name = "Aling Nena Santos",
            phone = "09171234567",
            addressOrNote = "Block 4 Lot 12, Purok 3",
            totalDebt = 485.00
        ),
        CustomerDebt(
            name = "Mang Juan Dela Cruz",
            phone = "09289876543",
            addressOrNote = "Corner Mango St. (Driver)",
            totalDebt = 260.00
        ),
        CustomerDebt(
            name = "Kagawad Bong Reyes",
            phone = "09055554321",
            addressOrNote = "Barangay Hall compound",
            totalDebt = 0.00
        )
    )

    val categories = listOf(
        "All",
        "Beverages",
        "Noodles",
        "Coffee & Milk",
        "Canned Goods",
        "Snacks",
        "Condiments",
        "Household",
        "Personal Care",
        "Rice & Grains",
        "Fresh Goods"
    )

    fun generateInitialSales(): List<SaleTransaction> {
        val now = System.currentTimeMillis()
        val oneHour = 60 * 60 * 1000L
        val oneDay = 24 * oneHour

        return listOf(
            SaleTransaction(
                receiptNumber = "REC-TODAY-001",
                totalAmount = 145.00,
                discountAmount = 0.0,
                discountType = "NONE",
                finalAmount = 145.00,
                amountPaid = 200.00,
                changeAmount = 55.00,
                paymentMethod = "CASH",
                totalCost = 109.00,
                profit = 36.00,
                itemsSummary = "Lucky Me! Pancit Canton Kalamansi x3 (₱54.00), Coca-Cola Mismo 290ml x2 (₱40.00), Kopiko Blanca Twin Pack x3 (₱51.00)",
                timestamp = now - (2 * oneHour),
                isVoided = false
            ),
            SaleTransaction(
                receiptNumber = "REC-TODAY-002",
                totalAmount = 112.00,
                discountAmount = 0.0,
                discountType = "NONE",
                finalAmount = 112.00,
                amountPaid = 112.00,
                changeAmount = 0.0,
                paymentMethod = "GCASH_MAYA",
                totalCost = 88.00,
                profit = 24.00,
                itemsSummary = "Sinandomeng Well-Milled Rice 1kg x2 (₱112.00)",
                timestamp = now - (4 * oneHour),
                isVoided = false
            ),
            SaleTransaction(
                receiptNumber = "REC-YEST-001",
                totalAmount = 215.00,
                discountAmount = 0.0,
                discountType = "NONE",
                finalAmount = 215.00,
                amountPaid = 500.00,
                changeAmount = 285.00,
                paymentMethod = "CASH",
                totalCost = 168.00,
                profit = 47.00,
                itemsSummary = "Argentina Corned Beef 150g x2 (₱78.00), Century Tuna Flakes in Oil 155g x2 (₱72.00), Egg (Medium Size) x5 (₱45.00), Datu Puti Soy Sauce 385ml x1 (₱20.00)",
                timestamp = now - (26 * oneHour),
                isVoided = false
            ),
            SaleTransaction(
                receiptNumber = "REC-YEST-002",
                totalAmount = 85.00,
                discountAmount = 0.0,
                discountType = "NONE",
                finalAmount = 85.00,
                amountPaid = 85.00,
                changeAmount = 0.0,
                paymentMethod = "UTANG_LENDING",
                customerId = 1L,
                customerName = "Aling Nena Santos",
                totalCost = 63.50,
                profit = 21.50,
                itemsSummary = "Surf Powder Detergent Sachet 65g x3 (₱36.00), Bear Brand Fortified Powder 33g x3 (₱45.00), Kopiko Blanca Twin Pack x1 (₱17.00)",
                timestamp = now - (30 * oneHour),
                isVoided = false
            ),
            SaleTransaction(
                receiptNumber = "REC-WEEK-001",
                totalAmount = 320.00,
                discountAmount = 0.0,
                discountType = "NONE",
                finalAmount = 320.00,
                amountPaid = 350.00,
                changeAmount = 30.00,
                paymentMethod = "CASH",
                totalCost = 252.00,
                profit = 68.00,
                itemsSummary = "Sinandomeng Well-Milled Rice 1kg x4 (₱224.00), Piattos Cheese 85g x2 (₱70.00), Lucky Me! Instant Mami Chicken x2 (₱26.00)",
                timestamp = now - (3 * oneDay),
                isVoided = false
            ),
            SaleTransaction(
                receiptNumber = "REC-WEEK-002",
                totalAmount = 140.00,
                discountAmount = 0.0,
                discountType = "NONE",
                finalAmount = 140.00,
                amountPaid = 140.00,
                changeAmount = 0.0,
                paymentMethod = "GCASH_MAYA",
                totalCost = 104.00,
                profit = 36.00,
                itemsSummary = "Safeguard White Soap 60g x2 (₱56.00), Palmolive Naturals Shampoo 15ml x4 (₱28.00), Surf Powder Detergent Sachet 65g x4 (₱48.00), Egg (Medium Size) x1 (₱9.00)",
                timestamp = now - (5 * oneDay),
                isVoided = false
            )
        )
    }
}
