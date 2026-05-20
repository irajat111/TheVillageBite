package com.example.thevillagebiteuser

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil3.compose.AsyncImage
import coil3.request.crossfade
import com.google.firebase.Firebase
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.firestore

// Data class for fetch data from Firebase db -> collection -> CategoryClass
@Composable
fun ProductScreen(navController: NavHostController) {

    val db = Firebase.firestore
    val context = LocalContext.current


    // ✅ STEP 1 — List banao
    var ProductList = remember { mutableStateListOf<ProductClass>() }

    // ✅ STEP 2 — Firestore se data fetch karo
    LaunchedEffect(Unit) {
        db.collection("Productclass").addSnapshotListener { result, error ->
            if (error != null) return@addSnapshotListener

            for (doc in result!!.documentChanges) {
                when (doc.type) {
                    DocumentChange.Type.ADDED -> {
                        val model = doc.document.toObject(ProductClass::class.java)
                        model.id = doc.document.id
                        ProductList.add(model)
                    }
                    DocumentChange.Type.MODIFIED -> {
                        val model = doc.document.toObject(ProductClass::class.java)
                        model.id = doc.document.id
                        val index = ProductList.indexOfFirst { it.id == model.id }
                        if (index != -1) ProductList[index] = model
                    }
                    DocumentChange.Type.REMOVED -> {
                        val model = doc.document.toObject(ProductClass::class.java)
                        model.id = doc.document.id
                        val index = ProductList.indexOfFirst { it.id == model.id }
                        if (index != -1) ProductList.removeAt(index)
                    }

                    else -> { }
                }
            }
        }
    }


    // ✅ STEP 3 — Data dikhao
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = Modifier.fillMaxWidth().padding(10.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items(ProductList.size){index->
            Card(
                modifier = Modifier.fillMaxWidth().clickable(){
                    // yhan pr InsideProductItem Screen show krwaani hai
                    // ✅ CHANGE — productId pass karke navigate karo
                    val productId = ProductList[index].id ?: ""
                    navController.navigate("productDetail/$productId") // 👈 navigate
//                            navController.navigate("productDetails")
                },
                colors = CardDefaults.cardColors(   // card color = white
                    containerColor = colorResource(R.color.white)
                ),
                elevation = CardDefaults.cardElevation(8.dp)
//                onClick = {
//
//                }
            ) {

                println("Checck image form Firebase: ${ ProductList[index].productImage}")

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    AsyncImage(
                        model = coil3.request.ImageRequest.Builder(LocalContext.current)
                            .data(ProductList[index].productImage)
                            .crossfade(true)
                            .build(),
                        placeholder = painterResource(R.drawable.ic_launcher_background),
                        contentDescription = stringResource(R.string.app_name),
                        contentScale = ContentScale.Crop,
//                                modifier = Modifier.fillMaxWidth().height(150.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(150.dp)
                            .padding(8.dp)                        // 👈 Padding
                            .clip(RoundedCornerShape(12.dp))   // 👈 Rounded corners

                    )

                    Spacer(Modifier.height(1.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        // changes here productList[index].categoryName..toString()
                        Text(ProductList[index].foodname?:"",
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(8.dp)
                        )
                    }
                }

            }

        }
    }


}