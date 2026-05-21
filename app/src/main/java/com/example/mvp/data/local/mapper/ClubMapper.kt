package com.example.mvp.data.local.mapper

import com.example.mvp.data.local.entities.ClubEntity
import com.example.mvp.domain.model.Club
import com.example.mvp.domain.model.ClubBadgeDefaults

fun ClubEntity.toModel(): Club = Club(
    id = id,
    name = name,
    stadium = stadium,
    city = city,
    coachName = coachName,
    badgeId = ClubBadgeDefaults.sanitize(badgeId),
    customBadgePath = customBadgePath?.takeIf { it.isNotBlank() },
    selectedFormationId = selectedFormationId.ifBlank { Club.DEFAULT_FORMATION_ID }
)

fun Club.toEntity(userId: Long): ClubEntity = ClubEntity(
    id = id,
    userId = userId,
    name = name.trim(),
    stadium = stadium.trim(),
    city = city.trim(),
    coachName = coachName.trim(),
    badgeId = ClubBadgeDefaults.sanitize(badgeId),
    customBadgePath = customBadgePath?.takeIf { it.isNotBlank() },
    selectedFormationId = selectedFormationId.ifBlank { Club.DEFAULT_FORMATION_ID }
)