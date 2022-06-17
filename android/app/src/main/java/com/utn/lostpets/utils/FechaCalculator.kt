package com.utn.lostpets.utils

import android.os.Build
import androidx.annotation.RequiresApi
import com.facebook.FacebookSdk.getApplicationContext
import com.utn.lostpets.R
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

object FechaCalculator {

    private val resources = getApplicationContext().getResources()

    @JvmStatic
    @RequiresApi(Build.VERSION_CODES.O)
    public fun calcularDistancia(fecha: String): String {
            var fechaP = LocalDateTime.parse(fecha, DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss"))
        var fechaActual = LocalDateTime.now()
        var tempDateTime = LocalDateTime.from(fechaP)

        /* Si pasaron años */
        val años = tempDateTime.until(fechaActual, ChronoUnit.YEARS)
        if (años != 0L) {
            return resources.getString(R.string.timeStart) + " $años " + resources.getString(R.string.timeYears)
        }

        /* Si pasaron meses */
        val meses = tempDateTime.until(fechaActual, ChronoUnit.MONTHS)
        if (meses != 0L) {
            return resources.getString(R.string.timeStart) + " $meses " + resources.getString(R.string.timeMonths)
        }

        /* Si pasaron días */
        val dias = tempDateTime.until(fechaActual, ChronoUnit.DAYS)
        if (dias != 0L) {
            return resources.getString(R.string.timeStart) + " $dias " + resources.getString(R.string.timeDays)
        }

        /* Si pasaron horas */
        val horas = tempDateTime.until(fechaActual, ChronoUnit.HOURS)
        if (horas != 0L) {
            return resources.getString(R.string.timeStart) + " $horas " + resources.getString(R.string.timeHours)
        }

        /* Si pasaron minutos */
        val minutos = tempDateTime.until(fechaActual, ChronoUnit.MINUTES)
        if (minutos != 0L) {
            return resources.getString(R.string.timeStart) + " $minutos " + resources.getString(R.string.timeMinutes)
        }

        /* Si pasaron segundos */
        val segundos = tempDateTime.until(fechaActual, ChronoUnit.SECONDS)
        if(segundos != 0L) {
            return resources.getString(R.string.timeStart) + " $segundos " + resources.getString(R.string.timeSeconds)
        }

        return resources.getString(R.string.timeStart) + " " + resources.getString(R.string.timeRecent)
    }
}