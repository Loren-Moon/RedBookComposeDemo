@file:OptIn(ExperimentalSharedTransitionApi::class)

package com.loren.redbook.base

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.loren.redbook.data.LocalAnimatedVisibilityScope
import com.loren.redbook.data.LocalNavHostSharedTransitionScope
import com.loren.redbook.data.R
import com.loren.redbook.data.bean.ContentBean
import com.loren.redbook.data.ext.cancelRipperClick
import com.loren.redbook.data.util.NumberUtil
import com.loren.redbook.theme.RedBookTheme

@Composable
fun ContentItem(
    contentBean: ContentBean,
    navigateToDetail: (ContentBean) -> Unit
) {
    val sharedTransitionScope = LocalNavHostSharedTransitionScope.current ?: return
    val animatedContentScope = LocalAnimatedVisibilityScope.current ?: return
    with(sharedTransitionScope) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .clip(RoundedCornerShape(2.dp))
                .background(color = RedBookTheme.colors.background)
                .sharedBounds(
                    sharedContentState = rememberSharedContentState(key = contentBean.id),
                    animatedVisibilityScope = animatedContentScope,
                    resizeMode = SharedTransitionScope.ResizeMode.RemeasureToBounds
                )
                .cancelRipperClick {
                    navigateToDetail(contentBean)
                }
        ) {
            Box {
                AsyncImage(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                        .sharedBounds(
                            sharedContentState = rememberSharedContentState(key = "${contentBean.id}-${contentBean.pic}"),
                            animatedVisibilityScope = animatedContentScope
                        ),
                    model = contentBean.pic,
                    contentDescription = "pic",
                    contentScale = ContentScale.FillWidth
                )

                if (contentBean.isVideo) {
                    Icon(
                        modifier = Modifier
                            .padding(8.dp)
                            .size(16.dp)
                            .align(Alignment.TopEnd)
                            .background(Color(0x80333333), RoundedCornerShape(16.dp))
                            .padding(start = 5.dp, top = 4.dp, end = 3.dp, bottom = 4.dp),
                        painter = painterResource(id = R.drawable.icon_video),
                        contentDescription = "video",
                        tint = Color.White
                    )
                }
            }

            Text(
                text = contentBean.title,
                style = RedBookTheme.textStyle.titleSmall,
                fontWeight = FontWeight.Bold,
                color = RedBookTheme.colors.title,
                modifier = Modifier
                    .padding(4.dp)
                    .sharedBounds(
                        sharedContentState = rememberSharedContentState(key = "${contentBean.id}-${contentBean.title}"),
                        animatedVisibilityScope = animatedContentScope
                    )
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AsyncImage(
                    model = contentBean.user.headImg,
                    contentDescription = "headImg",
                    modifier = Modifier
                        .size(16.dp)
                        .sharedBounds(
                            sharedContentState = rememberSharedContentState(key = "${contentBean.id}-${contentBean.user.headImg}"),
                            animatedVisibilityScope = animatedContentScope
                        )
                        .clip(RoundedCornerShape(16.dp))
                        .border(.5.dp, RedBookTheme.colors.divider, RoundedCornerShape(16.dp))
                )
                Text(
                    text = contentBean.user.userName,
                    color = RedBookTheme.colors.body,
                    style = RedBookTheme.textStyle.bodySmall,
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 4.dp)
                        .sharedBounds(
                            sharedContentState = rememberSharedContentState(key = "${contentBean.id}-${contentBean.user.userName}"),
                            animatedVisibilityScope = animatedContentScope
                        )
                )
                Text(
                    text = "❤︎ ${
                        NumberUtil.round2StringWithChinese(
                            value = contentBean.likeNum.toDouble(),
                            fraction = 1
                        )
                    }",
                    color = RedBookTheme.colors.body,
                    style = RedBookTheme.textStyle.bodySmall,
                )
            }
        }
    }

}
