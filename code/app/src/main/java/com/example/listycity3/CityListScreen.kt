package com.example.listycity3

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.listycity3.ui.theme.ListyCity3Theme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.material3.FloatingActionButton
import androidx.compose.foundation.layout.fillMaxSize

@Composable
fun CityListScreen(
    cities: List<City>,
    onAddCity: (City) -> Unit,
    // Sends the old city and its replacement upward so the repository can swap them
    onUpdateCity: (City, City) -> Unit,
    modifier: Modifier = Modifier
) {
    var newCityName by remember { mutableStateOf("") }
    var newProvinceName by remember { mutableStateOf("") }
    var showAddCityFields by remember { mutableStateOf(false) }
    // Null means no row is being edited
    var selectedCity by remember { mutableStateOf<City?>(null) }

    // Shared helper: leave edit/add mode and clear the text fields
    fun clearEditState() {
        selectedCity = null
        showAddCityFields = false
        newCityName = ""
        newProvinceName = ""
    }

    Column(modifier = modifier.fillMaxSize()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            FloatingActionButton(
                modifier = Modifier.padding(16.dp),
                onClick = {
                    // Leaving edit mode before toggling add so the shared fields are not dual-purpose at once
                    selectedCity = null
                    newCityName = ""
                    newProvinceName = ""
                    showAddCityFields = !showAddCityFields
                }
            ) {
                Text("+")
            }
        }

        // One field row serves both Add and Update
        if (showAddCityFields || selectedCity != null) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                OutlinedTextField(
                    value = newCityName,
                    onValueChange = { newCityName = it },
                    label = { Text("City") },
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(8.dp))
                OutlinedTextField(
                    value = newProvinceName,
                    onValueChange = { newProvinceName = it },
                    label = { Text("Province") },
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Button(
                    modifier = Modifier.padding(vertical = 12.dp),
                    onClick = {
                        if (newCityName.isNotBlank() && newProvinceName.isNotBlank()) {
                            val city = selectedCity
                            if (city != null) {
                                // Replace rather than mutate because City properties are val
                                onUpdateCity(
                                    city,
                                    City(name = newCityName, province = newProvinceName)
                                )
                            } else {
                                onAddCity(
                                    City(name = newCityName, province = newProvinceName)
                                )
                            }
                            clearEditState()
                        }
                    }
                ) {
                    // Same button, different label depending on mode
                    Text(if (selectedCity != null) "Update" else "Add City")
                }
            }
        }

        // Empty-list clicks cancel edit; child row clicks still take priority
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .clickable(enabled = selectedCity != null) { clearEditState() }
        ) {
            itemsIndexed(cities) { index, city ->
                CityRow(
                    city = city,
                    onClick = {
                        if (selectedCity != null) {
                            // Any row tap while editing cancels (same or different city)
                            clearEditState()
                        } else {
                            // Enter edit mode and pre-fill the shared fields
                            selectedCity = city
                            showAddCityFields = false
                            newCityName = city.name
                            newProvinceName = city.province
                        }
                    }
                )
                if (index < cities.lastIndex) {
                    HorizontalDivider()
                }
            }
        }
    }
}

@Composable
fun CityRow(city: City, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
        Text(
            text = city.name,
            fontSize = 30.sp,
            modifier = Modifier.weight(1f)
        )

        Text(
            text = city.province,
            fontSize = 30.sp,
            modifier = Modifier.weight(1f)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun CityListScreenPreview() {
    ListyCity3Theme {
        CityListScreen(
            cities = listOf(
                City("Edmonton", "AB"),
                City("Vancouver", "BC"),
                City("Calgary", "AB")
            ),
            onAddCity = {},
            // Two unused params so the lambda matches (City, City) -> Unit
            onUpdateCity = { _, _ -> }
        )
    }
}
