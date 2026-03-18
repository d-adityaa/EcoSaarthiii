package com.example.ecosaarthi

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    EcoSaarthiApp()
                }
            }
        }
    }
}

enum class BottomTab(val title: String, val emoji: String) {
    HOME("Home", "⌂"),
    TOOLS("Tools", "🛠"),
    VISIT("Visit List", "📍"),
    SETTINGS("Settings", "⚙")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EcoSaarthiApp() {
    val context = LocalContext.current
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    var selectedTab by remember { mutableStateOf(BottomTab.HOME) }
    var selectedMode by remember { mutableStateOf("Offline") }
    var connectivity by remember {
        mutableStateOf(
            if (hasGoodInternet(context)) "Online (validated)" else "Offline"
        )
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                modifier = Modifier.width(280.dp),
                drawerContainerColor = Color.White
            ) {
                DrawerContent(
                    onItemClick = { label ->
                        scope.launch { drawerState.close() }
                        Toast.makeText(context, label, Toast.LENGTH_SHORT).show()
                    }
                )
            }
        }
    ) {
        Scaffold(
            containerColor = Color(0xFFF4F7F2),
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        Text(
                            text = "EcoSaarthi",
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFF103B2C)
                        )
                    },
                    navigationIcon = {
                        IconButton(
                            onClick = { scope.launch { drawerState.open() } }
                        ) {
                            Text("☰", fontSize = 22.sp)
                        }
                    },
                    actions = {
                        IconButton(onClick = {
                            Toast.makeText(context, "Notifications", Toast.LENGTH_SHORT).show()
                        }) {
                            Text("🔔", fontSize = 18.sp)
                        }
                        IconButton(onClick = {
                            Toast.makeText(context, "Profile", Toast.LENGTH_SHORT).show()
                        }) {
                            Text("👤", fontSize = 18.sp)
                        }
                    }
                )
            },
            bottomBar = {
                BottomBar(
                    selectedTab = selectedTab,
                    onTabSelected = { selectedTab = it }
                )
            }
        ) { innerPadding ->

            when (selectedTab) {
                BottomTab.HOME -> HomeScreen(
                    paddingValues = innerPadding,
                    connectivity = connectivity,
                    selectedMode = selectedMode,
                    onRefresh = {
                        connectivity = if (hasGoodInternet(context)) {
                            "Online (validated)"
                        } else {
                            "Offline"
                        }
                        Toast.makeText(context, "Status refreshed", Toast.LENGTH_SHORT).show()
                    },
                    onOnlineClick = {
                        if (hasGoodInternet(context)) {
                            selectedMode = "Online"
                            Toast.makeText(context, "Online Mode opened", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(
                                context,
                                "Good internet required for Online Mode",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    },
                    onOfflineClick = {
                        selectedMode = "Offline"
                        Toast.makeText(context, "Offline Mode opened", Toast.LENGTH_SHORT).show()
                    },
                    onQuickAction = { action ->
                        Toast.makeText(context, action, Toast.LENGTH_SHORT).show()
                    }
                )

                BottomTab.TOOLS -> ToolsScreen(
                    paddingValues = innerPadding
                )

                BottomTab.VISIT -> PlaceholderScreen(
                    paddingValues = innerPadding,
                    title = "Visit List",
                    subtitle = "Saved places, zone visits, field notes"
                )

                BottomTab.SETTINGS -> PlaceholderScreen(
                    paddingValues = innerPadding,
                    title = "Settings",
                    subtitle = "Profile, preferences, offline data"
                )
            }
        }
    }
}

@Composable
fun DrawerContent(onItemClick: (String) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(18.dp)
    ) {
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "EcoSaarthi",
            fontSize = 24.sp,
            fontWeight = FontWeight.ExtraBold,
            color = Color(0xFF114232)
        )
        Text(
            text = "Offline-first eco tourism assistant",
            color = Color(0xFF5F6F65),
            fontSize = 13.sp
        )

        Spacer(modifier = Modifier.height(20.dp))
        HorizontalDivider()

        DrawerItem("🆘 SOS Emergency") { onItemClick("SOS Emergency") }
        DrawerItem("📶 Connectivity Status Indicator") { onItemClick("Connectivity Status") }
        DrawerItem("📘 Emergency Survival Guide") { onItemClick("Emergency Survival Guide") }

        Spacer(modifier = Modifier.height(16.dp))
        HorizontalDivider()
        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Tip",
            fontWeight = FontWeight.Bold,
            color = Color(0xFF114232)
        )
        Text(
            text = "Keep the offline kit ready for no-network zones.",
            color = Color(0xFF66756D),
            fontSize = 13.sp
        )
    }
}

