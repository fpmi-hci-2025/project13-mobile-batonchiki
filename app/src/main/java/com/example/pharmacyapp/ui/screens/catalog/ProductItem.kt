package com.example.pharmacyapp.ui.screens.catalog // Ваш package name

import androidx.compose.foundation.clickable // Для обработки кликов
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite // Иконка "избранное" (заполнено)
import androidx.compose.material.icons.outlined.FavoriteBorder // Иконка "избранное" (контур)
import androidx.compose.material3.* // Material 3
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage // Для загрузки изображений из Coil
import com.example.pharmacyapp.data.local.ProductEntity

@Composable
fun ProductItem(
    product: ProductEntity,
    onFavoriteClick: () -> Unit,
    onItemClick: () -> Unit,
    modifier: Modifier = Modifier // Добавляем Modifier
) {
    Card( // Используем Card для визуального выделения элемента
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onItemClick), // Делаем карточку кликабельной
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp) // Небольшая тень
    ) {
        Row(
            modifier = Modifier.padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Изображение товара
            AsyncImage(
                model = product.imageUrl, // URL изображения из данных
                contentDescription = product.name, // Описание для доступности
                modifier = Modifier
                    .size(80.dp) // Фиксированный размер изображения
                    .clip(MaterialTheme.shapes.medium), // Скругляем углы
                contentScale = ContentScale.Crop // Масштабируем изображение
            )

            Spacer(modifier = Modifier.width(16.dp))

            // Текстовая информация (Название, Цена, Категория)
            Column(
                modifier = Modifier.weight(1f) // Занимает оставшееся место по ширине
            ) {
                Text(
                    text = product.name,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1, // Не больше одной строки
                    overflow = TextOverflow.Ellipsis // Многоточие, если не влезает
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Цена: ${product.price} у.е.", // Отображаем цену
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Категория: ${product.category}",
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Кнопка "Избранное"
            IconButton(onClick = onFavoriteClick) {
                Icon(
                    imageVector = if (product.isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                    contentDescription = "Добавить в избранное",
                    tint = if (product.isFavorite) MaterialTheme.colorScheme.primary else LocalContentColor.current
                )
            }
        }
    }
}

// ----- Превью для ProductItem -----
// @Preview(showBackground = true)
// @Composable
// fun ProductItemPreview() {
//     val sampleProduct = ProductEntity(
//         id = 1,
//         name = "Очень длинное название витамина D3 2000 МЕ",
//         description = "Описание",
//         category = "Витамины",
//         price = 8.50,
//         imageUrl = "", // Оставить пустым или использовать placeholder для превью
//         isFavorite = true
//     )
//     PharmacyAppTheme {
//         ProductItem(
//             product = sampleProduct,
//             onFavoriteClick = {},
//             onItemClick = {}
//         )
//     }
// }