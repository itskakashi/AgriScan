//package com.example.agriscan.presentation.home
//
//import androidx.annotation.DrawableRes
//import androidx.compose.foundation.background
//import androidx.compose.foundation.clickable
//import androidx.compose.foundation.interaction.MutableInteractionSource
//import androidx.compose.foundation.layout.*
//import androidx.compose.foundation.shape.CircleShape
//import androidx.compose.material3.BottomAppBar
//import androidx.compose.material3.Icon
//import androidx.compose.material3.IconButton
//import androidx.compose.material3.Text
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.remember
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.draw.clip
//import androidx.compose.ui.draw.shadow
//import androidx.compose.ui.geometry.CornerRadius
//import androidx.compose.ui.geometry.Rect
//import androidx.compose.ui.geometry.RoundRect
//import androidx.compose.ui.geometry.Size
//import androidx.compose.ui.graphics.*
//import androidx.compose.ui.res.painterResource
//import androidx.compose.ui.tooling.preview.Preview
//import androidx.compose.ui.unit.*
//import com.example.agriscan.R
//
///* palette */
//private val BarGreen   = Color(0xFF2A5A35)
//private val AccentLime = Color(0xFFCBF535)
//
///* sizes (same look) */
//private val BarCornerRadius     = 50.dp
//private val BarHeight           = 96.dp
//private val FabDiameter         = 80.dp
//private val ScanIconSize        = 60.dp
//private val WhiteDiscDiameter   = 48.dp
//private val GreenRimThickness   = 3.dp
//private val UnderBumpSideGap    = 12.dp
//
///* nudges */
//private val BumpCenterNudgeY    = 5.dp
//private val WhiteDiscNudgeY     = 0.dp
//private val FabNudgeY           = 4.dp
//private val NavIconSize         = 70.dp
//private val LabelBottomPadding  = 10.dp
//private val SelectedIconLift    = (-6).dp
//
///** Normal bottom bar (use in Scaffold.bottomBar). No NavItem list — all items explicit. */
//@Composable
//fun CustomBottomBar(
//    selectedRoute: String = "home",
//    onNavItemClick: (String) -> Unit = {},
//    onScanClick: () -> Unit = {},
//    modifier: Modifier = Modifier,
//    scanLift: Dp = (-50).dp
//) {
//    val bumpShape = remember {
//        CenterBumpBarShape(
//            bumpRadius       = WhiteDiscDiameter / 2 + GreenRimThickness,
//            barCorner        = BarCornerRadius,
//            bumpCenterNudgeY = BumpCenterNudgeY
//        )
//    }
//    val centerGap = maxOf(FabDiameter, WhiteDiscDiameter) + (UnderBumpSideGap * 2)
//
//    Box(
//        modifier = modifier
//            .fillMaxWidth()
//            .height(BarHeight + FabDiameter / 2) // headroom for the raised scan icon
//    ) {
//        BottomAppBar(
//            modifier = Modifier
//                .fillMaxWidth()
//                .align(Alignment.BottomCenter)
//                .shadow(10.dp, shape = bumpShape)
//                .height(BarHeight)
//                .clip(bumpShape),
//            containerColor = BarGreen,
//            contentColor = Color.Transparent,
//            tonalElevation = 0.dp,
//            windowInsets = WindowInsets(0.dp)
//        ) {
//            Row(
//                modifier = Modifier.fillMaxWidth(),
//                verticalAlignment = Alignment.CenterVertically
//            ) {
//                // LEFT SIDE (Home, Guide)
//                Row(
//                    modifier = Modifier
//                        .weight(1f)
//                        .fillMaxHeight(),
//                    horizontalArrangement = Arrangement.SpaceEvenly,
//                    verticalAlignment = Alignment.CenterVertically
//                ) {
//                    BottomBarIcon(
//                        iconRes = R.drawable.ic_home,
//                        label = "Home",
//                        selected = selectedRoute == "home",
//                        onClick = { onNavItemClick("home") }
//                    )
//                    BottomBarIcon(
//                        iconRes = R.drawable.ic_guide,
//                        label = "Guide",
//                        selected = selectedRoute == "guide",
//                        onClick = { onNavItemClick("guide") }
//                    )
//                }
//
//                // CENTER GAP under the bump
//                Spacer(Modifier.width(centerGap))
//
//                // RIGHT SIDE (Tutorial, Profile)
//                Row(
//                    modifier = Modifier
//                        .weight(1f)
//                        .fillMaxHeight(),
//                    horizontalArrangement = Arrangement.SpaceEvenly,
//                    verticalAlignment = Alignment.CenterVertically
//                ) {
//                    BottomBarIcon(
//                        iconRes = R.drawable.ic_tutorial,
//                        label = "Tutorial",
//                        selected = selectedRoute == "tutorial",
//                        onClick = { onNavItemClick("tutorial") }
//                    )
//                    BottomBarIcon(
//                        iconRes = R.drawable.ic_profile,
//                        label = "Profile",
//                        selected = selectedRoute == "profile",
//                        onClick = { onNavItemClick("profile") }
//                    )
//                }
//            }
//        }
//
//        // Optional white disc inside bump (remove if you don’t want it)
//        Box(
//            modifier = Modifier
//                .size(WhiteDiscDiameter)
//                .align(Alignment.TopCenter)
//                .offset(y = BumpCenterNudgeY + WhiteDiscNudgeY)
//                .background(Color.White, CircleShape)
//        )
//
//        // Raised scan FAB (not clipped)
//        IconButton(
//            onClick = onScanClick,
//            modifier = Modifier
//                .size(FabDiameter)
//                .align(Alignment.TopCenter)
//                .offset(y = scanLift + BumpCenterNudgeY + FabNudgeY)
//                .background(AccentLime, CircleShape)
//        ) {
//            Icon(
//                painter = painterResource(id = R.drawable.bottom_bar_scna),
//                contentDescription = "Scan",
//                tint = Color.Unspecified,
//                modifier = Modifier.size(ScanIconSize)
//            )
//        }
//    }
//}
//
///* Explicit icon item (no models/lists). */
//@Composable
//private fun RowScope.BottomBarIcon(
//    @DrawableRes iconRes: Int,
//    label: String,
//    selected: Boolean,
//    onClick: () -> Unit
//) {
//    val color = if (selected) AccentLime else Color.White
//    val yOff  = if (selected) SelectedIconLift else 0.dp
//
//    Box(
//        modifier = Modifier
//            .weight(1f)
//            .fillMaxHeight()
//            .clickable(
//                onClick = onClick,
//                indication = null,
//                interactionSource = remember { MutableInteractionSource() }
//            ),
//        contentAlignment = Alignment.Center
//    ) {
//        Icon(
//            painter = painterResource(id = iconRes),
//            contentDescription = label,
//            tint = color,
//            modifier = Modifier
//                .size(NavIconSize)
//                .offset(y = yOff)
//        )
//        if (selected) {
//            Text(
//                text = label,
//                color = color,
//                fontSize = 12.sp,
//                modifier = Modifier
//                    .align(Alignment.BottomCenter)
//                    .padding(bottom = LabelBottomPadding)
//            )
//        }
//    }
//}
//
///* Shape for convex bump (bar UNION circle). */
//private class CenterBumpBarShape(
//    private val bumpRadius: Dp,
//    private val barCorner: Dp,
//    private val bumpCenterNudgeY: Dp = 0.dp
//) : Shape {
//    override fun createOutline(size: Size, layoutDirection: LayoutDirection, density: Density): Outline {
//        val rCorner = with(density) { barCorner.toPx() }
//        val rBump   = with(density) { bumpRadius.toPx() }
//        val nudgeY  = with(density) { bumpCenterNudgeY.toPx() }
//
//        val w = size.width
//        val h = size.height
//        val cx = w / 2f
//        val cy = -rBump + nudgeY
//
//        val base = Path().apply {
//            addRoundRect(RoundRect(0f, 0f, w, h, CornerRadius(rCorner, rCorner)))
//        }
//        val bump = Path().apply {
//            addOval(Rect(cx - rBump, cy - rBump, cx + rBump, cy + rBump))
//        }
//        val union = Path.combine(PathOperation.Union, base, bump)
//        return Outline.Generic(union)
//    }
//}
//
///* Quick preview (optional) */
//@Preview(showBackground = true, widthDp = 393)
//@Composable
//private fun CustomBottomBarPreview() {
//    Box(Modifier.fillMaxSize()) {
//        Column(
//            Modifier
//                .fillMaxSize()
//                .padding(bottom = 220.dp),
//            horizontalAlignment = Alignment.CenterHorizontally
//        ) {
//            repeat(14) { Text("Item $it", modifier = Modifier.padding(8.dp)) }
//        }
//
//        CustomBottomBar(
//            selectedRoute = "home",
//            onNavItemClick = {},
//            onScanClick = {},
//            modifier = Modifier
//                .align(Alignment.BottomCenter)
//                .fillMaxWidth(),
//            scanLift = (-50).dp
//        )
//    }
//}
