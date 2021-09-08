package com.mrlin.composemany.di

import com.google.gson.annotations.SerializedName
import retrofit2.Converter
import retrofit2.Retrofit
import java.lang.reflect.Type

/*********************************
 * 枚举的序列化
 * @author mrlin
 * 创建于 2021年09月08日
 ******************************** */
class EnumRetrofitConverterFactory : Converter.Factory() {
    override fun stringConverter(
        type: Type,
        annotations: Array<out Annotation>,
        retrofit: Retrofit
    ): Converter<*, String>? {
        if (type is Class<*> && type.isEnum) {
            return Converter<Any?, String> { value -> getSerializedNameValue(value as Enum<*>) }
        }
        return null
    }

    private fun <E : Enum<*>> getSerializedNameValue(e: E): String {
        try {
            return e.javaClass.getField(e.name).getAnnotation(SerializedName::class.java)?.value.orEmpty()
        } catch (exception: NoSuchFieldException) {
            exception.printStackTrace()
        }
        return ""
    }
}