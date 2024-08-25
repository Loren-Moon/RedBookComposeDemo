@file:OptIn(ExperimentalSharedTransitionApi::class, ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class)

package com.loren.redbook.home

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.loren.redbook.data.LocalAnimatedVisibilityScope
import com.loren.redbook.data.LocalNavHostSharedTransitionScope
import com.loren.redbook.data.bean.ContentBean
import com.loren.redbook.data.ext.cancelRipperClick
import com.loren.redbook.theme.RedBookTheme

@Composable
fun HomeDetail(
    contentBean: ContentBean?,
    navigateToHome: () -> Unit
) {
    val sharedTransitionScope = LocalNavHostSharedTransitionScope.current ?: return
    val animatedContentScope = LocalAnimatedVisibilityScope.current ?: return
    with(sharedTransitionScope) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(RedBookTheme.colors.background)
                .sharedBounds(
                    sharedContentState = rememberSharedContentState(key = "${contentBean?.id}"),
                    animatedVisibilityScope = animatedContentScope,
                    resizeMode = SharedTransitionScope.ResizeMode.RemeasureToBounds,
                )
        ) {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(containerColor = RedBookTheme.colors.background),
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            modifier = Modifier.cancelRipperClick {
                                navigateToHome()
                            },
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = RedBookTheme.colors.icon
                        )

                        Spacer(modifier = Modifier.width(16.dp))

                        AsyncImage(
                            model = contentBean?.user?.headImg,
                            contentDescription = "headImg",
                            modifier = Modifier
                                .size(24.dp)
                                .sharedBounds(
                                    sharedContentState = rememberSharedContentState(key = "${contentBean?.id}-${contentBean?.user?.headImg}"),
                                    animatedVisibilityScope = animatedContentScope
                                )
                                .clip(RoundedCornerShape(24.dp))
                                .border(.5.dp, RedBookTheme.colors.divider, RoundedCornerShape(24.dp))

                        )
                        Text(
                            text = contentBean?.user?.userName ?: "",
                            color = RedBookTheme.colors.title,
                            style = RedBookTheme.textStyle.bodyMedium,
                            modifier = Modifier
                                .weight(1f)
                                .padding(horizontal = 4.dp)
                                .sharedBounds(
                                    sharedContentState = rememberSharedContentState(key = "${contentBean?.id}-${contentBean?.user?.userName}"),
                                    animatedVisibilityScope = animatedContentScope
                                )
                        )
                    }
                })
            AsyncImage(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .sharedBounds(
                        sharedContentState = rememberSharedContentState(key = "${contentBean?.id}-${contentBean?.pic}"),
                        animatedVisibilityScope = animatedContentScope
                    ),
                model = contentBean?.pic,
                contentDescription = "pic",
                contentScale = ContentScale.FillWidth
            )

            Text(
                text = contentBean?.title ?: "",
                style = RedBookTheme.textStyle.titleMedium,
                fontWeight = FontWeight.Bold,
                color = RedBookTheme.colors.title,
                modifier = Modifier
                    .padding(4.dp)
                    .sharedBounds(
                        sharedContentState = rememberSharedContentState(key = "${contentBean?.id}-${contentBean?.title}"),
                        animatedVisibilityScope = animatedContentScope
                    )
            )
        }
    }
}