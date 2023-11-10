package com.helpful.dpuschedule.helpful.extentions

import java.util.Calendar

val Calendar.date
    get() = "${get(Calendar.YEAR)}-${get(Calendar.MONTH) + 1}-${get(Calendar.DAY_OF_MONTH)}"