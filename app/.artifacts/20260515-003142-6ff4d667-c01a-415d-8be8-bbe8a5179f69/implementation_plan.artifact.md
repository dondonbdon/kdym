# KDYM App Enhancements Phase 2

Implementation of home screen history, multi-phase sign up, church directory, enhanced play section, and advanced user management.

## User Review Required

- **Soft Deletion**: Confirm that "Danger Zone" account deletion should only mark the user as deleted in Firestore and sign them out, rather than deleting the Auth record immediately.
- **Church Data**: We will initialize a list of churches in Firestore. Please confirm if there is an existing list or if we should start with a small sample.
- **Play Interactions**: Confirm that "Clips & Moments" follow a TikTok-style vertical pager and that audio/gallery also follow a similar dismissable swiping pattern.

## Proposed Changes

### Core Data Models

#### [AppUser.kt](file:///C:/Users/Brandon/AndroidStudioProjects/kdym/app/src/main/java/dev/bti/kdym/data/models/AppUser.kt)
- Add `username: String`
- Add `churchId: String?`
- Add `churchName: String?`
- Add `isDeleted: Boolean = false` (for soft deletion)
- Update `hasCommandAccess` to include new roles.

#### [NEW] [Church.kt](file:///C:/Users/Brandon/AndroidStudioProjects/kdym/app/src/main/java/dev/bti/kdym/data/models/Church.kt)
- `id: String`, `name: String`, `pastorName: String`, `city: String`, `state: String`, `pastorId: String?`.

#### [PlayItem.kt](file:///C:/Users/Brandon/AndroidStudioProjects/kdym/app/src/main/java/dev/bti/kdym/data/models/PlayItem.kt)
- Add `likesCount: Int`, `commentsCount: Int`, `tribeId: String?`, `isFeatured: Boolean`.

#### [Camp.kt](file:///C:/Users/Brandon/AndroidStudioProjects/kdym/app/src/main/java/dev/bti/kdym/data/models/Camp.kt)
- Add `verse: String`, `verseReference: String`, `description: String`, `accentColor: String`, `historyPhotos: List<String>`.

#### [Enums.kt](file:///C:/Users/Brandon/AndroidStudioProjects/kdym/app/src/main/java/dev/bti/kdym/data/models/Enums.kt)
- Update `UserRole` to include `staff`, `groupLeader`, `tribeLeader`, `superAdmin`.

---

### Repositories

#### [UserRepository.kt](file:///C:/Users/Brandon/AndroidStudioProjects/kdym/app/src/main/java/dev/bti/kdym/data/repositories/UserRepository.kt)
- Add `softDeleteUser(uid: String)`
- Add `requestCampAccess(uid: String, campId: String, requestedRole: String)`

#### [NEW] [ChurchRepository.kt](file:///C:/Users/Brandon/AndroidStudioProjects/kdym/app/src/main/java/dev/bti/kdym/data/repositories/ChurchRepository.kt)
- Methods for fetching all churches, searching, and managing church-pastor links.

#### [PlayRepository.kt](file:///C:/Users/Brandon/AndroidStudioProjects/kdym/app/src/main/java/dev/bti/kdym/data/repositories/PlayRepository.kt)
- Add methods for `toggleLike`, `addComment`, `reportItem`, and administrative actions.

---

### Home Screen Enhancements

#### [HomeScreen.kt](file:///C:/Users/Brandon/AndroidStudioProjects/kdym/app/src/main/java/dev/bti/kdym/ui/screens/home/HomeScreen.kt)
- Implement `VerticalPager` to allow swiping between the current camp theme and history camp themes.
- Implement "KDYM mode" compact one-page view (hiding live updates when not in camp mode).
- Apply dynamic themes (accent colors, fonts) based on the current page in the pager.

---

### Authentication & Sign Up

#### [SignUpScreen.kt](file:///C:/Users/Brandon/AndroidStudioProjects/kdym/app/src/main/java/dev/bti/kdym/ui/screens/auth/SignUpScreen.kt)
- Refactor to 3-phase flow:
    1. Name & Username.
    2. Church Search & Selection.
    3. Finalize & Create Account.
- Integrate search for churches via `ChurchRepository`.

---

### Play Section

#### [PlayScreen.kt](file:///C:/Users/Brandon/AndroidStudioProjects/kdym/app/src/main/java/dev/bti/kdym/ui/screens/play/PlayScreen.kt)
- Add search bar for searching media items.
- Add horizontal filter chips for "General", and specific Tribes.
- Add FAB for admins to open `CreatePlayItemScreen`.
- Implement TikTok-style vertical pager for clips when clicked.
- Add interaction overlays for Likes, Comments, and Sharing.

---

### Profile & Settings

#### [SettingsScreen.kt](file:///C:/Users/Brandon/AndroidStudioProjects/kdym/app/src/main/java/dev/bti/kdym/ui/screens/settings/SettingsScreen.kt) [NEW]
- List of navigation items matching the requested design.
- External links to `kdym.org` for Privacy, Terms, and Guidelines.
- "Request Camp Access" button leading to the request form.

#### [ProfileScreen.kt](file:///C:/Users/Brandon/AndroidStudioProjects/kdym/app/src/main/java/dev/bti/kdym/ui/screens/profile/ProfileScreen.kt)
- Refactor header to be more minimalistic.
- Show Name, Profile Photo, Church, and Account Status.
- Add "Danger Zone" at the bottom for account deletion.

---

### Advanced Admin & User Management

#### [UsersScreen.kt](file:///C:/Users/Brandon/AndroidStudioProjects/kdym/app/src/main/java/dev/bti/kdym/ui/screens/admin/UsersScreen.kt) [NEW]
- Merged view of all users and pending access requests.
- Detailed "Manage User Access" dialog with role hierarchy and permission toggles.

#### [ChurchesScreen.kt](file:///C:/Users/Brandon/AndroidStudioProjects/kdym/app/src/main/java/dev/bti/kdym/ui/screens/admin/ChurchesScreen.kt) [NEW]
- Church directory management.
- Pastor assignments and claim management.

## Verification Plan

### Automated Tests
- N/A (Project currently uses manual verification for UI/UX flows).

### Manual Verification
1. **Sign Up Flow**: Test the 3-phase flow, ensuring church selection works and data is correctly saved to Firestore.
2. **Home Screen Pager**: Verify smooth vertical swiping between current camp and history. Check that live updates are hidden in "KDYM mode".
3. **Play Interactions**: Verify clicking a clip opens the full-screen vertical pager. Test liking and commenting.
4. **Admin Permissions**: Verify that "Create Play Item" and "Manage User" features are only visible to authorized roles.
5. **Account Deletion**: Verify that "Delete Account" marks the user as deleted and signs them out.
