package com.example.teashop.screen.screen.feedback_screen

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import com.example.teashop.R
import com.example.teashop.data.model.product.ProductFull
import com.example.teashop.data.model.saves.ReviewSave
import com.example.teashop.data.storage.TokenStorage
import com.example.teashop.navigation.common.Navigation
import com.example.teashop.reusable_interface.buttons.MakeAgreeBottomButton
import com.example.teashop.reusable_interface.cards.MakeTopCard
import com.example.teashop.ui.theme.Black10
import com.example.teashop.ui.theme.Green10
import com.example.teashop.ui.theme.Grey10
import com.example.teashop.ui.theme.Grey20
import com.example.teashop.ui.theme.White10
import com.example.teashop.ui.theme.Yellow10
import com.example.teashop.ui.theme.montserratFamily
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import java.io.IOException
import java.io.InputStream
import java.util.UUID

@Composable
fun LaunchNewFeedbackScreen(navController: NavController, product: ProductFull?){
    product?.let { p ->
        Navigation(navController = navController) {
            MakeNewFeedbackScreen(navController = navController, product = p)
        }
    }
}

@Composable
fun MakeNewFeedbackScreen(
    navController: NavController,
    product: ProductFull,
    viewModel: FeedbackViewModel = viewModel()
){
    val imageList = remember { mutableStateListOf<Uri>() }
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        imageList.add(uri!!)
    }
    var userFeedbackRate by remember{ mutableIntStateOf(0) }
    var userFeedbackContent by remember{ mutableStateOf("") }
    var userRateCnt = 1
    val imagesView by viewModel.images.observeAsState()
    val context = LocalContext.current
    val tokenStorage = remember {
        TokenStorage()
    }

    Column(
        modifier = Modifier.fillMaxHeight(),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            MakeTopCard(
                drawableId = R.drawable.back_arrow,
                text = "Ваш отзыв",
                navController = navController
            )
            Card(
                colors = CardDefaults.cardColors(containerColor = White10),
                shape = RectangleShape,
                modifier = Modifier
                    .padding(bottom = 10.dp)
                    .height(100.dp)
                    .fillMaxWidth()
                    .shadow(elevation = 3.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 10.dp, top = 5.dp)
                ) {
                    Image(
                        painter = rememberAsyncImagePainter(model = product.images[0].imageUrl),
                        modifier = Modifier
                            .padding(end = 10.dp)
                            .size(50.dp),
                        contentDescription = null,
                        contentScale = ContentScale.FillBounds
                    )
                    Text(
                        text = product.title,
                        fontFamily = montserratFamily,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.W400
                    )
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 10.dp, top = 10.dp)
                ) {
                    while (userRateCnt < 6) {
                        RateStarIcon(
                            userRateCnt,
                            userRate = { userFeedbackRate = it },
                            userFeedbackRate
                        )
                        userRateCnt++
                    }
                    userRateCnt = 1
                }
            }
            Card(
                colors = CardDefaults.cardColors(containerColor = White10),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .padding(start = 10.dp, top = 10.dp, end = 10.dp, bottom = 5.dp)
                        .fillMaxWidth()
                ) {
                    Text(
                        text = "Фотографии",
                        fontFamily = montserratFamily,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.W400
                    )
                    Text(
                        text = "До 3 штук",
                        fontFamily = montserratFamily,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.W400,
                        color = Grey10
                    )
                }
                Row(
                    modifier = Modifier
                        .padding(bottom = 5.dp, end = 10.dp)
                        .fillMaxWidth(),
                ) {
                    if (imageList.size < 3) {
                        Button(
                            colors = ButtonDefaults.buttonColors(containerColor = Grey20),
                            shape = RoundedCornerShape(15.dp),
                            onClick = {
                                launcher.launch("image/*")
                            },
                            contentPadding = PaddingValues(0.dp),
                            modifier = Modifier
                                .padding(start = 10.dp)
                                .size(100.dp)
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.add_image_icon),
                                tint = Green10,
                                modifier = Modifier.size(30.dp),
                                contentDescription = null
                            )
                        }
                    }
                    if (imageList.isNotEmpty()) {
                        for (i in 0..<imageList.size) {
                            MakeImage(i, imageList)
                        }
                    }
                }
                Text(
                    text = "Ваш отзыв",
                    fontFamily = montserratFamily,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.W400,
                    modifier = Modifier.padding(start = 10.dp, bottom = 5.dp)
                )
                TextField(
                    value = userFeedbackContent,
                    onValueChange = {
                        userFeedbackContent = it.take(299)
                    },
                    shape = RoundedCornerShape(15.dp),
                    colors = TextFieldDefaults.colors(
                        focusedIndicatorColor = White10,
                        unfocusedIndicatorColor = White10,
                        focusedContainerColor = Grey20,
                        unfocusedContainerColor = Grey20,
                        disabledContainerColor = Black10,
                        disabledTextColor = Black10,
                        focusedTextColor = Black10,
                    ),
                    keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
                    modifier = Modifier
                        .padding(start = 10.dp, end = 10.dp, bottom = 10.dp)
                        .fillMaxWidth(),
                    maxLines = 20
                )
            }
        }
        MakeAgreeBottomButton(onClick = {
            if (userFeedbackContent.trim().isEmpty() || userFeedbackRate == 0) {
                Toast.makeText(context, "Укажите рейтинг и отзыв", Toast.LENGTH_SHORT).show()
                return@MakeAgreeBottomButton
            }
            runBlocking {
                val parts = imageList.mapIndexed { _, uri ->
                    uriToMultipartPart(context, uri)
                }
                tokenStorage.getToken(context)?.let {
                    viewModel.saveReview(
                        parts,
                        it,
                        ReviewSave(
                            productId = product.id,
                            images = imagesView ?: listOf(),
                            reviewText = userFeedbackContent,
                            stars = userFeedbackRate.toShort()
                        ),
                        onSuccess = {
                            Toast.makeText(context, "Ваш отзыв успешно добавлен!", Toast.LENGTH_SHORT)
                                .show()
                            navController.popBackStack()
                        },
                        onError = {
                            Toast.makeText(
                                context,
                                "Не удалось сохранить отзыв",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    )
                }
            }
        }, text = "Оставить отзыв")
    }
}

