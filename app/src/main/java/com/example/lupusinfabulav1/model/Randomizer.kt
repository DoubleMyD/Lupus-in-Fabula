package com.example.lupusinfabulav1.model

class Randomizer() {

    fun <T> randomize(
        startingValues: Map<T, Int>,
        idealDistribution: Map<T, Int>,
        remainingPlaces: Int,
        maxValue: Int
    ): Map<T, Int> {
        // Calculate total weight (sum of ideal numbers)
        val totalWeight = idealDistribution.values.sum()

        // Weights map to store the relative weight (i.e., likelihood) of each role being selected
        val weights = idealDistribution.mapValues { (_, idealCount) ->
            idealCount.toFloat() / totalWeight // Normalized weight for each role
        }

        val updatedValues = startingValues.toMutableMap()
        var remainingSpace = remainingPlaces

        updatedValues.forEach { (key, _) ->
            if (remainingSpace <= 0) return@forEach // Stop if no places are left

            val idealValue = idealDistribution[key] ?: 1 // Fallback to 1 if key isn't specified
            val maxAllowedValue = minOf(remainingSpace, idealValue + 2, maxValue)
            val randomValue = getWeightedRandomValue(key, maxAllowedValue, weights, remainingPlaces)

            updatedValues[key] = randomValue
            remainingSpace -= randomValue // Decrease remaining places count
        }

        if (remainingSpace > 0) {
            return distributeRemainingPlaces(updatedValues, remainingSpace)
        }

        return updatedValues
    }

    // Helper function to calculate a weighted random value biased towards the ideal value
    private fun <T> getWeightedRandomValue(
        key: T,
        maxAllowedValue: Int,
        weights: Map<T, Float>,
        remainingPlaces: Int
    ): Int {
        // Calculate weighted range for the role
        val weight = weights[key] ?: 1.0f
        val weightedMax = (weight * remainingPlaces).toInt().coerceIn(1, maxAllowedValue)

        // Return a random number within the weighted range
        return (1..weightedMax).random()
    }

    private fun <T> distributeRemainingPlaces(
        initialMap: Map<T, Int>,
        remainingPlaces: Int
    ): Map<T, Int> {
        val updatedMap = initialMap.toMutableMap()
        var remaining = remainingPlaces
        val keys = initialMap.keys.shuffled() // Randomize role selection for distribution

        while (remaining > 0) {
            for (key in keys) {
                if (remaining <= 0) break
                val currentCount = updatedMap[key] ?: continue
                updatedMap[key] = currentCount + 1
                remaining--
            }
        }
        return updatedMap
    }


}