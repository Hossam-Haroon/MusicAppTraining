package com.example.musicapptraining.domain.utils

interface DataFetcher<T> {
    fun fetchDataFromDevice():List<T>
}