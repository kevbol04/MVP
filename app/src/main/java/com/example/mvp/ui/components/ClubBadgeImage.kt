package com.example.mvp.ui.components

import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.size
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import com.example.mvp.R
import com.example.mvp.domain.model.ClubBadgeDefaults
import java.io.File

@Composable
fun ClubBadgeImage(
    badgeId: String,
    customBadgePath: String?,
    size: Dp,
    modifier: Modifier = Modifier
) {
    val customBitmap = remember(customBadgePath) {
        customBadgePath
            ?.takeIf { it.isNotBlank() }
            ?.let { path -> File(path) }
            ?.takeIf { file -> file.exists() && file.isFile }
            ?.let { file -> BitmapFactory.decodeFile(file.absolutePath) }
    }

    if (customBitmap != null) {
        Image(
            bitmap = customBitmap.asImageBitmap(),
            contentDescription = "Escudo personalizado",
            contentScale = ContentScale.Fit,
            modifier = modifier.size(size)
        )
    } else {
        Image(
            painter = painterResource(id = badgeDrawableRes(badgeId)),
            contentDescription = "Escudo del club",
            contentScale = ContentScale.Fit,
            modifier = modifier.size(size)
        )
    }
}

private fun badgeDrawableRes(id: String): Int {
    return when (ClubBadgeDefaults.sanitize(id)) {
        "royal_blue" -> R.drawable.club_badge_classic_gold
        "galaxy_purple" -> R.drawable.club_badge_silver_star
        "ocean_cyan" -> R.drawable.club_badge_purple_ball
        "green_star" -> R.drawable.club_badge_blue_stadium
        "fire_red" -> R.drawable.club_badge_green_city
        "gold_crown" -> R.drawable.club_badge_orange_crown
        else -> R.drawable.club_badge_silver_star
    }
}