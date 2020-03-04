package com.example.test.html.richtext


/**
 * at 人的span
 * @author fortune
 */
class AtSpan(name: String?, var userId: Long) : BaseLinkSpan(name, "story://personCenter?userId=$userId")
