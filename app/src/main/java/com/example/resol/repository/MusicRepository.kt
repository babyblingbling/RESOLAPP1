package com.example.resol.repository

import com.example.resol.data.Track

interface MusicRepository {
    suspend fun searchMusic(query: String): Result<List<Track>>
    suspend fun getAudioStreamUrl(trackUrl: String): Result<String>
}