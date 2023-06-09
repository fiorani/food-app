package com.example.eatit.ui.components

import android.util.Log
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Map
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.eatit.model.*
import com.example.eatit.viewModel.RestaurantsViewModel
import com.example.eatit.viewModel.UsersViewModel
import com.gowtham.ratingbar.RatingBar
import com.gowtham.ratingbar.RatingBarStyle
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EatItCard(onItemClicked: () -> Unit, function: @Composable () -> Unit) {
    Card(modifier = Modifier
        .fillMaxWidth()
        .padding(20.dp, 10.dp),
        colors = CardDefaults.cardColors(MaterialTheme.colorScheme.background),
        elevation = CardDefaults.cardElevation(8.dp),
        shape = CardDefaults.shape,
        onClick = onItemClicked,
        content = @Composable {
            function()
        }
    )
}

@Composable
fun RestaurantCard(
    restaurant: Restaurant,
    onItemClicked: () -> Unit,
    restaurantsViewModel: RestaurantsViewModel,
    user: User,
    usersViewModel: UsersViewModel
) {
    EatItCard(onItemClicked = {
        restaurantsViewModel.selectRestaurant(restaurant)
        onItemClicked()
    }) {
        Column {
            ImageCard(restaurant.photo)
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.Start,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = restaurant.name,
                        modifier = Modifier
                            .padding(4.dp)
                            .width(200.dp),
                        fontSize = 25.sp,
                        fontWeight = Bold
                    )
                    Text(
                        text = restaurant.address,
                        modifier = Modifier.padding(4.dp),
                        fontSize = 20.sp
                    )
                }
                Column(
                    horizontalAlignment = Alignment.End,
                ) {
                    var rating: Float by remember { mutableStateOf(3.2f) }
                    RatingBar(
                        value = restaurant.avgRating.toString().toFloat(),
                        style = RatingBarStyle.Fill(),
                        onValueChange = {
                            rating = it
                        },
                        onRatingChanged = {
                        },
                        modifier = Modifier.padding(1.dp, 4.dp),
                        spaceBetween = 1.dp,
                        size = 20.dp
                    )
                    val (isFavorite, setFavorite) = remember { mutableStateOf(user.favouriteRestaurants.contains(restaurant.id)) }
                    if (isFavorite) {
                        EatItIconButton(icon = Icons.Filled.Favorite, function = {
                            user.favouriteRestaurants.remove(restaurant.id)
                            usersViewModel.setRestaurants(user.favouriteRestaurants)
                            setFavorite(false)
                        })
                    } else {
                        EatItIconButton(icon = Icons.Default.FavoriteBorder, function = {
                            user.favouriteRestaurants.add(restaurant.id!!)
                            usersViewModel.setRestaurants(user.favouriteRestaurants)
                            setFavorite(true)
                        })
                    }

                }
            }
        }

    }
}

@Composable
fun ProductCard(
    product: Product,
    restaurantViewModel: RestaurantsViewModel,
    order: Order,
    user: User,
    onAddButtonClicked: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(30.dp, 0.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row {
            Icon(
                imageVector = Icons.Default.Fastfood, contentDescription = "Agriculture"
            )
            Text(
                modifier = Modifier.padding(10.dp, 0.dp),
                text = product.name + " - " + product.price.toString() + "€",
                fontSize = 20.sp
            )
        }
        if (!user.restaurateur) {
            var quantity = 0
            if (order.listProductId.contains(product.id)) {
                quantity =
                    order.listQuantity[order.listProductId.indexOf(product.id)]
            }
            val (count, updateCount) = remember { mutableStateOf(quantity) }
            QuantitySelector(count = count, decreaseItemCount = {
                if (count > 0) updateCount(count - 1)
                order.reduceCount(product)
            }, increaseItemCount = {
                updateCount(count + 1)
                order.increaseCount(product)
            })
        } else {
            IconButton(
                onClick =
                {
                    restaurantViewModel.selectProduct(product)
                    onAddButtonClicked()
                }
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Edit"
                )
            }
        }
    }
}

@Composable
fun ShoppingCard(product: Product, order: Order) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(15.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Card(
            modifier = Modifier.padding(5.dp),
            colors = CardDefaults.cardColors(MaterialTheme.colorScheme.background),
            elevation = CardDefaults.cardElevation(8.dp),
            shape = CardDefaults.shape
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    modifier = Modifier.padding(17.dp, 7.dp),
                    text = product.name,
                    fontSize = 17.sp
                )
                Text(
                    modifier = Modifier.padding(17.dp, 7.dp),
                    text = product.price.toString() + "€ x " + order.listQuantity.get(
                        order.listProductId.indexOf(
                            product.id
                        )
                    ).toString(),
                    fontSize = 17.sp
                )
            }
        }
    }
}

