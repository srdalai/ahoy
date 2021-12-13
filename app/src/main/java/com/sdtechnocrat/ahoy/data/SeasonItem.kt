package com.sdtechnocrat.ahoy.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class SeasonItem(val title: String, val seriesNo: String): Parcelable
