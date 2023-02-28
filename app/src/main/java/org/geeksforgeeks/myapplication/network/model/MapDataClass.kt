package org.geeksforgeeks.myapplication.network.model

data class MapDataClass(
    var routes: ArrayList<RoutesData>
)

data class RoutesData(
    var legs: ArrayList<LegsData>
)

data class LegsData(
    var distance: DistanceData,
    var duration: DurationData,
    var end_address: String,
    var start_address: String,
    var end_location: LocationData,
    var start_location: LocationData,
    var steps: ArrayList<StepsData>
)

data class StepsData(
    var distance: DistanceData,
    var duration: DistanceData,
    var end_address: String,
    var start_address: String,
    var end_location: LocationData,
    var start_location: LocationData,
    var polyline: PolyLineData,
    var travel_mode: String,
    var maneuver: String
)

data class DurationData(
    var text: String,
    var value: Int
)

data class DistanceData(
    var text: String,
    var value: Int
)

data class PolyLineData(
    var points: String
)

data class LocationData(
    var lat: String,
    var lng: String
)