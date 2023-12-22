package pt.isec.amovtp.touristapp.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import pt.isec.amovtp.touristapp.R

data class Students (var name: String, var image: Painter, var number: String)

@Composable
fun CreditsScreen(modifier: Modifier = Modifier) {
    val students = listOf(
        Students("Acácio Agabalayeve Coutinho", painterResource(id = R.drawable.acacio), "2020141948"),
        Students("José Pedro Sousa Almeida", painterResource(id = R.drawable.jose), "2020141980")
    )

    Column (
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .fillMaxSize()
            .padding(14.dp)
    ) {
        Image(
            painter = painterResource(id = R.drawable.isec_logo),
            contentDescription = "ISEC logo",
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .fillMaxWidth()
                .size(120.dp)
        )
        Text(
            text = "Departamento de Engenharia Informática e de Sistemas",
            fontSize = 12.sp,
            modifier = Modifier.padding(4.dp)
        )
        Spacer(modifier = Modifier.height(18.dp))
        Text(
            text = "Practical Work Nº1 - Mobile Arquitecture",
            fontSize = 20.sp
        )
        Text(
            text = "Done by:",
            fontSize = 18.sp
        )
        Spacer(modifier = Modifier.height(18.dp))
        LazyRow (
            modifier = Modifier
                .fillMaxWidth()
        ) {
            items(students) {
                Card (
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    ),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.onPrimary),
                    modifier = Modifier
                        .fillMaxHeight()
                        .width(300.dp)
                        .padding(8.dp)
                        .clipToBounds()
                ) {
                    Column (
                        modifier = Modifier
                            .fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Image(
                            painter = it.image,
                            contentDescription = "Photo",
                            contentScale = ContentScale.Fit,
                            modifier = Modifier
                                .padding(6.dp)
                                .clip(CircleShape)
                                .border(
                                    BorderStroke(1.dp, MaterialTheme.colorScheme.onPrimary),
                                    CircleShape
                                )
                                .size(200.dp)
                        )
                        Text(
                            text = it.name,
                            modifier = Modifier.padding(4.dp)
                        )
                        Text(
                            text = it.number,
                            modifier = Modifier.padding(4.dp)
                        )
                    }
                }
            }
        }
    }
}