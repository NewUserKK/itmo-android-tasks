package com.learning.newuserkk.xkcdbrowser

import org.junit.Test

import org.junit.Assert.*


class GetComicsUnitTest {
    @Test
    fun testComics() {
        val content = Content
        assertEquals(content.START_COUNT, content.ITEM_MAP.size)
    }
}