@Composable
fun DrawerItem(text: String, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 12.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFFF6F8F5))
            .clickable { onClick() }
            .padding(14.dp)
    ) {
        Text(
            text = text,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF173D2E)
        )
    }
}

@Composable
fun HomeScreen(
    paddingValues: PaddingValues,
    connectivity: String,
    selectedMode: String,
    onRefresh: () -> Unit,
    onOnlineClick: () -> Unit,
    onOfflineClick: () -> Unit,
    onQuickAction: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .padding(horizontal = 16.dp)
            .navigationBarsPadding()
    ) {
        Spacer(modifier = Modifier.height(8.dp))

        HeroCard(connectivity = connectivity)

        Spacer(modifier = Modifier.height(14.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            ModeCard(
                modifier = Modifier.weight(1f),
                emoji = "📶",
                title = "Online Mode",
                subtitle = "AI Guide · Requires internet",
                selected = selectedMode == "Online",
                borderColor = Color(0xFF7DD3A7),
                backgroundColor = Color(0xFFF7FBF8),
                onClick = onOnlineClick
            )

            ModeCard(
                modifier = Modifier.weight(1f),
                emoji = "📴",
                title = "Offline Mode",
                subtitle = "Local data · No connection needed",
                selected = selectedMode == "Offline",
                borderColor = Color(0xFFE6B94F),
                backgroundColor = Color(0xFFFFF7DF),
                onClick = onOfflineClick
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        RefreshButton(onClick = onRefresh)

        Spacer(modifier = Modifier.height(18.dp))

        SectionTitle("Nearby Eco Zones")
        Spacer(modifier = Modifier.height(10.dp))
        NearbyZoneMapCard()

        Spacer(modifier = Modifier.height(18.dp))

        SectionTitle("Quick Actions")
        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            QuickActionCard(
                modifier = Modifier.weight(1f),
                emoji = "📞",
                title = "SOS Emergency",
                subtitle = "Call 112 · Always works",
                iconBg = Color(0xFFFFE6E4),
                onClick = { onQuickAction("SOS tapped") }
            )
            QuickActionCard(
                modifier = Modifier.weight(1f),
                emoji = "🧭",
                title = "Detect Region",
                subtitle = "Offline location quiz",
                iconBg = Color(0xFFE3F4E8),
                onClick = { onQuickAction("Detect Region tapped") }
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            QuickActionCard(
                modifier = Modifier.weight(1f),
                emoji = "☀",
                title = "Weather",
                subtitle = "Quick eco conditions",
                iconBg = Color(0xFFE7EEFF),
                onClick = { onQuickAction("Weather tapped") }
            )
            QuickActionCard(
                modifier = Modifier.weight(1f),
                emoji = "📘",
                title = "Guide Book",
                subtitle = "Offline resource kit",
                iconBg = Color(0xFFFFF0C9),
                onClick = { onQuickAction("Guide Book tapped") }
            )
        }

        Spacer(modifier = Modifier.height(12.dp))
    }
}

@Composable
fun HeroCard(connectivity: String) {
    Card(
        shape = RoundedCornerShape(26.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        modifier = Modifier.fillMaxWidth()
    ) {
        Box(
            modifier = Modifier
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(Color(0xFF16533D), Color(0xFF4FA36F))
                    )
                )
                .padding(20.dp)
        ) {
            Column {
                Text(
                    text = "📍 Kanha Forest Zone, MP",
                    color = Color(0xFFD9F1E1),
                    fontSize = 13.sp
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "EcoSaarthi",
                    color = Color.White,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.ExtraBold
                )

                Text(
                    text = "AI when online · Local intelligence when offline",
                    color = Color(0xFFE6F7EA),
                    fontSize = 14.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = connectivity,
                    color = Color.White,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 13.sp
                )
            }

            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .size(90.dp)
                    .clip(CircleShape)
                    .background(Color(0x22FFFFFF))
            )
        }
    }
}

