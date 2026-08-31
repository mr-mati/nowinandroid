package com.google.samples.apps.nowinandroid.core.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.google.samples.apps.nowinandroid.core.designsystem.component.NiaTopicTag
import com.google.samples.apps.nowinandroid.core.model.data.UserExternalNewsResource
import java.util.Locale

@Composable
fun ExternalNewsResourceCard(
    userExternalNewsResource: UserExternalNewsResource,
    onToggleBookmark: () -> Unit,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
    ) {
        Column {
            if (!userExternalNewsResource.imageUrl.isNullOrEmpty()) {
                NewsResourceHeaderImage(userExternalNewsResource.imageUrl)
            }
            Box(
                modifier = Modifier.padding(16.dp),
            ) {
                Column {
                    Spacer(modifier = Modifier.height(12.dp))
                    Row {
                        Text(
                            text = userExternalNewsResource.title,
                            style = MaterialTheme.typography.headlineSmall,
                            modifier = Modifier.fillMaxWidth(.8f),
                        )
                        Spacer(modifier = Modifier.weight(1f))
                        BookmarkButton(userExternalNewsResource.isBookmarked, onToggleBookmark)
                    }
                    Spacer(modifier = Modifier.height(14.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (!userExternalNewsResource.sourceIconUrl.isNullOrEmpty()) {
                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .clip(RoundedCornerShape(4.dp)),
                            ) {
                                NewsResourceHeaderImage(
                                    headerImageUrl = userExternalNewsResource.sourceIconUrl,
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                        }
                        Text(
                            text = userExternalNewsResource.source,
                            style = MaterialTheme.typography.labelSmall,
                        )
                    }
                    Spacer(modifier = Modifier.height(14.dp))
                    val category = userExternalNewsResource.category
                    if (!category.isNullOrEmpty()) {
                        NiaTopicTag(
                            followed = true,
                            onClick = { },
                            text = {
                                Text(
                                    text = category.uppercase(Locale.getDefault()),
                                )
                            },
                        )
                    }
                }
            }
        }
    }
}