@Composable
fun SectionShoppingCard(
    sectionName: String,
    products: List<Product>,
    order: Order,
) {
    Text(
        modifier = Modifier.padding(20.dp, 10.dp),
        text = sectionName,
        fontSize = 25.sp,
        fontWeight = Bold
    )

    for (product in products) {
        if (product.section == sectionName) {
            ShoppingCard(
                product = product,
                order = order
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SectionMenuCard(
    sectionName: String,
    products: List<Product>,
    restaurantViewModel: RestaurantsViewModel,
    order: Order,
    user: User,
    onAddButtonClicked: () -> Unit
) {
    var expandedState by remember { mutableStateOf(false) }
    val rotationState by animateFloatAsState(
        targetValue = if (expandedState) 180f else 0f
    )

    Card(modifier = Modifier
        .fillMaxWidth()
        .padding(30.dp, 10.dp),
        colors = CardDefaults.cardColors(MaterialTheme.colorScheme.background),
        elevation = CardDefaults.cardElevation(8.dp),
        shape = CardDefaults.shape,
        onClick = {
            expandedState = !expandedState
        }) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                modifier = Modifier.padding(20.dp, 10.dp),
                text = sectionName,
                fontSize = 25.sp,
                fontWeight = Bold
            )
            IconButton(modifier = Modifier.rotate(rotationState), onClick = {
                expandedState = !expandedState
            }) {
                Icon(
                    imageVector = Icons.Default.ExpandMore, contentDescription = "Drop-Down Arrow"
                )
            }
        }
    }

    if (expandedState) {
        products.forEach { product ->
            if (product.section == sectionName) {
                ProductCard(
                    product = product,
                    restaurantViewModel = restaurantViewModel,
                    order = order,
                    user = user,
                    onAddButtonClicked
                )
            }
        }

    }

}

@Composable
fun RatingCard(rating: Rating) {
    EatItCard(onItemClicked = {
    }) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = rating.text.toString(),
                modifier = Modifier.padding(4.dp),
                fontSize = 20.sp
            )
            Row {
                var name by remember { mutableStateOf("random user") }
                LaunchedEffect(Unit) {
                    // name = UsersViewModel().getUserById(rating.userId.toString()).name.toString()
                }
                Text(
                    text = name,
                    modifier = Modifier.padding(4.dp),
                    fontSize = 15.sp
                )
                var valrating: Float by remember { mutableStateOf(3.2f) }
                RatingBar(
                    value = rating.rating.toString().toFloat(),
                    style = RatingBarStyle.Fill(),
                    onValueChange = {
                        valrating = it
                    },
                    onRatingChanged = {
                    },
                    modifier = Modifier.padding(4.dp),
                    size = 20.dp
                )
            }


        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderProfileCard(
    orders: Order,
    listProducts: List<Product>,
    restaurant: Restaurant
) {
    var expandedState by remember { mutableStateOf(false) }
    val rotationState by animateFloatAsState(
        targetValue = if (expandedState) 180f else 0f
    )
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize(
                animationSpec = tween(
                    durationMillis = 300,
                    easing = LinearOutSlowInEasing
                )
            )
            .padding(20.dp, 10.dp),
        elevation = CardDefaults.cardElevation(6.dp),
        shape = CardDefaults.shape,
        onClick = {
            expandedState = !expandedState
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp, 0.dp)
        ) {
            Text(
                text = restaurant.name,
                modifier = Modifier.padding(10.dp),
                fontWeight = Bold,
                fontSize = 32.sp
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Order date:",
                    modifier = Modifier
                        .padding(10.dp, 1.dp)
                        .weight(1f),
                    fontSize = 20.sp
                )
                val dateFormat = SimpleDateFormat("dd/MM/yy", Locale.getDefault())
                Text(
                    text = dateFormat.format(orders.timestamp).toString(),
                    modifier = Modifier.padding(10.dp, 1.dp),
                    fontSize = 18.sp
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Order total:",
                    modifier = Modifier
                        .padding(10.dp, 1.dp)
                        .weight(1f),
                    fontWeight = Bold,
                    fontSize = 20.sp
                )
                Text(
                    text = "€" + String.format("%.${2}f", orders.totalPrice),
                    modifier = Modifier.padding(10.dp, 2.dp),
                    fontWeight = Bold,
                    fontSize = 20.sp
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Status:",
                    modifier = Modifier
                        .padding(10.dp, 1.dp)
                        .weight(1f),
                    fontWeight = Bold,
                    fontSize = 20.sp
                )
                Text(
                    text = orders.status,
                    modifier = Modifier.padding(10.dp, 2.dp),
                    fontWeight = Bold,
                    fontSize = 20.sp
                )
            }


            if (expandedState) {
                Column(Modifier.fillMaxWidth()) {
                    Spacer(modifier = Modifier.size(20.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "DETAILS",
                            modifier = Modifier
                                .padding(10.dp),
                            fontSize = 20.sp,
                            fontWeight = Bold,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                        )
                        Divider(modifier = Modifier.padding(10.dp))
                    }

                    listProducts.forEachIndexed { index, product ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = product.name,
                                modifier = Modifier
                                    .padding(10.dp, 1.dp)
                                    .weight(1f),
                                fontSize = 16.sp
                            )
                            Text(
                                text = "€" + orders.listPrice.get(index) + "  × " + orders.listQuantity.get(
                                    index
                                ),
                                modifier = Modifier.padding(10.dp, 1.dp),
                                fontSize = 16.sp
                            )
                        }
                    }
                }
            }
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    modifier = Modifier
                        .weight(1f)
                        .rotate(rotationState),
                    onClick = {
                        expandedState = !expandedState
                    }) {
                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = "Drop-Down Arrow"
                    )
                }
            }
        }
    }
}