@Composable
fun ModeCard(
    modifier: Modifier = Modifier,
    emoji: String,
    title: String,
    subtitle: String,
    selected: Boolean,
    borderColor: Color,
    backgroundColor: Color,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .height(136.dp)
            .border(
                width = 1.dp,
                color = borderColor,
                shape = RoundedCornerShape(22.dp)
            )
            .clickable { onClick() },
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color.White.copy(alpha = 0.7f)),
                contentAlignment = Alignment.Center
            ) {
                Text(text = emoji, fontSize = 20.sp)
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = title,
                fontWeight = FontWeight.ExtraBold,
                color = Color(0xFF102519)
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = subtitle,
                fontSize = 12.sp,
                color = Color(0xFF567465)
            )

            if (selected) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "✓ Selected",
                    fontSize = 12.sp,
                    color = Color(0xFF2B8A57),
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun RefreshButton(onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(18.dp))
            .background(Color(0xFF1E5B45))
            .clickable { onClick() }
            .padding(horizontal = 18.dp, vertical = 10.dp)
    ) {
        Text(
            text = "Refresh Status",
            color = Color.White,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
fun NearbyZoneMapCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFEAF2E1))
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(185.dp)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                Row(modifier = Modifier.weight(1f)) {
                    MapCell(Color(0xFFC9DBBE))
                    MapCell(Color(0xFFBDD3B0))
                    MapCell(Color(0xFFCDE1C2))
                    MapCell(Color(0xFFC1D7B7))
                }
                Row(modifier = Modifier.weight(1f)) {
                    MapCell(Color(0xFFBDD4AE))
                    MapCell(Color(0xFFCFE1C4))
                    MapCell(Color(0xFFBCD3AF))
                    MapCell(Color(0xFFCDE0C0))
                }
                Row(modifier = Modifier.weight(1f)) {
                    MapCell(Color(0xFFC8DBBD))
                    MapCell(Color(0xFFBFD4B3))
                    MapCell(Color(0xFFCEE0C2))
                    MapCell(Color(0xFFC4D8B8))
                }
            }

            ZoneBubble("Tiger Reserve", modifier = Modifier.align(Alignment.TopCenter).padding(top = 42.dp))
            ZoneBubble("Compost Hub", modifier = Modifier.align(Alignment.BottomStart).padding(start = 12.dp, bottom = 34.dp))
            ZoneBubble("Solar Farm", modifier = Modifier.align(Alignment.CenterEnd).padding(end = 28.dp))

            Box(
                modifier = Modifier
                    .align(Alignment.Center)
                    .size(26.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF2E6B54)),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFC6F6D5))
                )
            }

            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(14.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .background(Color(0xFF184B38))
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text(
                    text = "Explore",
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }

            Text(
                text = "Kanha Region · 3 sites nearby",
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(start = 14.dp, bottom = 16.dp),
                color = Color(0xFF476554),
                fontWeight = FontWeight.SemiBold,
                fontSize = 13.sp
            )
        }
    }
}

@Composable
fun MapCell(color: Color) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color)
    )
}

@Composable
fun ZoneBubble(text: String, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(Color.White.copy(alpha = 0.95f))
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Text(
            text = text,
            fontSize = 12.sp,
            color = Color(0xFF234132),
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
fun QuickActionCard(
    modifier: Modifier = Modifier,
    emoji: String,
    title: String,
    subtitle: String,
    iconBg: Color,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .height(118.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(iconBg),
                contentAlignment = Alignment.Center
            ) {
                Text(text = emoji, fontSize = 20.sp)
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = title,
                fontWeight = FontWeight.ExtraBold,
                color = Color(0xFF12281E)
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = subtitle,
                fontSize = 12.sp,
                color = Color(0xFF688071)
            )
        }
    }
}

@Composable
fun SectionTitle(text: String) {
    Text(
        text = text,
        fontSize = 18.sp,
        fontWeight = FontWeight.ExtraBold,
        color = Color(0xFF102519)
    )
}