suspend fun uriToMultipartPart(context: Context, uri: Uri): MultipartBody.Part {
    return withContext(Dispatchers.IO) {
        if (uri.scheme != ContentResolver.SCHEME_CONTENT) {
            val client = OkHttpClient()
            val request = Request.Builder().url(uri.toString()).build()

            try {
                val response = client.newCall(request).execute()
                if (!response.isSuccessful) throw IOException("Failed to download file: $response")

                val bytes = response.body?.bytes()
                if (bytes != null) {
                    val mediaType = "image/webp".toMediaTypeOrNull()
                    val requestBody = RequestBody.create(mediaType, bytes)
                    val uniqueFileName = "${UUID.randomUUID()}.webp"
                    return@withContext MultipartBody.Part.createFormData("files", uniqueFileName, requestBody)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        val contentResolver: ContentResolver = context.contentResolver
        val inputStream: InputStream? = contentResolver.openInputStream(uri)
        val mediaType = contentResolver.getType(uri)?.toMediaTypeOrNull()

        val requestBody = inputStream?.let {
            RequestBody.create(mediaType, it.readBytes())
        } ?: throw IllegalArgumentException("Невозможно загрузить изображение")
        val uniqueFileName = "${UUID.randomUUID()}.webp"

        return@withContext MultipartBody.Part.createFormData("files", uniqueFileName, requestBody)
    }
}

@Composable
fun RateStarIcon(starNumber: Int, userRate:(Int) -> Unit, rateTracker: Int){
    val starColor: Color = if(starNumber>rateTracker){
        Grey10
    } else Yellow10
    Icon(
        painter = painterResource(R.drawable.star),
        tint = starColor,
        modifier = Modifier
            .padding(end = 20.dp)
            .size(30.dp)
            .clip(RoundedCornerShape(15.dp))
            .clickable(onClick = { userRate(starNumber) }),
        contentDescription = null
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MakeImage(number: Int, imageList: SnapshotStateList<Uri>) {
    val imageUri = imageList[number]

    AsyncImage(
        model = imageUri,
        contentDescription = null,
        modifier = Modifier
            .padding(start = 10.dp)
            .size(100.dp)
            .clip(RoundedCornerShape(12.dp))
            .combinedClickable(
                onClick = {},
                onLongClick = {
                    imageList.removeAt(number)
                }
            ),
        contentScale = ContentScale.Crop,
    )
}