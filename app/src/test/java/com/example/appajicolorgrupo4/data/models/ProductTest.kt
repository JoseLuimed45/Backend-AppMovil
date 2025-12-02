package com.example.appajicolorgrupo4.data.models

import org.junit.Assert.*
import org.junit.Test

class ProductTest {

    @Test
    fun `Product should be created with all properties`() {
        // Given & When
        val product = Product(
            id = "prod_001",
            nombre = "Polera Beastie Boys",
            descripcion = "Polera diseño personalizado con técnica DTF",
            precio = 15000,
            categoria = "SERIGRAFIA",
            stock = 50,
            imagenUrl = "https://cloudinary.com/image.jpg"
        )

        // Then
        assertEquals("prod_001", product.id)
        assertEquals("Polera Beastie Boys", product.nombre)
        assertEquals("Polera diseño personalizado con técnica DTF", product.descripcion)
        assertEquals(15000, product.precio)
        assertEquals("SERIGRAFIA", product.categoria)
        assertEquals(50, product.stock)
        assertEquals("https://cloudinary.com/image.jpg", product.imagenUrl)
    }

    @Test
    fun `Product equality should work correctly`() {
        // Given
        val product1 = Product("1", "Polera A", "Desc A", 10000, "DTF", 10, "url1")
        val product2 = Product("1", "Polera A", "Desc A", 10000, "DTF", 10, "url1")
        val product3 = Product("2", "Polera B", "Desc B", 20000, "SERIGRAFIA", 5, "url2")

        // Then
        assertEquals(product1, product2)
        assertNotEquals(product1, product3)
    }

    @Test
    fun `Product hashCode should be consistent`() {
        // Given
        val product1 = Product("1", "Polera A", "Desc A", 10000, "DTF", 10, "url1")
        val product2 = Product("1", "Polera A", "Desc A", 10000, "DTF", 10, "url1")

        // Then
        assertEquals(product1.hashCode(), product2.hashCode())
    }

    @Test
    fun `Product with different IDs should not be equal`() {
        // Given
        val product1 = Product("1", "Polera A", "Desc", 10000, "DTF", 10, "url")
        val product2 = Product("2", "Polera A", "Desc", 10000, "DTF", 10, "url")

        // Then
        assertNotEquals(product1, product2)
    }

    @Test
    fun `Product copy should create new instance with same values`() {
        // Given
        val original = Product("1", "Original", "Desc", 15000, "SERIGRAFIA", 20, "url")

        // When
        val copy = original.copy()

        // Then
        assertEquals(original, copy)
        assertNotSame(original, copy)
    }

    @Test
    fun `Product copy with modified precio should change only precio`() {
        // Given
        val original = Product("1", "Polera", "Desc", 15000, "DTF", 10, "url")

        // When
        val modified = original.copy(precio = 20000)

        // Then
        assertEquals(20000, modified.precio)
        assertEquals(original.nombre, modified.nombre)
        assertEquals(original.id, modified.id)
        assertEquals(original.descripcion, modified.descripcion)
    }

    @Test
    fun `Product toString should contain all fields`() {
        // Given
        val product = Product("1", "Test Polera", "Test Desc", 12000, "ACCESORIOS", 15, "test_url")

        // When
        val stringRepresentation = product.toString()

        // Then
        assertTrue(stringRepresentation.contains("1"))
        assertTrue(stringRepresentation.contains("Test Polera"))
        assertTrue(stringRepresentation.contains("12000"))
    }

    @Test
    fun `Product should handle empty strings`() {
        // Given & When
        val product = Product("", "", "", 0, "", 0, "")

        // Then
        assertEquals("", product.id)
        assertEquals("", product.nombre)
        assertEquals("", product.descripcion)
        assertEquals(0, product.precio)
        assertEquals("", product.categoria)
        assertEquals(0, product.stock)
        assertEquals("", product.imagenUrl)
    }

    @Test
    fun `Product should handle large stock values`() {
        // Given & When
        val product = Product("1", "Polera", "Desc", 10000, "DTF", 999999, "url")

        // Then
        assertEquals(999999, product.stock)
    }

    @Test
    fun `Product should handle large precio values`() {
        // Given & When
        val product = Product("1", "Polera Premium", "Desc", 1000000, "SERIGRAFIA", 10, "url")

        // Then
        assertEquals(1000000, product.precio)
    }

    @Test
    fun `Product should handle special characters in nombre`() {
        // Given & When
        val product = Product("1", "Polera 100% Algodón & Diseño™", "Desc", 15000, "DTF", 10, "url")

        // Then
        assertEquals("Polera 100% Algodón & Diseño™", product.nombre)
    }

    @Test
    fun `Product should handle long descripcion`() {
        // Given
        val longDesc = "Esta es una descripción muy larga ".repeat(50)
        
        // When
        val product = Product("1", "Polera", longDesc, 15000, "DTF", 10, "url")

        // Then
        assertEquals(longDesc, product.descripcion)
    }

    @Test
    fun `Product categoria should accept different values`() {
        // Given & When
        val product1 = Product("1", "Polera 1", "Desc", 10000, "SERIGRAFIA", 10, "url")
        val product2 = Product("2", "Polera 2", "Desc", 10000, "DTF", 10, "url")
        val product3 = Product("3", "Polera 3", "Desc", 10000, "ACCESORIOS", 10, "url")

        // Then
        assertEquals("SERIGRAFIA", product1.categoria)
        assertEquals("DTF", product2.categoria)
        assertEquals("ACCESORIOS", product3.categoria)
    }

    @Test
    fun `Product should handle URL with query parameters`() {
        // Given & When
        val product = Product(
            "1", "Polera", "Desc", 10000, "DTF", 10, 
            "https://res.cloudinary.com/image.jpg?w=800&h=600&q=auto"
        )

        // Then
        assertTrue(product.imagenUrl.contains("?"))
        assertTrue(product.imagenUrl.contains("w=800"))
    }

    @Test
    fun `Product with zero stock should be valid`() {
        // Given & When
        val product = Product("1", "Agotado", "Desc", 10000, "DTF", 0, "url")

        // Then
        assertEquals(0, product.stock)
    }

    @Test
    fun `Product with negative stock should store negative value`() {
        // Given & When
        val product = Product("1", "Test", "Desc", 10000, "DTF", -5, "url")

        // Then
        assertEquals(-5, product.stock)
    }
}
