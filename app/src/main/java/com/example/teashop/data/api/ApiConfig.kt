package com.example.teashop.data.api

import com.squareup.moshi.FromJson
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonReader
import com.squareup.moshi.JsonWriter
import com.squareup.moshi.Moshi
import com.squareup.moshi.ToJson
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.time.LocalDate
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

class ZonedDateTimeAdapter: JsonAdapter<ZonedDateTime>() {
    private val formatter: DateTimeFormatter = DateTimeFormatter.ISO_ZONED_DATE_TIME

    @FromJson
    override fun fromJson(reader: JsonReader): ZonedDateTime? {
        return reader.nextString()?.let { ZonedDateTime.parse(it, formatter) }
    }

    @ToJson
    override fun toJson(writer: JsonWriter, value: ZonedDateTime?) {
        writer.value(value?.format(formatter))
    }
}

class LocalDateAdapter: JsonAdapter<LocalDate>() {
    private val formatter: DateTimeFormatter = DateTimeFormatter.ISO_DATE

    @FromJson
    override fun fromJson(reader: JsonReader): LocalDate? {
        return reader.nextString()?.let { LocalDate.parse(it, formatter) }
    }

    @ToJson
    override fun toJson(writer: JsonWriter, value: LocalDate?) {
        writer.value(value?.format(formatter))
    }
}

val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .add(ZonedDateTimeAdapter())
    .add(LocalDateAdapter())
    .build()

var retrofitBuilder: Retrofit = Retrofit.Builder()
    .baseUrl(BASE_URL)
    .addConverterFactory(ScalarsConverterFactory.create())
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .build();

var retrofitStripe: Retrofit = Retrofit.Builder()
    .baseUrl(STRIPE_BASE_URL)
    .addConverterFactory(GsonConverterFactory.create())
    .build()