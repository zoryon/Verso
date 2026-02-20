package it.zoryon.verso.core.ui.components

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import it.zoryon.verso.core.navigation.NavItem
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.runtime.remember
import androidx.compose.foundation.clickable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.graphicsLayer

@Composable
fun FloatingBottomBar(
    items: List<NavItem>,
    selectedItem: Int,
    onItemSelected: (Int) -> Unit,
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 20.dp),
        shape = RoundedCornerShape(100),
        color = Color.White,
        shadowElevation = 12.dp,
    ) {
        NavigationBar(
            containerColor = Color.Transparent,
            tonalElevation = 0.dp,
            modifier = Modifier
                .height(64.dp)
                .clip(RoundedCornerShape(100)),
            windowInsets = WindowInsets(0.dp, 0.dp, 0.dp, 0.dp)
        ) {
            items.forEachIndexed { index, item ->
                val isSelected = selectedItem == index
                val interactionSource = remember { MutableInteractionSource() }

                // Scale (size) animation
                val backgroundScale by animateFloatAsState(
                    targetValue = if (isSelected) 1f else 0.8f,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioNoBouncy,
                        stiffness = Spring.StiffnessLow
                    ),
                    label = "PillScale"
                )

                // Opacity animation
                val backgroundAlpha by animateFloatAsState(
                    targetValue = if (isSelected) 1f else 0f,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioNoBouncy,
                        stiffness = Spring.StiffnessLow
                    ),
                    label = "PillAlpha"
                )

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .padding(vertical = 5.dp)
                        .fillMaxHeight()
                        .clickable(
                            interactionSource = interactionSource,
                            indication = null
                        ) {
                            onItemSelected(index)
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .graphicsLayer {
                                scaleX = backgroundScale
                                scaleY = backgroundScale
                                alpha = backgroundAlpha
                            }
                            .fillMaxHeight()
                            .width(100.dp)
                            .clip(RoundedCornerShape(45.dp))
                            .background(
                                if (isSelected) Color(0x1A229ED9)
                                else Color.Transparent
                            )
                            .padding(vertical = 1.dp),
                        contentAlignment = Alignment.Center,
                    ) { }

                    Column (
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = item.icon,
                            contentDescription = item.label,
                            tint = if (isSelected) Color(0xFF229ED9) else Color.Gray,
                            modifier = Modifier.size(24.dp)
                        )

                        Text(
                            text = item.label,
                            fontSize = 13.sp,
                            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                            color = if (isSelected) Color(0xFF229ED9) else Color.Gray
                        )
                    }
                }
            }
        }
    }
}