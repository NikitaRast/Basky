package com.example.basky

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import org.json.JSONObject

private val Context.dataStore by preferencesDataStore(name = "basky_data")

class DataStoreHelper(context: Context) {
    private val dataStore = context.dataStore

    private val ITEMS_KEY = stringSetPreferencesKey("items")
    private val HISTORY_ITEMS_KEY = stringSetPreferencesKey("history_items")

    val itemsFlow: Flow<List<String>> = dataStore.data.map { preferences ->
        preferences[ITEMS_KEY]?.toList()?.sorted() ?: emptyList()
    }

    val historyItemsFlow: Flow<List<String>> = dataStore.data.map { preferences ->
        preferences[HISTORY_ITEMS_KEY]?.toList()?.sorted() ?: emptyList()
    }

    suspend fun addItem(newItem: String) {
        dataStore.edit { preferences ->
            val currentItems = preferences[ITEMS_KEY]?.toMutableSet() ?: mutableSetOf()
            currentItems.add(newItem)
            if (currentItems.size > 200) {
                currentItems.remove(currentItems.first())
            }
            preferences[ITEMS_KEY] = currentItems
        }
    }

    suspend fun removeItem(item: String) {
        dataStore.edit { preferences ->
            // Удаляем элемент из основного списка
            val currentItems = preferences[ITEMS_KEY]?.toMutableSet() ?: mutableSetOf()
            currentItems.remove(item)
            preferences[ITEMS_KEY] = currentItems

            // Добавляем элемент с временем в исторический список
            val currentHistory = preferences[HISTORY_ITEMS_KEY]?.toMutableSet() ?: mutableSetOf()
            val timestamp = System.currentTimeMillis() // Текущее время в миллисекундах
            val historyEntry = JSONObject().apply {
                put("item", item)
                put("timestamp", timestamp)
            }.toString()
            currentHistory.add(historyEntry)
            preferences[HISTORY_ITEMS_KEY] = currentHistory
        }
    }

    val detailedHistoryItemsFlow: Flow<List<Pair<String, Long>>> = dataStore.data.map { preferences ->
        preferences[HISTORY_ITEMS_KEY]?.mapNotNull { entry ->
            try {
                val json = JSONObject(entry)
                val item = json.getString("item")
                val timestamp = json.getLong("timestamp")
                item to timestamp
            } catch (e: Exception) {
                null
            }
        } ?: emptyList()
    }

    // Функция для очистки истории
    suspend fun clearHistory() {
        dataStore.edit { preferences ->
            preferences[HISTORY_ITEMS_KEY] = mutableSetOf()  // Очищаем только историю
        }
    }

    // Функция для поиска элементов в истории
    @OptIn(FlowPreview::class)
    fun searchItems(query: String): Flow<List<String>> {
        return itemsFlow
            .debounce(300) // Задержка 300 мс
            .map { items ->
                if (query.isBlank()) items
                else items.filter { it.contains(query, ignoreCase = true) }
            }
    }

}
