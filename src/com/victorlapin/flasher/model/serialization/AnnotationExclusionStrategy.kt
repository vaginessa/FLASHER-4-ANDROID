package com.victorlapin.flasher.model.serialization

import com.google.gson.ExclusionStrategy
import com.google.gson.FieldAttributes

class AnnotationExclusionStrategy : ExclusionStrategy {
    override fun shouldSkipField(f: FieldAttributes) =
            (f.getAnnotation(Exclude::class.java) != null)

    override fun shouldSkipClass(clazz: Class<*>) = false
}