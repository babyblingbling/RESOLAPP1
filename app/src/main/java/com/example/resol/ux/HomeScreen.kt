package com.example.resol.ux

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.resol.R
import com.example.resol.data.Artist
import com.example.resol.data.Track
import com.example.resol.viewmodel.HomeUiState
import com.example.resol.viewmodel.HomeViewModel
import com.example.resol.viewmodel.LibraryViewModel

@Composable
fun HomeScreen(
    homeViewModel: HomeViewModel,
    libraryViewModel: LibraryViewModel,
    onTrackClick: (Track) -> Unit,
    onArtistClick: (String) -> Unit,
    onSettingsClick: () -> Unit,
    contentPadding: PaddingValues,
    onDownloadQueueClick: () -> Unit,
    hasNewmessage: Boolean,
    onmessageClick: () -> Unit,
) {
    val homeState = homeViewModel.uiState
    val recentHistory by libraryViewModel.history.collectAsState()
    val downloadQueue by libraryViewModel.downloadQueue.collectAsState()
    val isOnline by homeViewModel.isOnline.collectAsState()

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(
            top = contentPadding.calculateTopPadding(),
            bottom = contentPadding.calculateBottomPadding() + 88.dp
        )
    ) {
        // 1. TOP BAR (Sử dụng TopBar từ SharedComposables.kt)
        item {
            TopBar(
                title = stringResource(R.string.app_name),
                onSettingsClick = onSettingsClick,
                downloadQueueSize = downloadQueue.size,
                onDownloadQueueClick = onDownloadQueueClick,
                hasNewmessage = hasNewmessage,
                onmessageClick = onmessageClick
            )
            Spacer(modifier = Modifier.height(12.dp))
        }

        // 2. OFFLINE WARNING
        if (!isOnline) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Text(
                        text = stringResource(R.string.offline_message),
                        modifier = Modifier.padding(16.dp),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
            }
        }

        if (isOnline) {
            // 3. TRENDING
            item {
                SectionHeader(stringResource(R.string.trending_now))
                Spacer(modifier = Modifier.height(12.dp))
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    when (homeState) {
                        // Sử dụng Skeleton từ Skeletons.kt
                        is HomeUiState.Loading -> items(5) { SkeletonTrendingCard() }
                        is HomeUiState.Success -> items(homeState.popularThisWeek) { track ->
                            TrendingCard(track = track, onCardClick = { onTrackClick(track) })
                        }
                        is HomeUiState.Error -> { }
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
            }

            // 4. TOP ARTISTS (Dữ liệu thật từ ViewModel)
            item {
                SectionHeader(stringResource(R.string.top_artists))
                Spacer(modifier = Modifier.height(12.dp))
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    when (homeState) {
                        is HomeUiState.Loading -> items(5) { SkeletonArtistCard() }
                        is HomeUiState.Success -> items(homeState.trendingArtists) { artist ->
                            ArtistCard(artist = artist, onArtistClick = onArtistClick)
                        }
                        is HomeUiState.Error -> { }
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
            }
        }

        // 5. RECENTLY PLAYED
        if (recentHistory.isNotEmpty()) {
            item {
                SectionHeader(stringResource(R.string.recently_played))
                Spacer(modifier = Modifier.height(12.dp))
            }
            items(recentHistory.take(4), key = { it.playedAt }) { historyEntry ->
                RecentlyPlayedItem(track = historyEntry.track, onTrackClick = { onTrackClick(historyEntry.track) })
            }
        }

        // 6. QUICK PICKS & MADE FOR YOU
        if (isOnline) {
            item {
                Spacer(modifier = Modifier.height(24.dp))
                SectionHeader(stringResource(R.string.quick_picks))
                Spacer(modifier = Modifier.height(12.dp))
            }
            when (homeState) {
                is HomeUiState.Loading -> items(5) { SkeletonQuickPickItem() }
                is HomeUiState.Success -> items(homeState.topSongsGlobal) { track ->
                    QuickPickItem(track = track, onTrackClick = { onTrackClick(track) })
                }
                is HomeUiState.Error -> { }
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))
                SectionHeader(stringResource(R.string.made_for_you))
                Spacer(modifier = Modifier.height(12.dp))
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    when (homeState) {
                        is HomeUiState.Loading -> items(4) { SkeletonTrendingCard() }
                        is HomeUiState.Success -> items(homeState.newReleases) { track ->
                            MadeForYouCard(track = track, onCardClick = { onTrackClick(track) })
                        }
                        is HomeUiState.Error -> { }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(100.dp))
        }
    }
}