@Composable
fun BottomBar(
    selectedTab: BottomTab,
    onTabSelected: (BottomTab) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(horizontal = 10.dp, vertical = 10.dp)
            .navigationBarsPadding(),
        horizontalArrangement = Arrangement.SpaceAround
    ) {
        BottomTab.values().forEach { tab ->
            val selected = tab == selectedTab
            Column(
                modifier = Modifier
                    .clip(RoundedCornerShape(18.dp))
                    .background(if (selected) Color(0xFFE7F4EA) else Color.Transparent)
                    .clickable { onTabSelected(tab) }
                    .padding(horizontal = 14.dp, vertical = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = tab.emoji,
                    fontSize = 18.sp
                )
                Spacer(modifier = Modifier.height(3.dp))
                Text(
                    text = tab.title,
                    fontSize = 11.sp,
                    fontWeight = if (selected) FontWeight.ExtraBold else FontWeight.Medium,
                    color = if (selected) Color(0xFF1A5A43) else Color(0xFF708276)
                )
            }
        }
    }
}

@Composable
fun PlaceholderScreen(
    paddingValues: PaddingValues,
    title: String,
    subtitle: String
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = title,
            fontSize = 28.sp,
            fontWeight = FontWeight.ExtraBold,
            color = Color(0xFF103B2C)
        )
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = subtitle,
            color = Color(0xFF64786E)
        )
    }
}

fun hasGoodInternet(context: Context): Boolean {
    val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    val network = connectivityManager.activeNetwork ?: return false
    val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false

    val hasInternet = capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    val validated = capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)

    return hasInternet && validated
}fun calculateSafetyScore(): Int {
    val weatherRisk = 10
    val wildlifeRisk = 30
    val networkRisk = 20
    val helpDistanceRisk = 18

    val totalRisk = weatherRisk + wildlifeRisk + networkRisk + helpDistanceRisk
    return 100 - totalRisk
}

@Composable
fun ToolsScreen(
    paddingValues: PaddingValues
) {
    var safetyScore by remember { mutableStateOf(calculateSafetyScore()) }
    var weatherRisk by remember { mutableStateOf("Low") }
    var wildlifeRisk by remember { mutableStateOf("Medium") }
    var networkRisk by remember { mutableStateOf("Low") }
    var nearestHelp by remember { mutableStateOf("8 km") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .padding(16.dp)
    ) {
        Text(
            text = "Forest Safety Score",
            fontSize = 24.sp,
            fontWeight = FontWeight.ExtraBold,
            color = Color(0xFF103B2C)
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Smart risk summary for the current eco zone",
            fontSize = 14.sp,
            color = Color(0xFF64786E)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "$safetyScore / 100",
                    fontSize = 34.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = when {
                        safetyScore >= 80 -> Color(0xFF1B8A4A)
                        safetyScore >= 50 -> Color(0xFFE6A700)
                        else -> Color(0xFFD64545)
                    }
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = when {
                        safetyScore >= 80 -> "Safe Zone"
                        safetyScore >= 50 -> "Moderate Risk"
                        else -> "High Risk"
                    },
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF476554)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        SafetyReasonCard("🌧 Weather risk", weatherRisk)
        Spacer(modifier = Modifier.height(10.dp))
        SafetyReasonCard("🐘 Wildlife activity", wildlifeRisk)
        Spacer(modifier = Modifier.height(10.dp))
        SafetyReasonCard("📶 Network availability", networkRisk)
        Spacer(modifier = Modifier.height(10.dp))
        SafetyReasonCard("🚑 Nearest help distance", nearestHelp)

        Spacer(modifier = Modifier.height(20.dp))

        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(18.dp))
                .background(Color(0xFF1E5B45))
                .clickable {
                    safetyScore = calculateSafetyScore()
                    weatherRisk = "Low"
                    wildlifeRisk = "Medium"
                    networkRisk = "Low"
                    nearestHelp = "8 km"
                }
                .padding(horizontal = 18.dp, vertical = 10.dp)
        ) {
            Text(
                text = "Refresh Score",
                color = Color.White,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
fun SafetyReasonCard(label: String, value: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = label,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF173D2E)
            )

            Text(
                text = value,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF5F6F65)
            )
        }
    }
}