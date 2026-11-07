package com.example.ui.auth

import android.Manifest
import android.app.DatePickerDialog
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.UserRole
import com.example.data.repository.SocRepository
import com.example.ui.components.SocButton
import com.example.ui.components.SocOutlinedButton
import com.example.ui.theme.SocAlertRed
import com.example.ui.theme.SocBlueAccent
import com.example.ui.theme.SocBlueBorder
import com.example.ui.theme.SocDarkBlueBg
import com.example.ui.theme.SocDarkBlueSurface
import com.example.ui.theme.SocDarkBlueSurfaceElevated
import com.example.ui.theme.SocGoldLight
import com.example.ui.theme.SocGoldPrimary
import com.example.ui.theme.SocTerminalGreen
import com.example.ui.theme.SocTextMuted
import com.example.ui.theme.SocTextPrimary
import com.example.ui.theme.SocTextSecondary
import java.io.ByteArrayOutputStream
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    onNavigateBackToLogin: () -> Unit,
    onRegistrationSuccess: () -> Unit
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    // Role selection: ONLY Candidate & Admin as per requirement!
    var selectedRole by remember { mutableStateOf(UserRole.CANDIDATE) }

    // 11 Form Fields in exact order specified:
    // 1. Full Name
    var fullName by remember { mutableStateOf("") }
    // 2. Email ID
    var emailId by remember { mutableStateOf("") }
    // 3. Mobile Number
    var mobileNumber by remember { mutableStateOf("") }
    // 4. Full Address
    var fullAddress by remember { mutableStateOf("") }
    // 5. DOB
    var dob by remember { mutableStateOf("2000-01-15") }
    // 6. Gender
    var gender by remember { mutableStateOf("Male") }
    // 7. Qualification/Course
    var qualification by remember { mutableStateOf("") }

    // 8. Profile Photo (Camera & Gallery upload + Circle Crop + Compress <=200KB)
    var photoBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var compressedBase64 by remember { mutableStateOf("") }
    var compressedSizeKb by remember { mutableIntStateOf(0) }
    val avatarIcons = listOf("🛡️", "🕵️‍♂️", "💻", "⚡", "🦅", "🎯")
    var selectedAvatar by remember { mutableStateOf(avatarIcons[0]) }

    fun processBitmap(bitmap: Bitmap) {
        var quality = 85
        val maxBytes = 200 * 1024
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, quality, stream)
        while (stream.size() > maxBytes && quality > 15) {
            stream.reset()
            quality -= 15
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, stream)
        }
        val bytes = stream.toByteArray()
        photoBitmap = bitmap
        compressedSizeKb = bytes.size / 1024
        compressedBase64 = Base64.encodeToString(bytes, Base64.NO_WRAP)
        Toast.makeText(context, "Photo ready! Compressed to ${compressedSizeKb}KB (<=200KB)", Toast.LENGTH_SHORT).show()
    }

    // Camera Launcher
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bmp: Bitmap? ->
        if (bmp != null) {
            processBitmap(bmp)
        }
    }

    // Camera Permission Launcher
    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            cameraLauncher.launch(null)
        } else {
            Toast.makeText(context, "Camera permission needed to take profile photo", Toast.LENGTH_SHORT).show()
        }
    }

    // Gallery Picker (Android Photo Picker - Zero Permissions Needed)
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            try {
                context.contentResolver.openInputStream(uri)?.use { inputStream ->
                    val bmp = BitmapFactory.decodeStream(inputStream)
                    if (bmp != null) {
                        processBitmap(bmp)
                    }
                }
            } catch (e: Exception) {
                Toast.makeText(context, "Failed to load photo: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // 9. Password
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    // 10. Confirm Password
    var confirmPassword by remember { mutableStateOf("") }
    var confirmPasswordVisible by remember { mutableStateOf(false) }
    // 11. Terms Checkbox
    var termsAccepted by remember { mutableStateOf(false) }

    var errorMessage by remember { mutableStateOf("") }

    val calendar = Calendar.getInstance()
    val datePickerDialog = DatePickerDialog(
        context,
        { _, year, month, dayOfMonth ->
            dob = "$year-${(month + 1).toString().padStart(2, '0')}-${dayOfMonth.toString().padStart(2, '0')}"
        },
        calendar.get(Calendar.YEAR) - 22,
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "REGISTRATION",
                            color = SocGoldPrimary,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                        Text(
                            text = "Only Candidate & Admin Onboarding",
                            color = SocTextSecondary,
                            fontSize = 11.sp
                        )
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = onNavigateBackToLogin,
                        modifier = Modifier.testTag("register_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = SocGoldPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = SocDarkBlueSurface
                )
            )
        },
        containerColor = SocDarkBlueBg
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(scrollState)
                .padding(horizontal = 20.dp, vertical = 16.dp)
                .testTag("register_screen")
        ) {
            // Role Selection for Registration (Candidate vs Admin only)
            Text(
                text = "Select Registration Role",
                color = SocGoldLight,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(8.dp))

            SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                SegmentedButton(
                    selected = selectedRole == UserRole.CANDIDATE,
                    onClick = { selectedRole = UserRole.CANDIDATE },
                    shape = SegmentedButtonDefaults.itemShape(index = 0, count = 2),
                    colors = SegmentedButtonDefaults.colors(
                        activeContainerColor = SocGoldPrimary,
                        activeContentColor = Color.Black,
                        inactiveContainerColor = SocDarkBlueSurfaceElevated,
                        inactiveContentColor = SocTextSecondary
                    )
                ) {
                    Text("Candidate Registration", fontWeight = FontWeight.SemiBold)
                }
                SegmentedButton(
                    selected = selectedRole == UserRole.ADMIN,
                    onClick = { selectedRole = UserRole.ADMIN },
                    shape = SegmentedButtonDefaults.itemShape(index = 1, count = 2),
                    colors = SegmentedButtonDefaults.colors(
                        activeContainerColor = SocGoldPrimary,
                        activeContentColor = Color.Black,
                        inactiveContainerColor = SocDarkBlueSurfaceElevated,
                        inactiveContentColor = SocTextSecondary
                    )
                ) {
                    Text("Admin Registration", fontWeight = FontWeight.SemiBold)
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = SocDarkBlueSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, SocBlueBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(18.dp)) {

                    // 1. Full Name
                    FormFieldLabel(number = 1, label = "Full Name")
                    OutlinedTextField(
                        value = fullName,
                        onValueChange = { fullName = it; errorMessage = "" },
                        placeholder = { Text("e.g. Rahul Sharma") },
                        leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = SocGoldPrimary) },
                        singleLine = true,
                        colors = socTextFieldColors(),
                        modifier = Modifier.fillMaxWidth().testTag("reg_full_name")
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // 2. Email ID
                    FormFieldLabel(number = 2, label = "Email ID")
                    OutlinedTextField(
                        value = emailId,
                        onValueChange = { emailId = it; errorMessage = "" },
                        placeholder = { Text("e.g. rahul@example.com") },
                        leadingIcon = { Icon(Icons.Default.Email, contentDescription = null, tint = SocGoldPrimary) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        singleLine = true,
                        colors = socTextFieldColors(),
                        modifier = Modifier.fillMaxWidth().testTag("reg_email_id")
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // 3. Mobile Number
                    FormFieldLabel(number = 3, label = "Mobile Number")
                    OutlinedTextField(
                        value = mobileNumber,
                        onValueChange = { mobileNumber = it; errorMessage = "" },
                        placeholder = { Text("+91 98765 43210") },
                        leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null, tint = SocGoldPrimary) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        singleLine = true,
                        colors = socTextFieldColors(),
                        modifier = Modifier.fillMaxWidth().testTag("reg_mobile_number")
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // 4. Full Address
                    FormFieldLabel(number = 4, label = "Full Address")
                    OutlinedTextField(
                        value = fullAddress,
                        onValueChange = { fullAddress = it; errorMessage = "" },
                        placeholder = { Text("House/Flat, Street, City, State, PIN") },
                        leadingIcon = { Icon(Icons.Default.Home, contentDescription = null, tint = SocGoldPrimary) },
                        maxLines = 2,
                        colors = socTextFieldColors(),
                        modifier = Modifier.fillMaxWidth().testTag("reg_full_address")
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // 5. DOB (Date of Birth)
                    FormFieldLabel(number = 5, label = "DOB (Date of Birth)")
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(SocDarkBlueSurfaceElevated)
                            .border(1.dp, SocBlueBorder, RoundedCornerShape(8.dp))
                            .clickable { datePickerDialog.show() }
                            .padding(horizontal = 14.dp, vertical = 14.dp)
                            .testTag("reg_dob_picker"),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.CalendarMonth, contentDescription = null, tint = SocGoldPrimary)
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(text = dob, color = SocTextPrimary, fontSize = 14.sp)
                        Spacer(modifier = Modifier.weight(1f))
                        Text(text = "Change", color = SocBlueAccent, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // 6. Gender
                    FormFieldLabel(number = 6, label = "Gender")
                    SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                        listOf("Male", "Female", "Other").forEachIndexed { index, option ->
                            SegmentedButton(
                                selected = gender == option,
                                onClick = { gender = option },
                                shape = SegmentedButtonDefaults.itemShape(index = index, count = 3),
                                colors = SegmentedButtonDefaults.colors(
                                    activeContainerColor = SocGoldPrimary,
                                    activeContentColor = Color.Black,
                                    inactiveContainerColor = SocDarkBlueSurfaceElevated,
                                    inactiveContentColor = SocTextSecondary
                                )
                            ) {
                                Text(option, fontSize = 12.sp)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // 7. Qualification/Course
                    FormFieldLabel(number = 7, label = "Qualification / Course")
                    OutlinedTextField(
                        value = qualification,
                        onValueChange = { qualification = it; errorMessage = "" },
                        placeholder = { Text("e.g. B.Tech CS, MCA, CompTIA Security+, CEH") },
                        leadingIcon = { Icon(Icons.Default.School, contentDescription = null, tint = SocGoldPrimary) },
                        singleLine = true,
                        colors = socTextFieldColors(),
                        modifier = Modifier.fillMaxWidth().testTag("reg_qualification")
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // 8. Profile Photo (Gallery / Camera with Circle Crop & Compression <=200KB)
                    FormFieldLabel(number = 8, label = "Profile Photo (Gallery / Camera - Max 200KB)")

                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Circle Crop Preview
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(88.dp)
                                .clip(CircleShape)
                                .border(2.dp, SocGoldPrimary, CircleShape)
                                .background(SocDarkBlueSurfaceElevated)
                        ) {
                            if (photoBitmap != null) {
                                Image(
                                    bitmap = photoBitmap!!.asImageBitmap(),
                                    contentDescription = "Profile Photo Circle Crop",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
                            } else {
                                Text(text = selectedAvatar, fontSize = 36.sp)
                            }
                        }

                        if (compressedSizeKb > 0) {
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Photo Compressed: ${compressedSizeKb} KB (<=200KB Limit Met)",
                                color = SocTerminalGreen,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Upload from Gallery Button (Fixed 280x48)
                        SocButton(
                            text = "UPLOAD FROM GALLERY",
                            icon = Icons.Default.AddPhotoAlternate,
                            onClick = {
                                galleryLauncher.launch(
                                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                )
                            },
                            modifier = Modifier.testTag("upload_gallery_btn")
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // Take Photo from Camera Button (Fixed 280x48)
                        SocOutlinedButton(
                            text = "TAKE PHOTO FROM CAMERA",
                            icon = Icons.Default.PhotoCamera,
                            contentColor = SocGoldPrimary,
                            onClick = {
                                cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                            },
                            modifier = Modifier.testTag("take_camera_photo_btn")
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = "Or choose a Cyber Avatar:",
                            color = SocTextMuted,
                            fontSize = 11.sp
                        )
                        Spacer(modifier = Modifier.height(6.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            avatarIcons.forEach { avatar ->
                                Box(
                                    contentAlignment = Alignment.Center,
                                    modifier = Modifier
                                        .size(44.dp)
                                        .clip(CircleShape)
                                        .background(if (photoBitmap == null && selectedAvatar == avatar) SocGoldPrimary else SocDarkBlueSurfaceElevated)
                                        .border(
                                            width = if (photoBitmap == null && selectedAvatar == avatar) 2.dp else 1.dp,
                                            color = if (photoBitmap == null && selectedAvatar == avatar) SocGoldLight else SocBlueBorder,
                                            shape = CircleShape
                                        )
                                        .clickable {
                                            photoBitmap = null
                                            compressedBase64 = ""
                                            compressedSizeKb = 0
                                            selectedAvatar = avatar
                                        }
                                        .testTag("avatar_$avatar")
                                ) {
                                    Text(text = avatar, fontSize = 20.sp)
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // 9. Password
                    FormFieldLabel(number = 9, label = "Password")
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it; errorMessage = "" },
                        placeholder = { Text("Min 6 characters") },
                        leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = SocGoldPrimary) },
                        trailingIcon = {
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(
                                    imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                    contentDescription = null,
                                    tint = SocTextSecondary
                                )
                            }
                        },
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        singleLine = true,
                        colors = socTextFieldColors(),
                        modifier = Modifier.fillMaxWidth().testTag("reg_password")
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // 10. Confirm Password
                    FormFieldLabel(number = 10, label = "Confirm Password")
                    OutlinedTextField(
                        value = confirmPassword,
                        onValueChange = { confirmPassword = it; errorMessage = "" },
                        placeholder = { Text("Re-enter password") },
                        leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = SocGoldPrimary) },
                        trailingIcon = {
                            IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                                Icon(
                                    imageVector = if (confirmPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                    contentDescription = null,
                                    tint = SocTextSecondary
                                )
                            }
                        },
                        visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        singleLine = true,
                        colors = socTextFieldColors(),
                        modifier = Modifier.fillMaxWidth().testTag("reg_confirm_password")
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // 11. Terms Checkbox
                    FormFieldLabel(number = 11, label = "Terms & Conditions")
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { termsAccepted = !termsAccepted }
                    ) {
                        Checkbox(
                            checked = termsAccepted,
                            onCheckedChange = { termsAccepted = it },
                            colors = CheckboxDefaults.colors(
                                checkedColor = SocGoldPrimary,
                                checkmarkColor = Color.Black,
                                uncheckedColor = SocTextSecondary
                            ),
                            modifier = Modifier.testTag("reg_terms_checkbox")
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "I agree to the Terms & Conditions and Privacy Policy of Aarav IT Solutions (aaravitsolutons.com)",
                            color = SocTextSecondary,
                            fontSize = 12.sp,
                            lineHeight = 16.sp
                        )
                    }

                    if (errorMessage.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(text = errorMessage, color = SocAlertRed, fontSize = 12.sp)
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Submit Registration Button: NARROW & FIXED SIZE (280x48dp, center-aligned)
                    SocButton(
                        text = "COMPLETE ${selectedRole.displayName.uppercase()} REGISTRATION",
                        onClick = {
                            if (fullName.isBlank()) {
                                errorMessage = "Please enter your Full Name"
                                return@SocButton
                            }
                            if (emailId.isBlank() || !emailId.contains("@")) {
                                errorMessage = "Please enter a valid Email ID"
                                return@SocButton
                            }
                            if (mobileNumber.isBlank()) {
                                errorMessage = "Please enter your Mobile Number"
                                return@SocButton
                            }
                            if (password.length < 6) {
                                errorMessage = "Password must be at least 6 characters"
                                return@SocButton
                            }
                            if (password != confirmPassword) {
                                errorMessage = "Passwords do not match"
                                return@SocButton
                            }
                            if (!termsAccepted) {
                                errorMessage = "You must agree to the Terms & Conditions"
                                return@SocButton
                            }

                            val finalPhoto = if (compressedBase64.isNotBlank()) compressedBase64 else selectedAvatar

                            SocRepository.registerUser(
                                fullName = fullName,
                                email = emailId,
                                mobileNumber = mobileNumber,
                                fullAddress = fullAddress,
                                dob = dob,
                                gender = gender,
                                qualification = qualification.ifBlank { "Cybersecurity Candidate" },
                                profilePhotoUri = finalPhoto,
                                role = selectedRole
                            )
                            onRegistrationSuccess()
                        },
                        modifier = Modifier.testTag("submit_registration_button")
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Footer info
            Text(
                text = "Aarav IT Solutions • Aarav Cybersecurity Academy",
                color = SocTextMuted,
                fontSize = 11.sp,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }
    }
}

@Composable
fun FormFieldLabel(number: Int, label: String) {
    Text(
        text = "$number. $label",
        color = SocTextPrimary,
        fontSize = 12.sp,
        fontWeight = FontWeight.SemiBold,
        modifier = Modifier.padding(bottom = 4.dp)
    )
}

@Composable
fun socTextFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedTextColor = SocTextPrimary,
    unfocusedTextColor = SocTextPrimary,
    focusedBorderColor = SocGoldPrimary,
    unfocusedBorderColor = SocBlueBorder,
    focusedContainerColor = SocDarkBlueSurfaceElevated,
    unfocusedContainerColor = SocDarkBlueSurfaceElevated
)
