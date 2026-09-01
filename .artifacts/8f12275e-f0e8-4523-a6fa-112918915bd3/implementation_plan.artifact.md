# Implementation Plan - Community Detail & Enhanced Posting

The goal is to allow users to interact with specific communities by entering their dedicated screens, viewing only their posts, and posting directly within them.

## User Review Required

> [!IMPORTANT]
> - **Community Detail Screen**: A new screen will be accessible by clicking on any community in the "Discover" list.
> - **Scoped Posting**: Users will be able to post directly within a community. These posts will be linked to that specific community ID.
> - **Type-Safe Navigation**: We will update the `CommunityDetail` route to accept a `communityId`.

## Proposed Changes

### [Navigation & Routes]

#### [MODIFY] [Routes.kt](file:///C:/Users/USUARIO/Desktop/GitHub Repository/PilloraMoney/app/src/main/java/com/example/pilloramoney/navigation/Routes.kt)
- Change `CommunityDetail` from `data object` to `data class CommunityDetail(val communityId: String) : Screen()`.

#### [MODIFY] [MainActivity.kt](file:///C:/Users/USUARIO/Desktop/GitHub Repository/PilloraMoney/app/src/main/java/com/example/pilloramoney/MainActivity.kt)
- Update `NavHost` to handle the `CommunityDetail` parameter.
- Implement the composable route for `CommunityDetail`.

---

### [Data Repository]

#### [MODIFY] [CommunityRepository.kt](file:///C:/Users/USUARIO/Desktop/GitHub Repository/PilloraMoney/app/src/main/java/com/example/pilloramoney/data/repository/CommunityRepository.kt)
- Add `getPostsByCommunity(communityId: String): Flow<List<Post>>`: Fetches posts where `communityId` matches.
- Add `getCommunityById(id: String): Result<Community>`: Fetches a single community's details.

---

### [ViewModels]

#### [NEW] [CommunityDetailViewModel.kt](file:///C:/Users/USUARIO/Desktop/GitHub Repository/PilloraMoney/app/src/main/java/com/example/pilloramoney/ui/viewmodels/CommunityDetailViewModel.kt)
- Handles state for a specific community.
- Manages posting within that community context.

---

### [UI Screens]

#### [NEW] [CommunityDetailScreen.kt](file:///C:/Users/USUARIO/Desktop/GitHub Repository/PilloraMoney/app/src/main/java/com/example/pilloramoney/ui/screens/CommunityDetailScreen.kt)
- Displays community header (image, name, description).
- Shows a list of posts belonging to this community.
- Floating "Post" area at the top or bottom of the list.

#### [MODIFY] [CommunityBrowseScreen.kt](file:///C:/Users/USUARIO/Desktop/GitHub Repository/PilloraMoney/app/src/main/java/com/example/pilloramoney/ui/screens/CommunityBrowseScreen.kt)
- Make `CommunityItem` clickable using `Modifier.clickable`.
- Add `onCommunityClick` parameter to navigate to the detail screen.

#### [MODIFY] [CommunityScreen.kt](file:///C:/Users/USUARIO/Desktop/GitHub Repository/PilloraMoney/app/src/main/java/com/example/pilloramoney/ui/screens/CommunityScreen.kt)
- Update `PostItem` to optionally show the community name (if relevant in general feed).

## Verification Plan

### Manual Verification
1.  Open "Comunidade" from the bottom bar.
2.  Click on "Explorar" (Explore communities).
3.  Tap on a community card.
4.  Verify that it opens the `CommunityDetailScreen` with the correct name and description.
5.  Create a post inside that specific community.
6.  Go back to the general community feed and verify the post appears there too (if it's one of the top posts).
7.  Return to the community detail and ensure the post is listed.
