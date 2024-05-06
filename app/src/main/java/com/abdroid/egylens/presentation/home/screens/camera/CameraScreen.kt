package com.abdroid.egylens.presentation.home.screens.camera

import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.view.CameraController
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.abdroid.egylens.MainActivity.MainActivity
import com.abdroid.egylens.R
import com.abdroid.egylens.presentation.common.ScanDialog
import com.abdroid.egylens.ui.theme.notoFont
import com.abdroid.egylens.util.Constants.BASE_URL
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import org.json.JSONObject
import java.io.IOException


@Composable
fun CameraScreen(
    navController: NavController,
    context: Context = LocalContext.current
) {

    val loading = remember { mutableStateOf(false) }

    val rawRes = if (loading.value) R.raw.loading_3 else R.raw.scanning_y

    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(rawRes))

    val progress by animateLottieCompositionAsState(
        composition,
        iterations = LottieConstants.IterateForever,
    )

    val controller = remember {
        LifecycleCameraController(
            context
        ).apply {
            setEnabledUseCases(
                CameraController.IMAGE_CAPTURE or
                        CameraController.VIDEO_CAPTURE
            )
        }
    }

    val cameraViewModel = hiltViewModel<CameraViewModel>()

    Box(
        Modifier
            .fillMaxSize()
    ) {

        val selectedImageUri = remember { mutableStateOf<Uri?>(null) }

        // ...

        val galleryLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.GetContent(),
            onResult = { uri: Uri? ->
                if (uri != null) {
                    // Set the selected image URI
                    selectedImageUri.value = uri
                }
            }
        )


        val lifecycleOwner = LocalLifecycleOwner.current
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = {
                PreviewView(it).apply {
                    this.controller = controller
                    controller.bindToLifecycle(lifecycleOwner)
                }
            }
        )
        Column (Modifier.fillMaxSize() , verticalArrangement = Arrangement.SpaceBetween){
            Row(
                Modifier.padding(vertical = 50.dp , horizontal = 20.dp),
                horizontalArrangement = Arrangement.Start ,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    modifier = Modifier
                        .clickable { navController.popBackStack() }
                        .size(35.dp),
                    painter = painterResource(id = R.drawable.close),
                    contentDescription ="",
                    tint = Color.White
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Focus on statue",
                    fontFamily = notoFont,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.White,
                )
            }
            Column (modifier = Modifier
                .fillMaxWidth()
                .height(350.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ){
                LottieAnimation(
                    composition = composition,
                    progress = { progress },
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 80.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {

                Box(
                    modifier = Modifier
                        .clip(CircleShape)
                        .size(45.dp)
                        .background(Color.White.copy(alpha = .5f))
                        .clickable {
                            galleryLauncher.launch("image/*")
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.gallery),
                        contentDescription = stringResource(R.string.open_gallery),
                        tint = Color.Black,
                        modifier = Modifier.size(26.dp)
                    )
                }
                Box(modifier = Modifier
                    .clip(CircleShape)
                    .size(90.dp)
                    .background(Color.White.copy(alpha = .3f))
                    .clickable {

                    },
                    contentAlignment = Alignment.Center)
                {
                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .size(70.dp)
                            .background(Color.White)
                            .clickable {
                                if ((context as MainActivity).arePermissionsGranted()) {
                                    cameraViewModel.onTakePhoto(controller)
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.scan),
                            contentDescription = stringResource(R.string.take_photo),
                            tint = colorResource(id = R.color.scanner_button),
                            modifier = Modifier.size(40.dp)
                        )
                    }
                }


                Box(
                    modifier = Modifier
                        .clip(CircleShape)
                        .size(45.dp)
                        .background(Color.White.copy(alpha = .5f))
                        .clickable {
                            controller.cameraSelector =
                                if (controller.cameraSelector == CameraSelector.DEFAULT_BACK_CAMERA) {
                                    CameraSelector.DEFAULT_FRONT_CAMERA
                                } else {
                                    CameraSelector.DEFAULT_BACK_CAMERA
                                }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.rotate),
                        contentDescription = stringResource(R.string.switch_camera_preview),
                        tint = Color.Black,
                        modifier = Modifier.size(26.dp)
                    )
                }

            }
        }

        var result by remember { mutableStateOf("") }
        val successDialog = remember { mutableStateOf(false) }
        val callMade = remember { mutableStateOf(false) }

        if (!callMade.value) {
            selectedImageUri.value?.let { uri ->
                if (!loading.value) {
                    loading.value = true // Set loading to true when the network call starts
                    val client = OkHttpClient()
                    val mediaType = "image/*".toMediaType() // Change media type according to your image type
                    val inputStream = context.contentResolver.openInputStream(uri)
                    val requestBody = inputStream?.readBytes()?.toRequestBody(mediaType)

                    val body = MultipartBody.Builder().setType(MultipartBody.FORM)
                        .addFormDataPart("image", uri.path, requestBody!!)
                        .build()

                    val request = Request.Builder()
                        .url(BASE_URL)
                        .post(body)
                        .build()

                    client.newCall(request).enqueue(object : Callback {
                        override fun onFailure(call: Call, e: IOException) {
                            Toast.makeText(context , "Error" , Toast.LENGTH_LONG).show()
                            e.printStackTrace()
                            loading.value = false // Set loading to false when the network call finishes
                        }

                        override fun onResponse(call: Call, response: Response) {
                            val responseBody = response.body?.string()
                            val jsonResponse = JSONObject(responseBody.toString())
                            val resultString = jsonResponse.getString("result")
                            result = resultString
                            successDialog.value = true
                            loading.value = false // Set loading to false when the network call finishes
                        }
                    })
                    callMade.value = true
                }
            }
        }
        if (successDialog.value) {
            ScanDialog(title = result, desc = "The description will be here on this " , onDismiss = { successDialog.value = false })
        }


    }
}

@Preview
@Composable
private fun CameraScreenPrev() {
    CameraScreen(navController = rememberNavController())
}

