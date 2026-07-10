package models

import org.junit.Assert.assertEquals
import org.junit.Test

class PostFeatureTest {

    @Test
    fun testToMap() {
        val post = PostFeature(
            uid = "123",
            left = "10",
            top = "20",
            right = "30",
            bottom = "40",
            label1 = "l1",
            label2 = "l2",
            label3 = "l3"
        )

        val map = post.toMap()

        assertEquals("123", map["uid"])
        assertEquals("10", map["left"])
        assertEquals("20", map["top"])
        assertEquals("30", map["right"])
        assertEquals("40", map["bottom"])
        assertEquals("l1", map["label1"])
        assertEquals("l2", map["label2"])
        assertEquals("l3", map["label3"])
        assertEquals(8, map.size)
    }

    @Test
    fun testToMapWithNulls() {
        val post = PostFeature()

        val map = post.toMap()

        assertEquals("", map["uid"])
        assertEquals("", map["left"])
        assertEquals("", map["top"])
        assertEquals("", map["right"])
        assertEquals("", map["bottom"])
        assertEquals("", map["label1"])
        assertEquals("", map["label2"])
        assertEquals("", map["label3"])
        assertEquals(8, map.size)
    }
}
