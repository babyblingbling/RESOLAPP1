package com.example.resol.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.resol.data.Artist
import com.example.resol.data.Track
import com.example.resol.repository.NewPipeMusicRepository
import com.example.resol.util.AppConnectivityManager
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed class HomeUiState {
    object Loading : HomeUiState()
    data class Success(
        val popularThisWeek: List<Track>,
        val topSongsGlobal: List<Track>,
        val newReleases: List<Track>,
        val trendingArtists: List<Artist> // <--- BẮT BUỘC CÓ DÒNG NÀY
    ) : HomeUiState()
    data class Error(val message: String) : HomeUiState()
}

class HomeViewModel(
    private val musicRepository: NewPipeMusicRepository,
    connectivityManager: AppConnectivityManager
) : ViewModel() {

    var uiState by mutableStateOf<HomeUiState>(HomeUiState.Loading)
        private set

    val isOnline: StateFlow<Boolean> = connectivityManager.isOnline
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)

    init {
        loadHomeData()
    }

    fun loadHomeData() {
        viewModelScope.launch {
            if (!isOnline.value) {
                // Xử lý khi offline (tùy chọn)
            }
            uiState = HomeUiState.Loading

            try {
                // Lấy nhạc (VN)
                val trendingDeferred = async { musicRepository.getTrendingTracks("VN", 20) }
                val topSongsDeferred = async { musicRepository.searchMusic("Top 50 Global") }
                val newReleasesDeferred = async { musicRepository.searchMusic("New Music Friday") }

                val trendingResult = trendingDeferred.await().getOrNull() ?: emptyList()
                val topSongsResult = topSongsDeferred.await().getOrNull() ?: emptyList()
                val newReleasesResult = newReleasesDeferred.await().getOrNull() ?: emptyList()

                // Logic: Tách Ca sĩ từ danh sách bài hát
                val extractedArtists = trendingResult
                    .map { track ->
                        Artist(
                            id = track.uploader,
                            name = track.artist,
                            imageUrl = track.thumbnailUrl
                        )
                    }
                    .distinctBy { it.name }
                    .take(10)

                uiState = HomeUiState.Success(
                    popularThisWeek = trendingResult,
                    topSongsGlobal = topSongsResult,
                    newReleases = newReleasesResult,
                    trendingArtists = extractedArtists // <--- Truyền dữ liệu vào đây
                )
            } catch (e: Exception) {
                uiState = HomeUiState.Error("Lỗi: ${e.message}")
            }
        }
    }
}