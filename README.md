# AL Prime (UniverseOfDisney)

Android application for managing and discovering movies, built in Kotlin with Firebase Realtime Database.

---

## Table of contents

- [Overview](#overview)
- [Technologies](#technologies)
- [Test accounts](#test-accounts)
- [General features](#general-features)
- [App structure](#app-structure)
  - [Authentication](#authentication)
  - [Home tab](#home-tab)
  - [Universes tab](#universes-tab)
  - [My Movies tab](#my-movies-tab)
  - [Search tab](#search-tab)
  - [Profile tab](#profile-tab)

---

## Overview

AL Prime (UniverseOfDisney) is an Android mobile app that lets users browse movies from different universes (Avatar, Disney, Marvel, Pixar, Star Wars), rate them, manage their personal collection, and share movies they want to get rid of.

---

## Technologies

- Language: Kotlin
- Database: Firebase Realtime Database
- All data (movie posters, universe logos, universe names, genre names, movies, user data) is stored and retrieved from Firebase Realtime Database.
- Profile pictures and appl logo are the only exception - they are stored locally in `res/drawable`.

---

## Test accounts

| First name | Email | Password |
|---|---|---|
| Amelie | amelie@gmail.com | amelie |
| Lucas | lucas@gmail.com | lucas123 |
| Elise | elise@gmail.com | elise456 |
| Emeric | emeric@gmail.com | emeric789 |

---

## General features

- The user logs in once and stays logged in even after closing the app, unless they manually log out or reinstall it.
- A navigation bar is present on every screen with 5 buttons in order: Home, Universes, My Movies, Search, Profile.

---

## App structure

### Authentication

<details>
<summary><code>LoginScreen.kt</code></summary>
<br>

- App logo with the message *"Welcome - Log in to continue"*
- Email and password input fields (spaces, tabs and line breaks are blocked)
- Log in button -> `HomeScreen.kt`
- Link *"Don't have an account yet? Sign up"* -> `RegisterScreen.kt`

</details>

<details>
<summary><code>RegisterScreen.kt</code></summary>
<br>

- App logo with the message *"Create an account - Sign up to continue"*
- First name, email and password input fields
  - Spaces blocked in email and password fields
  - Tabs and line breaks blocked in all fields
- Sign up button -> `HomeScreen.kt`
- Link *"Already have an account? Log in"* -> `LoginScreen.kt`

</details>

---

### Home tab

<details>
<summary><code>HomeScreen.kt</code></summary>
<br>

- Automatic greeting title *"Hi [First name]"*
- Main card: a random movie with its poster, title and a *"See Details"* button
- Top 10 movies carousel
- Category carousels: Recommended for you, Science-Fiction, Animation, Action, Star Wars
- Tapping a movie opens `MovieDetailScreen.kt` with a back button

</details>

---

### Universes tab

<details>
<summary><code>UniverseScreen.kt</code></summary>
<br>

- Sliding selector bar to switch between two lists: Universes and Genres
- Universes (5 cards): Avatar, Disney, Marvel, Pixar, Star Wars - each with logo, title and movie count
- Genres (4 cards): Action, Adventure, Animation, Science-Fiction - each with a film icon, title and movie count
- Tapping a category opens `MoviesScreen.kt`

</details>

<details>
<summary><code>MoviesScreen.kt</code></summary>
<br>

- List of movies for the selected category
- Total movie count displayed
- Movie cards: poster, title, release date
- Average user rating displayed with a star icon - hidden if no rating yet
- Movies sorted from oldest to most recent
- Back button -> `UniverseScreen.kt`
- Tapping a movie -> `MovieDetailScreen.kt`

</details>

<details>
<summary><code>MovieDetailScreen.kt</code></summary>
<br>

- Movie poster with mini info cards alongside it
- Synopsis card
- Rating card: 1 to 5 star rating system with average score
- Your status section:
  - Watched / Want to Watch
  - Own / Want to Get Rid of *(selecting "Get Rid" automatically selects "Own")*
- Card showing users who want to get rid of this movie, with their email addresses
- Each user's rating and status are personal and saved independently in Firebase
- Back button -> `MoviesScreen.kt`

</details>

---

### My Movies tab

<details>
<summary><code>MyMoviesScreen.kt</code></summary>
<br>

- Profile picture of the logged-in user
- 4 cards with counters: Watched, Want to Watch, Owned, Get Rid
- No card selected by default -> message *"Choose a category to see your movies"*
- Tapping a card shows the corresponding movie list (poster, title, date)
- Tapping a movie -> `MovieDetailScreen.kt`

</details>

---

### Search tab

<details>
<summary><code>SearchScreen.kt</code></summary>
<br>

- Search bar that updates on every keystroke
- Tabs and line breaks are blocked
- Clear button (x) to reset the search
- Default state: app logo shown in transparency with the message *"Find your favorite movie"*
- Search covers the full movie title, not just the beginning
- Tapping a result -> `MovieDetailScreen.kt`

</details>

---

### Profile tab

<details>
<summary><code>ProfileScreen.kt</code></summary>
<br>

- Profile card with user picture, first name and email
- Edit button (pencil icon) -> `EditProfileScreen.kt`
- 3 cards: Owned -> `OwnedMoviesScreen.kt`, Get Rid -> `WantToGetRidScreen.kt`, Shared Get Rid -> `SharedGetRidScreen.kt`
- Log out button -> `LoginScreen.kt`

</details>

<details>
<summary><code>EditProfileScreen.kt</code></summary>
<br>

- Displays current info: profile picture, first name, email
- Profile picture can be changed by tapping on it
- Only the first name is editable (email is read-only)
- Security section: password change with new password and confirm password fields
- Save changes button with a green confirmation message or a red error message
- Back button -> `ProfileScreen.kt`

</details>

<details>
<summary><code>OwnedMoviesScreen.kt</code></summary>
<br>

- Search bar among owned movies
- Sort options: alphabetical, most recent, oldest
- Empty state: *"No owned movies - Add movies to your collection"*
- Movies displayed with poster, title and date
- Trash icon with a confirmation popup before deletion
- Tapping a movie -> `MovieDetailScreen.kt`
- Back button -> `ProfileScreen.kt`

</details>

<details>
<summary><code>WantToGetRidScreen.kt</code></summary>
<br>

- Search bar among movies marked for removal
- Sort options: alphabetical, most recent, oldest
- Empty state: *"No movies to get rid of - Movies you want to sell or give away will appear here"*
- Removing a movie from Owned automatically removes it from Get Rid
- Trash icon with a confirmation popup before deletion
- Tapping a movie -> `MovieDetailScreen.kt`
- Back button -> `ProfileScreen.kt`

</details>

<details>
<summary><code>SharedGetRidScreen.kt</code></summary>
<br>

- Search bar across all movies that any user wants to get rid of
- Empty state: *"No shared movies for the moment"*
- Movies displayed with poster, title, number of interested users, their email addresses and a Contact button
- Contact button opens the phone's mail app to reach the relevant user
- Removing a movie from Owned automatically removes it from the shared list
- Tapping a movie -> `MovieDetailScreen.kt`
- Back button -> `ProfileScreen.kt`

</details>