// --- LOCAL COMPONENTS (Chỉ dùng trong màn hình này) ---

@Composable
private fun SectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleLarge,
        color = MaterialTheme.colorScheme.onBackground,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(horizontal = 16.dp)
    )
}

@Composable
private fun TrendingCard(track: Track, onCardClick: () -> Unit) {
    Card(
        modifier = Modifier.width(140.dp).height(180.dp).clickable { onCardClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column {
            AsyncImage(
                model = track.thumbnailUrl, contentDescription = null,
                modifier = Modifier.fillMaxWidth().height(120.dp), contentScale = ContentScale.Crop
            )
            Column(modifier = Modifier.padding(8.dp)) {
                Text(track.title, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text(track.artist, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.tertiary, maxLines = 1, overflow = TextOverflow.Ellipsis)
            }
        }
    }
}

@Composable
private fun ArtistCard(artist: Artist, onArtistClick: (String) -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.width(80.dp).clickable { onArtistClick(artist.name) }) {
        AsyncImage(
            model = artist.imageUrl, contentDescription = null,
            modifier = Modifier.size(64.dp).clip(CircleShape), contentScale = ContentScale.Crop
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(artist.name, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onBackground, maxLines = 1, overflow = TextOverflow.Ellipsis)
    }
}

@Composable
private fun RecentlyPlayedItem(track: Track, onTrackClick: () -> Unit) {
    Row(modifier = Modifier.fillMaxWidth().clickable { onTrackClick() }.padding(horizontal = 16.dp, vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
        AsyncImage(model = track.thumbnailUrl, contentDescription = null, modifier = Modifier.size(56.dp).clip(RoundedCornerShape(8.dp)), contentScale = ContentScale.Crop)
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(track.title, style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onBackground, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Text(track.artist, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.tertiary, maxLines = 1, overflow = TextOverflow.Ellipsis)
        }
    }
}

@Composable
private fun QuickPickItem(track: Track, onTrackClick: () -> Unit) {
    Row(modifier = Modifier.fillMaxWidth().clickable { onTrackClick() }.padding(horizontal = 16.dp, vertical = 6.dp), verticalAlignment = Alignment.CenterVertically) {
        AsyncImage(model = track.thumbnailUrl, contentDescription = null, modifier = Modifier.size(48.dp).clip(RoundedCornerShape(6.dp)), contentScale = ContentScale.Crop)
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(track.title, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onBackground, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Text(track.artist, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.tertiary, maxLines = 1, overflow = TextOverflow.Ellipsis)
        }
    }
}

@Composable
private fun MadeForYouCard(track: Track, onCardClick: () -> Unit) {
    Card(modifier = Modifier.width(120.dp).height(160.dp).clickable { onCardClick() }, shape = RoundedCornerShape(10.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
        Column {
            AsyncImage(model = track.thumbnailUrl, contentDescription = null, modifier = Modifier.fillMaxWidth().height(100.dp), contentScale = ContentScale.Crop)
            Column(modifier = Modifier.padding(6.dp)) {
                Text(track.title, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface, maxLines = 2, overflow = TextOverflow.Ellipsis)
                Text(track.artist, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.tertiary, maxLines = 1, overflow = TextOverflow.Ellipsis)
            }
        }
    }
}