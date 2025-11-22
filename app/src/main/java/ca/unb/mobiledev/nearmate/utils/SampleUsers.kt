package ca.unb.mobiledev.nearmate.utils

import ca.unb.mobiledev.nearmate.constants.Country
import ca.unb.mobiledev.nearmate.constants.Presence
import ca.unb.mobiledev.nearmate.constants.Status
import ca.unb.mobiledev.nearmate.models.User

fun getSampleNearbyUsers(): List<User> {
    return listOf(
        User(
            id = "7EZd6g5c7SYfGobwyJ9KH2wLEUY2",
            firstName = "Alice",
            lastName = "Johnson",
            userName = "alicej",
            prefersToShowUserName = true,
            email = "alice@example.com",
            country = Country.CANADA,
            status = Status.OPEN_TO_CHAT,
            presence = Presence.ONLINE,
            profilePhoto = null,
            lat = 37.4250,
            lng = -122.081
        ),
        User(
            id = "0rQcUaHtL8haUulKfloO9i0UOA42",
            firstName = "Bob",
            lastName = "Smith",
            userName = null,
            prefersToShowUserName = false,
            email = "bob@example.com",
            country = Country.NIGERIA,
            status = Status.LOOKING_FOR_STUDY_BUDDY,
            presence = Presence.ONLINE,
            profilePhoto = null,
            lat = 37.4190,
            lng = -122.085
        ),
        User(
            id = "3",
            firstName = "Carlos",
            lastName = "Martinez",
            userName = "carlos_m",
            prefersToShowUserName = true,
            email = "carlos@example.com",
            country = Country.INDIA,
            status = Status.JUST_HANGING_OUT,
            presence = Presence.ONLINE,
            profilePhoto = null,
            lat = 37.4235,
            lng = -122.089
        ),
        User(
            id = "4",
            firstName = "Diana",
            lastName = "Lee",
            userName = "diana_lee",
            prefersToShowUserName = true,
            email = "diana@example.com",
            country = Country.CANADA,
            status = Status.OPEN_TO_CHAT,
            presence = Presence.ONLINE,
            profilePhoto = null,
            lat = 37.4202,
            lng = -122.082
        ),
        User(
            id = "5",
            firstName = "Ethan",
            lastName = "Nguyen",
            userName = null,
            prefersToShowUserName = false,
            email = "ethan@example.com",
            country = Country.GHANA,
            status = Status.JUST_HANGING_OUT,
            presence = Presence.ONLINE,
            profilePhoto = null,
            lat = 37.4185,
            lng = -122.087
        ),
        User(
            id = "6",
            firstName = "Fiona",
            lastName = "Brown",
            userName = "fiona_b",
            prefersToShowUserName = true,
            email = "fiona@example.com",
            country = Country.NIGERIA,
            status = Status.LOOKING_FOR_STUDY_BUDDY,
            presence = Presence.ONLINE,
            profilePhoto = null,
            lat = 37.4228,
            lng = -122.080
        ),
        User(
            id = "7",
            firstName = "George",
            lastName = "Clark",
            userName = "georgec",
            prefersToShowUserName = true,
            email = "george@example.com",
            country = Country.INDIA,
            status = Status.OPEN_TO_CHAT,
            presence = Presence.ONLINE,
            profilePhoto = null,
            lat = 37.4210,
            lng = -122.088
        ),
        User(
            id = "8",
            firstName = "Hannah",
            lastName = "Kim",
            userName = "hannahk",
            prefersToShowUserName = true,
            email = "hannah@example.com",
            country = Country.CANADA,
            status = Status.JUST_HANGING_OUT,
            presence = Presence.ONLINE,
            profilePhoto = null,
            lat = 37.4245,
            lng = -122.084
        ),
        User(
            id = "9",
            firstName = "Ivan",
            lastName = "Petrov",
            userName = null,
            prefersToShowUserName = false,
            email = "ivan@example.com",
            country = Country.GHANA,
            status = Status.LOOKING_FOR_STUDY_BUDDY,
            presence = Presence.ONLINE,
            profilePhoto = null,
            lat = 37.4198,
            lng = -122.081
        ),
        User(
            id = "10",
            firstName = "Julia",
            lastName = "Garcia",
            userName = "julia_g",
            prefersToShowUserName = true,
            email = "julia@example.com",
            country = Country.INDIA,
            status = Status.OPEN_TO_CHAT,
            presence = Presence.ONLINE,
            profilePhoto = null,
            lat = 37.4230,
            lng = -122.086
        )
    )
}