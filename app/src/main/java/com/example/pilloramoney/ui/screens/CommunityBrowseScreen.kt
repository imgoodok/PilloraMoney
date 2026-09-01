package com.example.pilloramoney.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.pilloramoney.data.model.Community
import com.example.pilloramoney.ui.viewmodels.CommunityViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommunityBrowseScreen(
    onBack: () -> Unit,
    onCommunityClick: (String) -> Unit,
    viewModel: CommunityViewModel = hiltViewModel()
) {
    val communities by viewModel.communities.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Descobrir Comunidades") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar")
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (communities.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 48.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "Nenhuma comunidade encontrada.",
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                items(communities) { community ->
                    CommunityItem(
                        community = community,
                        onClick = { onCommunityClick(community.id) }
                    )
                }
            }
        }
    }
}

@Composable
fun CommunityItem(community: Community, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(50.dp),
                shape = RoundedCornerShape(8.dp),
                color = MaterialTheme.colorScheme.primaryContainer
            ) {
                if (community.imageBlob != null) {
                    AsyncImage(
                        model = community.imageBlob.toBytes(),
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Box(contentAlignment = Alignment.Center) {
                        Text((community.name ?: "").take(1).uppercase(), color = MaterialTheme.colorScheme.primary)
                    }
                }
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(community.name ?: "Sem nome", fontWeight = FontWeight.Bold)
                Text(
                    community.description ?: "",
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 2
                )
            }
            
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    "${community.memberCount}",
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text("membros", style = MaterialTheme.typography.labelSmall)
            }
        }
    }
}
